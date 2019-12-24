package g7anbindung;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.CountDownLatch;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

import dataHandler.DataHandler;
import dataHandler.DataHandlerBuilder;
import exceptions.PropertyDoesNotExistException;
import exceptions.WrongFormatException;

/**
 * The Class ListenerToRS232 enables a communication to a specified COM port.
 * The Class is written for the <code>TosohAPI</code> and is used to receive values that get send from the Tosoh modality.
 */
public class ListenerToRS232 implements Runnable, SerialPortEventListener {

	/** The port id identifies the COM port that the API gets connected to. */
	CommPortIdentifier portId;

	/** The transmitted data in a raw unformatted String format. */
	private String transmittedDataOfMeasurement = "";
	
	/** The transmitted data parsed in <code>BlutmessungG7</code> and  added to a List. */
	private List<BlutmessungG7> messungenDerUebertragung = new ArrayList<>();

	/** The datahandler will handle the transmitted data according
	 *  to the settings in the <code>SettingsManager</code>. */
	private DataHandler handler;
	
	/** This boolean identifies the state of the COM port. */
	private boolean portIsConnected = false;
	
	/** The latch is used to support multithreading. As long as the COM port listener is active, the menu of the <code>TosohAPI</code> wont be shown again. */
	private CountDownLatch latch;

	/** The input stream that reads the transmitted data. */
	private InputStream inputStream;
	
	/** The output stream that sends ASCII codes as a response. */
	private OutputStream outputStream;
	
	/** The serial COM port used for the connection to the Tosoh G7 */
	private SerialPort serialPort;
	
	/** The reading thread. */
	private Thread readingThread;

	/** The numeric value of the ASCII acknowledgement. */
	final int ACK_ASCII = 6;
	
	/** The numeric value of the ASCII end of text. */
	final int ETX_ASCII = 3;
	
	/** The numeric value of the ASCII end of transmission. */
	final int EOT_ASCII = 4;

		
	/**
	 * Connect port
	 * 
	 * This method connects the application to the specified COM port. 
	 * It opens the input and the output stream and adds the <code>ListenerToRS232</code> as an event Listener to the port.
	 * 
	 * the latch is used from the <code>TosohAPI</code> to await the data transmission before the menu is shown again.
	 * 
	 * To change the settings of the COM port connection the params of setSerialPortParams() need to be changed.
	 * 
	 * if the port is connected succesfully a reading <code>Thread</code> is started that awaits the <code>SerialPortEvent.DATA_AVAILABLE</code> event.
	 * 
	 * @param portToConnect the id of the COM port to connect
	 * @param latch the latch to support the multithreading of the application
	 */
	public void connectPort(CommPortIdentifier portToConnect, CountDownLatch latch) throws PropertyDoesNotExistException {
		this.latch = latch;
		this.portId = portToConnect;
		System.out.println("Port gets connected");
		try {
			System.out.println(this.portId.getName());
			this.serialPort = (SerialPort) this.portId.open("TosohG7Reader", 2000);
		} catch (PortInUseException e) {
			System.out.println(e);
		}
		try {
			this.inputStream = this.serialPort.getInputStream();
			this.outputStream = this.serialPort.getOutputStream();
		} catch (IOException e) {
			System.out.println(e);
		}
		try {
			this.serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			System.out.println(e);
		}
		this.serialPort.notifyOnDataAvailable(true);
		try {
			this.serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			this.serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		} catch (UnsupportedCommOperationException e) {
			System.out.println(e);
		}
		this.portIsConnected = true;

		System.out.println("\nPort initialized sucessfully");
		System.out.println("Ready for data transmission\n");

		
		this.readingThread = new Thread(this);
		this.readingThread.start();

	}

	/**
	 * Disconnect port.
	 * 
	 * This method disconnects the active COM port and closes all <code>Streams</code>.
	 */
	public void disconnectPort() {
		if (this.portIsConnected) {
			try {
				this.outputStream.flush();
				this.outputStream.close();
				this.serialPort.removeEventListener();
				this.serialPort.close();
				this.portIsConnected = false;
				System.out.println("Port disconnected");
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * This Method awaits the <code>SerialPortEvent.DATA_AVAILABLE</code> event. 
	 * If there is data available the data is read into the transmittedDataOfMeasurement.
	 * 
	 * Afterwards the API trys to parse the transmitted String into a List of <code>BlugmessungG7</code>.
	 * 
	 * If the ASCII code EOT is transmitted the <code>handleDataOfTransmission</code> method is called.
	 */
	public void serialEvent(SerialPortEvent event) {

		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			byte[] readBuffer = new byte[20];
			int numberOfBytesRead = 0;
			try {
				while (this.inputStream.available() > 0) {
					numberOfBytesRead = this.inputStream.read(readBuffer);
				}
				byte[] actualReadInput = new byte[numberOfBytesRead];
				System.arraycopy(readBuffer, 0, actualReadInput, 0, numberOfBytesRead);
				String bufferInput = new String(actualReadInput);
				System.out.print(bufferInput);
				this.transmittedDataOfMeasurement = this.transmittedDataOfMeasurement + bufferInput;
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

			if (this.transmittedDataOfMeasurement.contains(Character.toString((char) this.ETX_ASCII))) {
				this.handleSingleResult();
				System.out.println();
			}
			if (this.transmittedDataOfMeasurement.contains(Character.toString((char) this.EOT_ASCII))) {
				System.out.println("\n\nReached end of transmission\n");
				try {
					this.handleDataOfTransmission();
				} catch (PropertyDoesNotExistException e) {
					System.out.println(e.getMessage());
					System.out.println("There was a problem with the dataoutput");
					System.out.println("Please check your settings");
				}			
				this.latch.countDown();
			}
		}
	}

	
	/**
	 * Handle single result trys to parse a transmitted measurement into a <code>BlutmessungG7</code>
	 */
	private void handleSingleResult() {
		try {
			this.messungenDerUebertragung.add(new BlutmessungG7(this.transmittedDataOfMeasurement));

		} catch (WrongFormatException e) {
			System.out.println(e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		}
		this.transmittedDataOfMeasurement = "";
		this.sendACK();
	}

	/**
	 * Send ACK is used to support the HI-LEVEL transmission protocol of the Tosoh G7.
	 * 
	 * After every transmitted Measurement the modality awaits an Acknowledgement code that gets send by this method.
	 */
	private void sendACK() {
		try {
			this.outputStream.write(this.ACK_ASCII);
			this.outputStream.flush();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Handle data of transmission.
	 * 
	 * After the End of Transmission code this method will use the <code>DataHandlerBuilder</code> to create a <code>DataHandler</code> according to 
	 * the specified settings in the <code>SettingsManager</code>.
	 * 
	 * This <code>DataHandler</code> will afterward handle the transmitted data accordingly. 
	 * 
	 * If the data is handled successfully the COM port will be disconnected.
	 *
	 * @throws PropertyDoesNotExistException the property does not exist exception
	 */
	private void handleDataOfTransmission() throws PropertyDoesNotExistException {
		
		this.handler = new DataHandlerBuilder().buildDataHandler(new DataHandler());

		for(BlutmessungG7 messung : this.messungenDerUebertragung) {
			this.handler.handleData(messung);
		}
		System.out.println("\n\nData got successfully processed");
		this.transmittedDataOfMeasurement = "";
		this.messungenDerUebertragung = new ArrayList<>();
		System.out.println("Transmitted data cleared.\n");
		this.disconnectPort();
	}
	
	
	/** 
	 * @see java.lang.Runnable#run()
	 * 
	 * Needs to be here, but does simply nothing
	 */
	
	public void run() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

}
package g7anbindung;

import java.util.ArrayList;
import java.util.List;

import enums.OperationMode;
import enums.SamplePosition;
import exceptions.WrongFormatException;
import services.OperationModeService;


/**
 * The Class BlutmessungG7 represents a single bloodmeasurement with the Tosoh G7 modality. 
 * 
 * The transmitted Data is parsed into the Object attributes. 
 */
public class BlutmessungG7 {

	/** The operation mode. For further information see the manual of the TosohG7 APPI */
	private OperationMode operationMode;
	
	/** This Service holds all informations about the data that gets transmitted by the Tosoh G7
	 * It maps the Operation mode to the datatypes of the measurements.
	 */
	private OperationModeService operationModeService;

	/** The position. For further information see the manual of the TosohG7 APPI*/
	private SamplePosition position;
	
	/** The sample nr. For further information see the manual of the TosohG7 APPI*/
	private String sampleNr;
	
	/** The single values of the bloodmeasurement. For further information see the manual of the TosohG7 APPI*/
	private List<String> messungen = new ArrayList<>();
	
	/** The barcode. For further information see the manual of the TosohG7 APPI*/
	private String barcode;

	/** The numeric value of the ASCII start of text. */
	private final int STX_ASCII = 2;
	
	/** The numeric value of the ASCII end of text. */
	private final int ETX_ASCII = 3;
	
	/** The numeric value of the ASCII end of transmission. */
	private final int EOT_ASCII = 4;
	

	/**
	 * Instantiates a new <code>BlutmessungG7</code>
	 * 
	 * If the data String is not in the G7 Format or if it has an invalid form a <code>WrongFormatException</code> is thrown. 
	 * 
	 *
	 * @param data the data in the G7 Format according to Tosoh API specs.
	 * @throws WrongFormatException the wrong format exception
	 */
	public BlutmessungG7(String data) throws WrongFormatException {
		this.operationModeService = OperationModeService.getOperationModeService();
		this.parseInput(data);
	}

	/**
	 * Gets the patient ID.
	 *
	 * @return the patient ID
	 */
	public String getPatientID() {
		return this.barcode.substring(0,10);
	}
	
	/**
	 * Gets the case ID.
	 *
	 * @return the case ID
	 */
	public String getCaseID() {
		return this.barcode.substring(10,15);
	}
	
	/**
	 * Gets the order ID.
	 *
	 * @return the order ID
	 */
	public String getOrderID() {
		return this.barcode.substring(15);
	}
	
	/**
	 * Gets the Hba1c.
	 *
	 * @return the Hba1c
	 */
	public double getHba1c() {
		switch(this.operationMode) {
		case STD: 
			return Double.valueOf(this.messungen.get(5));			
		case VAR:
			return Double.valueOf(this.messungen.get(4));	
		case Beta_Thalassemia:
			return 0.0;
		}
		return 0.0;
	}
	

	/**
	 * To string CSV creates a CSV formatted String that represents the <code>BlutmessungG7</code> object.
	 *
	 *
	 * @return the string
	 */
	public String toStringCSV() {
		String str = this.operationMode.toString();
		str = this.addStringWithComaToOutput(str, this.position.toString());
		str = this.addStringWithComaToOutput(str, this.sampleNr);

		for (int i = 0; i < 10; i++) {
			str = this.addStringWithComaToOutput(str, this.messungen.get(i));
			str = this.addStringWithComaToOutput(str, this.operationModeService.getListOfParamsAccordingToOperationMode(this.operationMode).get(i));
		}

		str = this.addStringWithComaToOutput(str, this.barcode) + "\r\n";

		return str;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = "";
		int counter = 1;

		str = String.format("%-13s", "Messmodus: ") + String.format("%16s", this.operationMode.toString()) + "\n"
				+ String.format("%-16s", "Propenposition: ") + String.format("%13s", this.position.toString()) + "\n"
				+ String.format("%-21s", "Probennummer: ") + String.format("%8s", this.sampleNr) + "\n";
		for (String messwert : this.messungen) {
			str = str + String.format("%-10s", "Messwert" + counter) + " - "
					+ String.format("%10s",this.operationModeService.getListOfParamsAccordingToOperationMode(this.operationMode).get(counter-1))
					+ " " + messwert + "\n";
			counter++;
		}
		str = str + "Barcode:" + String.format("%21s", this.barcode);
		str = str + "\n-----------------------------\n";
		return str;
	}	
	
	/**
	 * Parses the input.
	 * 
	 * This method checks if the transmitted data String has the correct length, and removes all additional ASCII codes.
	 * 
	 * Afterwards the String will be filled into the different variables of the <code>BlutmessungG7</code> Object.
	 * 
	 * If an error occurs or an Exception is thrown this Method will throw a new <code>WrongFormatException</code>
	 *
	 * @param data the data
	 * @throws WrongFormatException the wrong format exception
	 */
	private void parseInput(String data) throws WrongFormatException {

		if (data.length() < 77) {
			throw new WrongFormatException("Der Input enspricht hat nicht die gewünschte Anzahl an Zeichen");
		}
		data = data.replace(Character.toString((char) this.ETX_ASCII), "");
		data = data.replace(Character.toString((char) this.EOT_ASCII), "");
		data = data.replace(Character.toString((char) this.STX_ASCII), "");

		this.operationMode = this.operationModeService.getOperationMode(Integer.valueOf(data.substring(0, 1)));
		this.position = this.operationModeService.getSamplePosition(Integer.valueOf(data.substring(1, 2)));
		int indexOfFirstMeasurement = data.indexOf(".") -3;
		this.sampleNr = data.substring(2,indexOfFirstMeasurement);

		for (int i = indexOfFirstMeasurement; i < indexOfFirstMeasurement+50; i += 5) {
			this.messungen.add(data.substring(i, i + 5));
		}

		this.barcode = data.substring(data.lastIndexOf(".") + 4, data.lastIndexOf(".") + 24);

	}

	/**
	 * Adds the string with coma to output.
	 * Is used in the to CSV toString method.
	 *
	 * @param output the output
	 * @param str the str
	 * @return the string
	 */
	private String addStringWithComaToOutput(String output, String str) {
		return output + "," + str;
	}
}

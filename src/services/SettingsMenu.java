
/**
 *date: 10.11.2019   -  time: 11:47:34
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package services;

import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

import javax.comm.CommPortIdentifier;

import enums.FileType;
import exceptions.PropertyDoesNotExistException;

/**
 * The Class SettingsMenu represents the menu that handles the user input to
 * change settings in the <code>TosohAPI</code>.
 */
public class SettingsMenu {

	/**
	 * The settingsmanager stores all current settings of the application and is
	 * used to initialize the API on a restart.
	 */
	private SettingsManager settings;

	/** The scanner to read user input in the console. */
	private Scanner scanner;

	/**
	 * Instantiates a new settings menu.
	 */
	public SettingsMenu() {
		this.settings = SettingsManager.getSettingsManager();
		this.scanner = new Scanner(System.in);
	}

	/**
	 * Show settings menu.
	 *
	 * This displays the whole settings menu and will await a user input that
	 * decides what to do next.
	 * 
	 * This will throw a <code>PropertyDoesNotExistException</code> if there is a
	 * problem rading the settings.xml file.
	 *
	 * @throws PropertyDoesNotExistException the property does not exist exception
	 */
	public void showSettingsMenu() throws PropertyDoesNotExistException {

		boolean keepLoopAlive = true;
		while (keepLoopAlive) {
			this.insertSmallDelay();
			System.out.println("\n\nWhat would you like to do? \n");

			System.out.println("Change the COM Port                -> 1");
			if (this.settings.isSavingToSystemEnabled()) {
				System.out.println("Disable saving to system           -> 2");
				System.out.println("Add Dataformat to systemoutput     -> 3");
			} else {
				System.out.println("Enable saving to System            -> 2");
				System.out.println("------unavailable-------           -> 3");
			}
			if (this.settings.isSendingToHTTPEnabled()) {
				System.out.println("Disable sending to HTTP            -> 4");
			} else {
				System.out.println("Enable sending to HTTP             -> 4");
			}
			System.out.println("Display current settings           -> 5");
			System.out.println("Back to menu                       -> 6");

			String input = this.scanner.nextLine();
			switch (input) {
			case "1":
				this.specifyPort();
				break;
			case "2":
				if (this.settings.isSavingToSystemEnabled()) {
					this.settings.setSavingToSystemEnabled(false);
				} else {
					this.settings.setSavingToSystemEnabled(true);
					this.specifySystemPath();
				}
				break;
			case "3":
				if (this.settings.isSavingToSystemEnabled()) {
					this.specifyDatatypesToSystem();
				}
				break;
			case "4":
				if (this.settings.isSendingToHTTPEnabled()) {
					this.settings.setSendingToHTTPEnabled(false);
				} else {
					this.settings.setSendingToHTTPEnabled(true);
					this.specifyHTTP_URL();
				}
				break;
			case "5":
				this.displayCurrentSettings();
				break;
			case "6":
				keepLoopAlive = false;
				break;
			default:
				System.out.println("Invalid input, which settings do you want to change? ");
				break;
			}
		}
	}

	/**
	 * Displays the current settings of the <code>TosohAPI</code>.
	 *
	 * @throws PropertyDoesNotExistException the property does not exist exception
	 */
	public void displayCurrentSettings() throws PropertyDoesNotExistException {

		System.out.println("Current settings of application:\n");
		System.out.println("Port to connect: " + this.settings.getPort().getName());
		if (this.settings.isSavingToSystemEnabled()) {
			System.out.println("\nSaving results to System: enabled");
			System.out.println("Path: " + this.settings.getSystemPath());

			if (!this.settings.getToSystemDataformats().isEmpty()) {
				String dataformatsToSystem = "Files are saved in these dataformats: ";
				for (String format : this.settings.getToSystemDataformats()) {
					dataformatsToSystem = dataformatsToSystem + format + ", ";
				}
				System.out.println(dataformatsToSystem.substring(0, dataformatsToSystem.length() - 2) + "\n");
			}
		} else {
			System.out.println("\nSaving results to System: disabled");
		}

		if (this.settings.isSendingToHTTPEnabled()) {
			System.out.println("\nSend results to HTTP: enabled");
			System.out.println("URL: " + this.settings.getHTTP_URL());

			if (!this.settings.getToHTTPDataformats().isEmpty()) {
				String dataformatsToSystem = "Files are sended in these dataformats: ";
				for (String format : this.settings.getToHTTPDataformats()) {
					dataformatsToSystem = dataformatsToSystem + format + ", ";
				}
				System.out.println(dataformatsToSystem.substring(0, dataformatsToSystem.length() - 2) + "\n");
			}
		} else {
			System.out.println("\nSend results to HTTP: disabled");
		}
	}

	/**
	 * Sets the up settings.
	 * 
	 * This method is used to set up all Settings of the <code>TosohAPI</code>.
	 * First the COM port to connect the <code>ListenerToRS232</code> to needs to be
	 * chosen. Afterwards the user needs to decide if he wants to save data locally
	 * (if yes to which path and in which dataformat) and if he wants to send the
	 * data with HTTP to a destination url.
	 * 
	 * 
	 */
	public void setUpSettings() {
		System.out.println("You need to do some set up, a few settings are required: \n");
		this.specifyPort();
		this.specifyToSystemEnabled();
		if (this.settings.isSavingToSystemEnabled()) {
			this.specifySystemPath();
			this.specifyDatatypesToSystem();
		}
		this.specifyToHTTPEnabled();
		if (this.settings.isSendingToHTTPEnabled()) {
			this.specifyHTTP_URL();
		}
		this.insertSmallDelay();
		System.out.println("\n\nAll settings done\n\n\n");
	}

	/**
	 * This method lets the user choose to which COM port the <code>TosohAPI</code>
	 * should get connected.
	 */
	@SuppressWarnings("unchecked")
	private void specifyPort() {
		System.out.println("You need to specify the serial port for the connection");
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		List<CommPortIdentifier> ports = new ArrayList<CommPortIdentifier>();
		System.out.println("List of available ports: \n");
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				ports.add(portId);
				System.out.println("Port: " + portId.getName());
			}
		}
		System.out.println("\nWhich port do you want to connect? \nPlease enter the portnumber:");
		boolean keepLoopAlive = true;
		while (keepLoopAlive) {
			try {
				int input = Integer.valueOf(scanner.nextLine());
				for (CommPortIdentifier port : ports) {
					if (Integer.valueOf(port.getName().substring(port.getName().length() - 1)) == input) {
						this.settings.setPort(port);
						System.out.println("Chosen port: " + port.getName());
						keepLoopAlive = false;
					}
				}
				if (keepLoopAlive) {
					System.out.println("Invalid input, please enter the portnumber: ");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input, please enter the portnumber: ");
			}
		}
	}

	/**
	 * This method lets the user choose if he wants to save dataoutput locally.
	 */
	private void specifyToSystemEnabled() {
		this.insertSmallDelay();
		System.out.println("\nDo you want to to save the results localy? ( Y / N )");
		boolean keepLoopAlive = true;
		while (keepLoopAlive) {
			String input = this.scanner.nextLine().toUpperCase();
			switch (input) {
			case "Y":
				this.settings.setSavingToSystemEnabled(true);
				System.out.println("Saving results to System: enabled");
				keepLoopAlive = false;
				break;
			case "N":
				this.settings.setSavingToSystemEnabled(false);
				System.out.println("Saving results to System: disabled");
				keepLoopAlive = false;
				break;
			default:
				System.out.println("Invalid input, please enter ( Y / N )");
				break;
			}

		}
	}

	/**
	 * This method lets the user choose if he wants to send dataoutput to a HTTP
	 * url.
	 */
	private void specifyToHTTPEnabled() {
		this.insertSmallDelay();
		System.out.println("\nDo you want to send the results via HTTP? ( Y / N )");
		boolean keepLoopAlive = true;
		while (keepLoopAlive) {
			String input = this.scanner.nextLine().toUpperCase();
			switch (input) {
			case "Y":
				this.settings.setSendingToHTTPEnabled(true);
				System.out.println("Sending results via HTTP: enabled");
				keepLoopAlive = false;
				break;
			case "N":
				this.settings.setSendingToHTTPEnabled(false);
				System.out.println("Sending results via HTTP: disabled");
				keepLoopAlive = false;
				break;
			default:
				System.out.println("Invalid input, please enter ( Y / N )");
				break;
			}

		}
	}

	/**
	 * This method lets the user choose to which path the dataoutput should be saved
	 * locally.
	 */
	private void specifySystemPath() {
		this.insertSmallDelay();
		System.out.println("\nYou need to specify a local path for the data output");
		System.out.println("Please enter the path: ");
		boolean keepLoopAlive = true;
		while (keepLoopAlive) {
			try {
				String input = this.scanner.nextLine();
				this.settings.setSystemPath(input);
				System.out.println("Specified path: " + input);
				keepLoopAlive = false;
			} catch (InvalidPathException e) {
				System.out.println("This path was not valid. Please enter the path: ");
			}
		}
	}

	/**
	 * This method lets the user choose to which url the dataoutput should be sent
	 * to.
	 */
	private void specifyHTTP_URL() {
		this.insertSmallDelay();
		System.out.println("\n\nYou need to specify a HTTP URL for the data output");
		System.out.println("Please enter the URL: ");
		boolean keepLoopAlive = true;
		while (keepLoopAlive) {
			try {
				String input = this.scanner.nextLine();
				this.settings.setHTTP_URL(input);
				System.out.println("Specified URL: " + input);
				keepLoopAlive = false;
			} catch (InvalidPathException e) {
				System.out.println("This URL was not valid. Please enter the path: ");
			}
		}
	}

	/**
	 * * This method lets the user choose in which dataformats the dataoutput should be saved locally.
	 * 
	 * Possibilites are all dataformats in the <code>FileType</code> Enumeration.
	 * 
	 */
	private void specifyDatatypesToSystem() {
		this.insertSmallDelay();
		System.out.println("\n\nYou need to specify the datatypes of the output");
		List<String> datatypes = new ArrayList<>();
		try {
			if (this.settings.getToSystemDataformats().size() == FileType.values().length) {
				System.out.println("No further datatypes available");
				return;
			}
			System.out.println("\nActive right now:");
			datatypes = this.settings.getToSystemDataformats();

			if (!datatypes.isEmpty()) {
				String str = "";
				for (String format : datatypes) {
					str = format + ", " + str;
				}
				System.out.println(str.substring(0, str.length() - 2) + "\n");
			} else {
				System.out.println("No dataformat chosen yet");
			}
		} catch (PropertyDoesNotExistException e) {
			System.out.println("No dataformats specified yet");
		}

		System.out.println("\nAvailable for selection: ");
		for (FileType type : FileType.values()) {
			if (!datatypes.contains(type.toString())) {
				System.out.println(type.toString());
			}
		}

		boolean keepLoopAlive = true;
		System.out.println("Please enter the desired dataformat:");
		while (keepLoopAlive) {
			String input = this.scanner.nextLine().toUpperCase();
			for (FileType type : FileType.values()) {
				if (type.toString().equals(input)) {
					this.settings.addDataformatToSaveToSystemList(type);
					System.out.println("Chosen dataformat: " + type.toString().toUpperCase());
					keepLoopAlive = false;
					break;
				}
			}
			if (keepLoopAlive) {
				System.out.println("The input was not valid. Please enter the datatype you want: ");
			}
		}

	}

	/**
	 * Insert small delay. This is simply used to make the console based application more userfriendly, as it gives a small delay before showing the new content.
	 */
	private void insertSmallDelay() {
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

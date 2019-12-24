
/**
 *date: 08.11.2019   -  time: 13:04:04
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package g7anbindung;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import exceptions.PropertyDoesNotExistException;
import services.SettingsManager;
import services.SettingsMenu;

/**
 * The Class TosohAPI serves as an API to the Tosoh G7. This is a bloodanalyzer that is used to measure the Hba1c value. 
 * 
 * When running this API you get a console based Menu, that lets you choose from transmitting data, changing the settions or shutting down the program.
 * 
 * This API was developed and written by gundy1 in the BFH - TI Medizininformatik Biel in the LivingCase 2 project.
 */
public class TosohAPI implements Runnable {
	
	

	/** Indicates if the API is running */
	private boolean API_IS_RUNNING;

	/** The scanner to read user input in the console. */
	private Scanner scanner;

	/** The settingsmanager stores all current settings of the application	 * and is used to initialize the API on a restart. */
	private SettingsManager settings;
	
	/** The settings menu handles the menu for the settings specific submenu.
	 * In this submenu you can change dataformats, filepaths and http urls */
	private SettingsMenu settingsMenu;
	
	/** The listener to the COM port. The port is specified in the settings.
	 * Transmitted data over this port will be handled according to the datahandler settings.  */
	private ListenerToRS232 listenerToRs232;

	/** The latch is used to support multithreading. As long as the <code>ListenerToRS232</code> is active, the menu wont be shown again. */
	private CountDownLatch latch;

	/**
	 *  Constructs a new <code>TosohAPI</code> that manages the application
	 *  Its used to listen to a COM port connected to a Tosoh G7.
	 *  
	 *  The initialize method will alway be called to load all current settings of the application.
	 */
	public TosohAPI() {
		this.initialize();
	}

	/* 
	 * @see java.lang.Runnable#run()
	 * 
	 * The run Method keeps the application alive.
	 * As long as the API_IS_RUNNING boolean is true it will show a menu and await the user input. 
	 * The menu contains Transmitting data, Changing the Settings and shuttigng down the application.
	 */
	@Override
	public void run() {
		this.API_IS_RUNNING = true;

		while (this.API_IS_RUNNING) {
			try {
				System.out.println("\nWhat would you like to do?\n");
				System.out.println("Menu: \n");
				System.out.println("Transmit data      -> 1");
				System.out.println("Settings           -> 2");
				System.out.println("Exit application   -> 3");
				String input = this.scanner.nextLine();
				switch (input) {
				case "1":
					this.latch = new CountDownLatch(1);
					this.listenerToRs232.connectPort(this.settings.getPort(), this.latch);
					try {
						this.latch.await();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					break;
				case "2":
					this.settingsMenu.showSettingsMenu();
					break;
				case "3":
					this.listenerToRs232.disconnectPort();
					System.out.println("Application is shutting down");
					this.API_IS_RUNNING = false;
					break;
				default:
					System.out.println("Invalid Input - what would you like to do?");
					break;
				}

			} catch (PropertyDoesNotExistException e) {
				System.out.println("\n\n"+e.getMessage());
				try {
					Thread.sleep(600);
				} catch (InterruptedException e1) {
				}
				System.out.println("There was a problem with the settings of the application\n\n");
				this.settingsMenu.setUpSettings();
			}
		}
	}

	/**
	 * Initialize gets called if the <code>TosohAPI</code> is initialized. It uses the 
	 * <code>SettingsManager</code> to read the current settings, and initializes the application with the given values.
	 */
	private void initialize() {
		this.scanner = new Scanner(System.in);
		this.listenerToRs232 = new ListenerToRS232();
		this.settings = SettingsManager.getSettingsManager();
		this.settingsMenu = new SettingsMenu();

		if (!settings.hasSettings()) {
			this.settingsMenu.setUpSettings();
		}
	}

}

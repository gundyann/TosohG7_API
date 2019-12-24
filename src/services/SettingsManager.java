
/**
 *date: 05.11.2019   -  time: 07:11:29
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.comm.CommPortIdentifier;

import enums.FileType;
import exceptions.PropertyDoesNotExistException;

/**
 * The Class SettingsManager manages all changes in the settings of the <code>TosohAPI</code>. This Class is implemented in the singleton pattern.
 */
public class SettingsManager {

	/** The settings manager that manages all changes in the settings.xml file. */
	private static SettingsManager settingsManager = null;

	/** The settings of the application are stored in the <code>Properties</code> of the application.. */
	private Properties settings;

	
	
	
	
	/**
	 * Gets the settings manager.
	 *
	 * @return the settings manager
	 */
	public static SettingsManager getSettingsManager() {
		if (settingsManager == null) {
			settingsManager = new SettingsManager();
		}
		return settingsManager;
	}
	
	
	/**
	 * Instantiates a new settings manager. This will due to the singleton pattern only be called once.
	 */
	private SettingsManager() {
		this.loadSettings();
	}
	
	
	
	
	
	/**
	 * Checks if saving to system is enabled.
	 *
	 * @return true, if saving to system is enabled
	 */
	public boolean isSavingToSystemEnabled(){
		if (this.settings.containsKey("toSystemEnabled")) {
			return Boolean.valueOf(this.settings.getProperty("toSystemEnabled"));
		}
		else return false;
	}
	
	
	
	
	
	/**
	 * Sets the saving to system enabled/disabled.
	 *
	 * @param isEnabled the new value that determines if saving to system is enabled
	 */
	public void setSavingToSystemEnabled(boolean isEnabled) {
		this.settings.setProperty("toSystemEnabled", Boolean.toString(isEnabled));
		if(isEnabled == false) {
			if(this.settings.containsKey("dataformatsToSystem")) {
				this.settings.remove("dataformatsToSystem");
			}
		}
		this.saveChanges();
	}
	
	
	
	

	/**
	 * Gets the system path that is used to save the dataoutput locally.
	 *
	 * @return the system path
	 * @throws PropertyDoesNotExistException the property does not exist exception
	 */
	public String getSystemPath() throws PropertyDoesNotExistException {
		if (this.settings.containsKey("systemPath")) {
			return this.settings.getProperty("systemPath");
		}
		throw new PropertyDoesNotExistException("Can't find property 'systemPath'");
	}
	
	
	
	

	/**
	 * Sets the system path.
	 *
	 * @param path the new system path
	 */
	public void setSystemPath(String path) {
		try {
			Paths.get(path);
		} catch (InvalidPathException e) {
			System.out.println(e.getMessage());
		}
		this.settings.setProperty("systemPath", path);
		this.saveChanges();
	}
	
	
	
	
	
	/**
	 * Checks if sending to HTTP is enabled.
	 *
	 * @return true, if sending to HTTP is enabled
	 */
	public boolean isSendingToHTTPEnabled() {
		if (this.settings.containsKey("toHTTPEnabled")) {
			return Boolean.valueOf(this.settings.getProperty("toHTTPEnabled"));
		}
		else return false;
	}
	
	
	
	
	
	/**
	 * Sets the sending to HTTP enabled/disabled.
	 *
	 * @param isEnabled the new value determining if sending to HTTP is enabled
	 */
	public void setSendingToHTTPEnabled(boolean isEnabled) {
		this.settings.setProperty("toHTTPEnabled", Boolean.toString(isEnabled));
		if(isEnabled == false) {
			if(this.settings.containsKey("dataformatsToHTTP")) {
				this.settings.remove("dataformatsToHTTP");
			}
		} else {
			this.addDataformatToSendToHTTP(FileType.XML);
		}
		this.saveChanges();
	}

	
	
	
	
	/**
	 * Gets the http url.
	 *
	 * @return the http url
	 * @throws PropertyDoesNotExistException the property does not exist exception
	 */
	public String getHTTP_URL() throws PropertyDoesNotExistException {
		if (this.settings.containsKey("HTTP_URL")) {
			return this.settings.getProperty("HTTP_URL");
		}
		throw new PropertyDoesNotExistException("Can't find property 'HTTP_URL'");
	}
	
	
	
	
	
	/**
	 * Sets the http url.
	 *
	 * @param url the new http url
	 */
	public void setHTTP_URL(String url) {
		try {
			URL u = new URL(url);
			u.toURI();
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
		} catch (URISyntaxException e) {
			System.out.println(e.getMessage());
		}
		this.settings.setProperty("HTTP_URL", url);
		this.saveChanges();
	}
	
	
	
	
	
	
	/**
	 * Gets the COM port that the <code>TosohAPI</code> should listen to.
	 *
	 * @return the port
	 * @throws PropertyDoesNotExistException the property does not exist exception
	 */
	@SuppressWarnings("unchecked")
	public CommPortIdentifier getPort() throws PropertyDoesNotExistException {
		if (this.settings.containsKey("port")) {
			Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
			while (portList.hasMoreElements()) {
				CommPortIdentifier portId = portList.nextElement();
				if(portId.getName().equals(this.settings.get("port"))) {
					return portId;
				}
			}			
		}
		throw new PropertyDoesNotExistException("Can't find property 'port'");
	}
	
	
	
	
	

	/**
	 * Sets the COM port of the <code>TosohAPI</code>.
	 *
	 * @param port the new port
	 */
	public void setPort(CommPortIdentifier port) {
		this.settings.setProperty("port", port.getName());
		this.saveChanges();
	}
	
	
	
	/**
	 * Adds a dataformat to the list that represents all active dataformats in which transmitted data will be saved locally.
	 *
	 * @param type the datatype that should be added to the list of active dataformats.
	 */
	public void addDataformatToSaveToSystemList(FileType type) {
		List<String> dataformatsToSystem;
		if(this.settings.containsKey("dataformatsToSystem")) {
			dataformatsToSystem = new ArrayList<String>(Arrays.asList(String.valueOf(this.settings.get("dataformatsToSystem")).split(",")));
			if(!dataformatsToSystem.contains(type.toString())) {
				dataformatsToSystem.add(type.toString());
			}
			this.settings.setProperty("dataformatsToSystem", String.join(",", dataformatsToSystem));
		} else {
			dataformatsToSystem = new ArrayList<>();
			dataformatsToSystem.add(type.toString());
			this.settings.setProperty("dataformatsToSystem", String.join(",", dataformatsToSystem));
		}		
		this.saveChanges();
	}
	
	
	/**
	 * Gets list of the current dataformats that the transmitted data is saved as.
	 *
	 * @return the to system dataformats
	 * @throws PropertyDoesNotExistException the property does not exist exception
	 */
	public List<String> getToSystemDataformats() throws PropertyDoesNotExistException{
		if(this.settings.containsKey("dataformatsToSystem")) {
			return Arrays.asList(String.valueOf(this.settings.get("dataformatsToSystem")).split(","));
		}
		throw new PropertyDoesNotExistException("Can't find property 'dataformatsToSystem'");
	}
	
	
	
	
	
	/**
	 * Adds the dataformat to send to HTTP.
	 * 
	 * This method is currently not in use as the Orchestra Scenario only supports XML.
	 *
	 * @param type the type
	 */
	public void addDataformatToSendToHTTP(FileType type) {
		List<String> dataformatsToSystem;
		if(this.settings.containsKey("dataformatsToHTTP")) {
			dataformatsToSystem = new ArrayList<String>(Arrays.asList(String.valueOf(this.settings.get("dataformatsToHTTP")).split(",")));
			if(!dataformatsToSystem.contains(type.toString())) {
				dataformatsToSystem.add(type.toString());
			}
			this.settings.setProperty("dataformatsToHTTP", String.join(",", dataformatsToSystem));
		} else {
			dataformatsToSystem = new ArrayList<>();
			dataformatsToSystem.add(type.toString());
			this.settings.setProperty("dataformatsToHTTP", String.join(",", dataformatsToSystem));
		}		
		this.saveChanges();
	}
	
	
	
	
	
	
	/**
	 * Gets the to HTTP dataformats.
	 *
	 * @return the to HTTP dataformats
	 * @throws PropertyDoesNotExistException the property does not exist exception
	 */
	public List<String> getToHTTPDataformats() throws PropertyDoesNotExistException{
		if(this.settings.containsKey("dataformatsToHTTP")) {
			return Arrays.asList(String.valueOf(this.settings.get("dataformatsToHTTP")).split(","));
		}
		throw new PropertyDoesNotExistException("Can't find property 'dataformatsToHTTP'");
	}
	
	
	
	
	

	/**
	 * Checks for settings.
	 *
	 * @return true, if successful
	 */
	public boolean hasSettings() {
		return !this.settings.isEmpty();
	}
	
	
	
	
	

	/**
	 * Save changes.
	 */
	private void saveChanges() {
		try {
			this.settings.storeToXML(new FileOutputStream("settings.xml"), "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	

	/**
	 * Load settings.
	 */
	private void loadSettings() {
		this.settings = new Properties();
		try {
			this.settings.loadFromXML(new FileInputStream("settings.xml"));
		} catch (IOException e) {
		}

	}
}

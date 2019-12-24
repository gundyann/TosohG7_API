
/**
 *date: 08.11.2019   -  time: 11:31:51
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package dataHandler;

import enums.FileType;
import exceptions.PropertyDoesNotExistException;
import services.SettingsManager;

public class DataHandlerBuilder {

	private SettingsManager settings;

	public DataHandlerBuilder() {
		this.settings = SettingsManager.getSettingsManager();
	}

	public DataHandler buildDataHandler(DataHandler handler) throws PropertyDoesNotExistException {
			if(this.settings.isSavingToSystemEnabled()) {
				if (!this.settings.getToSystemDataformats().isEmpty()) {
					for (String type : this.settings.getToSystemDataformats()) {
						handler = FileType.valueOf(type).appendToSystemHandler(handler);
					}
				}
			} else {
				System.out.println("Saving data to System: disabled");
			}
			if (this.settings.isSendingToHTTPEnabled()) {
				if (!this.settings.getToHTTPDataformats().isEmpty()) {
					for (String type : this.settings.getToHTTPDataformats()) {
						handler = FileType.valueOf(type).appendToHTTPHandler(handler);
					}
				}
			} else {
				System.out.println("Sending data via HTTP: disabled");
			}		
		return handler;
	}
}


/**
 *date: 08.11.2019   -  time: 10:10:01
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package dataHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

import services.SettingsManager;

public abstract class DataHandlerDecorator extends DataHandler{

	protected DataHandlerG7 handler;
	protected SettingsManager settings;
	protected String timestamp;
	
	public DataHandlerDecorator(DataHandlerG7 handler) {
		this.timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		this.settings = SettingsManager.getSettingsManager();
		this.handler = handler;
	}
	
}

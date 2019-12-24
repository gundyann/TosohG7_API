
/**
 *date: 08.11.2019   -  time: 10:13:41
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package dataHandler;

import exceptions.PropertyDoesNotExistException;
import g7anbindung.BlutmessungG7;

public class ToSystemTXT extends DataHandlerToSystem {

	public ToSystemTXT(DataHandlerG7 handler) {
		super(handler);
	}

	@Override
	public void handleData(BlutmessungG7 data) {
		this.handler.handleData(data);

		try {
			String path = this.settings.getSystemPath();
			String content = "";
			path = path + "\\output_" + this.timestamp + data.getPatientID()+".txt";
			content = content + data.toString();
			
			writeFileToSystem(path, content);
		} catch (PropertyDoesNotExistException e) {
			System.out.println(e.getMessage());
		}
	}

}


/**
 *date: 08.11.2019   -  time: 10:12:01
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package dataHandler;

import exceptions.PropertyDoesNotExistException;
import g7anbindung.BlutmessungG7;

public class ToSystemCSV extends DataHandlerToSystem {

	public ToSystemCSV(DataHandlerG7 handler) {
		super(handler);
	}

	@Override
	public void handleData(BlutmessungG7 data) {
		this.handler.handleData(data);

		try {
			String path = this.settings.getSystemPath();
			String content = "";
			path = path + "\\output_" + this.timestamp + data.getPatientID()+".csv";
			content = "operation mode,sample position,sample number,value01,value meaning 01,value02,value meaning 02,value03,"
					+ "value meaning 03,value04,value meaning 04,value05,value meaning 05,value06,value meaning 06,value07,"
					+ "value meaning 07,value08,value meaning 08,value09,value meaning 09,value10,value meaning 10,barcode\r\n";

			content = content + data.toStringCSV();

			this.writeFileToSystem(path, content);
		} catch (PropertyDoesNotExistException e) {
			System.out.println(e.getMessage());
		}
	}
}

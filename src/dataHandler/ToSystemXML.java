
/**
 *date: 08.11.2019   -  time: 10:13:16
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package dataHandler;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import exceptions.PropertyDoesNotExistException;
import g7anbindung.BlutmessungG7;
import services.XMLCreator;

public class ToSystemXML extends DataHandlerToSystem{

	public ToSystemXML(DataHandlerG7 handler) {
		super(handler);
	}

	@Override
	public void handleData(BlutmessungG7 data) {
		this.handler.handleData(data);

		try {
			String path = this.settings.getSystemPath();
			String content = "";
			path = path + "\\output_" + this.timestamp +data.getPatientID()+ ".xml";
			
			XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
			Document document = new XMLCreator().convertBlutmessungToXML(data);
			content = xmlOutput.outputString(document);
			this.writeFileToSystem(path, content);
		} catch (PropertyDoesNotExistException e) {
			System.out.println(e.getMessage());
		}	
	}
}

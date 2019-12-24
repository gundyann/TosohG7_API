
/**
 *date: 08.11.2019   -  time: 10:14:22
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

public class ToHTTP_XML extends DataHandlerToHTTP{

	public ToHTTP_XML(DataHandlerG7 handler) {
		super(handler);
	}

	@Override
	public void handleData(BlutmessungG7 data) {
		this.handler.handleData(data);

		try {
			String url = this.settings.getHTTP_URL();
			String content = "";	
			
			XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
			Document document = new XMLCreator().convertBlutmessungToXML(data);
			content = xmlOutput.outputString(document);
			this.postFileToURL(content, url);
		} catch (PropertyDoesNotExistException e) {
			System.out.println(e.getMessage());
		}			
	}


}

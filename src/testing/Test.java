
/**
 *date: 07.11.2019   -  time: 16:07:23
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package testing;

import dataHandler.DataHandler;
import dataHandler.DataHandlerBuilder;
import exceptions.PropertyDoesNotExistException;
import exceptions.WrongFormatException;
import g7anbindung.BlutmessungG7;

public class Test {

	public static void main(String[] args) {

		try {
			
			
			String str = "12001  0.5  0.9  0.7  1.8  6.4 92.0  0.0  0.0  0.0  6.4001050546101          ";

			BlutmessungG7 data = new BlutmessungG7(str);
			DataHandler handler = new DataHandlerBuilder().buildDataHandler(new DataHandler());
			handler.handleData(data);
			
			

		} catch (WrongFormatException e1) {
			System.out.println(e1.getMessage());
		} catch (PropertyDoesNotExistException e) {
			System.out.println(e.getMessage());
		} finally {
			System.out.println("\n\nFiles were successfully created");
		}
	}
}
//String str2 = "02001  0.5  0.9  0.7  1.8  5.1 92.0  0.0  0.0  0.0  6.4001050546102          ";
//String str3 = "02001  0.5  0.9  0.7  1.8  5.1 92.0  0.0  0.0  0.0  6.4001050546103          ";
//String str4 = "02001  0.5  0.9  0.7  1.8  5.1 92.0  0.0  0.0  0.0  6.4001050546104          ";
//String str5 = "02001  0.5  0.9  0.7  1.8  5.1 92.0  0.0  0.0  0.0  6.4001050546105          ";
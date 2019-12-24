package enums;

import dataHandler.DataHandler;
import dataHandler.ToHTTP_XML;
import dataHandler.ToSystemCSV;
import dataHandler.ToSystemTXT;
import dataHandler.ToSystemXML;

/**
 *	Contains all possible dataformats that gets supported by the TosohG7 API
 *
 */

public enum FileType {
	CSV{
		@Override
		public DataHandler appendToSystemHandler(DataHandler handler) {
			return new ToSystemCSV(handler);
		}
		@Override
		public DataHandler appendToHTTPHandler(DataHandler handler) {
			return handler; //Not implemented yet, does nothing right now
		}
	},
	TXT{
		@Override
		public DataHandler appendToSystemHandler(DataHandler handler) {
			return new ToSystemTXT(handler);
		}
		@Override
		public DataHandler appendToHTTPHandler(DataHandler handler) {
			return handler; //Not implemented yet, does nothing right now
		}
	},
	XML{
		@Override
		public DataHandler appendToSystemHandler(DataHandler handler) {
			return new ToSystemXML(handler);
		}
		@Override
		public DataHandler appendToHTTPHandler(DataHandler handler) {
			return new ToHTTP_XML(handler);
		}
	};
	
	/**
	 * Appends the given Type as a datahandler to the already existing handler, giving it more functionallity.
	 * Appends to the toSystem Handler that will write the output to the filesystem.
	 * @param handler - the preexisting datahandler, that gets expanded with more functionallity
	 * @return
	 */
	public abstract DataHandler appendToSystemHandler(DataHandler handler);
	
	/**
	 * Appends the given Type as a datahandler to the already existing handler, giving it more functionallity.
	 * Appends to the toHTTP Handler that will send the output to the given URL.
	 * @param handler - the preexisting datahandler, that gets expanded with more functionallity
	 * @return
	 */
	public abstract DataHandler appendToHTTPHandler(DataHandler handler);
}

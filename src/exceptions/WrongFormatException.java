package exceptions;

/**
 *date: 15.10.2019   -  time: 07:16:45
 *user: yanng   -  yann.gund@gmx.ch
 *
 */

public class WrongFormatException extends Exception{
	
	/**
	 * Exception that is thrown if the transmitted data does not fullfill the conditions to be a valid dataoutput of the Tosoh G7 in the G7 format.  
	 */
	private static final long serialVersionUID = 1L;

	public WrongFormatException(String e) {
		super(e);
	}
}


/**
 *date: 05.11.2019   -  time: 07:52:13
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package exceptions;

/**
 * Exception that is thrown if the searched property does not exist in the XML settings file.
 */
public class PropertyDoesNotExistException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public PropertyDoesNotExistException(String e) {
		super(e);
	}
}

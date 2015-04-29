/*
 * Created:  Oct 6, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.login.exception;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UserNotFoundException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public UserNotFoundException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public UserNotFoundException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}

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
public class UserNotLoggedInException extends Exception {

	/**
	 * <p></p>
	 *
	 */
	public UserNotLoggedInException() {}

	/**
	 * <p></p>
	 *
	 * @param message
	 */
	public UserNotLoggedInException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 *
	 * @param cause
	 */
	public UserNotLoggedInException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 * @param cause
	 */
	public UserNotLoggedInException(String message, Throwable cause) {
		super(message, cause);
	}
}

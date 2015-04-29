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
public class EmailAlreadyTakenException extends Exception {

	/**
	 * <p></p>
	 *
	 */
	public EmailAlreadyTakenException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 */
	public EmailAlreadyTakenException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * <p></p>
	 *
	 * @param cause
	 */
	public EmailAlreadyTakenException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 * @param cause
	 */
	public EmailAlreadyTakenException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}

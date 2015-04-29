/**
 * 
 */
package com.livescribe.aws.login.exception;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class InvalidResponseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1184863174817380363L;

	/**
	 * <p></p>
	 *
	 */
	public InvalidResponseException() {
		
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 */
	public InvalidResponseException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 *
	 * @param cause
	 */
	public InvalidResponseException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 * @param cause
	 */
	public InvalidResponseException(String message, Throwable cause) {
		super(message, cause);
	}
}

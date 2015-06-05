/*
 * Created:  Oct 31, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.exception;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class InvalidRegistrationTokenException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public InvalidRegistrationTokenException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public InvalidRegistrationTokenException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public InvalidRegistrationTokenException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public InvalidRegistrationTokenException(String message, Throwable cause) {
		super(message, cause);
	}

}

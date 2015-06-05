/*
 * Created:  Oct 26, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.exception;

/**
 * <p>Thrown when an error occurs during the device registration confirmation process.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegistrationConfirmationException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public RegistrationConfirmationException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public RegistrationConfirmationException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public RegistrationConfirmationException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public RegistrationConfirmationException(String message, Throwable cause) {
		super(message, cause);
	}

}

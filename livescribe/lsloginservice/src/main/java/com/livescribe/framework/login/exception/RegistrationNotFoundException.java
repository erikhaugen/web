/*
 * Created:  Oct 19, 2011
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
public class RegistrationNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 765284901507945107L;

	/**
	 * <p></p>
	 * 
	 */
	public RegistrationNotFoundException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public RegistrationNotFoundException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public RegistrationNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public RegistrationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}

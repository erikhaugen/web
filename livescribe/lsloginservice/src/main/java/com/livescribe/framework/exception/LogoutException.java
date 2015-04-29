/*
 * Created:  Nov 8, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.exception;

/**
 * 
 * @author kle
 *
 */
public class LogoutException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public LogoutException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public LogoutException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public LogoutException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public LogoutException(String message, Throwable cause) {
		super(message, cause);
	}

}

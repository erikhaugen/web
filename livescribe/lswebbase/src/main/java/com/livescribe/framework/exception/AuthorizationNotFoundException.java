package com.livescribe.framework.exception;

/**
 * 
 * @author kle
 *
 */
public class AuthorizationNotFoundException extends Exception {

	/**
	 * <p></p>
	 *
	 */
	public AuthorizationNotFoundException() {
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 */
	public AuthorizationNotFoundException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 *
	 * @param cause
	 */
	public AuthorizationNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 * @param cause
	 */
	public AuthorizationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}

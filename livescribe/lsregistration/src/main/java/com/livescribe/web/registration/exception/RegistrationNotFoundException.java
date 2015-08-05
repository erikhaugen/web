package com.livescribe.web.registration.exception;

public class RegistrationNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6701072060342902849L;

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

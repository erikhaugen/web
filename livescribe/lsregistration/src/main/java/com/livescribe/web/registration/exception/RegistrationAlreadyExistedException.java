package com.livescribe.web.registration.exception;

public class RegistrationAlreadyExistedException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2927747619528716280L;

	/**
	 * <p></p>
	 * 
	 */
	public RegistrationAlreadyExistedException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public RegistrationAlreadyExistedException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public RegistrationAlreadyExistedException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public RegistrationAlreadyExistedException(String message, Throwable cause) {
		super(message, cause);
	}
}

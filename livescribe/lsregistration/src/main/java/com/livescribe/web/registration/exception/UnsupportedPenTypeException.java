package com.livescribe.web.registration.exception;

public class UnsupportedPenTypeException extends Exception {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6676419566981417166L;

	/**
	 * <p></p>
	 * 
	 */
	public UnsupportedPenTypeException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public UnsupportedPenTypeException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public UnsupportedPenTypeException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public UnsupportedPenTypeException(String message, Throwable cause) {
		super(message, cause);
	}
}

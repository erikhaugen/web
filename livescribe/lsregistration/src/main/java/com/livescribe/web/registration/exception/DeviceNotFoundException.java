package com.livescribe.web.registration.exception;

public class DeviceNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6701072060342902849L;

	/**
	 * <p></p>
	 * 
	 */
	public DeviceNotFoundException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public DeviceNotFoundException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public DeviceNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public DeviceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}

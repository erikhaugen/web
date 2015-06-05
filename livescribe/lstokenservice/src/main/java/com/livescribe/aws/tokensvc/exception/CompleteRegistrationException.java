package com.livescribe.aws.tokensvc.exception;

/**
 * 
 * @author kle
 *
 */
public class CompleteRegistrationException extends Exception {
	
	/**
	 * 
	 */
	public CompleteRegistrationException() {
		super();
	}
	
	/**
	 * 
	 * @param message
	 */
	public CompleteRegistrationException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param cause
	 */
	public CompleteRegistrationException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public CompleteRegistrationException(String message, Throwable cause) {
		super(message, cause);
	}
}

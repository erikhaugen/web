package com.livescribe.aws.tokensvc.exception;

/**
 * 
 * @author kle
 */
public class DuplicateUidException extends Exception {

	/**
	 * 
	 */
	public DuplicateUidException() {

	}
	
	/**
	 * 
	 * @param message
	 */
	public DuplicateUidException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param cause
	 */
	public DuplicateUidException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public DuplicateUidException(String message, Throwable cause) {
		super(message, cause);
	}
}

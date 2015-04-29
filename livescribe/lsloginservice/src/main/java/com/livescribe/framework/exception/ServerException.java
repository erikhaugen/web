package com.livescribe.framework.exception;

/**
 * 
 * @author kle
 *
 */
public class ServerException  extends Exception {
	/**
	 * 
	 */
	public ServerException() {
		super();
	}
	
	/**
	 * 
	 * @param message
	 */
	public ServerException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param cause
	 */
	public ServerException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public ServerException(String message, Throwable cause) {
		super(message, cause);
	}
}

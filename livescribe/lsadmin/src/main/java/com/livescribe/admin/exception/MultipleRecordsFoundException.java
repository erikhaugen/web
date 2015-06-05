package com.livescribe.admin.exception;


/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MultipleRecordsFoundException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public MultipleRecordsFoundException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public MultipleRecordsFoundException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public MultipleRecordsFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public MultipleRecordsFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}

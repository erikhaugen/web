/**
 * Created:  Aug 15, 2013 10:22:41 AM
 */
package com.livescribe.framework.exception;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MultipleRecordsFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 513882626257957874L;

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
	 * @param arg0
	 */
	public MultipleRecordsFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public MultipleRecordsFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}

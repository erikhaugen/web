/*
 * Created:  Dec 5, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.exception;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class InvalidParameterException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public InvalidParameterException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public InvalidParameterException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public InvalidParameterException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public InvalidParameterException(String message, Throwable cause) {
		super(message, cause);
	}

}

/*
 * Created:  Sep 26, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.exception;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class DuplicateSerialNumberException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public DuplicateSerialNumberException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public DuplicateSerialNumberException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public DuplicateSerialNumberException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public DuplicateSerialNumberException(String message, Throwable cause) {
		super(message, cause);
	}

}

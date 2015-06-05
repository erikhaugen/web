/*
 * Created:  Nov 29, 2011
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
public class MultipleRegistrationsFoundException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public MultipleRegistrationsFoundException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public MultipleRegistrationsFoundException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public MultipleRegistrationsFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public MultipleRegistrationsFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}

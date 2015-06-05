/*
 * Created:  Nov 8, 2011
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
public class IncorrectPasswordException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public IncorrectPasswordException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public IncorrectPasswordException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public IncorrectPasswordException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public IncorrectPasswordException(String message, Throwable cause) {
		super(message, cause);
	}

}

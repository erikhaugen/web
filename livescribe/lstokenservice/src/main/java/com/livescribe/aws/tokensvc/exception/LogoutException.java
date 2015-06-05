/*
 * Created:  Nov 30, 2011
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
public class LogoutException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public LogoutException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public LogoutException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public LogoutException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public LogoutException(String message, Throwable cause) {
		super(message, cause);
	}

}

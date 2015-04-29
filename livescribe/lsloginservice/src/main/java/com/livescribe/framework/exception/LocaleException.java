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
public class LocaleException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public LocaleException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public LocaleException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public LocaleException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public LocaleException(String message, Throwable cause) {
		super(message, cause);
	}

}

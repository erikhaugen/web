/**
 * 
 */
package com.livescribe.framework.oauth.exception;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MultiplePrimaryAuthorizationsFoundException extends Exception {

	/**
	 * <p></p>
	 *
	 */
	public MultiplePrimaryAuthorizationsFoundException() {
	}

	/**
	 * <p></p>
	 *
	 * @param arg0
	 */
	public MultiplePrimaryAuthorizationsFoundException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 *
	 * @param arg0
	 */
	public MultiplePrimaryAuthorizationsFoundException(Throwable t) {
		super(t);
	}

	/**
	 * <p></p>
	 *
	 * @param arg0
	 * @param arg1
	 */
	public MultiplePrimaryAuthorizationsFoundException(String message,
			Throwable t) {
		super(message, t);
	}
}

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
public class LSDSClientException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public LSDSClientException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public LSDSClientException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public LSDSClientException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public LSDSClientException(String message, Throwable cause) {
		super(message, cause);
	}

}

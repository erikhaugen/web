/*
 * Created:  Dec 7, 2011
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
public class DuplicateEmailAddressException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public DuplicateEmailAddressException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public DuplicateEmailAddressException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public DuplicateEmailAddressException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public DuplicateEmailAddressException(String message, Throwable cause) {
		super(message, cause);
	}

}

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
public class ClientException extends Exception {

	/**
	 * <p></p>
	 *
	 */
	public ClientException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 */
	public ClientException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * <p></p>
	 *
	 * @param cause
	 */
	public ClientException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 * @param cause
	 */
	public ClientException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}

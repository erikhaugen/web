/**
 * 
 */
package com.livescribe.framework.client.exception;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6868051557441122743L;

	/**
	 * <p></p>
	 *
	 */
	public ClientException() {
		
	}

	/**
	 * <p></p>
	 *
	 * @param arg0
	 */
	public ClientException(String arg0) {
		super(arg0);
	}

	/**
	 * <p></p>
	 *
	 * @param arg0
	 */
	public ClientException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * <p></p>
	 *
	 * @param arg0
	 * @param arg1
	 */
	public ClientException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}

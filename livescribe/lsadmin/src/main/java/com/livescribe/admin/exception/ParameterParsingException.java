package com.livescribe.admin.exception;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ParameterParsingException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public ParameterParsingException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public ParameterParsingException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public ParameterParsingException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public ParameterParsingException(String message, Throwable cause) {
		super(message, cause);
	}

}

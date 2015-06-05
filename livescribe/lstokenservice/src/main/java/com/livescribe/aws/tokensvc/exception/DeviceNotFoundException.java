/*
 * Created:  Sep 26, 2011
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
public class DeviceNotFoundException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public DeviceNotFoundException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public DeviceNotFoundException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public DeviceNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public DeviceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}

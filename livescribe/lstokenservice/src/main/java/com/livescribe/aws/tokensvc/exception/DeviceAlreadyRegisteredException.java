/*
 * Created:  Oct 6, 2011
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
public class DeviceAlreadyRegisteredException extends Exception {

	/**
	 * <p></p>
	 * 
	 */
	public DeviceAlreadyRegisteredException() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 */
	public DeviceAlreadyRegisteredException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 * 
	 * @param cause
	 */
	public DeviceAlreadyRegisteredException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * @param cause
	 */
	public DeviceAlreadyRegisteredException(String message, Throwable cause) {
		super(message, cause);
	}

}

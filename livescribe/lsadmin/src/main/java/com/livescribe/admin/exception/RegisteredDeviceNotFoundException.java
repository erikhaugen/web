/**
 * 
 */
package com.livescribe.admin.exception;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegisteredDeviceNotFoundException extends Exception {

	/**
	 * <p></p>
	 *
	 */
	public RegisteredDeviceNotFoundException() {

	}

	/**
	 * <p></p>
	 *
	 * @param arg0
	 */
	public RegisteredDeviceNotFoundException(String arg0) {
		super(arg0);
	}

	/**
	 * <p></p>
	 *
	 * @param arg0
	 */
	public RegisteredDeviceNotFoundException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * <p></p>
	 *
	 * @param arg0
	 * @param arg1
	 */
	public RegisteredDeviceNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}

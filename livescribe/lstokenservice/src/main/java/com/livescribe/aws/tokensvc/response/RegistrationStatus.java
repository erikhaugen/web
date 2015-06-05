/**
 * 
 */
package com.livescribe.aws.tokensvc.response;

/**
 * <p>Represents the state of a device&apos;s registration with LS Token Service.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public enum RegistrationStatus {

	/**
	 * The device is not registered and there is no &quot;temporary&quot; 
	 * record in the database.
	 */
	UNREGISTERED	(-1, "Unregistered."),
	
	/**
	 * The device&apos;s registration code has been entered by the user.
	 */
	COMPLETE		(0, "Registration Complete."),
	
	/**
	 * 
	 */
	IN_PROGRESS		(1, "Registration is in progress.");
	
	private int code;
	private String message;
	
	RegistrationStatus(int code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}

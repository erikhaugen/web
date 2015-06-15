/**
 * Created:  Nov 15, 2010 3:38:32 PM
 */
package com.livescribe.community.service;

/**
 * <p>Hold information about the result of an operation or method invocation.</p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public enum ResponseStatus {

	OK					("Success", 0),
	SERVER_ERROR		("Server Error", -1),
	INVALID_EMAIL		("Invalid e-mail address.", -2),
	INVALID_GUID		("Invalid LD GUID.", -3),
	INVALID_VERSION		("Invalid LD Version.", -4),
	INVALID_TOKEN		("Invalid login token.", -5),
	USER_NOT_LOGGED_IN	("User not logged in.", -6),
	MISSING_PARAMETER	("Required parameter is missing.", -7),
	INVALID_PARAMETER	("Required parameter is invalid.", -8),
	USER_ALREADY_EXISTS	("User already exists.", -9),
	LOGOUT_FAILED		("Logout failed.", -10);
	
	private final String message;
	private final int code;
	
	/**
	 * <p>Constructor that takes both a numeric code and text message as parameters.</p>
	 * 
	 * @param message The message to associate with the given code.
	 * @param code A number to uniquely identify a status.
	 */
	ResponseStatus(String message, int code) {
		this.message = message;
		this.code = code;
	}
	
	/**
	 * <p>Returns the error message of the response.</p>
	 * 
	 * @return the error message of the response.
	 */
	public String message() { return message; }
	
	/**
	 * <p>Returns the error code of the response.</p>
	 * 
	 * @return the error code of the response.
	 */
	public int code() { return code; }
}

/*
 * Created:  Sep 23, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.response;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public enum ResponseCode {

	SUCCESS				(0, "Success"),
	SERVER_ERROR		( -1, "Server Error"),
	INVALID_EMAIL		( -2, "Invalid e-mail address."),
	INVALID_GUID		( -3, "Invalid LD GUID."),
	INVALID_VERSION		( -4, "Invalid LD Version."),
	INVALID_TOKEN		( -5, "Invalid login token."),
	USER_NOT_LOGGED_IN	( -6, "User not logged in."),
	MISSING_PARAMETER	( -7, "Required parameter is missing."),
	INVALID_PARAMETER	( -8, "Required parameter is invalid."),
	USER_ALREADY_EXISTS	( -9, "User already exists."),
	LOGOUT_FAILED		(-10, "Logout failed."),
	USER_NOT_FOUND		(-11, "User not found."),
	NOT_IMPLEMENTED		(-12, "Method not implemented yet."),
	UNABLE_TO_LOG_USER_IN			(-13, "Unable to log user in."),
	USER_ALREADY_LOGGED_IN			(-14, "User already logged in."),
	USER_IS_NOT_A_DISTRIBUTOR		(-15, "User is not a distributor"),
	APP_ALREADY_PROVISIONED			(-16, "App has already been provisioned."),
	APP_NOT_YET_PROVISIONED 		(-17, "App has not yet been provisioned."),
	INCORRECT_LOGIN_DOMAIN			(-18, "User is logged in a different domain."),
	DUPLICATE_SERIAL_NUMBER_FOUND	(-19, "Duplicate serial number found."),
	DEVICE_NOT_FOUND				(-20, "Device not found."),
	DEVICE_ALREADY_REGISTERED		(-21, "Device already registered."),
	REGISTRATION_NOT_FOUND			(-22, "Registration not found."),
	INVALID_REGISTRATION_TOKEN		(-23, "Invalid registration token."),
	DUPLICATE_REGISTRATION_FOUND	(-24, "Duplicate registration found."),
	UNREGISTRATION_FAILED			(-25, "Device could not be unregistered."),
	INVALID_LOCALE					(-26, "Locale was missing or in an invalid format."),
	DUPLICATE_EMAIL_ADDRESS_FOUND	(-27, "Duplicate email address found."),
	EMAIL_SENDING_ERROR				(-28, "Error when sending email."),
	COMPLETE_REGISTRATION_ERROR		(-29, "Error when completing registration."),
	DEVICE_REGISTRATION_ERROR		(-30, "Error when registering device."),
	INCORRECT_PASSWORD				(-31, "Error when logging in."),
	FAILED_TO_PROVIDE_NONCE_DATA	(-32, "Unable to provide nonce data."),
	INVALID_AUTH_DATA				(-33, "Invalid authorization data was provided."),
	INVALID_SETTING_DATA			(-34, "Invalid setting data."),
	NO_SETTING_FOUND				(-35, "No settings found.");
	
	
	private final int code;
	private final String message;
	
	/**
	 * <p>Constructor that takes both a numeric code and text message as parameters.</p>
	 * 
	 * @param message The message to associate with the given code.
	 * @param code A number to uniquely identify a status.
	 */
	ResponseCode(int code, String message) {
		this.message = message;
		this.code = code;
	}

	/**
	 * <p></p>
	 * 
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * <p></p>
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}

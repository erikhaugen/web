/*
 * Created:  Sep 23, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.web.response;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public enum ResponseCode {

	SUCCESS			(0, "Success"),
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
	EMAIL_ALREADY_IN_USE			(-29, "Email address already in use."),
	INCORRECT_PASSWORD				(-30, "Incorrect password."),
	INCORRECT_CAPTCHA				(-31, "Incorrect captcha."),
	UNAUTHORIZED					(-32, "Unauthorized."),
	INSUFFICIENT_PRIVILEGE			(-33, "Insufficient privilege."),
	
	/* Error codes for Evernote coupon redemption */
	INVALID_AUTH					(-34, "Invalid authentication token."),
	INVALID_PURCHASE_CODE			(-35, "Invalid coupon code."),
	ALREADY_REDEEMED_BY_USER		(-36, "Coupon code has already been redeemed."),
	ALREADY_REDEEMED_BY_OTHER_USER	(-37, "Coupon code has already been redeemed by other user."),
	INELIGIBLE_SPONSOR_OWNER		(-38, "Ineligible to redeem coupons because they are the owner of a sponsored group."),
	INELIGIBLE_OTHER				(-39, "Ineligible to redeem coupon."),
	INTERNAL_ERROR					(-40, "Internal server error occurred that prevented the coupon from being redeemed."),
	PREMIUM_EVERNOTE_USER			(-41, "User is a premium Evernote user."),
	NON_PREMIUM_EVERNOTE_USER		(-42, "Not a premium Evernote user."),
	EN_ALREADY_HAVE_SYNCED_DATA		(-43, "The user already had data synced to Evernote"),
	
	/* Client-related response codes. */
	EMPTY_RESPONSE_RECEIVED			(-44, "The received response did not have any useful information.  (i.e. <response />)"),
	
	/* LSShare Error codes */
	UNSUPPORTED_IMAGE_TYPE			(-45, "Unsupported image type"),
	UNSUPPORTED_STROKE_TYPE			(-46, "Unsupported stroke type"),
	BADLY_FORMATEED_STROKE_DATA		(-47, "Badly formatted stroke data"),
	UNSUPPORTED_AUDIO_TYPE			(-48, "Unsupported audio type"),
	MD5_HASH_DOES_NOT_MATCH			(-49, "MD5 hash does not match"),
	USER_TOKEN_INVALID				(-50, "User token is invalid"),
	USER_DOCUMENT_PERMISSION		(-51, "User does not have permission for that document"),
	USER_PAGE_PERMISSION			(-52, "User does not have permission for that page"),
	USER_SESSION_PERMISSION			(-53, "User does not have permission for that session"),
	USER_SOURCE_DOCUMENT_PERMISSION	(-54, "User does not have permission to the source document"),
	USER_SOURCE_PAGES_PERMISSION	(-55, "User does not have permission to the source pages"),
	USER_SOURCE_SESSION_PERMISSION	(-56, "User Source Session Permission"),
	TEMPLATE_DOES_NOT_EXIST			(-57, "Template does not exist"),
	IMAGE_DOES_NOT_EXIST			(-58, "Image does not exist"),
	STROKES_DOES_NOT_EXIST			(-59, "Stroke file does not exist"),
	SESSION_DOES_NOT_EXIST			(-60, "Session does not exist"),
	AUDIO_DOES_NOT_EXIST			(-61, "Audio does not exist"),
	SOURCE_DOCUMENT_DOES_NOT_EXIST	(-62, "Source document does not exist"),
	SOURCE_PAGES_DOES_NOT_EXIST		(-63, "Source pages do not exist"),
	SOURCE_SESSION_DOES_NOT_EXIST	(-64, "Source sessions do not exist"),
	DOCUMENT_DOES_NOT_EXIST			(-65, "Document does not exist"),
	PAGE_DOES_NOT_EXIST				(-66, "Page does not exist"),
	CONTENT_ACCESS_DENIED			(-67, "CAK is invalid for the desired contents"),
	NON_CAK_NOT_SUPPORTED			(-68, "Non-CAK links are not supported. Please write on the original paper and resync the pen contents to playback"),
	DOCUMENT_ALREADY_EXISTS			(-69, "Document already exists"),
	STROKES_ALREADY_EXIST			(-70, "Strokes file already exists (or reside in the past)"),
	AUDIO_ALREADY_EXIST				(-71, "Audio file already exists (or resides in the past)"),
	TIME_MAP_ALREADY_EXIST			(-72, "Time map already exists (or resides in the past)"),
	FAILURE							(-73, "The requested process failed to complete successfully"),

	/*	Transcoding Service response codes */
	M4A_FILE_NOT_FOUND				(-74, "The M4A input file was not found."),
	TRANSCODING_ERROR				(-75, "There was an error during transcoding of the audio file."),

	/* Codes related to Evernote Authorizations */
	NO_EN_AUTHORIZATIONS_FOUND		(-76, "None Evernote Authorizations found for the LS user."),
	AUTH_BY_EN_USERNAME_NOT_FOUND	(-77, "Evernote Authorization by given Evernote Username not found."),
	EN_AUTHORIZATION_IS_EXPIRED		(-78, "Evernote Authorization has been expired."),
	OWNER_IS_NOT_PRIMARY_EN			(-79, "The primary Evernote account is not the owener of this docuement."),
	EN_STORAGE_QUOTA_EXCEEDED		(-80, "The Evernote storage quota has been exceeded."),
	EN_UPLOAD_QUOTA_EXCEEDED		(-81, "The Evernote upload quota has been exceeded."),
	
	NOT_FOUND						(-91, "The requested resource was not found."),
	
	/* Archiving Feature error codes */
	ARCHIVING_DISABLED				(-101, "Archive is disabled, the Archive Notebook request will NOT be processed."),
	ARCHIVING_NOTEBOOK_NOT_FOUND	(-102, "Archived notebook not found to delete."),
	DOCUMENT_IS_ARCHIVED			(-103, "Document is archived.."),

	/* AFD */
	AFD_DATA_MISSING				(-121, "AFD data file is missing."),
	AFD_INVALID_FORMAT				(-122, "AFD data file format is invalid. It should be zip format."),
	AFD_TEMPLATE_ALREADY_UPLOADED	(-123, "AFD data file with same vesion is already stored in the database."),
	AFD_NOT_FOUND 					(-124, "Unable to find the paper product (AFD) related to specified guid or page address."),
	
	/* Pdf+ */
	PDFPLUS_DOC_NOT_FOUND			(-150, "Pdf+ document not found."),
	PDFPLUS_UPLOAD_FAILED			(-151, "Failed to upload the Pdf+ document of the user to the data store."),
	PDFPLUS_IS_INVALID				(-152, "Invalid Pdf+ document is uploaded."),
	
	/* VO Language Package */
	VOLP_NOT_FOUND					(-170, "VO Language Package not found."),
	VOLP_UPLOAD_FAILED				(-171, "Failed to upload the VO Language Package to the environment store."),
	VOLP_IS_INVALID					(-172, "Invalid VO Language Package was uploaded. It should also be in zip format."),

	/* Vector Registration Service */
	UNSUPPORTED_PEN_TYPE			(-190, "The provided pen is not a Vector pen."),
	
	WARNING							(-300, "WARNING");
	
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

/**
 * Created:  Jan 7, 2014 4:14:54 PM
 */
package com.livescribe.web.registration.client;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface MethodURI {

	public static final String METHOD_REGISTER = "register";
	public static final String METHOD_UNREGISTER = "unregister";
	
	public static final String METHOD_FIND_UNIQUE_REGISTRATION = "find/unique";


	public static final String METHOD_FIND_REGISTRATIONS_BY_PENSERIAL_EQUALS = "find/reg/penserial/equals";
	public static final String METHOD_FIND_REGISTRATIONS_BY_PENSERIAL_CONTAINS = "find/reg/penserial/contains";
	
	public static final String METHOD_FIND_REGISTRATIONS_BY_PENDISPLAYID_EQUALS = "find/reg/pendisplayid/equals";
	public static final String METHOD_FIND_REGISTRATIONS_BY_PENDISPLAYID_CONTAINS = "find/reg/pendisplayid/contains";
	
	public static final String METHOD_FIND_REGISTRATIONS_BY_EMAIL_EQUALS = "find/reg/email/equals";
	public static final String METHOD_FIND_REGISTRATIONS_BY_EMAIL_CONTAINS = "find/reg/email/contains";
	
	public static final String METHOD_FIND_REGISTRATIONS_BY_APP_ID = "find/app";
	
	public static final String METHOD_FIND_WARRANTY_BY_PEN_SERIAL = "find/warranty/pen";
	public static final String METHOD_FIND_WARRANTY_BY_EMAIL = "find/warranty/email";
	
	public static final String METHOD_FIND_REGISTRATION_HISTORY_BY_PEN_SERIAL = "history/pen";
	public static final String METHOD_FIND_REGISTRATION_HISTORY_BY_EMAIL = "history/email";
	
	public static final String METHOD_FIND_USERS_BY_EMAIL_EQUALS = "find/users/email/equals";
	public static final String METHOD_FIND_USERS_BY_EMAIL_CONTAINS = "find/users/email/contains";
	
	public static final String METHOD_DELETE_REGISTRATION_BY_EMAIL = "delete/reg/email";
}

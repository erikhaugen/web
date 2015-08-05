/**
 * Created:  Nov 8, 2013 1:03:26 PM
 */
package com.livescribe.web.registration;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface TestConstants {

	public static final String URI_DELETE_REGISTRATION_BY_EMAIL					= "delete/registration/email";

	public static final String URI_FIND_REGISTRATION_BY_PEN_SERIAL				= "find/reg/penserial/equals";
	public static final String URI_FIND_WARRANTY_AND_HISTORY_BY_PEN_SERIAL		= "registration/history/pen";
	public static final String URI_FIND_REGISTRATION_HISTORY_BY_EMAIL			= "history/email";
	public static final String URI_FIND_REGISTRATION_HISTORY_BY_PEN_SERIAL		= "history/pen";
	public static final String URI_FIND_WARRANTY_EMAIL							= "find/warranty/email";
	public static final String URI_FIND_WARRANTY_PEN							= "find/warranty/pen";
	
	public static final String PEN_DISPLAY_ID_NON_EXISTENT	= "AYE-ASX-XXX-UY";

	public static final String XML_LOADED_REGISTRATION_EMAIL_1				= "lester02@ls.com";
	public static final String XML_LOADED_REGISTRATION_EMAIL_2				= "lester03@ls.com";
	
	public static final String XML_LOADED_REGISTRATION_HISTORY_EMAIL_1		= "lester02@ls.com";
	public static final String XML_LOADED_REGISTRATION_HISTORY_PEN_SERIAL_1	= "2594172882907";
	public static final String XML_LOADED_REGISTRATION_HISTORY_DISPLAY_ID_1	= "AYE-ASW-AED-HB";
	public static final String XML_LOADED_REGISTRATION_HISTORY_FIRST_NAME_1	= "Lester";
	public static final String XML_LOADED_REGISTRATION_HISTORY_LAST_NAME_1	= "the Tester #2";
	public static final String XML_LOADED_REGISTRATION_HISTORY_PEN_NAME_1	= "Lester's Test Pen #2";
	public static final String XML_LOADED_REGISTRATION_HISTORY_LOCALE_1		= "en_US";
	public static final String XML_LOADED_REGISTRATION_HISTORY_COUNTRY_1	= "Somewhere else on the Internet.";
	public static final boolean XML_LOADED_REGISTRATION_HISTORY_OPTIN_1		= false;
	public static final String XML_LOADED_REGISTRATION_HISTORY_REGISTRATION_DATE	= "2013-10-21 17:32:57";
	
	public static final String XML_LOADED_WARRANTY_EMAIL_1		= "lester02@ls.com";
	public static final String XML_LOADED_WARRANTY_PEN_SERIAL_1	= "2594171696607";
	public static final String XML_LOADED_WARRANTY_DISPLAY_ID_1	= "AYE-ARE-CA3-TP";
	public static final String XML_LOADED_WARRANTY_FIRST_NAME_1	= "Lester";
	public static final String XML_LOADED_WARRANTY_LAST_NAME_1	= "the Tester #2";
	public static final String XML_LOADED_WARRANTY_PEN_NAME_1	= "Lester's Test Pen #2";
	public static final String XML_LOADED_WARRANTY_LOCALE_1		= "en_US";
}

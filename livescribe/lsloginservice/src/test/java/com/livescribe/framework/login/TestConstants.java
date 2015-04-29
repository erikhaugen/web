/**
 * Created:  Nov 12, 2013 5:30:56 PM
 */
package com.livescribe.framework.login;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface TestConstants {

	//	These values pertain to the xml-loaded user:  'samson@livescribe.com' user_id = 1
	public static String XML_LOADED_EMAIL_1			= "samson@livescribe.com";
	public static String XML_LOADED_PASSWORD_1		= "OGFBRQQLe_f5MBESOWJQcnQxacQ";	//	letmein
	public static String XML_LOADED_USER_UID_1		= "8f694533-f666-45c2-a6ab-7daf0252db0a";
	public static String XML_LOADED_LOGIN_TOKEN_1	= "95d12b4d-ffe2-4fed-b715-ba5821f27e33";
	public static String MOCK_LOGIN_TOKEN_1			= "e58373ff-90ea-4c54-bf9f-dff23a876709";
	
	//	These values pertain to the xml-loaded user:  'jackstr@livescribe.com' user_id = 2
	public static String XML_LOADED_EMAIL_2			= "jackstr@livescribe.com";
	public static String XML_LOADED_PASSWORD_2		= "OGFBRQQLe_f5MBESOWJQcnQxacQ";	//	letmein
	public static String XML_LOADED_USER_UID_2		= "bdcf046a-3aad-41fc-8954-246342a74cd3";
	public static String XML_LOADED_LOGIN_TOKEN_2	= "95d12b4d-ffe2-4fed-b715-ba5821f27e33";
	public static String XML_LOADED_ACCESS_TOKEN_2	= "jackstr-0270.137C3FAFE02.687474703A2F2F6C6F63616C686F73743A383038302F657665726E6F74656F617574682F6C697374416C6C4E6F7465626F6F6B3F616374696F6E3D63616C6C6261636B52657475726E.397734ECE01F8EF64A4ECDED3F4D9553";
	public static String XML_LOADED_EN_USER_ID_2 	= "440556";
	public static String XML_LOADED_EN_USER_NAME_2	= "jackstr";
	public static String XML_LOADED_EN_SHARD_ID_2	= "14";

	//	See 'testLoginWifiUser_Fail_IncorrectPassword()' in LoginClientTest.
	public static String XML_LOADED_EMAIL_1a		= "samson@livescribe.com";
	public static String XML_LOADED_PASSWORD_1a		= "qKL2HltYp4xU1iqs_UDH7H3r8Tc";	//	Incorrect password.
	
	public static String XML_LOADED_EMAIL_3			= "darkstar@yahoo.com";
	public static String XML_LOADED_USER_UID_3		= "ff137f69-b6f5-4aa7-a4b5-e7238d2410e8";
}

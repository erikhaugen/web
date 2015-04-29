package com.livescribe.aws.login.util;

public class Utils {
	
	/**
	 * <p>Obfuscating the oauth token for logging purposes.</p>
	 * 
	 * @param oauthToken
	 * @return
	 */
	public static String obfuscateOAuthToken(String oauthToken) {
		if (null == oauthToken) {
			return "";
		}
		
		if (oauthToken.length() <= 5) {
			return oauthToken;
		}
		
		return oauthToken.substring(0, oauthToken.length() - 5) + "xxxxx";
	}
	
}

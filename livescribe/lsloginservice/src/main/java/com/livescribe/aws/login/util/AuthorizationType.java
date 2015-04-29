/**
 * 
 */
package com.livescribe.aws.login.util;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public enum AuthorizationType {
	
	EVERNOTE_OAUTH_ACCESS_TOKEN		("EN"),
	FACEBOOK_OAUTH_ACCESS_TOKEN		("FB");
	
	private String code;
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * <p></p>
	 *
	 * @param code
	 */
	AuthorizationType(String code) {
		this.code = code;
	}
	
	/**
	 * <p>Returns the enumerated object representing the given <code>code</code>.</p>
	 * 
	 * @param code The 2-character code representing the provider.  (e.g.  &quot;EN&quot;)
	 * 
	 * @return the enumerated object.
	 */
	public static AuthorizationType fromString(String code) {
		if (code == null || code.isEmpty()) {
			return null;
		}
		
		for (AuthorizationType authType : AuthorizationType.values()) {
			if (code.equalsIgnoreCase(authType.getCode())) {
				return authType;
			}
		}
		
		return null;
	}
}

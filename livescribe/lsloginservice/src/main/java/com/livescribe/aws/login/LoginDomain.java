/**
 * 
 */
package com.livescribe.aws.login;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public enum LoginDomain {
	
	CORP	("CORP"),
	PEN		("PEN"),
	TABLET	("TABLET"),
	WEB		("WEB");
	
	private String domain;
	
	private LoginDomain(String domain) {
		this.domain = domain;
	}
}

/*
 * Created:  Dec. 8, 2011
 *      By:  kmurdoff
 */
package com.livescribe.aws.login.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class SmtpAuthenticator extends Authenticator {

	private String username;
	private String password;
	
	/**
	 * 
	 */
	public SmtpAuthenticator() {
		super();
	}
	
	/**
	 * @param username
	 * @param password
	 */
	public SmtpAuthenticator(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	/* (non-Javadoc)
	 * @see javax.mail.Authenticator#getPasswordAuthentication()
	 */
	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		
		PasswordAuthentication pa = new PasswordAuthentication(username, password);
		return pa;
	}
}

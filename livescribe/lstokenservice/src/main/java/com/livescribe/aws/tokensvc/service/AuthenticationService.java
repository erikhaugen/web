/*
 * Created:  Nov 11, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import com.livescribe.aws.tokensvc.exception.LoginException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface AuthenticationService {
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param password
	 * 
	 * @return
	 * 
	 * @throws LoginException
	 */
	public String login(String email, String password) throws LoginException;
	
	/**
	 * <p></p>
	 * 
	 * @param loginToken
	 * 
	 * @return
	 */
	public boolean logout(String loginToken);
}

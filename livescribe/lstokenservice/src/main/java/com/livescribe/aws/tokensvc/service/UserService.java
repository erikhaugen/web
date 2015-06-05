/*
 * Created:  Sep 27, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import java.io.IOException;

import com.livescribe.aws.tokensvc.exception.DuplicateEmailAddressException;
import com.livescribe.aws.tokensvc.exception.DuplicateUidException;
import com.livescribe.aws.tokensvc.exception.IncorrectPasswordException;
import com.livescribe.aws.tokensvc.exception.LocaleException;
import com.livescribe.aws.tokensvc.exception.LoginException;
import com.livescribe.framework.exception.LogoutException;
import com.livescribe.aws.tokensvc.exception.UserNotFoundException;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.login.exception.EmailAlreadyTakenException;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.orm.consumer.User;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface UserService {

	/**
	 * <p></p>
	 * 
	 * @param user
	 */
	public void deleteUser(User user);
	
	/**
	 * <p>Looks up the email address in the database.</p>
	 * 
	 * @param email The email address to search for.
	 * 
	 * @return a <code>User</code> object if found; <code>NULL</code> if not found.
	 * 
	 * @throws UserNotFoundException
	 * @throws DuplicateEmailAddressException 
	 */
	public User findByEmail(String email) throws UserNotFoundException, DuplicateEmailAddressException;
	
	/**
	 * <p>Finds a <code>User</code> identified by the given ID.</p>
	 * 
	 * @param id The ID to user.
	 * 
	 * @return a <code>User</code> object if found; <code>NULL</code> if not found.
	 * 
	 * @throws UserNotFoundException
	 */
	public User findUserById(Long id) throws UserNotFoundException;
	
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
	public String login(String email, String password) throws LoginException, ClientException, InvalidParameterException, UserNotFoundException, IncorrectPasswordException;
	
	/**
	 * <p></p>
	 * 
	 * @param user
	 * @param loginDomain
	 * @throws LogoutException 
	 * @throws com.livescribe.framework.exception.LogoutException 
	 */
	public void logout(User user, String loginDomain) throws LogoutException;
	
	/**
	 * <p></p>
	 * 
	 * @param loginToken
	 * @param loginDomain
	 * @return
	 * @throws LoginException
	 */
	public String getLoggedInUserEmail(String loginToken, String loginDomain) 
			throws IOException, InvalidParameterException, UserNotLoggedInException, ClientException;
	
	/**
	 * <p></p>
	 * 
	 * @param loginToken
	 * @return
	 * @throws UserNotLoggedInException
	 */
	public User findUserByLoginToken(String loginToken) throws UserNotLoggedInException;
}

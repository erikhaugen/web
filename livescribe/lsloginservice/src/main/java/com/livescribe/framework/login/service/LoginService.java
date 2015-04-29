/*
 * Created:  Nov 9, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.login.service;

import org.apache.xmlrpc.XmlRpcException;

import com.livescribe.afp.AFPException;
import com.livescribe.aws.login.util.AuthorizationType;
import com.livescribe.framework.dto.UserDTO;
import com.livescribe.framework.exception.DuplicateEmailAddressException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.LSDSClientException;
import com.livescribe.framework.exception.LocaleException;
import com.livescribe.framework.exception.LogoutException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.login.exception.AuthenticationException;
import com.livescribe.framework.login.exception.AuthorizationException;
import com.livescribe.framework.login.exception.EmailAlreadyTakenException;
import com.livescribe.framework.login.exception.IncorrectPasswordException;
import com.livescribe.framework.login.exception.InsufficientPrivilegeException;
import com.livescribe.framework.login.exception.LoginException;
import com.livescribe.framework.login.exception.RegistrationNotFoundException;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.login.service.result.LoginServiceResult;
import com.livescribe.framework.orm.consumer.User;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface LoginService {
	
	/**
	 * <p>Create a new user account in WiFi</p>
	 * 
	 * @param email
	 * @param password
	 * @param locale
	 * @param occupation
	 * @param loginDomain
	 * @param optIn
	 * @param sendDiagnostics
	 * 
	 * @return
	 * 
	 * @throws InvalidParameterException 
	 * @throws EmailAlreadyTakenException 
	 */
	public User createWifiAccount(String email, String password, String locale, String occupation, String loginDomain, Boolean optIn, Boolean sendDiagnostics) throws InvalidParameterException, EmailAlreadyTakenException, LocaleException;
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param password
	 * @param locale
	 * @param occupation
	 * @param loginDomain
	 * @param optIn
	 * @param sendDiagnostics
	 * @param uid
	 * 
	 * @return
	 * 
	 * @throws InvalidParameterException
	 * @throws EmailAlreadyTakenException
	 * @throws LocaleException
	 */
	public User createWifiUser(String email, String password, String locale, String occupation, String loginDomain, Boolean optIn, Boolean sendDiagnostics, String uid)
			throws InvalidParameterException, EmailAlreadyTakenException, LocaleException;
	
	/**
	 * <p>Create a new user account in WiFi and WOApps</p>
	 * 
	 * @param email
	 * @param password
	 * @param locale
	 * @param occupation
	 * @param loginDomain
	 * @param optIn
	 * @param sendDiagnostics
	 * @return
	 * @throws InvalidParameterException
	 * @throws EmailAlreadyTakenException
	 * @throws LocaleException
	 * @throws XmlRpcException
	 * @throws LSDSClientException
	 */
	public LoginServiceResult createAccount(String email, String password, String locale, String occupation, String loginDomain, Boolean optIn, Boolean sendDiagnostics) 
			throws InvalidParameterException, EmailAlreadyTakenException, LocaleException, LoginException, XmlRpcException, LSDSClientException;
	
	/**
	 * <p>Creates a new user account, but <b>does not</b> log the user in.</p>
	 * 
	 * @param email 
	 * @param password 
	 * @param locale 
	 * @param occupation 
	 * @param loginDomain 
	 * @param optIn 
	 * 
	 * @return
	 * @throws EmailAlreadyTakenException 
	 */
	/*public User createUser(String email, String password, String locale, String occupation, String loginDomain, Boolean optIn) throws EmailAlreadyTakenException;*/
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param password
	 * @param wifiUser
	 * @param loginToken
	 */
	public void createFrontBaseAccount(String email, String password, User wifiUser, String loginToken);
	
	/**
	 * <p>Returns whether a user with the given login token is logged in.</p>
	 * 
	 * @param token The login token to lookup.
	 * 
	 * @return <code>true</code> if the user is logged in; <code>false</code> if not.
	 */
	public boolean isLoggedIn(String token, String loginDomain);
	
	/**
	 * <p>Returns whether a user with the given email address is logged in.</p>
	 * 
	 * @param email The email address to use in looking up the <code>User</code>.
	 * 
	 * @return <code>true</code> if the user is logged in; <code>false</code> if not.
	 * 
	 * @throws UserNotFoundException if the given email address is not present in the MySQL <code>user</code> table. 
	 * @throws DuplicateEmailAddressException if the given email address is present in the MySQL <code>user</code> table more than once.
	 */
	public boolean isLoggedInEmail(String email, String loginDomain) throws DuplicateEmailAddressException, UserNotFoundException;
	
	/**
	 * 
	 * @param email
	 * @param password
	 * @param loginDomain
	 * @param loginToken
	 * @return
	 * @throws LoginException
	 * @throws DuplicateEmailAddressException
	 * @throws UserNotFoundException
	 * @throws InvalidParameterException
	 */
	public LoginServiceResult loginWifiUser(String email, String password, String loginDomain, String loginToken) 
			throws LoginException, DuplicateEmailAddressException, UserNotFoundException, InvalidParameterException;
	
	/**
	 * <p>Logs a user, identified by the given email address, into both MySQL and FrontBase.</p>
	 * 
	 * The user is first looked up in MySQL.&nbsp;&nbsp;If the user is found in MySQL,
	 * the <code>Set</code> of <code>Authenticated</code> objects is searched to 
	 * determine if the user is logged into the given domain.&nbsp;&nbsp;If so,
	 * that login token is returned in the result.
	 * 
	 * If the user is found, but not logged in, a new login token is generated.
	 * &nbsp;&nbsp;This new token is, then, sent to LSDS to be used to log the user
	 * into FrontBase. 
	 * 
	 * The login to FrontBase must be successful before logging the user into MySQL.
	 * 
	 * @param email The email address of the user to log in.
	 * @param password The password provided by the user.
	 * @param loginDomain The domain to log the user into.
	 * 
	 * @return
	 * 
	 * @throws LoginException
	 * @throws DuplicateEmailAddressException
	 * @throws UserNotFoundException
	 * @throws InvalidParameterException
	 */
	public LoginServiceResult login(String email, String password, String loginDomain) 
			throws LoginException, DuplicateEmailAddressException, UserNotFoundException, InvalidParameterException, XmlRpcException, LSDSClientException;
	
	public LoginServiceResult loginNewVersion(String email, String password, String loginDomain)
			throws XmlRpcException, LoginException, InvalidParameterException, UserNotFoundException, LSDSClientException, DuplicateEmailAddressException;
	
	/**
	 * <p></p>
	 * 
	 * @param loginToken
	 * @param loginDomain
	 * 
	 * @throws InvalidParameterException
	 */
	public void logout(String loginToken, String loginDomain) throws LogoutException, InvalidParameterException, XmlRpcException;
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @throws UserNotFoundException 
	 */
	public void removeAccount(String email) throws UserNotFoundException;
	
	/**
	 * <p></p>
	 * @param email
	 * @return
	 * @throws DuplicateEmailAddressException
	 * @throws UserNotFoundException
	 */
	public String getUserUID(String email) throws DuplicateEmailAddressException, UserNotFoundException;
	
	/**
	 * <p>Returns the UID of the user with registered pen identified by the given
	 * display ID.</p>
	 * 
	 * Calls {@link com.livescribe.framework.login.service.LoginService#findUserUIDBySerialNumber(java.lang.String)} 
	 * after converting the given display ID into a serial number.
	 * 
	 * @param penSerial
	 * @return
	 * @throws RegistrationNotFoundException 
	 * @throws AFPException 
	 */
	public String findUserUIDByPenDisplayId(String displayId) throws RegistrationNotFoundException, AFPException;

	/**
	 * <p></p>
	 * 
	 * @param serialNumber
	 * @return
	 * @throws RegistrationNotFoundException
	 */
	public String findUserUIDBySerialNumber(String serialNumber) throws RegistrationNotFoundException;
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param password
	 * 
	 * @return
	 * 
	 * @throws UserNotFoundException
	 * @throws XmlRpcException
	 */
	public boolean doesUserExistInLSDS(String email, String password) throws UserNotFoundException, XmlRpcException;
	
	/**
	 * <p>Finds a <code>User</Code> instnace from the consumer database by the given uid and returns a respective DTO object.</p>
	 * 
	 * @param uid uid of the user
	 * @return UserDTO dto of the found user
	 * @throws UserNotFoundException if no user found in the database by given uid.
	 */
	public UserDTO findUserByUId(String uid) throws UserNotFoundException;

	/**
	 * <p></p>
	 * 
	 * @param email
	 * 
	 * @return
	 * 
	 * @throws DuplicateEmailAddressException
	 * @throws UserNotFoundException
	 */
	public User findUserWithEmail(String email) throws DuplicateEmailAddressException, UserNotFoundException;
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 * @throws UserNotFoundException 
	 * @throws AuthenticationException 
	 */
	public boolean changePassword(String email, String oldPassword, String newPassword) throws UserNotFoundException, AuthenticationException;
	
	/**
	 * <p>Returns an OAuth access token for the user identified by the given 
	 * email address.</p>
	 * 
	 * @param email The user&apos;s email address.
	 * @param authType The provider of the access token.  Supported providers
	 * are <code>EN</code> (Evernote) and <code>FB</code> (Facebook).
	 * 
	 * @return an OAuth access token string
	 * 
	 * @throws InvalidParameterException
	 * @throws UserNotFoundException
	 * @throws AuthorizationException
	 */
	public String getAuthorizationToken(String email, AuthorizationType authType) throws InvalidParameterException, UserNotFoundException, AuthorizationException;
	
	/**
	 * <p></p>
	 * @param loginToken
	 * @param loginDomain
	 * @return
	 */
	public User getUserInfoByLoginToken(String loginToken, String loginDomain);
	
	/**
	 * <p>Determines if the given email address already exists in the database.</p>
	 * 
	 * @param email The email address to lookup.
	 * 
	 * @return <code>true</code> if the email exists in the database; <code>false</code> if not.
	 */
	public boolean isEmailAddressTaken(String email);
	
	/**
	 * <p>Returns a <code>Country</code> identified by the given locale.</p>
	 * 
	 * @param locale The locale to use when searching for the <code>Country</code>.
	 * 
	 * @return a <code>Country</code> object, or <code>null</code> if one could
	 * not be found.
	 * 
	 * @throws LocaleException if the given locale is not syntactically correct.  
	 * (e.g.  en-US)
	 */
	/*public Country lookupCountry(String locale) throws LocaleException;*/
	

	/**
	 * <p>Change user email in both WiFi and WOApps worlds</p>
	 * 
	 * 
	 * @param loginToken
	 * @param currentEmail
	 * @param newEmail
	 * @return
	 * @throws UserNotLoggedInException
	 * @throws EmailAlreadyTakenException
	 * @throws DuplicateEmailAddressException
	 * @throws UserNotFoundException
	 * @throws XmlRpcException
	 * @throws InvalidParameterException
	 * @throws LSDSClientException
	 */
	public User changeUserEmail(String loginToken, String currentEmail, String newEmail) 
			throws UserNotLoggedInException, EmailAlreadyTakenException, DuplicateEmailAddressException, UserNotFoundException, XmlRpcException, InvalidParameterException, LSDSClientException;

	/**
	 * <p>Change user email in both WiFi and WOApps worlds</p>
	 * 
	 * @deprecated as of LSLogin 1.4. This is replaced by {@link #changeUserEmail(String, String, String)} 
	 * 
	 * @param currentEmail
	 * @param newEmail
	 * @return
	 * @throws UserNotLoggedInException
	 * @throws EmailAlreadyTakenException
	 * @throws DuplicateEmailAddressException
	 * @throws UserNotFoundException
	 * @throws XmlRpcException
	 * @throws InvalidParameterException
	 * @throws LSDSClientException
	 */
	@Deprecated
	public User changeUserEmail(String currentEmail, String newEmail) 
			throws UserNotLoggedInException, EmailAlreadyTakenException, DuplicateEmailAddressException, UserNotFoundException, XmlRpcException, InvalidParameterException, LSDSClientException;
	
	/**
	 * <p>Change user password in both WiFi and WOApps worlds</p>
	 * 
	 * @param email
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 * @throws UserNotFoundException
	 * @throws DuplicateEmailAddressException
	 * @throws AuthenticationException
	 * @throws IncorrectPasswordException
	 * @throws XmlRpcException
	 * @throws InvalidParameterException
	 */
	public User changeUserPassword(String email, String oldPassword, String newPassword) 
			throws UserNotFoundException, DuplicateEmailAddressException, AuthenticationException, IncorrectPasswordException, XmlRpcException, InvalidParameterException;
	
	/**
	 * <p>Change user password in both WiFi and WOApps worlds</p>
	 * 
	 * @param loginToken
	 * @param email
	 * @param newPassword
	 * @return
	 * @throws UserNotLoggedInException
	 * @throws UserNotFoundException
	 * @throws IncorrectPasswordException
	 * @throws XmlRpcException
	 * @throws InvalidParameterException
	 * @throws AuthenticationException
	 */
	public User changeUserPasswordWithLoginToken(String loginToken, String email, String newPassword) 
			throws UserNotLoggedInException, UserNotFoundException, XmlRpcException, InvalidParameterException, AuthenticationException;
	
	/**
	 * <p>Change user password by a logged-in Support User (who has UserManager role)</p>
	 * 
	 * @param supportUserLoginToken
	 * @param userEmail
	 * @param userNewPassword
	 * @return
	 * @throws UserNotLoggedInException
	 * @throws InsufficientPrivilegeException
	 * @throws DuplicateEmailAddressException
	 * @throws UserNotFoundException
	 * @throws AuthenticationException
	 * @throws XmlRpcException
	 * @throws InvalidParameterException
	 */
	public User changeUserPasswordForSupportUser(String supportUserLoginToken, String userEmail, String userPassword) 
			throws UserNotLoggedInException, InsufficientPrivilegeException, DuplicateEmailAddressException, UserNotFoundException, AuthenticationException, XmlRpcException, InvalidParameterException;
	
	/**
	 * <p>Change user password in WiFi and WOApps worlds</p>
	 * 
	 * @param email
	 * @param newPassword
	 * @return
	 * @throws UserNotFoundException
	 * @throws DuplicateEmailAddressException
	 * @throws XmlRpcException
	 * @throws AuthenticationException
	 * @throws InvalidParameterException
	 */
	public User changePasswordWithoutUserTokenAndOldPassword(String email, String newPassword) 
			throws UserNotFoundException, DuplicateEmailAddressException, XmlRpcException, AuthenticationException, InvalidParameterException;
		
	/**
	 * <p>subscribes a customer with active token</p>
	 * @param token
	 * @return
	 * @throws XmlRpcException
	 * @throws AuthenticationException
	 * @throws InvalidParameterException
	 * @throws UserNotLoggedInException
	 */
	public boolean subscribeCustomer(String token)
			throws XmlRpcException, AuthenticationException, InvalidParameterException, UserNotLoggedInException;
	

	/**
	 * <p>unsubscribes a customer with active token</p>
	 * @param token
	 * @return
	 * @throws XmlRpcException
	 * @throws AuthenticationException
	 * @throws InvalidParameterException
	 * @throws UserNotLoggedInException
	 */
	public boolean unsubscribeCustomer(String token)
			throws XmlRpcException, AuthenticationException, InvalidParameterException, UserNotLoggedInException;
	
	/**
	 * <p>checks sendmail status with active token</p>
	 * @param token
	 * @return
	 * @throws XmlRpcException
	 * @throws AuthenticationException
	 * @throws InvalidParameterException
	 * @throws UserNotLoggedInException
	 */
	public boolean isCustomerSubscribed(String token)
			throws XmlRpcException, AuthenticationException, InvalidParameterException, UserNotLoggedInException;
	
	/**
	 * <p>subscribe a guest</p>
	 * @param email
	 * @return
	 * @throws XmlRpcException
	 * @throws AuthenticationException
	 * @throws InvalidParameterException
	 * @throws UserNotLoggedInException
	 */
	public boolean subscribeGuest(String email, String name, String country)
			throws XmlRpcException, AuthenticationException, InvalidParameterException;
	
	/**
	 * <p>unsubscribe a guest</p>
	 * @param email
	 * @return
	 * @throws XmlRpcException
	 * @throws AuthenticationException
	 * @throws InvalidParameterException
	 * @throws UserNotLoggedInException
	 */
	public boolean unsubscribeGuest(String email)
			throws XmlRpcException, AuthenticationException, InvalidParameterException;

    /**
     * <p>generate URL with PTA String used for RightNow</p>
     * @param token
     * @return
     * @throws AuthenticationException
     * @throws InvalidParameterException
     * @throws UserNotLoggedInException
     * @throws UserNotFoundException
     */
    public String composePTAUrl(String token)
            throws AuthenticationException, InvalidParameterException, UserNotLoggedInException, UserNotFoundException;
}

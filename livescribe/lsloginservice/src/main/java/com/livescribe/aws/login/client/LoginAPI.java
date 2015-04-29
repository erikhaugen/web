/**
 * 
 */
package com.livescribe.aws.login.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

import com.livescribe.aws.login.dto.AuthorizationDto;
import com.livescribe.aws.login.response.AuthorizationListResponse;
import com.livescribe.aws.login.response.AuthorizationResponse;
import com.livescribe.aws.login.response.UserInfoResponse;
import com.livescribe.aws.login.util.AuthorizationType;
import com.livescribe.framework.exception.AuthorizationNotFoundException;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.DuplicateEmailAddressException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.LocaleException;
import com.livescribe.framework.exception.LogoutException;
import com.livescribe.framework.exception.ServerException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.login.exception.AuthorizationException;
import com.livescribe.framework.login.exception.EmailAlreadyTakenException;
import com.livescribe.framework.login.exception.IncorrectPasswordException;
import com.livescribe.framework.login.exception.InsufficientPrivilegeException;
import com.livescribe.framework.login.exception.LoginException;
import com.livescribe.framework.login.exception.RegistrationNotFoundException;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.login.response.LoginResponse;
import com.livescribe.framework.web.response.ServiceResponse;

/**
 * <p>The API for all authentication and user account creation activities.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface LoginAPI {

	/**
	 * <p>Creates a new user account AND logs the user in.</p>
	 * 
	 * @param email The user&apos;s primary email address.
	 * @param password The user&apos;s secret password.
	 * @param locale (Optional) The language/country codes in the form:  < lang > - < country >  (e.g.  <code>en-US</code>)
	 * @param occupation The user-selected occupation from the Web page.
	 * @param caller The "source" of this request.  Can be one of <code>WEB</code>, <code>EN</code>, <code>ML</code>, or <code>TEST</code>.
	 * @param loginToken
	 * @param optIn <code>true</code>, if the user has selected to receive email from Livescribe; <code>false</code>, if not.
	 * @param sendDiagnostics <code>true</code>
	 * 
	 * @return a login token <code>String</code> for use in future authentication.
	 * 
	 * @throws InvalidParameterException if any of the parameters are missing or empty.
	 * @throws IOException 
	 * @throws LoginException 
	 * @throws URISyntaxException 
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public LoginResponse createWifiAccount(String email, String password, String locale, String occupation, String caller, String loginToken, String optin, String sendDiagnostics) 
			throws InvalidParameterException, EmailAlreadyTakenException, LocaleException, LoginException, DuplicateEmailAddressException, UserNotFoundException, ClientException;
	/**
	 * <p></p>
	 * 
	 * @param email The user&apos;s primary email address.
	 * @param password The user&apos;s secret password.
	 * @param locale (Optional) The language/country codes in the form:  < lang > - < country >  (e.g.  <code>en-US</code>)
	 * @param occupation The user-selected occupation from the Web page.
	 * @param caller The "source" of this request.  Can be one of <code>WEB</code>, <code>EN</code>, <code>ML</code>, or <code>TEST</code>.
	 * @param optIn <code>true</code>, if the user has selected to receive email from Livescribe; <code>false</code>, if not.
	 * @param sendDiagnostics <code>true</code>
	 * 
	 * @return The <code>uid</code> of the user that was created.
	 * 
	 * @throws InvalidParameterException if any of the parameters are missing or empty.
	 * @throws IOException if an error occurred when sending the HTTP request or handling the response.
	 * @throws ClientException when there is an error in the syntax of the HTTP request.
	 * @throws EmailAlreadyTakenException if the given email address is already present in the database.
	 */
	public String createUser(String email, String password, String locale, String occupation, String caller, String optin, String sendDiagnostics) throws InvalidParameterException, IOException, ClientException, EmailAlreadyTakenException;
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param password
	 * @param locale
	 * @param occupation
	 * @param caller
	 * @param optin
	 * @param sendDiagnostics
	 * 
	 * @return
	 * 
	 * @throws InvalidParameterException
	 * @throws EmailAlreadyTakenException
	 * @throws LocaleException
	 * @throws LoginException
	 * @throws DuplicateEmailAddressException
	 * @throws UserNotFoundException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public LoginResponse createAccount(String email, 
			String password, 
			String locale, 
			String occupation, 
			String caller, 
			String optin, 
			String sendDiagnostics)
			throws InvalidParameterException, EmailAlreadyTakenException, LocaleException, 
			LoginException, DuplicateEmailAddressException, UserNotFoundException, ClientException;
			
	/**
	 * <p>Checks if the given login token is still valid.</p>
	 * 
	 * @param loginToken The login token to search for.
	 * @param caller (Optional) The "source" of this request.  Can be one of 
	 * <code>WEB</code> (Web site), <code>EN</code> (Evernote), 
	 * <code>ML</code> (Market Live), <code>LD</code>, or <code>TEST</code> (Testing).
	 * 
	 * @return <code>true</code>, if the given token is still valid; 
	 * <code>false</code> if not.
	 * 
	 * @throws InvalidParameterException if any of the parameters are missing or empty.
	 * @throws IOException 
	 * @throws ServerException 
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public boolean isLoggedIn(String loginToken, String caller) throws InvalidParameterException, IOException, ServerException, ClientException;
	
	/**
	 * <p>Logs the user, identified by the given email address, into the system.</p>
	 * 
	 * @param email The user&apos;s primary email address.
	 * @param password The user&apos;s secret password.
	 * @param caller The "source" of this request.  Can be one of <code>WEB</code>, <code>EN</code>, <code>ML</code>, or <code>TEST</code>.
	 * 
	 * @return a login token <code>String</code> for use in future authentication.
	 * 
	 * @throws IOException
	 * @throws InvalidParameterException if any of the parameters are missing or empty.
	 * @throws LoginException 
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public LoginResponse login(String email, String password, String caller) 
			throws IncorrectPasswordException, LoginException, DuplicateEmailAddressException, InvalidParameterException, UserNotFoundException, ClientException;
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param password
	 * @param loginDomain
	 * @param loginToken
	 * 
	 * @return
	 * 
	 * @throws IncorrectPasswordException
	 * @throws LoginException
	 * @throws DuplicateEmailAddressException
	 * @throws InvalidParameterException
	 * @throws UserNotFoundException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public String loginWifiUser(String email, String password, String loginDomain, String loginToken) 
			throws IOException, IncorrectPasswordException, LoginException, DuplicateEmailAddressException, InvalidParameterException, UserNotFoundException, ClientException;
	
	/**
	 * <p>Call /loginWifiUser.xml service to log the user in WiFi system</p>
	 * 
	 * @param email
	 * @param password
	 * @param loginDomain
	 * @param loginToken
	 * 
	 * @return LoginResponse object which contains responseCode, loginToken and uid
	 * 
	 * @throws IOException
	 * @throws InvalidParameterException
	 * @throws LoginException
	 * @throws ClientException
	 * @throws UserNotFoundException
	 */
	public LoginResponse loginWifiUserWithLoginToken(String email, String password, String loginDomain, String loginToken) 
			throws IOException, InvalidParameterException, LoginException, ClientException, UserNotFoundException;
	
	/**
	 * <p></p>
	 * 
	 * @param loginToken 
	 * @param caller The "source" of this request.  Can be one of <code>WEB</code>, <code>EN</code>, <code>ML</code>, or <code>TEST</code>.
	 * 
	 * @throws InvalidParameterException if any of the parameters are missing or empty.
	 * @throws IOException 
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public void logout(String loginToken, String caller) throws InvalidParameterException, LogoutException, IOException, ClientException;
	
	/**
	 * <p>Returns the email address for the user logged in with the given login token.</p>
	 * 
	 * @param loginToken
	 * @param loginDomain
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * @throws InvalidParameterException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public String getLoggedInUserEmail(String loginToken, String loginDomain) throws IOException, InvalidParameterException, UserNotLoggedInException, ClientException;
	
	/**
	 * <p>Returns an authorization string or token for use in authenticating/authorizing 
	 * with an external third party site.</p>
	 * 
	 * @param email The primary email address of the user for whom the request 
	 * for authorization is being requested.
	 * @param authType The type of authorization being requested.
	 * 
	 * @return a string for use in authentication/authorization.
	 * 
	 * @throws InvalidParameterException if any of the parameters are <code>null</code> or empty.
	 */
//	public String getAuthorizationToken(String email, AuthorizationType authType) throws InvalidParameterException;
	
	/**
	 * <p>Changes user email address in both WiFi and WOApps worlds</p>
	 * 
	 * @param loginToken
	 * @param currentEmail
	 * @param newEmail
	 * @return
	 * @throws InvalidParameterException
	 * @throws UserNotLoggedInException
	 * @throws EmailAlreadyTakenException
	 * @throws DuplicateEmailAddressException
	 * @throws UserNotFoundException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public ServiceResponse changeUserEmail(String loginToken, String currentEmail, String newEmail) 
			throws InvalidParameterException, ClientException, UserNotLoggedInException, EmailAlreadyTakenException, DuplicateEmailAddressException, UserNotFoundException;
	
	/**
	 * <p>Change user password in both WiFi and WOApps worlds</p>
	 * 
	 * @param email
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 * @throws InvalidParameterException
	 * @throws IncorrectPasswordException
	 * @throws DuplicateEmailAddressException
	 * @throws UserNotFoundException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public ServiceResponse changeUserPassword(String email, String oldPassword, String newPassword) 
			throws InvalidParameterException, IncorrectPasswordException, ClientException, DuplicateEmailAddressException, UserNotFoundException;
	
	/**
	 * <p>Change user password in both WiFi and WOApps worlds</p>
	 * 
	 * @param loginToken
	 * @param email
	 * @param newPassword
	 * 
	 * @return
	 * 
	 * @throws InvalidParameterException
	 * @throws UserNotFoundException
	 * @throws UserNotLoggedInException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public ServiceResponse changeUserPasswordWithLoginToken(String loginToken, String email, String newPassword) 
			throws InvalidParameterException, UserNotFoundException, UserNotLoggedInException, ClientException;
	
	/**
	 * <p>Change user password by a logged-in Support User (who has UserManager role)</p>
	 * 
	 * @param supportUserLoginToken
	 * @param userEmail
	 * @param userPassword
	 * @return
	 * @throws InvalidParameterException
	 * @throws UserNotFoundException
	 * @throws UserNotLoggedInException
	 * @throws InsufficientPrivilegeException
	 * @throws DuplicateEmailAddressException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public ServiceResponse changeUserPasswordForSupportUser(String supportUserLoginToken, String userEmail, String userPassword) 
			throws InvalidParameterException, UserNotFoundException, UserNotLoggedInException, InsufficientPrivilegeException, DuplicateEmailAddressException, ClientException;
	
	/**
	 * <p>Change user password in both WiFi and WOApp worlds</p>
	 * 
	 * @param email
	 * @param newPassword
	 * @return
	 * @throws InvalidParameterException
	 * @throws UserNotFoundException
	 * @throws UserNotLoggedInException
	 * @throws InsufficientPrivilegeException
	 * @throws DuplicateEmailAddressException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public ServiceResponse changePasswordWithoutUserTokenAndOldPassword(String email, String newPassword) 
			throws InvalidParameterException, UserNotFoundException, UserNotLoggedInException, InsufficientPrivilegeException, DuplicateEmailAddressException, ClientException;
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * 
	 * @return
	 * 
	 * @throws InvalidParameterException 
	 * @throws ParseException 
	 * @throws UserNotFoundException 
	 * @throws DuplicateEmailAddressException 
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public UserInfoResponse findUserByEmail(String email) throws InvalidParameterException, ParseException, UserNotFoundException, DuplicateEmailAddressException, ClientException;
	
	/**
	 * <p></p>
	 * 
	 * @param uid
	 * 
	 * @return
	 * 
	 * @throws InvalidParameterException 
	 * @throws ParseException 
	 * @throws UserNotFoundException 
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public UserInfoResponse findUserByUId(String uid) throws InvalidParameterException, ParseException, UserNotFoundException, ClientException;
	
	/**
	 * <p>Returns the UID of the user to whom the given pen is registered.</p>
	 * 
	 * @param penDisplayId The 14-character display ID of the pen.
	 * 
	 * @return a user&apos;s UID.
	 * 
	 * @throws InvalidParameterException if a given parameter is invalid.
	 * @throws RegistrationNotFoundException if a registration record could not be found with the given information.
	 * @throws AuthorizationException if authorization was not permited using the given information.
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public String findUserUIDByPenDisplayId(String penDisplayId)
			throws InvalidParameterException, RegistrationNotFoundException, AuthorizationException, ClientException;
	
	/**
	 * <p></p>
	 * 
	 * @param penSerial
	 * @return
	 * @throws InvalidParameterException
	 * @throws RegistrationNotFoundException
	 * @throws AuthorizationException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public String findUserUIDBySerialNumber(String penSerial)
			throws InvalidParameterException, RegistrationNotFoundException, AuthorizationException, ClientException;
	
	/**
	 * <p>Returns the <b>primary</b> authorization by Pen's displayId and the provider type.</p>
	 * 
	 * @param penDisplayId pen's display id
	 * @param authorizationType AuthorizationType
	 * 
	 * @return the primary authorization
	 * 
	 * @throws InvalidParameterException
	 * @throws RegistrationNotFoundException
	 * @throws AuthorizationException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public AuthorizationResponse findAuthorizationByPenDisplayId(String penDisplayId, AuthorizationType authorizationType) 
			throws InvalidParameterException, RegistrationNotFoundException, AuthorizationException, ClientException;
	
	/**
	 * <p>Returns an AuthorizationTokenResponse by finding with Users' UID and AuthorizationType</p>
	 * 
	 * @deprecated With LSShare 2.1, use {@link #findAuthorizationByUIDAndProviderUId(String, String, AuthorizationType)} instead to find the unique
	 * Authorization for the given user for given en username.
	 * 
	 * @param String uid
	 * @param AuthorizationType{@link} authorizationType
	 * 
	 * @return AuthorizationTokenResponse{@link} AuthorizationTokenResponse built from values found
	 * 
	 * @throws InvalidParameterException
	 * @throws AuthorizationException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public AuthorizationResponse findAuthorizationByUID(String uid, AuthorizationType authorizationType)
			throws InvalidParameterException, AuthorizationException, ClientException;
	
	/**
	 * <p>Returns an AuthorizationTokenResponse by finding with Livescribe users' UID and Evernote User's id</p>
	 *  
	 * @param uid Livescribe user's id
	 * @param providerUserId provider user's id
	 * @param AuthorizationType{@link} authorizationType
	 * 
	 * @return AuthorizationTokenResponse{@link} AuthorizationTokenResponse built from values found
	 * 
	 * @throws InvalidParameterException
	 * @throws AuthorizationException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 * @throws UserNotFoundException 
	 * @throws AuthorizationNotFoundException 
	 */
	public AuthorizationResponse findAuthorizationByUIDAndProviderUId(String uid, String providerUserId, AuthorizationType authorizationType)
			throws InvalidParameterException, AuthorizationException, ClientException, UserNotFoundException, AuthorizationNotFoundException;
	
	public List<AuthorizationDto> findAuthorizationsByUid(String uid, AuthorizationType authorizationType) throws ClientException, InvalidParameterException;
	
	/**
	 * <p></p>
	 * 
	 * @param userId
	 * @param authorizationType
	 * 
	 * @return
	 * 
	 * @throws InvalidParameterException
	 * @throws ClientException
	 * @throws AuthorizationException
	 * 
	 * @deprecated Use {@link #findAuthorizationByUIDAndProviderUId(String, String, AuthorizationType)} instead.
	 */
	@Deprecated
	public AuthorizationListResponse findAuthorizationByProviderUserId(String userId, AuthorizationType authorizationType)
			throws InvalidParameterException, ClientException, AuthorizationException;
	
	/**
	 * <p>Method to check if the specified user existed in WiFi system</p>
	 * 
	 * @param email
	 * 
	 * @return
	 * 
	 * @throws InvalidParameterException
	 * @throws ClientException if an error occurs on the client side.&nbsp;&nbsp;This could 
	 * include errors in the transport protocol (HTTP/HTTPS), the request URI syntax, or 
	 * cryptographic errors (bad keys, encryption, etc.).
	 */
	public boolean isUserExisted(String email) throws InvalidParameterException, ClientException;
}

	
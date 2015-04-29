/**
 * 
 */
package com.livescribe.framework.login.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
<<<<<<< .mine
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
=======
import java.util.*;
>>>>>>> .r78830

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.afp.AFPException;
import com.livescribe.afp.PenID;
import com.livescribe.aws.login.lsds.LSDSClient;
import com.livescribe.aws.login.util.AuthorizationType;
import com.livescribe.framework.config.AppConstants;
import com.livescribe.framework.config.AppProperties;
import com.livescribe.framework.dto.UserDTO;
import com.livescribe.framework.exception.DuplicateEmailAddressException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.LSDSClientException;
import com.livescribe.framework.exception.LocaleException;
import com.livescribe.framework.exception.LogoutException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.login.crypto.EncryptionUtils;
import com.livescribe.framework.login.exception.AuthenticationException;
import com.livescribe.framework.login.exception.AuthorizationException;
import com.livescribe.framework.login.exception.EmailAlreadyTakenException;
import com.livescribe.framework.login.exception.IncorrectPasswordException;
import com.livescribe.framework.login.exception.InsufficientPrivilegeException;
import com.livescribe.framework.login.exception.LoginException;
import com.livescribe.framework.login.exception.RegistrationNotFoundException;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.login.service.result.LoginServiceResult;
import com.livescribe.framework.orm.consumer.Authenticated;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.CustomAuthenticatedDao;
import com.livescribe.framework.orm.consumer.CustomAuthorizationDao;
import com.livescribe.framework.orm.consumer.CustomRegisteredDeviceDao;
import com.livescribe.framework.orm.consumer.CustomUserDao;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.consumer.XUserGroup;
import com.livescribe.framework.orm.consumer.XUserRole;

/**
 * <p>Default implementation of a {@link com.livescribe.framework.login.service.LoginService}.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class LoginServiceImpl implements LoginService {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public static final String SERVICE_FULL_NAME	= "Login Service";
	public static final String PASSWORD_PREFIX		= "{SHA}";

	private static String DEFAULT_LOCALE		= "en-US";
	
	@Autowired
	private CustomAuthenticatedDao authenticatedDao;
	
	@Autowired
	private EncryptionUtils encryptionUtils;
	
	@Autowired
	private CustomAuthorizationDao authorizationDao;
	
	@Autowired
	private CustomUserDao userDao;
	
	@Autowired
	private CustomRegisteredDeviceDao registeredDeviceDao;
	
	@Autowired
	private LSDSClient lsdsClient;
	
	@Autowired
	private AppProperties appProperties;
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public LoginServiceImpl() {
		logger.debug("Instantiated.");
	}

	/*
	 * (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#createWifiUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, java.lang.Boolean)
	 */
	@Transactional("consumer")
	public User createWifiAccount(String email, String password, String locale, String occupation, String loginDomain, Boolean optIn, Boolean sendDiagnostics)
			throws InvalidParameterException, EmailAlreadyTakenException, LocaleException {
		
		return createWifiUser(email, password, locale, occupation, loginDomain, optIn, sendDiagnostics, null);

	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#createAccount(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public LoginServiceResult createAccount(String email, String password, String locale, String occupation, String loginDomain, Boolean optIn, Boolean sendDiagnostics) 
			throws InvalidParameterException, EmailAlreadyTakenException, LocaleException, LoginException, XmlRpcException, LSDSClientException {
		
		String method = "createAccount(" + email + "):  ";
		logger.debug(method);
		
		long start = System.currentTimeMillis();
		
		String country = parseCountryFromLocale(locale);
		
		String wifiDomain = loginDomain;
		// if loginDomain is a LS Desktop version format, we save it as "LD" in WiFi world
		if (loginDomain.matches(AppConstants.DESKTOP_VERSION_REGEX)) {
			wifiDomain = "LD";
		}
		
		long userExistStart = System.currentTimeMillis();
		
		//	Check if this email address exists in WOApps world
		boolean isEmailExistedInWOApp = lsdsClient.isUserExisted(email, null);

		//	Print how long the LSDS request took.
		long userExistEnd = System.currentTimeMillis();
		logger.debug("lsdsClient.isUserExisted(" + email + "):  completed in " + (userExistEnd - userExistStart) + " ms");
		
		//	If the email address was found in FrontBase, throw exception.
		if (isEmailExistedInWOApp) {
			throw new EmailAlreadyTakenException("Email '" + email + "' is already existed in WOApp system.");
		}
		
		//	Create wifi user.
		User user = createWifiUser(email, password, locale, occupation, wifiDomain, optIn, sendDiagnostics, null);
		
		//	Log the new user in so we have login_token to pass to LSDS.createAccount()
		LoginServiceResult loginResult = null;
		try {
			loginResult = loginWifiUser(email, password, wifiDomain, null);
			
		}
		catch (DuplicateEmailAddressException e) {
			throw new LoginException(e);
		}
		catch (UserNotFoundException e) {
			throw new LoginException(e);
		}
		catch (HibernateException he) {
			logger.error(method + "HibernateException thrown while attempting to log new user '" + email + "' into MySQL.", he);
		}
		
		if (loginResult == null) {
			throw new LoginException("Cannot get login token.");
		}
		
		// call LSDS to create user in WOApps with loginToken and uid
//		Country country = lookupCountry(locale);
		long lsdsStart = System.currentTimeMillis();
		try {
			lsdsClient.createAccount(email, password, occupation, country, loginResult.getLoginToken(), "WEB", optIn, user.getUid());
			
		} catch (EmailAlreadyTakenException eate) {
			// if user already exists in WOApps database, then do nothing
			logger.info("User '" + email + "' already exists in FrontBase database.");
			
		} catch (InvalidParameterException ipe) {
			throw ipe;
			
		} catch (Exception e) {
			// trying to remove account because the creation could be ok 
			// but a network problem makes a fake failure.
			try {
				boolean ret = lsdsClient.removeUser(email);
				if (!ret) {
					logger.error("Fail to rollback user (" + email + ")");
				}
			} catch (XmlRpcException e1) {
				logger.error("Looks like that it failed to rollback user (" + email + "): " + e1.getMessage(), e1);
			}
			throw new RuntimeException(e);
		}
		
		long lsdsEnd = System.currentTimeMillis();
		logger.debug(method + "lsdsClient.createAccount():  completed in " + (lsdsEnd - lsdsStart) + " ms");
		
		long end = System.currentTimeMillis();
		logger.debug(method + "completed in " + (end - start) + " ms");
		
		return loginResult;
	}
	
	public User createWifiUser(String email, String password, String locale, String occupation, String loginDomain, Boolean optIn, Boolean sendDiagnostics, String uid)
			throws InvalidParameterException, EmailAlreadyTakenException, LocaleException {
		
		String method = "createWifiUser(" + email + "):  ";
		logger.debug(method);
		
		long start = System.currentTimeMillis();
		
		//	Make sure the email address is not already taken in MySQL.
		boolean emailTaken = isEmailAddressTaken(email);
		if (emailTaken) {
			throw new EmailAlreadyTakenException("Email '" + email + "' was found in MySQL database.");
		}
		
		// Check if sendDiagnostics is null, set false as default
		if (sendDiagnostics == null) {
			sendDiagnostics = false;
		}
		
		//	Begin creating the account.
		User user = new User();
		user.setPrimaryEmail(email.toLowerCase());

		String encrypted = null;
		try {
			encrypted = encryptionUtils.generateBase64EncodedSHAHash(password);
		}
		catch (InvalidKeyException ike) {
			logger.warn(method + "InvalidKeyException thrown when creating user account.", ike);
		}
		catch (NoSuchAlgorithmException nsae) {
			logger.warn(method + "NoSuchAlgorithmException thrown when creating user account.", nsae);
		}
		catch (UnsupportedEncodingException uee) {
			logger.warn(method + "UnsupportedEncodingException thrown when creating user account.", uee);
		}
		user.setPassword(encrypted);
		user.setSendDiagnostics(sendDiagnostics);
		user.setConfirmed(false);
		Date confDate = createEpochDate();
		user.setConfirmationDate(confDate);
		user.setEnabled(true);
		user.setLastModifiedBy(SERVICE_FULL_NAME);
		Date created = new Date();
		user.setCreated(created);
		user.setLastModified(created);
		
		// if there uid param is not specified, generate a new one
		if (uid == null || uid.isEmpty()) {
			user.setUid(generateUserUUID());
		} else { // otherwise we use the specifed uid to set to this user
			user.setUid(uid);
		}
		
		try {
			userDao.persist(user);
		}
		catch (HibernateException he) {
			logger.error(method + "HibernateException thrown when attempting to save new WiFi user '" + email + "' in MySQL.", he);
		}
		
		long end = System.currentTimeMillis();
		logger.debug(method + "completed in " + (end - start) + " ms");
		
		return user;
	}
	
	private Date createEpochDate() {
		
		GregorianCalendar cal = new GregorianCalendar(2014, 02, 05);
		return cal.getTime();
	}
	
	public void createFrontBaseAccount(String email, String password, User wifiUser, String loginToken) {
		
		String method = "createFrontBaseAccount():  ";
		
//		User wifiUser = loginResult.getUser();
		try {
			logger.debug(method + "User '" + email + "' does not exist in WOApps.  Calling LSDS to create user in FrontBase.");
			lsdsClient.createAccount(email, password, "NA", "US", loginToken, "WEB", false, wifiUser.getUid());
			
		} catch (EmailAlreadyTakenException eate) {
			//	This exception is unlikely to happen due to the UserNotFoundException earlier.
		} catch (Exception e) {
			//	Trying to remove account because the creation could be ok 
			//	but a network problem makes a fake failure.
//			try {
//				boolean ret = lsdsClient.removeUser(email);
//				if (!ret) {
//					logger.error("Fail to rollback user (" + email + ")");
//				}
//			} catch (XmlRpcException e1) {
//				logger.error("Looks like that it failed to rollback user (" + email + "): " + e1.getMessage(), e1);
//			}
//			throw new RuntimeException(e);
		}
	}
	
	/**
	 * <p>Determine if the given email address is already being used in MySQL.</p>
	 * 
	 * @return
	 */
	@Transactional("consumer")
	public boolean isEmailAddressTaken(String email) {
		
		User user = null;
		try {
			user = userDao.findByEmail(email);
		}
		//	This is thrown if there was MORE than 1 record with the same
		//	email address, for some reason.
		catch (DuplicateEmailAddressException deae) {
			return true;
		}
		
		//	There might've been ONLY 1 record ...
		if (user != null) {
			return true;
		}
		
		return false;
	}

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
	/*
	@Transactional("consumer")
	public Country lookupCountry(String locale) throws LocaleException {
		
		logger.debug("lookupCountry():  Looking up Country with '" + locale + "'");
		
		//	Parse the locale String.
		String[] parts = locale.split("-");
		if (parts.length != 2) {
			String msg = "Locale '" + locale + "' was not in a valid format.";
			throw new LocaleException(msg);
		}
		Country example = new Country();
		example.setIsoCode2(parts[1].toUpperCase());
		List<Country> list = countryDao.findByExample(example);
		
		if ((list == null) || (list.isEmpty())) {
			String msg = "No Country found using locale [" + locale + "].";
			throw new LocaleException(msg);
		}
		return list.get(0);
	}
	*/
	
	/**
	 * <p>Generates a login token with the given email address.</p>
	 * 
	 * @param email The email address to use.
	 * 
	 * @return the login token.
	 */
	private String generateLoginToken(String email) {
		
		logger.debug("Generating login token.");
		UUID uuid = UUID.randomUUID();
		String loginToken = uuid.toString();
		
		return loginToken;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#getAuthorizationToken(java.lang.String, com.livescribe.aws.login.util.AuthorizationType)
	 */
	public String getAuthorizationToken(String email, AuthorizationType authType) throws InvalidParameterException, UserNotFoundException, AuthorizationException {
		
		String method = "getAuthorizationToken():  ";
		
		if ((email == null) || ("".equals(email))) {
			String msg = method + "The given email parameter was either 'null' or empty.";
			throw new InvalidParameterException(msg);
		}
		if (authType == null) {
			String msg = method + "The given 'authType' parameter was 'null'.";
			throw new InvalidParameterException(msg);
		}
		
		//	Look the user up.
		User user = null;
		
		try {
			user = userDao.findByEmail(email);
		}
		catch (DuplicateEmailAddressException deae) {
			String msg = "Duplicate records found for user with email address '" + email + "'.";
			logger.error(method + msg, deae);
		}
		catch (HibernateException he) {
			logger.error("getAuthorizationToken():  HibernateException thrown while attempting to find user '" + email + "'.", he);
		}
		
		if (user == null) {
			String msg = "User, with email address '" + email + "', was not found.";
			logger.error(method + msg);
			throw new UserNotFoundException(msg);
		}
		
		//	Check if there are any authorizations present.
		Set<Authorization> authList = user.getAuthorizations();
		if ((authList == null) || (authList.isEmpty())) {
			String msg = "No authorizations were found for user with email '" + email + "'.";
			logger.info(method + msg);
			throw new AuthorizationException(msg);
		}

		//	Find the requested token.
		String code = authType.getCode();
		String token = null;
		Iterator<Authorization> authIter = authList.iterator();
		while (authIter.hasNext()) {
			Authorization auth = authIter.next();
			if (code.equals(auth.getProvider())) {
				token = auth.getOauthAccessToken();
				break;
			}
		}
		
		return token;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#login(java.lang.String, java.lang.String)
	 */
	@Transactional("consumer")
	public LoginServiceResult loginWifiUser(String email, String password, String loginDomain, String loginToken) 
			throws LoginException, DuplicateEmailAddressException, UserNotFoundException, InvalidParameterException {
		
		String method = "loginWifiUser(" + email + "):  ";
		logger.debug(method);
		
		long start = System.currentTimeMillis();
		
		//	Verify all parameters have been given.
		if ((email == null) || ("".equals(email))) {
			String msg = "The given email parameter was either 'null' or empty.";
			throw new InvalidParameterException(msg);
		}
		if ((password == null) || ("".equals(password))) {
			String msg = "The given password parameter was either 'null' or empty.";
			throw new InvalidParameterException(msg);
		}
		if ((loginDomain == null) || ("".equals(loginDomain))) {
			String msg = "The given login domain parameter was either 'null' or empty.";
			throw new InvalidParameterException(msg);
		}
		
		//	If 'loginDomain' is an LS Desktop version, save it as "LD" in the WiFi world.
		if (loginDomain.matches(AppConstants.DESKTOP_VERSION_REGEX)) {
			loginDomain = "LD";
		}
		
		//	= = = = = = = = = = = = = = = = = = = = = = = = = = = 
		//			Find the User in MySQL.
		//	= = = = = = = = = = = = = = = = = = = = = = = = = = = 
		User user = findUserWithEmail(email);
		
		//	= = = = = = = = = = = = = = = = = = = = = = = = = = = 
		//			Compare passwords.
		//	= = = = = = = = = = = = = = = = = = = = = = = = = = = 		
		validatePassword(password, user);
		
		//	Avoid Hibernate lazy-loading by forcing the loading of Collections.
		//	This ensures the object graph is loaded prior to returning it outside
		//	of the Hibernate Session.
		Hibernate.initialize(user);

		//	Find any logins for that User.
		Set<Authenticated> authentications = user.getAuthenticateds();
		
//		List<Authenticated> authentications = null;
//		try {
//			authentications = authenticatedDao.findByUserId(user.getUserId());
//		} catch (HibernateException he) {
//			logger.error(method + "HibernateException thrown when attempting to find authorizations by user ID [" + user.getUserId() + "].", he);
//		}
		
		LoginServiceResult result = new LoginServiceResult();
		result.setUser(user);
		
		//	If the user is already logged into the given domain, 
		//	return that login token.
		if (authentications != null) {
			for (Authenticated login : authentications) {
				String domain = login.getLoginDomain();
				if (loginDomain.equals(domain)) {
					//	If loginToken is specified, override with the new loginToken
					if (loginToken != null && !loginToken.isEmpty()) {
						logger.debug("Saving 'authenticated' record for '" + email + "'.");
						login.setLoginToken(loginToken);
						authenticatedDao.persist(login);
					}
					
					result.setLoginToken(login.getLoginToken());
					
					long end = System.currentTimeMillis();
					logger.debug("loginWifiUser():  completed in " + (end - start) + " ms");
					
					return result;
				}
			}
		}

		//	... otherwise, create a new login token.
		
		//	Generate the login token if it's not specified in the param
		if (loginToken == null || loginToken.isEmpty()) {
			loginToken = generateLoginToken(email.toLowerCase());
		}
		
		// if loginToken is still null, throw exception
		if (loginToken == null) {
			String msg = "The login token was not generated!";
			throw new LoginException(msg);
		}
		
		saveLogin(loginDomain, loginToken, user);
		
		result.setLoginToken(loginToken);
		
		long end = System.currentTimeMillis();
		logger.debug(method + "completed in " + (end - start) + " ms");
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#login(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional("consumer")
	public LoginServiceResult loginNewVersion(String email, String password, String loginDomain) 
			throws XmlRpcException, LoginException, InvalidParameterException, UserNotFoundException, LSDSClientException, DuplicateEmailAddressException {
		
		String method = "loginNewVersion(" + email + "):  ";
		logger.debug(method);
		
		long start = System.currentTimeMillis();
		
		LoginServiceResult loginResult = null;
				
		//	= = = = = = = = = = = = = = = = = = = = = = = = = = = 
		//			Find the User in MySQL.
		//	= = = = = = = = = = = = = = = = = = = = = = = = = = = 
		User user = null;
//		try {
			user = findUserWithEmail(email);
//		} catch (UserNotFoundException unfe) {
//			logger.info(method, unfe);
//		}
		
		//	If the user was not found in MySQL, look in FrontBase.
		boolean woUserExists = false;
		if (user == null) {
			woUserExists = lsdsClient.isUserExisted(email, null);
			logger.debug(method + "User '" + email + "' exists in FrontBase:  " + woUserExists);
		}
		

		//	If the user is already logged into MySQL, assume the user is 
		//	also logged into FrontBase and return this login token.
		String mySQLToken = isLoggedIn(user, loginDomain);
		if ((mySQLToken != null) && (!"".equals(mySQLToken))) {
			logger.debug(method + "User '" + email + "' was already logged in.  Returning existing login token.");
			loginResult = new LoginServiceResult();
			loginResult.setUser(user);
			loginResult.setLoginToken(mySQLToken);
			return loginResult;
		}
		
		//	Generate the login token to be used in FrontBase and MySQL.
		//	If login To WebObjects/FrontBase succeeds, use this token in
		//	MySQL afterwards.
		String loginToken = generateLoginToken(email);
		
		//	= = = = = = = = = = = = = = = = = = = = = = = = = = = 
		//		Login via LSDS with the login token generated above.
		//	= = = = = = = = = = = = = = = = = = = = = = = = = = = 
		loginViaLSDS(email, password, loginDomain, loginToken);
		
		//	= = = = = = = = = = = = = = = = = = = = = = = = = = = 
		//			Compare passwords.
		//	= = = = = = = = = = = = = = = = = = = = = = = = = = = 		
		validatePassword(password, user);

		//	= = = = = = = = = = = = = = = = = = = = = = = = = = = 
		//			Save the login to MySQL.
		//	= = = = = = = = = = = = = = = = = = = = = = = = = = = 		
		saveLogin(loginDomain, loginToken, user);
		
		//	TODO:  What happens if FrontBase login succeeds, but MySQL login fails?
		//		   Need to handle this case.
//		logoutViaLSDS();
		
		loginResult = new LoginServiceResult();
		loginResult.setUser(user);
		loginResult.setLoginToken(loginToken);
		
		long end = System.currentTimeMillis();
		logger.debug(method + "completed in " + (end - start) + " ms");

		return loginResult;
	}
	
	/**
	 * <p>Finds the <code>User</code> record in the MySQL <code>user</code> table
	 * with the given email address.</p>
	 * 
	 * @param email The email address to search on.
	 * 
	 * @return a <code>User</code> object.
	 * 
	 * @throws DuplicateEmailAddressException
	 * @throws UserNotFoundException
	 */
	@Transactional("consumer")
	public User findUserWithEmail(String email) throws DuplicateEmailAddressException, UserNotFoundException {
		
		String method = "findUserWithEmail():  ";
		
		User user = null;
		try {
			logger.debug(method + "Finding user in MySQL with email '" + email + "'.");
			user = userDao.findByEmail(email);
		}
		catch (HibernateException he) {
			String msg = "HibernateException thrown while attempting to find user '" + email + "' in MySQL.";
			logger.error(method + msg, he);
			//	TODO:  [KFM]:  Need to throw an exception that is more descriptive than "User Not Found" below.
		}
		
		if (user == null) {
			String msg = method + "User with email '" + email + "' not found in MySQL.";
			throw new UserNotFoundException(msg);
		}
		logger.debug(method + "Found user '" + email + "' with UID '" + user.getUid() + "' in MySQL.");

		return user;
	}

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
	public boolean doesUserExistInLSDS(String email, String password) throws UserNotFoundException, XmlRpcException {
		
		String method = "doesUserExistInLSDS():  ";
		
		String fbUserShortId = lsdsClient.getExistingUserShortId(email, null);
		boolean isUserExistedInWOApps = (fbUserShortId == null || fbUserShortId.isEmpty()) ? false : true;
		boolean isLoggedInPasswordCorrect = false;
		
		//if the user exists in WOApps world, check if login credential is valid
		if (isUserExistedInWOApps) {
			isLoggedInPasswordCorrect = lsdsClient.isUserExisted(email, password);
			return isLoggedInPasswordCorrect;
		} else {
			// if user does not exist in WOApp, throw exception
			logger.error(method + "User '" + email + "' not found in both WiFi and WOApps worlds");
			throw new UserNotFoundException();
		}
	}
	
	/**
	 * <p>Logs a user, identified by the given email address, into LSDS.</p>
	 * 
	 * Verifies the login token returned by LSDS matches the login token provided.
	 * 
	 * @param email The email address of the user being logged in.
	 * @param password The password provided by the user.
	 * @param loginDomain The domain the user is being logged into.
	 * @param loginToken a login token generated 
	 * 
	 * @throws XmlRpcException
	 * @throws InvalidParameterException
	 * @throws UserNotFoundException
	 * @throws LSDSClientException
	 * @throws LoginException
	 */
	private void loginViaLSDS(String email, String password, String loginDomain, String loginToken)
			throws XmlRpcException, InvalidParameterException, UserNotFoundException, LSDSClientException, LoginException {
		
		String method = "loginViaLSDS(" + email + "):  ";
		logger.debug(method);
		
		logger.debug(method + "Logging user '" + email + "' into LSDS.");
		
		HashMap<String, Object> resultMap = lsdsClient.login(email, password, loginToken, loginDomain);
		
		if (resultMap == null) {
			String msg = "Received a 'null' result Map from LSDS.";
			logger.debug(method + msg);
			throw new LoginException(msg);
		}
		
		//	If no login token is returned by LSDS, throw an exception.
		String woLoginToken = (String)resultMap.get("token");
		if ((woLoginToken == null) || ("".equals(woLoginToken))) {
			String msg = "The login token returned by LSDS was 'null'.";
			logger.error(method + msg);
			throw new LoginException(msg);
		}
		
		//	If the login token returned by LSDS does not match the one that 
		//	was sent, throw an exception.
		if (!loginToken.equals(woLoginToken)) {
			String msg = "The login token returned by LSDS (" + woLoginToken + ") did not match the login token that was generated (" + loginToken + ").";
			logger.error(method + msg);
			throw new LoginException(msg);
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#login(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public LoginServiceResult login(String email, String password, String loginDomain) 
			throws LoginException, DuplicateEmailAddressException, UserNotFoundException, InvalidParameterException, XmlRpcException, LSDSClientException {
		
		String method = "login(" + email + "):  ";
		logger.debug(method);
		
		long start = System.currentTimeMillis();
		
		LoginServiceResult loginResult = null;
		
		String wifiDomain = loginDomain;
		
		//	If 'loginDomain' is an LS Desktop version, save it as "LD" in the WiFi world.
		if (loginDomain.matches(AppConstants.DESKTOP_VERSION_REGEX)) {
			wifiDomain = "LD";
		}
		
		//	Log user into WiFi world.
		try {
			loginResult = loginWifiUser(email, password, wifiDomain, null);
			
		} catch (UserNotFoundException unfe) {
			//	If this user does not exist in WiFi world,
			//	then try to find the user in the WOApp world
			logger.debug(method + "User '" + email + "' not found in WiFi db. Preparing to talk to LSDS...");
			String fbUserShortId = lsdsClient.getExistingUserShortId(email, null);
			boolean isUserExistedInWOApps = (fbUserShortId == null || fbUserShortId.isEmpty()) ? false : true;
			boolean isLoggedInPasswordCorrect = false;
			
			//if the user exists in WOApps world, check if login credential is valid
			if (isUserExistedInWOApps) {
				isLoggedInPasswordCorrect = lsdsClient.isUserExisted(email, password);
				
			} else {
				// if user does not exist in WOApp, throw exception
				logger.error(method + "User '" + email + "' not found in both WiFi and WOApps worlds");
				throw unfe;
			}
			
			// if user exists in WOApp and login credential is valid in WOApp, create this user in WiFi world
			if (isLoggedInPasswordCorrect) {
				logger.debug(method + "User '" + email + "' exists in WOApps and has provided correct password. Go on and create this user in WiFi.");
				try {
					// create Wifi user with the uid retrieved from WOApps world
					createWifiUser(email, password, DEFAULT_LOCALE, "NA", wifiDomain, false, false, fbUserShortId);
					
					// log this user in wifi world
					loginResult = loginWifiUser(email, password, wifiDomain, null);
					
				} catch (EmailAlreadyTakenException e) {
					// this exception is unlikely to happen due to the UserNotFoundException above
					
				} catch (LocaleException e) {
					// unlikely to happen since we're using hard-coded default locale
				}
				
			} else {
				logger.error(method + "User '" + email + "' exists in WOApps world but incorrect password was provided.");
				throw new IncorrectPasswordException("Incorrect password.");
				
			}
		} // catch
		
		
		// call LSDS to log user in WOApps world
		long lsdsStart = System.currentTimeMillis();
		try {
			logger.debug(method + "Call LSDS to log user '" + email + "' in WOApps world");
			lsdsClient.login(email, password, loginResult.getLoginToken(), "WEB");
			
		} catch (UserNotFoundException unfe) {
			// check if the user exists in WOApp, but got return UserNotFoundException due to incorrect password
			boolean isUserExistedWithWrongPassword = lsdsClient.isUserExisted(email, null);
			if (isUserExistedWithWrongPassword) {
				logger.error(method + "Incorrect password when loggin user '" + email + "' in WOApps.");
				throw new IncorrectPasswordException("Incorrect password.");
			}
			
			// Otherwise we failed to login WOApps world due to non-existing user
			// then we create new user in WOApps world
//			User wifiUser = userDao.findByEmail(email);
			User wifiUser = loginResult.getUser();
//			Country country = wifiUser.getCountry();

			try {
				logger.debug(method + "User '" + email + "' does not exist in WOApps. Call LSDS to create this user.");
				lsdsClient.createAccount(email, password, "NA", "US", loginResult.getLoginToken(), "WEB", false, wifiUser.getUid());
				
			} catch (EmailAlreadyTakenException e) {
				// this exception is unlikely to happen due to the UserNotFoundException above
			} catch (Exception e) {
				// trying to remove account because the creation could be ok 
				// but a network problem makes a fake failure.
				try {
					boolean ret = lsdsClient.removeUser(email);
					if (!ret) {
						logger.error("Fail to rollback user (" + email + ")");
					}
				} catch (XmlRpcException e1) {
					logger.error("Looks like that it failed to rollback user (" + email + "): " + e1.getMessage(), e1);
				}
				throw new RuntimeException(e);
			}
		} catch (Exception e) {
			try {
				// trying to logout because the login could be ok 
				// but a network problem makes a fake failure.
				lsdsClient.logout(loginResult.getLoginToken());
			} catch (Exception e1) {
				logger.error("Fail to logout user after a failure of login");
			}
			throw new RuntimeException(e);
		}
		
		long lsdsEnd = System.currentTimeMillis();
		logger.debug(method + "lsdsClient.login():  completed in " + (lsdsEnd - lsdsStart) + " ms");
		
		long end = System.currentTimeMillis();
		logger.debug(method + "completed in " + (end - start) + " ms");
		
		return loginResult;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#logout(java.lang.String)
	 */
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public void logout(String loginToken, String loginDomain) throws LogoutException, InvalidParameterException, XmlRpcException {
		
		String method = "logout():  ";
		
		if ((loginToken == null) || ("".equals(loginToken))) {
			String msg = method + "The given login token was 'null' or empty.";
			throw new InvalidParameterException(msg);
		}
		
		List<Authenticated> logins = authenticatedDao.findByLoginToken(loginToken, loginDomain);
		
		if (logins == null || logins.isEmpty()) {
			// Found no authentication
			String msg = "No authentication found for loginToken=" + loginToken + " loginDomain=" + loginDomain;
			logger.debug(method + msg);
			throw new LogoutException(msg);
			
		} else {
			for (Authenticated login : logins) {
				try {
					authenticatedDao.delete(login);
				}
				catch (HibernateException he) {
					logger.error(method + "HibernateException thrown when attempting to remove 'authenticated' record.", he);
				}
				
				// Call LSDS to logout the user in WOApps
				lsdsClient.logout(loginToken);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#isLoggedIn(java.lang.String)
	 */
	@Transactional("consumer")
	public boolean isLoggedIn(String token, String loginDomain) {
		
		List<Authenticated> list = authenticatedDao.findByLoginToken(token, loginDomain);

		if ((list == null) || (list.isEmpty())) {
			return false;
		}
		
		//	TODO:  Need to notify Web Services team of database inconsistency.
		if (list.size() > 1) {
			logger.warn("More than one record was found in the 'authenticated' table with login token '" + token + "'.");
		}
		return true;
	}

	/**
	 * <p></p>
	 * 
	 * @param loginDomain
	 * @param user
	 * 
	 * @return
	 */
	private String isLoggedIn(User user, String loginDomain) {
		
		Set<Authenticated> authenticatedSet = user.getAuthenticateds();
		if ((authenticatedSet == null) || (authenticatedSet.isEmpty())) {
			return null;
		}
		
		//	TODO:  [KFM]:  May not need this if domains are not enforced.
		Iterator<Authenticated> authIter = authenticatedSet.iterator();
		while (authIter.hasNext()) {
			Authenticated auth = authIter.next();
			if (loginDomain.equals(auth.getLoginDomain())) {
				return auth.getLoginToken();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#isLoggedInEmail(java.lang.String)
	 */
	@Transactional("consumer")
	public boolean isLoggedInEmail(String email, String loginDomain) throws DuplicateEmailAddressException, UserNotFoundException {
		
		User user = findUserWithEmail(email);
		
		String token = isLoggedIn(user, loginDomain);
		
		if ((token == null) || ("".equals(token))) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#removeAccount(java.lang.String)
	 */
	@Transactional("consumer")
	public void removeAccount(String email) throws UserNotFoundException {
		
		User user = null;
		try {
			user = userDao.findByEmail(email);
		}
		catch (DuplicateEmailAddressException deae) {
			deae.printStackTrace();
		}
		
		if (user == null) {
			String msg = "No user with email address '" + email + "' could be found.";
			throw new UserNotFoundException(msg);
		}
		
		long userId = user.getUserId();
		
		//	Logout all of the 'authenticated' records for the user.
		List<Authenticated> logins = authenticatedDao.findByUserId(userId);
		for (Authenticated auth : logins) {
			authenticatedDao.delete(auth);
		}
		
		userDao.delete(user);
	}
	
	/**
	 * 
	 * @return
	 */
	private String generateUserUUID() {
		UUID uuid = UUID.randomUUID();
		
		return uuid.toString();
	}
	/**
	 * 
	 * @param email
	 * @return
	 * @throws DuplicateEmailAddressException 
	 * @throws UserNotFoundException 
	 */
	@Transactional("consumer")
	public String getUserUID(String email) throws DuplicateEmailAddressException, UserNotFoundException {
		// Find user
		User user = userDao.findByEmail(email);
		
		if (user == null) {
			String msg = "No user with email address '" + email + "' could be found.";
			throw new UserNotFoundException(msg);
		}
		
		return user.getUid();
	}

	/* (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#findUserUIDByDisplayId(java.lang.String)
	 */
	@Transactional("consumer")
	public String findUserUIDByPenDisplayId(String displayId) throws RegistrationNotFoundException, AFPException {
		
		PenID penId = new PenID(displayId);
		
		String uid = findUserUIDBySerialNumber(Long.toString(penId.getId()));
		
		return uid;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#findUserUIDBySerialNumber(java.lang.String)
	 */
	@Transactional("consumer")
	public String findUserUIDBySerialNumber(String serialNumber) throws RegistrationNotFoundException {
		
		RegisteredDevice regDev = registeredDeviceDao.findBySerialNumber(serialNumber);
		if (regDev == null) {
			String msg = "Pen with serial number '" + serialNumber + "' is not registered.";
			throw new RegistrationNotFoundException(msg);
		}
		User user = regDev.getUser();
		String uid = user.getUid();
		
		return uid;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.framework.login.service.LoginService#changePassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional("consumer")
	public boolean changePassword(String email, String oldPassword, String newPassword) throws UserNotFoundException, AuthenticationException {
		
		//	Look up the user in the databse.
		User user = null;
		try {
			user = userDao.findByEmail(email);
		}
		catch (DuplicateEmailAddressException deae) {
			deae.printStackTrace();
			return false;
		}
		
		if (user == null) {
			throw new UserNotFoundException();
		}
		
		//	Verify the old password matches.
		String hashed = hashPassword(oldPassword);
		String oldHashed = user.getPassword();
		if (hashed.equals(oldHashed)) {
			
			//	Set the new password.
			String newHashed = hashPassword(newPassword);
			user.setPassword(newHashed);
			userDao.merge(user);
			return true;
		}
		else {
			throw new AuthenticationException();
		}
	}

	/**
	 * <p></p>
	 * 
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 */
	private String hashPassword(String password) throws AuthenticationException {
		
		String hashed = null;
		try {
			hashed = encryptionUtils.generateBase64EncodedSHAHash(password);
		}
		catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if (hashed == null) {
			throw new AuthenticationException();
		}
		
		return hashed;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param loginToken
	 * @param loginDomain
	 * @return
	 */
	@Transactional("consumer")
	public User getUserInfoByLoginToken(String loginToken, String loginDomain) {
		List<Authenticated> logins = authenticatedDao.findByLoginToken(loginToken, loginDomain);
		
		if (logins == null || logins.isEmpty()) {
			return null;
		}
		
		Authenticated auth = logins.get(0);
		
		User user = auth.getUser();
		
		// Fetching User and its associated objects to avoid lazy loading
		if (user != null) {
			Hibernate.initialize(user);
			Set<Authorization> authorizations = user.getAuthorizations();
			if (authorizations != null) {
				Hibernate.initialize(authorizations);
			}
			
			Set<RegisteredDevice> regDevices = user.getRegisteredDevices();
			if (regDevices != null) {
				Hibernate.initialize(regDevices);
			}
			
//			Country country = user.getCountry();
//			Address address = user.getAddress();
//			
//			if (country != null) {
//				Hibernate.initialize(country);
//			}
//			
//			if (address != null) {
//				Hibernate.initialize(address);
//				if (address.getCountry() != null) {
//					Hibernate.initialize(address.getCountry());
//				}
//				if (address.getUsState() != null) {
//					Hibernate.initialize(address.getUsState());
//				}
//			}
		}
		
		return user;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public User changeUserEmail(String loginToken, String currentEmail, String newEmail) 
			throws UserNotLoggedInException, EmailAlreadyTakenException, DuplicateEmailAddressException, UserNotFoundException, XmlRpcException, InvalidParameterException, LSDSClientException {
		
		String method = "changeUserEmail():  ";
		logger.debug(method + "[loginToken = " + loginToken + ", currentEmail = " + currentEmail + ", newEmail = " + newEmail + "].");
		
		// Check if given LoginToken is valid
		List<Authenticated> authenticated = authenticatedDao.findByLoginToken(loginToken, null);
		if ((authenticated == null) || (authenticated.isEmpty())) {
			String msg = "Login token '" + loginToken + "' not found.";
			logger.info(method + msg);
			throw new UserNotLoggedInException(msg);
		}

		Authenticated auth = authenticated.get(0);
		User user = auth.getUser();
		
		// check if the current user exists
		if (user == null) {
			String msg = "Cannot find a user by loginToken " + loginToken;
			logger.error(method + msg);
			throw new UserNotFoundException(msg);
		}
		
		// if the new provided email is the same as current
		if (user.getPrimaryEmail().equalsIgnoreCase(newEmail)) {
			return user;
		}
				
		// Validate if the new email is already existed in WiFi
		if (isEmailAddressTaken(newEmail)) {
			String msg = "Email '" + newEmail +"' is already taken.";
			logger.error(method + msg);
			throw new EmailAlreadyTakenException(msg);
		}
		
		// Call LSDSClient to change email in WOApp
		lsdsClient.changeEmail(currentEmail, newEmail, loginToken);
		
		// Update current User with new email address
		user.setPrimaryEmail(newEmail.toLowerCase());
		user.setLastModified(new Date());
		user.setLastModifiedBy(SERVICE_FULL_NAME);
		userDao.merge(user);
		
		return user;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated as of LSLogin 1.4. This is replaced by {@link #changeUserEmail(String, String, String)()} 
	 */
	@Deprecated
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public User changeUserEmail(String currentEmail, String newEmail) 
			throws UserNotLoggedInException, EmailAlreadyTakenException, DuplicateEmailAddressException, UserNotFoundException, XmlRpcException, InvalidParameterException, LSDSClientException {
		
		String method = "changeUserEmail():  ";
		
		// check if the current user exists
		User user = userDao.findByEmail(currentEmail);
		if (user == null) {
			String msg = "User '" + currentEmail + "' does not exist.";
			logger.error(method + msg);
			throw new UserNotFoundException(msg);
		}
		
		// if the new provided email is the same as current
		if (user.getPrimaryEmail().equalsIgnoreCase(newEmail)) {
			return user;
		}
		
		// check if the user is logged in		
		List<Authenticated> list = authenticatedDao.findByUserIdAndLoginDomain(user.getUserId(), "WEB");
		if (list == null || list.size() == 0) {
			String msg = "User '" + currentEmail + "' is not logged-in in LDApp.";
			logger.error(method + msg);
			throw new UserNotLoggedInException(msg);
		}
				
		// Validate if the new email is already existed in WiFi
		if (isEmailAddressTaken(newEmail)) {
			String msg = "Email '" + newEmail +"' is already taken.";
			logger.error(method + msg);
			throw new EmailAlreadyTakenException(msg);
		}
		
		// Call LSDSClient to change email in WOApp
		lsdsClient.changeEmail(currentEmail, newEmail, list.get(0).getLoginToken());
		
		// Update current User with new email address
		user.setPrimaryEmail(newEmail.toLowerCase());
		user.setLastModified(new Date());
		user.setLastModifiedBy(SERVICE_FULL_NAME);
		userDao.merge(user);
		
		return user;
	}
	
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public User changeUserPassword(String email, String oldPassword, String newPassword) 
			throws UserNotFoundException, DuplicateEmailAddressException, AuthenticationException, IncorrectPasswordException, XmlRpcException, InvalidParameterException {
		
		String method = "changeUserPassword():  ";
		
		// check if the user exists
		User user = userDao.findByEmail(email);
		if (user == null) {
			String msg = "User '" + email + "' does not exist.";
			logger.error(method + msg);
			throw new UserNotFoundException(msg);
		}
		
		// check if the provided old password matches current password
		if (!user.getPassword().equals(hashPassword(oldPassword))) {
			String msg = "The provided old password is incorrect.";
			logger.error(method + msg);
			throw new IncorrectPasswordException(msg);
		}
		
		// call LSDSClient to change password in WOApp
		lsdsClient.changePassword(email, oldPassword, newPassword);
		
		// update current User with new password
		user.setPassword(hashPassword(newPassword));
		user.setLastModified(new Date());
		user.setLastModifiedBy(SERVICE_FULL_NAME);
		userDao.merge(user);
		
		return user;
	}
	
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public User changeUserPasswordWithLoginToken(String loginToken, String email, String newPassword) 
			throws UserNotLoggedInException, UserNotFoundException, XmlRpcException, InvalidParameterException, AuthenticationException {
		
		String method = "changeUserPasswordWithLoginToken():  ";
		
		// Check if the  user is logged in
		List<Authenticated> auths = authenticatedDao.findByLoginToken(loginToken, "WEB");
		
		if (auths.isEmpty()) {
			String msg = "The loginToken '" + loginToken + "' could not be found.";
			logger.error(method + msg);
			throw new UserNotLoggedInException(msg);
		}
		
		User user = auths.get(0).getUser();
		
		// Check if the provided loginToken matches the specified email
		if (!user.getPrimaryEmail().equalsIgnoreCase(email)) {
			String msg = "The loginToken does not match with the provided email";
			logger.error(method + msg);
			throw new UserNotFoundException(msg);
		}
		
		// call LSDSClient to change password in WOApp
		lsdsClient.changePasswordWithLoginToken(loginToken, email, newPassword);
		
		// update current User with new password
		user.setPassword(hashPassword(newPassword));
		user.setLastModified(new Date());
		user.setLastModifiedBy(SERVICE_FULL_NAME);
		userDao.merge(user);
		
		return user;
	}
	
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public User changeUserPasswordForSupportUser(String supportUserLoginToken, String userEmail, String userPassword) 
			throws UserNotLoggedInException, InsufficientPrivilegeException, DuplicateEmailAddressException, UserNotFoundException, AuthenticationException, XmlRpcException, InvalidParameterException {
		
		String method = "changeUserPasswordForSupportUser():  ";
		
		// Check if the  support_user is logged in
		List<Authenticated> auths = authenticatedDao.findByLoginToken(supportUserLoginToken, "WEB");
		
		if (auths.isEmpty()) {
			String msg = "The loginToken '" + supportUserLoginToken + "' could not be found.";
			logger.error(method + msg);
			throw new UserNotLoggedInException(msg);
		}
		
		// Check if the user has appropriate role and group
		String userManagerRoleName = appProperties.getProperty("consumer.usermanager.rolename", "ROLE_UserManager, ROLE_SuperUser");
		String managerGroupName = appProperties.getProperty("consumer.usermanager.groupname", "LSManageUpdateService");
		User supportUser = auths.get(0).getUser();
		if (!isUserWithRole(supportUser, userManagerRoleName)) {
			String msg = "The user '" + supportUser.getPrimaryEmail() + "' does not have role '" + userManagerRoleName + "'";
			logger.error(method + msg);
			throw new InsufficientPrivilegeException(msg);
		}
		if (!isUserInGroup(supportUser, managerGroupName)) {
			String msg = "The user '" + supportUser.getPrimaryEmail() + "' does not belong to group '" + managerGroupName + "'";
			logger.error(method + msg);
			throw new InsufficientPrivilegeException(msg);
		}
		
		// Check if the user whose password being changed exists
		User user = userDao.findByEmail(userEmail);
		if (user == null) {
			String msg = "User '" + userEmail + "' does not exist.";
			logger.error(method + msg);
			throw new UserNotFoundException(msg);
		}
	
		// call LSDS to change user password in WOApps
		lsdsClient.changePasswordWithoutUserTokenAndOldPassword(userEmail, userPassword);
		
		// change user password in WiFi
		user.setPassword(hashPassword(userPassword));
		user.setLastModified(new Date());
		user.setLastModifiedBy(SERVICE_FULL_NAME);
		userDao.merge(user);
		
		return user;
	}
	
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public User changePasswordWithoutUserTokenAndOldPassword(String email, String newPassword) 
			throws UserNotFoundException, DuplicateEmailAddressException, XmlRpcException, AuthenticationException, InvalidParameterException {
		
		String method = "changePasswordWithoutUserTokenAndOldPassword";
		
		User user = userDao.findByEmail(email);
		if (user == null) {
			String msg = "User '" + email + "' does not exist.";
			logger.error(method + msg);
			throw new UserNotFoundException(msg);
		}
		
		// call LSDS to change user password in WOApps
		lsdsClient.changePasswordWithoutUserTokenAndOldPassword(email, newPassword);
		
		// change user password in WiFi
		user.setPassword(hashPassword(newPassword));
		user.setLastModified(new Date());
		user.setLastModifiedBy(SERVICE_FULL_NAME);
		userDao.merge(user);
		
		return user;
	}
		
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public boolean subscribeCustomer(String token)
			throws XmlRpcException, AuthenticationException, InvalidParameterException, UserNotLoggedInException {
		
		return lsdsClient.subscribeCustomer(token);			
	}
	
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public boolean unsubscribeCustomer(String token)
			throws XmlRpcException, AuthenticationException, InvalidParameterException, UserNotLoggedInException {
		
		return lsdsClient.unsubscribeCustomer(token);
	}
	
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public boolean isCustomerSubscribed(String token)
			throws XmlRpcException, AuthenticationException, InvalidParameterException, UserNotLoggedInException {
		
		return lsdsClient.isCustomerSubscribed(token);				
	}
	
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public boolean subscribeGuest(String email, String name, String country) throws XmlRpcException, InvalidParameterException {

		return lsdsClient.subscribeGuest(email, name, country);
	}

	@Transactional(value="consumer", rollbackFor=Exception.class)
	public boolean unsubscribeGuest(String email) throws XmlRpcException, InvalidParameterException {

		return lsdsClient.unsubscribeGuest(email);
	}

    @Transactional(value="consumer", rollbackFor=Exception.class)
    public String composePTAUrl(String loginToken)
            throws AuthenticationException, InvalidParameterException, UserNotLoggedInException, UserNotFoundException {

        String method = "composePTAUrl";

        if ((loginToken == null) || ("".equals(loginToken))) {
            String msg = "The given token parameter was either 'null' or empty.";
            throw new InvalidParameterException(msg);
        }

        // Check if given LoginToken is valid
        List<Authenticated> authenticated = authenticatedDao.findByLoginToken(loginToken, null);
        if ((authenticated == null) || (authenticated.isEmpty())) {
            String msg = "Login token '" + loginToken + "' not found.";
            logger.info(method + msg);
            throw new UserNotLoggedInException(msg);
        }

        Authenticated auth = authenticated.get(0);
        User user = auth.getUser();
        // check if the current user exists
        if (user == null) {
            String msg = "Cannot find a user by loginToken " + loginToken;
            logger.error(method + msg);
            throw new UserNotFoundException(msg);
        }

        UserDTO userDTO = null;
        String uuid = user.getUid();
        userDTO = this.findUserByUId(uuid);
        if (userDTO == null) {
            String msg = "Cannot find a user by UUID " + uuid;
            logger.error(method + msg);
            throw new UserNotFoundException(msg);
        }

        //build PTA URL
        StringBuilder sb_pta = new StringBuilder();

        sb_pta.append("p_name.first=");
        String firstName = (userDTO.getFirstName() == null) ? "" : userDTO.getFirstName();
        sb_pta.append(firstName);

        sb_pta.append("&p_name.last=");
        String lastName =  (userDTO.getLastName() == null) ? "" : userDTO.getLastName();
        sb_pta.append(lastName);

        sb_pta.append("&p_userid=");
        String email = userDTO.getPrimaryEmail();
        sb_pta.append(email);

        sb_pta.append("&p_email.addr=");
        sb_pta.append(email);

        sb_pta.append("&p_li_passwd=");
        String pwd =  appProperties.getProperty("consumer.rightnow.pta_secret_key", "Livescribe2014");
        sb_pta.append(pwd);

        byte[] encodedBytes = Base64.encodeBase64(sb_pta.toString().getBytes());
        String encodedPTAString = new String(encodedBytes);

        String login_url =  appProperties.getProperty("consumer.rightnow.url.login", "https://livescribe.custhelp.com/ci/pta/login/redirect/home/p_li/");
        StringBuilder sb_url = new StringBuilder();
        sb_url.append(login_url);
        sb_url.append(encodedPTAString);

        return sb_url.toString();
    }
	
	private boolean isUserWithRole(User user, String roleName) {
		Set<XUserRole> roles = user.getXUserRoles();
		
		if (roles == null || roles.isEmpty()) {
			return false;
		}
		
		for (XUserRole userRole : roles) {
			if (roleName.contains(userRole.getRole().getRoleName())) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isUserInGroup(User user, String groupName) {
		Set<XUserGroup> groups = user.getXUserGroups();
		
		if (groups == null || groups.isEmpty()) {
			return false;
		}
		
		for (XUserGroup userGroup : groups) {
			if (groupName.equals(userGroup.getGroup().getGroupName())) {
				return true;
			}
		}
		
		return false;
	}
	
	private String parseCountryFromLocale(String locale) throws LocaleException {
		String[] parts = locale.split("-");
		if (parts.length != 2) {
			String msg = "Locale '" + locale + "' was not in a valid format.";
			throw new LocaleException(msg);
		}
		
		return parts[1].toUpperCase();
	}


	/**
	 * <p>Saves the given parameters to the database as part of a record in the
	 * <code>authenticated</code> table.</p>
	 * 
	 * @param loginDomain The domain to tag this login with.
	 * @param loginToken The login token to assign.
	 * @param user The <code>User</code> being logged in.
	 * 
	 * @throws LoginException if a Hibernate or database error occurs.
	 */
	private void saveLogin(String loginDomain, String loginToken, User user) throws LoginException {
		
		String method = "saveLogin():  ";
		
		Authenticated newLogin = new Authenticated();
		newLogin.setLoginDomain(loginDomain);
		newLogin.setLoginToken(loginToken);
		newLogin.setUser(user);
		Date now = new Date();
		newLogin.setCreated(now);
		newLogin.setLastModified(now);
		newLogin.setCreatedBy(SERVICE_FULL_NAME);
		
		try {
			authenticatedDao.persist(newLogin);
		}
		catch (HibernateException he) {
			String msg = method + "HibernateException thrown when attempting to save Authenticated record.";
			throw new LoginException(msg, he);
		}
	}
	

	/**
	 * <p>Validates the given password against what is stored in the 
	 * database.</p>
	 * 
	 * If the stored password is prefaced with <code>{SHA}</code>, the algorithm
	 * used in WebObject/FrontBase is used to hash the given password.
	 * &nbsp;&nbsp;Otherwise, a newer algorithm is used.&nbsp;&nbsp;(This is 
	 * due to historical reasons, not a design decision!)
	 * 
	 * @param password The given password to match against what is stored in 
	 * the database.
	 *  
	 * @param user The <code>User</code> object representing a record in the 
	 * MySQL <code>user</code> table.
	 * 
	 * @throws LoginException if an error occurs when hashing the given 
	 * <code>password</code>.
	 * 
	 * @throws IncorrectPasswordException if the given <code>password</code> 
	 * did not match the saved password in the <code>User</code> object.
	 */
	private void validatePassword(String password, User user) throws LoginException, IncorrectPasswordException {
		
		String method = "validatePassword():  ";
		
		String savedPassword = user.getPassword();
		String hashedPassword = null;
		try {
			//	If '{SHA}' prefix is present, use the 'FrontBase' hashing algorithm.
			if (savedPassword.startsWith(PASSWORD_PREFIX)) {
				String hashed = encryptionUtils.generateFrontBaseHash(password);
				hashedPassword = PASSWORD_PREFIX + hashed; 
			}
			//	Otherwise, hash the given password using the 'new' algorithm.
			else {
				hashedPassword = encryptionUtils.generateBase64EncodedSHAHash(password);
			}
		}
		catch (InvalidKeyException ike) {
			String msg = "InvalidKeyException thrown while attempting to hash password.";
			throw new LoginException(method + msg, ike);
		}
		catch (NoSuchAlgorithmException nsae) {
			String msg = "NoSuchAlgorithmException thrown while attempting to hash password.";
			throw new LoginException(method + msg, nsae);
		}
		catch (UnsupportedEncodingException uee) {
			String msg = "UnsupportedEncodingException thrown while attempting to hash password.";
			throw new LoginException(method + msg, uee);
		}
	
		if (!savedPassword.equals(hashedPassword)) {
			String msg = "Incorrect password.";
			throw new IncorrectPasswordException(msg);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional("consumer")
	public UserDTO findUserByUId(String uid) throws UserNotFoundException {
		
		String method = "findUserByUID():  ";
		
		User user = null;
		try {
			logger.debug(method + "Finding user in MySQL with uid '" + uid + "'.");
			user = userDao.findByUID(uid);
		}
		catch (HibernateException he) {
			String msg = "HibernateException thrown while attempting to find user by uid '" + uid + "' in MySQL.";
			logger.error(method + msg, he);
			//	TODO:  [KFM]:  Need to throw an exception that is more descriptive than "User Not Found" below.
		}
		
		if (user == null) {
			String msg = method + "User with uid '" + uid + "' not found in MySQL.";
			throw new UserNotFoundException(msg);
		}
		logger.debug(method + "Found user with UID '" + user.getUid() + "' in MySQL.");

		UserDTO userDto = new UserDTO();
		userDto.setUid(user.getUid());
		userDto.setPrimaryEmail(user.getPrimaryEmail());
		userDto.setPassword(user.getPassword());
		userDto.setEnabled(user.getEnabled());
//		return new UserDTO(user.getUid(), user.getPrimaryEmail(), user.getPassword(), user.getEnabled(), user.getSendDiagnostics());
		return userDto;
	}

}

package com.livescribe.aws.login.lsds;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.framework.config.AppProperties;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.LSDSClientException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.login.exception.AuthenticationException;
import com.livescribe.framework.login.exception.EmailAlreadyTakenException;
import com.livescribe.framework.login.exception.IncorrectPasswordException;
import com.livescribe.framework.login.exception.UserNotLoggedInException;

public class LSDSClient {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static final String RETURN_CODE = "returnCode";
	private static final String SERVER_MSG 	= "serverMessage";	
	
	// All response code returned by LSDS
	private static final int SUCCESS_CODE 								= 0;
	private static final int UNKNOWN_SERVER_ERROR_CODE 					= -1;
	private static final int INVALID_USER_EMAIL_CODE 					= -2;
	private static final int INVALID_USER_TOKEN 						= -5;
	private static final int MISSING_REQUIRED_PARAMETER_CODE 			= -7;
	private static final int A_PASSED_IN_PARAMETER_WAS_INVALID_CODE 	= -8;
	private static final int INVALID_NEW_PASSWORD						= -9;
	private static final int INVALID_COUNTRY							= -10;
	private static final int USER_ALREADY_EXISTS_CODE 					= -100;
	private static final int USER_DOES_NOT_EXIST 						= 1003;
	private static final int INVALID_EMAIL_ADDRESS						= 1004;
	private static final String UNIQUE_ID 								= "uniqueID";
	private static final String OPT_IN									= "optIn";
	
	@Autowired
	private AppProperties appProperties;
	
	private XmlRpcClient client;
	
	private XmlRpcClientConfigImpl lsDsUserServiceConfig;
	
	public LSDSClient(AppProperties appProperties) throws MalformedURLException {
		this.appProperties = appProperties;
		String url = this.appProperties.getProperty("lsds.userservice.url");
		logger.debug("LSDS URL:  " + url);
		
		// prepare configs
		URL lsDsUserServiceUrl = new URL(this.appProperties.getProperty("lsds.userservice.url"));
		lsDsUserServiceConfig = new XmlRpcClientConfigImpl();
		lsDsUserServiceConfig.setEnabledForExtensions(true);
		lsDsUserServiceConfig.setServerURL(lsDsUserServiceUrl);
		
		// initialize XmlRpcClient
		client = new XmlRpcClient();
		client.setConfig(lsDsUserServiceConfig);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param password
	 * @param occupation
	 * @param country
	 * @param loginToken - login token generated by LSLoginService
	 * @return
	 * @throws XmlRpcException
	 * @throws InvalidParameterException
	 * @throws EmailAlreadyTakenException
	 * @throws LSDSClientException
	 */
	public HashMap<String, Object> createAccount(String email, String password, String occupation, String country, 
			String loginToken, String loginDomain, Boolean optIn, String uid) 
			throws XmlRpcException, InvalidParameterException, EmailAlreadyTakenException, LSDSClientException {
		
		String method = "createAccount(" + email + "):  ";
		logger.debug(method);
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("desktopVersion", loginDomain);
		paramMap.put("desktopUser", email);
		paramMap.put("password", password);
		paramMap.put("occupation", occupation);
		paramMap.put("country", country);
		paramMap.put("token", loginToken);
		paramMap.put("optIn", optIn.toString());
		paramMap.put("uid", uid);
		
		Object params[] = new Object[] {"newCreateAccount", paramMap};
		
		long start = System.currentTimeMillis();
		HashMap<String, Object> resultMap = (HashMap)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		long end = System.currentTimeMillis();
		logger.debug(method + "XmlRpcClient completed in " + (end - start) + "ms");
		
		int returnCode = (Integer)resultMap.get(RETURN_CODE);
		logger.debug(method + "returnCode=" + String.valueOf(returnCode));
		
		switch (returnCode) {
			case INVALID_USER_EMAIL_CODE:
				throw new InvalidParameterException("A valid email is required.");
				
			case MISSING_REQUIRED_PARAMETER_CODE:
				throw new InvalidParameterException("Missing parameter.");
				
			case USER_ALREADY_EXISTS_CODE:
				throw new EmailAlreadyTakenException("Email '" + email + "' is already in use.");
				
			case UNKNOWN_SERVER_ERROR_CODE:
				throw new LSDSClientException("Unknown error occured from LSDS.");
		}
		
		return resultMap;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param password
	 * @param loginToken - login token generated by LSLoginService
	 * @return
	 * @throws XmlRpcException
	 * @throws InvalidParameterException
	 * @throws UserNotFoundException
	 * @throws LSDSClientException
	 */
	public HashMap<String, Object> login(String email, String password, String loginToken, String loginDomain) 
			throws XmlRpcException, InvalidParameterException, UserNotFoundException, LSDSClientException {
		
		String method = "login(" + email + "):  ";
		logger.debug(method);
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("desktopVersion", loginDomain);
		paramMap.put("desktopUser", email);
		paramMap.put("password", password);
		paramMap.put("token", loginToken);
		
		Object params[] = new Object[] {"newLogin", paramMap};
		
		long start = System.currentTimeMillis();
		HashMap<String, Object> resultMap = (HashMap)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		long end = System.currentTimeMillis();
		logger.debug(method + "XmlRpcClient completed in " + (end - start) + "ms");
		
		int returnCode = (Integer)resultMap.get(RETURN_CODE);
		logger.debug(method + "returnCode=" + String.valueOf(returnCode));
		
		switch (returnCode) {
			case INVALID_USER_EMAIL_CODE:
				throw new InvalidParameterException("A valid email is required.");
				
			case MISSING_REQUIRED_PARAMETER_CODE:
				throw new InvalidParameterException("Missing parameter.");
				
			case USER_DOES_NOT_EXIST:
				throw new UserNotFoundException("Email '" + email + "' does not exist.");
				
			case UNKNOWN_SERVER_ERROR_CODE:
				throw new LSDSClientException("Unknown error occured from LSDS.");
		}
		return resultMap;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param loginToken
	 * @return
	 * @throws XmlRpcException
	 */
	public boolean logout(String loginToken) throws XmlRpcException {
		String method = "logout(" + loginToken + "):  ";
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("userToken", loginToken);
		
		Object params[] = new Object[] {"logoutWEB", paramMap};
		
		HashMap<String, Object> resultMap = (HashMap)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		boolean ret = false;
		
		//		Avoid NullPointerExceptions.
		if (resultMap == null) {
			String msg = "The result Map returned from LSDS was 'null'.";
			logger.error(method + msg);
			throw new XmlRpcException(msg);
		} else {
			Integer returnCode = (Integer)resultMap.get(RETURN_CODE);
			if (returnCode == null || returnCode != SUCCESS_CODE) {
				logger.error(method + "Fail to logout for token " +loginToken + ": " + resultMap.get(SERVER_MSG));
			} else 
				ret = true;
		}
		
		logger.debug(method + " returns " + ret);
		return ret;
	}

	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws XmlRpcException
	 */
	public String getExistingUserShortId(String email, String password) throws XmlRpcException {
		String method = "getExistingUserShortId(" + email + "):  ";
		logger.debug(method);
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("desktopUser", email);
		paramMap.put("password", password);
		
		Object params[] = new Object[] {"checkUserAvail", paramMap};
		
		HashMap<String, Object> resultMap = (HashMap)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		
		//	Avoid NullPointerExceptions.
		if (resultMap == null) {
			String msg = "The result Map returned from LSDS was 'null'.";
			logger.error(method + msg);
			throw new XmlRpcException(msg);
		}
		
		String userShortId = (String)resultMap.get(UNIQUE_ID);
		
		return userShortId;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws XmlRpcException
	 */
	public boolean isUserExisted(String email, String password) throws XmlRpcException {
		String method = "isUserExisted(" + email + "):  ";
		logger.debug(method);
		
		String userShortId = getExistingUserShortId(email, password);
		
		if (userShortId == null || userShortId.isEmpty()) {
			logger.debug(method + " returns false" );
			return false;
		}
		
		logger.debug(method + " returns true");
		return true;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws XmlRpcException
	 */
	public boolean removeUser(String email) throws XmlRpcException {
		String method = "removeUser(" + email + "):  ";
		logger.debug(method);
		boolean ret = false;
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("email", email);
		paramMap.put("secretKey", "88SDJFHSA7IS9DJ9NFKJ9SI9FG");
		
		Object params[] = new Object[] {"removeUser", paramMap};
		
		HashMap<String, Object> resultMap = (HashMap)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		
		//	Avoid NullPointerExceptions.
		if (resultMap == null) {
			String msg = "The result Map returned from LSDS was 'null'.";
			logger.error(method + msg);
			throw new XmlRpcException(msg);
		} else {
			Integer returnCode = (Integer)resultMap.get(RETURN_CODE);
			if (returnCode == null || returnCode != SUCCESS_CODE) {
				logger.error(method + "Fail to rollback user: " + resultMap.get(SERVER_MSG));
			} else 
				ret = true;
		}
		
		logger.debug(method + " returns " + ret);
		return ret;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param currentEmail
	 * @param newEmail
	 * @param loginToken
	 * @throws XmlRpcException
	 * @throws InvalidParameterException
	 * @throws UserNotLoggedInException
	 * @throws EmailAlreadyTakenException
	 * @throws LSDSClientException
	 */
	public void changeEmail(String currentEmail, String newEmail, String loginToken) 
			throws XmlRpcException, InvalidParameterException, UserNotLoggedInException, EmailAlreadyTakenException, LSDSClientException {
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("desktopUser", currentEmail);
		paramMap.put("newEmail", newEmail);
		paramMap.put("userToken", loginToken);
		
		Object params[] = new Object[] {"newChangeEmail", paramMap};
		HashMap<String, Object> resultMap = (HashMap)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		int returnCode = (Integer)resultMap.get(RETURN_CODE);
		
		switch(returnCode) {
			case MISSING_REQUIRED_PARAMETER_CODE:
				throw new InvalidParameterException("Missing parameter.");
				
			case INVALID_USER_TOKEN:
				throw new UserNotLoggedInException("loginToken '" + loginToken + "' is not a valid token.");
		
			case INVALID_EMAIL_ADDRESS:
				throw new InvalidParameterException("New email '" + newEmail + "' is not valid.");
				
			case USER_ALREADY_EXISTS_CODE:
				throw new EmailAlreadyTakenException("New email '" + newEmail + "' already exists in WOApps.");
				
			case UNKNOWN_SERVER_ERROR_CODE:
				throw new LSDSClientException("Unknown error occured from LSDS.");
		}
	}
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param oldPassword
	 * @param newPassword
	 * @throws XmlRpcException
	 * @throws InvalidParameterException
	 * @throws IncorrectPasswordException
	 */
	public void changePassword(String email, String oldPassword, String newPassword) 
			throws XmlRpcException, InvalidParameterException, IncorrectPasswordException {
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("desktopUser", email);
		paramMap.put("oldPassword", oldPassword);
		paramMap.put("newPassword", newPassword);
		
		Object params[] = new Object[] {"changePassword", paramMap};
		HashMap<String, Object> resultMap = (HashMap)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		int returnCode = (Integer)resultMap.get(RETURN_CODE);
		
		switch(returnCode) {
			case MISSING_REQUIRED_PARAMETER_CODE:
				throw new InvalidParameterException("Missing parameter.");

			case INVALID_NEW_PASSWORD:
				throw new InvalidParameterException("The new password is not valid.");
				
			case INVALID_USER_EMAIL_CODE:
				throw new IncorrectPasswordException("User does not exist or invalid password.");
		}
	}
	
	/**
	 * <p></p>
	 * @param token
	 * @param email
	 * @param newPassword
	 * @throws XmlRpcException
	 * @throws InvalidParameterException
	 * @throws UserNotLoggedInException
	 * @throws IncorrectPasswordException
	 */
	public void changePasswordWithLoginToken(String token, String email, String newPassword) 
			throws XmlRpcException, InvalidParameterException, UserNotLoggedInException {
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("userToken", token);
		paramMap.put("desktopUser", email);
		paramMap.put("newPassword", newPassword);
		
		Object params[] = new Object[] {"changePasswordWithUserToken", paramMap};
		HashMap<String, Object> resultMap = (HashMap)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		int returnCode = (Integer)resultMap.get(RETURN_CODE);
		
		switch(returnCode) {
			case MISSING_REQUIRED_PARAMETER_CODE:
				throw new InvalidParameterException("Missing parameter.");

			case INVALID_NEW_PASSWORD:
				throw new InvalidParameterException("The new password is not valid.");
				
			case INVALID_USER_TOKEN:
				throw new UserNotLoggedInException("loginToken '" + token + "' is not a valid token.");
			
			case INVALID_USER_EMAIL_CODE:
				throw new UserNotLoggedInException("The provided email does not match with the specified user token.");
		}
	}
	
	public void changePasswordWithoutUserTokenAndOldPassword(String userEmail, String userPassword) 
			throws XmlRpcException, InvalidParameterException, UserNotFoundException {
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("desktopUser", userEmail);
		paramMap.put("newPassword", userPassword);
		paramMap.put("secret", "9a62ad13-49c6-4fe8-92eb-b44a441f42bd");
		
		Object params[] = new Object[] {"changePasswordWithoutUserTokenAndOldPassword", paramMap};
		HashMap<String, Object> resultMap = (HashMap)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		int returnCode = (Integer)resultMap.get(RETURN_CODE);
		
		switch(returnCode) {
			case MISSING_REQUIRED_PARAMETER_CODE:
				throw new InvalidParameterException("Missing parameter.");
				
			case INVALID_NEW_PASSWORD:
				throw new InvalidParameterException("The new password is not valid.");
		
			case A_PASSED_IN_PARAMETER_WAS_INVALID_CODE:
				throw new InvalidParameterException("The value passed in was invalid.");
				
			case USER_DOES_NOT_EXIST:
				throw new UserNotFoundException("Email '" + userEmail + "' does not exist.");
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean subscribeCustomer(String token)
			throws XmlRpcException, AuthenticationException, InvalidParameterException, UserNotLoggedInException {
		
		String method = "subscribeCustomer";
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("userToken", token);
		Object params[] = new Object[] {"subscribeCustomer", paramMap};
		
		HashMap<String, Object> resultMap = (HashMap<String, Object>)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		
		boolean optIn = false;
		int returnCode = MISSING_REQUIRED_PARAMETER_CODE;
//		Avoid NullPointerExceptions.
		if (resultMap == null) {
			String msg = "The result Map returned from LSDS was 'null'.";
			logger.error(method + msg);
			throw new XmlRpcException(msg);
		} else {
			returnCode = (Integer)resultMap.get(RETURN_CODE);		
			optIn = Boolean.parseBoolean( (String)resultMap.get(OPT_IN) );			
		}
		
		switch(returnCode) {
			case MISSING_REQUIRED_PARAMETER_CODE:
				throw new InvalidParameterException("Missing parameter.");
	
			case INVALID_NEW_PASSWORD:
				throw new InvalidParameterException("The new password is not valid.");
				
			case INVALID_USER_TOKEN:
				throw new UserNotLoggedInException("loginToken '" + token + "' is not a valid token.");					
		}
		return optIn;
	}
	
	@SuppressWarnings("unchecked")
	public boolean unsubscribeCustomer(String token)
			throws XmlRpcException, AuthenticationException, InvalidParameterException, UserNotLoggedInException {
		
		String method = "unsubscribeCustomer";
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("userToken", token);
		Object params[] = new Object[] {"unsubscribeCustomer", paramMap};
		
		HashMap<String, Object> resultMap = (HashMap<String, Object>)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		boolean optIn = false;
		int returnCode = MISSING_REQUIRED_PARAMETER_CODE;
//		Avoid NullPointerExceptions.
		if (resultMap == null) {
			String msg = "The result Map returned from LSDS was 'null'.";
			logger.error(method + msg);
			throw new XmlRpcException(msg);
		} else {
			returnCode = (Integer)resultMap.get(RETURN_CODE);
			optIn = Boolean.parseBoolean( (String)resultMap.get(OPT_IN) );
		}
				
		switch(returnCode) {
			case MISSING_REQUIRED_PARAMETER_CODE:
				throw new InvalidParameterException("Missing parameter.");
	
			case INVALID_NEW_PASSWORD:
				throw new InvalidParameterException("The new password is not valid.");
				
			case INVALID_USER_TOKEN:
				throw new UserNotLoggedInException("loginToken '" + token + "' is not a valid token.");					
		}
		return optIn;
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean isCustomerSubscribed(String token)
			throws XmlRpcException, AuthenticationException, InvalidParameterException, UserNotLoggedInException {
		
		String method = "isCustomerSubscribed";
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("userToken", token);
		Object params[] = new Object[] {"isCustomerSubscribed", paramMap};
		
		HashMap<String, Object> resultMap = (HashMap<String, Object>)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		boolean optIn = false;
		int returnCode = MISSING_REQUIRED_PARAMETER_CODE;
//		Avoid NullPointerExceptions.
		if (resultMap == null) {
			String msg = "The result Map returned from LSDS was 'null'.";
			logger.error(method + msg);
			throw new XmlRpcException(msg);
		} else {
			returnCode = (Integer)resultMap.get(RETURN_CODE);
			optIn = Boolean.parseBoolean( (String)resultMap.get(OPT_IN) );
		}
				
		switch(returnCode) {
			case MISSING_REQUIRED_PARAMETER_CODE:
				throw new InvalidParameterException("Missing parameter.");
	
			case INVALID_NEW_PASSWORD:
				throw new InvalidParameterException("The new password is not valid.");
				
			case INVALID_USER_TOKEN:
				throw new UserNotLoggedInException("loginToken '" + token + "' is not a valid token.");					
		}
		return optIn;
	}
	
	@SuppressWarnings("unchecked")
	public boolean subscribeGuest(String email, String name, String country)
			throws XmlRpcException, InvalidParameterException {
		
		String method = "subscribeGuest";
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("email", email);
		paramMap.put("name", name);
		paramMap.put("country", country);
		
		Object params[] = new Object[] {"subscribeGuest", paramMap};
		
		HashMap<String, Object> resultMap = (HashMap<String, Object>)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		boolean optIn = false;
		int returnCode = MISSING_REQUIRED_PARAMETER_CODE;
//		Avoid NullPointerExceptions.
		if (resultMap == null) {
			String msg = "The result Map returned from LSDS was 'null'.";
			logger.error(method + msg);
			throw new XmlRpcException(msg);
		} else {
			returnCode = (Integer)resultMap.get(RETURN_CODE);
		}

		switch(returnCode) {
			case MISSING_REQUIRED_PARAMETER_CODE:
				throw new InvalidParameterException("Missing parameter.");
			case INVALID_COUNTRY:
				throw new InvalidParameterException("Invalid Country parameter expected format: en-US.");
			case SUCCESS_CODE:
				optIn = true;
				break;
		}
		return optIn;
	}
	
	@SuppressWarnings("unchecked")
	public boolean unsubscribeGuest(String email)
			throws XmlRpcException, InvalidParameterException {
		
		String method = "unsubscribeGuest";
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("email", email);
		
		Object params[] = new Object[] {"unsubscribeGuest", paramMap};
		
		HashMap<String, Object> resultMap = (HashMap<String, Object>)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		boolean optIn = true;
		int returnCode = MISSING_REQUIRED_PARAMETER_CODE;
//		Avoid NullPointerExceptions.
		if (resultMap == null) {
			String msg = "The result Map returned from LSDS was 'null'.";
			logger.error(method + msg);
			throw new XmlRpcException(msg);
		} else {
			returnCode = (Integer)resultMap.get(RETURN_CODE);
		}

		switch(returnCode) {
			case MISSING_REQUIRED_PARAMETER_CODE:
				throw new InvalidParameterException("Missing parameter.");
			case SUCCESS_CODE:
				optIn = false;
				break;
		}
		return optIn;
	}
}
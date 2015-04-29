package com.livescribe.framework.oauth.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.livescribe.afp.AFPException;
import com.livescribe.afp.PenID;
import com.livescribe.aws.login.dto.AuthorizationDto;
import com.livescribe.aws.login.util.AuthorizationType;
import com.livescribe.framework.exception.DuplicateEmailAddressException;
import com.livescribe.framework.exception.ServerException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.login.evernote.EvernoteProxy;
import com.livescribe.framework.login.exception.AuthorizationException;
import com.livescribe.framework.login.exception.AuthorizationExpiredException;
import com.livescribe.framework.login.exception.AuthorizationNotFoundException;
import com.livescribe.framework.login.exception.RegistrationNotFoundException;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.orm.consumer.Authenticated;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.CustomAuthenticatedDao;
import com.livescribe.framework.orm.consumer.CustomAuthorizationDao;
import com.livescribe.framework.orm.consumer.CustomRegisteredDeviceDao;
import com.livescribe.framework.orm.consumer.CustomUserDao;
import com.livescribe.framework.orm.consumer.PremiumCode;
import com.livescribe.framework.orm.consumer.PremiumCodeDao;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.web.response.ResponseCode;

/**
 * <p></p>
 * 
 * @author 
 * @version 1.0
 */
public class AuthorizationServiceImpl implements AuthorizationService {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public static final String SERVICE_FULL_NAME = "Authorization Service";
	private static final String EVERNOTE = "EN";
	
	@Autowired
	private CustomUserDao userDao;
	
	@Autowired
	private CustomAuthorizationDao authorizationDao;
	
	@Autowired
	private CustomAuthenticatedDao authenticatedDao;
	
	@Autowired
	private CustomRegisteredDeviceDao registeredDeviceDao;
	
	@Autowired
	private PremiumCodeDao premiumCodeDao;

	@Autowired
	private EvernoteProxy evernoteProxy;
	
	
	@Transactional("consumer")
	public Authorization getAuthorization(String email, AuthorizationType authorizationType) throws UserNotFoundException, DuplicateEmailAddressException {
		String method = "getAuthorization():  ";
		logger.debug(method);
		
		return authorizationDao.findPrimaryAuthorizationByEmail(email, authorizationType);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional("consumer")
	public Authorization findPrimaryAuthByUId(String uid, AuthorizationType authType) throws UserNotFoundException, AuthorizationException {
		
		String method = "findPrimaryAuthByUId():  ";
		logger.debug(method + " called with uid: " + uid + " and authType: " + authType); 
		Authorization auth = authorizationDao.findPrimaryAuthorizationByUid(uid, authType);
		if (null != auth) {
			logger.debug(method + "Found primary authorization for UID '" + uid + "'.");
			return auth;
		}
		
		String msg = "No authorizations were found for user with UID '" + uid + "'.";
		logger.info(method + msg);
		throw new AuthorizationException(msg);
	}

	/**
	 * {@inheritDoc}
	 * @throws DuplicateEmailAddressException 
	 */
	@Transactional("consumer")
	public Authorization findPrimaryAuthByEmail(String email, AuthorizationType authType) throws UserNotFoundException, DuplicateEmailAddressException {
		
		String method = "findPrimaryAuthByEmail():  ";
		logger.debug(method + " called with email: " + email + " and authType: " + authType); 
		return authorizationDao.findPrimaryAuthorizationByEmail(email, authType);
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.framework.oauth.service.AuthorizationService#findByPenDisplayId(java.lang.String, com.livescribe.aws.login.util.AuthorizationType)
	 */
	@Transactional("consumer")
	public Authorization findByPenDisplayId(String displayId, AuthorizationType authorizationType) throws RegistrationNotFoundException, AuthorizationException, UserNotFoundException, AFPException {
		
		PenID penId = new PenID(displayId);
		long id = penId.getId();
		String sn = String.valueOf(id);
		return findByPenSerialNumber(sn, authorizationType);
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.framework.oauth.service.AuthorizationService#findByPenSerialNumber(java.lang.String, com.livescribe.aws.login.util.AuthorizationType)
	 */
	@Transactional("consumer")
	public Authorization findByPenSerialNumber(String serialNumber, AuthorizationType authorizationType) 
			throws RegistrationNotFoundException, AuthorizationException, UserNotFoundException {
		
		String method = "findByPenSerialNumber():  ";
		
		// find if the pen is registered
		RegisteredDevice rd = registeredDeviceDao.findBySerialNumber(serialNumber);
		if (rd == null) {
			String msg = "Pen '" + serialNumber + "' is not registered yet.";
			logger.info(method + msg);
			throw new RegistrationNotFoundException(msg);
		}
		
		// Find the registered user of this pen
		User user = rd.getUser();
		if (user == null) {
			String msg = "Registered device '" + serialNumber + "' was found, but no user was associated to it.";
			logger.error(method + msg);
			throw new UserNotFoundException();
		}
		logger.debug(method + "Found device '" + rd.getDeviceSerialNumber() + "' registered to '" + user.getPrimaryEmail() + "'.");
		
		// Find the 'primary' authorization of this user
		Authorization auth = authorizationDao.findPrimaryAuthorizationByUid(user.getUid(), authorizationType);
		if (null != auth) {
			return auth;
		}

		String msg = "No authorizations were found for user with email '" + user.getPrimaryEmail() + "'.";
		logger.warn(method + msg);
		throw new AuthorizationException(msg);
	}
	


	@Transactional("consumer")
	public Authorization findPrimaryAuthByLoginToken(String loginToken) throws UserNotLoggedInException, AuthorizationException {
		
		String method = "findByLoginToken():  ";
		
		List<Authenticated> authenticated = authenticatedDao.findByLoginToken(loginToken, null);
		
		if ((authenticated == null) || (authenticated.isEmpty())) {
			String msg = "Login token '" + loginToken + "' not found.";
			logger.info(method + msg);
			throw new UserNotLoggedInException(msg);
		}
		
		Authenticated auth = authenticated.get(0);
		User user = auth.getUser();
		Set<Authorization> authorizations = user.getAuthorizations();
		
		if ((authorizations == null) || (authorizations.isEmpty())) {
			String msg = "The user '" + user.getPrimaryEmail() + "' is not authorized with Evernote.";
			logger.info(method + msg);
			throw new AuthorizationException(msg);
		}
		
		Iterator<Authorization> authIter = authorizations.iterator();
		while (authIter.hasNext()) {
			Authorization authorization = authIter.next();
			String provider = authorization.getProvider();
			logger.debug(method + "Provider = '" + provider + "', " + "isPrimary = " + authorization.getIsPrimary());
			if (provider.equals(AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN.getCode())
					&& authorization.getIsPrimary()) {
				logger.debug(method + "Found the Primary authorization for user '" + user.getPrimaryEmail() + "'.");
				return authorization;
			}
		}
		
		String msg = "No authorizations were found for user '" + user.getPrimaryEmail() + "'.";
		logger.info(method + msg);
		throw new AuthorizationException(msg);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional("consumer")
	public Authorization findAuthByUIDAndProviderUserId(String uid, Long providerUserId, AuthorizationType authType)
			throws UserNotFoundException, ServerException, AuthorizationException {

		String method = "findAuthByUIDAndProviderUserId():  ";
		logger.debug(method + "called with uid: " + uid + ", provider userId: " + providerUserId + ", provider: " + authType.getCode());
		
		Authorization auth = authorizationDao.findAuthorizationsByUidAndProviderUid(uid, providerUserId, authType);
		if (null != auth) {
			
			//	Force Hibernate to load the entire graph of objects prior to
			//	returning it.
			Hibernate.initialize(auth);
			
			logger.debug("Found a matching authorization, having its id: " + auth.getAuthorizationId());
			return auth;
		}

		String msg = "No authorizations were found for uid: " + uid + ", provider userId: " + providerUserId + ", provider: " + authType.getCode();
		logger.info(method + msg);
		throw new AuthorizationException(msg);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional("consumer")
	public Authorization saveEvernoteAuthorization(User lsUser, com.evernote.edam.type.User evernoteUser, String accessToken, Date expirationDate) throws UserNotFoundException, DuplicateEmailAddressException {
		
		logger.debug("saveEvernoteAuthorization called");
		
		// Find if the user already had an authorization with Evernote
		List<Authorization> authorizations = authorizationDao.findAuthorizations(lsUser.getPrimaryEmail(), AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		if (null == authorizations || authorizations.isEmpty()) {
			
			// First time authorization for this user with EN.. create a new Authorization record.. (And make it primary!)
			logger.info("No prior authorizations found for this user, creating a new one and since it is the only one, it will be set as primary..");
			return createEvernoteAuthorization(lsUser, accessToken, evernoteUser, expirationDate, true);
			
		} else {

			logger.debug("REAUTHORIZING - At least one existing authorization must be found for this user..");
			String uid = lsUser.getUid();
			Long enUserIDLong = new Long(evernoteUser.getId());
			Authorization auth = null;
			for (Authorization currentAuth : authorizations) {
				logger.debug("Current Auth has uid: " + currentAuth.getUser().getUid() + ", enUserId(int value): " + currentAuth.getEnUserId().intValue());
				if (uid.equals(currentAuth.getUser().getUid()) && 
						(enUserIDLong.equals(currentAuth.getEnUserId())) && 
						EVERNOTE.equalsIgnoreCase(currentAuth.getProvider())) {
					// Match Found..
					logger.debug("Found the existing auth that needs to be updated..");
					auth = currentAuth;
					break;
				}
			}
			
			if (null != auth) {
				// User is Re-Authorizing by an existing EN account.. just update the record with the new token and the new expiration date
				logger.debug("REAUTHORIZING - User is reauthorizing with an existing EN account: " + auth.getEnUsername());
				auth.setOauthAccessToken(accessToken);
				auth.setExpiration(expirationDate);
				auth.setLastModified(new Date());
				auth.setLastModifiedBy("Login Service");
				authorizationDao.merge(auth);
			} else {
				// Re-authorizing by different EN account this time, add a new record (but don't make it primary yet)
				logger.debug("REAUTHORIZING - User is reauthorizing with a new EN account: " + evernoteUser.getUsername());
				auth = createEvernoteAuthorization(lsUser, accessToken, evernoteUser, expirationDate, false);
				logger.debug("A new (NON-Primary) authorization record created with EN account: " + evernoteUser.getUsername());
			}
			
			// Ensure that this user has got "at-least one valid, primary" authorization. If none yet, then make this auth (saved above) as the primary auth..
			Set<Authorization> latestAuthsForThisUser = lsUser.getAuthorizations();
			if (!latestAuthsForThisUser.contains(auth)) {
				latestAuthsForThisUser.add(auth);
			}
			if (null == findPrimaryValidAuth(latestAuthsForThisUser)) {
				for (Authorization authorization : latestAuthsForThisUser) {
					if (authorization.getAuthorizationId().equals(auth.getAuthorizationId())) {
						// make it primary
						authorization.setIsPrimary(true);
						authorizationDao.merge(authorization);
					} else {
						// Toggle, if this is existing primary
						if (authorization.getIsPrimary()) {
							authorization.setIsPrimary(false);
							authorizationDao.merge(authorization);
						}
					}
				}
			}
			return auth;
		}
	}

	

	@Deprecated
	@Transactional("consumer")
	public Authorization saveAuthorization(String email, String accessToken, String enUserName, int enUserId, String enShardId, AuthorizationType authorizationType, Date expirationDate) throws UserNotFoundException, AuthorizationException, DuplicateEmailAddressException {
				
		// Find if the user already had authorization with the specified AuthorizationType
		Authorization auth = authorizationDao.findPrimaryAuthorizationByEmail(email, authorizationType);
		
		if (auth == null) {
			// Verify user exists
			User user = null;
			try {
				user = userDao.findByEmail(email);
				
				if (user == null) {
					throw new UserNotFoundException("Email '" + email + "' does not exist.");
				}
			} catch (DuplicateEmailAddressException e) {
				throw new AuthorizationException("Duplicate email found: " + email);
			}

			// Create new Authorization entity
			auth = new Authorization();
			auth.setUser(user);
			auth.setOauthAccessToken(accessToken);
			auth.setEnUsername(enUserName);
			auth.setEnUserId(new Long(enUserId));
			auth.setEnShardId(enShardId);
			auth.setProvider(authorizationType.getCode());
			auth.setExpiration(expirationDate);
			auth.setCreated(new Date());
			auth.setLastModified(new Date());
			auth.setLastModifiedBy("Login Service");
			auth.setIsPrimary(true);
			authorizationDao.persist(auth);
			
		} else {
			// update the existing Authorization with new accessToken and expirationDate
			auth.setOauthAccessToken(accessToken);
			auth.setEnUsername(enUserName);
			auth.setEnUserId(new Long(enUserId));
			auth.setEnShardId(enShardId);
			auth.setExpiration(expirationDate);
			auth.setLastModified(new Date());
			
			authorizationDao.merge(auth);
		}
		
		return auth;
	}
	
	@Transactional("consumer")
	public PremiumCode saveEvernotePremiumCode(User user, String code) {
		Date date = new Date();
		
		PremiumCode pc = new PremiumCode();
		pc.setUser(user);
		pc.setEnCode(code);
		pc.setCreated(date);
		pc.setLastModified(date);
		pc.setLastModifiedBy("Login Service");
		
		premiumCodeDao.persist(pc);
		
		return pc;
	}
	
	public boolean isAuthorizationExpired(Authorization authorization) {
		if (authorization == null) {
			return true;
		}

		try {
			if (ResponseCode.SUCCESS.equals(evernoteProxy.validateEvernoteAuth(authorization))) {
				return false; // This is a valid auth, NOT expired!
			}
		} catch (EDAMUserException e) {
			logger.fatal("Caught EDAMUserException: " + e.getErrorCode());
			//If this OAuth token has expired ...
			if (EDAMErrorCode.AUTH_EXPIRED.equals(e.getErrorCode())) {
				logger.info("Evernote auth for LS user [" + authorization.getUser().getPrimaryEmail() + "] and enUserName [" + authorization.getEnUsername() + "] is expired!");
				return true;
			} else {
				logger.info("Unable to validate the given Evernote OAuth!");
			}
		} catch (EDAMSystemException e) {
			logger.error("Caught EDAMSystemException! Unable to validate the given Evernote OAuth!", e);
		} catch (TException e) {
			logger.error("Caught TException! Unable to validate the given Evernote OAuth!", e);
		}
		return false; // If unable to validate for any reasons, treat it as not-expired!
	}

	/**
	 * The user is considered 'authorized' if one Valid Primary EN authorization is found for this user.
	 * 
	 */
	@Transactional("consumer")
	public boolean isAuthorized(String loginToken) throws UserNotLoggedInException {
		
		String method = "isAuthorized():  ";
		
		logger.debug(method + "Locating 'authenticated' record with token '" + loginToken + "'.");
		
		List<Authenticated> authenticated = authenticatedDao.findByLoginToken(loginToken, null);
		if ((authenticated == null) || (authenticated.isEmpty())) {
			String msg = "Login token '" + loginToken + "' not found.";
			logger.info(method + msg);
			throw new UserNotLoggedInException(msg);
		}

		if (null == findPrimaryValidAuth(authenticated.get(0).getUser().getAuthorizations())) {
			return false;
		}
		return true;
	}

	/**
	 * @Override
	 */
	@Transactional("consumer")
	public List<User> findUsersByProviderUserId(String userId, AuthorizationType provider) throws AuthorizationException, UserNotFoundException {
		String method = "findUsersByProviderUserId():\t";
		//Find Authorization by provider/AuthorizationType and en_user_id
		logger.debug("Finding Authorization with provider'" + provider.getCode() + "' and en_user_id '" + userId + "'");
		Authorization authToFindByExample = new Authorization();
		authToFindByExample.setProvider(provider.getCode());
		authToFindByExample.setEnUserId(Long.parseLong(userId));
		List<Authorization> authList = authorizationDao.findByExample(authToFindByExample);
		
		if ((authList == null) || (authList.isEmpty())) {
			String msg = "No authorizations found for provider '" + provider.getCode() + "' and en_user_id '" + userId + "'";
			logger.info(method + msg);
			throw new AuthorizationException(msg);
		}
		
		logger.debug(method + "Found authorization(s) for provider'" + provider.getCode() + "' and en_user_id '" + userId + "'");

		List<User> users = new ArrayList<User>();
		for (Authorization auth : authList) {

			User anAuthUser = auth.getUser();
			//Need to initialize any/all lazily ref'd relations now (before session goes away)
			Hibernate.initialize(anAuthUser);
			Hibernate.initialize(anAuthUser.getAuthenticateds());
			Hibernate.initialize(anAuthUser.getAuthorizations());
			Hibernate.initialize(anAuthUser.getPremiumCodes());
			Hibernate.initialize(anAuthUser.getRegisteredDevices());
			Hibernate.initialize(anAuthUser.getUserSettings());
			users.add(anAuthUser);
		}
		
		if (users.isEmpty()) {
			String msg = method + "No user(s) found for provider'" + provider.getCode() + "' and en_user_id '" + userId + "': " + userId;
			logger.info(method + msg);
			throw new UserNotFoundException(msg); 
		}
		logger.debug(method + "Total # of user(s) found for provider'" + provider.getCode() + "' and en_user_id '" + userId + "': " + users.size());
		return users;
	}

	/**
	 * @throws DuplicateEmailAddressException 
	 * @throws UserNotFoundException 
	 * @Override
	 */
	@Transactional("consumer")
	public List<AuthorizationDto> findAuthorizationListByLoginToken(String loginToken) throws UserNotLoggedInException, AuthorizationException, UserNotFoundException {
		
		String method = "findAuthorizationListByLoginToken( " + loginToken + " ):  ";
		logger.debug(method + "	called");
		
		User user = findUserByLoginToken(loginToken);
		if (null == user) {
			String msg = "No user found by the login token " + loginToken;
			logger.info(method + msg);
			throw new UserNotFoundException(msg);
		}
		
		Set<Authorization> authorizations = user.getAuthorizations();
		if ((authorizations == null) || (authorizations.isEmpty())) {
			String msg = "The user '" + user.getPrimaryEmail() + "' is not authorized with Evernote.";
			logger.info(method + msg);
			throw new AuthorizationException(msg);
		}  else {
			logger.debug(method + authorizations.size() + " authorization(s) found. Buliding the DTO list now..");
			List<AuthorizationDto> dtoList = new ArrayList<AuthorizationDto>();
			for (Authorization auth : authorizations) {
				logger.debug(method + "	EnUserName: " + auth.getEnUsername() + ", Expiration Date: " + auth.getExpiration());
				if (null != auth.getExpiration() && isAuthorizationExpired(auth)) {
					auth.setExpiration(null);
					authorizationDao.merge(auth); // update the local database to indicate it is expired..
				}
				dtoList.add(new AuthorizationDto(auth));
			}
			return dtoList;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional("consumer")
	public List<AuthorizationDto> findAuthorizationListByProviderUserId(String providerUserId, AuthorizationType authorizationType) {

		String method = "findAuthorizationListByProviderUserId( " + providerUserId + ", " + authorizationType.getCode() + " ):  ";
		
		List<Authorization> authList = authorizationDao.findAuthorizationsByProviderUid(providerUserId, authorizationType);
		if (null == authList || authList.isEmpty()) {
			logger.info(method + "No authorization record was found for given parameters");
			return null;
		} else {
			logger.debug(method + authList.size() + " authorization(s) found. Buliding the DTO list now..");
			List<AuthorizationDto> dtoList = new ArrayList<AuthorizationDto>();
			for (Authorization auth : authList) {
				dtoList.add(new AuthorizationDto(auth));
			}
			return dtoList;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional("consumer")
	public void switchPrimaryEvernoteAccount(String loginToken, String enUserName) throws UserNotLoggedInException, AuthorizationException, AuthorizationNotFoundException, AuthorizationExpiredException {		
		String method = "switchPrimaryEvernoteAccount( ):  ";
		logger.debug(method + "	called for enUserName: " + enUserName);
		
		User user = findUserByLoginToken(loginToken);
		Set<Authorization> authorizations = user.getAuthorizations();
		if ((authorizations == null) || (authorizations.isEmpty())) {
			String msg = "The user '" + user.getPrimaryEmail() + "' is not authorized with Evernote.";
			logger.info(method + msg);
			throw new AuthorizationException(msg);
		}
		
		Authorization toBePrimaryAuth = null;
		
		// Find the EN auth by the given enUserName
		for (Authorization auth : authorizations) {
			if (EVERNOTE.equalsIgnoreCase(auth.getProvider()) && enUserName.equalsIgnoreCase(auth.getEnUsername())) {
				toBePrimaryAuth = auth;
				break;
			}
		}
		
		// if not found, throw exception
		if (null == toBePrimaryAuth) {
			String msg = "Evernote authorization by username " + enUserName + " for the user " + user.getPrimaryEmail() + " NOT FOUND!!";
			logger.info(method + msg);
			throw new AuthorizationNotFoundException(msg);
		}
		
		// if expired, throw exception
		if (toBePrimaryAuth.getExpiration() == null || toBePrimaryAuth.getExpiration().before(new Date())) {
			String msg = "Evernote authorization by username " + enUserName + " for the user " + user.getPrimaryEmail() + " is EXPIRED!!";
			logger.info(method + msg);
			AuthorizationExpiredException ex = new AuthorizationExpiredException(msg);
			ex.setExpiredOn(toBePrimaryAuth.getExpiration());
			ex.setEnUserName(toBePrimaryAuth.getEnUsername());
			ex.setEnUserId(toBePrimaryAuth.getEnUserId());
			if (null != toBePrimaryAuth.getUser()) {
				ex.setLsUserId(toBePrimaryAuth.getUser().getUserId());
			}
			throw ex;
		}
		
		// Make the switch
		for (Authorization auth : authorizations) {
			if (EVERNOTE.equalsIgnoreCase(auth.getProvider()) && enUserName.equalsIgnoreCase(auth.getEnUsername())) {
				// make it primary
				logger.debug("Found the to be priamry auth");
				auth.setIsPrimary(true);
				authorizationDao.merge(auth);
			} else {
				// Toggle, if this is existing primary
				if (auth.getIsPrimary()) {
					auth.setIsPrimary(false);
					authorizationDao.merge(auth);
				}
			}
		}
		
		logger.debug(method + "Primary account switching completed for this user to the enUserName: " + enUserName);
	}


	/* (non-Javadoc)
	 * @see com.livescribe.framework.oauth.service.AuthorizationService#findAuthorizationsByUid(java.lang.String, com.livescribe.aws.login.util.AuthorizationType)
	 */
	@Transactional("consumer")
	public List<AuthorizationDto> findAuthorizationsByUid(String uid,
			AuthorizationType authorizationType) {

		ArrayList<AuthorizationDto> list = new ArrayList<AuthorizationDto>();
		
		Set<Authorization> authSet = authorizationDao.findByUid(uid);
		
		//	If no Authorizations are found, return an empty List. 
		if ((authSet == null) || (authSet.isEmpty())) {
			return list;
		}
		
		//	Convert the Set into a List.
		for (Authorization auth : authSet) {
			AuthorizationDto authDto = new AuthorizationDto(auth);
			list.add(authDto);
		}
		
		return list;
	}
	
	/* ************************************************************************************************************************
	 * 
	 * 							HELPER METHODS
	 * 
	 * ************************************************************************************************************************ */


	private User findUserByLoginToken(String loginToken) throws UserNotLoggedInException {
		
		String method = "findUserByLoginToken( " + loginToken + " ):  ";
		logger.debug(method + "	called");
		
		List<Authenticated> authenticated = authenticatedDao.findByLoginToken(loginToken, null);
		
		if ((authenticated == null) || (authenticated.isEmpty())) {
			String msg = "Login token '" + loginToken + "' not found.";
			logger.info(method + msg);
			throw new UserNotLoggedInException(msg);
		}
		
		Authenticated auth = authenticated.get(0);
		User user = auth.getUser();
		logger.debug(method + "	Found the user with email: " + user.getPrimaryEmail());
		return user;
	}

	private Authorization createEvernoteAuthorization(User user, String accessToken, com.evernote.edam.type.User evernoteUser, Date expirationDate, Boolean isPrimary) {

		// Create new Authorization entity
		Authorization auth = new Authorization();
		auth.setUser(user);
		auth.setOauthAccessToken(accessToken);
		auth.setEnUsername(evernoteUser.getUsername());
		auth.setEnUserId(new Long(evernoteUser.getId()));
		auth.setEnShardId(null == evernoteUser.getShardId() ? "" : evernoteUser.getShardId());
		auth.setProvider(AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN.getCode());
		auth.setExpiration(expirationDate);
		auth.setCreated(new Date());
		auth.setLastModified(new Date());
		auth.setLastModifiedBy("Login Service");
		auth.setIsPrimary(isPrimary);
		authorizationDao.persist(auth);
		return auth;
	}
	
	private Authorization findPrimaryValidAuth(Collection<Authorization> authorizations) {
		String method = "findPrimaryValidAuth():  ";

		// Primary-Valid = Primary + Non-Expired + Non-Blank Token + Non-Blank Shard ID
		for (Authorization authorization : authorizations) {			
			if (!isAuthorizationExpired(authorization) && 
					authorization.getIsPrimary() &&
					!StringUtils.isBlank(authorization.getOauthAccessToken()) &&
					!StringUtils.isBlank(authorization.getEnShardId())) {
				logger.debug(method + "Found the valid primary EN authorization: [id=" + authorization.getAuthorizationId() + "]");
				return authorization;
			}
		}
		logger.debug(method + "Could not find a single valid primary EN authorization for the user!");
		return null;
	}
}

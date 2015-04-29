package com.livescribe.framework.orm.consumer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.aws.login.util.AuthorizationType;
import com.livescribe.framework.exception.DuplicateEmailAddressException;
import com.livescribe.framework.exception.UserNotFoundException;

/**
 * Custom DAO class to access the AUTHORIZATION table data.
 * 
 * @author Mohammad M. Naqvi
 * @author kle
 */
public class CustomAuthorizationDao extends AuthorizationDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private CustomUserDao userDao;
	
	public CustomAuthorizationDao() {
		super();
	}
	
	/**
	 * <p>Returns a <code>Set</code> of <code>Authorization</code> objects
	 * belonging to the Livescribe user identified by the given UID.</p>
	 * 
	 * @param uid The unique UID of the Livescribe user from the 
	 * <code>consumer.user</code> table.
	 * 
	 * @return a <code>Set</code> of <code>Authorization</code> objects.
	 * &nbsp;&nbsp;Returns an empty <code>Set</code> if no authorizations 
	 * are found.
	 */
	public Set<Authorization> findByUid(String uid) {
		
		User user = userDao.findByUID(uid);
		if (user == null) {
			return new HashSet<Authorization>();
		}
		Hibernate.initialize(user);
		Set<Authorization> authSet = user.getAuthorizations();
		
		return authSet;
	}
	
	/**
	 * <p>Returns the <b>primary</p> authorization of the user by given email and the provider type. Note that a user may have multiple authorizations for the same 
	 * provider type, but only one of them must be 'primary'. Hence the method name contains 'Primary'.
	 * 
	 * @param email email of the user
	 * @param authorizationType provider type
	 * @return the primary authorization of this user for the given provider type. If none found, returns null.
	 * @throws com.livescribe.framework.exception.UserNotFoundException if no user was found by given email in the database.
	 * @throws DuplicateEmailAddressException if multiple users found by the given email.
	 */
	public Authorization findPrimaryAuthorizationByEmail(String email, AuthorizationType authorizationType) throws UserNotFoundException, DuplicateEmailAddressException {
		logger.debug("findPrimaryAuthorizationByEmail() called with email: " + email + ", and provider type: " + authorizationType); 
		
		User user = null;
		user = userDao.findByEmail(email);

		if (user == null) {
			throw new UserNotFoundException("Email '" + email + "' not found.");
		}
		return queryPrimaryAuthByUserAndProvider(user, authorizationType.getCode());
	}
	
	/**
	 * <p>Finds the <b>Primary</b> authorization for given uid and the provider.</p
	 * 
	 * @param uid uid of the user
	 * @param authorizationType provider type
	 * @return the primary authorization of this user
	 * @throws com.livescribe.framework.exception.UserNotFoundException if no user was found by given uid.
	 */
	public Authorization findPrimaryAuthorizationByUid(String uid, AuthorizationType authorizationType) throws UserNotFoundException {
		logger.debug("findPrimaryAuthorizationByUid() called with uid: " + uid + ", provider: " + authorizationType.getCode());
		
		User user = userDao.findByUID(uid);
		if (user == null) {
			throw new UserNotFoundException("user with uid '" + uid + "' not found.");
		}

		return queryPrimaryAuthByUserAndProvider(user, authorizationType.getCode());
	}

	/**
	 * <p></p>
	 * 
	 * @param oauthAccessToken
	 * @return
	 */
	public Authorization findByOAuthToken(String oauthAccessToken, AuthorizationType authorizationType) {

		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("FROM Authorization a WHERE a.oauthAccessToken = :oauthAccessToken AND a.provider = :provider");
		q.setString("oauthAccessToken", oauthAccessToken);
		q.setString("provider", authorizationType.getCode());
		
		@SuppressWarnings("unchecked")
		List<Authorization> result = q.list();
		
		if (result.size() == 0) {
			return null;
		}
		
		return result.get(0);
		
	}

	/**
	 * <p>Finds the authorization by given uid and provider user id.</p>
	 * 
	 * @param uid uid
	 * @param providerUserId provider's user id
	 * @param authorizationType provider type
	 * @return authorization matching the given parameters
	 * @throws UserNotFoundException if no user was found by given uid.
	 */
	public Authorization findAuthorizationsByUidAndProviderUid(String uid, Long providerUserId, AuthorizationType authorizationType) throws com.livescribe.framework.exception.UserNotFoundException {
		logger.debug("findAuthorizationsByUidAndProviderUid() called with uid: " + uid + ", provider userId: " + providerUserId + ", provider: " + authorizationType.getCode());
		
		User user = userDao.findByUID(uid);
		if (user == null) {
			throw new UserNotFoundException("user with uid '" + uid + "' not found.");
		}
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("FROM Authorization a WHERE a.user = :user AND a.enUserId = :providerUserId AND a.provider = :provider");
		q.setEntity("user", user);
		q.setLong("providerUserId", providerUserId);
		q.setString("provider", authorizationType.getCode());
		
		@SuppressWarnings("unchecked")
		List<Authorization> result = q.list();
		
		if (result.size() == 0) {
			return null;
		}
		
		return result.get(0);
	}

	/**
	 * <p>Finds the authorization list by given provider user id.</p>
	 * 
	 * @param providerUserId provider's user id
	 * @param authorizationType provider type
	 * @return authorization matching the given parameters
	 * @throws UserNotFoundException if no user was found by given uid.
	 */
	@SuppressWarnings("unchecked")
	public List<Authorization> findAuthorizationsByProviderUid(String providerUserId, AuthorizationType authorizationType) {
		logger.debug("findAuthorizationsByProviderUid() called with provider userId: " + providerUserId + ", provider: " + authorizationType.getCode());
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("FROM Authorization a WHERE a.enUserId = :providerUserId AND a.provider = :provider");
		q.setLong("providerUserId", Long.parseLong(providerUserId));
		q.setString("provider", authorizationType.getCode());

		return q.list();
	}	

	/**
	 * <p>Finds all authorizations of a given Livescribe user for given auth types</p
	 * >
	 * @param email primary email of the livescribe user.
	 * @param authorizationType auth type (such as 'EN', 'FB' etc..)
	 * @return list of matching authorizations
	 * @throws DuplicateEmailAddressException if more than one users found with given email.
	 */
	public List<Authorization> findAuthorizations(String email, AuthorizationType authorizationType) throws com.livescribe.framework.exception.UserNotFoundException, DuplicateEmailAddressException {
		logger.debug("findAuthorizations() called for email: " + email + " and authType: " + authorizationType);
		
		User user = null;
		user = userDao.findByEmail(email);
		
		if (user == null) {
			throw new UserNotFoundException("Email '" + email + "' not found.");
		}
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("FROM Authorization a WHERE a.user = :user AND a.provider = :provider");
		q.setEntity("user", user);
		q.setString("provider", authorizationType.getCode());

		@SuppressWarnings("unchecked")
		List<Authorization> result = q.list();		
		return result;
	}
	
	/**
	 * <p>Resets the "isPrimary" column of Authorization to zero for given authorizations.</p
	 * 
	 * @param authIdList list of auth id's
	 */
	public void updateAuthorizationsToSecondary(List<Long> authIdList) {
		logger.debug("updateAuthorizationsToSecondary() called ");
		
		if (null == authIdList || authIdList.isEmpty()) {
			return;
		}
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("UPDATE Authorization SET isPrimary = 0 WHERE authorizationId IN (:authIdList)");
		q.setParameterList("authIdList", authIdList);
		int noOfRowsUpdated = q.executeUpdate();
		logger.debug("Total # of records updated: " + noOfRowsUpdated);
	}
	

	/*	******************************************************************************************************************************* */
	/* 																HELPER METHODS														*/
	/*	*******************************************************************************************************************************	*/
	private Authorization queryPrimaryAuthByUserAndProvider(User user, String code) {
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("FROM Authorization a WHERE a.user = :user AND a.provider = :provider and a.isPrimary = 1");
		q.setEntity("user", user);
		q.setString("provider", code);
	
		@SuppressWarnings("unchecked")
		List<Authorization> queryResult = q.list();
		if (null == queryResult || queryResult.size() == 0) {
			return null;
		}
		
		// There may not be more than one primary authorization for a given user for a given provider.
		return queryResult.get(0);
	}
	
}

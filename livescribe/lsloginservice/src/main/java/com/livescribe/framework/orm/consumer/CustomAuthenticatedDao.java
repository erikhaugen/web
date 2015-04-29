/**
 * 
 */
package com.livescribe.framework.orm.consumer;

import java.util.List;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>Adds methods to look up <code>Authenticated</code> records.</p>
 * 
 * These methods are in addition to those in <code>AuthenticatedDao</code>.
 * 
 * @see com.livescribe.framework.orm.consumer.AuthenticatedDao.
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CustomAuthenticatedDao extends AuthenticatedDao {

	/**
	 * <p>Default class constructor.</p>
	 *
	 */
	public CustomAuthenticatedDao() {
		super();
	}

	/**
	 * <p>Returns a <code>List</code> of logged in sessions for a 
	 * <code>User</code>.</p>
	 * 
	 * @param userId The ID of the <code>User</code> to search on.
	 * 
	 * @return a <code>List</code> of logged in sessions for a 
	 * <code>User</code>.
	 */
	public List<Authenticated> findByUserId(long userId) {
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("FROM Authenticated a WHERE a.user.userId = :userId");
		q.setLong("userId", userId);
		
		@SuppressWarnings("unchecked")
		List<Authenticated> list = q.list();
		
		return list;
	}
	
	/**
	 * <p>Returns a <code>List</code> of login records using a given login token.</p>
	 * 
	 * If <code>loginDomain</code> is <code>null</code>, any and all records will
	 * be returned.  It will be up to the caller to determine what to do with
	 * them.
	 * 
	 * @param loginToken The token to use in looking up login records.
	 * @param loginDomain (Optional) The point where the user logged in.  Can be WEB, LD, ML, EN, TEST.
	 * 
	 * @return a <code>List</code> of login records using a given login token.
	 */
	@Transactional("consumer")
	public List<Authenticated> findByLoginToken(String loginToken, String loginDomain) {
		
		String method = "findByLoginToken():  ";
		
		Query q = null;
		if ((loginDomain == null) || ("".equals(loginDomain))) {
			logger.debug(method + "Locating record without 'loginDomain'.");
			q = sessionFactoryConsumer.getCurrentSession().createQuery("FROM Authenticated a WHERE a.loginToken = :loginToken");
			q.setString("loginToken", loginToken);
		} else {
			logger.debug(method + "Locating record with login domain:  " + loginDomain + ".");
			q = sessionFactoryConsumer.getCurrentSession().createQuery("FROM Authenticated a WHERE a.loginToken = :loginToken and a.loginDomain = :loginDomain");
			q.setString("loginToken", loginToken);
			q.setString("loginDomain", loginDomain);
		}
		
		@SuppressWarnings("unchecked")
		List<Authenticated> list = q.list();
		
		return list;
	}
	
	/**
	 * <p>Returns a <code>List</code> of login records using userId and login domain</p>
	 * 
	 * @param userId
	 * @param loginDomain
	 * @return
	 */
	@Transactional("consumer")
	public List<Authenticated> findByUserIdAndLoginDomain(long userId, String loginDomain) {
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("FROM Authenticated a WHERE a.user.userId = :userId AND a.loginDomain = :loginDomain");
		q.setLong("userId", userId);
		q.setString("loginDomain", loginDomain);
		
		@SuppressWarnings("unchecked")
		List<Authenticated> list = q.list();
		
		return list;
	}
}

package com.livescribe.aws.tokensvc.orm.consumer;

import java.util.List;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.framework.orm.consumer.Authenticated;
import com.livescribe.framework.orm.consumer.AuthenticatedDao;

public class CustomAuthenticatedDao extends AuthenticatedDao {

	/**
	 * <p>Default class constructor.</p>
	 *
	 */
	public CustomAuthenticatedDao() {
		super();
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
		}
		//	There WAS a case where this was needed.  It is left here in case it needs to be re-implemented.  [KFM]
		else if ("ML".equals(loginDomain)) {
			q = sessionFactoryConsumer.getCurrentSession().createQuery("FROM Authenticated a WHERE a.loginToken = :loginToken ");
			q.setString("loginToken", loginToken);
		}
		else {
			logger.debug(method + "Locating record with login domain:  " + loginDomain + ".");
			q = sessionFactoryConsumer.getCurrentSession().createQuery("FROM Authenticated a WHERE a.loginToken = :loginToken and a.loginDomain = :loginDomain");
			q.setString("loginToken", loginToken);
			q.setString("loginDomain", loginDomain);
		}
		
		List<Authenticated> list = q.list();
		
		return list;
	}
}

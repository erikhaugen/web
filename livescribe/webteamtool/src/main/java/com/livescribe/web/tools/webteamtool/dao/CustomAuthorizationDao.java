/**
 * Created:  Sep 20, 2013 5:40:40 PM
 */
package com.livescribe.web.tools.webteamtool.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.AuthorizationDao;
import com.livescribe.framework.orm.consumer.User;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CustomAuthorizationDao extends AuthorizationDao {

	private static Logger logger = Logger.getLogger(CustomAuthorizationDao.class);

	/**
	 * <p></p>
	 * 
	 */
	public CustomAuthorizationDao() {
		super();
	}

	/**
	 * <p></p>
	 * 
	 * @param userId The primary key of the <code>user</code> record.
	 * 
	 * @return
	 * 
	 * @throws MultipleRecordsFoundException
	 */
	@Deprecated
	public Authorization findByUser(User user) throws MultipleRecordsFoundException {
		
		Criteria crit = this.sessionFactoryConsumer.getCurrentSession().createCriteria(Authorization.class);
		crit.add(Restrictions.eq("user", user));
		List<Authorization> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			return null;
		}
		
		if (list.size() > 1) {
			throw new MultipleRecordsFoundException();
		}
		
		return list.get(0);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param userId
	 * @return
	 */
	public List<Authorization> findByUserId(Long userId) {
		
		Criteria crit = this.sessionFactoryConsumer.getCurrentSession().createCriteria(Authorization.class);
		crit.add(Restrictions.eq("userId", userId));
		List<Authorization> list = crit.list();
		
		return list;
	}

	/**
	 * <p>Returns the <code>Authorization</code> of the primary Evernote 
	 * account for the given user.</p>
	 * 
	 * @param user The <code>User</code> to use in the search.
	 * 
	 * @return an <code>Authorization</code> object, or <code>null</code> if 
	 * none was found.
	 * 
	 * @throws MultipleRecordsFoundException
	 */
	public Authorization findPrimaryAuthorizationByUser(User user) throws MultipleRecordsFoundException {
		
		Criteria crit = this.sessionFactoryConsumer.getCurrentSession().createCriteria(Authorization.class);
		crit.add(Restrictions.eq("user", user));
		@SuppressWarnings("unchecked")
		List<Authorization> list = crit.list();
		
		if (list == null) {
			return null;
		}
		
		for (Authorization auth : list) {
			if (auth.getIsPrimary()) {
				return auth;
			}
		}
		return null;
	}
	
}

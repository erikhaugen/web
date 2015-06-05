/**
 * 
 */
package com.livescribe.admin.dao;

import java.util.List;

import org.hibernate.Query;

import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.AuthorizationDao;

/**
 * @author mnaqvi
 *
 */
public class CustomAuthorizationDao extends AuthorizationDao {

	/**
	 * <p>Default class constructor.</p>
	 *
	 */
	public CustomAuthorizationDao() {
		super();
	}

	/**
	 * <p>Returns a list of EN authorizations by the given userId.</p>
	 * 
	 * @param userId the user Id (surrogate key of the User table)
	 * @return  a <code>List</code> of <code>Authorization</code>s.
	 */
	public List<Authorization> findByUserId(Long userId) {
		
		Query q = this.sessionFactoryConsumer.getCurrentSession().createQuery("from Authorization a where a.user.userId = :userId and a.provider = :provider");
		q.setLong("userId", userId);
		q.setString("provider", "EN");
		
		@SuppressWarnings("unchecked")
		List<Authorization> list = q.list();
		
		return list;
	}

	/**
	 * <p>Returns the primary EN authorization for the given userId.</p>
	 * 
	 * @param userId the user Id (surrogate key of the User table)
	 * @return the <bold>primary</bold> <code>Authorization</code>.
	 */
	public Authorization findPrimaryENAuthByUserId(Long userId) {
		
		Query q = this.sessionFactoryConsumer.getCurrentSession().createQuery("from Authorization a where a.user.userId = :userId and a.provider = :provider and a.isPrimary = 1");
		q.setLong("userId", userId);
		q.setString("provider", "EN");
		
		@SuppressWarnings("unchecked")
		List<Authorization> list = q.list();
		if (null == list || list.isEmpty()) {
			return null;
		}
		return list.get(0); // There cannot be more than 1 EN authorizations for a user.
	}

	/**
	 * <p>Returns a list of EN authorizations by the given userId.</p>
	 * 
	 * @param userId the user Id (surrogate key of the User table)
	 * @return  a <code>List</code> of <code>Authorization</code>s.
	 */
	public List<Authorization> findNonPrimaryENAuthsByUserId(Long userId) {
		
		Query q = this.sessionFactoryConsumer.getCurrentSession().createQuery("from Authorization a where a.user.userId = :userId and a.provider = :provider and a.isPrimary = 0");
		q.setLong("userId", userId);
		q.setString("provider", "EN");
		
		@SuppressWarnings("unchecked")
		List<Authorization> list = q.list();
		return list;
	}

}

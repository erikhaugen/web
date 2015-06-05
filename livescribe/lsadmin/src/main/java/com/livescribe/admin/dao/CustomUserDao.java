package com.livescribe.admin.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.livescribe.admin.lookup.LookupCriteria;
import com.livescribe.admin.lookup.LookupCriteriaList;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.consumer.UserDao;
import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;

public class CustomUserDao extends UserDao {

	/**
	 * 
	 * @param criteria
	 * @return
	 */
	public List<User> findByLookupCriteria(LookupCriteria criteria) {
		Query q = criteria.getSQLQuery(sessionFactoryConsumer);
		
		List<User> result = q.list();
		
		return result;
	}
	
	/**
	 * 
	 * @param criteriaList
	 * @return
	 */
	public List<User> findByLookupCriteriaList(LookupCriteriaList criteriaList) {
		Query query = criteriaList.getSQLQuery(sessionFactoryConsumer);
		
		List<User> result = query.list();
		
		return result;
	}
	
	/**
	 * Experiment method
	 * @return
	 */
	public List<User> findByAuthorization() {
		Query query = sessionFactoryConsumer.getCurrentSession().createQuery(
				"SELECT u, a FROM User u, Authorization a WHERE u = a.user AND a.enUsername = :enUsername");
		query.setString("enUsername", "kle");
		
		List result = query.list();
		
		return result;
	}
	
	/**
	 * 
	 * @param email
	 * @return
	 */
	public User findByEmail(String email) {
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("from User u where u.primaryEmail = :email");
		q.setString("email", email.toLowerCase());
		
		List<User> list = q.list();
		
		if ((list == null) || (list.isEmpty())) {
			return null;
		}
		
		return list.get(0);
	}
	/**
	 * <p>Find a registration by partial email</p>
	 * 
	 * @param partialEmail
	 * @return list of matching registration(s)
	 * @throws RegistrationNotFoundException
	 */
	public List<User> findByPartialEmail(String partialEmail) {

		logger.debug("findByPartialEmail() invoked with findByPartialEmail " + partialEmail);
		Criteria crit = sessionFactoryConsumer.getCurrentSession().createCriteria(Registration.class)
				.add(Restrictions.like("email", partialEmail, MatchMode.ANYWHERE))
				.addOrder(Order.desc("created"));
		crit.setMaxResults(200);
		
		@SuppressWarnings("unchecked")
		List<User> list = crit.list();
		logger.debug("findByPartialEmail() for " + partialEmail + " returning " + list.size() + " user(s).");	
		return list;
	}
	
	
	/**
	 * 
	 * @param uid
	 * @return
	 */
	public User findByUid(String uid) {
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("from User u where u.uid = :uid");
		q.setString("uid", uid);
		
		List<User> list = q.list();
		
		if ((list == null) || (list.isEmpty())) {
			return null;
		}
		
		return list.get(0);
	}
}

/*
 * Created:  Sep 21, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.orm.consumer;

import java.util.List;

import org.hibernate.Query;

import com.livescribe.framework.exception.DuplicateEmailAddressException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CustomUserDao extends UserDao {

	/**
	 * <p></p>
	 * 
	 */
	public CustomUserDao() {
	}

	/**
	 * <p></p>
	 * 
	 * @param email
	 * 
	 * @return
	 * 
	 * @throws DuplicateEmailAddressException 
	 */
	public User findByEmail(String email) throws DuplicateEmailAddressException {
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("from User u where u.primaryEmail = :email");
		q.setString("email", email.toLowerCase());
		
		List<User> list = q.list();
		
		if ((list == null) || (list.isEmpty())) {
			return null;
		}
		
		if (list.size() > 1) {
			throw new DuplicateEmailAddressException();
		}
		
		return list.get(0);
	}
	
	/**
	 * <p>Returns the Livescribe user identified by the given UID.</p>
	 * 
	 * @param uid The unique UID of the Livescribe user from the 
	 * <code>consumer.user</code> table.
	 * 
	 * @return the <code>user</code> record.
	 */
	public User findByUID(String uid) {
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("from User u where uid = :uid");
		q.setString("uid", uid);
		
		List<User> list = q.list();
		
		if ((list == null) || (list.isEmpty())) {
			return null;
		}
		
		if (list.size() > 1) {
			logger.error("findByUID():  More than 1 user was found with UID '" + uid + "'!!!");
			//	TODO:  Need to add handling/notification for this case.
		}
		
		return list.get(0);
	}
}

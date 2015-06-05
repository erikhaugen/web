/*
 * Created:  Sep 21, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.orm.consumer;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.livescribe.aws.tokensvc.exception.DuplicateEmailAddressException;
import com.livescribe.aws.tokensvc.exception.DuplicateUidException;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.consumer.UserDao;

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
		q.setString("email", email);
		
		List<User> list = q.list();
		
		if ((list == null) || (list.isEmpty())) {
			logger.debug("No user found with email '" + email + "'.");
			return null;
		}
		
		if (list.size() > 1) {
			logger.debug("Found " + list.size() + " users with email '" + email + "'.");
			throw new DuplicateEmailAddressException();
		}
		
		return list.get(0);
	}
	
	/**
	 * Find User by uid
	 * 
	 * @param uid
	 * @return
	 * @throws DuplicateUidException
	 */
	public User findByUid(String uid) throws DuplicateUidException {
		
		// Open new session
		Session session = sessionFactoryConsumer.openSession();
		
		Query q = session.createQuery("from User u where u.uid = :uid");
		q.setString("uid", uid);
		
		List<User> list = null;
		try {
			list = q.list();
		} finally {
			session.close();
		}
		
		if ((list == null) || (list.isEmpty())) {
			logger.debug("No user found with uid '" + uid + "'.");
			return null;
		}
		
		if (list.size() > 1) {
			logger.debug("Found " + list.size() + " users with uid '" + uid + "'.");
			throw new DuplicateUidException("Duplicated uid '" + uid + "'.");
		}
		
		return list.get(0);
	}
}

/**
 * Created:  Sep 20, 2013 5:34:26 PM
 */
package com.livescribe.web.tools.webteamtool.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.consumer.UserDao;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CustomUserDao extends UserDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public CustomUserDao() {
		super();
	}

	/**
	 * <p></p>
	 * 
	 * @param email
	 * 
	 * @return
	 * 
	 * @throws MultipleRecordsFoundException
	 */
	public User findByEmail(String email) throws MultipleRecordsFoundException {
		
		Criteria crit = this.sessionFactoryConsumer.getCurrentSession().createCriteria(User.class);
		crit.add(Restrictions.eq("primaryEmail", email));
		List<User> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			return null;
		}
		
		if (list.size() > 1) {
			throw new MultipleRecordsFoundException();
		}
		
		return list.get(0);
	}
}

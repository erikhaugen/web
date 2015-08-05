/**
 * Created:  Sep 30, 2013 5:00:02 PM
 */
package com.livescribe.web.tools.webteamtool.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.web.tools.webteamtool.dao.CustomAuthorizationDao;
import com.livescribe.web.tools.webteamtool.dao.CustomUserDao;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UserServiceImpl implements UserService {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private CustomUserDao userDao;
	
	@Autowired
	private CustomAuthorizationDao authorizationDao;
	
	/**
	 * <p></p>
	 * 
	 */
	public UserServiceImpl() {
	}

	@Transactional("consumer")
	public Authorization findPrimaryAuthorization(String email) throws MultipleRecordsFoundException {
		
		User user = userDao.findByEmail(email);
		Authorization auth = authorizationDao.findPrimaryAuthorizationByUser(user);
		
		return auth;
	}
}

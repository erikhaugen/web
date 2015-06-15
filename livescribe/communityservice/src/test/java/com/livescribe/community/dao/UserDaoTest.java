/**
 * 
 */
package com.livescribe.community.dao;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.community.BaseTest;
import com.livescribe.community.config.CommunityProperties;
import com.livescribe.community.orm.ActiveUser;
import com.livescribe.community.orm.UserProfile;
import com.livescribe.base.utils.WOAppMigrationUtils;

/**
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UserDaoTest extends BaseTest {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CommunityProperties communityProperties;
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public UserDaoTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	
	}

	/**
	 * <p>Determines whether an <code>ActiveUser</code> is found and returned.</p>
	 * 
	 * @see com.livescribe.community.dao.UserDao#findActiveUserByToken(String)
	 */
//	@Test
	@Transactional
	public void testFindActiveUserByToken() {
		
		if (!skipTests) {
			String TEST_USER_TOKEN = communityProperties.getProperty("UserDaoTest.testFindActiveUserByToken.test.token");
	
			logger.debug("TEST_USER_TOKEN = " + TEST_USER_TOKEN);
			
			ActiveUser aUser = userDao.findActiveUserByToken(TEST_USER_TOKEN);
			assertNotNull("The returned ActiveUser was 'null'.", aUser);
		}
	}
	
	/**
	 * <p>Determines whether a <code>UserProfile</code> is found and returned.</p>
	 * 
	 * @see com.livescribe.community.dao.UserDao#findUserProfileByActiveUser(ActiveUser)
	 */
	@Test
	@Transactional
	public void testFindUserProfileByActiveUser() {
		
		if (!skipTests) {
			String TEST_USER_PRIMARY_KEY = communityProperties.getProperty("UserDaoTest.testFindUserProfileByActiveUser.test.id");
			ActiveUser aUser = new ActiveUser();
			byte[] pk = WOAppMigrationUtils.convertStringToPrimaryKey(TEST_USER_PRIMARY_KEY);
			aUser.setUserId(pk);
			
			logger.debug("TEST_USER_PRIMARY_KEY = " + TEST_USER_PRIMARY_KEY);
			
			UserProfile uProfile = userDao.findUserProfileByActiveUser(aUser);
			assertNotNull("The returned UserProfile was 'null'.", uProfile);
		}
	}
}

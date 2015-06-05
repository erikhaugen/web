/*
 * Created:  Oct 6, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.tokensvc.BaseTest;
import com.livescribe.aws.tokensvc.exception.UserNotFoundException;
import com.livescribe.framework.orm.consumer.User;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UserServiceTest extends BaseTest {

	private static String TEST_EMAIL	= "jackstraw66@yahoo.com";
	
	@Autowired
	private UserService userService;
	
	/**
	 * <p></p>
	 * 
	 */
	public UserServiceTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		
		setUpConsumer();
	}
	
	@After
	public void tearDown() throws Exception {

		tearDownConsumer();
//		Connection conn = getDBConnection();
//		Statement stmt = conn.createStatement();
//		boolean success1 = stmt.execute("DELETE FROM user_email WHERE email_address = '" + TEST_EMAIL + "'");
//		boolean success2 = stmt.execute("DELETE FROM user WHERE email = '" + TEST_EMAIL + "'");
	}
	
//	@Test
	@Transactional("consumer")
	public void testCreateUser() throws Exception {
		
		String email = "jackstraw66yahoo.com";
		String lang = "en-US";
		
//		User user = userService.createUser(TEST_EMAIL, lang, "false", "false");
//		assertNotNull("The returned User was 'null'.", user);
//		assertEquals("Incorrect email address found for created User.", TEST_EMAIL, user.getPrimaryEmail());
		
//		Set<UserEmail> emails = user.getUserEmails();
//		assertNotNull("The returned Set of emails was 'null'", emails);
//		assertEquals("Incorrect number of UserEmails returned with User.", 1, emails.size());
//		actualDataSet = fetchConsumerResultDataSet();
//		expectedDataSet = fetchConsumerExpectedDataSet();
//		
//		assertEquals("", expectedDataSet, actualDataSet);
	}
	
	@Test
	@Transactional("consumer")
	public void testLogin() throws Exception {
		
		String loginToken = userService.login("kmurdoff@livescribe.com", "letmein");
		assertNotNull("The returned token was 'null'.", loginToken);
		
	}
	
	@Test
	@Transactional("consumer")
	public void testDeleteUser() throws Exception {
		
		User user = new User();
		user.setUserId(1L);
		
		userService.deleteUser(user);
		
		boolean exceptionThrown = false;
		try {
			User missingUser = userService.findUserById(1L);
		}
		catch (UserNotFoundException unfe) {
			exceptionThrown = true;
			unfe.printStackTrace();
		}
		assertTrue("An exception was NOT thrown.", exceptionThrown);
		
	}
}

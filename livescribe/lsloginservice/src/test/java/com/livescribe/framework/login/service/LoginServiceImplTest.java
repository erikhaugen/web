/**
 * 
 */
package com.livescribe.framework.login.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.afp.AFPException;
import com.livescribe.aws.login.util.AuthorizationType;
import com.livescribe.framework.exception.DuplicateEmailAddressException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.login.BaseTest;
import com.livescribe.framework.login.TestConstants;
import com.livescribe.framework.login.exception.AuthenticationException;
import com.livescribe.framework.login.exception.AuthorizationException;
import com.livescribe.framework.login.exception.IncorrectPasswordException;
import com.livescribe.framework.login.exception.LoginException;
import com.livescribe.framework.login.exception.RegistrationNotFoundException;
import com.livescribe.framework.login.service.result.LoginServiceResult;
import com.livescribe.framework.oauth.service.AuthorizationService;
import com.livescribe.framework.orm.consumer.Authenticated;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.CustomAuthenticatedDao;
import com.livescribe.framework.orm.consumer.CustomUserDao;
import com.livescribe.framework.orm.consumer.User;

/**
 * <p>Tests functionality of the {@link com.livescribe.framework.login.service.LoginServiceImpl} methods.</p>
 * 
 * Test data is loaded by DbUnit from the <code>src/test/resources/data/setup/user.xml</code> file.
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
//@Ignore
public class LoginServiceImplTest extends BaseTest implements TestConstants {
	
	private static String TEST_EMAIL		= "samson@livescribe.com";
	private static String TEST_EMAIL_2		= "jackstraw66@yahoo.com";
	private static String TEST_EMAIL_3		= "darkstar@yahoo.com";
	private static String TEST_EMAIL_4		= "sugaree@yahoo.com";
	private static String TEST_PASSWORD		= "letmein";		//	OGFBRQQLe_f5MBESOWJQcnQxacQ
	private static String TEST_PASSWORD_2	= "l3tm3in";		//	qKL2HltYp4xU1iqs_UDH7H3r8Tc
	private static String TEST_PASSWORD_3	= "letmein";		//	OGFBRQQLe_f5MBESOWJQcnQxacQ
	private static String TEST_PASSWORD_4	= "letmein";		//	t6h1/B6iKLkGEEG3zsS9PFKrPOM=
	private static String TEST_NEW_HASHED_3	= "kR2MwEjmN8XyFzTrvgLl2s07caY";		//	opensaysme

	@Autowired
	private LoginService loginService;
	
	@Autowired
	private CustomAuthenticatedDao authenticatedDao;
	
	@Autowired
	private CustomUserDao userDao;
	
	@Autowired
	private AuthorizationService authorizationService;
	
	private User user;
	private String loginToken = null;
	private LoginServiceResult loginResult = null;
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public LoginServiceImplTest() {
		super();
	}

	/**
	 * <p></p>
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		setUpConsumer();
	}
		
	/**
	 * <p>Tests that an account is created in both FrontBase and MySQL.</p>
	 * 
	 * @throws Exception
	 */
//	@Test
	@Transactional("consumer")
	public void testCreateAccount() throws Exception {
		
		String method = "testCreateAccount():  ";
		logger.debug(method + "----- S T A R T -----");
		
		LoginServiceResult loginResult = this.loginService.createAccount(TEST_EMAIL_2, TEST_PASSWORD_2, "en-US", "Tester", "TEST", false, false);
		this.user = loginResult.getUser();
		assertNotNull("The returned User object was 'null'.", user);
		assertEquals("The email address did not match what was expected.", TEST_EMAIL_2, user.getPrimaryEmail());
		logger.debug(method + "User email address: " + user.getPrimaryEmail());
		
		//	TODO:  Connect to FrontBase and ensure account is created there.
		logger.debug(method + "  ----- E N D -----");
	}
	
	/**
	 * <p>Tests logging a user in correctly.</p>
	 * 
	 * Logs a user in with the <code>kmurdoff@livescribe.com</code> email address.
	 * 
	 * @throws Exception
	 */
	@Test
	@Transactional("consumer")
	public void testLogin() throws Exception {
		
		String method = "testLogin():  ";
		logger.debug(method + "----- S T A R T -----");
		
		try {
			this.loginResult = loginService.login(TEST_EMAIL, TEST_PASSWORD, "TEST");
			this.loginToken = loginResult.getLoginToken();
		}
		catch (Exception e) {
			logger.error("Exception thrown.", e);
			e.printStackTrace();
		}
		
		logger.debug(method + "login token = " + this.loginToken);
		
		assertNotNull("The returned login token was 'null'.", this.loginToken);
		
		List<Authenticated> list = authenticatedDao.findByLoginToken(this.loginToken, "TEST");
		
		assertNotNull("The returned List was 'null'.", list);
		assertEquals("Incorrect number of logins found.", 1, list.size());
	}
	
	/**
	 * <p>Tests attempting to log a user in with an incorrect password.</p>
	 * 
	 * Uses the <code>kmurdoff@livescribe.com</code> email address.
	 * 
	 * @throws Exception
	 */
	@Test
	@Transactional("consumer")
	public void testLogin_Fail_IncorrectPassword() throws Exception {
		
		String method = "testLogin_Fail_IncorrectPassword():  ";
		logger.debug(method + "----- S T A R T -----");
		
		boolean exThrown = false;
		try {
			this.loginResult = loginService.login(TEST_EMAIL, "WrongPassword", "TEST");
			this.loginToken = loginResult.getLoginToken();
		}
		catch (IncorrectPasswordException ipe) {
			exThrown = true;
		}
		catch (LoginException le) {
			logger.error(method, le);
		}
		catch (DuplicateEmailAddressException deae) {
			logger.error(method, deae);
		}
		catch (UserNotFoundException unfe) {
			logger.error(method, unfe);
		}
		catch (InvalidParameterException ipe) {
			logger.error(method, ipe);
		}
		
		logger.debug(method + "login token = " + this.loginToken);
		
		assertNotNull("IncorrectPasswordException was NOT thrown.", exThrown);		
	}
	
	@Test
	@Transactional("consumer")
	public void testLogin_Fail_UserNotFoundException() throws Exception {
		
		String method = "testLogin_Fail_UserNotFoundException():  ";
		logger.debug(method + "----- S T A R T -----");

		boolean exThrown = false;
		try {
			this.loginResult = loginService.login("kmurdoff@livescribe.com", "letmein", "WEB");
			this.loginToken = loginResult.getLoginToken();
		}
		catch (LoginException le) {
			logger.error(method, le);
		}
		catch (DuplicateEmailAddressException deae) {
			logger.error(method, deae);
		}
		catch (UserNotFoundException unfe) {
			exThrown = true;
		}
		catch (InvalidParameterException ipe) {
			logger.error(method, ipe);
		}
		
		logger.debug(method + "login token = " + this.loginToken);
		
		assertNotNull("UserNotFoundException was NOT thrown.", exThrown);		
	}
	
	/**
	 * <p>Tests logging in with an incorrect password.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	@Transactional("consumer")
	public void testLogin_Fail_InvalidParameterException() throws Exception {
		
		String method = "testLogin_Fail_InvalidParameterException():  ";
		logger.debug(method + "----- S T A R T -----");

		boolean exThrown = false;
		try {
			this.loginResult = loginService.login(TEST_EMAIL_2, "letmeinplease", "WEB");
			this.loginToken = loginResult.getLoginToken();
		}
		catch (LoginException le) {
			le.printStackTrace();
		}
		catch (DuplicateEmailAddressException deae) {
			deae.printStackTrace();
		}
		catch (UserNotFoundException unfe) {
			unfe.printStackTrace();
		}
		catch (InvalidParameterException ipe) {
			exThrown = true;
		}
		
		logger.debug(method + "login token = " + this.loginToken);
		
		assertNotNull("InvalidParameterException was NOT thrown.", exThrown);		
	}
	
	/**
	 * <p></p>
	 * 
	 * @throws LoginException
	 * @throws DuplicateEmailAddressException
	 * @throws UserNotFoundException
	 * @throws InvalidParameterException
	 */
	@Test
	@Transactional("consumer")
	public void testLoginWiFiUser_ExistingFrontBaseUser_Success() throws LoginException, DuplicateEmailAddressException, UserNotFoundException, InvalidParameterException {
		
		String method = "testLoginWiFiUser_ExistingFrontBaseUser_Success():  ";
		logger.debug(method + "----- S T A R T -----");

		this.loginResult = loginService.loginWifiUser(TEST_EMAIL_4, TEST_PASSWORD_4, "WEB", null);
		this.loginToken = loginResult.getLoginToken();
		
		assertNotNull("The returned login token was 'null'.", loginToken);
		assertFalse("The returned login token was an empty string.", "".equals(loginToken));
	}
	
	/**
	 * <p>Tests a user&apos;s login status using the login domain matching 
	 * the domain the user logged in with.</p>
	 * 
	 */
	@Test
	@Transactional("consumer")
	public void testIsLoggedIn_Success_MatchingLoginDomain() {
		
		String method = "testIsLoggedIn_Success_MatchingLoginDomain():  ";
		logger.debug(method + "----- S T A R T -----");

		boolean isLoggedIn = this.loginService.isLoggedIn("e58373ff-90ea-4c54-bf9f-dff23a876709", "WEB");
		assertTrue("The user was not logged in.", isLoggedIn);
	}
	
	/**
	 * <p>Tests that a logged in user can be found using a <code>null</code>
	 * login domain.</p>
	 * 
	 */
	@Test
	@Transactional("consumer")
	public void testIsLoggedIn_Success_NullLoginDomain() {
		
		String method = "testIsLoggedIn_Success_NullLoginDomain():  ";

		boolean isLoggedIn = this.loginService.isLoggedIn("e58373ff-90ea-4c54-bf9f-dff23a876709", null);
		assertTrue("The user was not logged in.", isLoggedIn);
	}

	/**
	 * <p>This method tests whether a user is determined to be logged in via LD, 
	 * but the request comes from Market Live (ML).</p>
	 */
	@Test
	@Transactional("consumer")
	public void testIsLoggedIn_Success_LDLoginDomainFromLD() {
		
		String method = "testIsLoggedIn_Success_NullLoginDomain():  ";
		logger.debug(method + "----- S T A R T -----");

		boolean isLoggedIn = this.loginService.isLoggedIn("95d12b4d-ffe2-4fed-b715-ba5821f27e33", "LD");
		assertTrue("The user was not logged in.", isLoggedIn);
	}
	
	/**
	 * <p></p>
	 * 
	 * @throws Exception
	 */
//	@Test
	@Transactional("consumer")
	public void testLogout() throws Exception {
		
		boolean success = true;
		String lt = "e58373ff-90ea-4c54-bf9f-dff23a876709";
		
		try {
			loginService.logout(lt, "WEB");
		}
		catch (Exception e) {
			logger.error(e);
			success = false;
		}
		assertTrue("Logout was unsuccessful.", success);
		List<Authenticated> list = authenticatedDao.findByLoginToken(lt, "WEB");
		
		assertTrue("The List of logins was not empty.", list.isEmpty());
	}
	
	/**
	 * <p>Tests that a password is changed correctly in the Wi-Fi database ONLY!</p>
	 * 
	 * This test does not assume the Web Objects integration is present.
	 * 
	 * @throws Exception
	 */
	@Test
	@Transactional("consumer")
	public void testChangePassword_Success() throws Exception {
		
		String method = "testChangePassword_Success():  ";
		logger.debug(method + "----- S T A R T -----");

		boolean success = loginService.changePassword(TEST_EMAIL_3, TEST_PASSWORD_3, "opensaysme");
		assertTrue("The operation to change the user's password was not successful.", success);

		//	Cannot check if password was changed because current transaction 
		//	has not been committed yet.  [KFM - 2013-12-09]
//		User user = userDao.findByEmail(TEST_EMAIL_3);
//		assertEquals("The new password did not match what was expected.", user.getPassword(), TEST_NEW_HASHED_3);
	}

	@Test
	@Transactional("consumer")
	public void testChangePassword_Fail_IncorrectOldPassword() throws DuplicateEmailAddressException {
		
		boolean expThrown = false;
		boolean success = false;
		try {
			success = loginService.changePassword(TEST_EMAIL_3, "incorrect", "opensaysme");
		}
		catch (UserNotFoundException e) {
			e.printStackTrace();
		}
		catch (AuthenticationException e) {
			e.printStackTrace();
			expThrown = true;
		}
		assertFalse("The operation to change the user's password WAS successful.", success);
		assertTrue("The old password did not match what was stored in the database.", expThrown);
	}
	
	@Test
	@Transactional("consumer")
	public void testFindPrimaryAuthByEmail() throws InvalidParameterException, UserNotFoundException, AuthorizationException, DuplicateEmailAddressException {
		
		String method = "testFindPrimaryAuthByEmail():  ";
		logger.debug(method + "----- S T A R T -----");
		
		Authorization auth = authorizationService.findPrimaryAuthByEmail(XML_LOADED_EMAIL_2, AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		
		assertNotNull("The returned authorization was 'null'.", auth);
		String enUserId = auth.getEnUserId().toString();
		assertEquals("Incorrect authorization returned.", XML_LOADED_EN_USER_ID_2, enUserId);
	}
	
	@Test
	@Transactional("consumer")
	public void testFindUserUIDByDisplayId() throws RegistrationNotFoundException, AFPException {
		
		String displayId = "AYE-AAA-ACE-8F";
		
		String uid = loginService.findUserUIDByPenDisplayId(displayId);
		assertNotNull("The returned UID was 'null'.", uid);
		
		assertEquals("The returned UID did not match.", XML_LOADED_USER_UID_2, uid);
	}
}

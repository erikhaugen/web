/**
 * 
 */
package com.livescribe.aws.login.client;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.aws.login.client.jetty.HttpTestServer;
import com.livescribe.aws.login.dto.AuthorizationDto;
import com.livescribe.aws.login.response.AuthorizationResponse;
import com.livescribe.aws.login.util.AuthorizationType;
import com.livescribe.framework.exception.AuthorizationNotFoundException;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.LogoutException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.login.BaseTest;
import com.livescribe.framework.login.TestConstants;
import com.livescribe.framework.login.exception.AuthorizationException;
import com.livescribe.framework.login.exception.EmailAlreadyTakenException;
import com.livescribe.framework.login.exception.IncorrectPasswordException;
import com.livescribe.framework.login.exception.LoginException;
import com.livescribe.framework.login.exception.RegistrationNotFoundException;
import com.livescribe.framework.login.response.LoginResponse;
import com.livescribe.framework.login.service.LoginService;

/**
 * <p>Tests the handling of various methods by the <code>LoginClient</code>.</p>
 * 
 * <p>These unit tests DO NOT make actual calls to a running Login Service
 * in any environment.&nbsp;&nbsp;Instead, a &quot;mock&quot; HTTP server is
 * employed to receive requests and pass them off to a handler that returns
 * &quot;mock&quot; XML responses.&nbsp;&nbsp;These tests verify that sending 
 * requests and receiving/parsing the responses works as expected.</p>
 *  
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 * @see HttpTestServer
 */
public class LoginClientTest extends BaseTest implements TestConstants {

//	private static String TEST_EMAIL		= "kmurdoff@livescribe.com";
//	private static String TEST_EMAIL		= "jackstraw66@yahoo.com";
//	private static String TEST_PASSWORD		= "l3tm3in";
	private static String TEST_EMAIL		= "kmurdoff@livescribe.com";
	private static String TEST_EMAIL_2		= "jackstraw66@yahoo.com";
	private static String TEST_EMAIL_3		= "jackstr66@livescribe.com";
	private static String TEST_PASSWORD		= "letmein";		//	OGFBRQQLe_f5MBESOWJQcnQxacQ
	private static String TEST_PASSWORD_2	= "l3tm3in";		//	qKL2HltYp4xU1iqs_UDH7H3r8Tc
	private static String TEST_PASSWORD_3	= "opensaysme";		//	kR2MwEjmN8XyFzTrvgLl2s07caY
	
	@Autowired
	private LoginService loginService;
	
	private LoginClient client;
	private static String loginToken;
	
	private HttpTestServer server = null;
	private long start = 0;
	
	/**
	 * <p></p>
	 * @throws IOException 
	 *
	 */
	public LoginClientTest() throws IOException {
		super();
		logger.debug("Starting up ...");
		this.client = new LoginClient("testloginclient.properties");
		this.server = new HttpTestServer();
		logger.debug("Instantiated.");
	}

	@Before
	public void setUp() throws Exception {
		
		logger.debug("START - LoginClientTest");
		start = System.currentTimeMillis();

		this.server.start();
		
		setUpConsumer();
		logger.debug("'consumer' test data loaded.");

		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		long usedMem = totalMem - freeMem;
		logger.debug("Start:  Memory used:  " + usedMem);
	}
	
	@After
	public void tearDown() throws Exception {
		
		this.server.stop();

		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		long usedMem = totalMem - freeMem;
		logger.debug("  End:  Memory used:  " + usedMem);
		
//		tearDownConsumer();
		
		long end = System.currentTimeMillis();
		long duration = end - start;
		String uptime = calcUptime(duration);
		logger.debug("END - LoginClientTest - " + uptime + " [<days> - <hours> - <minutes> - <seconds>]\n");
	}
	
	@Test
	public void testFindAuthorizationByUIDAndProviderUserId() {
		
		String method = "testFindAuthorizationByUIDAndProviderUserId()";
		
		logger.debug("START - " + method + " ---------- ");
		
		AuthorizationResponse response = null;
		try {
			response = client.findAuthorizationByUIDAndProviderUId(XML_LOADED_USER_UID_2, XML_LOADED_EN_USER_ID_2, AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		} catch (InvalidParameterException ipe) {
			String msg = "InvalidParameterException thrown " + ipe.getMessage();
			logger.error(msg);
			Assert.fail(msg);
		} catch (AuthorizationException ae) {
			String msg = "AuthorizationException thrown " + ae.getMessage();
			logger.error(msg);
			Assert.fail(msg);
		} catch (ClientException ce) {
			String msg = "ClientException thrown " + ce.getMessage();
			logger.error(msg);
			Assert.fail(msg);
		} catch (UserNotFoundException unfe) {
			String msg = "UserNotFoundException thrown " + unfe.getMessage();
			logger.error(msg);
			Assert.fail(msg);
		} catch (AuthorizationNotFoundException anfe) {
			String msg = "AuthorizationNotFoundException thrown " + anfe.getMessage();
			logger.error(msg);
			Assert.fail(msg);
		}
		
		Assert.assertNotNull("The returned response object was 'null'.", response);
		
		String oauthAccessToken = response.getOauthAccessToken();
		Assert.assertEquals("The returned OAuth access token did not match.", XML_LOADED_ACCESS_TOKEN_2, oauthAccessToken);
		
		logger.debug("END - " + method);
	}
	
	@Test
	public void testFindAuthorizationsByUid() {
		
		String method = "testFindAuthorizationsByUid()";
		
		logger.debug("START - " + method + " ---------- ");
		
		List<AuthorizationDto> list = null;
		
		try {
			list = client.findAuthorizationsByUid("jftK2hSWf079", AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		} catch (ClientException ce) {
			String msg = "ClientException thrown";
			logger.error(msg);
			Assert.fail(msg);
		} catch (InvalidParameterException ipe) {
			String msg = "InvalidParameterException thrown";
			logger.error(msg);
			Assert.fail(msg);
		}
		Assert.assertNotNull("The returned list of AuthorizationDto objects was 'null'.", list);
		Assert.assertTrue("The returned list of AuthorizationDto objects was empty", list.size() > 0);

		logger.debug("END - " + method);
	}
	
//	@Test
	public void testCreateAccount() throws Exception {
		
		LoginResponse response = null;
		
		response = client.createWifiAccount(TEST_EMAIL, TEST_PASSWORD, "en-US", "Code Droid", "TEST", "TESTLOGINTOKEN", "true", "true");
		
		assertNotNull("The returned token was 'null'.", response.getLoginToken());
	}
	
//	@Test
	public void testCreateUser_Success() throws Exception {
		
		String uid = null;
		try {
			uid = client.createUser(TEST_EMAIL_2, TEST_PASSWORD_2, "en-US", "Code Droid", "TEST", "true", "true");
		}
		catch (IOException ioe) {
			logger.debug(ioe.getMessage(), ioe);
		}
		assertNotNull("The returned User object was 'null'.", uid);		
	}
	
//	@Test
	public void testCreateUser_Fail_EmailAlreadyTakenException() throws Exception {
		
		String uid = null;
		boolean exThrown = false;
		try {
			uid = client.createUser(TEST_EMAIL, TEST_PASSWORD, "en-US", "Code Droid", "TEST", "true", "true");
		}
		catch (EmailAlreadyTakenException eaee) {
			exThrown = true;
		}
		catch (IOException ioe) {
			logger.debug(ioe.getMessage(), ioe);
		}
		assertTrue("EmailAlreadyTakenException was NOT thrown!", exThrown);
	}
	
//	@Test
	public void testCreateUser_Fail_InvalidParameterException_optIn() throws Exception {
		
		String uid = null;
		boolean exThrown = false;
		try {
			uid = client.createUser(TEST_EMAIL_3, TEST_PASSWORD_3, "en-US", "Code Droid", "TEST", "", "");
		}
		catch (InvalidParameterException eaee) {
			exThrown = true;
		}
		catch (IOException ioe) {
			logger.debug(ioe.getMessage(), ioe);
		}
		assertTrue("InvalidParameterException was NOT thrown!", exThrown);
	}
	
	/**
	 * <p></p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoginWifiUser() throws Exception {
		
		//	A 'null' loginToken parameter forces Login Service to generate
		//	a new UUID token.
		String token = client.loginWifiUser(XML_LOADED_EMAIL_1, XML_LOADED_PASSWORD_1, "LoginClientTest", null);
		assertNotNull("The returned token was 'null'.", token);
		assertEquals("", XML_LOADED_LOGIN_TOKEN_1, token);		
	}
	
	/**
	 * <p>Verifies the correct Exception with thrown when a password match
	 * fails.</p>
	 */
	@Test
	public void testLoginWifiUser_Fail_IncorrectPassword() {
		
		String method = "testLoginWifiUser_Fail_IncorrectPassword()";
		
		String token = null;
		boolean correctExceptionThrown = false;
		try {
			token = client.loginWifiUser(XML_LOADED_EMAIL_1a, XML_LOADED_PASSWORD_1a, "LoginClientTest", null);
		} catch (IncorrectPasswordException ipe) {
			String msg = "IOException thrown";
			logger.debug(method + " - " + msg);
			correctExceptionThrown = true;
		} catch (IOException ioe) {
			String msg = "IOException thrown";
			logger.debug(method + " - " + msg);
			correctExceptionThrown = false;
		} catch (InvalidParameterException ipe) {
			String msg = "InvalidParameterException thrown";
			logger.debug(method + " - " + msg);
			correctExceptionThrown = false;
		} catch (LoginException le) {
			String msg = "LoginException thrown";
			logger.debug(method + " - " + msg);
			correctExceptionThrown = false;
		} catch (ClientException ce) {
			String msg = "ClientException thrown";
			logger.debug(method + " - " + msg);
			correctExceptionThrown = false;
		} catch (UserNotFoundException unfe) {
			String msg = "UserNotFoundException thrown";
			logger.debug(method + " - " + msg);
			correctExceptionThrown = false;
		}
		assertNull("The returned token WAS NOT 'null'.", token);
		assertTrue("Expected exception WAS NOT thrown.", correctExceptionThrown);
	}
	
	@Test
	public void testIsLoggedIn() throws Exception {
		
		boolean isLoggedIn = client.isLoggedIn(XML_LOADED_LOGIN_TOKEN_2, "LD");
		assertTrue("The user was NOT logged in.", isLoggedIn);
	}
	
//	@Test
	public void testLogout() {
		
		String method = "testLogout():  ";
		
		boolean exThrown = false;
		try {
			client.logout(loginToken, "TEST");
		}
		catch (InvalidParameterException e) {
			exThrown = true;
			logger.error(method + "InvalidParameterException thrown.");
		}
		catch (ClientException e) {
			exThrown = true;
			logger.error(method + "ClientException thrown.");
		}
		catch (IOException e) {
			exThrown = true;
			logger.error(method + "IOException thrown.");
		}
		catch (LogoutException e) {
			exThrown = true;
			logger.error(method + "LogoutException thrown.");
		}
		assertFalse("An exception was thrown!", exThrown);
	}

//	@Test
	public void testLogout_Success_EmptyLoginDomain() {
		
		String method = "testLogout_Success_EmptyLoginDomain():  ";
		
		boolean exThrown = false;
		try {
			client.logout(XML_LOADED_LOGIN_TOKEN_2, null);
		}
		catch (InvalidParameterException e) {
			logger.error(method + "InvalidParameterException thrown.");
			assertTrue("InvalidParameterException thrown.", false);
			exThrown = true;
		}
		catch (ClientException e) {
			logger.error(method + "ClientException thrown.");
			assertTrue("ClientException thrown.", false);
			exThrown = true;
		}
		catch (IOException e) {
			logger.error(method + "IOException thrown.");
			assertTrue("IOException thrown.", false);
			exThrown = true;
		}
		catch (LogoutException e) {
			exThrown = true;
			logger.error(method + "LogoutException thrown.");
		}
		assertFalse("An exception was thrown!", exThrown);
	}

//	@Test
	public void testLogout_Fail_InvalidParameterException() {
		
		String method = "testLogout_Fail_InvalidParameterException():  ";
		
		boolean exThrown = false;
		try {
			client.logout("", "TEST");
		}
		catch (InvalidParameterException e) {
			exThrown = true;
		}
		catch (ClientException e) {
			logger.error(method + "ClientException thrown.");
		}
		catch (IOException e) {
			logger.error(method + "IOException thrown.");	
		}
		catch (LogoutException e) {
			logger.error(method + "LogoutException thrown.");
		}
		assertTrue("InvalidParameterException was NOT thrown.", exThrown);
	}
	
	/**
	 * <p>Tests successful requests for an authorization token.</p>
	 * 
	 * @throws InvalidParameterException
	 * @throws IOException
	 * @throws ClientException
	 * @throws RegistrationNotFoundException
	 * @throws AuthorizationException
	 */
//	@Test
	public void testFindAuthorizationByPenDisplayId() throws InvalidParameterException, IOException, ClientException, RegistrationNotFoundException, AuthorizationException {
		
		String method = "testGetAuthorizationTokenByPenDisplayId():  ";
		
		AuthorizationResponse resp = client.findAuthorizationByPenDisplayId("AYE-AAA-ACE-8F", AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		assertNotNull("The returned response was 'null'.", resp);

		String token = resp.getOauthAccessToken();
		assertNotNull("The returned token was 'null'.", token);
		
		assertEquals("The returned token was incorrect.", XML_LOADED_ACCESS_TOKEN_2, token);
	}
	
	/**
	 * <p>Tests that requests with invalid display IDs are prevented from
	 * continuing.</p>
	 */
//	@Test
	public void testFindAuthorizationByPenDisplayId_Fail_InvalidParameterException() {
		
		String method = "testGetAuthorizationTokenByPenDisplayId_Fail_InvalidParameterException():  ";
		
		AuthorizationResponse resp = null;
		try {
			resp = client.findAuthorizationByPenDisplayId("AYE-KFM", AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		}
		catch (InvalidParameterException e) {
			assertTrue("An InvalidParameterException was NOT thrown.", true);
			return;
		}
		catch (RegistrationNotFoundException e) {
			assertFalse(" A RegistrationNotFoundException was thrown.", true);
			e.printStackTrace();
			return;
		}
		catch (AuthorizationException e) {
			assertFalse(" An AuthorizationException was thrown.", true);
			e.printStackTrace();
			return;
		}
		catch (ClientException e) {
			assertFalse(" A ClientException was thrown.", true);
			e.printStackTrace();
			return;
		}
	}

	/**
	 * <p>Tests that requests with valid, but unregistered, display IDs are 
	 * reported correctly.</p>
	 */
//	@Test
	public void testFindAuthorizationTokenByPenDisplayId_Fail_RegistrationNotFoundException() {
		
		String method = "testGetAuthorizationTokenByPenDisplayId_Fail_InvalidParameterException():  ";
		
		AuthorizationResponse resp = null;
		try {
			//	This display ID is present in the database, but not registered to a user yet.
			resp = client.findAuthorizationByPenDisplayId("AYE-AAA-A8R-5J", AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		}
		catch (InvalidParameterException e) {
			assertFalse("An InvalidParameterException was thrown.", true);
			e.printStackTrace();
			return;
		}
		catch (RegistrationNotFoundException e) {
			assertTrue(" A RegistrationNotFoundException was NOT thrown.", true);
			return;
		}
		catch (AuthorizationException e) {
			assertFalse(" An AuthorizationException was thrown.", true);
			e.printStackTrace();
			return;
		}
		catch (ClientException e) {
			assertFalse(" A ClientException was thrown.", true);
			e.printStackTrace();
			return;
		}
	}

	/**
	 * <p>Tests for requests where the given display ID is valid and registered
	 * to a user, but the user has no authorization token.</p>
	 */
//	@Test
	public void testFindAuthorizationTokenByPenDisplayId_Fail_AuthorizationException() {
		
		String method = "testGetAuthorizationTokenByPenDisplayId_Fail_InvalidParameterException():  ";
		
		AuthorizationResponse resp = null;
		try {
			//	This display ID is registered to a user, but the user has no
			//	no authorization token.
			resp = client.findAuthorizationByPenDisplayId("AYE-AAA-AXX-TQ", AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		}
		catch (InvalidParameterException e) {
			assertFalse("An InvalidParameterException was thrown.", true);
			e.printStackTrace();
			return;
		}
		catch (RegistrationNotFoundException e) {
			assertFalse(" A RegistrationNotFoundException was thrown.", true);
			e.printStackTrace();
			return;
		}
		catch (AuthorizationException e) {
			assertTrue(" An AuthorizationException was NOT thrown.", true);
			return;
		}
		catch (ClientException e) {
			assertFalse(" A ClientException was thrown.", true);
			e.printStackTrace();
			return;
		}
	}
	
//	@Test
	public void testFindAuthorizationByUID() throws Exception {
		
		String method = "testFindAuthorizationByUID():  ";

		String uid = "bdcf046a-3dda-41fc-8954-246342a74cd3";
		
		AuthorizationResponse resp = null;

		resp = client.findAuthorizationByUID(uid, AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		assertNotNull("The returned response was 'null'.", resp);

		String token = resp.getOauthAccessToken();
		assertNotNull("The returned token was 'null'.", token);
		
		assertEquals("The returned token was incorrect.", XML_LOADED_ACCESS_TOKEN_2, token);
	}
}

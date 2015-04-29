package com.livescribe.aws.login.client;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.livescribe.framework.login.response.LoginResponse;

@Ignore
public class LoginClientStressTest {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static final String BASE_TEST_EMAIL = "loginclient_";
	
	private static final String TEST_EMAIL_DOMAIN = "@livescribe.com";
	
	private static final String TEST_PASSWORD = "test";
	
	private static final int NUMBER_OF_RUNS = 200;
	
	public LoginClientStressTest() {
		super();
		DOMConfigurator.configureAndWatch("log4j.xml");
	}
	
	@Before
	public void setUp() throws Exception {
		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		long usedMem = totalMem - freeMem;
		logger.debug("Start:  Memory used:  " + usedMem);
	}
	
	@After
	public void tearDown() throws Exception {
		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		long usedMem = totalMem - freeMem;
		logger.debug("  End:  Memory used:  " + usedMem);
	}
	
	@Test
	public void testCreateAccount() throws Exception {
		String email;
		long startTime, endTime;
		
		for (int i = 0; i < NUMBER_OF_RUNS; i++) {
			email = BASE_TEST_EMAIL + String.valueOf(System.currentTimeMillis()) + TEST_EMAIL_DOMAIN;
			
			try {
				startTime = System.currentTimeMillis();
				LoginClient client = new LoginClient();
				LoginResponse response = client.createAccount(email, TEST_PASSWORD, "en-US", "Code Droid", "WEB", "true", "true");
				endTime = System.currentTimeMillis();
				
				if (response != null && response.getLoginToken() != null) {
					logger.debug("SUCCESSFULLY created user '" + email + "' in " + (endTime - startTime) + "ms");
				} else {
					logger.debug("FAILED to create user '" + email + "' in " + (endTime - startTime) + "ms");
				}
				
			} catch (Exception e) {
				String errMsg = "FAILED to create user '" + email + "'." + e.getClass().toString() + ": " + e.getMessage();
				logger.debug(errMsg, e);
				
			}
			
		}
	}
}

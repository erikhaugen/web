/**
 * 
 */
package com.livescribe.community.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.community.BaseTest;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UserServiceTest extends BaseTest {

	@Autowired
	private UserService userService;
	
	/**
	 * 
	 */
	public UserServiceTest() {
		super();
	}

	/**
	 * <p>Tests a case that should succeed.</p>
	 */
//	@Test
//	@Transactional
	public void testIsUserLoggedInByToken_Success() throws Exception {
		
		if (!skipTests) {
	//		String token = "154181492002SSbtqZkNCrWk";
	//		String token = "010747946974pJgtsnGxQbpn";
	//		String token = "115504162441nnnnnnnnnnnn";
//			String token = "615959276657qSGsFjLJNgGR";
			String token = "560409196995KpTvsgXltcGM";
			
			boolean loggedIn = userService.isUserLoggedInByToken(token);
			
			assertTrue("The user was NOT found to be logged in.", loggedIn);
		}
	}

	/**
	 * <p>Tests a case that should fail.</p>
	 */
//	@Test
//	@Transactional
	public void testIsUserLoggedInByToken_Fail() throws Exception {
		
		if (!skipTests) {
			String token = "154181492002SSbtqZkNCkfm";
	//		String token = "010747946974pJgtsnGxQbpn";
			
			boolean loggedIn = userService.isUserLoggedInByToken(token);
			
			assertFalse("The user WAS found to be logged in.", loggedIn);
		}
	}

	/**
	 * <p>Tests a case that should succeed using a separate XML RPC methodology.</p>
	 */
//	@Test
//	@Transactional
	public void testIsUserLoggedInByToken2() throws Exception {
		
		if (!skipTests) {
//			String token = "154181492002SSbtqZkNCrWk";
	//		String token = "010747946974pJgtsnGxQbpn";
			String token = "560409196995KpTvsgXltcGM";
			
			boolean loggedIn = userService.isUserLoggedInByToken2(token);
			
			assertTrue("The user was NOT found to be logged in.", loggedIn);
		}
	}

	/**
	 * <p>Tests a case a REST URL methodology.</p>
	 */
	@Test
	@Transactional
	public void testIsUserLoggedInByToken3() throws Exception {
		
		if (!skipTests) {
	//		String token = "287189666215dbHxDjvJLwvG";
//			String token = "656716667655DcrGSpQfWjgZ";
			String token = "560409196995KpTvsgXltcGM";
			
			boolean loggedIn = userService.isUserLoggedInByToken3(token);
			
			assertTrue("The user was NOT found to be logged in.", loggedIn);
		}
	}
}

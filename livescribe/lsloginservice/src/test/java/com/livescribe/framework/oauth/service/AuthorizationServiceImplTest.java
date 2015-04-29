/**
 * Created:  Nov 15, 2013 2:17:27 PM
 */
package com.livescribe.framework.oauth.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.login.dto.AuthorizationDto;
import com.livescribe.aws.login.util.AuthorizationType;
import com.livescribe.framework.login.BaseTest;
import com.livescribe.framework.login.TestConstants;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class AuthorizationServiceImplTest extends BaseTest implements TestConstants {

	@Autowired
	private AuthorizationService authorizationService;
	
	/**
	 * <p></p>
	 * 
	 */
	public AuthorizationServiceImplTest() {
		super();
	}

	@Test
	@Transactional("consumer")
	public void testFindAuthorizationsByUid() {
		
		List<AuthorizationDto> list = authorizationService.findAuthorizationsByUid(XML_LOADED_USER_UID_3, AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		Assert.assertNotNull("The returned list of authorizations was 'null'.", list);
		Assert.assertEquals("Incorrect number of authorizations returned.", 2, list.size());
	}
	
	@Test
	@Transactional("consumer")
	public void testFindAuthorizationsByUid_NoneFound() {
		
		List<AuthorizationDto> list = authorizationService.findAuthorizationsByUid("562459", AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		Assert.assertNotNull("The returned list of authorizations was 'null'.", list);
		Assert.assertEquals("The List of authorizations was NOT empty.", 0, list.size());
	}	
}

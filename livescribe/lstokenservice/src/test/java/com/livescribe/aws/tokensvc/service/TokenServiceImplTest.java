/*
 * Created:  Sep 19, 2011
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
import com.livescribe.aws.tokensvc.crypto.EncryptionUtils;
import com.livescribe.aws.tokensvc.dto.TemporaryCredentialsDTO;
import com.livescribe.aws.tokensvc.exception.DeviceAlreadyRegisteredException;
import com.livescribe.aws.tokensvc.exception.RegistrationNotFoundException;
import com.livescribe.aws.tokensvc.response.CredentialResponse;
import com.livescribe.aws.tokensvc.response.ErrorResponse;
import com.livescribe.aws.tokensvc.response.RegistrationResponse;
import com.livescribe.aws.tokensvc.response.ServiceResponse;
import com.livescribe.aws.tokensvc.response.UnregistrationResponse;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.manufacturing.Pen;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class TokenServiceImplTest extends BaseTest {

	private static String PEN_SERIAL = "2594160246848";
	private static long USER_ID= 1;
	
	private byte[] bytes;
	private String guid;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private EncryptionUtils encryptionUtils;
	
	/**
	 * <p></p>
	 * 
	 */
	public TokenServiceImplTest() {
		super();
	}

	/**
	 * <p></p>
	 * 
	 */
	@Before
	public void setUp() throws Exception {
		
		String tmp = PEN_SERIAL + ":" + USER_ID;
		bytes = encryptionUtils.encrypt(tmp);
		
		for (int i = 0; i < bytes.length; i++) {
			logger.debug(bytes[i]);
		}
		
		setUpConsumer();
		setUpManufacturingData();
	}
	
	@After
	public void tearDown() throws Exception {
		
//		tearDownConsumer();
//		tearDownManufacturingData();
	}
	
	@Test
	@Transactional("consumer")
	public void testDecryptRegistrationToken() {
		
		String token = "6397bb4f2f329bab65335107bdb5b48f";
		
		String decrypted = tokenService.decryptRegistrationToken(token);
		assertNotNull("The returned String was 'null'.", decrypted);
		
		assertEquals("The decrypted String was not what was expected.", "2594160246785:1", decrypted);
		
	}

	/**
	 * <p></p>
	 * 
	 */
	@Test
	@Transactional("consumer")
	public void testGetAwsCredentials() {
		
		String token = "9766d7617c405ed94981ad75f0eb43a2";
		
		TemporaryCredentialsDTO tempCredentials = null;
		try {
			tempCredentials = tokenService.getAwsCredentials(token);
		}
		catch (RegistrationNotFoundException rnfe) {
			rnfe.printStackTrace();
		}
		assertNotNull("The returned TemporaryCredentialsDTO object was 'null'.", tempCredentials);
		
		logger.debug("credentials: " + tempCredentials.toString());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetCredentialsForDataMetrics() {
		System.out.println("testGetCredentialsForDataMetrics()");
		
		TemporaryCredentialsDTO tempCredentials = null;
		tempCredentials = tokenService.getCredentialsForDataMetrics();
		
		System.out.println(tempCredentials);
	}
}

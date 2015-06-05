/*
 * Created:  Oct 11, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import static junit.framework.Assert.*;

import org.apache.commons.codec.DecoderException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.tokensvc.BaseTest;
import com.livescribe.aws.tokensvc.crypto.EncryptionUtils;
import com.livescribe.aws.tokensvc.dto.RegistrationDetails;
import com.livescribe.aws.tokensvc.dto.RegistrationState;
import com.livescribe.aws.tokensvc.exception.DeviceAlreadyRegisteredException;
import com.livescribe.aws.tokensvc.exception.DeviceNotFoundException;
import com.livescribe.aws.tokensvc.exception.RegistrationNotFoundException;
import com.livescribe.aws.tokensvc.orm.consumer.CustomRegisteredDeviceDao;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.manufacturing.Pen;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegistrationServiceTest extends BaseTest {

	//	This serial number is loaded by DbUnit using the pen.xml file.
	private static String PEN_SERIAL	= "2594160246848";
	
	private static long PEN_ID			= 99999L;
	private static long USER_ID			= 100;
	private static String USER_EMAIL	= "kmurdoff@livescribe.com";
	
	private byte[] bytes;
	private String guid;
	private Pen testPen;
	
	@Autowired
	private CustomRegisteredDeviceDao registeredDeviceDao;
	
	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	private EncryptionUtils encryptionUtils;
	
	/**
	 * <p></p>
	 * 
	 */
	public RegistrationServiceTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		
		String tmp = PEN_SERIAL + ":" + USER_ID;
		bytes = encryptionUtils.encrypt(tmp);
		
		for (int i = 0; i < bytes.length; i++) {
			logger.debug(bytes[i]);
		}
		testPen = new Pen();
		testPen.setPenId(PEN_ID);
		testPen.setPenType("Echo Wi-Fi (Test)");
		testPen.setSerialnumber(PEN_SERIAL);
		
		setUpConsumer();
//		setUpManufacturingData();
	}
	
	@After
	public void tearDown() throws Exception {
		
		RegisteredDevice regDev = null;
		try {
			regDev = registeredDeviceDao.findBySerialNumber(PEN_SERIAL);
			registeredDeviceDao.delete(regDev);
		}
		catch (RegistrationNotFoundException rnfe) {
			logger.error("Registered pen with serial number '" + PEN_SERIAL + "' could not be found.");
		}
	}
	
	@Test
	@Transactional("consumer")
	public void testRegisterDevice_Success() throws Exception {
		
		Pen pen = new Pen();
		pen.setPenId(7L);
		pen.setSerialnumber("ASDF9087ADSF");
		
		User user = new User();
		user.setUserId(USER_ID);
		
		RegisteredDevice regDev = registrationService.registerDevice(pen, user);
		assertNotNull("The response object was 'null'.", regDev);
	}
	
//	@Test
	@Transactional("consumer")
	public void testRegisterDevice_FailAlreadyRegistered() throws Exception {
		
		Pen pen = new Pen();
		pen.setPenId(12L);
		pen.setSerialnumber(PEN_SERIAL);
		
		User user = new User();
		user.setUserId(USER_ID);
		
		try {
			RegisteredDevice regDev = registrationService.registerDevice(pen, user);
			assertNotNull("The response object was 'null'.", regDev);
		}
		catch (Exception e) {
			assertEquals("Incorrect exception thrown.", DeviceAlreadyRegisteredException.class, e.getClass());
		}
	}
	
//	@Test
	@Transactional("consumer")
	public void testRegisterDevice_FailDeviceNotFound() throws Exception {
		
		Pen pen = new Pen();
		pen.setPenId(12L);
		pen.setSerialnumber("7788668");
		
		User user = new User();
		user.setUserId(USER_ID);
		
		try {
			RegisteredDevice regDev = registrationService.registerDevice(pen, user);
			assertNotNull("The response object was 'null'.", regDev);
		}
		catch (Exception e) {
			assertEquals("Incorrect exception thrown.", DeviceNotFoundException.class, e.getClass());
		}
	}
	
//	@Test
	@Transactional("consumer")
	public void testGetRegistrationState_FirstRegAttempt() throws Exception {
		
		RegistrationState state = registrationService.getRegistrationState(PEN_SERIAL, USER_EMAIL);
		assertNotNull("Returned 'state' was 'null'.", state);
		
		assertFalse("'isExistingUser() returned 'true'.", state.isExistingUser());
		assertFalse("'isPenRegistrationStarted()' returned 'true'.", state.isPenRegistrationStarted());
	}
	
	private void removeTestData() {
		
	}
}

package com.livescribe.web.registration.client;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.BaseTest;
import com.livescribe.web.registration.TestConstants;
import com.livescribe.web.registration.controller.RegistrationData;
import com.livescribe.web.registration.dto.RegistrationDTO;
import com.livescribe.web.registration.dto.RegistrationHistoryDTO;
import com.livescribe.web.registration.dto.WarrantyDTO;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.jetty.HttpTestServer;
import com.livescribe.web.registration.response.RegistrationHistoryListResponse;
import com.livescribe.web.registration.response.RegistrationListResponse;
import com.livescribe.web.registration.response.RegistrationResponse;
import com.livescribe.web.registration.response.WarrantyListResponse;
import com.livescribe.web.registration.response.WarrantyResponse;

public class RegistrationClientTest extends BaseTest implements TestConstants {

	private static final String PEN_DISPLAY_ID_1			= "AYE-ASX-DWY-UY";
	private static final String PEN_SERIAL_1				= "2594172913044";
	private static final String APP_ID_1					= "com.livescribe.web.KFMTestApp-JMeter-1";
	private static final String APP_ID_2					= "com.livescribe.web.KFMTestApp-JMeter-2";
	private static final String EMAIL_1						= "kfm1@ls.com";
	
	private HttpTestServer server = null;
	private RegistrationClient client;
	private RegistrationData regData;
	private RegistrationData errorRegData1;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * <p></p>
	 * 
	 * @throws IOException
	 */
	public RegistrationClientTest() throws Exception {
		super();
		logger.debug("Starting up ...");
		this.client = new RegistrationClient();
		this.server = new HttpTestServer();
		logger.debug("Instantiated.");
	}
	
	@Before
	public void setUp() throws Exception {
		
		logger.debug("BEFORE - setup()");
		
		this.server.start();

		regData = new RegistrationData();
		regData.setAppId(APP_ID_1);
		regData.setCountry("United States");
		regData.setDisplayId(PEN_DISPLAY_ID_1);
		regData.setEdition(0);
		regData.setEmail("kfm1@ls.com");
		regData.setFirstName("Jack");
		regData.setLastName("Straw");
		regData.setLocale("en_US");
		regData.setOptIn(new Boolean(false));
		regData.setPenName("Random Vector Pen #1");
		regData.setPenSerial(PEN_SERIAL_1);
		
		errorRegData1 = new RegistrationData();
		errorRegData1.setAppId("");
		errorRegData1.setCountry("United States");
		errorRegData1.setDisplayId(PEN_DISPLAY_ID_1);
		errorRegData1.setEdition(0);
		errorRegData1.setEmail("kfm2@ls.com");
		errorRegData1.setFirstName("Jack");
		errorRegData1.setLastName("Straw");
		errorRegData1.setLocale("en_US");
		errorRegData1.setOptIn(new Boolean(false));
		errorRegData1.setPenName("Random Vector Pen #2");
		errorRegData1.setPenSerial(PEN_SERIAL_1);
		
		logger.debug("AFTER - setup()");
	}
	
	@After
	public void tearDown() throws Exception {
		this.server.stop();
	}
	
	/*------------------------------------------------------------
	 *	Register Test Cases
	 *------------------------------------------------------------*/
	@Test
	public void testRegisterPen_Success() throws Exception {
		
		logger.debug("---------- START - testRegisterPen_Success()");
		ServiceResponse response = client.register(regData);
		
		Assert.assertNotNull("The returned response object was 'null'.", response);
		Assert.assertEquals("Incorrect ResponseCode received.", ResponseCode.SUCCESS, response.getResponseCode());
		logger.debug("---------- END - testRegisterPen_Success()");
	}
	
	@Test
	public void testRegister_Fail_MissingParameter() throws Exception {
		
		logger.debug("---------- START - testRegister_Fail_MissingParameter()");
		ServiceResponse response = null;
		boolean expThrown = false;
		try {
			response = client.register(errorRegData1);
		} catch (InvalidParameterException e) {
			String msg = "InvalidParameterException thrown";
			expThrown = true;
		}
		Assert.assertTrue("The 'InvalidParameterException' was NOT thrown.", expThrown);
		logger.debug("---------- END - testRegister_Fail_MissingParameter()");
	}
	
	/*------------------------------------------------------------
	 *	Find Registration Test Cases
	 *------------------------------------------------------------*/
	@Test
	public void testFindUniqueRegistration_Success() throws Exception {
		
		logger.debug("---------- START - testFindUniqueRegistration_Success()");
		
		RegistrationResponse response = client.findUniqueRegistration(APP_ID_1, PEN_DISPLAY_ID_1, EMAIL_1);
		Assert.assertNotNull("The returned response object was 'null'.", response);
		Assert.assertEquals("Incorrect response returned.", "United States", response.getRegistrationDto().getCountry());
		
		logger.debug("---------- END - testFindUniqueRegistration_Success()");
	}
	
	@Test
	public void testFindRegistrationByPenSerial_Success() throws Exception {
		
		logger.debug("---------- START - testFindRegistrationByPenSerial_Success()");
		
		RegistrationListResponse response = client.findRegistrationsListByPenSerial(PEN_DISPLAY_ID_1);
		Assert.assertNotNull("The returned response object was 'null'.", response);
		
		List<RegistrationDTO> list = response.getRegistrations();
		Assert.assertEquals("Incorrect number of registrations returned.", 3, list.size());
		
		logger.debug("---------- END - testFindRegistrationByPenSerial_Success()");
	}
	
	@Test
	public void testFindRegistrationByPenSerial_Fail_RegistrationNotFound() {
		
		logger.debug("---------- START - testFindRegistrationByPenSerial_Fail_RegistrationNotFound()");
		RegistrationListResponse response = null;
		boolean correctExpThrown = false;
		try {
			response = client.findRegistrationsListByPenSerial(PEN_DISPLAY_ID_NON_EXISTENT);
		} catch (IllegalStateException ise) {
			String msg = "IllegalStateException thrown";
			correctExpThrown = false;
		} catch (InvalidParameterException ipe) {
			String msg = "InvalidParameterException thrown";
			correctExpThrown = false;
		} catch (RegistrationNotFoundException rnfe) {
			String msg = "RegistrationNotFoundException thrown";
			correctExpThrown = true;
		} catch (ClientException ce) {
			String msg = "ClientException thrown";
			correctExpThrown = false;
		} catch (IOException ioe) {
			String msg = "IOException thrown";
			correctExpThrown = false;
		}
		if (response != null) {
			
			List<RegistrationDTO> list = response.getRegistrations();
			logger.debug("list contains " + list.size() + " registrations.");
			RegistrationDTO reg = list.get(0);
			logger.debug(reg.toString());
		}
		Assert.assertTrue("Incorrect exception thrown.", correctExpThrown);
		Assert.assertNull("The RegistrationListResponse object was NOT 'null'.", response);
		logger.debug("---------- END - testFindRegistrationByPenSerial_Fail_RegistrationNotFound()");
	}
	
	/*------------------------------------------------------------
	 *	Find Registration History Test Cases
	 *------------------------------------------------------------*/

	@Test
	public void testFindRegistrationHistoryByEmail_Success() throws InvalidParameterException, RegistrationNotFoundException, ClientException {
		
		logger.debug("---------- START - testFindRegistrationHistoryByEmail_Success()");
		
		RegistrationHistoryListResponse response = client.findRegistrationHistoryByEmail(XML_LOADED_REGISTRATION_HISTORY_EMAIL_1);
		Assert.assertNotNull("The returned RegistrationHistoryListResponse was 'null'.", response);
		
		List<RegistrationHistoryDTO> list = response.getRegistrationHistories();
		Assert.assertNotNull("The returned List was 'null'.", list);
		Assert.assertEquals("Incorrect number of RegistrationHistory objects was returned.", 4, list.size());
		
		RegistrationHistoryDTO regHistDto = list.get(0);
		Assert.assertEquals("Incorrect email address returned.",  XML_LOADED_REGISTRATION_HISTORY_EMAIL_1, regHistDto.getEmail());
		
		logger.debug("---------- END - testFindRegistrationHistoryByEmail_Success()");
	}
	
	@Test
	public void testFindRegistrationHistoryByPenSerial_Success() throws InvalidParameterException, RegistrationNotFoundException, ClientException {
		
		logger.debug("---------- START - testFindRegistrationHistoryByPenSerial_Success()");
		
		RegistrationHistoryListResponse response = client.findRegistrationHistoryByPenSerial(XML_LOADED_REGISTRATION_HISTORY_PEN_SERIAL_1);
		Assert.assertNotNull("The returned RegistrationHistoryListResponse was 'null'.", response);
		
		List<RegistrationHistoryDTO> list = response.getRegistrationHistories();
		Assert.assertNotNull("The returned List was 'null'.", list);
		Assert.assertEquals("Incorrect number of RegistrationHistory objects was returned.", 2, list.size());
		
		RegistrationHistoryDTO regHistDto = list.get(0);
		Assert.assertEquals("Incorrect pen display ID returned.",  XML_LOADED_REGISTRATION_HISTORY_PEN_SERIAL_1, regHistDto.getDisplayId());
		
		logger.debug("---------- END - testFindRegistrationHistoryByPenSerial_Success()");
	}
	
	/*------------------------------------------------------------
	 *	Find Warranty Test Cases
	 *------------------------------------------------------------*/

	@Test
	public void testFindWarrantyByEmail_Success() throws InvalidParameterException, RegistrationNotFoundException, ClientException {
		
		logger.debug("---------- START - testFindWarrantyByEmail_Success()");
		WarrantyListResponse response = client.findWarrantyByEmail(XML_LOADED_WARRANTY_EMAIL_1);
		Assert.assertNotNull("The returned response was 'null'.", response);
		List<WarrantyDTO> list = response.getWarranties();
		Assert.assertEquals("Incorrect number of WarrantyDTO objects returned.", 3, list.size());
		logger.debug("---------- END - testFindWarrantyByEmail_Success()");
	}

	@Test
	public void testFindWarrantyByPenSerial_Success() throws InvalidParameterException, RegistrationNotFoundException, MultipleRecordsFoundException, ClientException {
		 
		logger.debug("---------- START - testFindWarrantyByPenSerial_Success()");
		WarrantyResponse response = client.findWarrantyByPenSerial(XML_LOADED_WARRANTY_PEN_SERIAL_1);
		Assert.assertNotNull("The returned response was 'null'.", response);
		
		WarrantyDTO warrantyDto = response.getWarrantyDto();
		Assert.assertNotNull("The returned WarrantyDTO was 'null'.", warrantyDto);
		
		String email = warrantyDto.getEmail();
		Assert.assertEquals("Incorrect email address returned in WarrantyDTO.", XML_LOADED_WARRANTY_EMAIL_1, email);
		
		logger.debug("---------- END - testFindWarrantyByPenSerial_Success()");
	}
	
	/*------------------------------------------------------------
	 *	Delete Registration Test Cases
	 *------------------------------------------------------------*/
	
	/**
	 * <p></p>
	 * 
	 * NOTE:  This test DOES NOT verify that a record was, in fact, 
	 * deleted.&nbsp;&nbsp;It verifies that the client can parse the
	 * &quot;SUCCESS&quot; XML response message that it receives.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDeleteByEmail_Success() throws SQLException {
		
		logger.debug("---------- START - testDeleteByEmail_Success()");

		//	Delete the record.
		try {
			client.deleteByEmail(XML_LOADED_REGISTRATION_EMAIL_1);
		} catch (InvalidParameterException ipe) {
			Assert.fail("InvalidParameterException thrown");
		} catch (ClientException ce) {
			Assert.fail("ClientException thrown");
		}
				
		logger.debug("---------- END - testDeleteByEmail_Success()");
	}
	
	@Test
	public void testDeleteByEmail_Fail_NoEmailAddress() {
		
		logger.debug("---------- START - testDeleteByEmail_Fail_NoEmailAddress()");

		//	Delete the record.
		try {
			client.deleteByEmail("");
		} catch (InvalidParameterException ipe) {
			logger.debug(ipe.getMessage());
			Assert.assertTrue("InvalidParameterException was NOT thrown!", true);
			logger.debug("---------- END - testDeleteByEmail_Fail_NoEmailAddress()");
			return;
		} catch (ClientException ce) {
			Assert.fail("ClientException thrown");
		}
		
		Assert.fail("No exception was thrown.");
		
		logger.debug("---------- END - testDeleteByEmail_Fail_NoEmailAddress()");
	}
}

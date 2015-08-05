/**
 * Created:  Aug 27, 2013 1:06:52 PM
 */
package com.livescribe.web.registration.service;

import java.util.List;

import junit.framework.Assert;

import org.dbunit.Assertion;
import org.dbunit.dataset.ITable;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.livescribe.web.registration.BaseTest;
import com.livescribe.web.registration.TestConstants;
import com.livescribe.web.registration.controller.RegistrationData;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.exception.UnsupportedPenTypeException;
import com.livescribe.web.registration.mock.MockRegistrationDataFactory;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegistrationServiceImplTest extends BaseTest implements TestConstants {

	private static final String VALID_VECTOR_SERIAL_NUMBER				= "2594172882907";
	private static final String INVALID_VECTOR_SERIAL_NUMBER			= "2594171696607";
	private static final String MISSING_VECTOR_SERIAL_NUMBER			= "2594172883077";
	private static final String MULTIPLE_RECORDS_VECTOR_SERIAL_NUMBER	= "2594160247175";
	
	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private RegistrationHistoryService registrationHistoryService;

	private RegistrationData data;
	private RegistrationData existing;
	private RegistrationData existingVector;
	
	private static boolean isDbInitialized = false;
	
	/**
	 * <p></p>
	 * 
	 */
	public RegistrationServiceImplTest() {
		super();
		logger.debug("Instantiated.");
	}

	@Before
	public void setUp() throws Exception {
		
		String method = "setUp()";
		
		logger.debug("BEFORE - " + method);

		data = new RegistrationData();
		data.setAppId("APP-ID-TEST-12345");
		data.setDisplayId("DEVICE-ID-TEST-67890");
		data.setEmail("lester01@ls.com");
		data.setFirstName("Lester");
		data.setLastName("the Tester");
		data.setLocale("en_GB");
		data.setCountry("US");
		data.setOptIn(false);
		data.setPenName("Lester's Test Pen");
		data.setPenSerial("2594171696607");			//	Not a Vector pen.
		data.setEdition(0);
		
		existing = new RegistrationData();
		existing.setAppId("DbUnit-Loader-1");
		existing.setDisplayId("AYE-ARE-CA3-TP");
		existing.setEmail("lester02@ls.com");
		existing.setFirstName("Lester");
		existing.setLastName("the Tester #2");
		existing.setLocale("en_US");
		existing.setCountry("US");
		existing.setOptIn(false);
		existing.setPenName("Lester's Test Pen #2");
		existing.setPenSerial("2594171696607");		//	Not a Vector pen.
		existing.setEdition(0);
		
//		if (!isDbInitialized) {
			System.out.println("Initializing Pen and Registration testing data...");
			logger.debug("Initializing Pen and Registration testing data...");
			setUpRegistrationTable();
			loadPenTable();
			isDbInitialized = true;
//		}
		logger.debug("AFTER - " + method);
	}
	
//	@After
//	public void tearDown() throws Exception {
//		
//		unloadPenTable();
//		unloadRegistrationTable();
//	}
	
	@Test
	@Transactional("registration")
	public void testRegister() throws Exception {
	
		//	2594172911908
		RegistrationData vectorData = MockRegistrationDataFactory.create("RegistrationServiceImplTest.testRegister()", "AYE-ASX-CM4-KB", "lester01@ls.com");
		vectorData.setFirstName("Bob");
		
		Registration reg = registrationService.register(vectorData);
		Assert.assertNotNull("The returned Registration object was 'null'.", reg);
		Assert.assertEquals("Incorrect first name found in Registration object.",  "Bob", reg.getFirstName());
		
		Long regId = reg.getRegistrationId();
		Assert.assertNotNull("The registration ID returned was 'null'.", regId);
		
		ITable actualTable = getActualTable("registration");
		ITable expectedTable = getExpectedTable("registration");
		
		Assertion.assertEquals(expectedTable, actualTable);
	}
	
	@Test
	@Transactional("registration")
	public void testFind_RegistrationData_Existing() throws MultipleRecordsFoundException {
		
		Registration registration = registrationService.find(existing);
		
		Assert.assertNotNull("The returned Registration object was 'null'.", registration);
		Long regId = registration.getRegistrationId();
		Assert.assertNotNull("The returned Registration object did not have an ID.", regId);
		
		Assert.assertEquals("Incorrect App ID found.", "DbUnit-Loader-1", registration.getAppId());
		Assert.assertEquals("Incorrect ID for registration returned.", new Long(10000L), regId);
	}
	
	@Test
	@Transactional("registration")
	public void testFind_RegistrationData_NotFound() throws MultipleRecordsFoundException {
		
		Registration registration = registrationService.find(data);
		
		Assert.assertNull("The returned Registration object was NOT 'null'.", registration);
	}
	
	@Test
	@Transactional("manufacturing")
	public void testIsPenSerialNumberIdValid_Success() throws MultipleRecordsFoundException, UnsupportedPenTypeException {
		
		boolean valid = registrationService.isPenSerialNumberValid(VALID_VECTOR_SERIAL_NUMBER);
		Assert.assertTrue("The serial number '" + VALID_VECTOR_SERIAL_NUMBER + "' WAS NOT valid.", valid);
	}
	
	@Test
	@Transactional("manufacturing")
	public void testIsPenSerialNumberIdValid_Fail_DuplicateRecords() {
		
		boolean valid;
		boolean exceptionThrown = false;
		try {
			valid = registrationService.isPenSerialNumberValid(MULTIPLE_RECORDS_VECTOR_SERIAL_NUMBER);
		} catch (MultipleRecordsFoundException mrfe) {
			exceptionThrown = true;
		} catch (UnsupportedPenTypeException e) {
			exceptionThrown = false;
		}
		Assert.assertTrue("The 'MultipleRecordsFoundException' WAS NOT thrown.", exceptionThrown);
	}

	@Test
	@Transactional("manufacturing")
	public void testIsPenSerialNumberIdValid_Fail_MissingRecord() throws MultipleRecordsFoundException, UnsupportedPenTypeException {
		
		boolean valid = registrationService.isPenSerialNumberValid(MISSING_VECTOR_SERIAL_NUMBER);
		Assert.assertFalse("The serial number '" + MISSING_VECTOR_SERIAL_NUMBER + "' WAS valid.", valid);
	}

	@Test
	@Transactional("registration")
	public void testSave() {
		
		Registration registration = registrationService.save(data);
		
		Assert.assertNotNull("The returned Registration object was 'null'.", registration);
		Assert.assertNotNull("The returned Registration object did not have an ID.", registration.getRegistrationId());
		
		Assert.assertEquals("Incorrect App ID found.", "APP-ID-TEST-12345", registration.getAppId());
	}
	
//	@Test
	@Transactional("registration")
	public void testUnregister() {
		
	}
	
	@Test
	@Transactional("registration")
	public void testDeleteByEmail_Success() {
		
		registrationService.deleteByEmail(XML_LOADED_REGISTRATION_HISTORY_EMAIL_1);
		
		try {
			List<Registration> list = registrationService.findByEmail(XML_LOADED_REGISTRATION_HISTORY_EMAIL_1);
		} catch (RegistrationNotFoundException rnfe) {
			String msg = "RegistrationNotFoundException thrown";
			Assert.assertTrue("RegistrationNotFoundException not thrown!", true);
		}
		
		List<RegistrationHistory> rhList = null;
		try {
			rhList = registrationHistoryService.findByEmail(XML_LOADED_REGISTRATION_HISTORY_EMAIL_1);
		} catch (RegistrationNotFoundException rnfe) {
			Assert.fail("RegistrationNotFoundException thrown");
		}
		
		Assert.assertNotNull("The list of registration history records was 'null'", rhList);
		Assert.assertEquals("Incorrect number of registration history records returned.", 1, rhList.size());
	}
	
//	@Test
//	@Transactional
//	public void testSaveOrUpdate() throws MultipleRecordsFoundException {
//		
//		Registration registration = registrationService.saveOrUpdate(data);
//	}
}

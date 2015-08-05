package com.livescribe.web.registration.service;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.livescribe.web.registration.BaseTest;

public class RegistrationHistoryServiceImplTest extends BaseTest  {

	private static final String VALID_VECTOR_SERIAL_NUMBER				= "2594172882907";
	
	@Autowired
	private RegistrationHistoryService registrationHistoryService;
	
	private static boolean isDbInitialized = false;
	
	/**
	 * <p></p>
	 */
	public RegistrationHistoryServiceImplTest() {
		super();
	}
	
	@Before
	public void setUp() throws Exception {
		if (!isDbInitialized) {
			System.out.println("Initializing RegistrationHistory testing data...");
			setUpRegistrationHistoryTable();
			isDbInitialized = true;
		}
	}
	
//	@After
//	public void tearDown() throws Exception {
//		
//		unloadRegistrationHistoryTable();
//	}
	
	@Test
	@Transactional("registration")
	public void testFindRegistrationHistoryByEmail() throws Exception {
		List<RegistrationHistory> regHistoryList = registrationHistoryService.findByEmail("lester02@ls.com");
		Assert.assertTrue("No RegistrationHistory FOUND.", regHistoryList.size() > 0);
	}
	
	@Test
	@Transactional("registration")
	public void testFindRegistrationHistoryByPenSerial() throws Exception {
		List<RegistrationHistory> regHistoryList = registrationHistoryService.findByPenSerial(VALID_VECTOR_SERIAL_NUMBER);
		Assert.assertNotNull("No RegistrationHistory FOUND.", regHistoryList);
	}
	
}

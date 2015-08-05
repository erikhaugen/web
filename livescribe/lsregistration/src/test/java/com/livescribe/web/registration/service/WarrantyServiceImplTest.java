package com.livescribe.web.registration.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.framework.orm.vectordb.Warranty;
import com.livescribe.web.registration.BaseTest;

public class WarrantyServiceImplTest extends BaseTest  {

	@Autowired
	private WarrantyService warrantyService;
	
	private static boolean isDbInitialized = false;
	
	//	Loaded from 'data/setup/warranty.xml'.
	private static final String EXISTING_EMAIL			= "lester02@ls.com";
	private static final String EXISTING_PEN_DISPLAY_ID	= "AYE-ARE-CA3-TP";
	private static final String EXISTING_PEN_SERIAL		= "2594171696607";
	
	/**
	 * <p></p>
	 */
	public WarrantyServiceImplTest() {
		super();
	}
	
	@Before
	public void setUp() throws Exception {
//		if (!isDbInitialized) {
			System.out.println("Initializing Warranty testing data...");
			setUpWarrantyTable();
			isDbInitialized = true;
//		}
	}
	
	@Test
	@Transactional("registration")
	public void testFindWarrantyByEmail() throws Exception {
		List<Warranty> warrantyList = warrantyService.findByEmail(EXISTING_EMAIL);
		Assert.assertTrue("No Warranty FOUND.", warrantyList.size() > 0);
	}
	
	@Test
	@Transactional("registration")
	public void testFindWarrantyByPenSerial() {
		Warranty warranty = null;
		try {
			warranty = warrantyService.findByPenSerial(EXISTING_PEN_SERIAL);
		} catch (Exception e) {
			String msg = "Exception thrown";
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull("No Warranty FOUND.", warranty);
	}
	
}

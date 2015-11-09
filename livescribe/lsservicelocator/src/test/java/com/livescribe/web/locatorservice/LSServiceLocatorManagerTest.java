package com.livescribe.web.locatorservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.livescribe.base.StringUtils;
import com.livescribe.base.constants.LSConstants;
import com.livescribe.base.utils.MiscEncryptionUtils;
import com.livescribe.framework.services.response.ServiceResponse;
import com.livescribe.framework.services.response.ServiceResponse.ResponseCode;
import com.livescribe.lsconfig.LSConfigurationFactory;
import com.livescribe.servicelocator.ConfigClient;
import com.livescribe.servicelocator.LSServiceLocatorManager;
import com.livescribe.servicelocator.dao.ServiceLocatorDao;
import com.livescribe.servicelocator.dao.data.ServiceLocator;
import com.livescribe.servicelocator.dao.hbimpl.HbDaoFactory;

public class LSServiceLocatorManagerTest extends BaseTest {

	private ServiceLocatorDao serviceLocatorDao = null;
	private String services = "Authentication,LIVESCRIBE,/services/authentication,WWW,true,true&" +
								"Community,LIVESCRIBE,/services/community,WWW,true,true&" +
								"Download,LIVESCRIBE,/services/download,WWW,true,false&" +
								"Registration,LIVESCRIBE,/services/registration,WWW,true,false&" +
								"Storage,LIVESCRIBE,/services/storage,WWW,true,false&" +
								"Upload,LIVESCRIBE,/services/upload,WWW,true,false&" +
								"Test,LIVESCRIBE,/services/test,ADMIN,true,false";

	private int servicesCount = StringUtils.countOccurances(services, "&");
	private Map<String, ServiceLocator> parsedLocators = new HashMap<String, ServiceLocator>();

	@Override
	public  void setUp() throws Exception  {
		ConfigClient.getName();
		serviceLocatorDao = LSConfigurationFactory.getBean("daoFactory", HbDaoFactory.class).getServiceLocatorDao();
        
        StringTokenizer tokenizer = new StringTokenizer(services, "&");
        while (tokenizer.hasMoreTokens() ) {
        	ServiceLocator locator = ServiceLocator.parse(tokenizer.nextToken(), ",");
        	serviceLocatorDao.insertServiceLocator(locator);
        	parsedLocators.put(locator.getName(), locator);
        }
        
        //Need test shared secret key
        System.setProperty(LSConstants.SHARED_SECRET_RUNTIME_KEY, "Y3K0tpyRc!t3sTb3w3nG1n33R1ng");
	}
	
	public void testReads() throws Exception {
		
		List<ServiceLocator> locators = serviceLocatorDao.getAllServices();
		assertNotNull(locators);
		assertEquals(parsedLocators.size(), locators.size());
		assertEquals(servicesCount, locators.size());
		for ( ServiceLocator locator : locators ) {
			ServiceLocator parsedLocator = parsedLocators.get(locator.getName());
			assertNotNull(parsedLocator);
			assertEquals(parsedLocator, locator);
		}
	}

	public void testVerifySignature() throws Exception {
		LSServiceLocatorManager manager = LSServiceLocatorManager.getInstance();
		//Get sharedSecretKey for id
		//Right now it will be a system property defined at launch
		String idSecretKey = System.getProperty(LSConstants.SHARED_SECRET_RUNTIME_KEY);
		//Set the data to sign
		String input = new String("This is some text to convert to a signature");
		//Set some bad data
		String badInput = new String("This is some other text to verifySignature on, should fail");
		//set the id for verifySignature call
		String id = new String("0");
		//create signature of the input based on sharedSecretKey
		String signature = MiscEncryptionUtils.getBase64HMacSha1Signature(input, idSecretKey);
		assertTrue(manager.verifySignature(input, id, signature).isSuccessResponse());
		System.out.println("\t\tmanager.verifySignature(input, id, signature) successful as expected");
		assertFalse(manager.verifySignature("", id, signature).isSuccessResponse());
		System.out.println("\t\tmanager.verifySignature(\"\", id, signature)  un-successful as expected");
		assertFalse(manager.verifySignature(input, "", signature).isSuccessResponse());
		System.out.println("\t\tmanager.verifySignature(input, \"\", signature)  un-successful as expected");
		assertFalse(manager.verifySignature(input, id, "").isSuccessResponse());
		System.out.println("\t\tmanager.verifySignature(input, id, \"\")  un-successful as expected");
		assertNull(manager.verifySignature(badInput, id, signature));
		System.out.println("\t\tmanager.verifySignature(badInput, id, signature) un-successful as expected");
	}
}

package com.livescribe.web.locatorservice.dao.data;

import com.livescribe.servicelocator.dao.data.ServiceLocator;
import com.livescribe.web.locatorservice.BaseTest;


public class ServiceLocatorTest extends BaseTest {

	public void testParse() {
		String str = "Authentication,LIVESCRIBE,/services/authentication,WWW,true,true";
		
		ServiceLocator locator = ServiceLocator.parse(str, ",");
		
		assertNotNull(locator);
		assertEquals("Authentication", locator.getName());
		assertEquals("LIVESCRIBE", locator.getDomain());
		assertEquals("/services/authentication", locator.getUriSuffix());
		assertEquals("WWW", locator.getHostKey());
		assertTrue( locator.isSecure());
		assertTrue(locator.isActive());
	}
	
	public void testPrint() {
		ServiceLocator locator = new ServiceLocator();
		
		locator.setName("Authentication");
		locator.setDomain("LIVESCRIBE");
		locator.setUriSuffix("/services/authentication");
		locator.setHostKey("ADMIN");
		locator.setActive(false);
		locator.setSecure(false);
		
		String print = locator.toString();
		
		assertNotNull(print);
		assertEquals("Authentication,LIVESCRIBE,/services/authentication,ADMIN,false,false", print);
		
	}
	
	public void testUri() {
		ServiceLocator locator = new ServiceLocator();
		
		locator.setName("Authentication");
		locator.setDomain("LIVESCRIBE");
		locator.setUriSuffix("/services/authentication");
		locator.setHostKey("WWW");
		locator.setActive(false);
		locator.setSecure(false);
		
		String uri = locator.getUri();
		
		assertNotNull(uri);
		assertEquals("http://localhost/services/authentication", uri);
		
		locator.setHostKey("ADMIN");
		uri = locator.getUri();
		
		assertNotNull(uri);
		assertEquals("http://127.0.0.1/services/authentication", uri);

	}
}

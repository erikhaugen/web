/**
 * 
 */
package com.livescribe.aws.login.client;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import com.livescribe.framework.login.BaseTest;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ObjectFactoryTest extends BaseTest {

	private static String UID	= "6ccd780c-baba-1026-9564-0040f4311e29";
	private Document successDoc;
	private Document errorDoc;
	
	/**
	 * <p></p>
	 *
	 */
	public ObjectFactoryTest() {
		super();
	}

	@Before
	public void setUp() throws FileNotFoundException, UnsupportedEncodingException, DocumentException {
		
//		InputStream inSuccess = this.getClass().getClassLoader().getResourceAsStream("data/setup/response_success.xml");
		successDoc = readXMLFile("data/setup/response_success.xml");		
		errorDoc = readXMLFile("data/setup/response_error.xml");		
	}

	/**
	 * <p></p>
	 * 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws DocumentException
	 */
	private Document readXMLFile(String path) throws FileNotFoundException, UnsupportedEncodingException, DocumentException {
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
		if (in == null) {
			logger.error("Unable to load XML file:  " + path);
			throw new FileNotFoundException();
		}
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(buffReader);
		return doc;
	}
	
	@Test
	public void testParseObject() {
		
		Object obj = ObjectFactory.parseDocument(successDoc);
		assertNotNull("The returned Object was 'null'.", obj);
		assertTrue("The returned Object was not a String.", obj instanceof String);
		String uid = (String)obj;
		assertEquals("The returned 'uid' was incorrect.", UID, uid);
	}
	
	@Test
	public void testParseError() {
		
		Object obj = ObjectFactory.parseError(errorDoc);
		assertNotNull("The returned Object was 'null'.", obj);
		assertTrue("The returned Object was not a String.", obj instanceof String);
		String err = (String)obj;
		assertEquals("The returned error was incorrect.", "EMAIL_ALREADY_IN_USE", err);
	}
}

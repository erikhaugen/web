/**
 * 
 */
package com.livescribe.community.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.community.BaseTest;
import com.livescribe.community.config.CommunityProperties;
import com.livescribe.community.orm.PencastPage;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class FlashXmlParserTest extends BaseTest {

	@Autowired
	private CommunityProperties communityProperties;
	
	/**
	 * <p></p>
	 */
	public FlashXmlParserTest() {
		super();
	}

	/**
	 * 
	 */
	@Test
	public void testParse() {

		String methodName = new Exception().getStackTrace()[0].getMethodName();
		String filePathPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.flashXmlPath";
		String TEST_FLASH_XML_PATH = communityProperties.getProperty(filePathPropKey);
		
		File file = new File(TEST_FLASH_XML_PATH);
		
		if (file.exists()) {
			Document doc = FlashXmlParser.parse(file);
		
			assertNotNull("The returned Document object was 'null'.", doc);
		}
		else {
			//assertTrue("The test 'flash.xml' files do not exist on this computer.  Unable to complete this test.  See /src/test/resources/configuration/<env>/community.properties.", false);
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetPages() {
		
		String methodName = new Exception().getStackTrace()[0].getMethodName();
		String filePathPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.flashXmlPath";
		String derivPathPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.derivativePath";
		String TEST_FLASH_XML_PATH = communityProperties.getProperty(filePathPropKey);
		String TEST_DERIVATIVE_PATH = communityProperties.getProperty(derivPathPropKey);
		
		File file = new File(TEST_FLASH_XML_PATH);
		
		if (file.exists()) {
			Document doc = FlashXmlParser.parse(file);
			List<PencastPage> pages = FlashXmlParser.getPages(doc, TEST_DERIVATIVE_PATH);
			
			assertNotNull("The returned List of pencast pages was 'null'.", pages);
			assertEquals("Incorrect number of pages returned.", 3, pages.size());
		}
		else {
			//assertTrue("The test 'flash.xml' files do not exist on this computer.  Unable to complete this test.  See /src/test/resources/configuration/<env>/community.properties.", false);
		}
	}
}

/**
 * Created:  Dec 9, 2014 2:10:34 PM
 */
package org.kfm.camel.recipientlist;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

import com.livescribe.afp.Afd;
import com.livescribe.afp.AfdFactory;
import com.livescribe.web.lssettingsservice.client.Setting;
import com.livescribe.web.lssettingsservice.client.SettingNamespace;
import com.livescribe.web.lssettingsservice.client.SettingType;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration	//(locations = "FetchNotebooksProcessorTest-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(false)
public class FindPenNameTest {

	private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    protected CamelContext camelContext;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:results")
    protected MockEndpoint mock;

    private Afd afd;
    
	/**
	 * <p></p>
	 * 
	 */
	public FindPenNameTest() {
	}

	public void setUp() throws URISyntaxException {
		
		URL url = getClass().getClassLoader().getResource("AYE-ARE-4R3-WB_A5_Wifi_Starter_Notebook_Time_0.zip");
		File afdFile = new File(url.toURI());
		if (!afdFile.exists()) {
			logger.error("testProcess() - Unable to locate test AFD file.");
			assertFalse("Unable to locate test AFD file.", true);
			return;
		}
		afd = AfdFactory.create(afdFile);
		logger.debug("testProcess() - Found AFD with GUID: " + afd.getGuid());
		
	}
	
	@DirtiesContext
	@Test
	public void testGetSetting() throws InterruptedException {
		
		String expectedBody = "<setting key=\"penName\" value=\"BgALRGF3bidzIFBlbgA=\" type=\"device\" meta=\"BAAAAAMEAAAAAAIA\" modificationoffset=\"\"/>";
		SettingNamespace namespace = new SettingNamespace();
		namespace.setName("system");
		Setting expectedSetting = new Setting(namespace, SettingType.DEVICE, "penName", "BgALRGF3bidzIFBlbgA=", "BAAAAAMEAAAAAAIA");
		
		mock.expectedBodiesReceived(expectedBody);
		mock.expectedMessageCount(1);
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("displayId", "2594171716827");
		headers.put("uid", "9v8VcCCLsHtw");
		headers.put("accessToken", "S=s1:U=6b7fb:E=1512c943e0d:C=149d4e31038:P=18d:A=ls-web-test:V=2:H=2fd4fe454d8c39791ed922c32ca6ddcb");
		headers.put("enUserId", Long.valueOf(440315L));
		
//		template.sendBodyAndHeaders("activemq:queue:findExistingNotebooks?jmsMessageType=Object", afd, headers);
		template.sendBodyAndHeaders(afd, headers);
		
		Exchange outExchange = mock.getReceivedExchanges().get(0);
		Setting setting = outExchange.getIn().getBody(Setting.class);
		
		assertNotNull("The returned Setting was 'null'.", setting);
		assertEquals("Incorrect 'value' returned.", "BgALRGF3bidzIFBlbgA=", (String)setting.getValue());
		assertEquals("Incorrect 'meta' returned.", "BAAAAAMEAAAAAAIA", (String)setting.getMeta());
		
//		mock.assertIsSatisfied();
	}
}

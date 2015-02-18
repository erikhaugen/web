/**
 * Created:  Dec 8, 2014 6:15:21 PM
 */
package org.kfm.camel.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
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
import org.junit.runners.model.InitializationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

import com.evernote.edam.type.Notebook;
import com.livescribe.afp.Afd;
import com.livescribe.afp.AfdFactory;

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
public class FetchNotebooksProcessorTest {	//extends CamelSpringJUnit4ClassRunner {	//CamelSpringTestSupport { //CamelTestSupport {

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
     * @throws InitializationError 
	 * 
	 */
	public FetchNotebooksProcessorTest() throws InitializationError {
		
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
	public void testProcess() throws URISyntaxException {
		
//		URL url = getClass().getClassLoader().getResource("AYE-ARE-4R3-WB_A5_Wifi_Starter_Notebook_Time_0.zip");
//		File afdFile = new File(url.toURI());
//		if (!afdFile.exists()) {
//			logger.error("testProcess() - Unable to locate test AFD file.");
//			assertFalse("Unable to locate test AFD file.", true);
//			return;
//		}
//		Afd afd = AfdFactory.create(afdFile);
//		logger.debug("testProcess() - Found AFD with GUID: " + afd.getGuid());
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("displayId", "AYE-ARE-4R3-WB");
		headers.put("uid", "9v8VcCCLsHtw");
		headers.put("accessToken", "S=s1:U=6b7fb:E=1512c943e0d:C=149d4e31038:P=18d:A=ls-web-test:V=2:H=2fd4fe454d8c39791ed922c32ca6ddcb");
		headers.put("enUserId", Long.valueOf(440315L));
		
//		template.sendBodyAndHeaders("activemq:queue:findExistingNotebooks?jmsMessageType=Object", afd, headers);
		template.sendBodyAndHeaders(afd, headers);
		
		mock.expectedMessageCount(3);
		Exchange outExchange = mock.getReceivedExchanges().get(0);
		List<Notebook> notebooks = outExchange.getIn().getBody(List.class);
		assertEquals("Incorrect number of Notebook records found in Evernote.", 3, notebooks.size());
	}
}

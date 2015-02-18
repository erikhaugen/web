/**
 * Created:  Dec 10, 2014 12:03:59 PM
 */
package org.kfm.camel.strategy;

import static org.junit.Assert.*;

import java.io.File;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kfm.jpa.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

import com.evernote.edam.type.Notebook;
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
@ContextConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(false)
public class ExistingInfoAggregationStrategyTest {

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
	public ExistingInfoAggregationStrategyTest() {
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
	
	@Test
	public void testAggregate() {
		
		Document document = new Document();
		document.setArchive(BigInteger.valueOf(0L));
		document.setCopy(BigInteger.valueOf(0L));
		document.setCreated(new Date());
		document.setLastModified(new Date());
		document.setDocName("Lined Journal 2");
		document.setEnNotebookGuid("da7eddf1-b021-49ff-8554-c5ed438ab117");
		document.setPenDisplayId("AYE-ARE-4R3-WB");
		document.setEnUserId(BigInteger.valueOf(440315L));
		document.setUid("9v8VcCCLsHtw");
		document.setGuid("0x1a3d8e76d92a5747");

		Notebook notebook = new Notebook();
		notebook.setGuid("da7eddf1-b021-49ff-8554-c5ed438ab117");
		notebook.setName("Lined Journal 2");
		notebook.setStack("Penelope");
		
		String expectedBody = "<setting key=\"penName\" value=\"BgALRGF3bidzIFBlbgA=\" type=\"device\" meta=\"BAAAAAMEAAAAAAIA\" modificationoffset=\"\"/>";
		SettingNamespace namespace = new SettingNamespace();
		namespace.setName("system");
		Setting setting = new Setting(namespace, SettingType.DEVICE, "penName", "BgALRGF3bidzIFBlbgA=", "BAAAAAMEAAAAAAIA");
		
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.getIn().setHeader("displayId", "AYE-ARE-4R3-WB");
		exchange.getIn().setHeader("uid", "9v8VcCCLsHtw");
		exchange.getIn().setHeader("enUserId", 440315L);
		exchange.getIn().setHeader("accessToken", "S=s1:U=6b7fb:E=1512c943e0d:C=149d4e31038:P=18d:A=ls-web-test:V=2:H=2fd4fe454d8c39791ed922c32ca6ddcb");
		exchange.getIn().setHeader("penSerial", 2594171716827L);
		exchange.getIn().setHeader("CamelFileName", "AYE-ARE-4R3-WB_A5_Wifi_Starter_Notebook_Time_0.zip");
		
		exchange.getIn().setBody(afd);
		
		template.send(exchange);
		ArrayList<Document> docList = new ArrayList<Document>();
		docList.add(document);
		template.sendBodyAndHeader(docList, "CamelFileName", "AYE-ARE-4R3-WB_A5_Wifi_Starter_Notebook_Time_0.zip");
		ArrayList<Notebook> nbList = new ArrayList<Notebook>();
		nbList.add(notebook);
		template.sendBody(nbList);
		
		Exchange newExchange = mock.getReceivedExchanges().get(0);
		assertNotNull("The resulting Exchange was 'null'.", newExchange);
		Boolean mergeComplete = newExchange.getIn().getHeader("mergeComplete", Boolean.class);
		assertTrue("The 'mergeComplete' header was 'false' or not present.", mergeComplete);
	}
}

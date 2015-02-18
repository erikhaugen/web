/**
 * Created:  Dec 19, 2014 1:44:51 PM
 */
package org.kfm.camel.converter;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.kfm.camel.entity.evernote.XMLCoordinate;
import org.kfm.camel.entity.evernote.XMLStroke;
import org.kfm.camel.entity.evernote.XMLStrokes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

import com.livescribe.afp.Afd;
import com.livescribe.afp.AfdFactory;
import com.livescribe.afp.PageStroke;
import com.livescribe.afp.stf.STFStroke;
import com.thoughtworks.xstream.XStream;

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
public class StrokeConverterTest {

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
	public StrokeConverterTest() throws InitializationError {
		DOMConfigurator.configureAndWatch("log4j.xml");
	}

	@Before
	public void setUp() throws URISyntaxException {
		
		URL url = getClass().getClassLoader().getResource("AYE-ARE-4R3-WB_A5_Wifi_Starter_Notebook_Time_0.zip");
		File afdFile = new File(url.toURI());
		if (!afdFile.exists()) {
			logger.error("setUp() - Unable to locate test AFD file.");
			assertFalse("Unable to locate test AFD file.", true);
			return;
		}
		afd = AfdFactory.create(afdFile);
		logger.debug("setUp() - Found AFD with GUID: " + afd.getGuid());
		
	}

	@Test
	public void testFromSTFStrokes() {
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("displayId", "AYE-ARE-4R3-WB");
		headers.put("uid", "9v8VcCCLsHtw");
		headers.put("accessToken", "S=s1:U=6b7fb:E=1512c943e0d:C=149d4e31038:P=18d:A=ls-web-test:V=2:H=2fd4fe454d8c39791ed922c32ca6ddcb");
		headers.put("enUserId", Long.valueOf(440315L));
		headers.put("CamelFileName", "AYE-ARE-4R3-WB_A5_Wifi_Starter_Notebook_Time_0.zip");
		
		List<PageStroke> pageStrokes = afd.getPageStrokes();
		PageStroke pgStroke0 = pageStrokes.get(0);
		Set<STFStroke> stfStrokes0 = pgStroke0.getStrokes();
		
		template.sendBody(stfStrokes0);
		
		mock.expectedMessageCount(1);
		
		Exchange outExchange = mock.getReceivedExchanges().get(0);
		XMLStrokes xmlStrokes = outExchange.getIn().getBody(XMLStrokes.class);
		assertNotNull("The XMLStrokes object was 'null'.", xmlStrokes);
		assertEquals("Incorrect number of XMLStroke records found in converted STFStroke List.", 396, xmlStrokes.getList().size());
		
		XMLStroke xmlStroke = xmlStrokes.getList().get(2);
		List<XMLCoordinate> coordinates = xmlStroke.getCoords();
		assertNotNull("The List of XMLCoordinates from XMLStroke #2 was 'null'.", coordinates);
		assertEquals("Incorrect number of XMLCoordinate objects found in XMLStroke #2.", 43, coordinates.size());
//		printXMLStrokes(xmlStrokes);
	}
	
	private void printXMLStrokes(XMLStrokes xmlStrokes) {
		
		XStream xstream = new XStream();
		xstream.processAnnotations(XMLStrokes.class);
		String xml = xstream.toXML(xmlStrokes);
		logger.debug(xml);
	}
}

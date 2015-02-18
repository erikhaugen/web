/**
 * Created:  Jul 12, 2013 1:02:55 PM
 */
package org.kfm.camel.translator;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@ContextConfiguration(value = "classpath:test-application-context.xml")
public class AfdToXmlTranslatorTest extends CamelTestSupport {

	@EndpointInject(uri = "mock:result")
	protected MockEndpoint resultEndpoint;
	
	@Produce(uri = "direct:start")
	protected ProducerTemplate template;
	
	/**
	 * <p></p>
	 * 
	 */
	public AfdToXmlTranslatorTest() {
	}

	@DirtiesContext
	@Test
	public void testTranslate() {
		
	}
}

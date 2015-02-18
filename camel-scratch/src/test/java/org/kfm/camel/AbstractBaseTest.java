/**
 * Created:  Dec 9, 2014 2:11:27 PM
 */
package org.kfm.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

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
public abstract class AbstractBaseTest {

	private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    protected CamelContext camelContext;

	/**
	 * <p></p>
	 * 
	 */
	public AbstractBaseTest() {
	}

}

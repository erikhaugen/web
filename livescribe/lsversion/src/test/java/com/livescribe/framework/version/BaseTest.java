/**
 * Created:  May 13, 2013 5:00:46 PM
 */
package com.livescribe.framework.version;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@ContextConfiguration(locations={"classpath:test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseTest {

	public Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public BaseTest() {
		DOMConfigurator.configureAndWatch("log4j.xml");
	}
}

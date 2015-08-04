/*
 * Created:  Sep 6, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.heartbeat;

import org.apache.log4j.Logger;
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

	protected Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public BaseTest() {
	}

}

/**
 * Created:  Jul 27, 2013 8:37:43 PM
 */
package com.livescribe;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class BaseTest {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * <p></p>
	 * 
	 */
	public BaseTest() {
		DOMConfigurator.configureAndWatch("log4j.xml");
	}

}

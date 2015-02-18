/**
 * Created:  Dec 23, 2014 12:24:48 PM
 */
package org.kfm.camel.util;

import java.security.SecureRandom;

import org.apache.log4j.Logger;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CAKGenerator {

	private static Logger logger = Logger.getLogger(CAKGenerator.class.getName());

	//	Need to create this random number object only once
	private static SecureRandom random = new SecureRandom();

	private static CAKGenerator instance = new CAKGenerator();
	
	/**
	 * <p></p>
	 * 
	 */
	private CAKGenerator() {
	}

	public static CAKGenerator getInstance() {
		return instance;
	}

	public long getNextRandomNumber() {
		
		long rand = random.nextLong();
		
		if (0L == rand)
			rand = random.nextLong();

		if (0L > rand)
			rand = -rand;

		logger.info("CAKGenerator rand=" + Long.toHexString(rand) + "(" + rand + ")");
		return rand;
	}
}

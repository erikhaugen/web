/**
 * Created:  Nov 18, 2010 3:21:49 PM
 */
package com.livescribe.base.utils;

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class WOAppMigrationUtilsTest extends TestCase {

	/**
	 * <p></p>
	 * 
	 */
	public WOAppMigrationUtilsTest() {
		
	}

	public void testGeneratePrimaryKey() {
		
		byte[] pk = WOAppMigrationUtils.generatePrimaryKey();
		
		assertNotNull("The returned byte[] was 'null'", pk);
		assertEquals("The length of the returned byte[] was incorrect.", 24, pk.length);
		
		String pkStr = WOAppMigrationUtils.convertPrimaryKeyToString(pk);
		System.out.println(pkStr);
	}
}

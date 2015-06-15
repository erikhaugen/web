/**
 * 
 */
package com.livescribe.community.util;

import static org.junit.Assert.*;
import org.junit.Test;

import com.livescribe.community.BaseTest;

/**
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UtilsTest extends BaseTest {
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public UtilsTest() {
		super();
	}
	
	/**
	 * 
	 */
	@Test
	public void testConvertPrimaryKeyToString() {
		
		byte[] primaryKey = {0x00, 0x00, (byte)0xc0, (byte)0xa8, 0x01, 0x15, 0x00, 0x00, 0x09, (byte)0xc6, 0x2a, 0x00, 0x00, 0x00, 0x01, 0x19, 0x77, (byte)0xda, (byte)0xd2, 0x08, (byte)0x99, (byte)0xc4, (byte)0xee, (byte)0xdc};
		
		String actual = Utils.convertPrimaryKeyToString(primaryKey);
		String expected = "0000C0A80115000009C62A000000011977DAD20899C4EEDC";
		
		assertEquals("", expected, actual);
	}
	
	/**
	 * 
	 */
	@Test
	public void testConvertStringToPrimaryKey() {
		
		String method = "testConvertStringToPrimaryKey():  ";
		
//		String keyString = "0000c0a80115000009c62a000000011977dad20899c4eedc";
		String keyString = "0000C0A80115000009C62A000000011977DAD20899C4EEDC";
		
		byte[] actual = Utils.convertStringToPrimaryKey(keyString);
		byte[] expected = {0x00, 0x00, (byte)0xc0, (byte)0xa8, 0x01, 0x15, 0x00, 0x00, 0x09, (byte)0xc6, 0x2a, 0x00, 0x00, 0x00, 0x01, 0x19, 0x77, (byte)0xda, (byte)0xd2, 0x08, (byte)0x99, (byte)0xc4, (byte)0xee, (byte)0xdc};
		
		String actualConverted = Utils.convertPrimaryKeyToString(actual);
		String expectedConverted = Utils.convertPrimaryKeyToString(expected);
		
		logger.debug("        keyString = " + keyString);
		logger.debug("  actualConverted = " + actualConverted);
		logger.debug("expectedConverted = " + expectedConverted);
		
		assertArrayEquals("The string was not converted to a byte[] correctly.  Actual: " + actualConverted + "'", expected, actual);
	}
	
	/**
	 * 
	 */
	@Test
	public void testConvertStringToPrimaryKey2() {
		
		String method = "testConvertStringToPrimaryKey2():  ";
		
		String keyString = "00000A01015500003A98220000000129D733A501E05933A7";
		
		byte[] actual = Utils.convertStringToPrimaryKey(keyString);
		byte[] expected = {0x00, 0x00, 0x0a, 0x01, 0x01, 0x55, 0x00, 0x00, 0x3a, (byte)0x98, 0x22, 0x00, 0x00, 0x00, 0x01, 0x29, (byte)0xD7, (byte)0x33, (byte)0xa5, 0x01, (byte)0xe0, 0x59, 0x33, (byte)0xa7};
		
		String actualConverted = Utils.convertPrimaryKeyToString(actual);
		String expectedConverted = Utils.convertPrimaryKeyToString(expected);
		
		logger.debug("        keyString = " + keyString);
		logger.debug("  actualConverted = " + actualConverted);
		logger.debug("expectedConverted = " + expectedConverted);
		
		assertArrayEquals("The string was not converted to a byte[] correctly.  Actual: " + actualConverted + "'", expected, actual);
	}	
}

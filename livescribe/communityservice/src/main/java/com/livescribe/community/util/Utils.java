/**
 * 
 */
package com.livescribe.community.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This has been deprecated. Please use WOAppMigrationUtils from baseutils project
 * 
 * <p>A collection of utility methods.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */

@Deprecated()
public class Utils {
	
	//private static Logger logger = Logger.getLogger(Utils.class);
	
	/**
	 * <p></p>
	 * 
	 * @param ugFile
	 * 
	 * @return
	 */
	public static String constructDerivativePath(String afdPath) {
		
		int idx = afdPath.indexOf("content.afd");
		String derivPath = afdPath.substring(0, idx) + "derivative";
		
		return derivPath;
	}
	
	/**
	 * @param date
	 * @return
	 */
	public static String convertDateToGMTDateString(Date date) {
		
		//	Convert local date to GMT date String
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
		TimeZone tz = TimeZone.getTimeZone("GMT");
		sdf.setTimeZone(tz);
		String dateStr = sdf.format(date);
		
		return dateStr;
	}
	
	/**
	 * <p>Determines whether the given <code>String</code> is an
	 * </code>Integer</code> number.</p>
	 * 
	 * @param str The <code>String</code> to check.
	 * 
	 * @return <code>true</code> if the given <code>String</code> is
	 * an <code>Integer</code>; <code>false</code> if not.
	 */
	public static boolean isInteger(String str) {
		
		try {
		Integer.parseInt(str);
		}
		catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	/**
	 * <p>Converts the 192-bit primary key to a <code>String</code>.</p>
	 * The resulting string is returned as upper case.
	 * 
	 * @param byteKey The 192-bit primary key as a <code>byte[]</code>.
	 * 
	 * @return a <code>String</code> representation of the given primary key.
	 */
	public static String convertPrimaryKeyToString(byte[] primaryKey) {
		
		StringBuilder hexBuilder = new StringBuilder();
		int mask = 0x000000ff;
		
		for (int i = 0; i < primaryKey.length; i++) {
			int b = primaryKey[i] & mask;
			String strHex = Integer.toHexString(b);
			if (b < 16) {
				hexBuilder.append("0");
			}
			hexBuilder.append(strHex);
		}
		return hexBuilder.toString().toUpperCase();
	}
	
	/**
	 * <p>Converts a <code>String</code> to the 192-bit primary key.</p>
	 * Converts the given key to lower case prior to conversion to binary.
	 * 
	 * @param keyString the key string to convert.
	 * 
	 * @return a 192-bit primary key.
	 */
	public static byte[] convertStringToPrimaryKey(String keyString) {
		
		if ((keyString != null) && (!"".equals(keyString))) {
			
			keyString = keyString.toLowerCase();
			byte[] bArray = new byte[(keyString.length() / 2)];
			
			for (int i = 0; i < bArray.length; i++) {
				bArray[i] = (byte)Integer.parseInt(keyString.substring(i*2, i*2 + 2), 16);
			}
			return bArray;
		}
		return null;
	}
}

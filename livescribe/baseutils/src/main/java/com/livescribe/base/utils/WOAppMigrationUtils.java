package com.livescribe.base.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import com.livescribe.base.constants.LSConstants;

public class WOAppMigrationUtils {
	
	private static String PREFIX = "{SHA}";
	
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
	
	/**
	 * <p>Generates a new <code>byte[]</code> for use as a primary key in FrontBase tables.</p>
	 * 
	 * @param seed A seed value to use in generating the <code>byte[]</code>.
	 * 
	 * @return a new <code>byte[]</code> for use as a primary key in FrontBase tables.
	 */
	public static byte[] generatePrimaryKey() {
		
		Random random = new Random();
		byte[] b = new byte[24];
		random.nextBytes(b);
		return b;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param text The text to encrypt.
	 * 
	 * @return
	 */
	public static String shaDigest(String text) {
		
		String encrypted = null;
		
		try {
			byte[] textBytes = text.getBytes(LSConstants.ENCODING_UTF8 /* encoding */);
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(textBytes);
			byte[] digest = md.digest();
			encrypted = Base64.encodeToString(digest, false);
		} 
		catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} 
		catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		} 
		
		return PREFIX + encrypted.trim();
	}	
	
	public static String uuidFromPrimaryKey(String primaryKey) {
		return uuidFromPrimaryKey(convertStringToPrimaryKey(primaryKey));
	}
	
	public static String uuidFromPrimaryKey(byte[] primaryKey) {
		return UUID.nameUUIDFromBytes(primaryKey).toString();
	}
}

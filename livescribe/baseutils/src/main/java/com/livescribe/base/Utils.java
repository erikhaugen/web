package com.livescribe.base;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import com.livescribe.base.constants.LSConstants;
import com.livescribe.base.utils.Base64;

public class Utils {
	public static final String LOGGER_LAYOUT = "%d [%t] %-5p %c - %m%n";

	private static final String MASK_STR = "X";

	/**
	 * <p>Converts the 192-bit primary key to a <code>String</code>.</p>
	 * The resulting string is returned as upper case.
	 * 
	 * @param byteKey The 192-bit primary key as a <code>byte[]</code>.
	 * 
	 * @return a <code>String</code> representation of the given primary key.
	 */
	public static String convertPrimaryKeyToString(byte[] primaryKey) {
		 
		if (primaryKey != null) {
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
		else {
			return "";
		}
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
	 * <p></p>
	 * 
	 * @param text The text to encrypt.
	 * 
	 * @return
	 */
	@Deprecated
	public static String encryptUsingSHA(String text) {
		
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
		
		return encrypted.trim();
	}
	
	/**
	 * This method takes a string as input and masks its first maskLength characters
	 * @param src
	 * @param unmaskedLength
	 * @return
	 */
	public static String getMaskedString(String src, int maskLength) {
		StringBuilder returnStr = null;
		if ( maskLength <= 0 ) {
			// Number of characters to mask is less than equal to 0
			// donot mask anything and return the string as it is
			returnStr = new StringBuilder(src);
		} else {
			returnStr = new StringBuilder();
			// Create mask string of length which is minimum of the maskLength/length of String
			returnStr.append(StringUtils.repeat(MASK_STR, Math.min(maskLength, src.length())));
			// Append the unmasked characters
			if ( maskLength < src.length() ) {
				returnStr.append(src.substring(maskLength));
			}
		}
		return returnStr.toString();
	}
	
	public static String printBigDecimal(BigDecimal decimal) {
		return String.format("%.2f", decimal);
	}
	
	public static <K,V> String printMap(Map<K, V> map, String tokenSep, String keyValueSep) {
		StringBuilder sb = new StringBuilder();
		SortedSet<K> sortedset= new TreeSet<K>(map.keySet());
		for ( K entry : sortedset ) {
			sb.append(entry.toString());
			sb.append(keyValueSep);
			sb.append(map.get(entry).toString());
			sb.append(tokenSep);
		}
		if ( sb.length() >= tokenSep.length() ) {
			int lastIndex = sb.lastIndexOf(tokenSep);
			sb.delete(lastIndex, lastIndex+tokenSep.length());
		}
		return sb.toString();
	}
	
	public static Map<String, String> parseStringForMap(String parseStr, String tokenSep, String keyValueSep) {
		Map<String, String> map = new HashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(parseStr, tokenSep);
		while ( tokenizer.hasMoreTokens() ) {
			String token = tokenizer.nextToken();
			if ( token.contains(keyValueSep) ) {
				int index = token.indexOf(keyValueSep);
				String key = token.substring(0, index);
				String value = token.substring(index+keyValueSep.length());
				map.put(key, value);
			}
		}
		return map;
	} 
	
	public static Map<String,Object> createMapFromString(String parseStr) {
		return createMapFromString(parseStr, null);
	}
	
	public static Map<String,Object> createMapFromString(String parseStr, DateFormat df) {
		Map<String, Object> map = new HashMap<String, Object>();
		if ( parseStr.startsWith("{") ) {
			parseStr = parseStr.substring(1);
		}
		if ( parseStr.endsWith("}") ) {
			parseStr = parseStr.substring(0, parseStr.length()-1);
		}
		StringTokenizer tokenizer = new StringTokenizer(parseStr, ",");
		while ( tokenizer.hasMoreTokens() ) {
			String token = tokenizer.nextToken();
			if ( token.contains("=") ) {
				int index = token.indexOf("=");
				String key = token.substring(0, index).trim();
				String value = token.substring(index+"=".length()).trim();
				Object objValue = value;
				if ( value.startsWith("'") && value.endsWith("'") ) {
					objValue = value.substring(1, value.length()-1);
					// try date if df != null
					if ( df != null ) {
						try {
							objValue = df.parse(value);
						} catch ( Exception ex ) {
							// will fall through to being a string
						}
					}
				} else if ( "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value) ) {
					objValue = Boolean.parseBoolean(value);
				} else {
					// try int
					try {
						objValue = Integer.parseInt(value);
					} catch ( Exception ex ) {	}
					// try double
					try {
						objValue = Double.parseDouble(value);
					} catch ( Exception ex ) {	}
				}
				map.put(key, objValue);
			}
		}
		return map;
	}
		
//	public static void main (String[] argv) {
//		System.out.println(printBigDecimal(new BigDecimal(2345.078)));
//	}
}

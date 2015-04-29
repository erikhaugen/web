package com.livescribe.base;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static boolean isBlank(String str) {
		return (str == null || str.trim().equals(""));
	}

	public static boolean isNotBlank(String str) {
		return (str != null && !str.trim().equals(""));
	}

	public static String trimIfNotBlank(String str) {
		String s = null;
		return ( (str==null) ? null : (s=str.trim()).equals("") ? null : s);
	}

	public static String escapeNewlines(String input) {
		return input.replaceAll("\n", "\\\\n");
	}

	public static String escapeBackslash(String input) {
		return input.replaceAll("\\", "\\\\");
	}

	public static <K> String print(K[] values, String sep) {
		StringBuilder sb = new StringBuilder();
		for (K value : values ) {
			sb.append(value.toString() + sep);
		}
		return stripFromEnd(sb.toString(), sep);
	}
	
	public static <K> String print(Collection<K> values, String sep) {
		StringBuilder sb = new StringBuilder();
		for (K value : values ) {
			sb.append(value.toString() + sep);
		}
		return stripFromEnd(sb.toString(), sep);
	}
	
	public static <K> String print(Iterator<K> values, String sep) {
		StringBuilder sb = new StringBuilder();
		while ( values.hasNext() ) {
			sb.append(values.next().toString() + sep);
		}
		return stripFromEnd(sb.toString(), sep);
	}
	
	public static <K> String print(Enumeration<K> values, String sep) {
		StringBuilder sb = new StringBuilder();
		while ( values.hasMoreElements() ) {
			sb.append(values.nextElement().toString() + sep);
		}
		return stripFromEnd(sb.toString(), sep);
	}


	public static String stripFromEnd(String value, String strip) {
		return value.endsWith(strip) ? 
				value.substring(0, value.length() - strip.length()) : value;
	}

	public static String getHtmlTableTag(String id) {
		return "<table id=\'" + id + "\' name=\'" + id + "\'>\n";
	}

	public static String getHtmlTableRow(String[] values, boolean isHeader) {
		return getHtmlTableRow (values, isHeader? "th" : "td");

	}

	public static String getHtmlTableRow (String[] values) {
		return getHtmlTableRow(values, "td");

	}

	public static String getHtmlTableRowHead(String[] values) {
		return getHtmlTableRow(values, "th");

	}

	public static String getHtmlTableRow(String[] values, String type) {
		StringBuilder ret = new StringBuilder("<tr>");
		for ( String value : values ) {
			ret.append("<" + type + ">" + value + "</" + type + ">");
		}
		ret.append("</tr>\n");
		
		return ret.toString();
	}
	
	public static final String repeat(String str, int length) {
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < length; i++ ) {
			sb.append(str);
		}
		return sb.toString();
	}
	
	public static final int getHashCode(String...strings) {
		String input = "";
		for ( String string : strings ) {
			input = (string != null ? input + string : input);
		}
		return input.hashCode();
	}
	
	/**
	 * Checks to make sure all characters in the string are numbers
	 * @param str
	 * @return
	 */
	private static final Pattern INTEGER_PATTERN = Pattern.compile("([0-9]*)");
	public static boolean isInteger(String str) {
		 Matcher matcher = INTEGER_PATTERN.matcher(str);
		 return matcher.matches();
	}

	public static int countOccurances(String src, String occurance) {
		StringTokenizer tokenizer = new StringTokenizer(src, occurance);
		return tokenizer.countTokens();
	}
	
	/**
	 * <p>Removes invalid, non-XML characters from the given <code>String</code>.</p>
	 * 
	 * @param in The <code>String</code> to strip non-XML characters from.
	 * 
	 * @return the given <code>String</code> stripped of non-XML characters.
	 */
	public static String removeInvalidXMLCharacters(String in) {
		
		StringBuilder out = new StringBuilder();
		
		if (in != null) {
			int codePoint;
			int i =- 0;
			while (i < in.length()) {
				
				//	Get the Unicode code of the character.
				codePoint = in.codePointAt(i);
				
				//	Check for valid XML characters.
				if ((codePoint == 0x9) ||
						(codePoint == 0xA) ||
						(codePoint == 0xD) ||
						((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
						((codePoint == 0xE000) && (codePoint <= 0xFFFD)) ||
						((codePoint == 0x10000) && (codePoint <= 0x10FFFF))) {
					out.append(Character.toChars(codePoint));
				}
				
				//	Increment the index with the number of code units(java chars) 
				//	needed to represent a Unicode char.
				i += Character.charCount(codePoint);
			}
		}
		
		return out.toString();
	}
	
	/**
	 * <p>Converts the 192-bit primary key to a <code>String</code>.</p>
	 * The resulting string is returned as upper case.
	 * 
	 * @param byteKey The 192-bit primary key as a <code>byte[]</code>.
	 * 
	 * @return a <code>String</code> representation of the given primary key.
	 * 
	 * Please use WOAppMigrationUtils instead
	 */
	@Deprecated
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
	 * 
	 * Please use WOAppMigrationUtils instead
	 */
	@Deprecated
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
	
	public static void main(String[] args) {
		
		String str = "12a";
		System.out.println(str + " is a integer = " + isInteger(str) );
		
//		String var1 = "abc\ndef";
//		String var2 = escapeNewlines(var1);
//		String var3 = "abc\\ndef";
//		System.out.println("Output = " + var2);
//
//		if(var2.equals(var3)) {
//			System.out.println("Output equals expectedOutput");
//		}
	}
	
}


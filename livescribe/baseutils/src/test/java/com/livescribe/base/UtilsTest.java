package com.livescribe.base;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class UtilsTest extends TestCase {
	@Override
    protected void setUp() {
    	
    }
	
	@Override
    protected void tearDown() {
		
	}
	
	public void testEncryptUsingSHA() {
		
		String TEST_TEXT = "letmein";
		
		String encrypted = Utils.encryptUsingSHA(TEST_TEXT);
		System.out.println(encrypted);
		assertNotNull("The resulting encryption was 'null'.", encrypted);
	}
	
	public void test_printMap() {
		Map<String, String> map = new HashMap<String, String>();
		String tokenSep = ";#";
		String keyValueSep = "=";

		String key1="key1";
		String value1="value1";
		
		String key2="key2";
		String value2="value2";

		String key3="key3";
		String value3="value3";

		String parseStr = key1 + keyValueSep + value1 + 
					tokenSep + key2 + keyValueSep + value2 + 
					tokenSep + key3 + keyValueSep + value3;
		
		map.put(key1, value1);
		map.put(key2, value2);
		map.put(key3, value3);

		String printedMapStr = Utils.printMap(map, tokenSep, keyValueSep);		
		assertEquals(parseStr, printedMapStr);
	}
	
	public void test_parseStringForMap() {
		String tokenSep = ";#";
		String keyValueSep = "=";

		String key1="key1";
		String value1="value1";
		
		String key2="key2";
		String value2="value2";

		String key3="key3";
		String value3="value3";

		String parseStr = key1 + keyValueSep + value1 + 
					tokenSep + key2 + keyValueSep + value2 + 
					tokenSep + key3 + keyValueSep + value3;
		
		Map<String, String> map = Utils.parseStringForMap(parseStr, tokenSep, keyValueSep);
		
		assertEquals(value1, map.get(key1));
		assertEquals(value2, map.get(key2));
		assertEquals(value3, map.get(key3));
	}
}

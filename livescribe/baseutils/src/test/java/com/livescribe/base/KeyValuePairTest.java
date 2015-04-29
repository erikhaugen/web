package com.livescribe.base;

import junit.framework.TestCase;

public class KeyValuePairTest extends TestCase {
	
	@Override
    protected void setUp() {
    	
    }
	
	@Override
    protected void tearDown() {
		
	}
	
	public void testgetKeyValuePair() {
		String key = "key";
		String value = "value";
		String sep = "#";
		
		KeyValuePair<String, String> kv = KeyValuePair.getKeyValuePair(key+sep+value, sep);
		assertEquals(key, kv.getKey());
		assertEquals(value, kv.getValue());
	}
}

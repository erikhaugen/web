package com.livescribe.base;

public class KeyValuePair <K, V> {

	K key;
	V value;
	
	public KeyValuePair (K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}
	
	public static KeyValuePair<String, String> getKeyValuePair(String str, String separator) {
		KeyValuePair<String, String> returnValue = null;
		
		int separatorLength = separator.length();
		int strLength = str.length();
		
		if ( str != null && separator != null && strLength > 0 && separatorLength > 0 && strLength > separatorLength ) {
			int keyEndIndex = str.indexOf(separator);
			int valStartIndex = keyEndIndex + separatorLength;
			returnValue = new KeyValuePair<String, String>(str.substring(0, keyEndIndex), str.substring(valStartIndex));
		}
		return returnValue;
	}
}

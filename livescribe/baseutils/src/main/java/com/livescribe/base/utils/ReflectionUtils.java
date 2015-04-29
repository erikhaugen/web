package com.livescribe.base.utils;

import java.lang.reflect.Constructor;

public class ReflectionUtils {

	public static Object createAndSetValue(String className, String value) 
			throws IllegalArgumentException {
		try {
			Object obj = null;

			@SuppressWarnings("unchecked")
			Class clazz = Class.forName(className);

			@SuppressWarnings("unchecked")
			Constructor constructor  = clazz.getConstructor(String.class);

			if ( constructor != null ) {
				obj = constructor.newInstance(value);
			}
			return obj;
		} catch ( Exception ex ) {
			throw new IllegalArgumentException("Unable to resolve the object " + ex.getMessage(), ex);
		}
	}
	
	public static String getStringRepresentation(Object obj) {
		String string = null;
		
		@SuppressWarnings("unchecked")
		Class clazz = obj.getClass();
		if ( String.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz) 
				|| Integer.class.isAssignableFrom(clazz) || Float.class.isAssignableFrom(clazz)
				|| Double.class.isAssignableFrom(clazz) || Byte.class.isAssignableFrom(clazz)
				|| Character.class.isAssignableFrom(clazz) ) {
			string = obj.toString();
		}
		return string;
	}
	
//	/**
//	 * This method takes the output of the toString method of a Map and reconstructs
//	 * the map. The supported value classes are that can be constructed from 
//	 *  
//	 * @param string
//	 * @returnO
//	 */
//	public static Map<String, String> reconstructMap(String string) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		
//		return map;
//	}
}

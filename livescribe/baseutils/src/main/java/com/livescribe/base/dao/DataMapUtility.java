package com.livescribe.base.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This class will examine a Data class marked with the annotation
 * @Mappable and look for fields declared with annotation @MappedElement
 * and create a Map or covert a Map to the class
 * 
 * @author smukker
 *
 */
@Deprecated
public class DataMapUtility {
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> convertToMap(Object mappable) 
					throws IllegalAccessException, 
							NoSuchMethodException, 
							InvocationTargetException {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		Class clazz = mappable.getClass();
		
		while ( clazz != null ) {
			if ( clazz.getAnnotation(Mappable.class) != null ) {
				Field[] fields = clazz.getDeclaredFields();
				for ( Field field : fields ) {
					MappedElement annot = field.getAnnotation(MappedElement.class);
					if ( annot != null ) {
						Method method = clazz.getMethod("get"+getFieldNameForAccess(field.getName()));
						Object value = method.invoke(mappable);
						map.put(annot.value(), value);
					}
				}
			} 
			clazz = clazz.getSuperclass();
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static Object createObjectFromMap(Map<String, Object> map, Class clazz) 
							throws InstantiationException, 
									IllegalAccessException,
									NoSuchMethodException, 
									InvocationTargetException {
		Object obj = clazz.newInstance();
		
		Class localClazz = clazz;
		
		while ( localClazz != null ) {
			if ( localClazz.getAnnotation(Mappable.class) != null ) {
				Field[] fields = localClazz.getDeclaredFields();
				for ( Field field : fields ) {
					MappedElement annot = field.getAnnotation(MappedElement.class);
					if ( annot != null ) {
						String key = annot.value();
						Object value = map.get(key);
						if ( value != null ) {
							Method method = clazz.getMethod("set"+getFieldNameForAccess(field.getName()), field.getType());
							method.invoke(obj, value);
						}
					}
				}
			}
			localClazz = localClazz.getSuperclass();
		}
		return obj;
	}
	
	public static String getFieldNameForAccess(String fieldName) {
		StringBuilder sb = new StringBuilder(fieldName);
		char char1 = fieldName.charAt(0);
		if ( char1 >= 97 ) {
			sb.setCharAt(0, (char)(char1-32));
		}
		return sb.toString();
	}
}

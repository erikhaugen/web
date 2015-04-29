package com.livescribe.base.anno;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class will examine a Data class marked with the annotation
 * @Mappable and look for fields declared with annotation @MappedElement
 * and create a Map or covert a Map to the class
 * 
 * It will also examine the super class and if the direct superclass is 
 * also annotated with @Mappable then it adds the fields from the super
 * class into it. If the field is overwritten in the subclass, the value
 * from super class is ignored.
 * 
 * @author smukker
 *
 */
public class DataMapUtility {
	
	private static Logger logger = Logger.getLogger(DataMapUtility.class.getName());
	
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
						String fieldName = annot.value();
						logger.debug("Annotation 'value': " + fieldName);
						if (fieldName.equals("")) {
							fieldName = field.getName();
						}
						Method method = clazz.getMethod("get"+getFieldNameForAccess(fieldName));
						Object value = method.invoke(mappable);
						//Need to test to see if the value is @Mappable annotated
						if ((null != value) && (value.getClass().getAnnotation(Mappable.class)) != null){
							value = convertToMap(value);
						}
						//Need to test if value is null and convert to "<null>" string
						if (null == value) {
							value = new String("<null>");
						}
						String key = annot.key();
						if ( map.get(key) == null ) {
							map.put(key, value);
						}
					}
				}
			} 
			clazz = clazz.getSuperclass();
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static void readObjectFromMap(Map<String, Object> map, Object mappable) 
							throws InstantiationException, 
									IllegalAccessException,
									NoSuchMethodException, 
									InvocationTargetException {
		Class clazz = mappable.getClass();
		Class localClazz = clazz;
		
		while ( localClazz != null ) {
			if ( localClazz.getAnnotation(Mappable.class) != null ) {
				Field[] fields = localClazz.getDeclaredFields();
				for ( Field field : fields ) {
					MappedElement annot = field.getAnnotation(MappedElement.class);
					if ( annot != null ) {
						String key = annot.key();
						Object value;
						if (field.getType().getAnnotation(Mappable.class) != null){
							Constructor classConstructor = field.getType().getConstructor();
							value = classConstructor.newInstance();
							readObjectFromMap((Map<String, Object>)map.get(key), value);
						}else{
							value = map.get(key);
						}
						if ( value != null ) {
							if ((value instanceof String) && (value.equals("<null>"))){
								value = null;
							}
							String fieldName = annot.value();
							if (fieldName.equals("")) {
								fieldName = field.getName();
							}
							Method method = null;
								try {
									method = clazz.getMethod("set" + getFieldNameForAccess(fieldName), field.getType());
								} catch (NoSuchMethodException e) {
									method = clazz.getMethod("set" + getFieldNameForAccess(fieldName), value.getClass());
								}
							method.invoke(mappable, value);
						}
					}
				}
			}
			localClazz = localClazz.getSuperclass();
		}	
	}
	
	@SuppressWarnings("unchecked")
	public static Object createObjectFromMap(Map<String, Object> map, Class clazz) 
							throws InstantiationException, 
									IllegalAccessException,
									NoSuchMethodException, 
									InvocationTargetException {
		Object obj = clazz.newInstance();
		readObjectFromMap(map, obj);
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

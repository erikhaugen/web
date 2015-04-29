package com.livescribe.base;

@SuppressWarnings("serial")
public class UnsupportedMethodException extends RuntimeException {
	
	/**
	 * Standard exception message
	 */
	protected static final String UNSUPPORTED_METHOD_STRING = " does not support Method : ";
	
	/**
	 * Use to pass custom Exception Message
	 * @param message - Custom Exception Message
	 */
	public UnsupportedMethodException (String message) {
		super(message);
	}
	
	/**
	 * Use for standard error message "Class <classname> does not support Method <methodname> 
	 * 
	 * @param classname Class name
	 * @param methodname Method name that is not supported 
	 */
	public UnsupportedMethodException (String classname, String methodname) {
		super(classname + UNSUPPORTED_METHOD_STRING + methodname);
	}
	
}

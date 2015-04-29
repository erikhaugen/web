package com.livescribe.base.constants;

public class OpStatus<T> {

	public enum Result {
		OK,
		FAIL
	}
	
	private Result result;
	private String message;
	private T object;
	private String errorCode;
	
	public OpStatus() {
		this(Result.OK, null, null, null);
	}
	
	public OpStatus(T obj) {
		this(Result.OK, null, null, obj);
	}
	
	public OpStatus(Result result, String errorCode) {
		this(result, errorCode, null);
	}
	
	public OpStatus(Result result, T obj) {
		this(result, null, null, obj);
	}
	
	public OpStatus(Result result, String errorCode, String message) {
		this(result, errorCode, message, null);
	}
	
	public OpStatus(Result result, String errorCode, String message, T obj) {
		this.result = result;
		this.message = message;
		this.object = obj;
		this.errorCode = errorCode;
	}

	public Result getResult() {
		return result;
	}
	
	public String getResultAsString() {
		return result.toString();
	}

	public String getMessage() {
		return message;
	}

	public T getObject() {
		return object;
	}

	public String getErrorCode() {
		return errorCode;
	}
	
	public static OpStatus<Object> parseString(String str) {
		OpStatus<Object> retVal = null;
		int startIdx = "OpStatus{".length();
		int endIdx = str.length();
		try {
			String str1 = str.substring(startIdx, endIdx);
			if ( str1 != null ) {
				String[] tokens = str1.split(",");
				if ( tokens != null && tokens.length == 3 ) {
					Result res = Result.valueOf(tokens[0]);
					if ( res != null ) {
						String eCode = ( "null".equals(tokens[1]) ? null : tokens[1]);
						String message = ( "null".equals(tokens[2]) ? null : tokens[2]);
						retVal = new OpStatus<Object>(res, eCode, message);
					}
				}
			}
		} catch ( IndexOutOfBoundsException iobe ) {

		}
		return retVal;
	}
	
	private static final String TOSTR_FRMT = "OpStatus{%s,%s,%s}";
	
	public String toString() {
		return String.format(TOSTR_FRMT, result, errorCode, message);
	}
}

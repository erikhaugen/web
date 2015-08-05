package com.livescribe.web.registration.validation;

import org.springframework.validation.ObjectError;

import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.web.registration.util.PenId;

public class ValidationUtil {

	public static final String ERROR_CODE_INVALID_FORMAT = "invalidFormat";
	
	public static final String ERROR_CODE_TYPE_MISMATCH = "typeMismatch";
	
	public static final String ERROR_CODE_REQUIRED = "required";
	
	public static final String EMAIL_PATTERN = "\\b[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,4}\\b";
	
	private static final String PEN_DISPLAY_ID_PATTERN = "AYE-\\w{3}-\\w{3}-\\w{2}";
	private static final String PEN_SERIAL_NUMBER_PATTERN = "\\d{13}";
	
	/**
	 * Check if the penId (pen serial number or displayID) is a valid ID
	 * 
	 * @param penId
	 * @return
	 */
	public static boolean isValidPenID(String penId) {
		try {
			PenId.getPenIdObject(penId);
			
		} catch (InvalidParameterException e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Check if the pen serial number is in valid format
	 * 
	 * @param penSerial
	 * @return
	 */
	public static boolean isValidPenSerialFormat(String penSerial) {
		if (penSerial == null || penSerial.isEmpty()) {
			return false;
		}
		
		return penSerial.matches(ValidationUtil.PEN_SERIAL_NUMBER_PATTERN);
	}
	
	/**
	 * Check if the pen display id is in valid format
	 * 
	 * @param penDisplayId
	 * @return
	 */
	public static boolean isValidPenDisplayIdFormat(String penDisplayId) {
		if (penDisplayId == null || penDisplayId.isEmpty()) {
			return false;
		}
		
		return penDisplayId.matches(ValidationUtil.PEN_DISPLAY_ID_PATTERN);
	}
	
	/**
	 * Check if an email address is in valid format
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isValidEmailFormat(String email) {
		if (email == null || email.isEmpty()) {
			return false;
		}
		
		return email.matches(ValidationUtil.EMAIL_PATTERN);
	}
	
	/**
	 * Parse ObjectError and return appropriate error code
	 * 
	 * @param error
	 */
	public static ErrorResponse getErrorResponse(ObjectError error) {
		String errorCode = error.getCode();
		ErrorResponse errorResponse = null;
		
		if (ERROR_CODE_INVALID_FORMAT.equalsIgnoreCase(errorCode)) {
			errorResponse = new ErrorResponse(ResponseCode.INVALID_PARAMETER, error.getDefaultMessage());
			
		} else if (ERROR_CODE_REQUIRED.equalsIgnoreCase(errorCode)) {
			errorResponse = new ErrorResponse(ResponseCode.MISSING_PARAMETER, error.getDefaultMessage());
			
		} else if (ERROR_CODE_TYPE_MISMATCH.equalsIgnoreCase(errorCode)) {
			errorResponse = new ErrorResponse(ResponseCode.INVALID_PARAMETER, error.getDefaultMessage());
			
		} else {
			errorResponse = new ErrorResponse(ResponseCode.INVALID_PARAMETER, error.getDefaultMessage());
		}
		
		return errorResponse;
	}
}

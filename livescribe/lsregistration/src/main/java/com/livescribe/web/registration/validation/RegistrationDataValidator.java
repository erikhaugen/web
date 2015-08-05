/**
 * Created:  Aug 15, 2013 5:19:46 PM
 */
package com.livescribe.web.registration.validation;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.livescribe.web.registration.controller.RegistrationData;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegistrationDataValidator implements Validator {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public RegistrationDataValidator() {
		logger.debug("Instantiated.");
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		
		String method = "validate()";
		
		logger.debug(method + " - Class name:  " + clazz.getName());
		return RegistrationData.class.isAssignableFrom(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		
		String method = "validate()";
		
		logger.debug(method + " - Validating parameters ...");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "appId", "required", "The 'appId' field is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "penSerial", "required", "The 'penSerial' field is required.");
		validatePenIdFormat(target, errors);
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "required", "The 'email' field is required.");
		validateEmailFormat(target, errors);
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "locale", "required", "The 'locale' field is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "required", "The 'country' field is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "optIn", "required", "The 'optIn' field is required.");
	}
	
	protected void validateEmailFormat(Object target, Errors errors) {
		RegistrationData regData = (RegistrationData) target;
		String email = regData.getEmail();
		
		if (!ValidationUtil.isValidEmailFormat(email)) {
			errors.rejectValue("email", ValidationUtil.ERROR_CODE_INVALID_FORMAT, "The 'email' field is not a valid email address.");
		}
	}

	protected void validatePenIdFormat(Object target, Errors errors) {
		RegistrationData regData = (RegistrationData) target;
		String penId = regData.getPenSerial();
		
		if (!ValidationUtil.isValidPenID(penId)) {
			errors.rejectValue("penSerial", ValidationUtil.ERROR_CODE_INVALID_FORMAT, "The 'penID' field is not a valid pen serial or pen display ID.");
		}
	}
}

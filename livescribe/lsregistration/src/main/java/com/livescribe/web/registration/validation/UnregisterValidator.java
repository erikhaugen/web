package com.livescribe.web.registration.validation;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

public class UnregisterValidator extends RegistrationDataValidator {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public UnregisterValidator() {
		logger.debug("Instantiated.");
	}

	@Override
	public void validate(Object target, Errors errors) {
		String method = "validate()";
		
		logger.debug(method + " - Validating parameters ...");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "appId", "required", "The 'appId' field is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "penSerial", "required", "The 'penSerial' field is required.");
		validatePenIdFormat(target, errors);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "required", "The 'email' field is required.");
		validateEmailFormat(target, errors);
	}

}

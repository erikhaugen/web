package com.livescribe.aws.tokensvc.validation;

import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.aws.tokensvc.BaseTest;

public class EmailValidatorTest extends BaseTest {
	private static final String VALID_EMAIL1 = "kle@livescribe.com";
	private static final String VALID_EMAIL2 = "kle@kms-technology.com";
	private static final String INVALID_EMAIL = "test@in%valid.com";
	
	@Autowired
	private EmailValidator emailValidator;
	
	public EmailValidatorTest() {
		super();
	}
	
	@Test
	public void testValidateEmail() {
		assertTrue(emailValidator.validate(VALID_EMAIL1));
		assertTrue(emailValidator.validate(VALID_EMAIL2));
		assertTrue(!emailValidator.validate(INVALID_EMAIL));
	}
}

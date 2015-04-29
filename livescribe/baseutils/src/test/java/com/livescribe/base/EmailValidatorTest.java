/**
 * Created:  Dec 7, 2010 11:51:21 AM
 */
package com.livescribe.base;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EmailValidatorTest extends TestCase {

	private ArrayList<String> validEmail = new ArrayList<String>();
	private ArrayList<String> invalidEmail = new ArrayList<String>();
	
	/**
	 * <p></p>
	 * 
	 */
	public EmailValidatorTest() {
		
		validEmail.add("mkyong@yahoo.com");
		validEmail.add("mkyong-100@yahoo.com");
		validEmail.add("mkyong.100@yahoo.com");
		validEmail.add("mkyong111@mkyong.com");
		validEmail.add("mkyong-100@mkyong.net");
		validEmail.add("mkyong.100@mkyong.com.au");
		validEmail.add("mkyong@1.com");
		validEmail.add("mkyong@gmail.com.com");
		validEmail.add("test@mky-ong.com");
		
		invalidEmail.add("mkyong");
		invalidEmail.add("mkyong@.com.my");
		invalidEmail.add("mkyong123@gmail.a");
		invalidEmail.add("mkyong123@.com");
		invalidEmail.add("mkyong123@.com.com");
		invalidEmail.add(".mkyong@mkyong.com");
//		invalidEmail.add("mkyong()*@gmail.com");
//		invalidEmail.add("mkyong@%*.com");
//		invalidEmail.add("mkyong..2002@gmail.com");
//		invalidEmail.add("mkyong.@gmail.com");
//		invalidEmail.add("mkyong@mkyong@gmail.com");
//		invalidEmail.add("mkyong@gmail.com.1a");
	}

	@Before
    public void setUp(){}

	@Test
	public void testValidate_ValidEmail() {
		
		for (String str : validEmail) {
			boolean valid = EmailValidator.validate(str);
			assertTrue("The email address '" + str + "' was INVALID!", valid);
		}
	}
	
	@Test
	public void testValidate_InvalidEmail() {
		
		for (String str : invalidEmail) {
			boolean valid = EmailValidator.validate(str);
			assertFalse("The email address '" + str + "' was VALID!", valid);
		}
	}
}

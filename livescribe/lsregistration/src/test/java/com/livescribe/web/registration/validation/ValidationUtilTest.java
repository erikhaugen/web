/**
 * Created:  Nov 4, 2013 12:59:17 PM
 */
package com.livescribe.web.registration.validation;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.livescribe.web.registration.BaseTest;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ValidationUtilTest extends BaseTest {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private String validDisplayId = "AYE-APS-DHH-HS";
	private String inValidDisplayId = "AYE-APS-DHH-SH";
	private String validSerialNumber = "2594170374701";
	private String inValidSerialNumber = "2494170374710";
	private String validEmail = "info@livescribe.com";
	private String inValidEmail = "info@livescribe@com";
	
	/**
	 * <p></p>
	 * 
	 */
	public ValidationUtilTest() {
		super();
	}

	@Test
	public void testIsValidPenId_SuccessWithDisplayID() {
		
		boolean isValid = ValidationUtil.isValidPenID(validDisplayId);
		Assert.assertTrue(isValid);
	}

	@Test
	public void testIsValidPenId_SuccessWithSerialNumber() {
		
		boolean isValid = ValidationUtil.isValidPenID(validSerialNumber);
		Assert.assertTrue(isValid);
	}

	@Test
	public void testIsValidPenId_FailWithDisplayID() {
		
		boolean isValid = ValidationUtil.isValidPenID(inValidDisplayId);
		Assert.assertFalse(isValid);
	}

	@Test
	public void testIsValidPenId_FailWithSerialNumber() {
		
		boolean isValid = ValidationUtil.isValidPenID(inValidSerialNumber);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void testIsValidEmailFormat_Success() {
		
	}

	@Test
	public void testIsValidEmailFormat_Fail() {
		
	}
}

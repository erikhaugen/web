/**
 * Created:  Dec 7, 2010 11:41:55 AM
 */
package com.livescribe.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

/**
 * <p>Validates that email addresses conform to the syntax defined by RFC2822.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EmailValidator {

	private static Logger logger = Logger.getLogger(EmailValidator.class.getName());
	private static Pattern pattern;
	private static Matcher matcher;

//	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-+]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	 
    /**
	 * <p></p>
	 * 
	 */
	public EmailValidator() {
		pattern = Pattern.compile(EMAIL_PATTERN);
	}

//	/**
//	 * <p></p>
//	 * Uses a regular expression to perform the matching.
//	 * Taken from blog at mkyong.com (http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/)
//	 * 
//	 * @param str
//	 * 
//	 * @return
//	 */
//	public static boolean validate(final String str) {
//		
//		matcher = pattern.matcher(str);
//		return matcher.matches();
//	}
	
	/**
	 * <p></p>
	 * 
	 * @param str
	 * 
	 * @return
	 */
	public static boolean validate(String str) {
		
		if ((str == null) || ("".equals(str))) {
			return false;
		}
		
		int atIdx = str.indexOf("@");
		if (atIdx < 0) {
			return false;
		}
		
		//	A '.' cannot be the first character of an email address.
		int dotIdx = str.indexOf(".");
		if (dotIdx == 0) {
			return false;
		}
		
		//	The first character after the '@' cannot be a '.'.
		dotIdx = str.indexOf(".", atIdx);
		System.out.println(atIdx + "  -  " + dotIdx + "  -  " + str.length());
		if ((dotIdx - atIdx) < 2) {
			return false;
		}
		
		//	Top-level domain must be at least 2 characters.
		if ((str.length() - dotIdx) < 3) {
			return false;
		}
		try {
			InternetAddress addr = new InternetAddress(str);
		}
		catch (AddressException ae) {
			logger.info("The email address '" + str + "' was invalid.");
			ae.printStackTrace();
			return false;
		}
		
		return true;
	}
}

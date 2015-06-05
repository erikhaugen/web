/**
 * 
 */
package com.livescribe.aws.tokensvc.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EmailValidator {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private Pattern pattern;
	private Matcher matcher;
	
	/**
	 * <p></p>
	 *
	 */
	public EmailValidator() {
		pattern = Pattern.compile(EMAIL_PATTERN);
	}

	/**
	 * <p></p>
	 * 
	 * @param text
	 * @return
	 */
	public boolean validate(final String text) {
		
		matcher = pattern.matcher(text);
		return matcher.matches();
	}
}

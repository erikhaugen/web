/**
 * 
 */
package com.livescribe.framework.web.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EmailValidator {

	/* Email pattern copied from LSUSer woapp framework */
	private static final String sp = "!#$%&'*+\\-/=?^_`{|}~";
	private static final String atext = "[a-zA-Z0-9" + sp + "]";
	private static final String atom = atext + "+"; // one or more atext chars
	private static final String dotAtom = "\\." + atom;
	private static final String localPart = atom + "(" + dotAtom + ")*"; // one atom followed by 0 or more dotAtoms.
																		 // RFC 1035 tokens for domain names:
	private static final String letter = "[a-zA-Z]";
	private static final String letDig = "[a-zA-Z0-9]";
	private static final String letDigHyp = "[a-zA-Z0-9\\-]";
	private static final String rfcLabel = letDig + "(" + letDigHyp + "{0,61}" + letDig + "){0,1}";
	private static final String domain = rfcLabel + "((\\." + rfcLabel + ")*\\." + letter + "{2,6}){0,1}";
	private static final String EMAIL_PATTERN = "^" + localPart + "@" + domain + "$";
	
//	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
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
		String lower = text.toLowerCase();
		matcher = pattern.matcher(text);
		
		// this code is copied from LSUSer woapp
		if (matcher.find()) {
			// bug if you enter an email address just as : foobar@domain the regex will accept it, but it's not a valid one
			int atIndex = lower.indexOf("@");
			int dotIndex = lower.indexOf(".", atIndex+1);
			return dotIndex > 0;
		}
		
		return false;
	}
}

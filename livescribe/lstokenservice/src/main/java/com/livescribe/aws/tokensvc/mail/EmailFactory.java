/*
 * Created:  Nov 28, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.mail;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EmailFactory {

	/**
	 * <p></p>
	 * 
	 * @param signUpUrl
	 * @param email
	 * @param serialNumber
	 * @param lang
	 * 
	 * @return
	 */
	public static String createConfirmAccountEmail(String signUpUrl, String email, String serialNumber, String lang) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("Hi,<br /><br />");
		 
		builder.append("You are one step away from registering a new Echo Wifi Smartpen!<br /><br />");
		 
		builder.append("Please click this link to complete your registration.<br />");
		builder.append(signUpUrl + "<br /><br />");
		 
		builder.append("Your Smartpen is pending registration to email account: " + email + "<br />");
		builder.append("Your Smartpen serial number : " + serialNumber + "<br />");
		builder.append("Your language: " + lang + "<br /><br />");
		 
		builder.append("Best,<br />");
		builder.append("Livescribe Pen Registration Team<br />");

		return builder.toString();
	}
	
	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public static String createSuccessEmail() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("Congratulations, your Wifi Echo pen has been registered!<br /><br />");
		 
		builder.append("What&apos;s next:<br />");
		builder.append("  1.  Configure your pen settings<br />");
		builder.append("  2.  Tutorial<br />");
		builder.append("  3.  Lorem Ipsem<br /><br />");

		return builder.toString();
	}
}

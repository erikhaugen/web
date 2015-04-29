/*
 * Created:  Nov 28, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.login.mail;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EmailFactory {

	public static String createForgotPasswordMail() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Hi,<br /><br />");
		
		builder.append("Please follow the instruction below to reset your password:<br /><br />");
		
		builder.append("Forgot password instruction goes here...<br /><br />");
		
		builder.append("Best,<br />");
		builder.append("Livescribe Pen Registration Team<br />");
		
		return builder.toString();
	}

}

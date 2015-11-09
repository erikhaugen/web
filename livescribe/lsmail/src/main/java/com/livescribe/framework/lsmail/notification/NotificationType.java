/**
 * Created:  Nov 26, 2013 2:04:32 PM
 */
package com.livescribe.framework.lsmail.notification;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public enum NotificationType {

	AUTH_EXPIRED		("User's Evernote authorization has expired."),
	NOTE_SIZE_EXCEEDED	("Evernote Note size has been exceeded.");
	
	private final String message;
	
	NotificationType(String message) {
		this.message = message;
	}

	/**
	 * <p></p>
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}

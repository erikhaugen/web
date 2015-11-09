/**
 * Created:  Nov 26, 2013 2:03:50 PM
 */
package com.livescribe.framework.lsmail.notification;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public abstract class AbstractNotification {

	private NotificationType type;
	
	/**
	 * <p></p>
	 * 
	 */
	public AbstractNotification() {
	}

	/**
	 * <p></p>
	 * 
	 * @return the type
	 */
	public NotificationType getType() {
		return type;
	}

	/**
	 * <p></p>
	 * 
	 * @param type the type to set
	 */
	public void setType(NotificationType type) {
		this.type = type;
	}

	
}

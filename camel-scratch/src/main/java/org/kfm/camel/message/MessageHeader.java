/**
 * Created:  Dec 19, 2014 12:00:37 PM
 */
package org.kfm.camel.message;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public enum MessageHeader {

	ACCESS_TOKEN ("accessToken"),
	CAMEL_FILE_NAME ("CamelFileName"),
	EN_USER_ID ("enUserId"),
	IS_NEW_DOCUMENT ("isNewDocument"),
	PEN_DISPLAY_ID ("penDisplayId"),
	PEN_NAME ("penName"),
	PEN_SERIAL ("penSerial"),
	STACK_NAME ("stackName"),
	UID ("uid");
	
	String header;
	
	MessageHeader(String header) {
		this.header = header;
	}
	
	public String getHeader() {
		return header;
	}
}

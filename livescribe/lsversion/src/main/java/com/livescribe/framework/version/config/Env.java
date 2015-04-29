/**
 * Created:  Apr 2, 2014 1:32:50 PM
 */
package com.livescribe.framework.version.config;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public enum Env {

	DEV		("DEV"),
	QA		("QA"),
	STAGE	("STAGE"),
	PROD	("PROD");
	
	private final String value;
	
	Env(String value) {
		this.value = value;
	}

	/**
	 * <p>Returns an <code>Env</code> matching the given <code>String</code>
	 * value.</p>
	 * 
	 * @param value A case-insensitive string to compare with.
	 * 
	 * @return an <code>Env</code> object.
	 */
	public static Env fromString(String value) {
		
		if (value != null) {
			for (Env env : Env.values()) {
				if (value.equals(env.toString())) {
					return env;
				}
			}
		}
		return null;
	}
	
	/**
	 * <p></p>
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}

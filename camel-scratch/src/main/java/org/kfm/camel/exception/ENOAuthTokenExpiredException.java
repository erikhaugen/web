/**
 * 
 */
package org.kfm.camel.exception;

/**
 * A Custom Exception indicating that the user's Evernote authentication token is expired.
 * @author Kiman
 *
 */
public class ENOAuthTokenExpiredException extends Exception {
	
	private String uid;
	private String enUserName;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 124275637421L;

	/**
	 * 
	 */
	public ENOAuthTokenExpiredException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ENOAuthTokenExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ENOAuthTokenExpiredException(String message) {
		super(message);
	}	

	/**
	 * @param cause
	 */
	public ENOAuthTokenExpiredException(Throwable cause) {
		super(cause);
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the enUserName
	 */
	public String getEnUserName() {
		return enUserName;
	}

	/**
	 * @param enUserName the enUserName to set
	 */
	public void setEnUserName(String enUserName) {
		this.enUserName = enUserName;
	}
	
	
}

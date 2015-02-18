/**
 * 
 */
package org.kfm.camel.exception;

/**
 * A Custom Exception indicating that the user's Evernote storage quota has been reached.
 * @author Mohammad M. Naqvi
 *
 */
public class ENStorageQuotaReachedException extends Exception {
	
	private String uid;

	/**
	 * 
	 */
	private static final long serialVersionUID = 124875436423L;

	/**
	 * 
	 */
	public ENStorageQuotaReachedException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ENStorageQuotaReachedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ENStorageQuotaReachedException(String message) {
		super(message);
	}

	/**
	 * @param message
	 */
	public ENStorageQuotaReachedException(String message, String user) {
		super(message);
		uid = user;
	}
	/**
	 * @param message
	 */
	public ENStorageQuotaReachedException(String message, String user, Throwable cause) {
		super(message, cause);
		uid = user;
	}

	/**
	 * @param cause
	 */
	public ENStorageQuotaReachedException(Throwable cause) {
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
	
	
}

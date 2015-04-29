/**
 * 
 */
package com.livescribe.framework.login.exception;

import java.util.Date;

/**
 * Exception for an Evernote Authorization is expired.
 * 
 * @author Mohammad M. Naqvi
 *
 */
public class AuthorizationExpiredException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7849171340443713465L;
	private Date expiredOn;
	private String enUserName;
	private Long enUserId;
	private Long lsUserId;

	/**
	 * <p></p>
	 *
	 */
	public AuthorizationExpiredException() {
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 */
	public AuthorizationExpiredException(String message) {
		super(message);
	}

	/**
	 * <p></p>
	 *
	 * @param cause
	 */
	public AuthorizationExpiredException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p></p>
	 *
	 * @param message
	 * @param cause
	 */
	public AuthorizationExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @return the expiredOn
	 */
	public Date getExpiredOn() {
		return expiredOn;
	}

	/**
	 * @param expiredOn the expiredOn to set
	 */
	public void setExpiredOn(Date expiredOn) {
		this.expiredOn = expiredOn;
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

	/**
	 * @return the enUserId
	 */
	public Long getEnUserId() {
		return enUserId;
	}

	/**
	 * @param enUserId the enUserId to set
	 */
	public void setEnUserId(Long enUserId) {
		this.enUserId = enUserId;
	}

	/**
	 * @return the lsUserId
	 */
	public Long getLsUserId() {
		return lsUserId;
	}

	/**
	 * @param lsUserId the lsUserId to set
	 */
	public void setLsUserId(Long lsUserId) {
		this.lsUserId = lsUserId;
	}


}

/**
 * 
 */
package com.livescribe.aws.login.dto;

import com.livescribe.framework.orm.consumer.User;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("user")
public class UserDTO {

	@XStreamAlias("userId")
	private Long userId;
	
	@XStreamAlias("uid")
	private String uid;
	
	@XStreamAlias("primaryEmail")
	private String primaryEmail;
	
	@XStreamAlias("password")
	private String password;
	
	@XStreamAlias("confirmed")
	private Boolean confirmed;
	
	@XStreamAlias("enabled")
	private Boolean enabled;
	
	/**
	 * <p>Default class constructor.</p>
	 *
	 */
	public UserDTO() {}
	
	/**
	 * <p>Constructor that takes a <code>User</code> object as a parameter.</p>
	 *
	 * @param user The <code>User</code> object to use.
	 */
	public UserDTO(User user) {
		
		this.userId = user.getUserId();
		this.primaryEmail = user.getPrimaryEmail();
		this.password = user.getPassword();
		this.uid = user.getUid();
		this.confirmed = user.isConfirmed();
		this.enabled = user.getEnabled();
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @return the primaryEmail
	 */
	public String getPrimaryEmail() {
		return primaryEmail;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the confirmed
	 */
	public Boolean getConfirmed() {
		return confirmed;
	}

	/**
	 * @return the enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @param primaryEmail the primaryEmail to set
	 */
	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param confirmed the confirmed to set
	 */
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}

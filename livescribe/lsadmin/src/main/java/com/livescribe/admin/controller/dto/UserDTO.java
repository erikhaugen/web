/**
 * 
 */
package com.livescribe.admin.controller.dto;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.User;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UserDTO extends User {

//	private Long userId;
//	private String email;
//	private Integer enUserId;
//	private String enUsername;
//	private Date enTokenExpiration;
//	private Date lastLogin;
//	private Date created;
//	private Date lastModified;
	private boolean validAuthorization;
	
	/**
	 * <p></p>
	 *
	 */
	public UserDTO() {
		
	}

	/**
	 * <p></p>
	 *
	 * @param user
	 */
	public UserDTO(User user) {
		
		setUserId(user.getUserId());
		setPrimaryEmail(user.getPrimaryEmail());
		setAuthenticateds(user.getAuthenticateds());
		setAuthorizations(user.getAuthorizations());
		setConfirmationDate(user.getConfirmationDate());
		setConfirmed(user.isConfirmed());
		setCreated(user.getCreated());
		setEnabled(user.getEnabled());
		setLastModified(user.getLastModified());
		setLastModifiedBy(user.getLastModifiedBy());
		setPassword(user.getPassword());
		setPremiumCodes(user.getPremiumCodes());
		setRegisteredDevices(user.getRegisteredDevices());
		setSendDiagnostics(user.getSendDiagnostics());
		setUid(user.getUid());
		setUserSettings(user.getUserSettings());
		setXUserGroups(user.getXUserGroups());
		setXUserRoles(user.getXUserRoles());
	}
	
//	public boolean isAuthorizationValid() {
//		
//		Set<Authorization> authList = getAuthorizations();
//		if ((authList == null) || (authList.isEmpty())) {
//			return false;
//		}
//		
//		Iterator<Authorization> authIter = authList.iterator();
//		while (authIter.hasNext()) {
//			Authorization auth = authIter.next();
//			auth.getExpiration();
//		}
//	}

	/**
	 * @return the validAuthorization
	 */
	public boolean isValidAuthorization() {
		return validAuthorization;
	}

	/**
	 * @param validAuthorization the validAuthorization to set
	 */
	public void setValidAuthorization(boolean validAuthorization) {
		this.validAuthorization = validAuthorization;
	}
}

/**
 * 
 */
package com.livescribe.admin.controller.dto;

import java.util.HashSet;
import java.util.Set;

import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;

/**
 * @author Mohammad M. Naqvi
 * @since 1.3
 *
 */
public class RegisteredUserDto {
	
	private String primaryEmail;
	private String uid;
	private Set<EvernoteAuthDto> evernoteAuthorizations = new HashSet<EvernoteAuthDto>();
	private Set<DeviceDto> registeredDevices = new HashSet<DeviceDto>();

	/**
	 * 
	 */
	public RegisteredUserDto() {
	}
	/**
	 * 
	 */
	
	public RegisteredUserDto(User user) {
		if (null != user) {
			primaryEmail = user.getPrimaryEmail();
			uid = user.getUid();
			
			if (null != user.getAuthorizations() && !user.getAuthorizations().isEmpty()) {
				for (Authorization auth : user.getAuthorizations()) {
					evernoteAuthorizations.add(new EvernoteAuthDto(auth));
				}
			}
			
			if (null != user.getRegisteredDevices() && !user.getRegisteredDevices().isEmpty()) {
				for (RegisteredDevice registeredDevice : user.getRegisteredDevices()) {
					registeredDevices.add(new DeviceDto(registeredDevice));
				}
			}
		}
	}


	/**
	 * @return the primaryEmail
	 */
	public String getPrimaryEmail() {
		return primaryEmail;
	}


	/**
	 * @param primaryEmail the primaryEmail to set
	 */
	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
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
	 * @return the evernoteAuthorizations
	 */
	public Set<EvernoteAuthDto> getEvernoteAuthorizations() {
		return evernoteAuthorizations;
	}

	/**
	 * @param evernoteAuthorizations the evernoteAuthorizations to set
	 */
	public void setEvernoteAuthorizations(
			Set<EvernoteAuthDto> evernoteAuthorizations) {
		this.evernoteAuthorizations = evernoteAuthorizations;
	}

	/**
	 * @return the registeredDevices
	 */
	public Set<DeviceDto> getRegisteredDevices() {
		return registeredDevices;
	}

	/**
	 * @param registeredDevices the registeredDevices to set
	 */
	public void setRegisteredDevices(Set<DeviceDto> registeredDevices) {
		this.registeredDevices = registeredDevices;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((evernoteAuthorizations == null) ? 0
						: evernoteAuthorizations.hashCode());
		result = prime * result
				+ ((primaryEmail == null) ? 0 : primaryEmail.hashCode());
		result = prime
				* result
				+ ((registeredDevices == null) ? 0 : registeredDevices
						.hashCode());
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegisteredUserDto other = (RegisteredUserDto) obj;
		if (evernoteAuthorizations == null) {
			if (other.evernoteAuthorizations != null)
				return false;
		} else if (!evernoteAuthorizations.equals(other.evernoteAuthorizations))
			return false;
		if (primaryEmail == null) {
			if (other.primaryEmail != null)
				return false;
		} else if (!primaryEmail.equals(other.primaryEmail))
			return false;
		if (registeredDevices == null) {
			if (other.registeredDevices != null)
				return false;
		} else if (!registeredDevices.equals(other.registeredDevices))
			return false;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RegisteredUserDto [primaryEmail=");
		builder.append(primaryEmail);
		builder.append(", uid=");
		builder.append(uid);
		builder.append(", evernoteAuthorizations=");
		builder.append(evernoteAuthorizations);
		builder.append(", registeredDevices=");
		builder.append(registeredDevices);
		builder.append("]");
		return builder.toString();
	}
}

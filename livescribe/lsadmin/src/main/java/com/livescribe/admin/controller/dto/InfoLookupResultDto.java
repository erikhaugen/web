/**
 * 
 */
package com.livescribe.admin.controller.dto;

import java.util.List;

/**
 * A DTO class that holds a list of registered user info and unregistered pens info. It encapsulates all the data that
 * is needed to display the InfoLookup result page. 
 * 
 * @see infoLookup.jsp
 * @see RegisteredUserDto
 * @see DeviceDto
 * 
 * @author Mohammad M. Naqvi
 * @since 1.3
 *
 */
public class InfoLookupResultDto {
	
	List<RegisteredUserDto> usersInfo;
	
	List<DeviceDto> unregisteredDevicesInfo;

	/**
	 * 
	 */
	public InfoLookupResultDto() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the usersInfo
	 */
	public List<RegisteredUserDto> getUsersInfo() {
		return usersInfo;
	}

	/**
	 * @param usersInfo the usersInfo to set
	 */
	public void setUsersInfo(List<RegisteredUserDto> usersInfo) {
		this.usersInfo = usersInfo;
	}

	/**
	 * @return the unregisteredDevicesInfo
	 */
	public List<DeviceDto> getUnregisteredDevicesInfo() {
		return unregisteredDevicesInfo;
	}

	/**
	 * @param unregisteredDevicesInfo the unregisteredDevicesInfo to set
	 */
	public void setUnregisteredDevicesInfo(List<DeviceDto> unregisteredDevicesInfo) {
		this.unregisteredDevicesInfo = unregisteredDevicesInfo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((unregisteredDevicesInfo == null) ? 0
						: unregisteredDevicesInfo.hashCode());
		result = prime * result
				+ ((usersInfo == null) ? 0 : usersInfo.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InfoLookupResultDto other = (InfoLookupResultDto) obj;
		if (unregisteredDevicesInfo == null) {
			if (other.unregisteredDevicesInfo != null)
				return false;
		} else if (!unregisteredDevicesInfo
				.equals(other.unregisteredDevicesInfo))
			return false;
		if (usersInfo == null) {
			if (other.usersInfo != null)
				return false;
		} else if (!usersInfo.equals(other.usersInfo))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InfoLookupResultDto [usersInfo=");
		builder.append(usersInfo);
		builder.append(", unregisteredDevicesInfo=");
		builder.append(unregisteredDevicesInfo);
		builder.append("]");
		return builder.toString();
	}

}

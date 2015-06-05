/**
 * 
 */
package com.livescribe.admin.controller.dto;

import java.util.Date;

import com.livescribe.framework.orm.consumer.Authorization;

/**
 * @author Mohammad M. Naqvi
 * @since 1.3
 *
 */
public final class EvernoteAuthDto {

	private String evernoteUserName;
	private String evernoteOAuthToken;
	private Long evernoteUserId;
	private Date evernoteAuthExpirationDate;
	private boolean primary;
	

	/**
	 * Default constructor.
	 * 
	 */
	public EvernoteAuthDto() {
	}
	
	public EvernoteAuthDto(Authorization evernoteAuth) {
		if (null != evernoteAuth) {
			evernoteOAuthToken = evernoteAuth.getOauthAccessToken();
			evernoteUserId = evernoteAuth.getEnUserId();
			evernoteUserName = evernoteAuth.getEnUsername();
			primary = evernoteAuth.getIsPrimary();
			if (null != evernoteAuth.getExpiration()) {
				evernoteAuthExpirationDate = evernoteAuth.getExpiration();
			}
		}
	}

	/**
	 * @return the evernoteUserName
	 */
	public String getEvernoteUserName() {
		return evernoteUserName;
	}



	/**
	 * @param evernoteUserName the evernoteUserName to set
	 */
	public void setEvernoteUserName(String evernoteUserName) {
		this.evernoteUserName = evernoteUserName;
	}

	/**
	 * @return the evernoteUserId
	 */
	public Long getEvernoteUserId() {
		return evernoteUserId;
	}

	/**
	 * @param evernoteUserId the evernoteUserId to set
	 */
	public void setEvernoteUserId(Long evernoteUserId) {
		this.evernoteUserId = evernoteUserId;
	}

	/**
	 * @return the evernoteAuthExpirationDate
	 */
	public Date getEvernoteAuthExpirationDate() {
		return evernoteAuthExpirationDate;
	}



	/**
	 * @param evernoteAuthExpirationDate the evernoteAuthExpirationDate to set
	 */
	public void setEvernoteAuthExpirationDate(Date evernoteAuthExpirationDate) {
		this.evernoteAuthExpirationDate = evernoteAuthExpirationDate;
	}



	/**
	 * @return the primary
	 */
	public boolean isPrimary() {
		return primary;
	}

	/**
	 * @param primary the primary to set
	 */
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	/**
	 * @return the evernoteOAuthToken
	 */
	public String getEvernoteOAuthToken() {
		return evernoteOAuthToken;
	}

	/**
	 * @param evernoteOAuthToken the evernoteOAuthToken to set
	 */
	public void setEvernoteOAuthToken(String evernoteOAuthToken) {
		this.evernoteOAuthToken = evernoteOAuthToken;
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
				+ ((evernoteAuthExpirationDate == null) ? 0
						: evernoteAuthExpirationDate.hashCode());
		result = prime
				* result
				+ ((evernoteOAuthToken == null) ? 0 : evernoteOAuthToken
						.hashCode());
		result = prime * result
				+ ((evernoteUserId == null) ? 0 : evernoteUserId.hashCode());
		result = prime
				* result
				+ ((evernoteUserName == null) ? 0 : evernoteUserName.hashCode());
		result = prime * result + (primary ? 1231 : 1237);
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
		EvernoteAuthDto other = (EvernoteAuthDto) obj;
		if (evernoteAuthExpirationDate == null) {
			if (other.evernoteAuthExpirationDate != null)
				return false;
		} else if (!evernoteAuthExpirationDate
				.equals(other.evernoteAuthExpirationDate))
			return false;
		if (evernoteOAuthToken == null) {
			if (other.evernoteOAuthToken != null)
				return false;
		} else if (!evernoteOAuthToken.equals(other.evernoteOAuthToken))
			return false;
		if (evernoteUserId == null) {
			if (other.evernoteUserId != null)
				return false;
		} else if (!evernoteUserId.equals(other.evernoteUserId))
			return false;
		if (evernoteUserName == null) {
			if (other.evernoteUserName != null)
				return false;
		} else if (!evernoteUserName.equals(other.evernoteUserName))
			return false;
		if (primary != other.primary)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EvernoteAuthDto [evernoteUserName=");
		builder.append(evernoteUserName);
		builder.append(", evernoteOAuthToken=");
		builder.append(evernoteOAuthToken);
		builder.append(", evernoteUserId=");
		builder.append(evernoteUserId);
		builder.append(", evernoteAuthExpirationDate=");
		builder.append(evernoteAuthExpirationDate);
		builder.append(", primary=");
		builder.append(primary);
		builder.append("]");
		return builder.toString();
	}

}

package com.livescribe.aws.login.dto;

import java.util.Date;

import com.livescribe.framework.orm.consumer.Authorization;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * DTO class used to hold the <code>Authorization</code> data.
 * 
 * @author Mohammad M. Naqvi
 *
 */
@XStreamAlias("authorization")
public class AuthorizationDto {

	@XStreamAlias("authorized")
	private Boolean authorized;
 	@XStreamAlias("authorizationId")
    private Long authorizationId;
 	@XStreamAlias("userEmail")
    private String userEmail;
 	@XStreamAlias("enUsername")
    private String enUsername;
 	@XStreamAlias("oauthAccessToken")
    private String oauthAccessToken;
 	@XStreamAlias("provider")
    private String provider;
 	@XStreamAlias("enShardId")
    private String enShardId;
 	@XStreamAlias("expiration")
    private Date expiration;
 	@XStreamAlias("created")
    private Date created;
 	@XStreamAlias("lastModified")
    private Date lastModified;
 	@XStreamAlias("lastModifiedBy")
    private String lastModifiedBy;
 	@XStreamAlias("enUserId")
    private Long enUserId;
 	@XStreamAlias("uid")
 	private String uid;
 	@XStreamAlias("isPrimary")
 	private Boolean isPrimary;

 	/**
 	 * The Default Constructor.
 	 */
 	public AuthorizationDto() {
 		
 	}

 	/**
 	 * The Constructor.
 	 */
 	public AuthorizationDto(Authorization authRecord) {

 		if (null != authRecord) {
 			authorizationId = authRecord.getAuthorizationId();
 			created = authRecord.getCreated();
 			enShardId = authRecord.getEnShardId();
 			enUserId = authRecord.getEnUserId();
 			enUsername = authRecord.getEnUsername();
 			expiration = authRecord.getExpiration();
 			isPrimary = authRecord.getIsPrimary();
 			lastModified = authRecord.getLastModified();
 			lastModifiedBy = authRecord.getLastModifiedBy();
 			oauthAccessToken = authRecord.getOauthAccessToken();
 			provider = authRecord.getProvider();
 			authorized = isAuthorized(authRecord);
 			if (null != authRecord.getUser()) {
 				uid = authRecord.getUser().getUid();
 				userEmail = authRecord.getUser().getPrimaryEmail();
 			}
 		}
 	}

 	// ***********************************************************************
 	// 							Getters and Setters
 	// ***********************************************************************

	public Boolean getAuthorized() {
		return authorized;
	}

	public void setAuthorized(Boolean authorized) {
		this.authorized = authorized;
	}

	public Long getAuthorizationId() {
		return authorizationId;
	}

	public void setAuthorizationId(Long authorizationId) {
		this.authorizationId = authorizationId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getEnUsername() {
		return enUsername;
	}

	public void setEnUsername(String enUsername) {
		this.enUsername = enUsername;
	}

	public String getOauthAccessToken() {
		return oauthAccessToken;
	}

	public void setOauthAccessToken(String oauthAccessToken) {
		this.oauthAccessToken = oauthAccessToken;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getEnShardId() {
		return enShardId;
	}

	public void setEnShardId(String enShardId) {
		this.enShardId = enShardId;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Long getEnUserId() {
		return enUserId;
	}

	public void setEnUserId(Long enUserId) {
		this.enUserId = enUserId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Boolean getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(Boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

 
 	// ***********************************************************************
 	// 							Helper Methods
 	// ***********************************************************************
 	
	private Boolean isAuthorized(Authorization authorization) {
		if (authorization == null) {
			return null;
		}
		
		// if expired, return false
		if (authorization.getExpiration() == null || authorization.getExpiration().before(new Date())) {
			return false;
		}

		// if not a valid OAuth token, return false
		if (null == authorization.getOauthAccessToken() || authorization.getOauthAccessToken().isEmpty() || authorization.getOauthAccessToken().equalsIgnoreCase("null")) {
			return false;
		}
		
		// if not a valid OAuth token, return false
		if (null == authorization.getEnShardId() || authorization.getEnShardId().isEmpty() || authorization.getEnShardId().equalsIgnoreCase("null")) {
			return false;
		}
		
		// all good..
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthorizationDto [authorized=");
		builder.append(authorized);
		builder.append(", authorizationId=");
		builder.append(authorizationId);
		builder.append(", userEmail=");
		builder.append(userEmail);
		builder.append(", enUsername=");
		builder.append(enUsername);
		builder.append(", oauthAccessToken=");
		builder.append(oauthAccessToken);
		builder.append(", provider=");
		builder.append(provider);
		builder.append(", enShardId=");
		builder.append(enShardId);
		builder.append(", expiration=");
		builder.append(expiration);
		builder.append(", created=");
		builder.append(created);
		builder.append(", lastModified=");
		builder.append(lastModified);
		builder.append(", lastModifiedBy=");
		builder.append(lastModifiedBy);
		builder.append(", enUserId=");
		builder.append(enUserId);
		builder.append(", uid=");
		builder.append(uid);
		builder.append(", isPrimary=");
		builder.append(isPrimary);
		builder.append("]");
		return builder.toString();
	}
}

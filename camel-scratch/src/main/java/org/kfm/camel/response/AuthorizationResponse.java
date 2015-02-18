/**
 * 
 */
package org.kfm.camel.response;

import java.io.Serializable;
import java.util.Date;

import org.kfm.camel.util.Utils;

import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class AuthorizationResponse implements Serializable {

	@XStreamAlias("responseCode")
	private ResponseCode responseCode;
	
	@XStreamAlias("authorized")
	private Boolean authorized;
	
 	@XStreamAlias("authorizationId")
    private Long authorizationId;
 	@XStreamAlias("user")
    private UserInfoResponse user;
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
	 * <p></p>
	 *
	 */
	public AuthorizationResponse() {
	}

	/**
	 * <p></p>
	 *
	 * @param code
	 */
	public AuthorizationResponse(ResponseCode code) {
		this(code, null);
	}

	/**
	 * <p></p>
	 * @param Authorization authorization
	 */
	public AuthorizationResponse(Authorization authorization) {
		this(null, authorization);
	}
	
	/**
	 * <p></p>
	 * @param ResponseCode code
	 * @param Authorization authorization
	 */
	public AuthorizationResponse(ResponseCode code, Authorization authorization) {
		super();
		if(code != null) {
			this.setResponseCode(code);
		}
		this.authorized = null;
		if (authorization != null) {
			this.authorizationId = authorization.getAuthorizationId();
			this.user = new UserInfoResponse(authorization.getUser());
			this.enUsername = authorization.getEnUsername();
			this.oauthAccessToken = authorization.getOauthAccessToken();
			this.provider = authorization.getProvider();
			this.enShardId = authorization.getEnShardId();
			this.expiration = authorization.getExpiration();
			this.created = authorization.getCreated();
			this.lastModified = authorization.getLastModified();
			this.lastModifiedBy = authorization.getLastModifiedBy();
			this.isPrimary = authorization.getIsPrimary();
			this.enUserId = authorization.getEnUserId();
			if (this.user != null) {
				this.uid = this.user.getUid();
			}
		}
	}

	/**
	 * @return the authorized
	 */
	public Boolean isAuthorized() {
		return authorized;
	}

	/**
	 * @param authorized the authorized to set
	 */
	public void setAuthorized(Boolean authorized) {
		this.authorized = authorized;
	}

	/**
	 * @return
	 */
	public Long getAuthorizationId() {
		return authorizationId;
	}
	
	/**
	 * @param evernoteUserId
	 */
	public void setAuthorizationId(Long authorizationId) {
		this.authorizationId = authorizationId;
	}

	public Boolean getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(Boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	/**
	 * @return the user
	 */
	public UserInfoResponse getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(UserInfoResponse user) {
		this.user = user;
	}

	/**
	 * @return Evernote user name
	 */
	public String getEnUsername() {
		return enUsername;
	}

	/**
	 * @param enUserName The Evernote user name
	 */
	public void setEnUsername(String enUsername) {
		this.enUsername = enUsername;
	}
	
	/**
	 * @return the authorizationToken
	 */
	public String getOauthAccessToken() {
		return oauthAccessToken;
	}

	/**
	 * @param authorizationToken the authorizationToken to set
	 */
	public void setOauthAccessToken(String oauthAccessToken) {
		this.oauthAccessToken = oauthAccessToken;
	}

	/**
	 * @return the provider
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * @param provider the provider to set
	 */
	public void setProvider(String provider) {
		this.provider = provider;
	}

	/**
	 * 
	 * @return
	 */
	public String getEnShardId() {
		return enShardId;
	}

	/**
	 * 
	 * @param evernoteShardId
	 */
	public void setEnShardId(String enShardId) {
		this.enShardId = enShardId;
	}

	/**
	 * @return the expiration
	 */
	public Date getExpiration() {
		return expiration;
	}

	/**
	 * @param expiration the expiration to set
	 */
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the lastModified
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the lastModifiedBy
	 */
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	/**
	 * @param lastModifiedBy the lastModifiedBy to set
	 */
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	/**
	 * @return
	 */
	public Long getEnUserId() {
		return enUserId;
	}
	
	/**
	 * @param evernoteUserId
	 */
	public void setEnUserId(Long enUserId) {
		this.enUserId = enUserId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("AuthorizationResponse <authorized = ").append(authorized);
		builder.append(", user = ").append(user);
		builder.append(", enUsername = ").append(enUsername);
		builder.append(", oauthAccessToken = ").append(Utils.obfuscateOAuthToken(oauthAccessToken));
		builder.append(", provider = ").append(provider);
		builder.append(", enShardId = ").append(enShardId);
		builder.append(", expiration = ").append(expiration);
		builder.append(", created = ").append(created);
		builder.append(", lastModified = ").append(lastModified);
		builder.append(", lastModifiedBy = ").append(lastModifiedBy);
		builder.append(", enUserId = ").append(enUserId);
		builder.append(", isPrimary = ").append(isPrimary);
		builder.append(", authorizationId = ").append(authorizationId);
		builder.append(", uid = ").append(uid).append(">");
		return builder.toString();
	}

	/**
	 * <p></p>
	 * 
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * <p></p>
	 * 
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * <p></p>
	 * 
	 * @return the responseCode
	 */
	public ResponseCode getResponseCode() {
		return responseCode;
	}

	/**
	 * <p></p>
	 * 
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}
}

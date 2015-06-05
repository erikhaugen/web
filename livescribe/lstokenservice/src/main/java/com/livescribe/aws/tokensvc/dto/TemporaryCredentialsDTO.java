/*
 * Created:  Nov 28, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.dto;

import com.amazonaws.services.securitytoken.model.Credentials;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("credentials")
public class TemporaryCredentialsDTO {

	String accessKeyId = null;
	String secretKey = null;
	String sessionToken = null;

	/**
	 * <p></p>
	 * 
	 */
	public TemporaryCredentialsDTO() {
		
	}
	
	/**
	 * <p></p>
	 * 
	 * @param credentials
	 */
	public TemporaryCredentialsDTO(Credentials credentials) {
		
		this.accessKeyId = credentials.getAccessKeyId();
		this.secretKey = credentials.getSecretAccessKey();
		this.sessionToken = credentials.getSessionToken();
	}

	/**
	 * <p></p>
	 * 
	 * @param accessKeyId
	 * @param secretKey
	 * @param sessionToken
	 */
	public TemporaryCredentialsDTO(String accessKeyId, String secretKey, String sessionToken) {
		
		this.accessKeyId = accessKeyId;
		this.secretKey = secretKey;
		this.sessionToken = sessionToken;
	}

	/**
	 * <p></p>
	 * 
	 * @return the accessKeyId
	 */
	public String getAccessKeyId() {
		return accessKeyId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * <p></p>
	 * 
	 * @return the sessionToken
	 */
	public String getSessionToken() {
		return sessionToken;
	}

	/**
	 * <p></p>
	 * 
	 * @param accessKeyId the accessKeyId to set
	 */
	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	/**
	 * <p></p>
	 * 
	 * @param secretKey the secretKey to set
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * <p></p>
	 * 
	 * @param sessionToken the sessionToken to set
	 */
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}
	
	public String toString() {
		
		String aki = getAccessKeyId();
		String sk = getSecretKey();
		String token = getSessionToken();
		StringBuilder builder = new StringBuilder();
		builder.append("\n--------------------------------------------------\n");
		builder.append("  Access Key ID:  " + aki + "\n");
		builder.append("     Secret Key:  " + sk + "\n");
		builder.append("   Secret Token:  " + token + "\n");
		builder.append("--------------------------------------------------\n");
		
		return builder.toString();
	}
}

/*
 * Created:  Sep 22, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.response;

import com.amazonaws.services.securitytoken.model.Credentials;
import com.livescribe.aws.tokensvc.dto.TemporaryCredentialsDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class CredentialResponse extends ServiceResponse {

	@XStreamAlias("credentials")
	private TemporaryCredentialsDTO tempCredentials = null;

	/**
	 * <p></p>
	 * 
	 */
	public CredentialResponse() {
	}

	/**
	 * <p></p>
	 *
	 * @param responseCode
	 */
	public CredentialResponse(ResponseCode responseCode) {
		super(responseCode);
	}

	/**
	 * <p></p>
	 * 
	 * @param tempCredentials
	 */
	public CredentialResponse(TemporaryCredentialsDTO tempCredentials) {
		
		this.tempCredentials = tempCredentials;
	}
		
	/**
	 * <p></p>
	 *
	 * @param responseCode
	 * @param tempCredentials
	 */
	public CredentialResponse(ResponseCode responseCode, TemporaryCredentialsDTO tempCredentials) {
		
		super(responseCode);
		this.tempCredentials = tempCredentials;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		String aki = tempCredentials.getAccessKeyId();
		String sk = tempCredentials.getSecretKey();
		String token = tempCredentials.getSessionToken();
		StringBuilder builder = new StringBuilder();
		builder.append("\n--------------------------------------------------\n");
		builder.append("  Access Key ID:  " + aki + "\n");
		builder.append("     Secret Key:  " + sk + "\n");
		builder.append("   Secret Token:  " + token + "\n");
		builder.append("--------------------------------------------------\n");
		
		return builder.toString();
	}

	/**
	 * <p></p>
	 * 
	 * @return the tempCredentials
	 */
	public TemporaryCredentialsDTO getTempCredentials() {
		return tempCredentials;
	}

	/**
	 * <p></p>
	 * 
	 * @param tempCredentials the tempCredentials to set
	 */
	public void setTempCredentials(TemporaryCredentialsDTO tempCredentials) {
		this.tempCredentials = tempCredentials;
	}
}

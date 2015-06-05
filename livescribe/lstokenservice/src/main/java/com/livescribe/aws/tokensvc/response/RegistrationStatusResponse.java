/**
 * 
 */
package com.livescribe.aws.tokensvc.response;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class RegistrationStatusResponse extends ServiceResponse {

	@XStreamAlias("status")
	private RegistrationStatus status;
	
	@XStreamAlias("email")
	private String email;
	
	/**
	 * <p></p>
	 *
	 */
	public RegistrationStatusResponse() {
		super();
	}

	/**
	 * <p></p>
	 *
	 * @param code
	 */
	public RegistrationStatusResponse(ResponseCode code) {
		super(code);
	}

	/**
	 * @return the status
	 */
	public RegistrationStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(RegistrationStatus status) {
		this.status = status;
	}

	/**
	 * @return email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
}

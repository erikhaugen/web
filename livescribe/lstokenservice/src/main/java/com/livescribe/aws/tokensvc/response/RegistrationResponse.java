/*
 * Created:  Sep 28, 2011
 *      By:  kmurdoff
 */
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
public class RegistrationResponse extends ServiceResponse {

	private String registrationToken;
	
	/**
	 * <p></p>
	 * 
	 */
	public RegistrationResponse() {
	}

	/**
	 * <p></p>
	 * 
	 * @return the registrationToken
	 */
	public String getRegistrationToken() {
		return registrationToken;
	}

	/**
	 * <p></p>
	 * 
	 * @param registrationToken the registrationToken to set
	 */
	public void setRegistrationToken(String registrationToken) {
		this.registrationToken = registrationToken;
	}

}

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
public class NewRegistrationResponse extends ServiceResponse {

	@XStreamAlias("loginToken")
	private String loginToken;
	
	/**
	 * <p></p>
	 *
	 */
	public NewRegistrationResponse() {
		
	}

	/**
	 * <p></p>
	 *
	 * @param code
	 */
	public NewRegistrationResponse(ResponseCode code) {
		super(code);
	}

	/**
	 * <p></p>
	 *
	 * @param code
	 * @param loginToken
	 */
	public NewRegistrationResponse(ResponseCode code, String loginToken) {
		super(code);
		this.loginToken = loginToken;
	}

	/**
	 * @return the loginToken
	 */
	public String getLoginToken() {
		return loginToken;
	}

	/**
	 * @param loginToken the loginToken to set
	 */
	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}

}

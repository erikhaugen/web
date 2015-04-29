/**
 * 
 */
package com.livescribe.framework.login.response;

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
public class LoginResponse extends ServiceResponse {

	@XStreamAlias("loginToken")
	private String loginToken;
	
	@XStreamAlias("uid")
	private String uid;
	
	/**
	 * <p></p>
	 *
	 */
	public LoginResponse() {
		super();
	}

	/**
	 * <p></p>
	 *
	 * @param code
	 */
	public LoginResponse(ResponseCode code) {
		super(code);
	}

	/**
	 * <p></p>
	 *
	 * @param code
	 * @param loginToken
	 */
	public LoginResponse(ResponseCode code, String loginToken, String uid) {
		super(code);
		this.loginToken = loginToken;
		this.uid = uid;
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

	/**
	 * @return uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "<response>" +
				"<responseCode>" + getResponseCode().getCode() + "</responseCode>" +
				"<loginToken>" + getLoginToken() + "</loginToken>" + 
				"<uid>" + getUid() + "</uid>" +
				"</response>";
	}
}

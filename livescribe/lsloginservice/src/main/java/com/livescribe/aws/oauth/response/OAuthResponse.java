package com.livescribe.aws.oauth.response;

import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class OAuthResponse extends ServiceResponse {

	@XStreamAlias("redirectUrl")
	private String redirectUrl;
	
	public OAuthResponse() {
		
	}
	
	public OAuthResponse(ResponseCode code, String redirectUrl) {
		super(code);
		this.redirectUrl = redirectUrl;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
	
}

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
public class ServiceResponse {

	@XStreamAlias("responseCode")
	private ResponseCode responseCode;
	
	/**
	 * <p>Constructor that takes a <code>ResponseCode</code> as an arguement.</p>
	 * 
	 */
	public ServiceResponse() {
	}
	
	/**
	 * <p>Constructor that takes a <code>ResponseCode</code> as an arguement.</p>
	 * 
	 * @param code The <code>ResponseCode</code> to set for the response.
	 */
	public ServiceResponse(ResponseCode code) {
		this.responseCode = code;
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

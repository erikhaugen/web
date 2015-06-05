/*
 * Created:  Sep 23, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.response;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>Represents an error response to a request.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class ErrorResponse extends ServiceResponse {

	@XStreamAlias("errorCode")
	private ResponseCode responseCode;
	
	@XStreamAlias("message")
	private String message;
	
	/**
	 * <p>Default class constructor.</p>
	 * 
	 */
	public ErrorResponse() {
	}

	/**
	 * <p>Constructor that takes a <code>ResponseCode</code> as an arguement.</p>
	 * 
	 * @param code The <code>ResponseCode</code> to set for the response.
	 */
	public ErrorResponse(ResponseCode code) {
		this.responseCode = code;
	}
	
	/**
	 * <p>Constructor that takes a <code>ResponseCode</code> and message as 
	 * arguements.</p>
	 * 
	 * @param code The <code>ResponseCode</code> to set for the response.
	 * @param message The message to return with the response.
	 */
	public ErrorResponse(ResponseCode code, String message) {
		this.responseCode = code;
		this.message = message;
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
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * <p></p>
	 * 
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * <p></p>
	 * 
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}

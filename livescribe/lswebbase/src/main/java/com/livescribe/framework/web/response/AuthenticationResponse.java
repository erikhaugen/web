/*
 * Created:  March 05, 2013
 *      By:  Gurmeet Kalra
 */
/**
 * 
 */
package com.livescribe.framework.web.response;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>Represents an http authentication response and that needs to be send to the client.
 * It also contains the authenticate header to be send in the response, if needed.</p>
 * 
 * @version 1.0
 */
@XStreamAlias("response")
public class AuthenticationResponse /* extends ServiceResponse */ {

	@XStreamAlias("httpErrorCode")
	private int httpResponseCode;
	
	@XStreamAlias("WWW-Authenticate")
	private String authenticateHeader;
	
	/**
	 * <p>Default class constructor.</p>
	 * 
	 */
	public AuthenticationResponse() {
	}

	/**
	 * <p>Constructor that takes a <code>ResponseCode</code> as an arguement.</p>
	 * 
	 * @param code The <code>ResponseCode</code> to set for the response.
	 */
	public AuthenticationResponse(int code) {
		this.httpResponseCode = code;
	}
	
	/**
	 * <p>Constructor that takes a <code>ResponseCode</code> and authenticate message as 
	 * arguments.</p>
	 * 
	 * @param code The <code>ResponseCode</code> to set for the response.
	 * @param authenticate The authenticate message to be set in the header of the response.
	 */
	public AuthenticationResponse(int code, String authenticate) {
		this.httpResponseCode = code;
		this.authenticateHeader = authenticate;
	}
	
	/**
	 * <p></p>
	 * 
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return httpResponseCode;
	}

	/**
	 * <p></p>
	 * 
	 * @return the message
	 */
	public String getAuthenticateMessage() {
		return authenticateHeader;
	}

	/**
	 * <p></p>
	 * 
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(int responseCode) {
		this.httpResponseCode = responseCode;
	}

	/**
	 * <p></p>
	 * 
	 * @param message the message to set
	 */
	public void setAuthenticateMessage(String message) {
		this.authenticateHeader = message;
	}

}

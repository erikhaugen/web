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
public class RegistrationCodeResponse extends ServiceResponse {

	@XStreamAlias("regCode")
	private String regCode;
	
	@XStreamAlias("expires")
	private long expires;
	
	/**
	 * <p></p>
	 *
	 */
	public RegistrationCodeResponse() {
		
	}

	/**
	 * <p></p>
	 *
	 * @param responseCode
	 */
	public RegistrationCodeResponse(ResponseCode responseCode) {
		super(responseCode);
	}
	
	/**
	 * <p></p>
	 *
	 * @param regCode
	 */
	public RegistrationCodeResponse(ResponseCode responseCode, String regCode) {
		
		this.regCode = regCode;
	}

	/**
	 * @return the regCode
	 */
	public String getRegCode() {
		return regCode;
	}

	/**
	 * @param regCode the regCode to set
	 */
	public void setRegCode(String regCode) {
		this.regCode = regCode;
	}

	/**
	 * @return the expires
	 */
	public long getExpires() {
		return expires;
	}

	/**
	 * @param expires the expires to set
	 */
	public void setExpires(long expires) {
		this.expires = expires;
	}

}

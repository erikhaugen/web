/*
 * Created:  Sep 30, 2011
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
public class UnregistrationResponse extends ServiceResponse {

	private boolean unregistered = true;
	
	/**
	 * <p></p>
	 * 
	 */
	public UnregistrationResponse() {
		super();
	}

	/**
	 * <p></p>
	 * 
	 * @param responseCode
	 */
	public UnregistrationResponse(ResponseCode responseCode) {
		super(responseCode);
	}
	
	/**
	 * <p></p>
	 * 
	 * @return the unregistered
	 */
	public boolean isUnregistered() {
		return unregistered;
	}

	/**
	 * <p></p>
	 * 
	 * @param unregistered the unregistered to set
	 */
	public void setUnregistered(boolean unregistered) {
		this.unregistered = unregistered;
	}

}

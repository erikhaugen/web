/**
 * Created:  Mar 28, 2014 5:19:31 PM
 */
package com.livescribe.scratch.velocity;

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
public class MergeResponse extends ServiceResponse {

	@XStreamAlias("mergedContent")
	private String mergedContent;
	
	/**
	 * <p></p>
	 * 
	 */
	public MergeResponse() {
	}

	/**
	 * <p></p>
	 * 
	 * @param code
	 */
	public MergeResponse(ResponseCode code) {
		super(code);
	}

	/**
	 * <p></p>
	 * 
	 * @param code
	 */
	public MergeResponse(ResponseCode code, String mergedContent) {
		super(code);
		this.mergedContent = mergedContent;
	}

	/**
	 * <p></p>
	 * 
	 * @return the mergedContent
	 */
	public String getMergedContent() {
		return mergedContent;
	}

	/**
	 * <p></p>
	 * 
	 * @param mergedContent the mergedContent to set
	 */
	public void setMergedContent(String mergedContent) {
		this.mergedContent = mergedContent;
	}

}

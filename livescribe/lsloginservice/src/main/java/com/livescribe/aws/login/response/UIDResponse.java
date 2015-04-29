/**
 * 
 */
package com.livescribe.aws.login.response;

import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class UIDResponse extends ServiceResponse {

	@XStreamAlias("uid")
	private String uid;

	/**
	 * <p></p>
	 *
	 */
	public UIDResponse() {
		super();
	}
	
	/**
	 * <p></p>
	 *
	 * @param code
	 */
	public UIDResponse(ResponseCode code) {
		super(code);
	}
	
	/**
	 * <p></p>
	 *
	 * @param code
	 * @param uid
	 */
	public UIDResponse(ResponseCode code, String uid) {
		super(code);
		this.uid = uid;
	}

	@Override
	public String toString() {
		
		XStream xStream = new XStream();
		xStream.autodetectAnnotations(true);
		String xml = xStream.toXML(this);
		
		return xml;
	}
	
	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
}

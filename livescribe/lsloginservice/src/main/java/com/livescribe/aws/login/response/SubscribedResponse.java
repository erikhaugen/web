package com.livescribe.aws.login.response;

import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class SubscribedResponse extends ServiceResponse{

	@XStreamAlias("subscribed")
	private boolean subscribed;
	
	public SubscribedResponse() {
		super();
	}
	
	public SubscribedResponse(ResponseCode code) {
		super(code);
	}
	
	public SubscribedResponse(ResponseCode code, boolean subscribed) {
		super(code);
		this.subscribed = subscribed;
	}
	
	@Override
	public String toString() {
		
		XStream xStream = new XStream();
		xStream.autodetectAnnotations(true);
		String xml = xStream.toXML(this);
		
		return xml;
	}
	
	public boolean getSubscribed() {
		return this.subscribed;
	}
	
	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}
}

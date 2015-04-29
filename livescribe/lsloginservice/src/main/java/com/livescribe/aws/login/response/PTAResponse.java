package com.livescribe.aws.login.response;

import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by Kiman on 23.07.14.
 */
@XStreamAlias("response")
public class PTAResponse extends ServiceResponse {
    @XStreamAlias("url")
    private String url;

    public PTAResponse() {
        super();
    }

    public PTAResponse(ResponseCode code) {
        super(code);
    }

    public PTAResponse(ResponseCode code, String url) {
        super(code);
        this.url = url;
    }

    @Override
    public String toString() {

        XStream xStream = new XStream();
        xStream.autodetectAnnotations(true);
        String xml = xStream.toXML(this);

        return xml;
    }
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

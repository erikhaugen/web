/**
 * Created:  Nov 20, 2014 11:04:20 PM
 */
package org.kfm.camel.translator;

import org.apache.log4j.Logger;
import org.kfm.camel.response.AuthorizationResponse;
import org.kfm.camel.response.UserInfoResponse;

import com.thoughtworks.xstream.XStream;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class StringToAuthorizationResponseTranslator {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p>Default class constructor.</p>
	 * 
	 */
	public StringToAuthorizationResponseTranslator() {
	}

	/**
	 * <p>Converts the given XML string into an <code>AuthorizationResponse</code> instance.</p>
	 * 
	 * @param xml The XML String to convert.
	 * 
	 * @return an <code>AuthorizationResponse</code> instance.
	 */
	public AuthorizationResponse translate(String xml) {
		
		logger.debug("translate() - xml = " + xml);
		
		XStream xstream = new XStream();
//		xstream.autodetectAnnotations(true);
		xstream.processAnnotations(AuthorizationResponse.class);
		xstream.processAnnotations(UserInfoResponse.class);
		AuthorizationResponse response = (AuthorizationResponse)xstream.fromXML(xml);
		if (response != null) {
			logger.debug("translate() - response = " + response.toString());
		}
		return response;
	}
}

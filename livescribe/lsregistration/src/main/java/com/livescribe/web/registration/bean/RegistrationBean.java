/**
 * Created:  Aug 13, 2013 5:03:42 PM
 */
package com.livescribe.web.registration.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.web.registration.util.RegistrationFactory;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegistrationBean {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * <p></p>
	 * 
	 */
	public RegistrationBean() {
	}
	
	/**
	 * <p>Parses the given stream of name/value parameters from the HTTP
	 * request and creates a registration record for storage in the database.</p>
	 * 
	 * @param stream The query string from the HTTP request.
	 * 
	 * @return A registration record to be stored in the database.
	 */
	public Registration register(InputStream stream) {
		
		String method = "register()";
		
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(stream, writer, "UTF-8");
		} catch (IOException ioe) {
			String msg = "";
		}
		
		logger.debug(method + " - Read stream:  " + writer.toString());
		
		String[] nameValuePairs = writer.toString().split("&");
		HashMap<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < nameValuePairs.length; i++) {
			String[] nvPair = nameValuePairs[i].split("=");
			try {
				params.put(nvPair[0], URLDecoder.decode(nvPair[1], "UTF-8"));
			} catch (UnsupportedEncodingException uee) {
				String msg = "UnsupportedEncodingException thrown while attempting to URL-decode '" + nvPair[0] + "' parameter value '" + nvPair[1] + "'.";
				logger.error(method + " - " + msg);
				continue;
			}
			logger.debug(method + " - Added [" + nvPair[0] + ", " + nvPair[1] + "]");
		}
		
		Registration registration = RegistrationFactory.create(params);
		
		return registration;
	}
}

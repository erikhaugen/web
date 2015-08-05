/**
 * Created:  Aug 20, 2013 12:23:09 PM
 */
package com.livescribe.web.registration.controller;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CustomWebArgumentResolver implements WebArgumentResolver {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public CustomWebArgumentResolver() {
		logger.debug("Instantiated.");
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.bind.support.WebArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.context.request.NativeWebRequest)
	 */
	@Override
	public Object resolveArgument(MethodParameter methodParameter,
			NativeWebRequest webRequest) throws Exception {

		String method = "resolveArgument()";

		String name = methodParameter.getParameterName();
		Class<?> clazz = methodParameter.getParameterType();
		logger.debug(method + " - parameter name is '" + name + "', Class is '" + clazz.getName() + "'.");
		
		Iterator<String> iter = webRequest.getParameterNames();
		while (iter.hasNext()) {
			String paramName = iter.next();
			logger.debug(method + " - " + paramName);
		}
		
//		RegistrationData data = new RegistrationData();
//		data.setAppId(webRequest.getParameter("appId"));
//		data.setDeviceId(webRequest.getParameter("deviceId"));
//		data.setAppId(webRequest.getParameter("penSerial"));
//		data.setAppId(webRequest.getParameter("penName"));
//		data.setAppId(webRequest.getParameter("firstName"));
//		data.setAppId(webRequest.getParameter("email"));
//		data.setAppId(webRequest.getParameter("locale"));
//		data.setAppId(webRequest.getParameter("location"));
//		String latStr = webRequest.getParameter("locLat");
//		float lat = Float.parseFloat(latStr);
//		data.setLocLat(lat);
//		String longStr = webRequest.getParameter("locLong");
//		float lon = Float.parseFloat(longStr);
//		data.setLocLong(lon);
//		String optInStr = webRequest.getParameter("optIn");
//		boolean optIn = Boolean.parseBoolean(optInStr);
//		data.setAppId();
		
		if (RegistrationData.class.equals(clazz)) {
			logger.debug(method + " - Returning MethodParameter (RegistrationData) object,");
			return methodParameter;
		}
		return WebArgumentResolver.UNRESOLVED;
	}

}

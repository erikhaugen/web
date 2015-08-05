/**
 * Created:  Aug 16, 2013 1:19:21 PM
 */
package com.livescribe.web.registration.validation;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegistrationDataValueProcessor implements
		RequestDataValueProcessor {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public RegistrationDataValueProcessor() {
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.support.RequestDataValueProcessor#processAction(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@Override
	public String processAction(HttpServletRequest request, String action) {
		
		String method = "processAction()";
		
		logger.debug(method);
		
		return action;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.support.RequestDataValueProcessor#processFormFieldValue(javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String processFormFieldValue(HttpServletRequest request, String name, String value, String type) {
		
		String method = "processFormFieldValue()";
		
		logger.debug(method);
		
		return value;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.support.RequestDataValueProcessor#getExtraHiddenFields(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {
		
		String method = "getExtraHiddenFields()";
		
		logger.debug(method);
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.support.RequestDataValueProcessor#processUrl(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@Override
	public String processUrl(HttpServletRequest request, String url) {
		
		String method = "processUrl()";
		
		logger.debug(method);
		
		return url;
	}
}

/**
 * Created:  Aug 20, 2013 12:46:28 PM
 */
package com.livescribe.web.registration.controller;

import org.apache.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CustomHandlerMethodArgumentResolver implements
		HandlerMethodArgumentResolver {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public CustomHandlerMethodArgumentResolver() {
		logger.debug("Instantiated.");
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#supportsParameter(org.springframework.core.MethodParameter)
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {

		String method = "supportsParameter()";
		
		Class<?> clazz = parameter.getParameterType();
		
		logger.debug(method + " - Parameter type:  " + clazz.getName());
		
		if (RegistrationData.class.equals(clazz)) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {

		String method = "resolveArgument()";

		String name = parameter.getParameterName();
		Class<?> clazz = parameter.getParameterType();
		logger.debug(method + " - parameter name is '" + name + "', Class is '" + clazz.getName() + "'.");
		
		return parameter;
	}

}

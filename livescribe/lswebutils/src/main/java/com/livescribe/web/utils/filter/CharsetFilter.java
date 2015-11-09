package com.livescribe.web.utils.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * This is a filter that can be used to set the request and response charset 
 * encodings. It uses UTF-8 as defaults but the defaults can be changed from 
 * the filter config in web.xml
 * 
 * Usage example: Edit your web.xml and configure the filter. The init params
 * are not necessary unless you want to use an encoding other than UTF-8. Then
 * configure the URL pattern the filter should be applied to.
 * 
 * 
 * 	<!--
 *	==========================================================================
 *		Filters and Mappings
 *	========================================================================== -->
 * 
 * <filter>
 *    <filter-name>CharsetFilter</filter-name>
 *    <filter-class>com.livescribe.web.utils.filter.CharsetFilter</filter-class>
 *      <init-param>
 *        <param-name>requestEncoding</param-name>
 *        <param-value>UTF-8</param-value>
 *      </init-param>
 *      <init-param>
 *        <param-name>responseEncoding</param-name>
 *        <param-value>UTF-8</param-value>
 *      </init-param>
 *      <init-param>
 *        <param-name>responseEncoding</param-name>
 *        <param-value>text/html</param-value>
 *      </init-param>
 *  </filter>
 *
 *  <filter-mapping>
 *    <filter-name>CharsetFilter</filter-name>
 *    <url-pattern>/*</url-pattern>
 *  </filter-mapping>
 * 
 *
 * @author smukker
 *
 */
public class CharsetFilter implements Filter {
	private String requestEncoding;
	private String responseEncoding;
	private String responseContentType;

	public void init(FilterConfig config) throws ServletException {
		requestEncoding = config.getInitParameter("requestEncoding");
		if (requestEncoding == null)  {
			requestEncoding = "UTF-8";
		}
		responseEncoding = config.getInitParameter("responseEncoding");
		if (responseEncoding == null)  {
			responseEncoding = "UTF-8";
		}
		responseContentType = config.getInitParameter("responseContentType");
		if ( responseContentType == null ) {
			responseContentType = "text/html; charset=" + responseEncoding;
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain next)
	throws IOException, ServletException {
		// Respect the client-specified character encoding
		// (see HTTP specification section 3.4.1)
		if (null == request.getCharacterEncoding()) {
			request.setCharacterEncoding(requestEncoding);
		}
		/**
		 * Set the default response content type and encoding
		 */
		response.setContentType(responseContentType);
		response.setCharacterEncoding(responseEncoding);

		next.doFilter(request, response);
	}

	public void destroy() {
	}
}

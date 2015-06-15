/**
 * 
 */
package com.livescribe.community.view;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * <p></p>
 * 
 * @author kmurdoff
 *
 */
public class UTFResponseFilter implements Filter {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * <p></p>
	 */
	public UTFResponseFilter() {
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		String uri = ((HttpServletRequest)request).getRequestURI();
		response.setCharacterEncoding("UTF-8");
		
//		if (uri.contains(".atom")) {
//			response.setContentType("text/xml+atom; charset=UTF-8");
//			logger.debug("Set content to 'text/xml+atom'");
//		}
//		else if (uri.contains(".html")) {
//			response.setContentType("text/html; charset=UTF-8");
//			logger.debug("Set content to 'text/plain'");
//		}
//		else {
//			response.setContentType("text/plain; charset=UTF-8");
//			logger.debug("Set content to 'text/plain'");
//		}
		chain.doFilter(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		
		logger.debug("Filter initialized.");
	}
}

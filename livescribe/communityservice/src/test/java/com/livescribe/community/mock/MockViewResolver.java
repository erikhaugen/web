/**
 * 
 */
package com.livescribe.community.mock;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MockViewResolver implements ViewResolver {

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.ViewResolver#resolveViewName(java.lang.String, java.util.Locale)
	 */
	@Override
	public View resolveViewName(final String viewName, Locale locale) throws Exception {
		
        return new View() {
            
        	/* (non-Javadoc)
        	 * @see org.springframework.web.servlet.View#getContentType()
        	 */
        	public String getContentType() {
        		
                return null;
            }
            
        	/* (non-Javadoc)
        	 * @see org.springframework.web.servlet.View#render(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
        	 */
        	public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                
            	response.getWriter().write(viewName);
            }
        };
	}
}

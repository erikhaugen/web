/**
 * 
 */
package com.livescribe.community.mock;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MockWebContextLoader extends AbstractContextLoader {
	
	private Logger logger = Logger.getLogger(getClass());
	
    public static final ServletContext SERVLET_CONTEXT = new MockServletContext("/WEB-INF", new FileSystemResourceLoader());
    
    private final static GenericWebApplicationContext webContext = new GenericWebApplicationContext();
 
    /**
     * <p></p>
     * 
     * @param context
     * 
     * @return
     */
    protected BeanDefinitionReader createBeanDefinitionReader(final GenericApplicationContext context) {
    	
    	return new XmlBeanDefinitionReader(context);
    }
 
	/* (non-Javadoc)
	 * @see org.springframework.test.context.ContextLoader#loadContext(java.lang.String[])
	 */
	@Override
    public final ConfigurableApplicationContext loadContext(final String... locations) throws Exception {
		
		logger.debug("Context Path:  " + SERVLET_CONTEXT.getContextPath());
		
        SERVLET_CONTEXT.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webContext);
        webContext.setServletContext(SERVLET_CONTEXT);
        createBeanDefinitionReader(webContext).loadBeanDefinitions(locations);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(webContext);
        webContext.refresh();
        webContext.registerShutdownHook();
        
        return webContext;
    }
 
    /**
     * <p></p>
     * 
     * @return
     */
    public static WebApplicationContext getInstance() {
    	
        return webContext;
    }
 
	/* (non-Javadoc)
	 * @see org.springframework.test.context.support.AbstractContextLoader#getResourceSuffix()
	 */
	@Override
    protected String getResourceSuffix() {
		
        return "-context.xml";
    }
}

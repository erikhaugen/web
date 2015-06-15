/**
 * 
 */
package com.livescribe.community.view;

import java.util.Map;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.livescribe.community.BaseTest;
import com.livescribe.community.mock.MockViewResolver;
import com.livescribe.community.mock.MockWebContextLoader;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(loader=MockWebContextLoader.class, locations={"classpath:/configuration/local/communityservice-test-context.xml"})
public abstract class AbstractControllerTest extends BaseTest {

    private static DispatcherServlet dispatcherServlet;
    
	/**
	 * <p>Returns a testing instance of a <code>DispatcherServlet</code>.</p>
	 * 
	 * @return a testing instance of a <code>DispatcherServlet</code>.
	 */
	public static DispatcherServlet getServletInstance() {
		
        try {
            if (null == dispatcherServlet) {
            	dispatcherServlet = new DispatcherServlet() {
                    
                	protected WebApplicationContext createWebApplicationContext(WebApplicationContext parent) {
                        
//                    	GenericWebApplicationContext wac = new GenericWebApplicationContext();
//                        wac.setParent(MockWebContextLoader.getInstance());
//                        wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(MockViewResolver.class));
//                        wac.refresh();
//                        
//                        return wac;
                		GenericWebApplicationContext wac = (GenericWebApplicationContext)MockWebContextLoader.getInstance();
                		wac.refresh();
                		
                		return wac;
                	}
                };
 
                dispatcherServlet.init(new MockServletConfig());
            }
        } 
        catch (Throwable t) {
            Assert.fail("Unable to create a dispatcher servlet: " + t.getMessage());
        }
        return dispatcherServlet;
    }
 
    /**
     * <p>Creates a mock HTTP request.</p>
     * 
     * @param method The HTTP method to set on the request.
     * @param uri The URI of the request.
     * @param params A list of name/value pairs for use in a query string.
     * 
     * @return a mock HTTP request.
     */
    protected MockHttpServletRequest mockRequest(String method, String uri, Map<String ,String> params) {
        
    	MockHttpServletRequest req = new MockHttpServletRequest(method, uri);
    	req.addHeader("Content-Type", "application/atom+xml");
        for (String key : params.keySet()) {
            req.addParameter(key, params.get(key));
        }
        return req;
    }
 
    /**
     * <p>Returns a new mock HTTP response object.</p>
     * 
     * @return a new mock HTTP response object.
     */
    protected MockHttpServletResponse mockResponse() {
        return new MockHttpServletResponse();
    }
}

/**
 * Created:  Sep 5, 2013 2:32:04 PM
 */
package com.livescribe.admin.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.HttpURI;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.handler.AbstractHandler;

import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.thoughtworks.xstream.XStream;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class AdminHandler extends AbstractHandler {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public AdminHandler() {
	}

	/* (non-Javadoc)
	 * @see org.mortbay.jetty.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, int)
	 */
	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException,
			ServletException {
		
		String method = "handle()";
		
		logger.debug(method + "Handling request ...");
		logger.debug(method + "dispatch mode:  " + dispatch);
		
		Request req = null;
		if (request instanceof Request) {
			req = (Request)request;
		}
		else {
			req = HttpConnection.getCurrentConnection().getRequest();
		}
		
		//	Per Jetty documentation: 
		//	"It is also very important that a handler indicate that it has 
		//	completed handling the request and that the request should not 
		//	be passed to other handlers:"
		req.setHandled(true);
		
		Response resp = null;
		if (response instanceof Response) {
			resp = (Response)response;
		}
		else {
			resp = (Response)HttpConnection.getCurrentConnection().getResponse();
		}
		
		HttpURI uri = req.getUri();
		String path = uri.getPath();
		logger.debug(method + "URI path: " + path);
		
		if (path.contains("/sync/delete/")) {
			
			//--------------------------------------------------
			//	Prepare the response.
			ServiceResponse svcResponse = new ServiceResponse(ResponseCode.SUCCESS);

			XStream xStream = new XStream();
			xStream.alias("response", ServiceResponse.class);
			String respXML = xStream.toXML(svcResponse);

			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("text/xml");
			resp.getWriter().print(respXML);
		}
		else {
			logger.error(method + "Didn't receive a supported URI.");
		}
	}

}

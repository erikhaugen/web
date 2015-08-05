/**
 * Created:  Nov 5, 2013 1:01:55 PM
 */
package com.livescribe.web.registration.jetty;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class HttpTestServer {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public static final int HTTP_PORT = 60204;
	
	private Server server;
	
	/**
	 * <p></p>
	 * 
	 */
	public HttpTestServer() {
		this.server = new Server(HTTP_PORT);
		RegistrationHandler handler = new RegistrationHandler();
		this.server.setHandler(handler);
	}

	/**
	 * <p>Starts the Jetty HTTP server.</p>
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		this.server.start();
		logger.debug("Test server started.");
	}
	
	/**
	 * <p>Stops the Jetty HTTP server.</p>
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception {
		this.server.stop();
		logger.debug("Test server stopped.");
	}
}

/**
 * 
 */
package com.livescribe.aws.login.client.jetty;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;

/**
 * <p>A &quot;mock&quot; HTTP server used to receive requests from the
 * <code>LoginClient</code> during unit testing and hand them off to the
 * <code>LoginHandler</code> for processing.</p>
 * 
 * <p>Uses Jetty 6</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 * @see LoginHandler
 */
public class HttpTestServer {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public static final int HTTP_PORT = 60003;
	
	private Server server;
	
	/**
	 * <p>Default class constructor.</p>
	 *
	 */
	public HttpTestServer() {
		this.server = new Server(HTTP_PORT);		
		LoginHandler handler = new LoginHandler();
		this.server.setHandler(handler);
		
		logger.debug("Handler added to Server.");
	}

	/**
	 * <p>Starts the Jetty HTTP server.</p>
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		this.server.start();
		logger.debug("START - Test server");		
	}
	
	/**
	 * <p>Stops the Jetty HTTP server.</p>
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception {
		this.server.stop();
		logger.debug("STOP - Test server");
	}
}

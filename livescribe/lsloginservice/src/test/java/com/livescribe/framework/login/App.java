/**
 * Created:  Nov 12, 2013 11:35:19 AM
 */
package com.livescribe.framework.login;

import org.apache.log4j.Logger;

import com.livescribe.aws.login.client.jetty.HttpTestServer;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class App extends BaseTest {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static HttpTestServer server = null;
	
	/**
	 * <p></p>
	 * 
	 */
	public App() {
	}

	/**
	 * <p></p>
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		String method = "main()";
		
		//	Create a Thread to be used during shutdown of this process.
		Thread runtimeHookThread = new Thread() {
			public void run() {
				shutdownHook();
			}
		};
		
		//	Register shutdown Thread with the runtime.
		Runtime.getRuntime().addShutdownHook (runtimeHookThread);

		server = new HttpTestServer();
		server.start();
		System.out.println("HttpTestServer started ...");
	}

	/**
	 * <p></p>
	 * 
	 */
	protected static void shutdownHook() {
		
		String method = "shutdownHook()";
		
		System.out.println("Shutdown complete.");
	}
}

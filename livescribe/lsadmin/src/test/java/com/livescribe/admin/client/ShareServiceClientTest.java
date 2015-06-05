/**
 * Created:  Sep 5, 2013 2:26:27 PM
 */
package com.livescribe.admin.client;

import org.junit.Before;
import org.junit.Test;

import com.livescribe.admin.BaseTest;
import com.livescribe.admin.jetty.HttpTestServer;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ShareServiceClientTest extends BaseTest {

	private HttpTestServer server = null;
	private ShareServiceClient client = null;
	
	/**
	 * <p></p>
	 * 
	 */
	public ShareServiceClientTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		
		//	Start the Mock HTTP Server.
		this.server = new HttpTestServer();
		this.server.start();
		
		this.client = new ShareServiceClient("shareclient.properties");
	}
	
	@Test
	public void testDeleteDocumentsByUserAndPen_StringString() {
		
	}

//	@Test
	public void testDeleteDocumentsByUserAndPen_MapString() {
		
	}
}

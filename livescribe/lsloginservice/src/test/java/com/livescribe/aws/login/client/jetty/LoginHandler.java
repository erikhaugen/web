/**
 * Created:  Sep 26, 2013 12:00:39 PM
 */
package com.livescribe.aws.login.client.jetty;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.HttpURI;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.handler.AbstractHandler;

import com.livescribe.aws.login.dto.AuthorizationDto;
import com.livescribe.aws.login.response.AuthorizationListResponse;
import com.livescribe.aws.login.response.AuthorizationResponse;
import com.livescribe.framework.login.TestConstants;
import com.livescribe.framework.login.mock.MockAuthorizationFactory;
import com.livescribe.framework.login.response.LoginResponse;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.thoughtworks.xstream.XStream;

/**
 * <p>Handles HTTP requests from the <code>LoginClient</code> during unit
 * testing.</p>
 * 
 * <p>Returns mock responses to known test requests.</p>
 * 
 * <p>Uses Jetty 6</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class LoginHandler extends AbstractHandler implements TestConstants {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String URI_FIND_AUTH_BY_UID_PROVIDER_UID		= "/findAuthorizationByUIDAndProviderUserId.xml";
	private static final String URI_FIND_AUTH_BY_UID					= "/auth/";
	private static final String URI_NEW_WIFI_ACCOUNT					= "/newWifiAccount/";
	private static final String URI_LOGIN								= "/login";
	private static final String URI_LOGIN_WIFI_USER						= "/loginWifiUser";
	private static final String URI_IS_LOGGED_IN						= "/isloggedin";

	/**
	 * <p></p>
	 * 
	 */
	public LoginHandler() {
		logger.debug("Instantiated.");
	}

	/* (non-Javadoc)
	 * @see org.mortbay.jetty.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, int)
	 */
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException,
			ServletException {

		String method = "handle()";

		//--------------------------------------------------
		//	Cast to a Jetty Request object.
		Request req = null;
		if (request instanceof Request) {
			req = (Request)request;
		}
		else {
			req = HttpConnection.getCurrentConnection().getRequest();
		}

		//--------------------------------------------------
		//	Per Jetty documentation: 
		//	"It is also very important that a handler indicate that it has 
		//	completed handling the request and that the request should not 
		//	be passed to other handlers:"
		req.setHandled(true);
		
		//--------------------------------------------------
		//	Cast to a Jetty Response object.
		Response resp = null;
		if (response instanceof Response) {
			resp = (Response)response;
		}
		else {
			resp = (Response)HttpConnection.getCurrentConnection().getResponse();
		}
		
		//--------------------------------------------------
		//	Get the URI of the request.
		HttpURI uri = req.getUri();
		String path = uri.getPath();
		logger.debug(method + " - URI path: " + path);
		
		//--------------------------------------------------
		//	Handle specific requests.
		if (path.contains(URI_FIND_AUTH_BY_UID_PROVIDER_UID)) {
			Authorization auth = MockAuthorizationFactory.create();
			AuthorizationResponse authResponse = new AuthorizationResponse(ResponseCode.SUCCESS, auth);
			XStream xStream = new XStream();
			xStream.alias("response", AuthorizationResponse.class);
			String respXML = xStream.toXML(authResponse);
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("text/xml");
			resp.getWriter().print(respXML);
		}
		else if (path.contains(URI_FIND_AUTH_BY_UID)) {
			ArrayList<AuthorizationDto> authList = new ArrayList<AuthorizationDto>();
			AuthorizationDto authDto = new AuthorizationDto();
			authDto.setAuthorized(new Boolean(true));
			authDto.setEnShardId("s1");
			authDto.setEnUserId(365064L);
			authDto.setEnUsername("paul_kiman_at_me_com");
//			authDto.setExpiration("2014-06-14 08:59:19");
			authDto.setIsPrimary(true);
			authDto.setOauthAccessToken("S=s1:U=59208:E=1469b1abd04:C=13f43699108:P=18d:A=ls-web-test:V=2:H=6a57f5064e18ebfedf5de37016a93e17");
			authDto.setProvider("EN");
			authDto.setUid("jftK2hSWf079");
			authDto.setUserEmail("paul.kiman+test100@gmail.com");
			authList.add(authDto);
			AuthorizationListResponse authResponse = new AuthorizationListResponse(ResponseCode.SUCCESS, authList);

			XStream xStream = new XStream();
			xStream.alias("response", AuthorizationListResponse.class);
			xStream.alias("authorization", AuthorizationDto.class);
			String respXML = xStream.toXML(authResponse);
			
			logger.debug(method + " - response XML:  \n" + respXML);
			
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("text/xml");
			resp.getWriter().print(respXML);
		}
		else if (path.contains(URI_LOGIN_WIFI_USER)) {
			String email = req.getParameter("email");
			String password = req.getParameter("password");
			String respXML = null;
			XStream xStream = new XStream();
			LoginResponse loginResponse = null;
			
			//	Handle SUCCESS login.
			if ((XML_LOADED_EMAIL_1.equals(email)) && (XML_LOADED_PASSWORD_1.equals(password))) {
				loginResponse = new LoginResponse(ResponseCode.SUCCESS, XML_LOADED_LOGIN_TOKEN_1, XML_LOADED_USER_UID_1);
				xStream.alias("response", LoginResponse.class);
				respXML = xStream.toXML(loginResponse);
				logger.debug("SUCCESS - respXML:  \n" + respXML);
			}
			
			//	Handle failure case:  Incorrect password.
			else if ((XML_LOADED_EMAIL_1a.equals(email)) && (XML_LOADED_PASSWORD_1a.equals(password))) {
				ErrorResponse errResponse = new ErrorResponse(ResponseCode.INCORRECT_PASSWORD, "Incorrect password.");
				
//	This causes the marshalling to honor the @XStreamAlias annotation.
				xStream.autodetectAnnotations(true);
				
//	This DOES NOT honor the @XStreamAlias annotation during marshalling.  (???)
//				xStream.alias("response", ErrorResponse.class);
				
				respXML = xStream.toXML(errResponse);
				logger.debug("Incorrect password - respXML:  \n" + respXML);
			}

			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("text/xml");
			resp.getWriter().print(respXML);
		}
		else if (path.contains(URI_IS_LOGGED_IN)) {
			XStream xStream = new XStream();
			LoginResponse loginResponse = new LoginResponse(ResponseCode.SUCCESS);
			loginResponse.setUid(XML_LOADED_USER_UID_2);
			xStream.autodetectAnnotations(true);
			String respXML = xStream.toXML(loginResponse);
			logger.debug("SUCCESS - respXML:  \n" + respXML);
			
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("text/xml");
			resp.getWriter().print(respXML);
		}
		else {
			logger.error(method + " - Didn't receive a supported URI.");
		}
	}
}

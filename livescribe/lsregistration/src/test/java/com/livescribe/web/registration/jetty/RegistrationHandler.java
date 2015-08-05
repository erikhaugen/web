/**
 * Created:  Nov 5, 2013 1:04:25 PM
 */
package com.livescribe.web.registration.jetty;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.HttpURI;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.handler.AbstractHandler;

import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.livescribe.framework.orm.vectordb.Warranty;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.TestConstants;
import com.livescribe.web.registration.client.MethodURI;
import com.livescribe.web.registration.dto.RegistrationDTO;
import com.livescribe.web.registration.dto.RegistrationHistoryDTO;
import com.livescribe.web.registration.dto.WarrantyDTO;
import com.livescribe.web.registration.mock.MockRegistrationFactory;
import com.livescribe.web.registration.mock.MockRegistrationHistoryFactory;
import com.livescribe.web.registration.mock.MockWarrantyFactory;
import com.livescribe.web.registration.response.RegistrationHistoryListResponse;
import com.livescribe.web.registration.response.RegistrationListResponse;
import com.livescribe.web.registration.response.RegistrationResponse;
import com.livescribe.web.registration.response.WarrantyListResponse;
import com.livescribe.web.registration.response.WarrantyResponse;
import com.thoughtworks.xstream.XStream;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegistrationHandler extends AbstractHandler implements MethodURI, TestConstants {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String PEN_DISPLAY_ID_1	= "AYE-ASX-DWY-UY";
	private static final String APP_ID_1			= "com.livescribe.web.registration.jetty.RegistrationHandler-1";
	private static final String APP_ID_2			= "com.livescribe.web.KFMTestApp-JMeter-1";
	private static final String EMAIL_1				= "kfm1@ls.com";

	private static String PARAM_NAME_APP_ID		= "appId";
	private static String PARAM_NAME_DISPLAY_ID	= "displayId";
	private static String PARAM_NAME_PEN_SERIAL	= "penSerial";
	private static String PARAM_NAME_FIRST_NAME	= "firstName";
	private static String PARAM_NAME_LAST_NAME	= "lastName";
	private static String PARAM_NAME_PEN_NAME	= "penName";
	private static String PARAM_NAME_EMAIL		= "email";
	private static String PARAM_NAME_EDITION	= "edition";
	private static String PARAM_NAME_LOCALE		= "locale";
	private static String PARAM_NAME_COUNTRY	= "country";
	private static String PARAM_NAME_OPT_IN		= "optIn";
	
	/**
	 * <p></p>
	 * 
	 */
	public RegistrationHandler() {
	}

	/* (non-Javadoc)
	 * @see org.mortbay.jetty.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, int)
	 */
	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException,
			ServletException {
		
		String method = "handle()";
		
		
		logger.debug(method + "- Handling request ...");
		logger.debug(method + "- dispatch mode:  " + dispatch);
		
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
		logger.debug(method + " - URI path: " + path);
		
		//	Handle specific requests.

		/*--------------------------------------------------
		 * Register Test Cases
		 *--------------------------------------------------*/
		if (path.contains("/" + METHOD_REGISTER)) {
			Enumeration names = req.getParameterNames();
			while (names.hasMoreElements()) {
				String name = (String)names.nextElement();
				logger.debug(method + " - " + name);
			}

			String appId = req.getParameter(PARAM_NAME_APP_ID);
			String penSerial = req.getParameter(PARAM_NAME_PEN_SERIAL);
			String email = req.getParameter(PARAM_NAME_EMAIL);
			
			//--------------------------------------------------
			//	Check for missing parameters.
			if ((appId == null) || (appId.isEmpty())) {
				sendErrorResponse(ResponseCode.MISSING_PARAMETER, "The '" + PARAM_NAME_APP_ID + "' parameter was missing.", resp);
				return;
			}
			if ((penSerial == null) || (penSerial.isEmpty())) {
				sendErrorResponse(ResponseCode.MISSING_PARAMETER, "The '" + PARAM_NAME_PEN_SERIAL + "' parameter was missing.", resp);
				return;
			}
			if ((email == null) || (email.isEmpty())) {
				sendErrorResponse(ResponseCode.MISSING_PARAMETER, "The '" + PARAM_NAME_EMAIL + "' parameter was missing.", resp);
				return;
			}
			
			//--------------------------------------------------
			//	Marshal the response into an XML String.
			ServiceResponse svcResponse = new ServiceResponse(ResponseCode.SUCCESS);
			XStream xStream = new XStream();
			xStream.alias("response", ServiceResponse.class);
			String respXML = xStream.toXML(svcResponse);
			
			logger.debug(method + " - \n" + respXML);
			
			returnResponse(resp, respXML);
		}

		/*--------------------------------------------------
		 * Find Registration Test Cases
		 *--------------------------------------------------*/
		else if (path.contains(METHOD_FIND_UNIQUE_REGISTRATION)) {
			Registration reg = new Registration();
			reg.setAppId(APP_ID_1);
			reg.setCountry("United States");
			reg.setEmail(EMAIL_1);
			reg.setLocale("en_US");
			reg.setPenSerial(PEN_DISPLAY_ID_1);
			RegistrationResponse regResponse = new RegistrationResponse(ResponseCode.SUCCESS, reg);
			RegistrationDTO regDTO = regResponse.getRegistrationDto();
			logger.debug(method + " - " + regDTO.toString());
			
			XStream xStream = new XStream();
			xStream.alias("response", RegistrationResponse.class);
			xStream.alias("registrationDto", RegistrationDTO.class);
			String respXML = xStream.toXML(regResponse);
			
			logger.debug(method + " - \n" + respXML);
			
			returnResponse(resp, respXML);
		}
		
		//	Handles 'testFindRegistrationByPenSerial_Fail_RegistrationNotFound()'
		else if (path.contains(METHOD_FIND_REGISTRATIONS_BY_PENSERIAL_EQUALS + "/" + PEN_DISPLAY_ID_NON_EXISTENT)) {
			
			sendErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, "No registration found for pen with display ID '" + PEN_DISPLAY_ID_NON_EXISTENT + "'", resp);
		}
		else if (path.contains(METHOD_FIND_REGISTRATIONS_BY_PENSERIAL_EQUALS)) {
			
			String[] parts = path.split("/");
			String penSerial = parts[4];
			Registration reg1 = MockRegistrationFactory.create(APP_ID_1, penSerial, EMAIL_1);
			Registration reg2 = MockRegistrationFactory.create(APP_ID_1, penSerial, "kfm7@ls.com");
			Registration reg3 = MockRegistrationFactory.create(APP_ID_2, penSerial, EMAIL_1);
			ArrayList<Registration> list = new ArrayList<Registration>();
			list.add(reg1);
			list.add(reg2);
			list.add(reg3);
			RegistrationListResponse regListResponse = new RegistrationListResponse(ResponseCode.SUCCESS, list);
			
			XStream xStream = new XStream();
			xStream.alias("response", RegistrationListResponse.class);
			xStream.alias("registration", RegistrationDTO.class);
			String respXML = xStream.toXML(regListResponse);
			
			logger.debug(method + " - \n" + respXML);
			
			returnResponse(resp, respXML);
		}

		/*--------------------------------------------------
		 * Find Registration History Test Cases
		 *--------------------------------------------------*/
		else if (path.contains(METHOD_FIND_REGISTRATION_HISTORY_BY_EMAIL)) {
			
			String urlDecodedEmail = extractEmailFromUri(path);
			
			List<RegistrationHistory> list = MockRegistrationHistoryFactory.create(4);
			
			//	Make sure they all have the same email address!
			for (RegistrationHistory rh : list) {
				rh.setEmail(urlDecodedEmail);
			}
			RegistrationHistoryListResponse rhListResponse = new RegistrationHistoryListResponse(ResponseCode.SUCCESS, list);
			
			XStream xStream = new XStream();
			xStream.alias("response", RegistrationHistoryListResponse.class);
			xStream.alias("registration", RegistrationHistoryDTO.class);
			String respXML = xStream.toXML(rhListResponse);
			
			logger.debug(method + " - XML:  \n" + respXML);
			
			returnResponse(resp, respXML);
		}
		else if (path.contains(METHOD_FIND_REGISTRATION_HISTORY_BY_PEN_SERIAL)) {
			
			int lastSlashIdx = path.lastIndexOf("/") + 1;
			String penSerialAndSuffix = path.substring(lastSlashIdx);
			
			//	Remove the '.xml' part.
			int end = penSerialAndSuffix.length() - 4;
			String penSerial = penSerialAndSuffix.substring(0, end);
			
			List<RegistrationHistory> list = MockRegistrationHistoryFactory.create(2);
			
			//	Make sure they all have the same email address!
			for (RegistrationHistory rh : list) {
				rh.setDisplayId(penSerial);
			}
			
			RegistrationHistoryListResponse rhListResponse = new RegistrationHistoryListResponse(ResponseCode.SUCCESS, list);
			
			XStream xStream = new XStream();
			xStream.alias("response", RegistrationHistoryListResponse.class);
			xStream.alias("registration", RegistrationHistoryDTO.class);
			String respXML = xStream.toXML(rhListResponse);
			
			logger.debug(method + " - XML:  \n" + respXML);
			
			returnResponse(resp, respXML);
		}
		
		else if (path.contains(URI_FIND_WARRANTY_AND_HISTORY_BY_PEN_SERIAL)) {
			logger.debug(URI_FIND_WARRANTY_AND_HISTORY_BY_PEN_SERIAL + " - Not implemented yet.");
		}
		
		/*--------------------------------------------------
		 * Find Warranty Test Cases
		 *--------------------------------------------------*/
		else if (path.contains(METHOD_FIND_WARRANTY_BY_EMAIL)) {
			List<Warranty> list = MockWarrantyFactory.create(3);
			WarrantyListResponse warrantyListResponse = new WarrantyListResponse(ResponseCode.SUCCESS, list);
			
			XStream xStream = new XStream();
			xStream.alias("response", WarrantyListResponse.class);
			xStream.alias("warranties", List.class);
			xStream.alias("warranty", WarrantyDTO.class);
			String respXML = xStream.toXML(warrantyListResponse);

			logger.debug(method + " - \n" + respXML);
			
			returnResponse(resp, respXML);
		}
		else if (path.contains(METHOD_FIND_WARRANTY_BY_PEN_SERIAL)) {
			logger.debug(METHOD_FIND_WARRANTY_BY_PEN_SERIAL + " - Not implemented yet.");
			
			Warranty warranty = MockWarrantyFactory.create();
			WarrantyResponse warrantyResponse = new WarrantyResponse(ResponseCode.SUCCESS, warranty);
			
			XStream xStream = new XStream();
			xStream.alias("response", WarrantyResponse.class);
			xStream.alias("warranty", WarrantyDTO.class);
			String respXML = xStream.toXML(warrantyResponse);

			logger.debug(method + " - \n" + respXML);
			
			//--------------------------------------------------
			//	Write the response XML String back to the caller.
			returnResponse(resp, respXML);
		}

		/*--------------------------------------------------
		 * Delete Registration Test Cases
		 *--------------------------------------------------*/
		else if (path.contains(METHOD_DELETE_REGISTRATION_BY_EMAIL)) {
			
			String urlDecodedEmail = extractEmailFromUri(path);

			String respXML = null;
			
			//	Use email #1 for the "Success" case.
			if (XML_LOADED_REGISTRATION_EMAIL_1.equals(urlDecodedEmail)) {
				
				ServiceResponse regResponse = new ServiceResponse(ResponseCode.SUCCESS);
				XStream xStream = new XStream();
				xStream.alias("response", ServiceResponse.class);
				respXML = xStream.toXML(regResponse);
			}
			else if ((urlDecodedEmail != null) && (urlDecodedEmail.isEmpty())) {
				sendErrorResponse(ResponseCode.MISSING_PARAMETER, "The '" + PARAM_NAME_EMAIL + "' parameter was missing.", resp);
			}
						
			logger.debug(method + " - XML:  \n" + respXML);
			
			returnResponse(resp, respXML);
		}
		else {
			logger.error(method + " - Didn't receive a supported URI.  " + path);
		}
	}

	/**
	 * <p>Returns the email address at the end of a URI.</p>
	 * 
	 * @param path The URI path to parse.
	 * 
	 * @return an email address.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private String extractEmailFromUri(String path)
			throws UnsupportedEncodingException {
		
		int lastSlashIdx = path.lastIndexOf("/") + 1;
		String emailAndSuffix = path.substring(lastSlashIdx);
		
		//	Remove the '.xml' part.
		int end = emailAndSuffix.length() - 4;
		String email = emailAndSuffix.substring(0, end);
		
		//	URL-decode the email address from the URL.
		String urlDecodedEmail = URLDecoder.decode(email, "UTF-8");
		
		return urlDecodedEmail;
	}

	/**
	 * <p>Writes the given response XML to the <code>PrintWriter</code> of 
	 * the given <code>Response</code> object.</p>
	 * 
	 * @param resp The <code>Response</code> object to use.
	 * @param respXML The XML string to write.
	 * 
	 * @throws IOException
	 */
	private void returnResponse(Response resp, String respXML)
			throws IOException {
		
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("text/xml");
		resp.getWriter().print(respXML);
	}

	private void sendErrorResponse(ResponseCode code, String message, Response response) throws IOException {
		
		//--------------------------------------------------
		//	Marshal the response into an XML String.
		ErrorResponse errResponse = new ErrorResponse(code, message);
		XStream xStream = new XStream();
		xStream.autodetectAnnotations(true);
//		xStream.alias("response", ErrorResponse.class);		//	This will not recognize the XStreamAlias('errorCode') annotation, for some reason. 
		String respXML = xStream.toXML(errResponse);

		logger.debug("Writing response XML:  \n" + respXML);
		
		returnResponse(response, respXML);
	}
}

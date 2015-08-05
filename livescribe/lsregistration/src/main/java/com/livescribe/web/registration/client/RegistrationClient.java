package com.livescribe.web.registration.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import com.livescribe.framework.client.AbstractClient;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.livescribe.framework.orm.vectordb.Warranty;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.controller.RegistrationData;
import com.livescribe.web.registration.dto.RegistrationDTO;
import com.livescribe.web.registration.dto.UserDto;
import com.livescribe.web.registration.dto.WarrantyDTO;
import com.livescribe.web.registration.exception.DeviceNotFoundException;
import com.livescribe.web.registration.exception.RegistrationAlreadyExistedException;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.exception.UnsupportedPenTypeException;
import com.livescribe.web.registration.response.RegistrationHistoryListResponse;
import com.livescribe.web.registration.response.RegistrationListResponse;
import com.livescribe.web.registration.response.RegistrationResponse;
import com.livescribe.web.registration.response.UserListResponse;
import com.livescribe.web.registration.response.WarrantyListResponse;
import com.livescribe.web.registration.response.WarrantyResponse;
import com.livescribe.web.registration.validation.ValidationUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class RegistrationClient extends AbstractClient implements RegistrationAPI, MethodURI {

	private static final String SUCCESS		= "SUCCESS";
	
	private static final String ENCODING_UTF_8	= "UTF-8";
	
	private static final String NODE_RESPONSE_CODE	= "responseCode";
	private static final String NODE_ERROR_CODE		= "errorCode";
	
	/**
	 * Default constructor
	 * @throws IOException 
	 */
	public RegistrationClient() throws IOException {
		super("registrationclient.properties");
	}
	
	/**
	 * <p>Deletes all Vector registration records for the user with the given
	 * email address.</p>
	 * 
	 * @param email The email address to use.
	 * 
	 * @throws InvalidParameterException 
	 * @throws ClientException 
	 */
	public void deleteByEmail(String email) throws InvalidParameterException, ClientException {
		
		String method = "deleteByEmail()";
		
		if ((email == null) || (email.isEmpty())) {
			throw new InvalidParameterException("The 'email' parameter was not provided.");
		}
		
		HttpClient httpClient = null;
		try {
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				
				//-------------------------------------------------------
				//	Create the base URL for the Service.
				String baseUrlString = createBaseUrlString();
				
				//-------------------------------------------------------
				//	Add the method-specific part to the URL.
				StringBuilder uriString = new StringBuilder();
				uriString.append(baseUrlString);
				uriString.append(METHOD_DELETE_REGISTRATION_BY_EMAIL).append("/");
				String encodedEmail = URLEncoder.encode(email, ENCODING_UTF_8);
				uriString.append(encodedEmail).append(".xml");
				
				logger.debug(method + " - URL:  " + uriString.toString());
				
				URI uri = new URI(uriString.toString());
				HttpDelete deleteRequest = createDeleteMethod(uri);
				
				httpResponse = send(httpClient, deleteRequest);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method + " - " + msg, use);
				throw new ClientException(msg, use);
			} catch (UnsupportedEncodingException uee) {
				String msg = "There was error in URI encoding.";
				logger.debug(method + " - " + msg, uee);
				throw new ClientException(msg, uee);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSRegistrationService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + " - HTTP Status:  " + statusCode);
				String reason = httpResponse.getStatusLine().getReasonPhrase();
				logger.debug(method + " - Reason:  " + reason);
				throw new ClientException("Error when making http call to LSRegistrationService. HTTP Status: " + statusCode);
			}
			
			//--------------------------------------------------
			//	Read the response into an XML String.
			String responseXml = null;
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				responseXml = readEntityIntoString(entity);
			}
			
			if ((responseXml == null) || (responseXml.isEmpty())) {
				String msg = "The response from the server was empty or 'null'.";
				throw new ClientException(msg);
			}
			
			logger.debug(method + " - responseXml = \n" + responseXml);
			
			//--------------------------------------------------
			//	If this is a SUCCESS response ...
			if (responseXml.contains(NODE_RESPONSE_CODE)) {
				
				//	... parse XML into a WarrantyResponse object.
				XStream xStream = new XStream(new DomDriver());
				xStream.alias("response", ServiceResponse.class);
				ServiceResponse response = (ServiceResponse)xStream.fromXML(responseXml);
				ResponseCode code = response.getResponseCode();
				if (code.equals(ResponseCode.SUCCESS)) {
					return;
				}
				else {
					throw new ClientException("An unknown error occurred.");
				}
			}
			else if (responseXml.contains(NODE_ERROR_CODE)) {
				
				//	...otherwise, parse into ErrorResponse object.
				ErrorResponse errResponse = parseErrorResponse(responseXml);
				if (errResponse == null) {
					String msg = "Error response from Service was either 'null' or empty.";
					throw new ClientException(msg);
				}
				
				ResponseCode code = errResponse.getResponseCode();
				String errorMessage = errResponse.getMessage();
				
				switch (code) {
				case MISSING_PARAMETER:
					throw new InvalidParameterException(errorMessage);
				default:
					throw new ClientException(errorMessage);
				}
			}
			else {
				//	Unknown response received.
				String msg = "Unknown response received from Service.";
				logger.error(method + " - " + msg);
				throw new ClientException(msg);
			}

		} finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.client.RegistrationAPI#register(com.livescribe.web.registration.controller.RegistrationData)
	 */
	@Override
	public ServiceResponse register(RegistrationData data) throws InvalidParameterException, DeviceNotFoundException, 
			UnsupportedPenTypeException, RegistrationAlreadyExistedException, ClientException {
		
		String method = "register()";
		
		if (data == null) {
			throw new InvalidParameterException("RegistrationData is null.");
		}
		
		// Validate penSerial param
		String penSerial = data.getPenSerial();
		if (penSerial == null || penSerial.isEmpty()) {
			String msg = "Missing 'penSerial' parameter.";
			logger.error(method + " - " + msg);
			throw new InvalidParameterException(msg);
		}			
		
		// Validate appId param
		String appId = data.getAppId();
		if (appId == null || appId.isEmpty()) {
			String msg = "Missing 'appId' parameter.";
			logger.error(method + " - " + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Validate email param
		String email = data.getEmail();
		if (email == null || email.isEmpty()) {
			String msg = "Missing 'email' parameter.";
			logger.error(method + " - " + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Validate email format
		if (!ValidationUtil.isValidEmailFormat(email)) {
			String msg = "Invalid email format.";
			logger.error(method + " - " + msg);
			throw new InvalidParameterException(msg);
		}

		// Validate locale param
		String locale = data.getLocale();
		if (locale == null || locale.isEmpty()) {
			String msg = "Missing 'locale' parameter.";
			logger.error(method + " - " + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Validate country param
		String country = data.getCountry();
		if (country == null || country.isEmpty()) {
			String msg = "Missing 'country' parameter.";
			logger.error(method + " - " + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Validate optIn param
		Boolean optIn = data.getOptIn();
		if (optIn == null) {
			String msg = "Missing 'optIn' parameter.";
			logger.error(method + " - " + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("penSerial", penSerial));
		params.add(new BasicNameValuePair("appId", appId));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("locale", locale));
		params.add(new BasicNameValuePair("country", locale));
		params.add(new BasicNameValuePair("optIn", optIn.toString()));
		
		if (null != data.getEdition()) {
			params.add(new BasicNameValuePair("edition", String.valueOf(data.getEdition())));
		}
		if (null != data.getPenName()) {
			params.add(new BasicNameValuePair("penName", data.getPenName()));
		}
		if (null != data.getFirstName()) {
			params.add(new BasicNameValuePair("firstName", data.getFirstName()));
		}
		if (null != data.getLastName()) {
			params.add(new BasicNameValuePair("lastName", data.getLastName()));
		}
		
		HttpClient httpClient = null;
		try {
			HttpResponse httpResponse;
			try {
				httpClient = new DefaultHttpClient();
				
				//-------------------------------------------------------
				//	Create the base URL for the Service.
				String baseUrlString = createBaseUrlString();
				
				//-------------------------------------------------------
				//	Add the method-specific part to the URL.
				StringBuilder uriString = new StringBuilder();
				uriString.append(baseUrlString);
				uriString.append(METHOD_REGISTER).append(".xml");
				
				logger.debug(method + " - URL:  " + uriString.toString());
				
				URI uri = new URI(uriString.toString());
				HttpPost postRequest = createPostMethod(uri, params);
				
				httpResponse = send(httpClient, postRequest);
				
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method + " - " + msg, use);
				throw new ClientException(msg, use);
				
			} catch (UnsupportedEncodingException uee) {
				String msg = "There was error in URI encoding.";
				logger.debug(method + " - " + msg, uee);
				throw new ClientException(msg, uee);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSRegistrationService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + " - HTTP Status:  " + statusCode);
				String reason = httpResponse.getStatusLine().getReasonPhrase();
				logger.debug(method + " - Reason:  " + reason);
				throw new ClientException("Error when making http call to LSRegistrationService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response from server. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response from server. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSRegistrationService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response from server.");
			}			
			
			ServiceResponse response = new ServiceResponse();
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					response.setResponseCode(ResponseCode.SUCCESS);
				}
				
			} else { // ERROR
				Node errorCodeNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
				if (errorCodeNode == null) {
					throw new ClientException("Error code was empty.");
				}
				String errorCode = errorCodeNode.getText();
				
				Node errorMessageNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_MESSAGE);
				String errorMessage;
				if (errorMessageNode == null) {
					errorMessage = "";
				} else {
					errorMessage = errorMessageNode.getText();
				}
				
				switch(ResponseCode.valueOf(errorCode)) {
				case MISSING_PARAMETER:
					throw new InvalidParameterException(errorMessage);
					
				case DEVICE_NOT_FOUND:
					throw new DeviceNotFoundException(errorMessage);
					
				case DUPLICATE_REGISTRATION_FOUND:
					throw new RegistrationAlreadyExistedException(errorMessage);
					
				case UNSUPPORTED_PEN_TYPE:
					throw new UnsupportedPenTypeException(errorMessage);
					
				default:
					throw new ClientException(errorMessage);
			}
			}
			
			return response;
			
		} finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.client.RegistrationAPI#unregister(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ServiceResponse unregister(String appId, String penSerial, String email) 
			throws InvalidParameterException, RegistrationNotFoundException, MultipleRecordsFoundException, ClientException {
		
		String method = "unregister()";
		
		// Validate penSerial param
		if (penSerial == null || penSerial.isEmpty()) {
			String msg = "Missing 'penSerial' parameter.";
			logger.error(method + " - " + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Validate appId param
		if (appId == null || appId.isEmpty()) {
			String msg = "Missing 'appId' parameter.";
			logger.error(method + " - " + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Validate email param
		if (email == null || email.isEmpty()) {
			String msg = "Missing 'email' parameter.";
			logger.error(method + " - " + msg);
			throw new InvalidParameterException(msg);
		}

		// Validate email format
		if (!ValidationUtil.isValidEmailFormat(email)) {
			String msg = "Invalid email format.";
			logger.error(method + " - " + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("penSerial", penSerial));
		params.add(new BasicNameValuePair("appId", appId));
		params.add(new BasicNameValuePair("email", email));
		
		HttpClient httpClient = null;
		try {
			HttpResponse httpResponse;
			try {
				httpClient = new DefaultHttpClient();
				
				// Create the URL of the request.
				String context = this.properties.getProperty(KEY_CONTEXT);
				String host = this.properties.getProperty(KEY_HOST);
				String port = this.properties.getProperty(KEY_PORT);		
				String path = context + "/" + METHOD_UNREGISTER + ".xml";
				URI uri = new URI(PROTOCOL + "://" + host + ":" + port + path);
				
				HttpPost postRequest = createPostMethod(uri, params);
				
				httpResponse = send(httpClient, postRequest);
				
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method + " - " + msg, use);
				throw new ClientException(msg, use);
				
			} catch (UnsupportedEncodingException uee) {
				String msg = "There was error in URI encoding.";
				logger.debug(method + " - " + msg, uee);
				throw new ClientException(msg, uee);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSRegistrationService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + " - HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSRegistrationService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response from server. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response from server. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSRegistrationService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response from server.");
			}			
			
			ServiceResponse response = new ServiceResponse();
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					response.setResponseCode(ResponseCode.SUCCESS);
				}
				
			} else { // ERROR
				Node errorCodeNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
				if (errorCodeNode == null) {
					throw new ClientException("Error code was empty.");
				}
				String errorCode = errorCodeNode.getText();
				
				Node errorMessageNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_MESSAGE);
				String errorMessage;
				if (errorMessageNode == null) {
					errorMessage = "";
				} else {
					errorMessage = errorMessageNode.getText();
				}
				
				switch(ResponseCode.valueOf(errorCode)) {
				case MISSING_PARAMETER:
					throw new InvalidParameterException(errorMessage);
					
				case REGISTRATION_NOT_FOUND:
					throw new RegistrationNotFoundException(errorMessage);
					
				case DUPLICATE_REGISTRATION_FOUND:
					throw new MultipleRecordsFoundException(errorMessage);
					
				default:
					throw new ClientException(errorMessage);
			}
			}
			
			return response;
			
		} finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.client.RegistrationAPI#findUniqueRegistration(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistrationResponse findUniqueRegistration(String appId, String penSerial, String email)
			throws InvalidParameterException, RegistrationNotFoundException, MultipleRecordsFoundException, ClientException, IllegalStateException, IOException {
		
		String method = "findUniqueRegistration()";
		
		// Validate penSerial param
		if (penSerial == null || penSerial.isEmpty()) {
			String msg = "Missing 'penSerial' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}				
		
		// Validate appId param
		if (appId == null || appId.isEmpty()) {
			String msg = "Missing 'appId' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Validate email param
		if (email == null || email.isEmpty()) {
			String msg = "Missing 'email' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Validate email format
		if (!ValidationUtil.isValidEmailFormat(email)) {
			String msg = "Invalid email format.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Construct the query string.
		HttpClient httpClient = null;
		try {
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				
				//-------------------------------------------------------
				//	Create the base URL for the Service.
				String baseUrlString = createBaseUrlString();
				
				//-------------------------------------------------------
				//	Add the method-specific part to the URL.
				StringBuilder uriString = new StringBuilder();
				uriString.append(baseUrlString);
				uriString.append(METHOD_FIND_UNIQUE_REGISTRATION).append("/");
				String encodedPenSerial = URLEncoder.encode(penSerial, ENCODING_UTF_8);
				String encodedAppId = URLEncoder.encode(appId, ENCODING_UTF_8);
				String encodedEmailAddress = URLEncoder.encode(email, ENCODING_UTF_8);
				uriString.append(encodedPenSerial).append("/");
				uriString.append(encodedAppId).append("/");
				uriString.append(encodedEmailAddress).append(".xml");

				URI uri = new URI(uriString.toString());
				HttpGet getRequest = createGetMethod(uri);
				
				httpResponse = send(httpClient, getRequest);
				
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
				
			} catch (UnsupportedEncodingException uee) {
				String msg = "UnsupportedEncodingException thrown";
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSRegistrationService");
			}
			
			StatusLine statusLine = httpResponse.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode != 200) {
				String statusMessage = statusLine.getReasonPhrase();
				String msg = "HTTP Status:  " + statusCode + " - " + statusMessage;
				logger.debug(method + " - " + msg);
				throw new ClientException("Error when making http call to LSRegistrationService.  " + msg);
			}
			
			HttpEntity entity = httpResponse.getEntity();
			String responseXml = readEntityIntoString(entity);
			
			if ((responseXml == null) || (responseXml.isEmpty())) {
				String msg = "Response XML was either 'null' or empty.";
				logger.error(method + " - " + msg);
				throw new ClientException(msg);
			}
			
			logger.debug(method + " - responseXml = \n" + responseXml);
			
			//--------------------------------------------------
			//	If this is a SUCCESS response ...
			if (responseXml.contains(NODE_RESPONSE_CODE)) {
				
				//	... parse XML into a WarrantyResponse object.
				XStream xStream = new XStream(new DomDriver());
				xStream.alias("response", RegistrationResponse.class);
				xStream.alias("warranty", WarrantyDTO.class);
				RegistrationResponse response = (RegistrationResponse)xStream.fromXML(responseXml);
				return response;
			}
			else if (responseXml.contains(NODE_ERROR_CODE)) {
				
				//	...otherwise, parse into ErrorResponse object.
				ErrorResponse errResponse = parseErrorResponse(responseXml);
				if (errResponse == null) {
					String msg = "Error response from Service was either 'null' or empty.";
					throw new ClientException(msg);
				}
				
				ResponseCode code = errResponse.getResponseCode();
				String errorMessage = errResponse.getMessage();
				
				switch (code) {
				case MISSING_PARAMETER:
					throw new InvalidParameterException(errorMessage);
				case REGISTRATION_NOT_FOUND:
					throw new RegistrationNotFoundException(errorMessage);
				case DUPLICATE_REGISTRATION_FOUND:
					throw new MultipleRecordsFoundException(errorMessage);
				default:
					throw new ClientException(errorMessage);
				}
			}
			else {
				//	Unknown response received.
				String msg = "Unknown response received from Service.";
				logger.error(method + " - " + msg);
				throw new ClientException(msg);
			}
		} finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}


	@Override
	public RegistrationListResponse findRegistrationsListByAppId(String appId) throws InvalidParameterException,
																						RegistrationNotFoundException,
																						ClientException{

		String method = "findRegistrationsListByAppId()";
		
		// Validate appId param
		if (appId == null || appId.isEmpty()) {
			String msg = "Missing 'appId' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		return fetchRegListFromRegistrationService(METHOD_FIND_REGISTRATIONS_BY_APP_ID, appId);
	}

	@Override
	public RegistrationListResponse findRegistrationsListByEmail(String email) throws InvalidParameterException,
																						RegistrationNotFoundException,
																						ClientException{

		String method = "findRegistrationsListByEmail()";
		
		// Validate email param
		if (email == null || email.isEmpty()) {
			String msg = "Missing 'email' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Validate email format
		if (!ValidationUtil.isValidEmailFormat(email)) {
			String msg = "Invalid email format.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		return fetchRegListFromRegistrationService(METHOD_FIND_REGISTRATIONS_BY_EMAIL_EQUALS, email);
	}

	@Override
	public RegistrationListResponse findRegistrationsListByPartialEmail(String email) throws RegistrationNotFoundException,
																								ClientException,
																								InvalidParameterException {

		String method = "findRegistrationsListByPartialEmail()";

		// Validate email param
		if (email == null || email.isEmpty()) {
			String msg = "Missing 'email' parameter.";
			logger.error(method + msg);
			throw new IllegalArgumentException(msg);
		}
		
		return fetchRegListFromRegistrationService(METHOD_FIND_REGISTRATIONS_BY_EMAIL_CONTAINS, email);
	}

	@Override
	public RegistrationListResponse findRegistrationsListByPenSerial(String penSerial) throws InvalidParameterException,
																								RegistrationNotFoundException,
																								ClientException,
																								IllegalStateException,
																								IOException{
		
		String method = "findRegistrationsListByPenSerial()";
		
		// Validate penSerial param
		if (penSerial == null || penSerial.isEmpty()) {
			String msg = "Missing 'penSerial' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		return fetchRegListFromRegistrationService(METHOD_FIND_REGISTRATIONS_BY_PENSERIAL_EQUALS, penSerial);
	}

	@Override
	public RegistrationListResponse findRegistrationsListByPartialPenSerial(String penSerial) throws InvalidParameterException,
																								RegistrationNotFoundException,
																								ClientException,
																								IllegalStateException,
																								IOException{
		
		String method = "findRegistrationsListByPartialPenSerial()";
		
		// Validate penSerial param
		if (penSerial == null || penSerial.isEmpty()) {
			String msg = "Missing 'penSerial' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		return fetchRegListFromRegistrationService(METHOD_FIND_REGISTRATIONS_BY_PENSERIAL_CONTAINS, penSerial);
	}

	@Override
	public RegistrationListResponse findRegistrationsListByPenDisplayId(String penDisplayId) throws InvalidParameterException,
																								RegistrationNotFoundException,
																								ClientException,
																								IllegalStateException,
																								IOException{
		
		String method = "findRegistrationsListByPenSerial()";
		
		// Validate penSerial param
		if (penDisplayId == null || penDisplayId.isEmpty()) {
			String msg = "Missing 'penSerial' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		return fetchRegListFromRegistrationService(METHOD_FIND_REGISTRATIONS_BY_PENDISPLAYID_EQUALS, penDisplayId);
	}

	@Override
	public RegistrationListResponse findRegistrationsListByPartialPenDisplayId(String penDisplayId) throws InvalidParameterException,
																								RegistrationNotFoundException,
																								ClientException,
																								IllegalStateException,
																								IOException{
		
		String method = "findRegistrationsListByPartialPenSerial()";
		
		// Validate penSerial param
		if (penDisplayId == null || penDisplayId.isEmpty()) {
			String msg = "Missing 'penSerial' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		return fetchRegListFromRegistrationService(METHOD_FIND_REGISTRATIONS_BY_PENDISPLAYID_CONTAINS, penDisplayId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.client.RegistrationAPI#findWarrantyByPenSerial(java.lang.String)
	 */
	@Override
	public WarrantyResponse findWarrantyByPenSerial(String penSerialNumber) 
			throws InvalidParameterException, RegistrationNotFoundException, MultipleRecordsFoundException, ClientException {

		String method = "findWarrantyByPenSerial()";
		WarrantyResponse response = null;

		// Validate penSerialNumber param
		if (penSerialNumber == null || penSerialNumber.isEmpty()) {
			String msg = "Missing 'penSerialNumber' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}

		// Construct the query string.
		HttpClient httpClient = null;
		try {
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				
				//-------------------------------------------------------
				//	Create the base URL for the Service.
				String baseUrlString = createBaseUrlString();
				
				//-------------------------------------------------------
				//	Added the method-specific part to the URL.
				StringBuilder uriString = new StringBuilder();
				uriString.append(baseUrlString);
				uriString.append(METHOD_FIND_WARRANTY_BY_PEN_SERIAL).append("/");
				String encodedPenSerial = URLEncoder.encode(penSerialNumber, ENCODING_UTF_8);
				uriString.append(encodedPenSerial).append(".xml");

				URI uri = new URI(uriString.toString());
				
				HttpGet getRequest = createGetMethod(uri);
				httpResponse = send(httpClient, getRequest);
				
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
				
			} catch (UnsupportedEncodingException uee) {
				String msg = "UnsupportedEncodingException thrown";
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSRegistrationService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSRegistrationService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response from server. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response from server. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSRegistrationService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response from server.");
			}			

			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					// Parse the warranty node
					Node warrrantyNode = document.selectSingleNode("//*[name()='warranty']");

					return new WarrantyResponse(ResponseCode.SUCCESS, parseWarrantyXML(warrrantyNode));
				}
				
			} else { // ERROR
				Node errorCodeNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
				if (errorCodeNode == null) {
					throw new ClientException("Error code was empty.");
				}
				String errorCode = errorCodeNode.getText();
				
				Node errorMessageNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_MESSAGE);
				String errorMessage;
				if (errorMessageNode == null) {
					errorMessage = "";
				} else {
					errorMessage = errorMessageNode.getText();
				}
				
				switch(ResponseCode.valueOf(errorCode)) {
				case MISSING_PARAMETER:
					throw new InvalidParameterException(errorMessage);
					
				case REGISTRATION_NOT_FOUND:
					throw new RegistrationNotFoundException(errorMessage);
					
				default:
					throw new ClientException(errorMessage);
				}
			}
		} finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.client.RegistrationAPI#findWarrantyByEmail(java.lang.String)
	 */
	@Override
	public WarrantyListResponse findWarrantyByEmail(String email) 
			throws InvalidParameterException, RegistrationNotFoundException, ClientException {

		String method = "findWarrantyByEmail()";
		
		// Validate email param
		if (email == null || email.isEmpty()) {
			String msg = "Missing 'email' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Validate email format
		if (!ValidationUtil.isValidEmailFormat(email)) {
			String msg = "Invalid email format.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}

		// Construct the query string.
		HttpClient httpClient = null;
		try {
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				
				//-------------------------------------------------------
				//	Create the base URL for the Service.
				String baseUrlString = createBaseUrlString();
				
				//-------------------------------------------------------
				//	Added the method-specific part to the URL.
				StringBuilder uriString = new StringBuilder();
				uriString.append(baseUrlString);
				uriString.append(METHOD_FIND_WARRANTY_BY_EMAIL).append("/");
				String encodedEmail = URLEncoder.encode(email, ENCODING_UTF_8);
				uriString.append(encodedEmail).append(".xml");

				URI uri = new URI(uriString.toString());
				
				HttpGet getRequest = createGetMethod(uri);
				httpResponse = send(httpClient, getRequest);
				
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
				
			} catch (UnsupportedEncodingException uee) {
				String msg = "UnsupportedEncodingException thrown";
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSRegistrationService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSRegistrationService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response from server. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response from server. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSRegistrationService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response from server.");
			}			
			
			WarrantyListResponse response = null;
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					// Parse warranty nodes
					List<Node> warranties = document.selectNodes("//*[name()='warranty']");
					List<Warranty> parsedWarranties = new ArrayList<Warranty>();
					for (Node warrrantyNode : warranties) {
						Warranty war = parseWarrantyXML(warrrantyNode);
						parsedWarranties.add(war);
					}
					
					response = new WarrantyListResponse(ResponseCode.SUCCESS, parsedWarranties);
				}
				
			} else { // ERROR
				Node errorCodeNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
				if (errorCodeNode == null) {
					throw new ClientException("Error code was empty.");
				}
				String errorCode = errorCodeNode.getText();
				
				Node errorMessageNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_MESSAGE);
				String errorMessage;
				if (errorMessageNode == null) {
					errorMessage = "";
				} else {
					errorMessage = errorMessageNode.getText();
				}
				
				switch(ResponseCode.valueOf(errorCode)) {
				case MISSING_PARAMETER:
					throw new InvalidParameterException(errorMessage);
					
				case REGISTRATION_NOT_FOUND:
					throw new RegistrationNotFoundException(errorMessage);
					
				default:
					throw new ClientException(errorMessage);
				}
			} 
			
			return response;
			
		} finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.client.RegistrationAPI#findRegistrationHistoryByPenSerial(java.lang.String)
	 */
	@Override
	public RegistrationHistoryListResponse findRegistrationHistoryByPenSerial( String penSerial) 
			throws InvalidParameterException, RegistrationNotFoundException, ClientException {

		String method = "findRegistrationHistoryByPenSerial()";
		
		// Validate penSerial param
		if (penSerial == null || penSerial.isEmpty()) {
			String msg = "Missing 'penSerial' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}

		// Construct the query string.
		HttpClient httpClient = null;
		try {
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				
				//-------------------------------------------------------
				//	Create the base URL for the Service.
				String baseUrlString = createBaseUrlString();
				
				//-------------------------------------------------------
				//	Add the method-specific part to the URL.
				StringBuilder uriString = new StringBuilder();
				uriString.append(baseUrlString);
				uriString.append(METHOD_FIND_REGISTRATION_HISTORY_BY_PEN_SERIAL).append("/");
				String encodedPenSerial = URLEncoder.encode(penSerial, ENCODING_UTF_8);
				uriString.append(encodedPenSerial).append(".xml");

				URI uri = new URI(uriString.toString());
				HttpGet getRequest = createGetMethod(uri);
				
				httpResponse = send(httpClient, getRequest);
				
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
				
			} catch (UnsupportedEncodingException uee) {
				String msg = "UnsupportedEncodingException thrown";
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSRegistrationService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSRegistrationService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response from server. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response from server. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSRegistrationService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response from server.");
			}			
			
			RegistrationHistoryListResponse response = null;
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					// Parse registration history nodes
					List<Node> regHistoryNodes = document.selectNodes("//*[name()='registration']");
					List<RegistrationHistory> parsedRegHistories = new ArrayList<RegistrationHistory>();
					for (Node regHistoryNode : regHistoryNodes) {
						RegistrationHistory regHistory = parseRegistrationHistoryXML(regHistoryNode);
						parsedRegHistories.add(regHistory);
					}
										
					response = new RegistrationHistoryListResponse(ResponseCode.SUCCESS, parsedRegHistories);
				}
				
			} else { // ERROR
				Node errorCodeNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
				if (errorCodeNode == null) {
					throw new ClientException("Error code was empty.");
				}
				String errorCode = errorCodeNode.getText();
				
				Node errorMessageNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_MESSAGE);
				String errorMessage;
				if (errorMessageNode == null) {
					errorMessage = "";
				} else {
					errorMessage = errorMessageNode.getText();
				}
				
				switch(ResponseCode.valueOf(errorCode)) {
				case MISSING_PARAMETER:
					throw new InvalidParameterException(errorMessage);
					
				case REGISTRATION_NOT_FOUND:
					throw new RegistrationNotFoundException(errorMessage);
					
				default:
					throw new ClientException(errorMessage);
				}
			} 
			
			return response;
			
		} finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.client.RegistrationAPI#findRegistrationHistoryByEmail(java.lang.String)
	 */
	@Override
	public RegistrationHistoryListResponse findRegistrationHistoryByEmail(String email) 
			throws InvalidParameterException, RegistrationNotFoundException, ClientException {

		String method = "findRegistrationHistoryByEmail()";
		
		// Validate email param
		if (email == null || email.isEmpty()) {
			String msg = "Missing 'email' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		// Validate email format
		if (!ValidationUtil.isValidEmailFormat(email)) {
			String msg = "Invalid email format.  '" + email + "'";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}

		// Construct the query string.
		HttpClient httpClient = null;
		try {
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				
				//-------------------------------------------------------
				//	Create the base URL for the Service.
				String baseUrlString = createBaseUrlString();
				
				//-------------------------------------------------------
				//	Add the method-specific part to the URL.
				StringBuilder uriString = new StringBuilder();
				uriString.append(baseUrlString);
				uriString.append(METHOD_FIND_REGISTRATION_HISTORY_BY_EMAIL).append("/");
				String encodedEmail = URLEncoder.encode(email, ENCODING_UTF_8);
				uriString.append(encodedEmail).append(".xml");

				logger.debug(method + " - URL-encoded:  " + uriString);
				
				URI uri = new URI(uriString.toString());
				
				HttpGet getRequest = createGetMethod(uri);
				
				httpResponse = send(httpClient, getRequest);
				
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
				
			} catch (UnsupportedEncodingException uee) {
				String msg = "UnsupportedEncodingException thrown";
				logger.debug(method +  msg, uee);
				throw new ClientException(msg, uee);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSRegistrationService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSRegistrationService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response from server. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response from server. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSRegistrationService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response from server.");
			}			
			
			RegistrationHistoryListResponse response = null;
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					// Parse registration history nodes
					List<Node> regHistoryNodes = document.selectNodes("//*[name()='registration']");
					List<RegistrationHistory> parsedRegHistories = new ArrayList<RegistrationHistory>();
					for (Node regHistoryNode : regHistoryNodes) {
						RegistrationHistory regHistory = parseRegistrationHistoryXML(regHistoryNode);
						parsedRegHistories.add(regHistory);
					}
										
					response = new RegistrationHistoryListResponse(ResponseCode.SUCCESS, parsedRegHistories);
				}
				
			} else { // ERROR
				Node errorCodeNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
				if (errorCodeNode == null) {
					throw new ClientException("Error code was empty.");
				}
				String errorCode = errorCodeNode.getText();
				
				Node errorMessageNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_MESSAGE);
				String errorMessage;
				if (errorMessageNode == null) {
					errorMessage = "";
				} else {
					errorMessage = errorMessageNode.getText();
				}
				
				switch(ResponseCode.valueOf(errorCode)) {
				case MISSING_PARAMETER:
					throw new InvalidParameterException(errorMessage);
					
				case REGISTRATION_NOT_FOUND:
					throw new RegistrationNotFoundException(errorMessage);
					
				default:
					throw new ClientException(errorMessage);
				}
			} 
			
			return response;
			
		} finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}


	@Override
	public UserListResponse findUsersByEmail(String email) throws InvalidParameterException, ClientException {

		String method = "findUsersByEmail()";

		// Validate email param
		if (email == null || email.isEmpty()) {
			String msg = "Missing 'email' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}

		// Validate email format
		if (!ValidationUtil.isValidEmailFormat(email)) {
			String msg = "Invalid email format.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		return fetchUserListFromRegistrationService(METHOD_FIND_USERS_BY_EMAIL_EQUALS, email);
	}

	@Override
	public UserListResponse findUsersByPartialEmail(String email) throws InvalidParameterException, ClientException {

		String method = "findUsersByPartialEmail()";
		
		// Validate email param
		if (email == null || email.isEmpty()) {
			String msg = "Missing 'email' parameter.";
			logger.error(method + msg);
			throw new InvalidParameterException(msg);
		}

		return fetchUserListFromRegistrationService(METHOD_FIND_USERS_BY_EMAIL_CONTAINS, email);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param registrationNode
	 * @return
	 */
	private Registration parseRegistrationXML(Node registrationNode) {
		Registration registration = new Registration();
		
		registration.setAppId(ObjectFactory.selectSingleNodeText(registrationNode, ObjectFactory.NODE_REGISTRATION_APP_ID));
		registration.setEdition(ObjectFactory.selectSingleNodeInt(registrationNode, ObjectFactory.NODE_REGISTRATION_EDITION));
		registration.setPenSerial(ObjectFactory.selectSingleNodeText(registrationNode, ObjectFactory.NODE_REGISTRATION_PEN_SERIAL));
		registration.setDisplayId(ObjectFactory.selectSingleNodeText(registrationNode, ObjectFactory.NODE_REGISTRATION_DISPLAY_ID));
		registration.setPenName(ObjectFactory.selectSingleNodeText(registrationNode, ObjectFactory.NODE_REGISTRATION_PEN_NAME));
		registration.setFirstName(ObjectFactory.selectSingleNodeText(registrationNode, ObjectFactory.NODE_REGISTRATION_FIRST_NAME));
		registration.setLastName(ObjectFactory.selectSingleNodeText(registrationNode, ObjectFactory.NODE_REGISTRATION_LAST_NAME));
		registration.setEmail(ObjectFactory.selectSingleNodeText(registrationNode, ObjectFactory.NODE_REGISTRATION_EMAIL));
		registration.setLocale(ObjectFactory.selectSingleNodeText(registrationNode, ObjectFactory.NODE_REGISTRATION_LOCALE));
		registration.setCountry(ObjectFactory.selectSingleNodeText(registrationNode, ObjectFactory.NODE_REGISTRATION_COUNTRY));
		registration.setOptIn(ObjectFactory.selectSingleNodeBoolean(registrationNode, ObjectFactory.NODE_REGISTRATION_OPT_IN));
		registration.setCreated(ObjectFactory.selectSingleNodeDate(registrationNode, ObjectFactory.NODE_REGISTRATION_CREATED_DATE));
		
		return registration;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param warrantyNode
	 * @return
	 */
	private Warranty parseWarrantyXML(Node warrantyNode) {
		Warranty warranty = new Warranty();
		
		warranty.setAppId(ObjectFactory.selectSingleNodeText(warrantyNode, ObjectFactory.NODE_REGISTRATION_APP_ID));
		warranty.setEdition(ObjectFactory.selectSingleNodeInt(warrantyNode, ObjectFactory.NODE_REGISTRATION_EDITION));
		warranty.setPenSerial(ObjectFactory.selectSingleNodeText(warrantyNode, ObjectFactory.NODE_REGISTRATION_PEN_SERIAL));
		warranty.setDisplayId(ObjectFactory.selectSingleNodeText(warrantyNode, ObjectFactory.NODE_REGISTRATION_DISPLAY_ID));
		warranty.setPenName(ObjectFactory.selectSingleNodeText(warrantyNode, ObjectFactory.NODE_REGISTRATION_PEN_NAME));
		warranty.setFirstName(ObjectFactory.selectSingleNodeText(warrantyNode, ObjectFactory.NODE_REGISTRATION_FIRST_NAME));
		warranty.setLastName(ObjectFactory.selectSingleNodeText(warrantyNode, ObjectFactory.NODE_REGISTRATION_LAST_NAME));
		warranty.setEmail(ObjectFactory.selectSingleNodeText(warrantyNode, ObjectFactory.NODE_REGISTRATION_EMAIL));
		warranty.setLocale(ObjectFactory.selectSingleNodeText(warrantyNode, ObjectFactory.NODE_REGISTRATION_LOCALE));
		warranty.setCreated(ObjectFactory.selectSingleNodeDate(warrantyNode, ObjectFactory.NODE_REGISTRATION_CREATED_DATE));
		
		return warranty;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param regHistoryNode
	 * @return
	 */
	private RegistrationHistory parseRegistrationHistoryXML(Node regHistoryNode) {
		RegistrationHistory regHistory = new RegistrationHistory();
		
		regHistory.setAppId(ObjectFactory.selectSingleNodeText(regHistoryNode, ObjectFactory.NODE_REGISTRATION_APP_ID));
		regHistory.setEdition(ObjectFactory.selectSingleNodeInt(regHistoryNode, ObjectFactory.NODE_REGISTRATION_EDITION));
		regHistory.setPenSerial(ObjectFactory.selectSingleNodeText(regHistoryNode, ObjectFactory.NODE_REGISTRATION_PEN_SERIAL));
		regHistory.setDisplayId(ObjectFactory.selectSingleNodeText(regHistoryNode, ObjectFactory.NODE_REGISTRATION_DISPLAY_ID));
		regHistory.setPenName(ObjectFactory.selectSingleNodeText(regHistoryNode, ObjectFactory.NODE_REGISTRATION_PEN_NAME));
		regHistory.setFirstName(ObjectFactory.selectSingleNodeText(regHistoryNode, ObjectFactory.NODE_REGISTRATION_FIRST_NAME));
		regHistory.setLastName(ObjectFactory.selectSingleNodeText(regHistoryNode, ObjectFactory.NODE_REGISTRATION_LAST_NAME));
		regHistory.setEmail(ObjectFactory.selectSingleNodeText(regHistoryNode, ObjectFactory.NODE_REGISTRATION_EMAIL));
		regHistory.setLocale(ObjectFactory.selectSingleNodeText(regHistoryNode, ObjectFactory.NODE_REGISTRATION_LOCALE));
		regHistory.setCountry(ObjectFactory.selectSingleNodeText(regHistoryNode, ObjectFactory.NODE_REGISTRATION_COUNTRY));
		regHistory.setOptIn(ObjectFactory.selectSingleNodeBoolean(regHistoryNode, ObjectFactory.NODE_REGISTRATION_OPT_IN));
		regHistory.setRegistrationDate(ObjectFactory.selectSingleNodeDate(regHistoryNode, ObjectFactory.NODE_REGISTRATION_DATE));
		
		return regHistory;
	}
	

	private RegistrationListResponse fetchRegListFromRegistrationService(String findRegistrationMethod, String inputParameter) throws ClientException, InvalidParameterException, RegistrationNotFoundException {
		
		final String method = "fetchRegListFromRegistrationService()	";		
		logger.debug(method + "called for method " + findRegistrationMethod + " and input " + inputParameter);

		// Construct the query string.
		HttpClient httpClient = null;
		try {
			HttpResponse httpResponse;
			try {
				httpClient = new DefaultHttpClient();
				
				//-------------------------------------------------------
				//	Create the base URL for the Service.
				String baseUrlString = createBaseUrlString();
				
				//-------------------------------------------------------
				//	Added the method-specific part to the URL.
				StringBuilder uriString = new StringBuilder();
				uriString.append(baseUrlString);
				uriString.append(findRegistrationMethod).append("/");
				String encodedParameter = URLEncoder.encode(inputParameter, ENCODING_UTF_8);
				uriString.append(encodedParameter).append(".xml");
				
				URI uri = new URI(uriString.toString());
				HttpGet getRequest = createGetMethod(uri);
				
				httpResponse = send(httpClient, getRequest);
				
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
				
			} catch (UnsupportedEncodingException uee) {
				String msg = "UnsupportedEncodingException thrown when attempting to encode 'penSerial' parameter.  [" + inputParameter + "]";
				logger.debug(method +  msg, uee);
				throw new ClientException(msg, uee);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSRegistrationService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSRegistrationService. HTTP Status: " + statusCode);
			}
			
			HttpEntity entity = httpResponse.getEntity();

			String responseXml = readEntityIntoString(entity);
			
			if ((responseXml == null) || (responseXml.isEmpty())) {
				String msg = "Response XML was either 'null' or empty.";
				logger.error(method + " - " + msg);
				throw new ClientException(msg);
			}
			
			//--------------------------------------------------
			//	If this is a SUCCESS response ...
			if (responseXml.contains(NODE_RESPONSE_CODE)) {
				
				//	... parse XML into a WarrantyResponse object.
				XStream xStream = new XStream(new DomDriver());
				xStream.alias("response", RegistrationListResponse.class);
				xStream.alias("registrations", List.class);
				xStream.alias("registration", RegistrationDTO.class);
				RegistrationListResponse response = null;
				try {
					response = (RegistrationListResponse)xStream.fromXML(responseXml);
				} catch (XStreamException xse) {
					logger.debug("Caught XStreamException --> ", xse);
					return null;
				}
				return response;
			}
			else if (responseXml.contains(NODE_ERROR_CODE)) {
				
				//	...otherwise, parse into ErrorResponse object.
				ErrorResponse errResponse = parseErrorResponse(responseXml);
				if (errResponse == null) {
					String msg = "Error response from Service was either 'null' or empty.";
					throw new ClientException(msg);
				}
				
				ResponseCode code = errResponse.getResponseCode();
				String errorMessage = errResponse.getMessage();
				
				switch (code) {
				case MISSING_PARAMETER:
					throw new InvalidParameterException(errorMessage);
				case REGISTRATION_NOT_FOUND:
					throw new RegistrationNotFoundException(errorMessage);
				default:
					throw new ClientException(errorMessage);
				}
			}
			else {
				//	Unknown response received.
				String msg = "Unknown response received from Service.";
				logger.error(method + " - " + msg);
				throw new ClientException(msg);
			}
		} finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}


	private UserListResponse fetchUserListFromRegistrationService(String findUsersMethodName, String inputParameter) throws ClientException, InvalidParameterException {
	
		final String method = "fetchUserListFromRegistrationService()	";		
		logger.debug(method + "called for method " + findUsersMethodName + " and input " + inputParameter);
	
		// Construct the query string.
		HttpClient httpClient = null;
		try {
			HttpResponse httpResponse;
			try {
				httpClient = new DefaultHttpClient();
				
				//-------------------------------------------------------
				//	Create the base URL for the Service.
				String baseUrlString = createBaseUrlString();
				
				//-------------------------------------------------------
				//	Added the method-specific part to the URL.
				StringBuilder uriString = new StringBuilder();
				uriString.append(baseUrlString);
				uriString.append(findUsersMethodName).append("/");
				String encodedParameter = URLEncoder.encode(inputParameter, ENCODING_UTF_8);
				uriString.append(encodedParameter).append(".xml");
				
				URI uri = new URI(uriString.toString());
				HttpGet getRequest = createGetMethod(uri);
				
				httpResponse = send(httpClient, getRequest);
				
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
				
			} catch (UnsupportedEncodingException uee) {
				String msg = "UnsupportedEncodingException thrown when attempting to encode parameter.  [" + inputParameter + "]";
				logger.debug(method +  msg, uee);
				throw new ClientException(msg, uee);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSRegistrationService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSRegistrationService. HTTP Status: " + statusCode);
			}
			
			HttpEntity entity = httpResponse.getEntity();
	
			String responseXml = readEntityIntoString(entity);
			
			if ((responseXml == null) || (responseXml.isEmpty())) {
				String msg = "Response XML was either 'null' or empty.";
				logger.error(method + " - " + msg);
				throw new ClientException(msg);
			}
			
			//--------------------------------------------------
			//	If this is a SUCCESS response ...
			if (responseXml.contains(NODE_RESPONSE_CODE)) {
				
				//	... parse XML into a WarrantyResponse object.
				XStream xStream = new XStream(new DomDriver());
				xStream.alias("response", UserListResponse.class);
				xStream.alias("userDtoList", List.class);
				xStream.alias("userDto", UserDto.class);
				UserListResponse response = null;
				try {
					response = (UserListResponse)xStream.fromXML(responseXml);
				} catch (XStreamException xse) {
					logger.debug("Caught XStreamException --> ", xse);
					return null;
				}
				return response;
			}
			else if (responseXml.contains(NODE_ERROR_CODE)) {
				
				//	...otherwise, parse into ErrorResponse object.
				ErrorResponse errResponse = parseErrorResponse(responseXml);
				if (errResponse == null) {
					String msg = "Error response from Service was either 'null' or empty.";
					throw new ClientException(msg);
				}
				
				ResponseCode code = errResponse.getResponseCode();
				String errorMessage = errResponse.getMessage();
				
				switch (code) {
				case MISSING_PARAMETER:
					throw new InvalidParameterException(errorMessage);
				default:
					throw new ClientException(errorMessage);
				}
			}
			else {
				//	Unknown response received.
				String msg = "Unknown response received from Service.";
				logger.error(method + " - " + msg);
				throw new ClientException(msg);
			}
		} finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}
}

/**
 * 
 */
package com.livescribe.aws.login.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import com.livescribe.aws.login.dto.AuthorizationDto;
import com.livescribe.aws.login.response.AuthorizationListResponse;
import com.livescribe.aws.login.response.AuthorizationResponse;
import com.livescribe.aws.login.response.UserInfoResponse;
import com.livescribe.aws.login.util.AuthorizationType;
import com.livescribe.framework.client.AbstractClient;
import com.livescribe.framework.config.AppConstants;
import com.livescribe.framework.exception.AuthorizationNotFoundException;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.DuplicateEmailAddressException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.LocaleException;
import com.livescribe.framework.exception.LogoutException;
import com.livescribe.framework.exception.ServerException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.login.exception.AuthorizationException;
import com.livescribe.framework.login.exception.EmailAlreadyTakenException;
import com.livescribe.framework.login.exception.IncorrectPasswordException;
import com.livescribe.framework.login.exception.InsufficientPrivilegeException;
import com.livescribe.framework.login.exception.LoginException;
import com.livescribe.framework.login.exception.RegistrationNotFoundException;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.login.response.LoginResponse;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class LoginClient extends AbstractClient implements LoginAPI {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	protected static final String urlString		= "";
	private static final String CHARSET			= "UTF-8";
	private static final String MIME_TYPE_XML	= "application/xml";
	private static final String MIME_TYPE_FORM	= "application/x-www-form-urlencoded";
	private static final String PROTOCOL		= "https";
	private static final String DATE_FORMAT		= "yyyy-MM-dd HH:mm:ss";
	
	//	TODO:  Need to make these configurable.
	private static final String KEY_HOST		= "host";
	private static final String KEY_PORT		= "port";
	private static final String KEY_CONTEXT		= "context";
	private static final String DEFAULT_LOCALE	= "en-US";
	private static final String DEFAULT_SEND_DIAGNOSTICS = "false";
	
	private static final String SUCCESS		= "SUCCESS";
	
	private	static	final	String	METHOD_FIND_AUTH_BY_PENDISPLAYID				=	"findAuthorizationByPenDisplayId";
	private	static	final	String	METHOD_FIND_AUTH_BY_UID							=	"findAuthorizationByUID";
	private	static	final	String	METHOD_FIND_AUTH_BY_UID_AND_PROVIDER_USER_ID	=	"findAuthorizationByUIDAndProviderUserId";
	private	static	final	String	METHOD_FIND_AUTH_BY_PROVIDER_USER_ID			=	"findAuthorizationByProviderUserId";
//	private ClientProperties properties;
    private String jsessionCookie;
	
	/**
	 * <p>Default class constructor.</p>
	 * 
	 * Loads properties from the <code>loginclient.properties</code> file.
	 * 
	 * @throws IOException if the <code>loginclient.properties</code> file could not be read.
	 * @throws ClientException if the <code>ENV</code> environment variable is not set.
	 */
	public LoginClient() throws IOException {
		super("loginclient.properties");
//		InputStream in = this.getClass().getClassLoader().getResourceAsStream("loginclient.properties");
//		properties = new ClientProperties();
//		properties.load(in);
//		in.close();
//		logger.debug("Read in " + this.properties.size() + " properties.");
	}

	/**
	 * <p></p>
	 * 
	 * @param clientProperties
	 * @throws IOException 
	 */
	public LoginClient(String propertiesFilename) throws IOException {
		super(propertiesFilename);
//		InputStream in = this.getClass().getClassLoader().getResourceAsStream(propertiesFilename);
//		properties = new ClientProperties();
//		properties.load(in);
//		in.close();
//		logger.debug("Read in " + this.properties.size() + " properties from '" + propertiesFilename + "'.");
	}

	/**
	 * <p>Constructor that takes in an InputStream</p>
	 * 
	 * @param is The InputStream of a specific property file to load
	 * @throws IOException if client properties could not be loaded.
	 */
	public LoginClient(InputStream is) throws IOException {
		super(is);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.livescribe.aws.login.client.LoginAPI#createWifiAccount(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public LoginResponse createWifiAccount(	String email, String password, String locale, String occupation, 
											String caller, String loginToken, String optin, String sendDiagnostics) 
					throws InvalidParameterException, EmailAlreadyTakenException, LocaleException, 
					LoginException, DuplicateEmailAddressException, UserNotFoundException, ClientException {
		
		String method = "createAccount():  ";
		String URL_FILENAME = "newWifiAccount";
		LoginResponse response = new LoginResponse();
		
		//	Validate given parameters.
		if ((email == null) || ("".equals(email))) {
			String msg = "The 'email' parameter is required.";
			throw new InvalidParameterException(msg);
		}
		if ((password == null) || ("".equals(password))) {
			String msg = "The 'password' parameter is required.";
			throw new InvalidParameterException(msg);
		}
		if ((occupation == null) || ("".equals(occupation))) {
			String msg = "The 'occupation' parameter is required.";
			throw new InvalidParameterException(msg);
		}
		if ((caller == null) || ("".equals(caller))) {
			String msg = "The 'caller' parameter is required.";
			throw new InvalidParameterException(msg);
		}
		if ((loginToken == null) || ("".equals(loginToken))) {
			String msg = "The 'loginToken' parameter is required";
			throw new InvalidParameterException(msg);
		}
		if ((optin == null) || ("".equals(optin))) {
			String msg = "The 'optIn' parameter is required.";
			throw new InvalidParameterException(msg);
		}
		//	NOT required.  If missing, use a default.
		if ((locale == null) || ("".equals(locale))) {
			locale = DEFAULT_LOCALE;
		}
		
		// NOT required. If missing, use false as default
		if (sendDiagnostics == null || "".equals(sendDiagnostics)) {
			sendDiagnostics = DEFAULT_SEND_DIAGNOSTICS;
		}

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("locale", locale));
		params.add(new BasicNameValuePair("occupation", occupation));
		params.add(new BasicNameValuePair("loginDomain", caller));
		params.add(new BasicNameValuePair("loginToken", loginToken));
		params.add(new BasicNameValuePair("optIn", optin));
		params.add(new BasicNameValuePair("sendDiagnostics", sendDiagnostics));

		DefaultHttpClient httpClient = null;
		try {
			HttpResponse httpResponse;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send(URL_FILENAME, params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (KeyManagementException kme) {
				String msg = "There was an error involving the SSL key.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available in the current environment.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
	
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response.");
			}
			
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					response.setResponseCode(ResponseCode.SUCCESS);
					response.setLoginToken(document.selectSingleNode(ObjectFactory.NODE_LOGIN_TOKEN).getText());
					response.setUid(document.selectSingleNode(ObjectFactory.NODE_UID).getText());
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
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					case INVALID_EMAIL:
						throw new InvalidParameterException(errorMessage);
						
					case EMAIL_ALREADY_IN_USE:
						throw new EmailAlreadyTakenException(errorMessage);
						
					case INVALID_LOCALE:
						throw new LocaleException(errorMessage);
						
					case SERVER_ERROR:
						throw new ClientException(errorMessage);
						
					case UNABLE_TO_LOG_USER_IN:
						throw new LoginException(errorMessage);
						
					case DUPLICATE_EMAIL_ADDRESS_FOUND:
						throw new DuplicateEmailAddressException(errorMessage);
						
					case USER_NOT_FOUND:
						throw new UserNotFoundException(errorMessage);
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			return response;
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}		
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.login.client.LoginAPI#createUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String createUser(String email, String password, String locale, String occupation, String caller, String optin, String sendDiagnostics) 
			throws InvalidParameterException, IOException, ClientException, EmailAlreadyTakenException {
		
		String method = "createUser():  ";
		String URL_FILENAME = "newuser";

		String uid = null;
		
		//	Validate given parameters.
		if ((email == null) || ("".equals(email))) {
			String msg = "The 'email' parameter is required.";
			throw new InvalidParameterException(msg);
		}
		if ((password == null) || ("".equals(password))) {
			String msg = "The 'password' parameter is required.";
			throw new InvalidParameterException(msg);
		}
		if ((occupation == null) || ("".equals(occupation))) {
			String msg = "The 'occupation' parameter is required.";
			throw new InvalidParameterException(msg);
		}

		//	Default the login domain to "WEB".
		if ((caller == null) || ("".equals(caller))) {
//			String msg = "The 'caller' parameter is required.";
//			throw new InvalidParameterException(msg);
			caller = "WEB";
		}
		else if ((!"WEB".equals(caller)) && (!"EN".equals(caller)) && (!"ML".equals(caller)) && (!"TEST".equals(caller)) && (!"LD".equals(caller))) {
			caller = "WEB";
		}
		if ((optin == null) || ("".equals(optin))) {
			String msg = "The 'optIn' parameter is required.";
			throw new InvalidParameterException(msg);
		}
		//	NOT required.  If missing, use a default.
		if ((locale == null) || ("".equals(locale))) {
			locale = DEFAULT_LOCALE;
		}
		
		// NOT required. If missing, use false as default
		if (sendDiagnostics == null || "".equals(sendDiagnostics)) {
			sendDiagnostics = DEFAULT_SEND_DIAGNOSTICS;
		}
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("locale", locale));
		params.add(new BasicNameValuePair("occupation", occupation));
		params.add(new BasicNameValuePair("loginDomain", caller));
		params.add(new BasicNameValuePair("optIn", optin));
		params.add(new BasicNameValuePair("sendDiagnostics", sendDiagnostics));
		
		DefaultHttpClient httpClient = null;
		try {
			HttpResponse httpResponse;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send(URL_FILENAME, params, httpClient, null);
			}
			catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			}
			catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			}
			catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			}
			catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse != null) {
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					
					Document document = null;
					try {
						//	Parse the response into an XML Document.
						document = parseResponse(httpResponse);
						
						if (document != null) {
	//						printDocument(document);
							
							Node codeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
							if (codeNode != null) {
								
								//	Parse the XML into a User object.
								Object obj = ObjectFactory.parseDocument(document);
								if (obj != null) {
									if (obj instanceof String) {
										uid = (String)obj;
									}
									else {
										logger.debug("Parsed object was of type '" + obj.getClass().getName() + "'.");
									}
								} else {
									logger.debug("Parsed object was 'null'.");
								}
							} else {
								Node errorNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
								if (errorNode != null) {
									logger.debug(errorNode.getText() + "'" + email + "'");
									throw new EmailAlreadyTakenException();
								}
							}
						} else {
							//	TODO:  Handle 'null' documents here.
						}
					} catch (IllegalStateException ise) {
						ise.printStackTrace();
					} catch (DocumentException de) {
						de.printStackTrace();
						String msg = "The response from the server could not be understood.";
						logger.debug(method +  msg, de);
	//					throw new LoginException(msg, de);
					}
				} else {
					//	TODO:  Need to add error handling here.
					logger.debug("HTTP Status:  " + statusCode);
					return null;
				}
			}
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}

		return uid;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.livescribe.aws.login.client.LoginAPI#createWifiUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public LoginResponse createAccount  (String email, 
										String password, 
										String locale, 
										String occupation, 
										String caller, 
										String optin, 
										String sendDiagnostics)
										throws InvalidParameterException, EmailAlreadyTakenException, LocaleException, 
										LoginException, DuplicateEmailAddressException, UserNotFoundException, ClientException {
		
		String method = "createAccount():  ";
		String URL_FILENAME = "new";
		LoginResponse response = new LoginResponse();
		
		//	Validate given parameters.
		if ((email == null) || ("".equals(email))) {
			String msg = "The 'email' parameter is required.";
			throw new InvalidParameterException(msg);
		}
		if ((password == null) || ("".equals(password))) {
			String msg = "The 'password' parameter is required.";
			throw new InvalidParameterException(msg);
		}
//		if ((occupation == null) || ("".equals(occupation))) {
//			String msg = "The 'occupation' parameter is required.";
//			throw new InvalidParameterException(msg);
//		}
		if ((optin == null) || ("".equals(optin))) {
			String msg = "The 'optIn' parameter is required.";
			throw new InvalidParameterException(msg);
		}
		
		//	Default the login domain to "WEB".
		if ((caller == null) || ("".equals(caller))) {
			caller = "WEB";
		} else if ((!"WEB".equals(caller)) && (!"EN".equals(caller)) && (!"ML".equals(caller)) 
				&& (!"TEST".equals(caller)) && (!"LD".equals(caller)) && (!"LSDS".equals(caller))
				&& !caller.matches(AppConstants.DESKTOP_VERSION_REGEX)) {
			caller = "WEB";
		}
		
		//	NOT required.  If missing, use a default.
		if ((locale == null) || ("".equals(locale))) {
			locale = DEFAULT_LOCALE;
		}
		
		// NOT required. If missing, use false as default
		if (sendDiagnostics == null || "".equals(sendDiagnostics)) {
			sendDiagnostics = DEFAULT_SEND_DIAGNOSTICS;
		}
		
		// Prepare parameter list
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("locale", locale));
		params.add(new BasicNameValuePair("occupation", occupation));
		params.add(new BasicNameValuePair("loginDomain", caller));
		params.add(new BasicNameValuePair("optIn", optin));
		params.add(new BasicNameValuePair("sendDiagnostics", sendDiagnostics));
		
		DefaultHttpClient httpClient = null;
		try {
			//	Make http request to LSLoginService
			HttpResponse httpResponse;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send(URL_FILENAME, params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (KeyManagementException kme) {
				String msg = "There was an error involving the SSL key.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available in the current environment.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response.");
			}
			
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					response.setResponseCode(ResponseCode.SUCCESS);
					response.setLoginToken(document.selectSingleNode(ObjectFactory.NODE_LOGIN_TOKEN).getText());
					response.setUid(document.selectSingleNode(ObjectFactory.NODE_UID).getText());
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
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					case INVALID_EMAIL:
						throw new InvalidParameterException(errorMessage);
						
					case EMAIL_ALREADY_IN_USE:
						throw new EmailAlreadyTakenException(errorMessage);
						
					case INVALID_LOCALE:
						throw new LocaleException(errorMessage);
						
					case SERVER_ERROR:
						throw new ClientException(errorMessage);
						
					case UNABLE_TO_LOG_USER_IN:
						throw new LoginException(errorMessage);
						
					case DUPLICATE_EMAIL_ADDRESS_FOUND:
						throw new DuplicateEmailAddressException(errorMessage);
						
					case USER_NOT_FOUND:
						throw new UserNotFoundException(errorMessage);
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			
			return response;
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.login.client.LoginAPI#isLoggedIn(java.lang.String)
	 */
	public boolean isLoggedIn(String loginToken, String caller) throws InvalidParameterException, IOException, ServerException, ClientException {
		
		String method = "isLoggedIn():  ";
		
		if ((loginToken == null) || ("".equals(loginToken))) {
			String msg = "A required parameter was missing.";
			throw new InvalidParameterException(msg);
		}
		if ((caller == null) || ("".equals(caller))) {
			String msg = "A required parameter was missing.";
			throw new InvalidParameterException(msg);
		}

		//	Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tk", loginToken));
		params.add(new BasicNameValuePair("loginDomain", caller));
		
		DefaultHttpClient httpClient = null;
		try {
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("isloggedin", params, httpClient, loginToken);
			}
			catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			}
			catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			}
			catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			}
			catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse != null) {
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					
					Document document = null;
					try {
						//	Parse the response into an XML Document.
						document = parseResponse(httpResponse);
						
						if (document != null) {
	//						printDocument(document);
							
							Node codeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
							if (codeNode != null) {
								String code = codeNode.getText();
								if (SUCCESS.equals(code)) {
									return true;
								}
							}
							else {
								Node errorNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
								if (errorNode != null) {
									String msg = "An error occurred on the server.";
									logger.error(msg + " " + errorNode.getText());
									throw new ServerException(msg);
								}
								else {
									String msg = "An unknown error occurred on the server.";
									logger.error(msg + " " + errorNode.getText());
									throw new ServerException(msg);
								}
							}
						}
						else {
							//	TODO:  Handle 'null' documents here.
						}
					}
					catch (IllegalStateException ise) {
						ise.printStackTrace();
					}
					catch (DocumentException de) {
						de.printStackTrace();
						String msg = "The response from the server could not be understood.";
						logger.debug(method +  msg, de);
	//					throw new LoginException(msg, de);
					}
				}
				else {
					//	TODO:  Need to add error handling here.
					logger.debug("HTTP Status:  " + statusCode);
					return false;
				}
			}
			return false;
		}
		finally {
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
	 * @see com.livescribe.aws.login.client.LoginAPI#loginWifiUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String loginWifiUser(String email, String password, String caller, String loginToken) 
			throws IncorrectPasswordException, IOException, InvalidParameterException, LoginException, ClientException, UserNotFoundException {
		
		String method = "loginWifiUser():  ";
		
		//	Validate given parameters.
		if ((email == null) || ("".equals(email))) {
			String msg = "A required parameters was missing.";
			throw new InvalidParameterException(msg);
		}
		if ((password == null) || ("".equals(password))) {
			String msg = "A required parameters was missing.";
			throw new InvalidParameterException(msg);
		}
		if ((caller == null) || ("".equals(caller))) {
			String msg = "A required parameters was missing.";
			throw new InvalidParameterException(msg);
		}
//		if ((loginToken == null) || ("".equals(loginToken))) {
//			String msg = "The 'loginToken' parameter is required";
//			throw new InvalidParameterException(msg);
//		}
		
		//	Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("loginDomain", caller));
		params.add(new BasicNameValuePair("loginToken", loginToken));

		DefaultHttpClient httpClient = null;
		try {
			
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("loginWifiUser", params, httpClient, null);
			}
			catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			}
			catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			}
			catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			}
			catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			String returnedLoginToken = null;
			
			if (httpResponse != null) {
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					
					Document document = null;
					try {
						//	Parse the response into an XML Document.
						document = parseResponse(httpResponse);
						
						if (document != null) {					
							// success response
							Node codeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
							if (codeNode != null) {
								
								//	Parse the XML into a User object.
								Object obj = ObjectFactory.parseDocumentAndFindNode(document, ObjectFactory.NODE_LOGIN_TOKEN);
								if (obj != null) {
									if (obj instanceof String) {
										returnedLoginToken = (String)obj;
										return returnedLoginToken;
									}
									else {
										logger.debug("Parsed object was of type '" + obj.getClass().getName() + "'.");
									}
								}
								else {
									logger.debug("Parsed object was 'null'.");
								}
							} else {
								// Handling error response
								Node errorNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
								if (errorNode != null) {
									String nodeText = errorNode.getText();
	
									if (ResponseCode.INCORRECT_PASSWORD.name().equals(nodeText)) {
										throw new IncorrectPasswordException(nodeText);
										
									} else if (ResponseCode.USER_NOT_FOUND.name().equals(nodeText)) {
										throw new UserNotFoundException(nodeText);
										
									} else if (ResponseCode.INVALID_PARAMETER.name().equals(nodeText)) {
										throw new InvalidParameterException(nodeText);
										
									} else {
										throw new LoginException(nodeText);
										
									}
								}
							}
						}
						else {
							//	TODO:  Handle 'null' documents here.
						}
					}
					catch (IllegalStateException ise) {
						ise.printStackTrace();
					}
					catch (DocumentException de) {
						de.printStackTrace();
						String msg = "The response from the server could not be understood.";
						logger.debug(method +  msg, de);
	//					throw new LoginException(msg, de);
					}
				}
				else {
					//	TODO:  Need to add error handling here.
					logger.debug("HTTP Status:  " + statusCode);
					return null;
				}
			}
			return null;
		}
		finally {
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
	 * @see com.livescribe.aws.login.client.LoginAPI#loginWifiUserWithLoginToken(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public LoginResponse loginWifiUserWithLoginToken(String email, String password, String loginDomain, String loginToken) 
			throws IOException, InvalidParameterException, LoginException, ClientException, UserNotFoundException {
		logger.debug("Entering LoginClient.loginWifiUserWithLoginToken()");
		
		LoginResponse response = new LoginResponse();
		
		String method = "login():  ";
		
		//	Validate given parameters.
		if ((email == null) || ("".equals(email))) {
			String msg = "A required parameters was missing.";
			throw new InvalidParameterException(msg);
		}
		if ((password == null) || ("".equals(password))) {
			String msg = "A required parameters was missing.";
			throw new InvalidParameterException(msg);
		}
		if ((loginDomain == null) || ("".equals(loginDomain))) {
			String msg = "A required parameters was missing.";
			throw new InvalidParameterException(msg);
		}
		
		// Default the login domain to "WEB".
		if ((loginDomain == null) || ("".equals(loginDomain))) {
			loginDomain = "WEB";
		} else if ((!"WEB".equals(loginDomain)) && (!"EN".equals(loginDomain)) && (!"ML".equals(loginDomain)) 
				&& (!"TEST".equals(loginDomain)) && (!"LD".equals(loginDomain)) && (!"LSDS".equals(loginDomain))
				&& !loginDomain.matches(AppConstants.DESKTOP_VERSION_REGEX)) {
			loginDomain = "WEB";
		}
		
		//	Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("loginDomain", loginDomain));
		params.add(new BasicNameValuePair("loginToken", loginToken));

		DefaultHttpClient httpClient = null;
		try {
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("loginWifiUser", params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.debug(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (KeyManagementException kme) {
				String msg = "There was an error involving the SSL key.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available in the current environment.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response.");
			}
			
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				logger.debug(method + "responseCode = " + responseCodeNode.getText());
				if (SUCCESS.equals(responseCodeNode.getText())) {
					response.setResponseCode(ResponseCode.SUCCESS);
					response.setLoginToken(document.selectSingleNode(ObjectFactory.NODE_LOGIN_TOKEN).getText());
					response.setUid(document.selectSingleNode(ObjectFactory.NODE_UID).getText());
				}
				
			} else { // ERROR
				Node errorCodeNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
				if (errorCodeNode == null) {
					throw new ClientException("Error code was empty.");
				}
				String errorCode = errorCodeNode.getText();
				logger.debug(method + "errorCode = " + errorCode);
				
				Node errorMessageNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_MESSAGE);
				String errorMessage;
				if (errorMessageNode == null) {
					errorMessage = "";
				} else {
					errorMessage = errorMessageNode.getText();
				}
				
				switch(ResponseCode.valueOf(errorCode)) {
					case INCORRECT_PASSWORD:
						throw new IncorrectPasswordException(errorMessage);
						
					case UNABLE_TO_LOG_USER_IN:
						throw new LoginException(errorMessage);
												
					case USER_NOT_FOUND:
						throw new UserNotFoundException(errorMessage);
						
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
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
	 * @see com.livescribe.aws.login.client.LoginAPI#loginWifiUser(java.lang.String, java.lang.String, java.lang.String)
	 */
	public LoginResponse login(String email, String password, String loginDomain) 
			throws IncorrectPasswordException, LoginException, DuplicateEmailAddressException, InvalidParameterException, UserNotFoundException, ClientException {
		logger.debug("Entering LoginClient.login()");
		
		LoginResponse response = new LoginResponse();
		
		String method = "login():  ";
		
		//	Validate given parameters.
		if ((email == null) || ("".equals(email))) {
			String msg = "A required parameters was missing.";
			throw new InvalidParameterException(msg);
		}
		if ((password == null) || ("".equals(password))) {
			String msg = "A required parameters was missing.";
			throw new InvalidParameterException(msg);
		}
		if ((loginDomain == null) || ("".equals(loginDomain))) {
			String msg = "A required parameters was missing.";
			throw new InvalidParameterException(msg);
		}
		
		// Default the login domain to "WEB".
		if ((loginDomain == null) || ("".equals(loginDomain))) {
			loginDomain = "WEB";
		} else if ((!"WEB".equals(loginDomain)) && (!"EN".equals(loginDomain)) && (!"ML".equals(loginDomain)) 
				&& (!"TEST".equals(loginDomain)) && (!"LD".equals(loginDomain)) && (!"LSDS".equals(loginDomain))
				&& !loginDomain.matches(AppConstants.DESKTOP_VERSION_REGEX)) {
			loginDomain = "WEB";
		}
		
		//	Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("loginDomain", loginDomain));

		DefaultHttpClient httpClient = null;
		try {
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("login", params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.debug(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (KeyManagementException kme) {
				String msg = "There was an error involving the SSL key.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available in the current environment.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response.");
			}
			
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				logger.debug(method + "responseCode = " + responseCodeNode.getText());
				if (SUCCESS.equals(responseCodeNode.getText())) {
					response.setResponseCode(ResponseCode.SUCCESS);
					response.setLoginToken(document.selectSingleNode(ObjectFactory.NODE_LOGIN_TOKEN).getText());
					response.setUid(document.selectSingleNode(ObjectFactory.NODE_UID).getText());
				}
				
			} else { // ERROR
				Node errorCodeNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
				if (errorCodeNode == null) {
					throw new ClientException("Error code was empty.");
				}
				String errorCode = errorCodeNode.getText();
				logger.debug(method + "errorCode = " + errorCode);
				
				Node errorMessageNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_MESSAGE);
				String errorMessage;
				if (errorMessageNode == null) {
					errorMessage = "";
				} else {
					errorMessage = errorMessageNode.getText();
				}
				
				switch(ResponseCode.valueOf(errorCode)) {
					case INCORRECT_PASSWORD:
						throw new IncorrectPasswordException(errorMessage);
						
					case UNABLE_TO_LOG_USER_IN:
						throw new LoginException(errorMessage);
						
					case DUPLICATE_EMAIL_ADDRESS_FOUND:
						throw new DuplicateEmailAddressException(errorMessage);
						
					case USER_NOT_FOUND:
						throw new UserNotFoundException(errorMessage);
						
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			
			return response;
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.login.client.LoginAPI#logout(java.lang.String, java.lang.String)
	 */
	public void logout(String loginToken, String caller) throws InvalidParameterException, LogoutException, IOException, ClientException {
		
		String method = "logout():  ";
		
		//	Validate given parameters.
		if ((loginToken == null) || ("".equals(loginToken))) {
			String msg = "A required parameters was missing.";
			throw new InvalidParameterException(msg);
		}

		// Default the login domain to "WEB".
		if ((caller == null) || ("".equals(caller))) {
			caller = "WEB";
		} else if ((!"WEB".equals(caller)) && (!"EN".equals(caller)) && (!"ML".equals(caller)) 
				&& (!"TEST".equals(caller)) && (!"LD".equals(caller)) && (!"LSDS".equals(caller))
				&& !caller.matches(AppConstants.DESKTOP_VERSION_REGEX)) {
			caller = "WEB";
		}

		//	Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tk", loginToken));
		params.add(new BasicNameValuePair("loginDomain", caller));

		DefaultHttpClient httpClient = null;
		try {
			
			//	Send and receive the HTTP response.
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("logout", params, httpClient, null);
			}
			catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			}
			catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			}
			catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			}
			catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
						
			if (document == null) {
				throw new ClientException("Error when parsing response.");
			}
			
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				logger.debug(method + "responseCode = " + responseCodeNode.getText());
				if (SUCCESS.equals(responseCodeNode.getText())) {
					logger.debug(method + "Logout successfully.");
				}
				
			} else { // ERROR
				Node errorCodeNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
				if (errorCodeNode == null) {
					throw new ClientException("Error code was empty.");
				}
				String errorCode = errorCodeNode.getText();
				logger.debug(method + "errorCode = " + errorCode);
				
				Node errorMessageNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_MESSAGE);
				String errorMessage;
				if (errorMessageNode == null) {
					errorMessage = "";
				} else {
					errorMessage = errorMessageNode.getText();
				}
				
				switch(ResponseCode.valueOf(errorCode)) {						
					case USER_NOT_LOGGED_IN:
						throw new LogoutException(errorMessage);

					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					default:
						throw new ClientException(errorMessage);
				}
			}
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.login.client.LoginAPI#getAuthorizationToken(java.lang.String, com.livescribe.aws.login.util.AuthorizationType)
	 */
//	public String getAuthorizationToken(String email, AuthorizationType authType) throws InvalidParameterException {
//	
//		String method = "getAuthorizationToken():  ";
//		
//		if ((email == null) || ("".equals(email))) {
//			String msg = "A required parameter was either empty or 'null'.";
//			logger.debug(method + msg);
//			throw new InvalidParameterException(msg);
//		}
//		if (authType == null) {
//			String msg = "A required parameter was either empty or 'null'.";
//			logger.debug(method + msg);
//			throw new InvalidParameterException(msg);
//		}
//		
//		
//		return null;
//	}
	
	/*
	 * (non-Javadoc)
	 * @see com.livescribe.aws.login.client.LoginAPI#getLoggedInUserEmail(java.lang.String, java.lang.String)
	 */
	public String getLoggedInUserEmail(String loginToken, String loginDomain) 
			throws IOException, InvalidParameterException, UserNotLoggedInException, ClientException {
		
		String method = "getUserInfo():  ";
		
		// Validating
		if ((loginToken == null) || ("".equals(loginToken))) {
			String msg = "Missing 'loginToken' parameter.";
			throw new InvalidParameterException(msg);
		}
		if ((loginDomain == null) || ("".equals(loginDomain))) {
			String msg = "Missing 'loginDomain' parameter.";
			throw new InvalidParameterException(msg);
		}
		
		//	Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tk", loginToken));
		params.add(new BasicNameValuePair("loginDomain", loginDomain));
		
		DefaultHttpClient httpClient = null;
		try {

			// Make http request
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("getuserinfo", params, httpClient, loginToken);
			}
			catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			}
			catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			}
			catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			}
			catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response.");
			}
			
			// Processing response
			String emailAddress = "";
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					// Parse <email> XML element to get email node.
					Node emailNode = document.selectSingleNode(ObjectFactory.NODE_EMAIL_ADDRESS);
					if (emailNode == null) {
						throw new ClientException("Email element was empty.");
					}
					emailAddress = emailNode.getText();
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
					case USER_NOT_LOGGED_IN:
						throw new UserNotLoggedInException(errorMessage);
					
					case SERVER_ERROR:
						throw new ClientException(errorMessage);
				}
			}
			
			return emailAddress;
		}
		finally {
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
	 * @see com.livescribe.aws.login.client.LoginAPI#changeUserEmail(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ServiceResponse changeUserEmail(String loginToken, String currentEmail, String newEmail) 
			throws InvalidParameterException, ClientException, UserNotLoggedInException, EmailAlreadyTakenException, DuplicateEmailAddressException, UserNotFoundException {
		
		String method = "changeUserEmail():  ";
		
		ServiceResponse response = new ServiceResponse();
		
		// validate loginToken
		if ((loginToken == null) || ("".equals(loginToken))) {
			throw new InvalidParameterException("Missing 'loginToken' parameter.");
		}
		
		// validate currentEmail
		if ((currentEmail == null) || ("".equals(currentEmail))) {
			throw new InvalidParameterException("Missing 'currentEmail' parameter.");
		}
		
		// validate newEmail
		if (newEmail == null || ("".equals(newEmail))) {
			throw new InvalidParameterException("Missing 'newEmail' parameter.");
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("loginToken", loginToken));
		params.add(new BasicNameValuePair("currentEmail", currentEmail));
		params.add(new BasicNameValuePair("newEmail", newEmail));
		
		DefaultHttpClient httpClient = null;
		try {
			//	Make http request
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("changeUserEmailByLoginToken", params, httpClient, loginToken);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response.");
			}
			
			// Processing response
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
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					case USER_NOT_LOGGED_IN:
						throw new UserNotLoggedInException(errorMessage);
						
					case EMAIL_ALREADY_IN_USE:
						throw new EmailAlreadyTakenException(errorMessage);
						
					case DUPLICATE_EMAIL_ADDRESS_FOUND:
						throw new DuplicateEmailAddressException();
						
					case USER_NOT_FOUND:
						throw new UserNotFoundException();
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			
			return response;
		}
		finally {
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
	 * @see com.livescribe.aws.login.client.LoginAPI#changeUserPassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ServiceResponse changeUserPassword(String email, String oldPassword, String newPassword) 
			throws InvalidParameterException, IncorrectPasswordException, ClientException, DuplicateEmailAddressException, UserNotFoundException {
		
		String method = "changeUserPassword():  ";
		
		ServiceResponse response = new ServiceResponse();
		
		// validate email
		if ((email == null) || ("".equals(email))) {
			throw new InvalidParameterException("Missing 'email' parameter.");
		}
		
		// validate oldPassword
		if ((oldPassword == null) || ("".equals(oldPassword))) {
			throw new InvalidParameterException("Missing 'oldPassword' parameter.");
		}
		
		// validate newPassword
		if ((newPassword == null) || ("".equals(newPassword))) {
			throw new InvalidParameterException("Missing 'newPassword' parameter.");
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("oldPassword", oldPassword));
		params.add(new BasicNameValuePair("newPassword", newPassword));
		
		DefaultHttpClient httpClient = null;
		try {
			// Make http request
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("changeUserPassword", params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response.");
			}
			
			// Processing response
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
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					case INCORRECT_PASSWORD:
						throw new IncorrectPasswordException(errorMessage);
						
					case DUPLICATE_EMAIL_ADDRESS_FOUND:
						throw new DuplicateEmailAddressException();
						
					case USER_NOT_FOUND:
						throw new UserNotFoundException();
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			
			return response;
		}
		finally {
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
	 * @see com.livescribe.aws.login.client.LoginAPI#changeUserPassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ServiceResponse changeUserPasswordWithLoginToken(String loginToken, String email, String newPassword) 
			throws InvalidParameterException, ClientException, UserNotFoundException, UserNotLoggedInException {
		
		String method = "changeUserPasswordWithLoginToken():  ";
		
		ServiceResponse response = new ServiceResponse();
		
		// validate loginToken
		if ((loginToken == null) || ("".equals(loginToken))) {
			throw new InvalidParameterException("Missing 'loginToken' parameter.");
		}
		
		// validate email
		if ((email == null) || ("".equals(email))) {
			throw new InvalidParameterException("Missing 'email' parameter.");
		}
		
		// validate newPassword
		if ((newPassword == null) || ("".equals(newPassword))) {
			throw new InvalidParameterException("Missing 'newPassword' parameter.");
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("loginToken", loginToken));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("newPassword", newPassword));
		
		DefaultHttpClient httpClient = null;
		try {
			//	Make http request
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("changeUserPasswordWithLoginToken", params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
					logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response.");
			}
			
			// Processing response
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
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					case USER_NOT_FOUND:
						throw new UserNotFoundException();
						
					case USER_NOT_LOGGED_IN:
						throw new UserNotLoggedInException(errorMessage);
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			
			return response;
		}
		finally {
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
	 * @see com.livescribe.aws.login.client.LoginAPI#changeUserPasswordForSupportUser(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ServiceResponse changeUserPasswordForSupportUser(String supportUserLoginToken, String userEmail, String userPassword) 
			throws InvalidParameterException, ClientException, UserNotFoundException, UserNotLoggedInException, InsufficientPrivilegeException, DuplicateEmailAddressException {
		
		String method = "changeUserPasswordForSupportUser():  ";
		
		ServiceResponse response = new ServiceResponse();
		
		// validate loginToken
		if ((supportUserLoginToken == null) || ("".equals(supportUserLoginToken))) {
			throw new InvalidParameterException("Missing 'supportUserLoginToken' parameter.");
		}
		
		// validate email
		if ((userEmail == null) || ("".equals(userEmail))) {
			throw new InvalidParameterException("Missing 'userEmail' parameter.");
		}
		
		// validate newPassword
		if ((userPassword == null) || ("".equals(userPassword))) {
			throw new InvalidParameterException("Missing 'userPassword' parameter.");
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("supportUserLoginToken", supportUserLoginToken));
		params.add(new BasicNameValuePair("userEmail", userEmail));
		params.add(new BasicNameValuePair("userPassword", userPassword));
		
		DefaultHttpClient httpClient = null;
		try {
			//	Make http request
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("changeUserPasswordForSupportUser", params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
					logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response.");
			}
			
			// Processing response
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
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					case USER_NOT_FOUND:
						throw new UserNotFoundException(errorMessage);
						
					case USER_NOT_LOGGED_IN:
						throw new UserNotLoggedInException(errorMessage);
						
					case INSUFFICIENT_PRIVILEGE:
						throw new InsufficientPrivilegeException(errorMessage);
						
					case DUPLICATE_EMAIL_ADDRESS_FOUND:
						throw new DuplicateEmailAddressException(errorMessage);
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			
			return response;
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}
	
	public ServiceResponse changePasswordWithoutUserTokenAndOldPassword(String email, String newPassword) 
			throws InvalidParameterException, ClientException, UserNotFoundException, UserNotLoggedInException, InsufficientPrivilegeException, DuplicateEmailAddressException {
		
		String method = "changePasswordWithoutUserTokenAndOldPassword():  ";
		
		ServiceResponse response = new ServiceResponse();
			
		// validate email
		if ((email == null) || ("".equals(email))) {
			throw new InvalidParameterException("Missing 'email' parameter.");
		}
		
		// validate newPassword
		if ((newPassword == null) || ("".equals(newPassword))) {
			throw new InvalidParameterException("Missing 'newPassword' parameter.");
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("newPassword", newPassword));
		
		DefaultHttpClient httpClient = null;
		try {
			//	Make http request
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("changePasswordWithoutUserTokenAndOldPassword", params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
					logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			//	Parsing response
			Document document = null;
			try {
				logger.debug(method + "Parsing response.");
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + ise.getMessage());
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ClientException("Error occured when parsing response. " + de.getMessage());
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response.");
			}
			
			// Processing response
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
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					case USER_NOT_FOUND:
						throw new UserNotFoundException(errorMessage);
						
					case DUPLICATE_EMAIL_ADDRESS_FOUND:
						throw new DuplicateEmailAddressException(errorMessage);
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			
			return response;
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}
	
	/**
	 * {@inheritDoc} 
	 */
	public AuthorizationResponse findAuthorizationByPenDisplayId(String penDisplayId, AuthorizationType authorizationType) 
			throws InvalidParameterException, RegistrationNotFoundException, AuthorizationException, ClientException {
		
		String method = METHOD_FIND_AUTH_BY_PENDISPLAYID + ":  ";
		
		// Validating parameters
		if ((penDisplayId == null) || ("".equals(penDisplayId))) {
			throw new InvalidParameterException("Missing parameter 'penDisplayId'");
		}
		
		if (authorizationType == null) {
			throw new InvalidParameterException("Missing parameter 'authorizationType'");
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("penDisplayId", penDisplayId));
		params.add(new BasicNameValuePair("provider", authorizationType.getCode()));
		
		DefaultHttpClient httpClient = null;
		try {
			//	Make http request
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send(METHOD_FIND_AUTH_BY_PENDISPLAYID, params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
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
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response from server.");
			}
			
			AuthorizationResponse response = new AuthorizationResponse();
			
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					response.setResponseCode(ResponseCode.SUCCESS);
					response.setOauthAccessToken(document.selectSingleNode(ObjectFactory.NODE_AUTHORIZATION_OAUTHACCESSTOKEN).getText());
					response.setEnUserId(Long.parseLong(document.selectSingleNode(ObjectFactory.NODE_AUTHORIZATION_USERID).getText()));
					response.setEnShardId(document.selectSingleNode(ObjectFactory.NODE_AUTHORIZATION_SHARDID).getText());
					response.setEnUsername(document.selectSingleNode(ObjectFactory.NODE_AUTHORIZATION_USERNAME).getText());
					response.setAuthorizationId(Long.parseLong(document.selectSingleNode(ObjectFactory.NODE_AUTHORIZATION_ID).getText()));
					response.setIsPrimary(Boolean.parseBoolean(document.selectSingleNode(ObjectFactory.NODE_IS_PRIMARY).getText()));

					Node responseUserNode = document.selectSingleNode(ObjectFactory.NODE_USER);
					if (responseUserNode != null) {
						UserInfoResponse userInfoResponse = new UserInfoResponse();
						userInfoResponse.setEmail(responseUserNode.getDocument().selectSingleNode("//user/email").getText());
						userInfoResponse.setUid(responseUserNode.getDocument().selectSingleNode("//user/uid").getText());
						response.setUser(userInfoResponse);
					}
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
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					case REGISTRATION_NOT_FOUND:
						throw new RegistrationNotFoundException(errorMessage);
						
					case UNAUTHORIZED:
						throw new AuthorizationException(errorMessage);
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			logger.debug(method + response.toString());
			
			return response;
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public AuthorizationResponse findAuthorizationByUID(String uid, AuthorizationType authorizationType) throws InvalidParameterException, ClientException, AuthorizationException {
		
		String method = METHOD_FIND_AUTH_BY_UID + ":  ";
		
		// Validating parameters
		if ((uid == null) || ("".equals(uid))) {
			throw new InvalidParameterException("Missing parameter 'uid'");
		}
		
		if (authorizationType == null) {
			throw new InvalidParameterException("Missing parameter 'authorizationType'");
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("provider", authorizationType.getCode()));
		
		String path = "/" + METHOD_FIND_AUTH_BY_UID + "/auth/" + authorizationType.getCode() + "/" + uid + ".xml";

		DefaultHttpClient httpClient = null;
		try {
			// Make http request
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send(METHOD_FIND_AUTH_BY_UID, params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			//--------------------------------------------------
			//	Read the response into an XML String.
//			String responseXml = null;
//			HttpEntity entity = httpResponse.getEntity();
//			if (entity != null) {
//				responseXml = readEntityIntoString(entity);
//			}
//			
//			logger.debug(method + " - responseXml = " + responseXml);
//			
//			if ((responseXml == null) || (responseXml.isEmpty())) {
//				String msg = "The response from the server was empty or 'null'.";
//				throw new ClientException(msg);
//			}
			
			// Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				String msg = "Error occured when parsing response from server. ";
				throw new ClientException(msg, ise);
			} catch (DocumentException de) {
				String msg = "Error occured when parsing response from server. ";
				throw new ClientException(msg, de);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response from server.");
			}
			
			AuthorizationResponse response = new AuthorizationResponse();
			
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					response.setResponseCode(ResponseCode.SUCCESS);
					response.setOauthAccessToken(document.selectSingleNode(ObjectFactory.NODE_AUTHORIZATION_OAUTHACCESSTOKEN).getText());
					response.setEnUserId(Long.parseLong(document.selectSingleNode(ObjectFactory.NODE_AUTHORIZATION_USERID).getText()));
					response.setEnShardId(document.selectSingleNode(ObjectFactory.NODE_AUTHORIZATION_SHARDID).getText());
					response.setEnUsername(document.selectSingleNode(ObjectFactory.NODE_AUTHORIZATION_USERNAME).getText());
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
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					case UNAUTHORIZED:
						throw new AuthorizationException(errorMessage);
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			
			return response;
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.login.client.LoginAPI#findByEmail(java.lang.String)
	 */
	public UserInfoResponse findUserByEmail(String email) throws InvalidParameterException, ClientException, ParseException, UserNotFoundException, DuplicateEmailAddressException {
		
		String method = "findUserByEmail():  ";
		logger.info("BEFORE - " + method + " - " + email + " - ");
		long startMilliseconds = System.currentTimeMillis();
		
		// Validating parameters
		if ((email == null) || ("".equals(email.trim()))) {
			String msg = "Missing 'email' parameter.";
			logger.error("EXCEPTION - " + method + " - " + email + " - " + msg);
			throw new InvalidParameterException(msg);
		}
		
		String uriString = "user/email/" + email;
		URI uri;
		try {
			uri = createRequestURI(uriString);
		} catch (URISyntaxException use) {
			String msg = "There was a URI syntax error.";
			logger.error(method + msg, use);
			throw new ClientException(msg, use);
		}
		
		HttpGet getMethod = createGetMethod();
		getMethod.setURI(uri);
		
		HttpClient httpClient = null;		
		try {
		
			// Make http request
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				
				String env = this.properties.getEnv();
				
				//	For 'local' environments to avoid SSL issue.
				if ("local".equals(env)) {
					logger.debug(method + "Handling local environment...");
					HttpClient wrappedClient = wrapHttpClient(httpClient);
					httpResponse = wrappedClient.execute(getMethod);
					if (httpResponse != null) {
						logger.debug(method + httpResponse.toString());
					}
					else {
						logger.debug(method + "Wrapped response was 'null'.");
					}
				}
				else {					
					httpResponse = httpClient.execute(getMethod);
				}
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + email + " - " + msg + " - " + duration + " milliseconds.", cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + email + " - " +  msg + " - " + duration + " milliseconds.", ioe);
				throw new ClientException(msg, ioe);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + email + " - " +  msg + " - " + duration + " milliseconds.", kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + email + " - " +  msg + " - " + duration + " milliseconds.", nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				String msg = "Error when making http call to LSLoginService";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + email + " - " +  msg);
				throw new ClientException(msg);
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			//	Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				String msg = "Error occured when parsing response from server. ";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + email + " - " +  msg + " - " + duration + " milliseconds.");
				throw new ClientException(msg, ise);
			} catch (DocumentException de) {
				String msg = "Error occured when parsing response from server. ";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + email + " - " +  msg + " - " + duration + " milliseconds.");
				throw new ClientException(msg, de);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + email + " - " +  msg + " - " + duration + " milliseconds.");
				throw new ClientException(msg, ioe);
			}
			
			//	If we somehow received a 'null' response, throw an exception.
			if (document == null) {
				String msg = "The response from Login Service was 'null'";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + email + " - " +  msg + " - " + duration + " milliseconds.");
				throw new ClientException(msg);
			}
			
			//	If the response is present and equals "SUCCESS", create the UserInfoResponse.
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {
				if ("SUCCESS".equals(responseCodeNode.getText())) {
					UserInfoResponse response = new UserInfoResponse(document);
					long endMilliseconds = System.currentTimeMillis();
					long duration = endMilliseconds - startMilliseconds;
					logger.info("AFTER - " + method + " - " + email + " - " + duration + " milliseconds.");
					return response;
				}
				else {
					//	TODO:  Handle other <responseCode> responses when they are added.
				}
			}
			//	Handle the <errorCode> responses.
			else {
				Node errorCodeNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
				if (errorCodeNode == null) {
					throw new ClientException("Error code was empty.");
				}
				String errorCode = errorCodeNode.getText();
				
				Node errorMessageNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_MESSAGE);
				String errorMessage = null;
				if (errorMessageNode == null) {
					errorMessage = "";
				} else {
					errorMessage = errorMessageNode.getText();
				}
				
				//	Throw the appropriate exception based on the error response code.
				switch(ResponseCode.valueOf(errorCode)) {
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
					
					case USER_NOT_FOUND:
						throw new UserNotFoundException(errorMessage);
						
					case DUPLICATE_EMAIL_ADDRESS_FOUND:
						throw new DuplicateEmailAddressException(errorMessage);
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			
			return null;
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @throws UserNotFoundException 
	 * @throws AuthorizationNotFoundException 
	 */
	public AuthorizationResponse findAuthorizationByUIDAndProviderUId(String uid, String providerUserId, AuthorizationType authorizationType)
			throws InvalidParameterException, AuthorizationException, ClientException, UserNotFoundException, AuthorizationNotFoundException {

		String method = METHOD_FIND_AUTH_BY_UID_AND_PROVIDER_USER_ID + ":  ";
		logger.debug(method + "called with uid: " + uid + " and enUserId: " + providerUserId);
		
		//--------------------------------------------------
		// Validating parameters
		if ((uid == null) || ("".equals(uid.trim()))) {
			throw new InvalidParameterException("Missing parameter 'uid'");
		}
		
		if ((providerUserId == null) || ("".equals(providerUserId.trim()))) {
			throw new InvalidParameterException("Missing parameter 'enUserId'");
		}
		
		if (authorizationType == null) {
			throw new InvalidParameterException("Missing parameter 'authorizationType'");
		}
		
		//--------------------------------------------------
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("providerUserId", providerUserId));
		params.add(new BasicNameValuePair("provider", authorizationType.getCode()));
		
		HttpClient httpClient = null;
		try {
			//--------------------------------------------------
			// Make http request
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
				uriString.append(METHOD_FIND_AUTH_BY_UID_AND_PROVIDER_USER_ID).append(".xml");
				
				logger.debug(method + " - URL:  " + uriString.toString());
				
				URI uri = new URI(uriString.toString());
				HttpPost postRequest = createPostMethod(uri, params);

				httpResponse = send(httpClient, postRequest);
//				httpResponse = send(METHOD_FIND_AUTH_BY_UID_AND_PROVIDER_USER_ID, params, httpClient, null);
				
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making HTTP call to LSLoginService method:  " + METHOD_FIND_AUTH_BY_UID_AND_PROVIDER_USER_ID);
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			//--------------------------------------------------
			//	Read the response into an XML String.
			String responseXml = null;
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				responseXml = readEntityIntoString(entity);
			}
			
			if ((responseXml == null) || ("".equals(responseXml.trim()))) {
				String msg = "The response from the server was empty or 'null'.";
				throw new ClientException(msg);
			}
			
			logger.debug(method + " - responseXml = \n" + responseXml);
			
			//--------------------------------------------------
			//	If this is a SUCCESS response ...
			if (responseXml.contains(NODE_RESPONSE_CODE)) {
				
				XStream xStream = new XStream(new DomDriver());
				xStream.alias("response", AuthorizationResponse.class);
				xStream.alias("user", User.class);
//				xStream.alias("responseList", List.class);
//				xStream.alias("authorization", AuthorizationDto.class);
				AuthorizationResponse response = (AuthorizationResponse) xStream.fromXML(responseXml);
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
				case INVALID_PARAMETER:
					throw new InvalidParameterException(errorMessage);
				case USER_NOT_FOUND:
					throw new UserNotFoundException(errorMessage);
				case UNAUTHORIZED:
					throw new AuthorizationNotFoundException(errorMessage);
				case SERVER_ERROR:
					throw new ClientException(errorMessage);
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
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.login.client.LoginAPI#findAuthorizationsByUid(java.lang.String, com.livescribe.aws.login.util.AuthorizationType)
	 */
	public List<AuthorizationDto> findAuthorizationsByUid(String uid, AuthorizationType authType) throws ClientException, InvalidParameterException {
		
		String method = "findAuthorizationsByUid()";
		
		String baseUrl = createBaseUrlString();
		StringBuilder uriString = new StringBuilder();
		uriString.append(baseUrl);
		uriString.append("auth/").append(authType.getCode()).append("/");
		uriString.append(uid).append(".xml");
		
		logger.debug(method + " - URI:  " + uriString);
		
		HttpClient httpClient = null;
		try {
			HttpResponse httpResponse;
			try {
				httpClient = new DefaultHttpClient();
				
				URI uri = new URI(uriString.toString());
				HttpGet getRequest = createGetMethod(uri);
				
				httpResponse = send(httpClient, getRequest);
				
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method + " - " + msg, use);
				throw new ClientException(msg, use);				
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LS Login Service");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + " - HTTP Status:  " + statusCode);
				String reason = httpResponse.getStatusLine().getReasonPhrase();
				logger.debug(method + " - Reason:  " + reason);
				throw new ClientException("Error when making http call to LS Login Service. HTTP Status: " + statusCode);
			}
			
			//--------------------------------------------------
			//	Read the response into an XML String.
			String responseXml = null;
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				responseXml = readEntityIntoString(entity);
			}
			
			if ((responseXml == null) || ("".equals(responseXml.trim()))) {
				String msg = "The response from the server was empty or 'null'.";
				throw new ClientException(msg);
			}
			
			logger.debug(method + " - responseXml = \n" + responseXml);
			
			List<AuthorizationDto> list = null;
			
			//--------------------------------------------------
			//	If this is a SUCCESS response ...
			if (responseXml.contains(NODE_RESPONSE_CODE)) {
				
				XStream xStream = new XStream(new DomDriver());
				xStream.alias("response", AuthorizationListResponse.class);
				xStream.alias("authorization", AuthorizationDto.class);
				AuthorizationListResponse response = (AuthorizationListResponse)xStream.fromXML(responseXml);
				list = response.getResponseList();
				return list;
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
				case INVALID_PARAMETER:
					throw new InvalidParameterException(errorMessage);
//				case USER_NOT_FOUND:
//					throw new UserNotFoundException(errorMessage);
//				case UNAUTHORIZED:
//					throw new AuthorizationNotFoundException(errorMessage);
//				case SERVER_ERROR:
//					throw new ClientException(errorMessage);
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
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.login.client.LoginAPI#findUserInfoByProviderUserId(String userId, AuthorizationType authorizationType)
	 */
	@Deprecated
	public AuthorizationListResponse findAuthorizationByProviderUserId(String userId, AuthorizationType authorizationType) throws InvalidParameterException, ClientException, AuthorizationException {
		
		String method = METHOD_FIND_AUTH_BY_PROVIDER_USER_ID + ":  ";
		
		// Validating parameters
		if ((userId == null) || ("".equals(userId.trim()))) {
			throw new InvalidParameterException("Missing parameter 'userId'");
		}
		if (authorizationType == null) {
			throw new InvalidParameterException("Missing parameter 'authorizationType'");
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", userId));
		params.add(new BasicNameValuePair("authType", authorizationType.getCode()));		

		DefaultHttpClient httpClient = null;
		try {
			// Make http request
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send(METHOD_FIND_AUTH_BY_PROVIDER_USER_ID, params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}

			//--------------------------------------------------
			//	Read the response into an XML String.
			String responseXml = null;
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				responseXml = readEntityIntoString(entity);
			}

			if ((responseXml == null) || ("".equals(responseXml.trim()))) {
				String msg = "The response from the server was empty or 'null'.";
				throw new ClientException(msg);
			}
			
			//--------------------------------------------------
			//	Parsing response
			XStream xStream = new XStream(new DomDriver());
			xStream.alias("response", AuthorizationListResponse.class);
			xStream.alias("responseList", List.class);
			xStream.alias("authorization", AuthorizationDto.class);
			AuthorizationListResponse response = (AuthorizationListResponse) xStream.fromXML(responseXml);
			
			return response;
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}


	/**
	 * <p></p>
	 * 
	 * @param httpResponse
	 */
	private void xstreamParse(HttpResponse httpResponse) {
		
		XStream xstream = new XStream(new DomDriver());
		Class[] classes = new Class[2];
		classes[0] = com.livescribe.aws.login.response.UserInfoResponse.class;
		classes[1] = com.livescribe.framework.web.response.ResponseCode.class;
		xstream.processAnnotations(classes);
		xstream.autodetectAnnotations(true);
//		xstream.setClassLoader(com.livescribe.framework.web.response.ResponseCode.class.getClassLoader());
//		xstream.setClassLoader(com.livescribe.aws.login.response.UserInfoResponse.class.getClassLoader());
		xstream.alias("response", com.livescribe.aws.login.response.UserInfoResponse.class);
		xstream.alias("responseCode", com.livescribe.framework.web.response.ResponseCode.class);
		HttpEntity entity = httpResponse.getEntity();
		InputStream is = null;
		ObjectInputStream ois = null;
		UserInfoResponse uir = null;
		ResponseCode code = null;
		try {
			is = entity.getContent();
			ois = xstream.createObjectInputStream(is);
			Object obj = null;
			while ((obj = ois.readObject()) != null) {
				if (obj instanceof ResponseCode) {
					code = (ResponseCode)obj;
					logger.debug("Found 'ResponseCode':  " + code.getCode());
				}
				else if (obj instanceof UserInfoResponse) {
					uir = (UserInfoResponse)obj;
					logger.debug("Found 'UserInfoResponse':  " + uir.toString());
				}
				else {
					logger.debug("Found 'obj' to be of class:  " + obj.getClass().getName());
				}
			}
			uir = (UserInfoResponse)ois.readObject();
			if (uir != null) {
				logger.debug("uir:  " + uir.toString());
			}
		} catch (IllegalStateException ise) {
			String msg = "IllegalStateException thrown when attempting ot unmarshal response from Login Service.  " + ise.getMessage();;
			logger.error(msg, ise);
			
		} catch (ClassNotFoundException cnfe) {
			String msg = "ClassNotFoundException thrown when attempting ot unmarshal response from Login Service.  " + cnfe.getMessage();
			logger.error(msg, cnfe);
			
		} catch (IOException ioe) {
			String msg = "IOException thrown when attempting ot unmarshal response from Login Service.  " + ioe.getMessage();
			logger.error(msg, ioe);
			
		}
		finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException ioe) {
				
			}
		}
	}


	/**
	 * <p></p>
	 * 
	 * @param userId
	 * @param method
	 * @param httpResponse
	 * @throws ClientException
	 */
	private void jaxbParse(String userId, HttpResponse httpResponse) throws ClientException {
		
		String method = "jaxbParse()";
		
		InputStream in = null;
		try {
			HttpEntity entity = httpResponse.getEntity();
			if (entity == null) {
				String msg = "No 'HttpEntity' found in HTTP response from Login Service.";
				logger.error(method + " - " + userId + " - " + msg);
				throw new ClientException(msg);
			}
			in = entity.getContent();
			
			JAXBContext ctx = JAXBContext.newInstance(UserInfoResponse.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			UserInfoResponse uir = (UserInfoResponse)unmarshaller.unmarshal(in);
			
			if (uir != null) {
				logger.debug(method + "uir:  " + uir.toString());
			}
		} catch (IllegalStateException ise) {
			String msg = "IllegalStateException";
			logger.error(msg, ise);
			
		} catch (IOException ioe) {
			String msg = "IOException";
			logger.error(msg, ioe);
			
		} catch (JAXBException jbe) {
			String msg = "JAXBException";
			logger.error(msg, jbe);
			
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
					
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.login.client.LoginAPI#findUserUIDByDisplayId(java.lang.String)
	 */
	public String findUserUIDByPenDisplayId(String penDisplayId) throws InvalidParameterException, ClientException, RegistrationNotFoundException {
		
		String method = "findUserUIDByPenDisplayId():  ";
		
		// Validating parameters
		if ((penDisplayId == null) || ("".equals(penDisplayId))) {
			throw new InvalidParameterException("Missing parameter 'penDisplayId'");
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("displayId", penDisplayId));
		
		DefaultHttpClient httpClient = null;
		try {
			// Make http request
			HttpResponse httpResponse = null;
			
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("finduseruidbypendisplayid", params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending HTTP request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
					logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
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
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response from server.");
			}
			
			String uid = "";
			
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					uid = document.selectSingleNode(ObjectFactory.NODE_UID).getText();
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
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					case REGISTRATION_NOT_FOUND:
						throw new RegistrationNotFoundException(errorMessage);
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			
			return uid;
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.login.client.LoginAPI#findUserUIDByDisplayId(java.lang.String)
	 */
	public String findUserUIDBySerialNumber(String serialNumber) throws InvalidParameterException, ClientException, RegistrationNotFoundException, AuthorizationException {
		
		String method = "findUserUIDBySerialNumber():  ";
		
		// Validating parameters
		if (serialNumber == null || ("".equals(serialNumber))) {
			throw new InvalidParameterException("Missing parameter 'serialNumber'");
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("serialNumber", serialNumber));
		
		DefaultHttpClient httpClient = null;
		try {
			// Make http request
			HttpResponse httpResponse = null;
			
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("finduseruidbyserialnumber", params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending HTTP request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
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
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response from server.");
			}
			
			String uid = "";
			
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {	// SUCCESS
				if (SUCCESS.equals(responseCodeNode.getText())) {
					uid = document.selectSingleNode(ObjectFactory.NODE_UID).getText();
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
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
						
					case REGISTRATION_NOT_FOUND:
						throw new RegistrationNotFoundException(errorMessage);
						
					case UNAUTHORIZED:
						throw new AuthorizationException(errorMessage);
						
					default:
						throw new ClientException(errorMessage);
				}
			}
			
			return uid;
		}
		finally {
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
	 * @see com.livescribe.aws.login.client.LoginAPI#isUserExisted(java.lang.String)
	 */
	public boolean isUserExisted(String email) throws InvalidParameterException, ClientException {
		
		String method = "isUserExisted():  ";
		
		// Validating parameters
		if (email == null || ("".equals(email))) {
			throw new InvalidParameterException("Missing parameter 'email'");
		}
		
		// Construct the query string.
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		
		int statusCode = -1;
		
		DefaultHttpClient httpClient = null;
		try {

			// Make http request
			HttpResponse httpResponse = null;
			
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = send("isUserExisted", params, httpClient, null);
				
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending HTTP request to LSLoginService.";
				logger.debug(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			} catch (URISyntaxException use) {
				String msg = "There was an error in the syntax of the request.";
				logger.debug(method +  msg, use);
				throw new ClientException(msg, use);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				logger.debug(method +  msg, kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				logger.debug(method +  msg, nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LSLoginService");
			}
			
			statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
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
				String msg = "There was an error when sending http request to LSLoginService.";
				throw new ClientException(msg, ioe);
			}
			
			if (document == null) {
				throw new ClientException("Error when parsing response from server.");
			}
			
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {
				String resCode = responseCodeNode.getText();
				switch(ResponseCode.valueOf(resCode)) {
					case USER_ALREADY_EXISTS:
						return true;
					
					case USER_NOT_FOUND:
						return false;
				}	
			}
			throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}
	
	/**
	 * <p>Sends the given <code>query</code> to the server located at the 
	 * URI ending with the given <code>rqMap</code>.</p>
	 * 
	 * If a request is to be sent to <code>https://server.com/services/lslogin/new.xml</code>,
	 * the <code>rqMap</code> parameter represents the <code>new</code> portion 
	 * of the URI.  The "<code>.xml</code>" extension is added by this method.
	 * 
	 * @param rqMap The end of the URI to send the request to. 
	 * @param params The URL-encoded list of parameters for the request.
	 * @param token
	 *  
	 * @throws IOException
	 * @throws DocumentException 
	 */
	private HttpResponse send(String rqMap, List<NameValuePair> params, DefaultHttpClient httpClient, String token) throws ClientProtocolException, NoSuchAlgorithmException, KeyManagementException, IOException, URISyntaxException {
		
		String method = "send():  ";
		
		logger.debug("Entering LoginClient.send()");
		
		//	Create the URL of the request.
		String context = this.properties.getProperty(KEY_CONTEXT);
		String host = this.properties.getProperty(KEY_HOST);
		String port = this.properties.getProperty(KEY_PORT);
		
		//	If a port number is configured, use that as part of the URL.
		int portInt = -1;
		if ((port != null) && (!"".equals(port.trim()))) {
			try {
				portInt = Integer.parseInt(port);
			}
			catch (NumberFormatException nfe) {
				logger.error(method + " - " + nfe.getMessage(), nfe);
			}
		}
		
		//--------------------------------------------------
		//	Construct the request URI.
		URI uri = null;
		String path = null;
		StringBuilder uriString = new StringBuilder();
		
		//	If there IS a port number configured ...
		if (portInt > 0) {
			//	... and the port number ends with "43", use HTTPS ...
			if (port.endsWith("43")) {
				uriString.append("https://").append(host);
				if (portInt != 443) {
					uriString.append(":").append(port);
				}
			}
			//	... otherwise, use the given port and HTTP as the protocol.
			else {
				uriString.append("http://").append(host);
				uriString.append(":").append(port);
			}
		}
		//	If a port number IS NOT configured, use HTTP as the protocol.
		else {
			uriString.append("http://").append(host);
		}
		
		uriString.append(context).append("/");
		uriString.append(rqMap).append(".xml");
		
		uri = new URI(uriString.toString());
		
		//	URL-Encode the parameters.
		String paramStr = URLEncodedUtils.format(params, CHARSET);
		
		logger.debug(method + "Sending URI:  " + uri);
		logger.debug(method + " Parameters:  " + processParamsForLogging(params));
		
		StringEntity paramsEntity = new StringEntity(paramStr);
		
		//--------------------------------------------------
		//	Create the HTTP POST request.
		HttpPost postMethod = createPostMethod(uri, paramsEntity);
		
		StringBuilder sb = new StringBuilder();
		sb.append(postMethod.getURI()).append("\n");
		sb.append(postMethod.toString()).append("\n");
		logger.debug(method + sb.toString());
		
		//--------------------------------------------------
		// Set token param to request's cookie
		if (token != null && token.trim().length() > 0) {
			CookieStore cookieStore = httpClient.getCookieStore();
			BasicClientCookie cookie = new BasicClientCookie("tk", token);
			cookie.setDomain(host);
			cookie.setPath("/");
			cookieStore.addCookie(cookie);
		}
		
		String env = this.properties.getEnv();
		if ("local".equals(env)) {
			logger.debug(method + "Handling local environment...");
			DefaultHttpClient wrappedClient = (DefaultHttpClient) wrapHttpClient(httpClient);
			
			//--------------------------------------------------
			// Set token param to request's cookie
			if (token != null && token.trim().length() > 0) {
				CookieStore cookieStore = wrappedClient.getCookieStore();
				BasicClientCookie cookie = new BasicClientCookie("tk", token);
				cookie.setDomain(host);
				cookie.setPath("/");
				cookieStore.addCookie(cookie);
			}
			
			HttpResponse response = wrappedClient.execute(postMethod);
			if (response != null) {
				logger.debug(method + response.toString());
			}
			else {
				logger.debug(method + "Wrapped response was 'null'.");
			}
			return response;
		}
		
		HttpResponse response = httpClient.execute(postMethod);
		if (response != null) {
			logger.debug(method + "Received HTTP response:  " + response.toString());
		}
		else {
			logger.debug(method + "HTTP response was 'null'.  Returning 'null'.");
		}
		logger.debug("Completed sending POST request to: " + uri);
		return response;
	}

	/**
	 * <p>Creates an HTTP GET request.</p>
	 * 
	 * This can be used for URLs where the parameters are part of the path.
	 * 
	 * @param uri The URI of the request.
	 * 
	 * @return an <code>HttpGet</code> object.
	 */
	private HttpGet createGetMethod() {
		
		HttpGet getMethod = new HttpGet();
		
		BasicHeader charsetHeader = new BasicHeader(HttpHeaders.ACCEPT_CHARSET, CHARSET);
		getMethod.addHeader(charsetHeader);
		
		return getMethod;
	}
	
	/**
	 * <p>Creates an HTTP POST request.</p>
	 * 
	 * @param uri The URI of the request.
	 * @param paramsEntity The parameters to be sent with the request.
	 * 
	 * @return an <code>HttpPost</code> object.
	 */
	private HttpPost createPostMethod(URI uri, StringEntity paramsEntity) {
		
		HttpPost postMethod = new HttpPost(uri);
		postMethod.setEntity(paramsEntity);
		BasicHeader charsetHeader = new BasicHeader(HttpHeaders.ACCEPT_CHARSET, CHARSET);
		BasicHeader contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, MIME_TYPE_FORM);
		postMethod.addHeader(charsetHeader);
		postMethod.addHeader(contentTypeHeader);
		
		return postMethod;
	}

	/**
	 * <p></p>
	 * 
	 * @param uriString
	 * @return
	 * @throws URISyntaxException 
	 */
	private URI createRequestURI(String uriString) throws URISyntaxException {
		
		//	Create the URL of the request.
		String context = this.properties.getProperty(KEY_CONTEXT);
		String host = this.properties.getProperty(KEY_HOST);
		String port = this.properties.getProperty(KEY_PORT);		
		String path = context + "/" + uriString + ".xml";
		URI uri = new URI(PROTOCOL + "://" + host + ":" + port + path);
		
		return uri;
	}

	/**
	 * @return the jsessionCookie
	 */
	public String getJsessionCookie() {
		return jsessionCookie;
	}


	/**
	 * @param jsessionCookie the jsessionCookie to set
	 */
	public void setJsessionCookie(String jsessionCookie) {
		this.jsessionCookie = jsessionCookie;
	}


	public UserInfoResponse findUserByUId(String uid) throws InvalidParameterException, ParseException, UserNotFoundException, ClientException {
		String method = "findUserByUId():  ";
		logger.info("BEFORE - " + method + " - " + uid + " - ");
		long startMilliseconds = System.currentTimeMillis();
		
		// Validating parameters
		if ((uid == null) || ("".equals(uid.trim()))) {
			String msg = "Missing 'uid' parameter.";
			logger.error("EXCEPTION - " + method + " - " + uid + " - " + msg);
			throw new InvalidParameterException(msg);
		}
		
		String uriString = "user/uid/" + uid;
		URI uri;
		try {
			uri = createRequestURI(uriString);
		} catch (URISyntaxException use) {
			String msg = "There was a URI syntax error.";
			logger.error(method + msg, use);
			throw new ClientException(msg, use);
		}
		
		HttpGet getMethod = createGetMethod();
		getMethod.setURI(uri);
		
		HttpClient httpClient = null;		
		try {
		
			// Make http request
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				
				String env = this.properties.getEnv();
				
				//	For 'local' environments to avoid SSL issue.
				if ("local".equals(env)) {
					logger.debug(method + "Handling local environment...");
					HttpClient wrappedClient = wrapHttpClient(httpClient);
					httpResponse = wrappedClient.execute(getMethod);
					if (httpResponse != null) {
						logger.debug(method + httpResponse.toString());
					}
					else {
						logger.debug(method + "Wrapped response was 'null'.");
					}
				}
				else {					
					httpResponse = httpClient.execute(getMethod);
				}
			} catch (ClientProtocolException cpe) {
				String msg = "There was an error in the HTTP protocol.";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + uid + " - " + msg + " - " + duration + " milliseconds.", cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + uid + " - " +  msg + " - " + duration + " milliseconds.", ioe);
				throw new ClientException(msg, ioe);
			} catch (KeyManagementException kme) {
				String msg = "There was an SSL key management error in the client.";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + uid + " - " +  msg + " - " + duration + " milliseconds.", kme);
				throw new ClientException(msg, kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The requested cryptographic algorithm is not available to the client.";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + uid + " - " +  msg + " - " + duration + " milliseconds.", nsae);
				throw new ClientException(msg, nsae);
			}
			
			if (httpResponse == null) {
				String msg = "Error when making http call to LSLoginService";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + uid + " - " +  msg);
				throw new ClientException(msg);
			}
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				logger.debug(method + "HTTP Status:  " + statusCode);
				throw new ClientException("Error when making http call to LSLoginService. HTTP Status: " + statusCode);
			}
			
			//	Parsing response
			Document document = null;
			try {
				document = parseResponse(httpResponse);
	
			} catch (IllegalStateException ise) {
				String msg = "Error occured when parsing response from server. ";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + uid + " - " +  msg + " - " + duration + " milliseconds.");
				throw new ClientException(msg, ise);
			} catch (DocumentException de) {
				String msg = "Error occured when parsing response from server. ";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + uid + " - " +  msg + " - " + duration + " milliseconds.");
				throw new ClientException(msg, de);
			} catch (IOException ioe) {
				String msg = "There was an error when sending http request to LSLoginService.";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + uid + " - " +  msg + " - " + duration + " milliseconds.");
				throw new ClientException(msg, ioe);
			}
			
			//	If we somehow received a 'null' response, throw an exception.
			if (document == null) {
				String msg = "The response from Login Service was 'null'";
				long endMilliseconds = System.currentTimeMillis();
				long duration = endMilliseconds - startMilliseconds;
				logger.error("EXCEPTION - " + method + " - " + uid + " - " +  msg + " - " + duration + " milliseconds.");
				throw new ClientException(msg);
			}
			
			//	If the response is present and equals "SUCCESS", create the UserInfoResponse.
			Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
			if (responseCodeNode != null) {
				if ("SUCCESS".equals(responseCodeNode.getText())) {
					UserInfoResponse response = new UserInfoResponse(document);
					long endMilliseconds = System.currentTimeMillis();
					long duration = endMilliseconds - startMilliseconds;
					logger.info("AFTER - " + method + " - " + uid + " - " + duration + " milliseconds.");
					return response;
				}
				else {
					//	TODO:  Handle other <responseCode> responses when they are added.
				}
			}
			//	Handle the <errorCode> responses.
			else {
				Node errorCodeNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_CODE);
				if (errorCodeNode == null) {
					throw new ClientException("Error code was empty.");
				}
				String errorCode = errorCodeNode.getText();
				
				Node errorMessageNode = document.selectSingleNode(ObjectFactory.NODE_ERROR_MESSAGE);
				String errorMessage = null;
				if (errorMessageNode == null) {
					errorMessage = "";
				} else {
					errorMessage = errorMessageNode.getText();
				}
				
				//	Throw the appropriate exception based on the error response code.
				switch(ResponseCode.valueOf(errorCode)) {
					case INVALID_PARAMETER:
						throw new InvalidParameterException(errorMessage);
					
					case USER_NOT_FOUND:
						throw new UserNotFoundException(errorMessage);
												
					default:
						throw new ClientException(errorMessage);
				}
			}
			
			return null;
		}
		finally {
			//	Close the HTTP connection and other resources.  Otherwise, they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
	}

}

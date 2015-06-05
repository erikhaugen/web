/**
 * 
 */
/**
 * @author Gurmeet Kalra
 *
 */
package com.livescribe.auth.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

import com.livescribe.auth.client.config.ClientProperties;
import com.livescribe.auth.client.exception.ClientException;
import com.livescribe.auth.client.exception.InvalidParameterException;


/**
 * <p>This class basically provides the package to be included by the client service</p>
 * 
 * @author Gurmeet S. Kalra
 * @version 1.0
 */
public class DigestClient {

	private static Logger logger = Logger.getLogger(DigestClient.class.getName());

	private static final String HEADER_AUTHENTICATE = "WWW-Authenticate";
	private static final String HEADER_AUTHORIZATION = "Authorization";
	private static final String HEADER_AUTH_DIGEST = "Digest";
	private static final String HEADER_AUTH_USERNAME = "username";

	private static final String URL_TOKENSERVIVE_CONTEXT = "/services/lstokenservice";
	private static final String URL_AUTH_VALIDATE = "/auth/validateDigest/";
	private static final String URL_AUTH_GETNONCE = "/auth/getNonce/";

	private static final String CHARSET			= "UTF-8";
	private static final String MIME_TYPE_FORM	= "application/x-www-form-urlencoded";
	
	private static final String KEY_TOKEN_HOST		= "tokenservice.host";
	private static final String KEY_TOKEN_CONTEXT	= "tokenservice.context";
	private static final String KEY_TOKEN_PATH		= "tokenservice.path";
	
	private ClientProperties properties = null;
    private String jsessionCookie;

	
	/**
	 * Constructor of the authenticate object which starts a thread on first usage of this object
	 * 
	 */
	public DigestClient() {
		
        final String METHOD_NAME = "DigestClient(): ";

        // Client related only

		InputStream in = this.getClass().getClassLoader().getResourceAsStream("tokenclient.properties");
		properties = new ClientProperties();
		try {
			properties.load(in);
			in.close();
			logger.debug(METHOD_NAME + "Read in " + properties.size() + " properties.");
			logger.debug(METHOD_NAME + properties.toString());
		} catch (IOException e) {
			logger.error(METHOD_NAME + "Failed to read properties file.");
			try {
				in.close();
			} catch (IOException ioe) {
				logger.error(METHOD_NAME + "IOException thrown when attempting to close InputStream.", ioe);
			}
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
					logger.error(METHOD_NAME + "IOException thrown when attempting to close InputStream.", ioe);
				}
			}
		}
	}

    
    // The aim of this function is extract the various pieces of name value pairs
    // send by the client for authorization of the pen messages
	private HashMap<String, String> parseAuthHeader(String authHeader)
	{
        final String METHOD_NAME = "parseAuthHeader";
        
        HashMap<String, String> authData = new HashMap<String, String>();

        String headerStringWithoutScheme = authHeader.substring(authHeader.indexOf(" ") + 1).trim();
        String keyValueArray[] = headerStringWithoutScheme.split(",");
        for (String keyval : keyValueArray) {
            if (keyval.contains("=")) {
                String key = keyval.substring(0, keyval.indexOf("="));
                String value = keyval.substring(keyval.indexOf("=") + 1);
                authData.put(key.trim(), value.replaceAll("\"", "").trim());
            }
        }

		logger.info(METHOD_NAME + ": AUTH data received raw=" + authHeader + ", " + HEADER_AUTH_USERNAME + "=" + authData.get(HEADER_AUTH_USERNAME));

		return authData;
	}
	
	/**
	 * <p>Creates an HTTP POST request.</p>
	 * 
	 * @param uri The URI of the request.
	 * @param headerName The name of parameters to be sent with the request header.
	 * @param headerValue The value of parameters to be sent with the request header.
	 * 
	 * @return an <code>HttpPost</code> object.
	 */
	private HttpPost createPostMethod(URI uri, String headerName, String headerValue) {
		
		HttpPost postMethod = new HttpPost(uri);
		BasicHeader charsetHeader = new BasicHeader(HttpHeaders.ACCEPT_CHARSET, CHARSET);
		BasicHeader contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, MIME_TYPE_FORM);
		postMethod.addHeader(charsetHeader);
		postMethod.addHeader(contentTypeHeader);
		if (null != headerName && null != headerValue)
			postMethod.addHeader(headerName, headerValue);
		return postMethod;
	}

	/**
	 * <p>Returns a nonce <code>String</code> to be used in HTTP digest authentication.</p>
	 * 
	 * @param displayId The 14-character alphanumeric ID of the pen.  (e.g.  <code>AYE-APS-DHH-SI</code>)
	 * 
	 * @return a nonce <code>String</code> to be used in HTTP digest authentication.
	 * 
	 * @throws InvalidParameterException if a NULL or empty display ID is provided.
	 * @throws ClientException 
	 */
	public String getAuthCurrentNonce(String displayId) throws InvalidParameterException, ClientException {
		
		String method = "getAuthCurrentNonce(" + displayId  + "):  ";
		
		if ((displayId == null) || (displayId.isEmpty())) {
			String msg = "Required 'display ID' parameter was missing or invalid.";
			logger.info(method + msg);
			throw new InvalidParameterException(msg);
		}
		
		//	Create the URI to Token Service.
		String host = this.properties.getProperty(KEY_TOKEN_HOST);
		String context = this.properties.getProperty(KEY_TOKEN_CONTEXT);
		String path = this.properties.getProperty(KEY_TOKEN_PATH);
		StringBuilder sb = new StringBuilder();
		sb.append("https://").append(host).append(context).append(path);
		sb.append("/").append(displayId);
		logger.debug(method + sb.toString());
		
		URI uri = null;
		try {
			uri = new URI(sb.toString());
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}

		//	Create the HTTP GET request.
		HttpGet httpGet = new HttpGet();
		httpGet.setURI(uri);
		
		HttpClient httpClient = null;
		try {
			//	Send the request.
			HttpResponse httpResponse = null;
			try {
				httpClient = new DefaultHttpClient();
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException cpe) {
				String msg = "";
				logger.error(method + msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "IOException thrown when attempting to contact Token Service.";
				logger.error(method + msg, ioe);
				throw new ClientException(msg, ioe);
			}

			if (httpResponse == null) {
				throw new ClientException("Error when making http call to LS Token Service");
			}
			
			//	Find the nonce in the header.
			Header[] headers = httpResponse.getHeaders(HEADER_AUTHENTICATE);
			
			if (headers == null) {
				String msg = "No HTTP headers found named 'WWW-Authenticate'.";
				logger.error(method + msg);
				throw new ClientException(msg);
			}
			
			if (headers.length > 1) {
				String msg = "More than one HTTP header named 'WWW-Authenticate' was found.";
				logger.error(method + msg);
				throw new ClientException(msg);
			}
			
			String nonce = headers[0].getValue();
			
			//get Set-cookie header
			headers = httpResponse.getHeaders("Set-Cookie");
            for (Header header : headers) {
                String jsessionCookie = header.getValue();
                if (jsessionCookie.contains("JSESSIONID")) {
                    this.setJsessionCookie(jsessionCookie.substring(0, jsessionCookie.indexOf(";") + 1));
                }
            }
			
			return nonce;
		}
		finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	/**
	 * <p></p>
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param deviceId
	 * 
	 * @return
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public int validateAuthorization2(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String deviceId)
			throws URISyntaxException, IOException
	{
		final String METHOD_NAME = "validateAuthorization():  ";

		// ensure the original request is not processed
		int responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		
		logger.debug(METHOD_NAME + "Entered");
		
		boolean foundAuthData = false;
		
		String authHeader = httpRequest.getHeader(DigestClient.HEADER_AUTHORIZATION);
		
		if (!StringUtils.isBlank(authHeader) && authHeader.startsWith(HEADER_AUTH_DIGEST)) {
			logger.info("DIGEST Authorization PRESENT in header ");
			// parse the values of the Authentication header into a hash map
			HashMap<String, String> authData = parseAuthHeader(authHeader);
			deviceId = authData.get(HEADER_AUTH_USERNAME);
			if (!StringUtils.isBlank(deviceId)) {
				
				foundAuthData = true;		
			}
		}

		// extract url data from original message to create the internal query url
		String scheme = httpRequest.getScheme();             // http
		String hostName = httpRequest.getServerName();     // hostname.com
		if (null != properties)
			hostName = properties.getProperty(KEY_TOKEN_HOST);

		String url = scheme + "://" + hostName;
		URI uri = null;

		// did have the authorization data in original message?
		if (foundAuthData) {
			logger.info(METHOD_NAME + "Authorization FOUND in header - validating... ");
			
			responseCode = HttpServletResponse.SC_OK;
			
			// send the message to the token service to validate this data
			url += URL_TOKENSERVIVE_CONTEXT + URL_AUTH_VALIDATE + deviceId + ".xml";
			uri = new URI(url);

			HttpPost postMethod = createPostMethod(uri, DigestClient.HEADER_AUTHORIZATION, authHeader);

			HttpResponse vdResponse = null;
			HttpClient client = null;
			try {
				client = new DefaultHttpClient();
				vdResponse = client.execute(postMethod);

				if (null != vdResponse) {
					
					// check what is returned
					if (vdResponse.containsHeader(DigestClient.HEADER_AUTHENTICATE)) {

						// authorization failed
						responseCode = HttpServletResponse.SC_UNAUTHORIZED;

						// extract from tokenservice response and transfer it to the original message response
						Header[] headers = vdResponse.getHeaders(DigestClient.HEADER_AUTHENTICATE);
						httpResponse.addHeader(DigestClient.HEADER_AUTHENTICATE, headers[0].getValue());
						logger.info(METHOD_NAME + "FAILURE RETURN " + DigestClient.HEADER_AUTHENTICATE + "=" + headers[0].getValue() + ", response=" + vdResponse.toString());
					} else {
						
						// authorization passed
						responseCode = HttpServletResponse.SC_OK;
						// the original request should be processed NOW
					}
				} else {
					logger.error(METHOD_NAME + "SERIOUS: Post call to " + url + " does NOT give RESPONSE - Unlikely Situation. ONLY if TOKEN SERVICE DOES NOT RESPOND");
				}

			} catch (Exception ex) {
				// set error
				responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
				logger.error(METHOD_NAME + "Post call to " + url + " throw error msg=" + ex.getMessage());

			} finally {
				//	Close the HTTP connection and other resources.  Otherwise,
				//	they will be held on to indefinitely.
				client.getConnectionManager().shutdown();
				client = null;
			}

			// finally set the return code with the response to be unauthorized - DON"T SET HERE
			//httpResponse.sendError(responseCode);
			
		} else {
			logger.info("Authorization NOT in header - Sending for Nonce... ");
			
			// default return code with the response to be unauthorized in this block
			responseCode = HttpServletResponse.SC_UNAUTHORIZED;
					
			// get that info from the token service
			url += URL_TOKENSERVIVE_CONTEXT + URL_AUTH_GETNONCE + deviceId + ".xml";
			uri = new URI(url);

			HttpPost postMethod = createPostMethod(uri, null, null);

			HttpResponse gnResponse = null;
			HttpClient client = null;
			try {
				// get reply from get nonce call
				client = new DefaultHttpClient();
				gnResponse = client.execute(postMethod);
				if (gnResponse != null) {
					logger.debug(METHOD_NAME + gnResponse.toString());

					// extract from tokenservice response and transfer it to the original message response
					//String authenticate = httpRequest.getHeader(DigestClient.HEADER_AUTHENTICATE);
					Header[] headers = gnResponse.getHeaders(DigestClient.HEADER_AUTHENTICATE);
					httpResponse.addHeader(DigestClient.HEADER_AUTHENTICATE, headers[0].getValue());
					logger.debug(METHOD_NAME + "header is " + headers[0].getValue());
				}
				else {
					logger.debug(METHOD_NAME + "Response to " + url + " is 'null'. Returning internal service not available.");

					responseCode = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
				}
			} catch (Exception ex) {
				// set error
				responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
				logger.error(METHOD_NAME + "Post call to " + url + " throw error msg=" + ex.getMessage());
			} finally {
				//	Close the HTTP connection and other resources.  Otherwise,
				//	they will be held on to indefinitely.
				client.getConnectionManager().shutdown();
				client = null;
			}

			// finally set the return code with the response to be unauthorized - DON"T SET HERE
			//httpResponse.sendError(responseCode);
		}

		logger.debug(METHOD_NAME + "Exited with response=" + responseCode + ". Send POST request to: " + uri.toString());
		return responseCode;
	}

	public boolean validateAuthorization(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String deviceId)
			throws URISyntaxException, IOException
	{
		int responseCode = validateAuthorization2(httpRequest, httpResponse, deviceId);
		return (HttpServletResponse.SC_OK == responseCode);
	}


    public String getJsessionCookie() {
        return jsessionCookie;
    }


    public void setJsessionCookie(String jsessionCookie) {
        this.jsessionCookie = jsessionCookie;
    }


}
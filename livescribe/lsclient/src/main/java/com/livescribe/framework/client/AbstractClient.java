/**
 * 
 */
package com.livescribe.framework.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.http.HttpStatus;

import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.web.response.ErrorResponse;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public abstract class AbstractClient {

	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	protected static final String KEY_HOST			= "host";
	protected static final String KEY_PORT			= "port";
	protected static final String KEY_CONTEXT		= "context";
	protected static final String PROTOCOL			= "https";
	protected static final String CHARSET			= "UTF-8";
	protected static final String MIME_TYPE_XML		= "application/xml";
	protected static final String MIME_TYPE_FORM	= "application/x-www-form-urlencoded";

	protected static final String XPATH_OUTPUT_PATH			= "/response/path";
	protected static final String XPATH_ERROR_RESPONSE_CODE	= "/response/errorCode";
	protected static final String XPATH_ERROR_MESSAGE		= "/response/message";
	protected static final String NODE_RESPONSE_CODE		= "responseCode";
	protected static final String NODE_ERROR_CODE			= "errorCode";

	protected ClientProperties properties;
	protected String jsessionCookie;

	/**
	 * <p></p>
	 *
	 */
	public AbstractClient() {
		
	}

	/**
	 * <p></p>
	 *
	 * @param propertiesFilename The name of the property file to load.
	 * 
	 * @throws IOException if client properties could not be loaded. 
	 */
	public AbstractClient(String propertiesFilename) throws IOException {
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(propertiesFilename);
		properties = new ClientProperties();
		properties.load(in);
		in.close();
		logger.debug("Read in " + this.properties.size() + " properties.");
	}
	
	/**
	 * <p></p>
	 * 
	 * @param is The InputStream of a specific property file to load
	 * 
	 * @throws IOException if client properties could not be loaded. 
	 */
	public AbstractClient(InputStream is) throws IOException {
	
		properties = new ClientProperties();
		try {
			properties.load(is);
		} finally {
			is.close();
		}
		logger.debug("Read in " + this.properties.size() + " properties.");
	}
	
	protected ErrorResponse parseErrorResponse(String responseXml) throws XStreamException {
		
		XStream xStream = new XStream(new DomDriver());
		xStream.alias("response", ErrorResponse.class);
		xStream.autodetectAnnotations(true);		//	This will not recognize the XStreamAlias('errorCode') annotation, for some reason. 
		ErrorResponse response = (ErrorResponse)xStream.fromXML(responseXml);
		return response;
	}
	
	/**
	 * <p>Parses an <code>HttpResponse</code> into a DOM4J XML <code>Document</code>.</p>
	 * 
	 * @param response the <code>HttpResponse</code> to parse.
	 * 
	 * @return a DOM4J XML <code>Document</code>.
	 * 
	 * @throws DocumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws ClientException 
	 */
	protected Document parseResponse(HttpResponse response) throws DocumentException, IllegalStateException, IOException, ClientException {
		
		String method = "parseResponse():  ";
		logger.debug(method + response.getStatusLine());

		Document document = null;
		
		int code = response.getStatusLine().getStatusCode();
		if (HttpStatus.OK.value() != code) {
			String respMsg = response.getStatusLine().getReasonPhrase();
			String msg = "Service returned HTTP " + code + ":  " + respMsg;
			logger.error(method + msg);
			throw new ClientException(msg);
		}
		
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream in = entity.getContent();
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(in, CHARSET));
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(buffReader);			
		}
		return document;
	}
	
	/**
	 * <p></p>
	 * 
	 * @return
	 * @throws ClientException 
	 */
	protected HttpResponse send(HttpClient client, HttpUriRequest request) throws ClientException {
		
		String method = "send():  ";
		
		logger.info("BEFORE - " + method);
		long start = System.currentTimeMillis();
		
		HttpResponse response = null;

		//	Wrap the client to avoid SSL errors in local environment.
		String env = this.properties.getEnv();
		if ("local".equals(env)) {
			try {
				response = sendLocalRequest(client, request);
			} catch (KeyManagementException kme) {
				String msg = "An error occurred involving the SSL key.";
				logger.error(method + " - " + msg, kme);
				throw new ClientException(kme);
			} catch (NoSuchAlgorithmException nsae) {
				String msg = "The SSL algorithm requested was not avaliable.";
				logger.error(method + " - " + msg, nsae);
				throw new ClientException(nsae);
			} catch (ClientProtocolException cpe) {
				String msg = "An error in the HTTPS protocol occurred.";
				logger.error(method + " - " + msg, cpe);
				throw new ClientException(cpe);
			} catch (IOException ioe) {
				String msg = "An IOException occurred when sending request to " + request.getURI();
				logger.error(method + " - " + msg, ioe);
				throw new ClientException(ioe);
			}
			
//			logger.debug("Completed sending " + request.getMethod() + " request to: " + request.getURI());
//			
//			long end = System.currentTimeMillis();
//			long duration = end - start;
//			logger.info("AFTER - " + method + " - Completed in " + duration + " milliseconds.");
//			
//			return response;
		}
		else {
			try {
				response = client.execute(request);
			} catch (ClientProtocolException cpe) {
				String msg = "An error in the HTTPS protocol occurred.";
				logger.error(method + " - " + msg, cpe);
				throw new ClientException(cpe);
			} catch (IOException ioe) {
				String msg = "An IOException occurred when sending request to " + request.getURI();
				logger.error(method + " - " + msg, ioe);
				throw new ClientException(ioe);
			}
		}
		
		if (response != null) {
			logger.debug(method + " - Received HTTP response:  " + response.toString());
		}
		else {
			logger.debug(method + "- HTTP response was 'null'.  Returning 'null'.");
		}

		logger.debug("Completed sending " + request.getMethod() + " request to: " + request.getURI());
		
		long end = System.currentTimeMillis();
		long duration = end - start;
		logger.info("AFTER - " + method + " - Completed in " + duration + " milliseconds.");
		
		return response;
	}

	/**
	 * <p></p>
	 * 
	 * @param client
	 * @param request
	 * @param method
	 * 
	 * @return
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	protected HttpResponse sendLocalRequest(HttpClient client, HttpUriRequest request) throws NoSuchAlgorithmException, KeyManagementException, IOException, ClientProtocolException {
		
		String method = "sendLocalRequest():  ";
		
		logger.debug(method + "Handling 'local' environment...");
		HttpClient wrappedClient = wrapHttpClient(client);
		
		if (wrappedClient == null) {
			logger.debug("wrappedClient was 'null'.");
		}
		
		HttpResponse response = wrappedClient.execute(request);
		if (response != null) {
//			logger.debug(method + response.toString());
		}
		else {
			logger.debug(method + "Wrapped response was 'null'.");
		}
		return response;
	}

	/**
	 * <p>Constructs a URL from the configured client properties file, 
	 * handling both direct and indirect hosts.</p>
	 * 
	 * <p>If a port is configured, it will verify the port is a number
	 * and make use of it in the URL.&nbsp;&nbsp;If the port is 443, 
	 * <code>https://</code> is prepended to the string instead.</p>
	 * 
	 * <p>The resulting string will be terminated with a &apos;/&apos; 
	 * character.</p>
	 * 
	 * @return a base URL string for use by a Client.
	 */
	protected String createBaseUrlString() {
		
		String method = "createUrlString()";
		
		//	Create the URL of the request.
		String context = this.properties.getProperty(KEY_CONTEXT);
		String host = this.properties.getProperty(KEY_HOST);
		String port = this.properties.getProperty(KEY_PORT);

		//	If a port number is configured, use that as part of the URL.
		int portInt = -1;
		if ((port != null) && (!port.isEmpty())) {
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

		return uriString.toString();
	}
	
	/**
	 * <p>Creates an HTTP DELETE request.</p>
	 * 
	 * <p>This can be used for URLs where the parameters are part of the path.</p>
	 * 
	 * @return an <code>HttpDelete</code> object.
	 */
	protected HttpDelete createDeleteMethod(URI uri) {
		
		HttpDelete deleteMethod = new HttpDelete(uri);
		BasicHeader charsetHeader = new BasicHeader(HttpHeaders.ACCEPT_CHARSET, CHARSET);
		BasicHeader jsessionHeader = new BasicHeader("Cookie", jsessionCookie);
		deleteMethod.addHeader(charsetHeader);
		deleteMethod.addHeader(jsessionHeader);
		
		return deleteMethod;
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
	protected HttpGet createGetMethod(URI uri) {
		
		HttpGet getMethod = new HttpGet(uri);
		
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
	 * @throws UnsupportedEncodingException 
	 */
	protected HttpPost createPostMethod(URI uri, List<NameValuePair> params) throws UnsupportedEncodingException {
		
		//	URL-Encode the parameters.
		String paramStr = URLEncodedUtils.format(params, CHARSET);
		
		//	Remove any 'password' parameter values from possible logging.
		processParamsForLogging(params);
		StringEntity paramsEntity = new StringEntity(paramStr);
		
		HttpPost postMethod = new HttpPost(uri);
		postMethod.setEntity(paramsEntity);
		BasicHeader charsetHeader = new BasicHeader(HttpHeaders.ACCEPT_CHARSET, CHARSET);
		BasicHeader contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, MIME_TYPE_FORM);
		postMethod.addHeader(charsetHeader);
		postMethod.addHeader(contentTypeHeader);
		
		return postMethod;
	}

	/**
	 * <p>Wraps an <code>HttpClient</code> with a <code>X509TrustManager</code> that 
	 * will accept invalid SSL certificates.</p>
	 * 
	 * This is to avoid "javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated" 
	 * errors on local development machines.
	 * 
	 * NOTE:  Must only be used in local environments!
	 * 
	 * @param client
	 * 
	 * @return an <code>HttpClient</code> that will accept all SSL certificates.
	 * 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	protected HttpClient wrapHttpClient(HttpClient client) throws NoSuchAlgorithmException, KeyManagementException {
		
		String method = "wrapHttpClient():  ";
		
		logger.debug(method + "Creating new X509TrustManager.");
		
		X509TrustManager tm = new X509TrustManager() {
			
			public boolean isTrusted(final X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
			
			public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			}
			
			public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			}
			
			public X509Certificate[] getAcceptedIssuers() {
				
				return null;
			}
		};
		
		//	TLS is the successor to SSL, but they use the same SSLContext.
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(null, new TrustManager[]{tm}, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		
		ClientConnectionManager ccm = client.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", 443, ssf));

		logger.debug(method + "Creating wrapped HttpClient.");
		HttpParams params = client.getParams();
		HttpClient wrappedClient = null;
		if (params == null) {
			logger.debug("params was 'null'");
			wrappedClient = new DefaultHttpClient(ccm);
		}
		else {
			logger.debug("params was NOT 'null'.");
			wrappedClient = new DefaultHttpClient(ccm, client.getParams());
		}

		return wrappedClient;
	}

	protected String processParamsForLogging(List<NameValuePair> params) {
		
		if (params.size() == 0) {
			return "";
		}
		
		StringBuffer buf = new StringBuffer();
		
		for (NameValuePair pair : params) {
			buf.append(pair.getName());
			buf.append("=");
			if (pair.getName().toLowerCase().contains("password")) {
				buf.append("xxx");
			} else {
				buf.append(pair.getValue());
			}
			buf.append("&");
		}
		
		return buf.substring(0, buf.length() - 1);
	}

	/**
	 * <p>Reads the content of the given <code>HttpEntity</code> into a
	 * <code>String</code>.</p>
	 * 
	 * @param entity The <code>HttpEntity</code> to read.
	 * 
	 * @return a string representation of the content.&nbsp;&nbsp;Returns 
	 * &apos;null&apos; if an error occurs.
	 */
	protected String readEntityIntoString(HttpEntity entity) {
		
		String method = "readEntityIntoString()";
		
		StringBuilder sb = new StringBuilder();
		InputStream in = null;
		try {
			in = entity.getContent();
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(in, CHARSET));
			String line = null;
			while ((line = buffReader.readLine()) != null) {
				sb.append(line);
			}
			
		} catch (IllegalStateException ise) {
			String msg = "IllegalStateException thrown when reading content from HttpEntity of HttpResponse.";
			logger.error(method + " - " + msg, ise);
		} catch (IOException ioe) {
			String msg = "IOException thrown when reading content from HttpEntity of HttpResponse.";
			logger.error(method + " - " + msg, ioe);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					//	IGNORE.
				}
			}
		}
		
		return sb.toString();
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

}

/**
 * 
 */
package com.livescribe.admin.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.livescribe.admin.client.config.ClientProperties;
import com.livescribe.aws.tokensvc.auth.DigestAuthentication;
import com.livescribe.framework.client.AbstractClient;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.version.response.VersionResponse;


/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @author <a href="mailto:MNaqvi@livescribe.com">Mohammad M. Naqvi</a>
 * @version 1.0
 */
public class ShareServiceClient extends AbstractClient {

	private static final String CHARSET			= "UTF-8";
	private static final String PROTOCOL		= "http";

	private static final String KEY_USERNAME	= "username";
	private static final String KEY_REALM		= "realm";
	private static final String KEY_NONCE		= "nonce";
	private static final String KEY_OPAQUE		= "opaque";
	private static final String KEY_QOP			= "qop";
	private static final String KEY_URI			= "uri";

	private static final String METHOD_DELETE_DOCUMENTS_BY_UID	= "sync/delete/uid";
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p>Default class constructor.</p>
	 * 
	 * @throws IOException if an error occurs while reading the <code>client.properties</code> file.
	 * @throws ClientException  if an error occurs while reading the <code>client.properties</code> file.
	 */
	public ShareServiceClient(String jsessionCookie) throws ClientException, IOException {
		
		super("shareclient.properties");
		
	    this.jsessionCookie = jsessionCookie;
		
		logger.debug("Read in " + this.properties.size() + " properties.");
	}
	
	private Header createAuthHeader(Map<String, String> nonceParts) {
		
		String method = "createAuthHeader():  ";

		StringBuilder sb = new StringBuilder();

		sb.append("Digest ").append(KEY_USERNAME).append("=\"").append(nonceParts.get(KEY_USERNAME)).append("\", ");
		sb.append(KEY_REALM).append("=\"").append(nonceParts.get(KEY_REALM)).append("\", ");
		sb.append(KEY_NONCE).append("=\"").append(nonceParts.get(KEY_NONCE)).append("\", ");
		sb.append(KEY_URI).append("=\"").append(nonceParts.get(KEY_URI)).append("\", ");
//		sb.append(KEY_RESPONSE).append("=\"").append(nonceParts.get(KEY_RESPONSE)).append("\", ");
		sb.append(KEY_OPAQUE).append("=\"").append(nonceParts.get(KEY_OPAQUE)).append("\", ");
		sb.append(KEY_QOP).append("=").append(nonceParts.get(KEY_QOP)).append(", ");
		sb.append("nc=00000001, ");
		Date now = new Date();
		sb.append("cnonce=\"").append(now.getTime()).append("\"");
		
		logger.debug(method + "Created Digest: " + sb.toString());

		Header header = new BasicHeader(DigestAuthentication.HEADER_AUTHORIZATION, sb.toString());
		
		return header;
	}
	
	/**
	 * <p>Deletes a document identified by the given document ID.</p>
	 * 
	 * @param docId The unique ID of the document to delete.
	 * 
	 * @throws UnsupportedEncodingException 
	 * @throws URISyntaxException 
	 * @throws ClientException 
	 */
	public void deleteDocument(Map<String, String> nonceParts, String docId) throws UnsupportedEncodingException, URISyntaxException, ClientException {
		
		String method = "deleteDocument( Map: " + nonceParts.get(KEY_USERNAME) + " ):  ";
		
		//	Construct the URI.
		String context = this.properties.getProperty(KEY_CONTEXT);
		String host = this.properties.getProperty(KEY_HOST);

		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder.append(PROTOCOL).append("://").append(host);
		pathBuilder.append(context).append("/document/").append(docId);
		
		String uriString = pathBuilder.toString();
		URI uri = new URI(uriString);
		nonceParts.put(KEY_URI, uriString);
		
		logger.debug(method + "URI String:  " + uriString);
		
		HttpDelete deleteRequest = createDeleteMethod(uri);
		Header header = createAuthHeader(nonceParts);
		
		deleteRequest.addHeader(header);
		
		HttpClient httpClient = null;
		
		try {
			httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = null;
			
			try {
				httpResponse = httpClient.execute(deleteRequest);
				String content = readResponse(httpResponse);
				int responseCode = httpResponse.getStatusLine().getStatusCode();
				StringBuilder builder = new StringBuilder();
				if (responseCode != HttpServletResponse.SC_OK) {
					String codeMsg = "Response code from LS Share Service was:  " + responseCode;
					builder.append(codeMsg).append("\n");
					builder.append(content).append("\n");
					String msg = builder.toString();
					logger.debug(method + msg);
					throw new ClientException(msg);
				}
			} catch (ClientProtocolException cpe) {
				String msg = "An ClientProtocolException was thrown during communication with LS Share Service.";
				logger.error(method +  msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "An IOException was thrown during communication with LS Share Service.";
				logger.error(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			}
		}
		finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	/**
	 * <p>Deletes an <i>archived</i> document identified by the given displayId, guid, copy and archive number.</p>
	 * 
	 * @param nonceParts 
	 * @param penDisplayId displayId of the pen
	 * @param guid guid of the document to be deleted
	 * @param copy copy # of the document
	 * @param number archive number of the document
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 * @throws ClientException
	 * @author Mohammad M. Naqvi
	 */
	public void deleteArchivedDocument(Map<String, String> nonceParts, String penDisplayId, String guid, Long copy, Long number) throws UnsupportedEncodingException, URISyntaxException, ClientException {
		
	
		
		String method = "deleteArchivedDocument( Map: " + nonceParts.get(KEY_USERNAME) + " ):  ";
		
		//	Construct the URI.
		String context = this.properties.getProperty(KEY_CONTEXT);
		String host = this.properties.getProperty(KEY_HOST);

		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder.append(PROTOCOL).append("://").append(host);
		pathBuilder.append(context).append("/archive/deletenotebook");
		
		String uriString = pathBuilder.toString();
		nonceParts.put(KEY_URI, uriString);
		
		// Build Parameters string
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("GUID", guid));
		params.add(new BasicNameValuePair("copy", copy.toString()));
		params.add(new BasicNameValuePair("number", number.toString()));
		uriString = uriString + "?" + URLEncodedUtils.format(params, CHARSET);
		logger.debug(method + "FULL URI String =  " + uriString);
		
		URI uri = new URI(uriString);
		HttpDelete deleteRequest = createDeleteMethod(uri);

		// Set HTTP headers
		deleteRequest.addHeader(createAuthHeader(nonceParts));
		deleteRequest.addHeader(new BasicHeader("penserial", penDisplayId));

		HttpClient httpClient = null;
		try {
			httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = null;
			
			try {
				httpResponse = httpClient.execute(deleteRequest);
				String content = readResponse(httpResponse);
				int responseCode = httpResponse.getStatusLine().getStatusCode();
				StringBuilder builder = new StringBuilder();
				if (responseCode != HttpServletResponse.SC_OK) {
					String codeMsg = "Response code from LS Share Service was:  " + responseCode;
					builder.append(codeMsg).append("\n");
					builder.append(content).append("\n");
					String msg = builder.toString();
					logger.debug(method + msg);
					throw new ClientException(msg);
				}
			} catch (ClientProtocolException cpe) {
				String msg = "An ClientProtocolException was thrown during communication with LS Share Service.";
				logger.error(method +  msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "An IOException was thrown during communication with LS Share Service.";
				logger.error(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			}
		}
		finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	/**
	 * <p>Deletes all <code>Document</code> records, including <code>Page</code>s, 
	 * <code>Session</code>s, etc., owned by the user identified by the given UID 
	 * and created by the pen identified by the pen display ID found in the
	 * given <code>Map</code> of nonce parts.</p>
	 * 
	 * <p>The pen display ID is found in the <code>Map</code> using the 
	 * <code>username</code> key.</p>
	 *  
	 * @param nonceParts A <code>Map</code> of the key/value pairs of the nonce.
	 * @param uid The unique ID of the user&apos;s record in the <code>consumer.user</code> table.
	 * 
	 * @throws URISyntaxException 
	 * @throws ClientException 
	 */
	public void deleteDocumentsByUserAndPen(Map<String, String> nonceParts, String uid) throws URISyntaxException, ClientException {
		
		String method = "deleteDocumentsByUserAndPen(Map<String, String>, String)";
		
		String context = this.properties.getProperty(KEY_CONTEXT);
		String host = this.properties.getProperty(KEY_HOST);

		//	Construct the URI.
		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder.append(PROTOCOL).append("://").append(host);
		pathBuilder.append(context).append("/sync/delete/");
		pathBuilder.append(uid).append("/").append(nonceParts.get(KEY_USERNAME));
		String uriString = pathBuilder.toString();
		URI uri = new URI(uriString);
		nonceParts.put(KEY_URI, uriString);
		
		logger.debug(method + "URI String:  " + uriString);
		
		HttpDelete deleteRequest = createDeleteMethod(uri);
		Header header = createAuthHeader(nonceParts);
		
		deleteRequest.addHeader(header);
		
		issueDeleteRequest(deleteRequest);		
	}
	
	/**
	 * <p>Deletes all <code>Document</code> records, including <code>Page</code>s, 
	 * <code>Session</code>s, etc., owned by the user identified by the given UID 
	 * and created by the pen identified by the given pen display ID.</p>
	 * 
	 * <p>Calls {@link com.livescribe.web.lsshareservice.controller.ContentSyncController#deleteDocumentsByUserAndPen() ContentSyncController.deleteDocumentsByUserAndPen()}.</p>
	 * 
	 * @param uid The unique ID of the user&apos;s record in the <code>consumer.user</code> table.
	 * @param penDisplayId The 14-character display ID of the device.
	 * 
	 * @throws URISyntaxException
	 * @throws ClientException
	 * 
	 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
	 */
	public void deleteDocumentsByUserAndPen(String uid, String penDisplayId) throws URISyntaxException, ClientException {
		
		String method = "deleteByUserAndPen(String, String)";
		
		//	Construct the URI.
		String baseUrl = createBaseUrlString();
		StringBuilder uriBuilder = new StringBuilder(baseUrl);
		uriBuilder.append("sync/delete/");
		uriBuilder.append(uid).append("/").append(penDisplayId);
		String uriString = uriBuilder.toString();
		URI uri = new URI(uriString);

		logger.debug(method + "URI String:  " + uriString);
		
		HttpDelete deleteRequest = createDeleteMethod(uri);
		Header header = new BasicHeader(DigestAuthentication.HEADER_AUTHORIZATION, penDisplayId);
		
		deleteRequest.addHeader(header);
		
		HttpClient httpClient = null;
		try {
			
		} finally {
			//	Close the HTTP connection and other resources.  Otherwise,
			//	they will be held on to indefinitely.
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}
		issueDeleteRequest(deleteRequest);
	}
	
	/**
	 * <p>Deletes all documents owned by the user identified by the given UID.</p>
	 * 
	 * <p>Calls Share Service delete method mapped to <code>sync/delete/uid</code>.</p>
	 * 
	 * @param uid The unique ID of the user&apos;s record in the <code>consumer.user</code> table.
	 * 
	 * @throws URISyntaxException
	 * @throws ClientException
	 */
	public void deleteDocumentsByUid(String uid) throws URISyntaxException, ClientException {
		
		String method = "deleteDocumentsByUid()";
		
		String baseUrl = createBaseUrlString();
		StringBuilder uriBuilder = new StringBuilder(baseUrl);
		uriBuilder.append(METHOD_DELETE_DOCUMENTS_BY_UID);
		uriBuilder.append("/").append(uid);
		String uriString = uriBuilder.toString();
		URI uri = new URI(uriString);

		logger.debug(method + "URI String:  " + uriString);
		
		HttpDelete deleteRequest = createDeleteMethod(uri);
		Header header = new BasicHeader(DigestAuthentication.HEADER_AUTHORIZATION, "AYE-XXX-YYY-ZZ");
		
		deleteRequest.setHeader(header);
		
		issueDeleteRequest(deleteRequest);
	}
	
	/**
	 * <p>Returns version information of the Share Service.</p>
	 * 
	 * @return an object encapsulating version information.
	 * 
	 * @throws ClientException
	 */
	public VersionResponse getVersion() throws ClientException {
		
		String method = "getVersion():  ";
		
		//	Construct the URI.
		String context = this.properties.getProperty(KEY_CONTEXT);
		String host = this.properties.getProperty(KEY_HOST);
	
		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder.append(PROTOCOL).append("://").append(host);
		pathBuilder.append(context).append("/version.xml");
		
		String uriString = pathBuilder.toString();
		URI uri = null;
		try {
			uri = new URI(uriString);
		}
		catch (URISyntaxException use) {
			String msg = "URISyntaxException thrown when attempting create URI '" + pathBuilder.toString() + "'.";
			logger.error(method + msg);
			throw new ClientException(msg);
		}
		
		HttpGet getRequest = new HttpGet();
		getRequest.setURI(uri);
		BasicHeader jsessionHeader = new BasicHeader("Cookie", jsessionCookie);
		getRequest.addHeader(jsessionHeader);
		HttpClient httpClient = null;
				
		try {
			httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = null;
			VersionResponse version = null;
			try {
				httpResponse = httpClient.execute(getRequest);
				version = parseVersionResponse(httpResponse);
				return version;
			}
			catch(ClientProtocolException cpe) {
				String msg = "ClientProtocolException thrown when attempting issue GET request to URI '" + pathBuilder.toString() + "'.";
				logger.error(method + msg);
				throw new ClientException(msg);
			}
			catch(JAXBException jbe) {
				String msg = "JAXBException thrown when attempting to parse version response.";
				logger.error(method + msg);
				throw new ClientException(msg);
			}
			catch (IllegalStateException ise) {
				String msg = "IllegalStateException thrown when attempting to parse version response.";
				logger.error(method + msg);
				throw new ClientException(msg);
			}
			catch(IOException ioe) {
				String msg = "IOException thrown when attempting to issue GET request  to '" + pathBuilder.toString() + "' or parse version response.";
				logger.error(method + msg);
				throw new ClientException(msg);
			} catch (DocumentException de) {
				String msg = "DocumentException thrown when attempting to parse version response.";
				logger.error(method + msg);
				throw new ClientException(msg);
			}
		}
		finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * <p></p>
	 * 
	 * @param method
	 * @param deleteRequest
	 * 
	 * @throws ClientException
	 */
	private void issueDeleteRequest(HttpDelete deleteRequest) throws ClientException {
		
		String method = "issueDeleteRequest():  ";
		
		HttpClient httpClient = null;
		try {
			httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = null;
			
			try {
				httpResponse = httpClient.execute(deleteRequest);
				int responseCode = httpResponse.getStatusLine().getStatusCode();
				
				//--------------------------------------------------
				//	Read the response into an XML String.
				HttpEntity entity = httpResponse.getEntity();
				String responseXml = readEntityIntoString(entity);
				
				//	HTTP error occurred (response code != 200)
				if (responseCode != HttpServletResponse.SC_OK) {
					StringBuilder builder = new StringBuilder();
					String codeMsg = "Response code from LS Share Service was:  " + responseCode;
					String reason = httpResponse.getStatusLine().getReasonPhrase();
					builder.append(codeMsg).append("\n");
					builder.append(reason).append("\n");
					builder.append(responseXml).append("\n");
					String msg = builder.toString();
					logger.debug(method + msg);
					throw new ClientException(msg);
				}
				
				//	No HTTP error..
				//	Check for application internal error(s)
				if (!successResponse(responseXml)) {
					logger.error("Application Error encountered: " + responseXml);
					String errorMessage = extractMessage(responseXml);
					throw new ClientException(errorMessage);
				}
				//	No Errors 
				logger.debug(method + "responseXml = \n" + responseXml);
			} catch (ClientProtocolException cpe) {
				String msg = "An ClientProtocolException was thrown during communication with LS Share Service.";
				logger.error(method +  msg, cpe);
				throw new ClientException(msg, cpe);
			} catch (IOException ioe) {
				String msg = "An IOException was thrown during communication with LS Share Service.";
				logger.error(method +  msg, ioe);
				throw new ClientException(msg, ioe);
			}
		}
		finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/*
	 * Extracts and returns the message from error response XML string. 
	 */
	private String extractMessage(String content) {
		DocumentBuilder db = null;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Unable to extract error message from the response due to ParserConfigurationException");
			return "";
		}
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(content));

		org.w3c.dom.Document doc = null;
		try {
			doc = db.parse(is);
		} catch (SAXException e) {
			logger.error("Unable to extract error message from the response due to SAXException");
			return "";
		} catch (IOException e) {
			logger.error("Unable to extract error message from the response due to IOException");
			return "";
		}
		NodeList nodes = doc.getElementsByTagName("response");
		Element element = (Element) nodes.item(0);

		NodeList name = element.getElementsByTagName("message");
		String message = getCharacterDataFromElement((Element) name.item(0));
		return message;
	}

	private String getCharacterDataFromElement(Element message) {
		if (null == message) {
			return "";
		}
	    Node child = message.getFirstChild();
	    if (child instanceof CharacterData) {
	      CharacterData cd = (CharacterData) child;
	      return cd.getData();
	    }
	    return "";
	}

	private boolean successResponse(String content) {

		if (null == content) {
			return false;
		}
		return (content.contains("<responseCode>SUCCESS</responseCode>"));
	}

	private VersionResponse parseVersionResponse(HttpResponse httpResponse) throws JAXBException, IllegalStateException, IOException, DocumentException {
		
		HttpEntity entity = httpResponse.getEntity();
		InputStream in = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, CHARSET));
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(reader);
		
		VersionResponse response = new VersionResponse();
		String appName = document.selectSingleNode("/response/appName").getText();
		String version = document.selectSingleNode("/response/version").getText();
		String hudsonBuildNumber = document.selectSingleNode("/response/hudsonBuildNumber").getText();
		String svnRevision = document.selectSingleNode("/response/svnRevision").getText();
		String buildDate = document.selectSingleNode("/response/buildDate").getText();
		
		response.setAppName(appName);
		response.setVersion(version);
		response.setBuildDate(buildDate);
		response.setSvnRevision(svnRevision);
		response.setHudsonBuildNumber(hudsonBuildNumber);
		
		return response;
	}
	
	private String readResponse(HttpResponse httpResponse) throws IOException {
		
		HttpEntity entity = httpResponse.getEntity();
		InputStream in = null;
		try {
			in = entity.getContent();
		} catch (IllegalStateException ise) {
			ise.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
}

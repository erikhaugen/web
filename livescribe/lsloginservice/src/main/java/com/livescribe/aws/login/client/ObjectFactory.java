/**
 * 
 */
package com.livescribe.aws.login.client;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ObjectFactory {

	private static Logger logger = Logger.getLogger(ObjectFactory.class.getName());
	
	public	static	final	String	NODE_RESPONSE_CODE					= "//response/responseCode";
	public	static	final	String	NODE_ERROR_CODE						= "//response/errorCode";
	public	static	final	String	NODE_ERROR_MESSAGE					= "//response/message";
	public	static	final	String	NODE_LOGIN_TOKEN					= "//response/loginToken";
	public	static	final	String	NODE_USER							= "//response/user";
	public	static	final	String	NODE_UID							= "//response/uid";
	public	static	final	String	NODE_EMAIL_ADDRESS					= "//response/email";
	
	public	static	final	String	NODE_AUTHORIZATION_USERNAME			= "//response/enUsername";
	public	static	final	String	NODE_AUTHORIZATION_ID				= "//response/authorizationId";
	public	static	final	String	NODE_AUTHORIZATION_OAUTHACCESSTOKEN	= "//response/oauthAccessToken";
	public	static	final	String	NODE_AUTHORIZATION_PROVIDER			= "//response/provider";
	public	static	final	String	NODE_AUTHORIZATION_SHARDID 			= "//response/enShardId";
	public	static	final	String	NODE_AUTHORIZATION_EXPIRATION 		= "//response/expiration";
	public	static	final	String	NODE_AUTHORIZATION_CREATED	 		= "//response/created";
	public	static	final	String	NODE_AUTHORIZATION_LASTMODIFIED 	= "//response/lastModified";
	public	static	final	String	NODE_AUTHORIZATION_LASTMODIFIEDBY 	= "//response/lastModifiedBy";
	public	static	final	String	NODE_AUTHORIZATION_USERID 			= "//response/enUserId";
	public	static	final	String	NODE_IS_PRIMARY						= "//response/isPrimary";
	
	public	static	final	String	NODE_USERINFO_FIRST_NAME			= "//response/firstName";
	public	static	final	String	NODE_USERINFO_MIDDLE_NAME			= "//response/middleName";
	public	static	final	String	NODE_USERINFO_LAST_NAME				= "//response/lastName";
	public	static	final	String	NODE_USERINFO_GRADYEAR				= "//response/gradYear";
	public	static	final	String	NODE_USERINFO_BIRTHYEAR				= "//response/birthYear";
	public	static	final	String	NODE_USERINFO_MAJOR					= "//response/major";
	public	static	final	String	NODE_USERINFO_PHONE					= "//response/phone";
	public	static	final	String	NODE_USERINFO_ORGANIZATION			= "//response/organization";
	public	static	final	String	NODE_USERINFO_OCCUPATION			= "//response/occupation";
	public	static	final	String	NODE_USERINFO_SEX					= "//response/sex";
	public	static	final	String	NODE_USERINFO_UNIVERSITY			= "//response/university";
	public	static	final	String	NODE_USERINFO_LOCAL					= "//response/locale";
	public	static	final	String	NODE_USERINFO_ADDRESS_ADDRESS1		= "//response/address/address1";
	public	static	final	String	NODE_USERINFO_ADDRESS_ADDRESS2		= "//response/address/address2";
	public	static	final	String	NODE_USERINFO_ADDRESS_CITY			= "//response/address/city";
	public	static	final	String	NODE_USERINFO_ADDRESS_COUNTY		= "//response/address/county";
	public	static	final	String	NODE_USERINFO_ADDRESS_ZIP			= "//response/address/zip";
	public	static	final	String	NODE_USERINFO_ADDRESS_PROVINCE		= "//response/address/province";
	public	static	final	String	NODE_USERINFO_ADDRESS_STATE			= "//response/address/state";
	public	static	final	String	NODE_USERINFO_ADDRESS_COUNTRY		= "//response/address/country";
	public	static	final	String	NODE_USERINFO_AUTHORIZATIONS		= "//response/authorizations";
	public	static	final	String	NODE_USERINFO_AUTHORIZATIONS_RESPONSE			= "response";

	public	static	final	String	NODE_USERINFO_AUTHORIZATIONS_USERNAME			= "enUsername";
	public	static	final	String	NODE_USERINFO_AUTHORIZATIONS_OAUTHACCESSTOKEN	= "oauthAccessToken";
	public	static	final	String	NODE_USERINFO_AUTHORIZATIONS_PROVIDER			= "provider";
	public	static	final	String	NODE_USERINFO_AUTHORIZATIONS_SHARDID 			= "enShardId";
	public	static	final	String	NODE_USERINFO_AUTHORIZATIONS_EXPIRATION 		= "expiration";
	public	static	final	String	NODE_USERINFO_AUTHORIZATIONS_CREATED	 		= "created";
	public	static	final	String	NODE_USERINFO_AUTHORIZATIONS_LASTMODIFIED 		= "lastModified";
	public	static	final	String	NODE_USERINFO_AUTHORIZATIONS_LASTMODIFIEDBY 	= "lastModifiedBy";
	public	static	final	String	NODE_USERINFO_AUTHORIZATIONS_USERID 			= "enUserId";
	public	static	final	String	NODE_USERINFO_AUTHORIZATIONS_UID	 			= "uid";
	

	public static void main(String[] args) {
		
		
	}
	
	/**
	 * <p>Parses the given XML document into a domain class.</p>
	 * 
	 * If the response is an error response, this method will return 
	 * <code>null</code>.
	 * 
	 * @param document The XML document to parse.
	 * 
	 * @return
	 */
	public static Object parseDocument(Document document) {
		
		Node responseCodeNode = document.selectSingleNode(NODE_RESPONSE_CODE);
		if (responseCodeNode == null) {
			//	TODO:	Prepare error response here.  Throw Exception?
			//	TODO:	check for SUCCESS code
			logger.debug("codeNode was 'null'.");
			return null;
		}
		
		Element root = document.getRootElement();
		List<Element> children = root.elements();
		if (children.isEmpty()) {
			//	TODO:  Probably need to throw an exception here.
			return null;
		}
		//	If the response XML has only one element, it must be the 
		//	<responseCode> element here.
		else if (children.size() == 1) {
			String code = responseCodeNode.getText();
			return code;
		}
		
		Node uidNode = document.selectSingleNode(NODE_UID);
		if (uidNode == null) {
//			TODO:  Try a different XPath, or create error.  Throw Exception?
			logger.debug("userNode was 'null'.");
			return null;
		}
		else {
			String uid = uidNode.getText();
			return uid;
		}
	}
	
	/**
	 * parse XML Document and return the specified node
	 * 
	 * @param document XML document to parse
	 * @param node xpath expression of XML node which value will be returned
	 * 
	 * @return value of the specified node
	 */
	public static Object parseDocumentAndFindNode(Document document, String nodeXPath) {
		Node responseCodeNode = document.selectSingleNode(NODE_RESPONSE_CODE);
		if (responseCodeNode == null) {
			//	TODO:	Prepare error response here.  Throw Exception?
			//	TODO:	check for SUCCESS code
			logger.debug("codeNode was 'null'.");
			return null;
		}
		
		Element root = document.getRootElement();
		List<Element> children = root.elements();
		if (children.isEmpty()) {
			//	TODO:  Probably need to throw an exception here.
			return null;
		}
		//	If the response XML has only one element, it must be the 
		//	<responseCode> element here.
		else if (children.size() == 1) {
			String code = responseCodeNode.getText();
			return code;
		}
		
		Node node = document.selectSingleNode(nodeXPath);
		if (node == null) {
			logger.debug(nodeXPath + " was 'null'.");
			return null;
		}
		
		return node.getText();
	}
	
	/**
	 * <p>Parses an XML document to return the error that was received.</p>
	 * 
	 * @param document The XML document to parse.
	 * 
	 * @return
	 */
	public static Object parseError(Document document) {
		
		String method = "parseError():  ";
		
		Node errorNode = document.selectSingleNode(NODE_ERROR_CODE);
		if (errorNode == null) {
			logger.error(method + "No 'errorCode' element found.");
			return null;
		}
		String code = errorNode.getText();
		return code;
	}
}

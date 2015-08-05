/**
 * Created:  Sep 20, 2013 5:14:28 PM
 */
package com.livescribe.web.tools.webteamtool.util;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;
import com.livescribe.web.tools.webteamtool.config.AppProperties;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class Utils {

	private static Logger logger = Logger.getLogger(Utils.class);

	private static final String PROP_EN_USER_STORE_URL = "evernotebookkeeper.userstoreurl";
	private static final String PROP_EN_USER_AGENT = "evernotebookkeeper.useragent";
	private static final String USER_AGENT = "User-Agent";

	/**
	 * Gets an Evernote NoteStore client for a given authToken.
	 * 
	 * @param aAuthToken auth token.
	 * 
	 * @return EverNote NoteStore client.
	 * 
	 * @throws TException 
	 * @throws EDAMSystemException 
	 * @throws EDAMUserException 
	 * @throws Exception an exception while getting UserStore client 
	 */
	public static NoteStore.Client getEvernoteNoteStoreClient(String aAuthToken) throws EDAMUserException, EDAMSystemException, TException {
		
		AppProperties appProperties = AppProperties.getInstance();

		UserStore.Client userStoreClient = getEvernoteUserStoreClient();
		String noteStoreUrl = userStoreClient.getNoteStoreUrl(aAuthToken);

		THttpClient httpClientForNoteStore = new THttpClient(noteStoreUrl);
		httpClientForNoteStore.setCustomHeader(USER_AGENT, appProperties.getProperty(PROP_EN_USER_AGENT));

		return new NoteStore.Client(new TBinaryProtocol(httpClientForNoteStore));
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 * 
	 * @throws TTransportException
	 */
	public static UserStore.Client getEvernoteUserStoreClient() throws TTransportException {
		
		String method = "getEvernoteUserStoreClient()";
		
		logger.debug("BEGIN - " + method);			
		
		AppProperties appProperties = AppProperties.getInstance();

		if (appProperties == null) {
			logger.debug("AppProperties was 'null'!");
			return null;
		}
		
		THttpClient httpClientForENUserStore = new THttpClient(appProperties.getProperty(PROP_EN_USER_STORE_URL));
		httpClientForENUserStore.setCustomHeader(USER_AGENT, appProperties.getProperty(PROP_EN_USER_AGENT));
		TBinaryProtocol tUserStoreProt = new TBinaryProtocol(httpClientForENUserStore);

		return new UserStore.Client(tUserStoreProt);
	}
	
    public static String capitalizeName(String name) {
    	
    	if (name == null) {
    		return "";
    	}
    	
    	String elementName = null;
    	if (name.contains("_")) {
    		String[] words = name.split("_");
    		StringBuilder builder = new StringBuilder();
    		for (String word : words) {
    			builder.append(WordUtils.capitalize(word));
    		}
    		elementName = builder.toString();
    	}
    	else {
    		elementName = WordUtils.capitalize(name);
    	}
    	return elementName;
    }
    
    public static String parseReference(String reference) {
    	
    	if ((reference == null) || (reference.isEmpty())) {
    		return null;
    	}
    	
    	String typeName = null;
    	if (reference.contains(":")) {
    		String[] parts = reference.split(":");
    		typeName = parts[1];
    	}
    	
    	String name = null;
    	if (typeName.contains(".")) {
    		String[] pieces = typeName.split("\\.");
    		name = pieces[0];
    	} else {
    		name = typeName;
    	}
    	return name;
    }
}

/**
 * Created:  Aug 21, 2013 12:37:22 PM
 */
package com.livescribe.web.tools.webteamtool;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.userstore.Constants;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;
import com.livescribe.framework.lsconfiguration.AppProperties;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class OAuthTokenValidator {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String USER_AGENT = "User-Agent";
	private static final String PROP_KEY_EBK_USERSTORE_URL = "evernotebookkeeper.userstoreurl";
	private static final String PROP_KEY_EBK_USER_AGENT = "evernotebookkeeper.useragent";
	
	@Autowired
	private AppProperties appProperties;
	
	/**
	 * <p></p>
	 * 
	 */
	public OAuthTokenValidator() {
	}

	public boolean validate(String token) throws EDAMUserException, EDAMSystemException, TTransportException, TException, EDAMNotFoundException {
		
		NoteStore.Client noteStore = getNoteStoreByAuthToken(token);
		
		if (noteStore != null) {
			return true;
		}
		return false;
	}

	private UserStore.Client getEvernoteUserStore() throws TTransportException, TException {
		
		logger.info("BEFORE - getEvernoteUserStore()");			
		
		String userAgent = appProperties.getProperty(PROP_KEY_EBK_USER_AGENT);
		THttpClient userStoreTrans = new THttpClient(appProperties.getProperty(PROP_KEY_EBK_USERSTORE_URL));
		userStoreTrans.setCustomHeader(USER_AGENT, userAgent);
		TBinaryProtocol userStoreProtocol = new TBinaryProtocol(userStoreTrans);

		UserStore.Client client = new UserStore.Client(userStoreProtocol);
		boolean correctVersion = client.checkVersion(userAgent, Constants.EDAM_VERSION_MAJOR, Constants.EDAM_VERSION_MINOR);

		logger.info("AFTER - getEvernoteUserStore()");			

		if (correctVersion) {
			return client;
		}
		else {
			return null;
		}
	}

	private NoteStore.Client getNoteStoreByAuthToken(String aAuthToken) throws EDAMUserException, EDAMSystemException, TTransportException, TException, EDAMNotFoundException {
		
		logger.info("BEFORE - getNoteStoreByAuthToken()");			

		UserStore.Client userStore = getEvernoteUserStore();
		if (userStore == null) {
			return null;
		}
		
		String tNoteURL = userStore.getNoteStoreUrl(aAuthToken);

		THttpClient noteStoreTrans = new THttpClient(tNoteURL);
		String userAgent = appProperties.getProperty(PROP_KEY_EBK_USER_AGENT);
		noteStoreTrans.setCustomHeader(USER_AGENT, userAgent);

		NoteStore.Client client = new NoteStore.Client(new TBinaryProtocol(noteStoreTrans));
//		client.getNotebook(aAuthToken, "3bc88ee1-2f6c-42b9-93be-ecceba287db3");
		
		logger.info("AFTER - getNoteStoreByAuthToken()");			

		return client;
	}
	
}

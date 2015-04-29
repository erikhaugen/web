/**
 * 
 */
package com.livescribe.framework.login.evernote;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;
import com.livescribe.framework.config.AppProperties;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.web.response.ResponseCode;
/**
 * @author Mohammad M. Naqvi
 * @version 1.0
 *
 */
@Component
public class EvernoteProxy {
	
	private static final Logger logger = Logger.getLogger(EvernoteProxy.class);

	private static final String USER_AGENT = "User-Agent";
	private static final String PROP_EN_USER_STORE_URL = "evernoteapi.userstoreurl";
	private static final String PROP_EN_USER_AGENT = "evernote.useragent";

	@Autowired
 	private AppProperties appProperties;

	/**
	 * 
	 */
	public EvernoteProxy() {
	}
	
	public ResponseCode validateEvernoteAuth(Authorization auth) throws EDAMUserException, EDAMSystemException, TException {

		if (null == auth) {
			logger.warn("validateEvernoteAuth called with null auth");
			return ResponseCode.INVALID_PARAMETER;
		}
		logger.debug("validateEvernoteAuth called with enAuthorization [id = " + auth.getAuthorizationId() + "].");

		String uid = auth.getUser().getUid();
		String enUserName = auth.getEnUsername();
		String oauthAccessToken = auth.getOauthAccessToken();
		
		// Get Evernote NoteStore..
		UserStore.Client tUserStore = this.getENUserStoreClient();

		// Make an attempt to get the Evernote user based on the given oAuth-token
		if (tUserStore.getUser(oauthAccessToken) != null) {
			logger.info("Evernote Auth is valid for uid [" + uid + "] and enUserName [" + enUserName + "].");
			return ResponseCode.SUCCESS;
		}
		return ResponseCode.FAILURE;
	}

	/**
	 * Gets an Evernote User Store Client.
	 * 
	 * @return Evernote UserStore Client
	 * @throws TTransportException 
	 */
	private UserStore.Client getENUserStoreClient() throws TTransportException {
		
		logger.debug("getENUserStoreClient() called");

		if (appProperties == null) {
			logger.warn("AppProperties was 'null'!");
			return null;
		}
		
		THttpClient httpClientForENUserStore = new THttpClient(appProperties.getProperty(PROP_EN_USER_STORE_URL));
		httpClientForENUserStore.setCustomHeader(USER_AGENT, appProperties.getProperty(PROP_EN_USER_AGENT));
		TBinaryProtocol tUserStoreProt = new TBinaryProtocol(httpClientForENUserStore);

		return new UserStore.Client(tUserStoreProt);
	}
}

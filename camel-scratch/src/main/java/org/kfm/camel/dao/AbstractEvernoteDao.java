/**
 * Created:  Dec 5, 2014 4:56:47 PM
 */
package org.kfm.camel.dao;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.kfm.camel.bean.CacheItem;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public abstract class AbstractEvernoteDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String USER_AGENT = "User-Agent";
	private static final String USER_STORE_URL = "https://sandbox.evernote.com/edam/user";
	private static final String LS_USER_AGENT = "Livescribe LSShare 0.5";
	
	private Map<String, CacheItem> clientCache = new TreeMap<String, CacheItem>();

	/**
	 * <p></p>
	 * 
	 */
	public AbstractEvernoteDao() {
	}

	protected NoteStore.Client getNoteStoreClient(String oAuthAccessToken) throws EDAMUserException, EDAMSystemException, TException {
		
		//--------------------------------------------------
		//	If there is already a NoteStore object in the cache for 
		//	this OAuth token, use it.
		if (clientCache.containsKey(oAuthAccessToken)) {
			logger.debug("getNoteStoreClient() - Returning existing NoteStore object from cache.");
			CacheItem item = clientCache.get(oAuthAccessToken);
			NoteStore.Client client = item.getClient();
//			exchange.getOut().setBody(client, NoteStore.Client.class);
			return client;
		} else {
			UserStore.Client userStoreClient = getUserStoreClient();
			String noteStoreUrl = userStoreClient.getNoteStoreUrl(oAuthAccessToken);

			THttpClient httpClientForNoteStore = new THttpClient(noteStoreUrl);
			httpClientForNoteStore.setCustomHeader(USER_AGENT, LS_USER_AGENT);

			NoteStore.Client noteStoreClient = new NoteStore.Client(new TBinaryProtocol(httpClientForNoteStore));
			CacheItem item = new CacheItem();
			item.setClient(noteStoreClient);
			logger.debug("getNoteStoreClient() - Storing new NoteStore object in cache.");
			clientCache.put(oAuthAccessToken, item);
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					String method = "run()";
//					removeExpiredItems();
//				}
//				
//			}).start();
			return noteStoreClient;
		}
	}

	/**
	 * <p>Gets an Evernote User Store Client.</p>
	 * 
	 * @return Evernote UserStore Client
	 * @throws TTransportException if an error occurs when attempting to 
	 * connect to Evernote&apos;s UserStore. 
	 */
	protected UserStore.Client getUserStoreClient() throws TTransportException {
		
		logger.debug("getUserStore() - Called.");			
		
//		AppProperties appProperties = AppProperties.getInstance();

//		if (appProperties == null) {
//			logger.debug("AppProperties was 'null'!");
//			return null;
//		}

//		THttpClient httpClientForENUserStore = new THttpClient(appProperties.getProperty(PROP_EN_USER_STORE_URL));
//		httpClientForENUserStore.setCustomHeader(USER_AGENT, appProperties.getProperty(PROP_EN_USER_AGENT));
		THttpClient httpClientForENUserStore = new THttpClient(USER_STORE_URL);
		httpClientForENUserStore.setCustomHeader(USER_AGENT, LS_USER_AGENT);
		TBinaryProtocol tUserStoreProt = new TBinaryProtocol(httpClientForENUserStore);

		return new UserStore.Client(tUserStoreProt);
	}
	
	protected void removeExpiredItems() {
		
		Set<String> keys = clientCache.keySet();
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR, -1);
		for (String key : keys) {
			CacheItem item = clientCache.get(key);
			Calendar entryTime = item.getEntryTime();
			if (entryTime.before(now)) {
				clientCache.remove(key);
			}
		}
	}
}

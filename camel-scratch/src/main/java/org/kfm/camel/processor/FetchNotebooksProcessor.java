/**
 * Created:  Nov 21, 2014 2:34:21 PM
 */
package org.kfm.camel.processor;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.kfm.camel.bean.CacheItem;
import org.kfm.camel.entity.UploadTransaction;
import org.springframework.stereotype.Service;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;
import com.livescribe.afp.AFPException;
import com.livescribe.afp.PenID;
import com.livescribe.web.lssettingsservice.client.LSSettingsServiceClient;
import com.livescribe.web.lssettingsservice.client.Setting;
import com.livescribe.web.lssettingsservice.client.SettingType;
import com.livescribe.web.lssettingsservice.client.exception.InvalidSettingDataException;
import com.livescribe.web.lssettingsservice.client.exception.LSSettingsServiceConnectionException;
import com.livescribe.web.lssettingsservice.client.exception.NoRegisteredDeviceFoundException;
import com.livescribe.web.lssettingsservice.client.exception.NoSettingFoundException;
import com.livescribe.web.lssettingsservice.client.exception.ProcessingErrorException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Service
public class FetchNotebooksProcessor implements Processor {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static final String ARCHIVED_STACK_NAME = "Archived Notebooks";
	private static final String USER_AGENT = "User-Agent";
	private static final String USER_STORE_URL = "https://sandbox.evernote.com/edam/user";
	private static final String LS_USER_AGENT = "Livescribe LSShare 0.5";
	
	private Map<String, CacheItem> clientCache = new TreeMap<String, CacheItem>();
	
	/**
	 * <p></p>
	 * 
	 */
	public FetchNotebooksProcessor() {
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		
		String method = "process()";
		
		String displayId = exchange.getIn().getHeader("displayId", String.class);
		String oAuthAccessToken = exchange.getIn().getHeader("accessToken", String.class);
		String uid = exchange.getIn().getHeader("uid", String.class);
		UploadTransaction upTx = exchange.getIn().getBody(UploadTransaction.class);
		
		//	Get the Evernote NoteStore client.
		NoteStore.Client client = getNoteStoreClient(oAuthAccessToken);
		
		if (client != null) {
			
			//	Get a list of Notebooks from the Evernote account.
			List<Notebook> notebooks = null;
			try {
				notebooks = client.listNotebooks(oAuthAccessToken);
				
				logger.debug(method + " - Found " + notebooks.size() + " notebooks.");
				
				for (int i = 0; i < notebooks.size(); i++) {
					Notebook nb = notebooks.get(i);
					String name = nb.getName();
					logger.debug(method + " - " + name);
				}
				
				//	Determine the name of the stack to use.
				String currentPenStackName = getStackName(displayId, notebooks);
				
				exchange.getIn().setHeader("stackName", currentPenStackName);
				if (upTx != null) {
					logger.debug(method + " - Adding Notebooks to UploadTransaction.");
					upTx.setNotebooks(notebooks);
					exchange.getIn().setBody(upTx);
				} else {
					logger.debug(method + " - No UploadTransaction found.  Adding Notebooks to Exchange.");
					exchange.getIn().setBody(notebooks);
				}
				
			} catch (EDAMUserException eue) {
				eue.printStackTrace();
			} catch (EDAMSystemException ese) {
				ese.printStackTrace();
			} catch (TException te) {
				te.printStackTrace();
			}			
		} else {
			logger.error(method + " - Unable to get NoteStore.Client object for UID = " + uid);
		}
	}

	/**
	 * <p></p>
	 * 
	 * @param displayId
	 * @param notebooks
	 * @return
	 * @throws IOException
	 * @throws AFPException
	 * @throws InvalidSettingDataException
	 * @throws LSSettingsServiceConnectionException
	 * @throws NoSettingFoundException
	 * @throws NoRegisteredDeviceFoundException
	 * @throws ProcessingErrorException
	 */
	private String getStackName(String displayId, List<Notebook> notebooks)
			throws IOException, AFPException, InvalidSettingDataException,
			LSSettingsServiceConnectionException, NoSettingFoundException,
			NoRegisteredDeviceFoundException, ProcessingErrorException {
		
		String method = "getStackName()";
		
		//	Set the default value for the future stack name for this upload.
		String currentPenStackName = new String("Sky Smartpen");
		
		LSSettingsServiceClient ssClient = new LSSettingsServiceClient();
		
		//	LSSettingsService expects serial (259XXXXXXXXXX) not DisplayID (which we get from Pen) need to convert
		PenID penId = new PenID(displayId);
		Setting setting = null;
		try {
			setting = ssClient.getSetting(Long.toString(penId.getId()), SettingType.DEVICE, "system", "penName");
		} catch (InvalidSettingDataException isde) {
			isde.printStackTrace();
		} catch (LSSettingsServiceConnectionException lssce) {
			lssce.printStackTrace();
		} catch (NoSettingFoundException nsfe) {
			nsfe.printStackTrace();
		} catch (NoRegisteredDeviceFoundException nrdfe) {
			nrdfe.printStackTrace();
		} catch (ProcessingErrorException pee) {
			pee.printStackTrace();
		}
		
		//	Update the future stack name with the pen name.
		if (setting != null) {
			currentPenStackName = (String)setting.getSettingValue();
		}
		
		boolean haveSameStackName = true;
		Notebook prevNotebook = notebooks.get(0);
		for (int i = 0; i < notebooks.size(); i++) {
			Notebook currNotebook = notebooks.get(i);
			String name = currNotebook.getName();
			logger.debug(method + " - " + name);
			if ((prevNotebook != null) && (prevNotebook.getStack() != null)) {
				
				if (!prevNotebook.getStack().equalsIgnoreCase(currNotebook.getStack())) {
					haveSameStackName = false;
					break;
				}
			}
			prevNotebook = currNotebook;
		}
		
		if (haveSameStackName) {
			String stackName = prevNotebook.getStack();
			if (!stackName.equalsIgnoreCase(ARCHIVED_STACK_NAME)) {
				currentPenStackName = stackName;
			}
		}
		return currentPenStackName;
	}

	private NoteStore.Client getNoteStoreClient(String oAuthAccessToken) throws EDAMUserException, EDAMSystemException, TException {
		
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
	private UserStore.Client getUserStoreClient() throws TTransportException {
		
		logger.debug("getUserStore() - Called.");			
		
//		AppProperties appProperties = AppProperties.getInstance();

//		if (appProperties == null) {
//			logger.debug("AppProperties was 'null'!");
//			return null;
//		}
		String userStoreUrl = "https://sandbox.evernote.com/edam/user";
		String userAgent = "Livescribe LSShare 0.5";
		
//		THttpClient httpClientForENUserStore = new THttpClient(appProperties.getProperty(PROP_EN_USER_STORE_URL));
//		httpClientForENUserStore.setCustomHeader(USER_AGENT, appProperties.getProperty(PROP_EN_USER_AGENT));
		THttpClient httpClientForENUserStore = new THttpClient(USER_STORE_URL);
		httpClientForENUserStore.setCustomHeader(USER_AGENT, LS_USER_AGENT);
		TBinaryProtocol tUserStoreProt = new TBinaryProtocol(httpClientForENUserStore);

		return new UserStore.Client(tUserStoreProt);
	}
	
	private void removeExpiredItems() {
		
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

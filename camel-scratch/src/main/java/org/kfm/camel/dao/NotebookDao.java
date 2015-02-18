/**
 * Created:  Dec 5, 2014 2:44:59 PM
 */
package org.kfm.camel.dao;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.kfm.camel.bean.CacheItem;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.Notebook;
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
public class NotebookDao extends AbstractEvernoteDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public NotebookDao() {
	}

	/**
	 * <p>Returns a list of <code>Notebook</code> objects for a user identified
	 * by the given OAuth access token.</p>
	 * 
	 * @param oAuthAccessToken The OAuth access token to use.
	 * 
	 * @return a list of <code>Notebook</code> objects.
	 */
	public List<Notebook> findByOAuthToken(String oAuthAccessToken) {
		
		String method = "findByOAuthToken()";
		
		try {
			//	Get the Evernote NoteStore client.
			NoteStore.Client client = getNoteStoreClient(oAuthAccessToken);
			
			if (client != null) {
				
				//	Get a list of Notebooks from the Evernote account.
				List<Notebook> notebooks = client.listNotebooks(oAuthAccessToken);

				return notebooks;
			} else {
				logger.error(method + " - Unable to get NoteStore.Client object for OAuth token = " + oAuthAccessToken);
				return null;
			}
		} catch (EDAMUserException e) {
			e.printStackTrace();
		} catch (EDAMSystemException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
		return null;
	}

}

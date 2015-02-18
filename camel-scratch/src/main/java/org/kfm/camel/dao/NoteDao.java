/**
 * Created:  Dec 5, 2014 4:55:59 PM
 */
package org.kfm.camel.dao;

import org.apache.log4j.Logger;
import org.kfm.camel.exception.ENOAuthTokenExpiredException;
import org.kfm.camel.exception.ENStorageQuotaReachedException;
import org.kfm.jpa.Page;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class NoteDao extends AbstractEvernoteDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public NoteDao() {
	}

	public Note find(Page page, String oAuthAccessToken) {
		
		String method = "find(Page, String)";
		
		String enNoteGuid = page.getEnNoteGuid();
		if (enNoteGuid == null) {
			return null;
		}
		try {
			//	Get the Evernote NoteStore client.
			NoteStore.Client client = getNoteStoreClient(oAuthAccessToken);
			
			Note note = null;
			if (client != null) {
				note = client.getNote(oAuthAccessToken, enNoteGuid, true, true, false, false);
			} else {
				logger.error(method + " - Unable to get NoteStore.Client object for OAuth token = " + oAuthAccessToken);
			}
			return note;
		} catch (EDAMNotFoundException enfe) {
			enfe.printStackTrace();
		} catch (EDAMUserException e) {
			e.printStackTrace();
		} catch (EDAMSystemException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param note
	 * @param oAuthAccessToken
	 * @param uid
	 * 
	 * @return
	 * 
	 * @throws ENStorageQuotaReachedException
	 * @throws ENOAuthTokenExpiredException
	 * @throws EDAMUserException
	 */
	public Note save(Note note, String oAuthAccessToken, String uid) throws ENStorageQuotaReachedException, ENOAuthTokenExpiredException, EDAMNotFoundException, EDAMUserException {
		
		String method = "save()";
		
		Note savedNote = null;
		try {
			NoteStore.Client client = getNoteStoreClient(oAuthAccessToken);
			if (client != null) {
				savedNote = client.createNote(oAuthAccessToken, note);
			} else {
				logger.error(method + " - Unable to get NoteStore.Client object for OAuth token = " + oAuthAccessToken);
			}
			return savedNote;
		} catch (EDAMUserException e) {
			logger.error(e);
			switch ( e.getErrorCode() ){
			case QUOTA_REACHED:
				throw new ENStorageQuotaReachedException("Evernote Storage quota reached during createNoteInEvernote. ", uid, e);
			case AUTH_EXPIRED:
				throw new ENOAuthTokenExpiredException("Evernote Authentication token is expired during createNoteInEvernote. ", e);
			default:
				throw e;
		}						
		} catch (EDAMSystemException e) {
			logger.error(e);
		} catch (TException e) {
			logger.error(e);
		}
		return savedNote;
	}
}

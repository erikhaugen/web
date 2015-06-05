/**
 * 
 */
package com.livescribe.admin.service;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.livescribe.admin.controller.dto.PageDTO;
import com.livescribe.admin.controller.dto.SessionDTO;
import com.livescribe.admin.exception.MultipleRecordsFoundException;
import com.livescribe.admin.exception.RegisteredDeviceNotFoundException;
import com.livescribe.admin.exception.UserNotFoundException;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.lsevernotedb.Document;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface AdminService {

	/**
	 * <p></p>
	 * 
	 * @param uid
	 * @param displayId The 14-character display ID of the device.
	 * 
	 * @throws ClientException
	 * @throws URISyntaxException
	 */
	public void clearSyncDocsFromUserAndPen(String uid, String displayId) throws ClientException, URISyntaxException;
	
	/**
	 * <p></p>
	 * 
	 * @param uid
	 * @throws ClientException
	 * @throws URISyntaxException
	 */
	public void clearSyncDocsFromUser(String uid) throws ClientException, URISyntaxException;
	
	/**
	 * <p></p>
	 * 
	 * @param displayId The 14-character display ID of the device.
	 * @throws ClientException
	 * @throws URISyntaxException
	 */
	public void clearSyncDocsFromPen(String displayId) throws ClientException, URISyntaxException;
	
	/**
	 * <p></p>
	 * 
	 * @param displayId The 14-character display ID of the device.
	 * 
	 * @return
	 */
	public List<Document> findSyncDocumentsByPen(String displayId);
	
	/**
	 * <p></p>
	 * 
	 * @param documentId
	 * 
	 * @return
	 */
	public Document findSyncDocumentById(String documentId);
	
	/**
	 * <p></p>
	 * 
	 * @param documentId
	 * 
	 * @return
	 */
	public List<PageDTO> findPagesOfSyncDocumentByPen(String documentId);
	
	/**
	 * <p></p>
	 * 
	 * @param documentId
	 * 
	 * @return
	 */
	public List<SessionDTO> findSessionsOfSyncDocumentByPen(String documentId);
	
	/**
	 * <p></p>
	 * 
	 * @param uid
	 * @return
	 */
	public List<Document> findSyncDocumentsByUser(String uid);
	
	/**
	 * <p></p>
	 * 
	 * @param uid
	 * @param displayId The 14-character display ID of the device.
	 * 
	 * @return
	 */
	List<Document> findSyncDocumentsByUserAndPen(String uid, String displayId);
	
	/**
	 * <p></p>
	 * 
	 * @param penSerial
	 * 
	 * @throws MultipleRecordsFoundException
	 * @throws RegisteredDeviceNotFoundException
	 */
	public void unregisterPen(String penSerial) throws MultipleRecordsFoundException, RegisteredDeviceNotFoundException;
	
	/**
	 * <p></p>
	 * 
	 * @param uid
	 * 
	 * @throws UserNotFoundException
	 */
	public void removeAuthorization(String uid) throws UserNotFoundException;
	
	/**
	 * <p></p>
	 * 
	 * @param uid
	 * 
	 * @return
	 */
	public boolean hasSyncedData(String uid);

	/**
	 * <p></p>
	 * 
	 * @param displayId The 14-character display ID of the device.
	 * @param uid
	 * @param enUserId
	 * @return
	 */
	public List<Document> findSyncDocumentsByPenUserEnUser(String displayId, String uid, Long enUserId);
	
	/**
	 * <p></p>
	 * 
	 * @param uid
	 * @param enUserId
	 * 
	 * @return
	 */
	public List<Document> findSyncDocumentsByUserEnUser(String uid, Long enUserId);
	
	/**
	 * <p></p>
	 * 
	 * @param user
	 * @param displayId The 14-character display ID of the device.
	 * 
	 * @return
	 */
	public Map<String, List<Document>> findSyncDocsMapByUserAndPenForPrimaryENAuth(User user, String displayId);
	
	/**
	 * <p></p>
	 * 
	 * @param user
	 * @param displayId The 14-character display ID of the device.
	 * 
	 * @return
	 */
	public Map<String, List<Document>> findSyncDocsMapByUserAndPenForNonPrimaryENAuth(User user, String displayId);
	
	/**
	 * <p></p>
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public Map<String, List<Document>> findSyncDocsMapByUserForPrimaryENAuth(User user);
	
	/**
	 * <p>Returns a <code>Map</code> of <code>Document</code>s belonging to each
	 * non-primary Evernote authorization.</p>
	 * 
	 * <p>The key is an Evernote username which is not identified as the user&apos;s primary authorization.</p>
	 * 
	 * @param user A record from the <code>consumer.user</code> table.
	 * 
	 * @return
	 */
	public Map<String, List<Document>> findSyncDocsMapByUserForNonPrimaryENAuth(User user);
	
	/**
	 * <p>Deletes a single document identified by the given document ID.</p>
	 * 
	 * <p>WARNING:  This method WILL delete an <u>archived</u> 
	 * <code>Document</code>!!!</p>
	 * 
	 * @param documentId The unique ID of the document to delete.  (i.e.  The primary key of the record in the database.)
	 * @param penDisplayId The 14-character display ID of the device.
	 * 
	 * @throws ClientException
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 */
	public void deleteDocument(String documentId, String penDisplayId) throws ClientException, UnsupportedEncodingException, URISyntaxException;
}

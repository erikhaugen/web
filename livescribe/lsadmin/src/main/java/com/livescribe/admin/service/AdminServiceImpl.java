/**
 * 
 */
package com.livescribe.admin.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.admin.client.ShareServiceClient;
import com.livescribe.admin.config.AppProperties;
import com.livescribe.admin.controller.dto.PageDTO;
import com.livescribe.admin.controller.dto.SessionDTO;
import com.livescribe.admin.dao.CustomAuthorizationDao;
import com.livescribe.admin.dao.CustomDocumentDao;
import com.livescribe.admin.dao.CustomPageDao;
import com.livescribe.admin.dao.CustomRegisteredDeviceDao;
import com.livescribe.admin.dao.CustomSessionDao;
import com.livescribe.admin.dao.CustomUserDao;
import com.livescribe.admin.exception.MultipleRecordsFoundException;
import com.livescribe.admin.exception.RegisteredDeviceNotFoundException;
import com.livescribe.admin.exception.UserNotFoundException;
import com.livescribe.auth.client.DigestClient;
import com.livescribe.auth.client.exception.InvalidParameterException;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.lsevernotedb.Document;
import com.livescribe.framework.orm.lsevernotedb.Page;
import com.livescribe.framework.orm.lsevernotedb.Session;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class AdminServiceImpl implements AdminService {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * <p>The key for the "username" parameter of the nonce.&nbsp;&nbsp;
	 * Usually contains the pen display ID.</p>
	 */
	private static final String KEY_USERNAME			= "username";
	private static final String KEY_PEN_ID		= "penserial";
	private static final String KEY_REALM				= "realm";
	private static final String KEY_NONCE				= "nonce";
	private static final String KEY_OPAQUE				= "opaque";
	private static final String KEY_QOP					= "qop";
	private static final String KEY_HTTP_DIGEST_ENABLED	= "httpdigest.enabled";
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private CustomRegisteredDeviceDao registeredDeviceDao;
	
	@Autowired
	private CustomDocumentDao documentDao;
	
	@Autowired
    private CustomPageDao pageDao;
	
	@Autowired
    private CustomSessionDao sessionDao;
	
	@Autowired
	private CustomUserDao userDao;
	
	@Autowired
	private CustomAuthorizationDao authorizationDao;
	
	/**
	 * <p></p>
	 *
	 */
	public AdminServiceImpl() {
		
	}

	/* (non-Javadoc)
	 * This method is incomplete, need to implement: 
	 *     deleteAllDocumentsByUserAndPen
	 *     deleteAllDocumentsByUser
	 *     deleteAllDocumentsByPen
	 *     
	 * @see com.livescribe.admin.service.AdminService#clearAllSyncData(java.lang.String)
	 */
	@Override
	public void clearSyncDocsFromUserAndPen(String uid, String displayId) throws ClientException, URISyntaxException {
		
		String method = "clearSyncDocsFromUserAndPen(String, String):  ";
		
		logger.info("BEGIN - " + method + " - " + displayId + " - " + uid);
		long start = System.currentTimeMillis();
		
		//	Get a nonce from LS Token Service.
		DigestClient digestClient = new DigestClient();
		String nonce = null;
		try {
			nonce = digestClient.getAuthCurrentNonce(displayId);
		} catch (InvalidParameterException ipe) {
			String msg = "InvalidParameterException thrown when contacting Token Service.";
			logger.error(method + msg, ipe);
			throw new ClientException(msg, ipe);
		} catch (com.livescribe.auth.client.exception.ClientException cpe) {
			String msg = "com.livescribe.framework.exception.ClientException thrown when contacting Token Service.";
			logger.error(method + msg, cpe);
			throw new ClientException(msg, cpe);
		}
		
		if (nonce == null) {
			String msg = "The returned nonce String was 'null'.";
			logger.error(msg);
			throw new ClientException(msg);
		}
		
		logger.debug(method + "Received nonce from Token Service: " + nonce);
		
		HashMap<String, String> nonceParts = parseAuthHeader(nonce);
		nonceParts.put(KEY_USERNAME, displayId);
		
		ShareServiceClient client = null;
		try {
			client = new ShareServiceClient(digestClient.getJsessionCookie());
		} catch (IOException ioe) {
			String msg = "IOException thrown when creating ShareServiceClient class.  ";
			logger.error(method + " - " + msg);
			throw new ClientException(msg, ioe);
		}
		
		String httpDigestEnabledStr = this.appProperties.getProperty(KEY_HTTP_DIGEST_ENABLED);
		boolean httpDigestEnabled = Boolean.parseBoolean(httpDigestEnabledStr);

		//	If HTTP Digest is enabled, use HTTP Digest
		if (httpDigestEnabled) {
//			client.deleteAllDocumentsByUserAndPen(nonceParts);		<-- deprecated.
			client.deleteDocumentsByUserAndPen(nonceParts, uid);
		}
		//	... otherwise, use only the display ID for "Authorization" header ....
		else {
//			client.deleteAllDocumentsByUserAndPen(uid, displayId);	<-- deprecated.
			client.deleteDocumentsByUserAndPen(uid, displayId);
		}

		long end = System.currentTimeMillis();
		long duration = end - start;
		logger.info("AFTER - " + method + " - " + displayId + " - " + uid + " - " + duration + " milliseconds.");
	}

    @Override
    public void clearSyncDocsFromUser(String uid) throws ClientException,
            URISyntaxException {
        clearSyncDocsFromUserAndPen(uid, null);
    }

    @Override
    public void clearSyncDocsFromPen(String displayId) throws ClientException,
            URISyntaxException {
        clearSyncDocsFromUserAndPen(null, displayId);
    }
	
	/**
	 * <p></p>
	 * 
	 * @param nonce A unique string obtained from LS Token Service.
	 * 
	 * @return
	 */
	private HashMap<String, String> parseAuthHeader(String nonce) {
		
		String method = "parseAuthHeader():  ";
		
		HashMap<String, String> parts = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		sb.append("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - \n");
		String paramStr = nonce.substring(nonce.indexOf(" ") + 1).trim();
        String keyValues[] = paramStr.split(",");
        for (String keyval : keyValues) {
            if (keyval.contains("=")) {
                String key = keyval.substring(0, keyval.indexOf("="));
                String value = keyval.substring(keyval.indexOf("=") + 1);
                parts.put(key.trim(), value.replaceAll("\"", "").trim());
                sb.append(key).append("\t").append(value).append("\n");
            }
        }
		sb.append("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - \n");
		logger.debug(method + sb.toString());
		
		return parts;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.admin.service.AdminService#findSyncDocuments(java.lang.String)
	 */
	@Override
	public List<Document> findSyncDocumentsByPen(String displayId) {
		
		String method = "findSyncDocumentsByPen(" + displayId + "):  ";
		
		List<Document> list = this.documentDao.findByDisplayId(displayId);
		
		if (list != null) {
			logger.debug(method + "Found " + list.size() + " documents.");
		}

		return list;
	}
	
	@Override
    public List<Document> findSyncDocumentsByUserAndPen(String uid, String displayId) {
        
        String method = "findSyncDocumentsByPen(" + displayId + "):  ";
        
        List<Document> list = this.documentDao.findByUIDAndDisplayId(uid, displayId);
        
        if (list != null) {
            logger.debug(method + "Found " + list.size() + " documents.");
        }

        return list;
    }

	/* (non-Javadoc)
	 * @see com.livescribe.admin.service.AdminService#findSyncDocumentsByUser(java.lang.String)
	 */
	@Override
	public List<Document> findSyncDocumentsByUser(String uid) {
		
		String method = "findSyncDocumentsByUser(" + uid + "):  ";
		
		List<Document> list = this.documentDao.findByUid(uid);
		
		if (list != null) {
			logger.debug(method + "Found " + list.size() + " documents.");
		}
		
		return list;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.admin.service.AdminService#unregisterPen(java.lang.String)
	 */
	@Override
	@Transactional("consumer")
	public void unregisterPen(String penSerial) throws MultipleRecordsFoundException, RegisteredDeviceNotFoundException {
		
		String method = "unregisterPen(" + penSerial + "):  ";
		
		RegisteredDevice regDev = this.registeredDeviceDao.findByPenSerial(penSerial);
		
		logger.debug(method + "Deleting 'registered_device' with pen serial number '" + penSerial + "'.");
		
		this.registeredDeviceDao.delete(regDev);
//		this.registeredDeviceDao.deleteByPenSerial(penSerial);
	}

    @Override
    public List<PageDTO> findPagesOfSyncDocumentByPen(String documentId) {
    	
        String method = "findPageOfSyncDocumentsByPen(" + documentId + "):  ";
        long docId = Long.parseLong(documentId);
        
        List<Page> list = this.pageDao.findPagesOfSyncDocumentByPen(docId);
        
        if (list != null) {
            logger.debug(method + "Found " + list.size() + " pages.");
            List<PageDTO> pageList = new ArrayList<PageDTO>(list.size());
            for (Page page : list) {
            	pageList.add(new PageDTO(page));
            }
            return pageList;
        } else
            logger.debug(method + "Cannot find pages.");
        return null;
    }

    @Override
    public List<SessionDTO> findSessionsOfSyncDocumentByPen(String documentId) {
    	
        String method = "findSessionsOfSyncDocumentByPen(" + documentId + "):  ";
        long docId = Long.parseLong(documentId);
        
        List<Session> list = this.sessionDao.findSessionsOfSyncDocumentByPen(docId);
        
        if (list != null) {
            logger.debug(method + "Found " + list.size() + " sessions.");
            List<SessionDTO> sessionList = new ArrayList<SessionDTO>(list.size());
            for (Session session : list) {
            	sessionList.add(new SessionDTO(session));
            }
            return sessionList;
        } else
            logger.debug(method + "Cannot find sessions.");
        return null;
    }

    @Override
    public Document findSyncDocumentById(String documentId) {
    	
        Document result = documentDao.findById(Long.parseLong(documentId));
        return result;
    }
    
    @Transactional("consumer")
    public void removeAuthorization(String uid) throws UserNotFoundException {
    	String method = "removeAuthorization(" + uid + "):  ";
    	logger.debug(method);
    	
    	User user = userDao.findByUid(uid);
    	if (user == null) {
    		throw new UserNotFoundException("User with uid=" + uid + " is not found.");
    	}
    	
    	Set<Authorization> authorizations = user.getAuthorizations();
    	Iterator<Authorization> iter = authorizations.iterator();
    	if (iter.hasNext()) {
    		Authorization authz = iter.next();
    		logger.debug(method + "De-authorizing Evernote user '" + authz.getEnUsername() + "'");
    		authorizationDao.delete(authz);
    	}
    }
    
    @Transactional("evernote")
    public boolean hasSyncedData(String uid) {
    	
    	String method = "hasSyncedData(" + uid + "):  ";
    	logger.debug(method);
    	
    	List<Document> result = documentDao.findByUid(uid);
    	if (result == null || result.size() == 0) {
    		return false;
    	}
    	
    	return true;
    }


	@Override
	public List<Document> findSyncDocumentsByUserEnUser(String uid,	Long enUserId) {
        
        String method = "findSyncDocumentsByUserEnUser(" + ", " + uid + ", " + enUserId + "):  ";        
        List<Document> list = documentDao.findByUidAndEnUserId(uid, enUserId);
        if (list != null) {
            logger.debug(method + "Found " + list.size() + " documents.");
        }
        return list;
	}

	@Override
	public List<Document> findSyncDocumentsByPenUserEnUser(String displayId, String uid, Long enUserId) {
        
        String method = "findSyncDocumentsByPenUserEnUser(" + displayId + ", " + uid + ", " + enUserId + "):  ";        
        List<Document> list = documentDao.findByDisplayIdUidAndEnUserId(displayId, uid, enUserId);
        if (list != null) {
            logger.debug(method + "Found " + list.size() + " documents.");
        }
        return list;
	}

	@Override
	public Map<String, List<Document>> findSyncDocsMapByUserAndPenForPrimaryENAuth(User user, String displayId) {
		
		if (null == user) {
			return null;
		}
		
		Authorization primaryAuth = authorizationDao.findPrimaryENAuthByUserId(user.getUserId());
		if (null == primaryAuth) {
			return null;
		}
		
		String enUserName = primaryAuth.getEnUsername();
		List<Document> syncedDocuments = findSyncDocumentsByPenUserEnUser(displayId, user.getUid(), primaryAuth.getEnUserId());

		Map<String, List<Document>> map = new HashMap<String, List<Document>>();
		map.put(enUserName, syncedDocuments);
		return map;
	}

	@Override
	public Map<String, List<Document>> findSyncDocsMapByUserAndPenForNonPrimaryENAuth(User user, String displayId) {
		
		if (null == user) {
			return null;
		}

		List<Authorization> nonPrimaryAuths = authorizationDao.findNonPrimaryENAuthsByUserId(user.getUserId());
		if (null == nonPrimaryAuths || nonPrimaryAuths.isEmpty()) {
			return null;
		}
		
		Map<String, List<Document>> map = new HashMap<String, List<Document>>();
		for (Authorization auth : nonPrimaryAuths) {
			String enUserName = auth.getEnUsername();
			List<Document> syncedDocuments = findSyncDocumentsByPenUserEnUser(displayId, user.getUid(), auth.getEnUserId());
			map.put(enUserName, syncedDocuments);
		}
		return map;
	}

	@Override
	public Map<String, List<Document>> findSyncDocsMapByUserForPrimaryENAuth(User user) {
		
		if (null == user) {
			return null;
		}
		
		Authorization primaryAuth = authorizationDao.findPrimaryENAuthByUserId(user.getUserId());
		if (null == primaryAuth) {
			return null;
		}
		
		String enUserName = primaryAuth.getEnUsername();
		List<Document> syncedDocuments = findSyncDocumentsByUserEnUser(user.getUid(), primaryAuth.getEnUserId());

		Map<String, List<Document>> map = new HashMap<String, List<Document>>();
		map.put(enUserName, syncedDocuments);
		return map;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.admin.service.AdminService#findSyncDocsMapByUserForNonPrimaryENAuth(com.livescribe.framework.orm.consumer.User)
	 */
	@Override
	public Map<String, List<Document>> findSyncDocsMapByUserForNonPrimaryENAuth(User user) {
		
		if (null == user) {
			return null;
		}

		List<Authorization> nonPrimaryAuths = authorizationDao.findNonPrimaryENAuthsByUserId(user.getUserId());
		if (null == nonPrimaryAuths || nonPrimaryAuths.isEmpty()) {
			return null;
		}
		
		Map<String, List<Document>> map = new HashMap<String, List<Document>>();
		for (Authorization auth : nonPrimaryAuths) {
			String enUserName = auth.getEnUsername();
			List<Document> syncedDocuments = findSyncDocumentsByUserEnUser(user.getUid(), auth.getEnUserId());
			map.put(enUserName, syncedDocuments);
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.admin.service.AdminService#deleteDocument(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteDocument(String documentId, String penDisplayId) throws ClientException, UnsupportedEncodingException, URISyntaxException {
		
		String method = "deleteDocument(" + documentId + ", " + penDisplayId + ")";
		logger.debug("BEGIN - " + method);
		
		//--------------------------------------------------
		//	Get an nonce from LS Token Service.
		DigestClient digestClient = new DigestClient();
		String nonce = null;
		try {
			nonce = digestClient.getAuthCurrentNonce(penDisplayId);
		} catch (InvalidParameterException ipe) {
			String msg = "InvalidParameterException thrown when contacting Token Service.";
			logger.error(method + " - " + msg, ipe);
			throw new ClientException(msg, ipe);
		} catch (com.livescribe.auth.client.exception.ClientException cpe) {
			String msg = "com.livescribe.framework.exception.ClientException thrown when contacting Token Service.";
			logger.error(method + " - " + msg, cpe);
			throw new ClientException(msg, cpe);
		}
		
		if (nonce == null) {
			String msg = "The returned nonce String was 'null'.";
			logger.error(method + " - " + msg);
			throw new ClientException(msg);
		}
		
		logger.debug(method + " - Received nonce from Token Service: " + nonce);
		
		HashMap<String, String> nonceParts = parseAuthHeader(nonce);
		nonceParts.put(KEY_USERNAME, penDisplayId);
		nonceParts.put(KEY_PEN_ID, penDisplayId);
		
		ShareServiceClient client = null;
		try {
			client = new ShareServiceClient(digestClient.getJsessionCookie());
		} catch (IOException ioe) {
			String msg = "IOException thrown when creating ShareServiceClient class.  ";
			logger.error(method + " - " + msg);
			throw new ClientException(msg, ioe);
		}
		
		//--------------------------------------------------
		//	Find the Document in the database.
		Document document = documentDao.findById(Long.parseLong(documentId));
		
		if (null != document && document.getArchive() > 0) {
			
			//	Deleting an archived document/notebook
			String guid = document.getGuid();
			long copy = document.getCopy();
			long number = document.getArchive();
			logger.debug(method + " - Deleting an archived document with id [" + documentId + "], guid[" + guid + "], copy["+ copy +  "], archive["+ number + "]");
			client.deleteArchivedDocument(nonceParts, penDisplayId, guid, copy, number);
		} else {
			logger.debug(method + " - Deleting an NON-archived document with id [" + documentId + "]");
			
			client.deleteDocument(nonceParts, documentId);
		}
		
		logger.debug("AFTER - " + method);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param documentId
	 * @param penDisplayId
	 * @throws ClientException
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 */
	public void deleteArchivedDocument(String documentId, String penDisplayId) throws ClientException, UnsupportedEncodingException, URISyntaxException {
		
		String method = "deleteArchivedDocument(" + documentId + ", " + penDisplayId + ")";
		logger.debug("BEGIN - " + method);
		
		//--------------------------------------------------
		//	Get an nonce from LS Token Service.
		DigestClient digestClient = new DigestClient();
		String nonce = null;
		try {
			nonce = digestClient.getAuthCurrentNonce(penDisplayId);
		} catch (InvalidParameterException ipe) {
			String msg = "InvalidParameterException thrown when contacting Token Service.";
			logger.error(method + " - " + msg, ipe);
			throw new ClientException(msg, ipe);
		} catch (com.livescribe.auth.client.exception.ClientException cpe) {
			String msg = "com.livescribe.framework.exception.ClientException thrown when contacting Token Service.";
			logger.error(method + " - " + msg, cpe);
			throw new ClientException(msg, cpe);
		}
		
		if (nonce == null) {
			String msg = "The returned nonce String was 'null'.";
			logger.error(method + " - " + msg);
			throw new ClientException(msg);
		}
		
		logger.debug(method + " - Received nonce from Token Service: " + nonce);
		
		HashMap<String, String> nonceParts = parseAuthHeader(nonce);
		nonceParts.put(KEY_USERNAME, penDisplayId);
		nonceParts.put(KEY_PEN_ID, penDisplayId);
		
		ShareServiceClient client = null;
		try {
			client = new ShareServiceClient(digestClient.getJsessionCookie());
		} catch (IOException ioe) {
			String msg = "IOException thrown when creating ShareServiceClient class.  ";
			logger.error(method + " - " + msg);
			throw new ClientException(msg, ioe);
		}
		
		//--------------------------------------------------
		//	Find the Document in the database.
		Document document = documentDao.findById(Long.parseLong(documentId));
		
		if (null != document && document.getArchive() > 0) {
			
			//	Deleting an archived document/notebook
			String guid = document.getGuid();
			long copy = document.getCopy();
			long number = document.getArchive();
			logger.debug(method + " - Deleting an archived document with id [" + documentId + "], guid[" + guid + "], copy["+ copy +  "], archive["+ number + "]");
			client.deleteArchivedDocument(nonceParts, penDisplayId, guid, copy, number);
		} else {
			logger.debug(method + " - Deleting an NON-archived document with id [" + documentId + "]");
			
			client.deleteDocument(nonceParts, documentId);
		}
		
		logger.debug("AFTER - " + method);
	}
}

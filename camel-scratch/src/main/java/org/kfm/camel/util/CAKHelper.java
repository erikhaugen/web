/**
 * Created:  Dec 23, 2014 12:10:19 PM
 */
package org.kfm.camel.util;

import java.math.BigInteger;

import javax.persistence.EntityExistsException;

import org.apache.log4j.Logger;
import org.kfm.camel.dao.ContentAccessDao;
import org.kfm.jpa.ContentAccess;
import org.kfm.jpa.Document;
import org.kfm.jpa.Page;
import org.kfm.jpa.Session;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CAKHelper {

	private static Logger logger = Logger.getLogger(CAKHelper.class.getName());

	public static final String CAK_NAME = "cak";

	@Autowired
	private static ContentAccessDao contentAccessDao;
	
	/**
	 * <p>Gets the CAK record from the database.</p>
	 * 
	 * If not found, it will generate a new one and provide it to the caller 
	 * and also add the newly generated one to the database.
	 * 
	 * @param uid
	 * @param document
	 * @param session
	 * @param page
	 * @param enUserId
	 * 
	 * @author Gurmeet Kalra
	 * @author Mohammad M. Naqvi (Added enUserId..)
	 */
	public static long getCAK(String uid, Document document, Session session, Page page, long enUserId) {
		
		String method = "getCAK()";
		
		long cakId = 0L;
		
		
		// Algorithm
		// 1. Find out if the CAK for the userid, docId and session id exists?
		// 2. If yes, then we can reuse the CAK with it and return
		// 3. If not, then we can create a brand new one and send it as the return value
		
		// 1. Query the Content Access table
		// and check if record exists?
//		cakId = fetchCakfromDB(userId, docId, sessionId, pageId, enUserId, (0 == sessionId));
		
		ContentAccess ca = null;
		if (session != null) {
			ca = contentAccessDao.find(uid, document, session, enUserId);
		} else if (page != null) {
			ca = contentAccessDao.find(uid, document, page, enUserId);
		} else {
			logger.error(method + " - Both session and page were 'null'.  Unable to create new CAK.");
			return -1;
		}
		
		if (ca == null) {
			logger.error(method + " - Unable to find CAK for either session or page.  Unable to create new CAK.");
			return -1;
		}
		
		String cakIdStr = ca.getCakId();
		cakId = Long.valueOf(cakIdStr);
		
		if (cakId < 0L) {
			//	Remove negative CAKs because of parsing from hex string problem, see below
			contentAccessDao.delete(ca);
			//	Continue as if no record was found 
		} else  if (cakId > 0L) {
			//	2. we are done
			return cakId;
		}

		// 3. else we need to generate a new cak and save it in the database with the other contents

		
//		boolean saved = saveContentAccess(uid, document, session, page,
//				enUserId);
		
		ContentAccess cakRecord = createContentAccess(uid, document, session,
				page, enUserId);

		boolean saved = false;
		int retries = 3;
		while ((!saved) && (retries < 3)) {
			try {
				contentAccessDao.save(cakRecord);
				saved = true;
			} catch (EntityExistsException e) {
				logger.warn(method + " - CAK already exists.");
				cakRecord = createContentAccess(uid, document, session, page, enUserId);
			}
		}

		if (!saved) {
			logger.error(method + " - Unable to save CAK after 3 retires.");
		}
		return cakId;
	}

	/**
	 * <p></p>
	 * 
	 * @param uid
	 * @param document
	 * @param session
	 * @param page
	 * @param enUserId
	 * @param method
	 * @return
	 */
	private static boolean saveContentAccess(String uid, Document document,
			Session session, Page page, long enUserId) {
		
		String method = "saveContentAccess()";
		
		ContentAccess cakRecord = createContentAccess(uid, document, session,
				page, enUserId);

		boolean saved = false;
		int retries = 3;
		while ((!saved) && (retries < 3)) {
			try {
				contentAccessDao.save(cakRecord);
				saved = true;
			} catch (EntityExistsException e) {
				logger.warn(method + " - CAK already exists.");
				cakRecord = createContentAccess(uid, document, session, page, enUserId);
			}
		}
		return saved;
	}

	/**
	 * <p></p>
	 * 
	 * @param uid
	 * @param document
	 * @param session
	 * @param page
	 * @param enUserId
	 * @param cakId
	 * @return
	 */
	private static ContentAccess createContentAccess(String uid,
			Document document, Session session, Page page, long enUserId) {

		//GSK - creating a new CAK identifier that we will add to the link in EN and also store this info in our database
		long cakId = CAKGenerator.getInstance().getNextRandomNumber();

		ContentAccess cakRecord = new ContentAccess();
		cakRecord.setCakId(String.valueOf(cakId));
		cakRecord.setUid(uid);
		cakRecord.setDocument(document);
		cakRecord.setEnUserId(BigInteger.valueOf(enUserId));
		if (session != null) {
			cakRecord.setPage(page);
		} else if (page != null) {
			cakRecord.setSession(session);
		}
		return cakRecord;
	}
}

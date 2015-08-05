/**
 * Created:  Sep 20, 2013 6:10:38 PM
 */
package com.livescribe.web.tools.webteamtool.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.notestore.SyncState;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.TException;
import com.livescribe.framework.exception.AuthorizationNotFoundException;
import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.web.tools.webteamtool.dao.CustomAuthorizationDao;
import com.livescribe.web.tools.webteamtool.dao.CustomUserDao;
import com.livescribe.web.tools.webteamtool.util.Utils;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EvernoteServiceImpl implements EvernoteService {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private CustomUserDao userDao;
	
	@Autowired
	private CustomAuthorizationDao authorizationDao;
	
	/**
	 * <p></p>
	 * 
	 */
	public EvernoteServiceImpl() {
		logger.debug("Instantiated.");
	}

	/* (non-Javadoc)
	 * @see com.livescribe.web.tools.webteamtool.service.EvernoteService#findRemainingQuota(java.lang.String)
	 */
	@Transactional("consumer")
	public long findRemainingQuota(String email) throws MultipleRecordsFoundException, EDAMUserException, EDAMSystemException, TException, UserNotFoundException, AuthorizationNotFoundException {

		String method = "findRemainingQuota()";

		System.out.println("BEGIN - " + method);
		long start = System.currentTimeMillis();

		//	Find the User object to get the primary key.
		User user = userDao.findByEmail(email);
		if (user == null) {
			logger.info(method + " - User with email address '" + email + "' was not found in the database.");
			throw new UserNotFoundException();
		}
		
		//	Find the Authorization object to get the OAuth access token.
		Authorization authorization = authorizationDao.findByUser(user);
		if (authorization == null) {
			String msg = "Could not locate Authorization record in the database for User with email address '" + email + "'.";
			logger.info(method + " - " + msg);
			throw new AuthorizationNotFoundException(msg);
		}
		String oauthAccessToken = authorization.getOauthAccessToken();
		if ((oauthAccessToken == null) || (oauthAccessToken.isEmpty())) {
			String msg = "The OAuth access token from the database was 'null' or empty for User with email address '" + email + "'.";
			logger.info(method + " - " + msg);
			throw new AuthorizationNotFoundException(msg);
		}
		
		//	Find the user's remaining quota.
		NoteStore.Client noteStore = Utils.getEvernoteNoteStoreClient(oauthAccessToken);
		
		System.out.println("    Got NoteStore ...");
		
		SyncState syncState = noteStore.getSyncState(oauthAccessToken);
		long bytesUploaded = syncState.getUploaded();
		
		UserStore.Client userStore = Utils.getEvernoteUserStoreClient();
		
		System.out.println("    Got UserStore ...");
		
		com.evernote.edam.type.User edamUser = userStore.getUser(oauthAccessToken);
		long byteUploadLimit = edamUser.getAccounting().getUploadLimit();
		
		long remainingQuota = byteUploadLimit - bytesUploaded;
		System.out.println("\n");
		System.out.println("remainingQuota = byteUploadLimit - bytesUploaded");
		System.out.println(remainingQuota + " = " + byteUploadLimit + " - " + bytesUploaded);
		System.out.println("\n");
		
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("AFTER - " + method + " - " + duration + " milliseconds.");

		return remainingQuota;
	}

}

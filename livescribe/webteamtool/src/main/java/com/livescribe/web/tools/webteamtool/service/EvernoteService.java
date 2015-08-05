/**
 * Created:  Sep 20, 2013 6:09:56 PM
 */
package com.livescribe.web.tools.webteamtool.service;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.livescribe.framework.exception.AuthorizationNotFoundException;
import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.exception.UserNotFoundException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface EvernoteService {

	/**
	 * <p></p>
	 * 
	 * @param email
	 * 
	 * @return
	 * 
	 * @throws MultipleRecordsFoundException 
	 * @throws TException 
	 * @throws EDAMSystemException 
	 * @throws EDAMUserException 
	 * @throws UserNotFoundException 
	 * @throws AuthorizationNotFoundException 
	 */
	public long findRemainingQuota(String email) throws MultipleRecordsFoundException, EDAMUserException, EDAMSystemException, TException, UserNotFoundException, AuthorizationNotFoundException;
}

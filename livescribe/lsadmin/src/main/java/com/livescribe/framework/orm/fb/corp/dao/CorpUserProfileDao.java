package com.livescribe.framework.orm.fb.corp.dao;

import org.hibernate.HibernateException;

import com.livescribe.framework.orm.fb.corp.CorpUserProfile;

/**
 * <p>Defines read/write operations for <code>CorpUserProfile</code> instances.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface CorpUserProfileDao {
	
	/**
	 * <p>Returns a user&apos; profile identified by the given email address.</p>
	 * 
	 * @param primaryKey The email address to use in looking up the user&apos;s profile.
	 * 
	 * @return the deleted user&apos; profile identified by the given email address.
	 */
	public CorpUserProfile deleteCorpUserProfileByEmail(String email) throws HibernateException ;
	
	/**
	 * <p></p>
	 * 
	 * @param email The e-mail address to use in looking up the user&apos;s profile.
	 * 
	 * @return 
	 */
	public CorpUserProfile getCorpUserProfileByEmail(String email);
	
	/**
	 * <p>Saves the given <code>CorpUserProfile</code> instance to the database.</p>
	 * 
	 * @param userProfile The <code>CorpUserProfile</code> instance to save.
	 */
	public CorpUserProfile save(CorpUserProfile corpUserProfile);
	
	/**
	 * <p>Updates the given <code>CorpUserProfile</code> instance to the database.</p>
	 * 
	 * @param userProfile The <code>CorpUserProfile</code> instance to save.
	 */
	public CorpUserProfile update(CorpUserProfile corpUserProfile);
}


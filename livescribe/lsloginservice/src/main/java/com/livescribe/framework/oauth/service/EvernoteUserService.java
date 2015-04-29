package com.livescribe.framework.oauth.service;

import com.evernote.edam.type.PremiumInfo;
import com.evernote.edam.type.User;
import com.evernote.edam.userstore.PublicUserInfo;

public interface EvernoteUserService {

	/**
	 * <p>Get User from access token</p>
	 * 
	 * @param accessToken
	 * @return
	 * @throws Exception
	 */
	public User getCurrentUser(String accessToken) throws Exception;
	
	/**
	 * <p>Get PublicUserInfo from username</p>
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public PublicUserInfo getPublicUserInfo(String username) throws Exception;
	
	/**
	 * <p>Get PremiumInfo</p>
	 * 
	 * @param authToken
	 * @return
	 * @throws Exception
	 */
	public PremiumInfo getUserPremiumInfo(String authToken) throws Exception;
}

package com.livescribe.framework.oauth.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.evernote.edam.type.PremiumInfo;
import com.evernote.edam.type.User;
import com.evernote.edam.userstore.PublicUserInfo;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.livescribe.framework.config.AppProperties;

public class EvernoteUserServiceImpl implements EvernoteUserService {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public static final String SERVICE_FULL_NAME = "Evernote User Service";
	public static final String PROP_USER_STORE_URL = "evernoteapi.userstoreurl";
	
	@Autowired
	private AppProperties appProperties;
	
	public User getCurrentUser(String accessToken) throws Exception {
		String method = "getCurrentUser():  ";
		
		UserStore.Client userStore = getUserStore();
		User user = userStore.getUser(accessToken);
		
		logger.debug(method + user.toString());
		return user;
	}
	
	public PublicUserInfo getPublicUserInfo(String username) throws Exception {
		String method = "getPublicUserInfo():  ";
		
		UserStore.Client userStore = getUserStore();
		PublicUserInfo pui = userStore.getPublicUserInfo(username);
		
		logger.debug(method + pui.toString());
		return pui;
	}
	
	public PremiumInfo getUserPremiumInfo(String authToken) throws Exception {
		String method = "getUserPremiumInfo():  ";
		
		UserStore.Client userStore = getUserStore();
		PremiumInfo premiumInfo = userStore.getPremiumInfo(authToken);
		
		logger.debug(method + premiumInfo.toString());
		return premiumInfo;
	}
	
	private UserStore.Client getUserStore() throws Exception
	{
		THttpClient tUserStoreTrans = new THttpClient(appProperties.getProperty(PROP_USER_STORE_URL));
		TBinaryProtocol tUserStoreProt = new TBinaryProtocol(tUserStoreTrans);

		return new UserStore.Client(tUserStoreProt);
	}
}

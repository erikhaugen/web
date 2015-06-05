package com.livescribe.admin.utils;

import org.apache.log4j.Logger;

import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;
import com.livescribe.admin.config.AppProperties;
import com.livescribe.admin.service.AdminService;

public class EvernoteAPIUtils {

	private static Logger logger = Logger.getLogger(EvernoteAPIUtils.class.getName());
	
	public static final String PROP_USER_STORE_URL = "evernoteapi.userstoreurl";
	
	private AdminService adminService;
	
	private static EvernoteAPIUtils instance;
	
	/**
	 * Method to get instance of the util class
	 * 
	 * @return
	 */
	public static EvernoteAPIUtils getInstance() {
		if (instance != null) {
			return instance;
		}
		
		instance = new EvernoteAPIUtils();
		return instance;
	}
	
	/**
	 * 
	 * @param oauthToken
	 * @return
	 */
	public static boolean isEvernoteOAuthExpired(String oauthToken) {
		String method = "isEvernoteOAuthExpired():  ";
		try {
			UserStore.Client userStore = getUserStore();
			userStore.getUser(oauthToken);
			
		} catch (EDAMUserException eue) {
			if (eue.getErrorCode().equals(EDAMErrorCode.AUTH_EXPIRED)) {
				return true;
			}
			logger.info(method + eue.getMessage());
			logger.info(method + eue.getErrorCode().name());
			eue.printStackTrace();
			
		} catch (EDAMSystemException ese) {
			if (ese.getErrorCode().equals(EDAMErrorCode.AUTH_EXPIRED)) {
				return true;
			}
			logger.info(method + ese.getMessage());
			logger.info(method + ese.getErrorCode().name());
			ese.printStackTrace();
			
		} catch (TTransportException e) {
			logger.info(method + e.getMessage());
			e.printStackTrace();
			
		} catch (TException e) {
			logger.info(method + e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param uid
	 * @return
	 */
	public static boolean hasEvernoteSyncedData(String uid) {
		return getInstance().getAdminService().hasSyncedData(uid);
	}
	
	
	private static UserStore.Client getUserStore() throws TTransportException {
		THttpClient tUserStoreTrans = new THttpClient(AppProperties.getIntance().getProperty(PROP_USER_STORE_URL));
		TBinaryProtocol tUserStoreProt = new TBinaryProtocol(tUserStoreTrans);

		return new UserStore.Client(tUserStoreProt);
	}

	public AdminService getAdminService() {
		return adminService;
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}
	
	
}

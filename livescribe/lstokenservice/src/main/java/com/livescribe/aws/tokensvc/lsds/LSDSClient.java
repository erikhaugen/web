package com.livescribe.aws.tokensvc.lsds;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.aws.tokensvc.config.AppProperties;
import com.livescribe.aws.tokensvc.exception.LSDSClientException;
import com.livescribe.aws.tokensvc.exception.UserNotFoundException;

public class LSDSClient {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static final String RETURN_CODE = "returnCode";
	
	// All response code returned by LSDS
	private static final int SUCCESS_CODE 						= 0;
	private static final int UNKNOWN_SERVER_ERROR_CODE 			= -1;
	private static final int INVALID_USER_EMAIL_CODE 			= -2;
	private static final int INVALID_USER_TOKEN 				= -5;
	private static final int MISSING_REQUIRED_PARAMETER_CODE 	= -7;
	private static final int INVALID_NEW_PASSWORD				= -9;
	private static final int USER_ALREADY_EXISTS_CODE 			= -100;
	private static final int USER_DOES_NOT_EXIST 				= 1003;
	private static final int INVALID_EMAIL_ADDRESS				= 1004;
	private static final String UNIQUE_ID 						= "uniqueID";
	
	@Autowired
	private AppProperties appProperties;
	
	private XmlRpcClient client;
	
	private XmlRpcClientConfigImpl lsDsUserServiceConfig;
	
	public LSDSClient(AppProperties appProperties) throws MalformedURLException {
		this.appProperties = appProperties;
		String url = this.appProperties.getProperty("lsds.userservice.url");
		logger.debug("LSDS URL:  " + url);
		
		// prepare configs
		URL lsDsUserServiceUrl = new URL(this.appProperties.getProperty("lsds.userservice.url"));
		lsDsUserServiceConfig = new XmlRpcClientConfigImpl();
		lsDsUserServiceConfig.setEnabledForExtensions(true);
		lsDsUserServiceConfig.setServerURL(lsDsUserServiceUrl);
		
		// initialize XmlRpcClient
		client = new XmlRpcClient();
		client.setConfig(lsDsUserServiceConfig);
	}
	
	public HashMap<String, Object> sendCompleteRegistrationEmail(String email, String penSerialNumber, String penType) 
			throws XmlRpcException, LSDSClientException, UserNotFoundException {
		
		String method = "sendCompleteRegistrationEmail():  ";
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("email", email);
		paramMap.put("serialNumber", penSerialNumber);
		paramMap.put("penType", penType);
		
		Object params[] = new Object[] {"sendRegistrationEmail", paramMap};
		
		HashMap<String, Object> resultMap = (HashMap)client.execute(lsDsUserServiceConfig, "ld.submitAction", params);
		int returnCode = (Integer)resultMap.get(RETURN_CODE);
		
		switch (returnCode) {
			case USER_DOES_NOT_EXIST:
				String msg = "User '" + email + "' does not exist in WOApp system.";
				logger.error(method + msg);
				throw new UserNotFoundException(msg);
		}
		
		return resultMap;
	}
}

/**
 * 
 */
package com.livescribe.community.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.client.util.ClientFactory;
import org.apache.xmlrpc.common.XmlRpcExtensionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.livescribe.authservice.api.LSAuthenticationService;
import com.livescribe.authservice.api.impl.LSAuthenticationServiceClientWrapper;
import com.livescribe.community.config.ConfigClient;
import com.livescribe.community.dao.UserDao;
import com.livescribe.community.orm.ActiveUser;
import com.livescribe.framework.services.ResponseStatus;
import com.livescribe.framework.services.ServiceResponse;

/**
 * <p>Designed to be the interface between Community Service and the 
 * Authentication Service.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UserService implements UserDetailsService {

	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static final String KEY_PASSWORD = "userPassword";
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private XStreamMarshaller responseUnmarshaller;
	
	private String authServiceUrl;
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public UserService() {
		
		authServiceUrl = ConfigClient.getAuthServiceUrl();
	}

	/**
	 * <p></p>
	 * 
	 * @param email The email address to use when creating the user.
	 * @param password The password to use when creating the user.
	 * 
	 * @return 
	 */
	public Map<String, Object> createAccount(String email, String password) {
		
		String method = "createAccount():  ";
		
		Map<String, Object> mappedResponse = null;
		
		XmlRpcClientConfigImpl config = createXmlRpcConfig();

		XmlRpcClient client = new XmlRpcClient();			
		client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
		client.setConfig(config);
		
		String[] params = new String[]{email, password};
		
		try {
			mappedResponse = (Map)client.execute("LSAuthenticationService.createUserAccount", params);
		}
		catch (XmlRpcException xre) {
			//	TODO:  Needs to be thrown by the method to the calling class.
			logger.error(method + "XmlRpcException thrown!  " + xre);

			URL configURL = config.getServerURL();
			logger.debug(method + "Config URL:  " + configURL);
			
			if (xre instanceof XmlRpcClientException) {
				logger.error(method + "XmlRpcClientException thrown!");
			}
			if (xre instanceof XmlRpcExtensionException) {
				logger.error(method + "XmlRpcExtensionException thrown!");
			}
			xre.printStackTrace();
		}		
		return mappedResponse;
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	private XmlRpcClientConfigImpl createXmlRpcConfig() {
		
		String method = "createXmlRpcClient():  ";
		
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL(authServiceUrl));
		}
		catch (MalformedURLException mue) {
			//	TODO:  Needs to be thrown by the method to the calling class.
			logger.error(method + "MalformedURLException thrown!  " + mue);
			mue.printStackTrace();
		} 
		config.setEnabledForExtensions(true);
		config.setEnabledForExceptions(true);
		return config;
	}
	
	/**
	 * <p>Returns <code>true</code> if the user is logged in, <code>false</code> if not.</p>
	 * 
	 * @param token A login token provided to a user from a previous login.
	 * 
	 * @return <code>true</code> if the user is logged in, <code>false</code> if not.
	 */
	@Deprecated
	public boolean isUserLoggedInByToken(String token) {
		
		String method = "isUserLoggedInByToken():  ";
		
		boolean loggedIn = false;
		
		if (token != null) {
			
			XmlRpcClientConfigImpl config = createXmlRpcConfig();
	
			XmlRpcClient client = new XmlRpcClient();			
			client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
			client.setConfig(config);
				
			logger.debug(method + "authServiceUrl = '" + authServiceUrl + "'");
			
			String[] params = new String[]{token};
			
			try {
				loggedIn = (Boolean)client.execute("LSAuthenticationService.isUserLoggedInByToken", params);
			}
			catch (XmlRpcException xre) {
				//	TODO:  Needs to be thrown by the method to the calling class.
				logger.error(method + "XmlRpcException thrown!  " + xre);
	
				URL configURL = config.getServerURL();
				logger.debug(method + "Config URL:  " + configURL);
				
				if (xre instanceof XmlRpcClientException) {
					logger.error(method + "XmlRpcClientException thrown!");
				}
				if (xre instanceof XmlRpcExtensionException) {
					logger.error(method + "XmlRpcExtensionException thrown!");
				}
				xre.printStackTrace();
			}		
		}
		else {
			logger.debug(method + "No login token provided.");
		}
		return loggedIn;
	}

	/**
	 * <p>Not completed.</p>
	 * 
	 * @param token
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isUserLoggedInByToken2(String token) {
		
		boolean loggedIn = false;
		
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL(authServiceUrl));
		}
		catch (MalformedURLException mue) {
			//	TODO:  Needs to be thrown by the method to the calling class.
			logger.error("isUserLoggedInByToken():  MalformedURLException thrown!  " + mue);
			mue.printStackTrace();
		} 
		config.setEnabledForExtensions(true);
		config.setEnabledForExceptions(true);

		XmlRpcClient client = new XmlRpcClient();			
		client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
		client.setConfig(config);
		
		ClientFactory factory = new ClientFactory(client);
		LSAuthenticationService service = (LSAuthenticationService)factory.newInstance(LSAuthenticationService.class);
//		loggedIn = service.isUserLoggedInByToken(token);
		
		return loggedIn;
	}
	
	/**
	 * <p>Not completed.</p>
	 * 
	 * @param token
	 * 
	 * @return
	 */
	public boolean isUserLoggedInByToken3(String token) {
		
		String method = "isUserLoggedInByToken3():  ";
		
		String authCheckUrl = authServiceUrl + "/ws/authCheck.xml";
		String cookieValue = "tk=" + token;
		logger.debug(method + "authCheckUrl:  " + authCheckUrl);
		
		try {
			URL url = new URL(authCheckUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestProperty("Cookie", cookieValue);
//			conn.connect();
			
			int respCode = conn.getResponseCode();
			
			logger.debug(method + "response code:  " + respCode);
			
			InputStream stream = conn.getInputStream();
			StreamSource source = new StreamSource(stream);
			ServiceResponse response = (ServiceResponse)responseUnmarshaller.unmarshal(source);
			
			logger.debug(method + response.toString());
			
			if (response.getStatus().equals(ResponseStatus.OK)) {
				return true;
			}
		} 
		catch (MalformedURLException mue) {
			logger.error(method + "MalformedURLException thrown when instantiating Authentication Service URL.");
			logger.error(method + mue.getMessage());
			mue.printStackTrace();
		}
		catch (UnknownServiceException use) {
			logger.error(method + "UnknownServiceException thrown when accessing Authentication Service with token '" + token + "'.");
			logger.error(method + use.getMessage());
			use.printStackTrace();
		}
		catch (IOException ioe) {
			logger.error(method + "IOException thrown when accessing Authentication Service with token '" + token + "'.");
			logger.error(method + ioe.getMessage());
			ioe.printStackTrace();
		}
		return false;
	}
	
	/**
	 * <p>Uses {@link com.livescribe.community.dao.UserDao} to determine if a user is logged in.</p>
	 * 
	 * @param token
	 * 
	 * @return
	 */
	public boolean isUserLoggedInUsingDao(String token) {
		
		boolean loggedIn = false;
		
		ActiveUser activeUser = userDao.findActiveUserByToken(token);
		
		if (activeUser != null) {
			loggedIn = true;
		}
		return loggedIn;
	}
	
	/**
	 * @return the authServiceUrl
	 */
	public String getAuthServiceUrl() {
		return authServiceUrl;
	}

	/**
	 * <p>Returns details of a user identified by the given email address.</p>
	 * 
	 * The returned <code>UserDetails</code> is created with attributes <code>enabled</code>, 
	 * <code>accountNonExpired</code>, <code>credentialsNonExpired</code>, and <code>accountNonLocked</code>
	 * all set to <code>true</code>.  The list of <code>GrantedAuthorities</code> is empty.
	 * 
	 * @param email The email address to use when looking up the user in the database.
	 * 
	 * @return details of a user identified by the given email address.
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, DataAccessException {
		
		String method = "loadUserByUsername():  ";
		
		logger.debug(method);
		
		Map<String, Object> mappedResponse = null;
		User userDetails = null;
		
		if ((email != null) && (!"".equals(email))) {
			XmlRpcClientConfigImpl config = createXmlRpcConfig();
			
			XmlRpcClient client = new XmlRpcClient();			
			client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
			client.setConfig(config);
			
			String[] params = new String[]{email};
			
			try {
				mappedResponse = (Map)client.execute("LSAuthenticationService.getUserProfileByEmail", params);
			}
			catch (XmlRpcException xre) {
				//	TODO:  Needs to be thrown by the method to the calling class.
				logger.error(method + "XmlRpcException thrown!  " + xre);

				URL configURL = config.getServerURL();
				logger.debug(method + "Config URL:  " + configURL);
				
				if (xre instanceof XmlRpcClientException) {
					logger.error(method + "XmlRpcClientException thrown!");
				}
				if (xre instanceof XmlRpcExtensionException) {
					logger.error(method + "XmlRpcExtensionException thrown!");
				}
				xre.printStackTrace();
			}		
			String password = (String)mappedResponse.get(KEY_PASSWORD);
			
			List<GrantedAuthority> grantedAuth = new ArrayList<GrantedAuthority>();
			
			//	TODO:  Need to add groups/roles to returned details.
			
			userDetails = new User(email, password, true, true, true, true, grantedAuth);
		}
		return userDetails;
	}

	/**
	 * <p></p>
	 * 
	 * @param email The email address to use when looking up the user in the database.
	 * @param password
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> logUserInWithEmail(String email, String password) {
		
		String method = "logUserInWithEmail():  ";
		
		Map<String, Object> mappedResponse = null;
		
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL(authServiceUrl));
		}
		catch (MalformedURLException mue) {
			//	TODO:  Needs to be thrown by the method to the calling class.
			logger.error("isUserLoggedInByToken():  MalformedURLException thrown!  " + mue);
			mue.printStackTrace();
		} 
		config.setEnabledForExtensions(true);
		config.setEnabledForExceptions(true);

		XmlRpcClient client = new XmlRpcClient();			
		client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
		client.setConfig(config);
		
//		Map<String, Object> stdParams = new HashMap<String, Object>();
		
//		Object[] params = new String[]{email, password, stdParams};
		String[] params = new String[]{email, password};
		
		try {
			mappedResponse = (Map)client.execute("LSAuthenticationService.logUserIn", params);
		}
		catch (XmlRpcException xre) {
			//	TODO:  Needs to be thrown by the method to the calling class.
			logger.error(method + "XmlRpcException thrown!  " + xre);

			URL configURL = config.getServerURL();
			logger.debug(method + "Config URL:  " + configURL);
			
			if (xre instanceof XmlRpcClientException) {
				logger.error(method + "XmlRpcClientException thrown!");
			}
			if (xre instanceof XmlRpcExtensionException) {
				logger.error(method + "XmlRpcExtensionException thrown!");
			}
			xre.printStackTrace();
		}		

//		ClientFactory factory = new ClientFactory(client);
//		LSAuthenticationService service = (LSAuthenticationService)factory.newInstance(LSAuthenticationService.class);
//		mappedResponse = service.logUserIn(email, password, stdParams);
		return mappedResponse;
	}
	
	/**
	 * <p>Logs a user out identified by the given login token.</p>
	 * 
	 * @param token The login token previously assigned to the user upon login.
	 * 
	 * @return 
	 * 
	 * @see #logUserInWithEmail(String email, String password) logUserInWithEmail()
	 */
	public Map<String, Object> logUserOut(String token) {
		
		String method = "logUserOut():  ";
		
		Map<String, Object> mappedResponse = null;
		
		XmlRpcClientConfigImpl config = createXmlRpcConfig();

		XmlRpcClient client = new XmlRpcClient();			
		client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
		client.setConfig(config);
		
		String[] params = new String[]{token};
		
		try {
			mappedResponse = (Map)client.execute("LSAuthenticationService.logUserOut", params);
		}
		catch (XmlRpcException xre) {
			//	TODO:  Needs to be thrown by the method to the calling class.
			logger.error(method + "XmlRpcException thrown!  " + xre);

			URL configURL = config.getServerURL();
			logger.debug(method + "Config URL:  " + configURL);
			
			if (xre instanceof XmlRpcClientException) {
				logger.error(method + "XmlRpcClientException thrown!");
			}
			if (xre instanceof XmlRpcExtensionException) {
				logger.error(method + "XmlRpcExtensionException thrown!");
			}
			xre.printStackTrace();
		}		

		return mappedResponse;
	}
	
	/**
	 * @param authServiceUrl the authServiceUrl to set
	 */
	public void setAuthServiceUrl(String authServiceUrl) {
		this.authServiceUrl = authServiceUrl;
	}
}

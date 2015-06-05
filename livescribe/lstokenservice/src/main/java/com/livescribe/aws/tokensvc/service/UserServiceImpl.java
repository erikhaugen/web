/*
 * Created:  Sep 27, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.login.client.LoginClient;
import com.livescribe.aws.tokensvc.config.AppConstants;
import com.livescribe.aws.tokensvc.crypto.EncryptionUtils;
import com.livescribe.aws.tokensvc.exception.DuplicateEmailAddressException;
import com.livescribe.aws.tokensvc.exception.IncorrectPasswordException;
import com.livescribe.aws.tokensvc.exception.LocaleException;
import com.livescribe.aws.tokensvc.exception.LoginException;
import com.livescribe.aws.tokensvc.exception.UserNotFoundException;
import com.livescribe.aws.tokensvc.orm.consumer.CustomAuthenticatedDao;
import com.livescribe.aws.tokensvc.orm.consumer.CustomRegisteredDeviceDao;
import com.livescribe.aws.tokensvc.orm.consumer.CustomUserDao;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.LogoutException;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.login.response.LoginResponse;
import com.livescribe.framework.orm.consumer.Authenticated;
import com.livescribe.framework.orm.consumer.DeviceSetting;
import com.livescribe.framework.orm.consumer.DeviceSettingDao;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.consumer.XUserGroup;
import com.livescribe.framework.orm.consumer.XUserGroupDao;
import com.livescribe.framework.orm.consumer.XUserRole;
import com.livescribe.framework.orm.consumer.XUserRoleDao;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UserServiceImpl implements UserService, AppConstants {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private CustomAuthenticatedDao authenticatedDao;
	
	@Autowired
	private CustomUserDao userDao;
	
	@Autowired
	private CustomRegisteredDeviceDao registeredDeviceDao;
	
	@Autowired
	private DeviceSettingDao deviceSettingDao;

	@Autowired
	private XUserGroupDao xUserGroupDao;
	
	@Autowired
	private XUserRoleDao xUserRoleDao;
		
	/**
	 * <p>Default class constructor.</p>
	 * 
	 */
	public UserServiceImpl() {
		
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.UserService#deleteUser(com.livescribe.framework.orm.consumer.User)
	 */
	@Override
	@Transactional("consumer")
	public void deleteUser(User user) {
		
		//	No cascading is allowed in database.  Therefore, must
		//	programmatically delete object Lists, Sets, etc.
		try {
//			Set<UserEmail> emailList = user.getUserEmails();
//			for (UserEmail ue : emailList) {
//				userEmailDao.delete(ue);
//			}
			Set<RegisteredDevice> devices = user.getRegisteredDevices();
			for (RegisteredDevice device : devices) {
				Set<DeviceSetting> settings = device.getDeviceSettings();
				for (DeviceSetting setting : settings) {
					deviceSettingDao.delete(setting);
				}
				registeredDeviceDao.delete(device);
			}
			Set<XUserGroup> groups = user.getXUserGroups();
			for (XUserGroup group : groups) {
				xUserGroupDao.delete(group);
			}
			Set<XUserRole> roles = user.getXUserRoles();
			for (XUserRole role : roles) {
				xUserRoleDao.delete(role);
			}
			userDao.delete(user);
		}
		catch (HibernateException he) {
			String msg = "HibernateException thrown when attempting to delete User.";
			logger.debug(msg, he);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.UserService#lookupUserByEmail(java.lang.String)
	 */
	@Override
	@Transactional("consumer")
	public User findByEmail(String email) throws UserNotFoundException, DuplicateEmailAddressException {
		
		logger.debug("findByEmail():  Finding User with '" + email + "'");

		User user = userDao.findByEmail(email);
		if (user == null) {
			throw new UserNotFoundException();
		}
		
		return user;
	}

	/**
	 * <p></p>
	 * 
	 * @param email
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public User findByEmail2(String email) throws IOException {
		
		logger.debug("findByEmail():  Finding User with '" + email + "'");
		
		LoginClient client = new LoginClient();
		
		//	TODO:  Complete implementation by adding a 'findByEmail()' method
		//		   to LoginClient class.
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.UserService#findUserById(java.lang.Long)
	 */
	@Override
	@Transactional("consumer")
	public User findUserById(Long id) throws UserNotFoundException {
		
		User user = userDao.findById(id);
		if (user == null) {
			throw new UserNotFoundException();
		}
		
		return user;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.UserService#login(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional("consumer")
	public String login(String email, String password) 
			throws LoginException, ClientException, InvalidParameterException, UserNotFoundException, IncorrectPasswordException {
		
		String loginToken = null;
		
		// Call LSLoginService to log this user in
		try {
			LoginClient loginClient = new LoginClient();
			LoginResponse response = loginClient.login(email, password, "WEB");
			loginToken = response.getLoginToken();
			
		} catch (IOException e) {
			throw new ClientException(e.getMessage());
			
		} catch (com.livescribe.framework.exception.UserNotFoundException e) {
			throw new UserNotFoundException(e.getMessage());
			
		} catch (com.livescribe.framework.login.exception.IncorrectPasswordException e) {
			throw new IncorrectPasswordException(e.getMessage());
			
		} catch (com.livescribe.framework.login.exception.LoginException e) {
			throw new ClientException(e.getMessage());
			
		} catch (com.livescribe.framework.exception.DuplicateEmailAddressException e) {
			throw new LoginException(e.getMessage());
			
		} 
		
		return loginToken;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.UserService#logout(com.livescribe.framework.orm.consumer.User, java.lang.String)
	 */
	@Override
	@Transactional("consumer")
	@Deprecated
	public void logout(User user, String loginDomain) throws LogoutException {
		
		String method = "logout(User, String):  ";
		
		if (user == null) {
			String msg = method + "The given User object was 'null'.  No one to logout!";
			logger.error(msg);
			throw new LogoutException(msg);
		}
		Authenticated example = new Authenticated();
		example.setUser(user);
		example.setLoginDomain(loginDomain);
		
		List<Authenticated> authList = authenticatedDao.findByExample(example);
		
		if ((authList == null) || (authList.isEmpty())) {
			return;
		}
		
		// Handle multiple logins.
		// For each 'authenticated' record, call LoginClient to do logout
		try {
			LoginClient loginClient = new LoginClient();
			
			for (Authenticated auth : authList) {
				logger.debug("doing logout for loginToken=" + auth.getLoginToken() + " loginDomain=" + loginDomain);
				loginClient.logout(auth.getLoginToken(), loginDomain);
			}
			
		} catch (IOException e) {
			logger.error(method + e.getMessage());
			throw new LogoutException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(method + e.getMessage());
			throw new LogoutException(e.getMessage());
		} catch (ClientException e) {
			logger.error(method + e.getMessage());
			throw new LogoutException(e.getMessage());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.UserService#getLoggedInUserEmail(java.lang.String, java.lang.String)
	 */
	@Override
	public String getLoggedInUserEmail(String loginToken, String loginDomain) 
			throws IOException, InvalidParameterException, UserNotLoggedInException, ClientException {
		
		String method = "getLoggedInUserEmail():  ";
		logger.debug(method + "Get user with loginToken=" + loginToken + " loginDomain=" + loginDomain);

		LoginClient loginClient = new LoginClient();
		String loggedInEmail = loginClient.getLoggedInUserEmail(loginToken, loginDomain);
		
		return loggedInEmail;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.UserService#findUserByLoginToken(java.lang.String)
	 */
	@Transactional("consumer")
	public User findUserByLoginToken(String loginToken) throws UserNotLoggedInException {
		
		String method = "findUserByLoginToken():  ";
		
		List<Authenticated> auths = authenticatedDao.findByLoginToken(loginToken, null);
		if (auths == null || auths.isEmpty()) {
			String msg = "Login token '" + loginToken + "' not found.";
			logger.info(method + msg);
			throw new UserNotLoggedInException(msg);
		}
		
		Authenticated auth = auths.get(0);
		return auth.getUser();
	}
}

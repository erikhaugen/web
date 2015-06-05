/**
 * 
 */
package com.livescribe.admin.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.admin.client.ShareServiceClient;
import com.livescribe.admin.config.AppProperties;
import com.livescribe.admin.controller.dto.DeviceDto;
import com.livescribe.admin.controller.dto.EvernoteAuthDto;
import com.livescribe.admin.controller.dto.RegisteredUserDto;
import com.livescribe.admin.controller.dto.UserDTO;
import com.livescribe.admin.dao.CustomDocumentDao;
import com.livescribe.admin.dao.CustomRegisteredDeviceDao;
import com.livescribe.admin.dao.CustomUserDao;
import com.livescribe.admin.exception.MultipleRecordsFoundException;
import com.livescribe.admin.exception.UserNotFoundException;
import com.livescribe.admin.lookup.LookupCriteria;
import com.livescribe.admin.lookup.LookupCriteriaList;
import com.livescribe.afp.AFPException;
import com.livescribe.afp.PenID;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.lsevernotedb.Document;
import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.framework.orm.manufacturing.PenDao;
import com.livescribe.web.registration.client.RegistrationClient;
import com.livescribe.web.registration.dto.RegistrationDTO;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.response.RegistrationListResponse;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @author Mohammad M. Naqvi
 * @version 1.0
 */
public class UserServiceImpl implements UserService {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private CustomRegisteredDeviceDao registeredDeviceDao;
	
	@Autowired
	private CustomDocumentDao documentDao;
	
	@Autowired
	private CustomUserDao userDao;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private PenDao penDao;
	
	/**
	 * <p></p>
	 *
	 */
	public UserServiceImpl() {
		
	}

	/* (non-Javadoc)
	 * @see com.livescribe.admin.service.UserService#findByEmail(java.lang.String)
	 */
	@Override
	public User findByEmail(String email) throws UserNotFoundException, MultipleRecordsFoundException {
		
		String method = "findByEmail('" + email + "'):  ";
		
		User example = new User();
		example.setPrimaryEmail(email);
		List<User> list = userDao.findByExample(example);
		
		if (list == null || list.size() == 0) {
			String msg = "No user found with email address '" + email + "'.";
			logger.info(method + msg);
			throw new UserNotFoundException(msg);
		}
		
		if (list.size() > 1) {
			String msg = "Multiple records found with email address '" + email + "'.";
			logger.info(method + msg);
			throw new MultipleRecordsFoundException();
		}
		
		User user = list.get(0);
		
		//	Wrap the User with a DTO.
		UserDTO userDto = new UserDTO(user);
		
		//	Make sure the OAuth access token is valid.
		Set<Authorization> authList = user.getAuthorizations();
		if ((authList == null) || (authList.isEmpty())) {
			userDto.setValidAuthorization(false);
		}
		
		Iterator<Authorization> authIter = authList.iterator();
		while (authIter.hasNext()) {
			Authorization auth = authIter.next();
			logger.debug("Checking OAuth access token for EN user '" + auth.getEnUsername() + "'.");
			Date expDate = auth.getExpiration();
			Date nowDate = new Date();
			long expLong = expDate.getTime();
			long nowLong = nowDate.getTime();
			if (expLong > nowLong) {
				userDto.setValidAuthorization(true);
			}
		}
		
		return list.get(0);
	}

	/* (non-Javadoc)
	 * @see com.livescribe.admin.service.UserService#findByUID(java.lang.String)
	 */
	@Override
	public User findByUID(String UID) {
		
	    String method = "findByUID(" + UID + "):  ";
        User user = userDao.findByUid(UID);
        
        if (user == null) {
            logger.info(method + "No user found with UID '" + UID + "'.");
        }
        
        return user;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.admin.service.UserService#findByPenDisplayId(java.lang.String)
	 */
	@Override
	public User findByPenDisplayId(String displayId) throws AFPException, MultipleRecordsFoundException {
		
		String method = "findByPenDisplayId(" + displayId + "):  ";

		//	Convert display ID to serial number.
		PenID penId = new PenID(displayId);
		long penSerial = penId.getId();
		String penSerialStr = String.valueOf(penSerial);
		
		User user = findByPenSerialNumber(penSerialStr);
		
		if (user == null) {
			logger.info(method + "No user found registered to pen with display ID '" + displayId + "'.");
		}
		
		return user;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.admin.service.UserService#findByPenSerialNumber()
	 */
	@Override
	public User findByPenSerialNumber(String penSerial) throws MultipleRecordsFoundException {
		
		String method = "findByPenSerialNumber(" + penSerial + "):  ";
		
		RegisteredDevice regDev = this.registeredDeviceDao.findByPenSerial(penSerial);
		
		if (regDev == null) {
			logger.info(method + "No user found registered to pen with serial number '" + penSerial + "'.");
			return null;
		}
		
		User user = regDev.getUser();
		
		return user;
	}

	@Override
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public List<User> findByCriteria(LookupCriteria criteria) {
		String method = "findByCriteria():  ";
		
		List<User> userList = userDao.findByLookupCriteria(criteria);
		logger.debug(method + "Found " + userList.size() + " users.");
		
		return userList;
	}

	@Override
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public List<User> findByCriteriaList(LookupCriteriaList criteriaList) {
		String method = "findByCriteriaList():  ";
		
		List<User> userList = userDao.findByLookupCriteriaList(criteriaList);
		logger.debug(method + "Found " + userList.size() + " users.");
		
		return userList;
	}
	
	@Override
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public List<User> findByAuthorization() {
		String method = "findByAuthorization():  ";
		
		List<User> userList = userDao.findByAuthorization();
		logger.debug(method + "Found " + userList.size() + " users.");
		
//		// Initialize User object
//		for (User user : userList) {
//			initializeUser(user);
//		}
		
		return userList;
	}

	
	@Override
	@Transactional(value="consumer", rollbackFor=Exception.class)
	public void deleteConsumerUser(String email) throws UserNotFoundException, ClientException {
		
		String method = "deleteConsumerUser()";
		logger.info("BEFORE - " + method);
		
		User user = this.userDao.findByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("User '" + email + "' not found.");
		}
		String uid = user.getUid();
		Set<RegisteredDevice> registeredDevices = user.getRegisteredDevices();
		
		//--------------------------------------------------
		//	Clear sync'd data
		//--------------------------------------------------
		
		//--------------------------------------------------
		//	If the user already unregistered their pen ...
		if ((registeredDevices == null) || (registeredDevices.isEmpty())) {
			ShareServiceClient client = null;
			try {
				client = new ShareServiceClient("");
				logger.debug(method + " - " + uid + " - Deleting documents by UID.");
				client.deleteDocumentsByUid(uid);
			} catch (URISyntaxException use) {
				String msg = "URISyntaxException thrown.  ";
				logger.error(method + " - " + msg, use);
				throw new ClientException(msg, use);
			} catch (IOException ioe) {
				String msg = "IOException thrown.  ";
				logger.error(method + " - " + msg, ioe);
				throw new ClientException(msg, ioe);
			}
		}
		//--------------------------------------------------
		//	... otherwise, use the pen's display ID and 
		//	user's UID to delete the sync'd data.
		else {
			logger.debug(method + " - " + uid + " - Deleting documents by UID and pen display ID.");
			
			// Delete synced data
			try {
				for (RegisteredDevice rd : registeredDevices) {
					Pen pen = penDao.findById(rd.getDeviceId());
					if (pen != null) {
						adminService.clearSyncDocsFromUserAndPen(uid, pen.getDisplayId());
					}
				}
			} catch (URISyntaxException e) {
				throw new ClientException(e.getMessage()); 
			} 
		}
		
		//--------------------------------------------------
		//	Handle Vector registrations.
		//--------------------------------------------------
		
		//	Create the RegistrationClient.
		RegistrationClient regClient = null;
		try {
			regClient = new RegistrationClient();
		} catch (IOException ioe) {
			String msg = "IOException thrown when creating RegistrationClient.";
			logger.error(method + " - " + msg);
			throw new ClientException(msg, ioe);
		}
		
		try {
			//	Find any Vector registrations for the given email address.
			RegistrationListResponse regListResponse = regClient.findRegistrationsListByEmail(email);
			List<RegistrationDTO> registrations = regListResponse.getRegistrations();
			
			//	If found, delete them all.
			if ((registrations != null) && (registrations.size() > 0)) {
				regClient.deleteByEmail(email);
			}
			
		//	This SHOULD NOT happen.  The 'email' parameter should be checked prior to invoking this method.
		} catch (InvalidParameterException ipe) {
			String msg = "InvalidParameterException thrown when looking up Vector registrations with email address '" + email + "'.";
			logger.error(method + " - " + msg);

		//	Don't care if no Vector registrations were found here.
		} catch (RegistrationNotFoundException rnfe) {
			String msg = "No Vector registrations found for email address '" + email + "'.";
			logger.info(method + " - " + msg);
		}
		
		//	After successfully clearing sync'd data, call UserDao to delete 
		//	user in consumer db
		userDao.delete(user);
		
		logger.info("AFTER - " + method);
	}

	@Override
	@Transactional("evernote")
	public String getUIDByDocumentId(Long docId) {

		if (null == docId) {
			return null;
		}
		
		Document doc = documentDao.findById(docId);
		if (null == doc) {
			return null;
		}
		return doc.getUser();
	}

	@Override
	public List<User> findByPartialEmail(String partialEmail) throws UserNotFoundException {
		
		List<User> users = userDao.findByPartialEmail(partialEmail);
		
		if (null == users || users.isEmpty()) {
			throw new UserNotFoundException("No user found with email containing " + partialEmail);
		}
		return users;
	}

	@Override
	public List<RegisteredUserDto> findUserDtoByCriteriaList(LookupCriteriaList criteriaList) {

		List<User> usersList = findByCriteriaList(criteriaList);
		if (null == usersList || usersList.isEmpty()) {
			return null;
		}
		
		// Found at least one user, prepare the response..
		List<RegisteredUserDto> userDtoList = new ArrayList<RegisteredUserDto>();
		for (User user : usersList) {
			RegisteredUserDto userDto = new RegisteredUserDto();
			userDto.setPrimaryEmail(user.getPrimaryEmail());
			userDto.setUid(user.getUid());
			
			// Populate and Set EN authorizations for this user
			Set<Authorization> evernoteAuthorizations = user.getAuthorizations();
			for (Authorization auth : evernoteAuthorizations) {
				userDto.getEvernoteAuthorizations().add(new EvernoteAuthDto(auth));
			}
			
			// Populate and Set devices registered to this user
			Set<RegisteredDevice> registeredDevices = user.getRegisteredDevices();
			for (RegisteredDevice device : registeredDevices) {
				userDto.getRegisteredDevices().add(new DeviceDto(device));
			}
			
			userDtoList.add(userDto);
		}
		
		return userDtoList;
	}
}

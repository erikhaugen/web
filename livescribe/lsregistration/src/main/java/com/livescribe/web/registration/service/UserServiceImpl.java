/**
 * 
 */
package com.livescribe.web.registration.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.framework.orm.vectordb.CustomRegistrationDao;
import com.livescribe.framework.orm.vectordb.CustomRegistrationHistoryDao;
import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.livescribe.web.registration.dto.UserDto;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;

/**
 * @author Mohammad M. Naqvi
 * @since 1.3
 *
 */
public class UserServiceImpl implements UserService {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private CustomRegistrationDao registrationDao;
	
	@Autowired
	private CustomRegistrationHistoryDao registrationHistoryDao;

	/**
	 * 
	 */
	public UserServiceImpl() {
		// TODO Auto-generated constructor stub
	}



	@Override
	@Transactional("registration")
	public Set<UserDto> findVectorUsersByPartialEmail(String email) {
		String method = "findVectorUsersByPartialEmail(" + email + ")";
		
		Set<UserDto> usersSet = new HashSet<UserDto>();
		List<Registration> registrationsList = null;
		List<RegistrationHistory> registrationsHistoryList = null;
		
		// Find users from the 'Registration' table.
		try {
			registrationsList = registrationDao.findByPartialEmail(email);
		} catch (RegistrationNotFoundException e) {
			logger.debug(method + "No registration found!!");
		}
		
		if (null != registrationsList && !registrationsList.isEmpty()) {
			logger.debug(method + "Number of registration(s) found = " + registrationsList.size());
			for (Registration reg : registrationsList) {
				usersSet.add(new UserDto(reg));
			}
		}
		
		// Find users from the 'RegistrationHistory' table.
		try {
			registrationsHistoryList = registrationHistoryDao.findByPartialEmail(email);
		} catch (RegistrationNotFoundException e) {
			logger.debug(method + "No registration history found!!");
		}
		
		if (null != registrationsHistoryList && !registrationsHistoryList.isEmpty()) {
			logger.debug(method + "Number of registration history found = " + registrationsHistoryList.size());
			for (RegistrationHistory regHistory : registrationsHistoryList) {
				usersSet.add(new UserDto(regHistory));
			}
		}

		logger.debug(method + "Number of unique user(s) found = " + usersSet.size());
		return usersSet;
	}

	/**
	 * <p>Returns a <code>Set</code> of user accounts identified by the given
	 * email address.</p>
	 * 
	 * <p>Searches both the <code>registration</code> and <code>registration_history</code>
	 * tables.</p>
	 * 
	 * @param email The email address to use in the search.
	 * 
	 * @return a <code>Set</code> of user accounts.&nbsp;&nbsp;<u>Never</u> returns 
	 * <code>null</code>.
	 * 
	 * @see com.livescribe.web.registration.service.UserService#findVectorUsersByEmail(java.lang.String)
	 */
	@Override
	@Transactional("registration")
	public Set<UserDto> findVectorUsersByEmail(String email) {
		String method = "findVectorUserByEmail(" + email + ")";
		
		Set<UserDto> usersSet = new HashSet<UserDto>();
		List<Registration> registrationsList = null;
		List<RegistrationHistory> registrationsHistoryList = null;
		
		// Find users from the 'Registration' table.
		try {
			registrationsList = registrationDao.findByEmail(email);
		} catch (RegistrationNotFoundException e) {
			logger.debug(method + "No registration found!!");
		}
		
		if (null != registrationsList && !registrationsList.isEmpty()) {
			logger.debug(method + "Number of registration(s) found = " + registrationsList.size());
			for (Registration reg : registrationsList) {
				usersSet.add(new UserDto(reg));
			}
		}
		
		// Find users from the 'RegistrationHistory' table.
		try {
			registrationsHistoryList = registrationHistoryDao.findByEmail(email);
		} catch (RegistrationNotFoundException e) {
			logger.debug(method + "No registration history found!!");
		}
		
		if (null != registrationsHistoryList && !registrationsHistoryList.isEmpty()) {
			logger.debug(method + "Number of registration history found = " + registrationsHistoryList.size());
			for (RegistrationHistory regHistory : registrationsHistoryList) {
				usersSet.add(new UserDto(regHistory));
			}
		}

		logger.debug(method + "Number of unique user(s) found = " + usersSet.size());
		return usersSet;
	}

}

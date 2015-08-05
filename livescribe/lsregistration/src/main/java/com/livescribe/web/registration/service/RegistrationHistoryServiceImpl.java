package com.livescribe.web.registration.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.vectordb.CustomRegistrationDao;
import com.livescribe.framework.orm.vectordb.CustomRegistrationHistoryDao;
import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.util.PenId;
import com.livescribe.web.registration.util.RegistrationHistoryFactory;

public class RegistrationHistoryServiceImpl implements  RegistrationHistoryService {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private CustomRegistrationDao registrationDao;
	
	@Autowired
	private CustomRegistrationHistoryDao registrationHistoryDao;
	
	@Override
	@Transactional("registration")
	public List<RegistrationHistory> find(String appId, String penSerial, String email) throws RegistrationNotFoundException {

		String method = "find(appId, penSerialNumber, email)";
		
		PenId penId = null;
		String penSerialNumber;
		try {
			penId = PenId.getPenIdObject(penSerial);
			penSerialNumber = String.valueOf(penId.getId());
			
		} catch (InvalidParameterException e) {
			logger.info(method + " Error when converting penSerial to PenId object.");
			penSerialNumber = penSerial;
		}
		
		// Find the current registration
		Registration currentRegistration = null;
		try {
			currentRegistration = registrationDao.find(appId, penSerialNumber, email);
			
		} catch (MultipleRecordsFoundException e) {
			logger.info(method + e.getMessage());
		}
		
		// Find all the registration history
		List<RegistrationHistory> registrationHistoryList;
		try {
			registrationHistoryList = registrationHistoryDao.find(appId, penSerialNumber, email);
			
		} catch (RegistrationNotFoundException e) {
			// If there is no history record found
			registrationHistoryList = new ArrayList<RegistrationHistory>();
		}
		
		if (currentRegistration != null) {
			RegistrationHistory rh = RegistrationHistoryFactory.create(currentRegistration);
			registrationHistoryList.add(0, rh); // add current registrations to top of the list
		}
		
		return registrationHistoryList;
	}

	@Override
	@Transactional("registration")
	public List<RegistrationHistory> findByPenSerial(String penSerial) throws RegistrationNotFoundException {

		String method = "findByPenSerial(penSerial)";
		
		PenId penId = null;
		String penSerialNumber;
		try {
			penId = PenId.getPenIdObject(penSerial);
			penSerialNumber = String.valueOf(penId.getId());
			
		} catch (InvalidParameterException e) {
			logger.info(method + " Error when converting penSerial to PenId object.");
			penSerialNumber = penSerial;
		}
		
		
		// Find current registrations for the pen
		List<Registration> currentRegistrations = null;
		try {
			currentRegistrations = registrationDao.findByPenSerial(penSerialNumber);
			
		} catch (RegistrationNotFoundException e) {
			// Could not found any current registration for the user, this might be the case when  all the pens got unregistered.
		}
		
		// Find all the registration history
		List<RegistrationHistory> registrationHistoryList;
		try {
			registrationHistoryList = registrationHistoryDao.findByPenSerial(penSerialNumber);
			
		} catch (RegistrationNotFoundException e) {
			// If there is no history record found
			registrationHistoryList = new ArrayList<RegistrationHistory>();
		}
		
		// If we cannot find any current registration and any history registration, we throw exception here
		if ((currentRegistrations == null || currentRegistrations.size() == 0) 
				&& registrationHistoryList.size() == 0) {
			String msg = "No registration history found for Pen '" + penSerial + "'.";
			logger.info(method + " " + msg);
			throw new RegistrationNotFoundException(msg);
		}
		
		if (currentRegistrations != null) {
			List<RegistrationHistory> tempRegHistoryList = new ArrayList<RegistrationHistory>();
			
			for (Registration curReg : currentRegistrations) {
				RegistrationHistory rh = RegistrationHistoryFactory.create(curReg);
				tempRegHistoryList.add(rh);
			}
			
			registrationHistoryList.addAll(0, tempRegHistoryList); // add current registrations to top of the list
		}
		
		return registrationHistoryList;
	}

	@Override
	@Transactional("registration")
	public List<RegistrationHistory> findByEmail(String email) throws RegistrationNotFoundException {

		String method = "findByPenSerial(penSerial)";
		
		// Find current registrations for the user
		List<Registration> currentRegistrations = null;
		try {
			currentRegistrations = registrationDao.findByEmail(email);
			
		} catch (RegistrationNotFoundException e) {
			// Could not found any current registration for the user, this might be the case when  all the pens got unregistered.
		}
		
		// Find all the registration history
		List<RegistrationHistory> registrationHistoryList;
		try {
			registrationHistoryList = registrationHistoryDao.findByEmail(email);
			
		} catch (RegistrationNotFoundException e) {
			// If there is no history record found
			registrationHistoryList = new ArrayList<RegistrationHistory>();
		}
		
		// If we cannot find any current registration and any history registration, we throw exception here
		if ((currentRegistrations == null || currentRegistrations.size() == 0) 
				&& registrationHistoryList.size() == 0) {
			String msg = "No registration history found for user '" + email + "'.";
			logger.info(method + " " + msg);
			throw new RegistrationNotFoundException(msg);
		}
		
		if (currentRegistrations != null) {
			List<RegistrationHistory> tempRegHistoryList = new ArrayList<RegistrationHistory>();
			
			for (Registration curReg : currentRegistrations) {
				RegistrationHistory rh = RegistrationHistoryFactory.create(curReg);
				tempRegHistoryList.add(rh);
			}
			
			registrationHistoryList.addAll(0, tempRegHistoryList); // add current registrations to top of the list
		}
		
		return registrationHistoryList;
	}

	@Override
	@Transactional("registration")
	public void save(Registration registration) {
		
		RegistrationHistory regHistory = RegistrationHistoryFactory.create(registration);
		registrationHistoryDao.merge(regHistory);
	}
}

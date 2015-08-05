/**
 * Created:  Aug 15, 2013 10:07:20 AM
 */
package com.livescribe.web.registration.service;

import java.util.Date;
import java.util.List;

import com.livescribe.base.StringUtils;
import com.livescribe.web.registration.service.support.EMailNotifications;
import net.sf.json.JSONObject;
import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.manufacturing.CustomPenDao;
import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.framework.orm.vectordb.CustomRegistrationDao;
import com.livescribe.framework.orm.vectordb.CustomRegistrationHistoryDao;
import com.livescribe.framework.orm.vectordb.CustomWarrantyDao;
import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.livescribe.framework.orm.vectordb.Warranty;
import com.livescribe.web.registration.controller.RegistrationData;
import com.livescribe.web.registration.exception.DeviceNotFoundException;
import com.livescribe.web.registration.exception.RegistrationAlreadyExistedException;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.exception.UnsupportedPenTypeException;
import com.livescribe.web.registration.util.PenId;
import com.livescribe.web.registration.util.RegistrationFactory;
import com.livescribe.web.registration.util.RegistrationHistoryFactory;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @author Mohammad M. Naqvi (added methods to search registrations by 'partial' parameters)
 * @version 1.2
 */
public class RegistrationServiceImpl implements RegistrationService {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private RegistrationHistoryService registrationHistoryService;
	
	@Autowired
	private CustomPenDao penDao;
	
	@Autowired
	private CustomRegistrationDao registrationDao;
	
	@Autowired
	private CustomRegistrationHistoryDao registrationHistoryDao;
	
	@Autowired
	private CustomWarrantyDao warrantyDao;

    @Autowired
    private EMailNotifications emailNotifications;

	/**
	 * <p></p>
	 * 
	 */
	public RegistrationServiceImpl() {
	}

	/* (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#delete(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional("registration")
	public void delete(String appId, String penSerialNumber, String email) throws RegistrationNotFoundException, MultipleRecordsFoundException {
		
		String method = "delete(appId, penSerialNumber, email)";
		
		logger.info("BEFORE - " + method);
		
		PenId penId;
		try {
			penId = PenId.getPenIdObject(penSerialNumber);
			registrationDao.delete(appId, String.valueOf(penId.getId()), email);
			
		} catch (InvalidParameterException e) {
			logger.info(method + " Error when converting penSerial to PenId object.");
			throw new RegistrationNotFoundException("No registration found for pen '" + penSerialNumber + "'.");
		}
		
		logger.info("AFTER - " + method);
	}
	
	@Transactional("registration")
	public void deleteByEmail(String email) {
		
		String method = "delete(email)";
		
		logger.info("BEFORE - " + method + " - " + email);
		
		//	Find all of the registration records.
		List<Registration> regList = null;
		try {
			regList = findByEmail(email);
		} catch (RegistrationNotFoundException rnfe) {
			logger.info(method + " - " + email + " - No registration records to save or delete.");
			//	Nothing to save or delete!
			return;
		}
		
		if ((regList != null) && (!regList.isEmpty())) {
			for (Registration registration : regList) {
				
				//	Save the registration as 'history' record.
				registrationHistoryService.save(registration);
				
				//	Delete the registration record.
				registrationDao.delete(registration);
			}
		}
		
		logger.info("AFTER - " + method + " - " + email);
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#find(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional("registration")
	public Registration find(String appId, String penSerialNumber, String email) throws RegistrationNotFoundException, MultipleRecordsFoundException {
		
		String method = "find(appId, penSerialNumber, email)";
		
		logger.info("BEFORE - " + method);
		
		PenId penId = null;
		Registration reg = null;
		try {
			penId = PenId.getPenIdObject(penSerialNumber);
			
			reg = registrationDao.find(appId, String.valueOf(penId.getId()), email);
			
		} catch (InvalidParameterException e) {
			logger.info(method + " Error when converting penSerial to PenId object.");
			throw new RegistrationNotFoundException("No registration found for pen '" + penSerialNumber + "'.");
		}
		
		logger.info("AFTER - " + method);

		return reg;
	}
	
	/* (non-Javadoc)
	 * 
	 * To be used with Camel implementation.
	 * 
	 * @see com.livescribe.web.registration.service.RegistrationService#find(org.apache.camel.Exchange)
	 */
	@Override
	@Transactional("registration")
	public void find(Exchange exchange) {
		
		String method = "find(Exchange)";
		
		logger.info("BEFORE - " + method);
		long start = System.currentTimeMillis();
		
		Registration example = exchange.getIn().getBody(Registration.class);
		
		Registration result = null;
		try {
			result = registrationDao.find(example.getAppId(), example.getPenSerial(), example.getEmail());
		} catch (MultipleRecordsFoundException e) {
			String msg = "MultipleRecordsFoundException thrown";
			logger.error(method + " - " + msg);
			return;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "RegistrationNotFoundException thrown";
			logger.error(method + " - " + msg);
			return;
		}
		
		if (result == null) {
			logger.debug(method + " - No record found.");
			exchange.getOut().setHeader("existing", new Boolean(false));
//			exchange.getOut().setBody(example, Registration.class);
		}
		else {
			logger.debug(method + " - Found 1 record.");
			exchange.getOut().setHeader("existing", new Boolean(true));
			exchange.getOut().setBody(result, Registration.class);
		}
		
		long end = System.currentTimeMillis();
		long duration = end - start;
		logger.info("AFTER - " + method + " - Completed in " + duration + " milliseconds.");
	}

	/* (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#find(com.livescribe.web.registration.controller.RegistrationData)
	 */
	@Override
	@Transactional("registration")
	public Registration find(RegistrationData data) throws MultipleRecordsFoundException {
		
		String method = "find(RegistrationData)";
		
		logger.info("BEFORE - " + method);
		
		Registration result = null;
		try {
			PenId penId = data.getPenId();
			result = registrationDao.find(data.getAppId(), String.valueOf(penId.getId()), data.getEmail());
			
		} catch (RegistrationNotFoundException e) {
//			return null;
			
		} catch (InvalidParameterException e) {
			logger.error(method + " Error when converting penSerial to PenId object.");
		} 

		logger.info("AFTER - " + method);
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#findByPenSerial(java.lang.String)
	 */
	@Override
	@Transactional("registration")
	public List<Registration> findByPenSerial(String penSerial) throws RegistrationNotFoundException {
		
		String method = "findByPenSerial()";
		
		logger.info("BEFORE - " + method);
		
		PenId penId = null;
		try {
			penId = PenId.getPenIdObject(penSerial);
			
		} catch (InvalidParameterException e) {
			logger.info(method + " Error when converting penSerial to PenId object.");
			return registrationDao.findByPenSerial(penSerial);
		}
		
		List<Registration> list = registrationDao.findByPenSerial(String.valueOf(penId.getId()));
		
		logger.info("AFTER - " + method);

		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#findByDisplayId(java.lang.String)
	 */
	@Override
	@Transactional("registration")
	public List<Registration> findByDisplayId(String displayId) throws RegistrationNotFoundException {
		return registrationDao.findByDisplayId(displayId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#findByAppId(java.lang.String)
	 */
	@Override
	@Transactional("registration")
	public List<Registration> findByAppId(String appId) throws RegistrationNotFoundException {
		return registrationDao.findByAppId(appId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#findByEmail(java.lang.String)
	 */
	@Override
	@Transactional("registration")
	public List<Registration> findByEmail(String email) throws RegistrationNotFoundException {
		return registrationDao.findByEmail(email);
	}

	@Override
	@Transactional("registration")
	public List<Registration> findByPartialEmail(String email) throws RegistrationNotFoundException {
		return registrationDao.findByPartialEmail(email);
	}

	@Override
	@Transactional("registration")
	public List<Registration> findByPartialPenSerial(String partialPenSerial) throws RegistrationNotFoundException {
		return registrationDao.findByPartialPenSerial(partialPenSerial);
	}

	@Override
	@Transactional("registration")
	public List<Registration> findByPartialPenDisplayId( String partialPenDisplayId) throws RegistrationNotFoundException {
		return registrationDao.findByPartialDisplayId(partialPenDisplayId);
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#isDisplayIdValid(java.lang.String)
	 */
	@Override
	@Transactional("manufacturing")
	public boolean isPenSerialNumberValid(String penSerialNumber) throws MultipleRecordsFoundException, UnsupportedPenTypeException {
		
		String method = "isPenSerialNumberValid()";
		
		logger.info("BEFORE - " + method + " - " + penSerialNumber);

		Pen pen = penDao.findBySerialNumber(penSerialNumber);
		if (pen == null) {
			return false;
		}
		
		if (!"Vector".equalsIgnoreCase(pen.getPenType())) {
			logger.info(method + " The pen '" + penSerialNumber + "' is not a Vector pen.");
			throw new UnsupportedPenTypeException("The pen '" + penSerialNumber + "' is not a Vector pen.");
		}
		
		logger.info("AFTER - " + method + " - " + penSerialNumber);

		return true;
	}

    @Transactional("manufacturing")
    private String getPenTypeNameByPenSerialNumber(String penSerialNumber){
        final String method = "getPenTypeNameByPenSerialNumber()";

        logger.info("BEFORE - " + method + " - " + penSerialNumber);

        String penType = "Unknown";;

        try {
            Pen pen = penDao.findBySerialNumber(penSerialNumber);
            if (pen != null) {
                // convert to title case;
                penType = org.springframework.util.StringUtils.capitalize( pen.getPenType().toLowerCase() );
            }
        } catch (Exception e) {

        }

        logger.info("AFTER - " + method + " - " + penSerialNumber);
        return penType;
    }
	
	/* (non-Javadoc)
	 * 
	 * To be used with Camel implementation.
	 * 
	 * @see com.livescribe.web.registration.service.RegistrationService#save(org.apache.camel.Exchange)
	 */
	@Override
	public void save(Exchange exchange) {
		
		String method = "save(Exchange)";
		
		logger.info("BEFORE - " + method);
		
		Registration instance = exchange.getIn().getBody(Registration.class);
		
		registrationDao.merge(instance);

		logger.info("AFTER - " + method);
	}

	/* (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#save(com.livescribe.web.registration.controller.RegistrationData)
	 */
	@Override
	@Transactional("registration")
	public Registration save(RegistrationData data) {
		
		String method = "save(RegistrationData)";

		logger.info("BEFORE - " + method);
		
		Registration registration = RegistrationFactory.create(data);
		registrationDao.persist(registration);

		logger.info("AFTER - " + method);

		return registration;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#update(com.livescribe.web.registration.controller.RegistrationData)
	 */
	@Override
	@Transactional("registration")
	public Registration saveOrUpdate(RegistrationData data) throws RegistrationNotFoundException, MultipleRecordsFoundException {
		
		String method = "update()";

		logger.info("BEFORE - " + method);

		Registration found = find(data);
		
		Registration merged = null;
		if (found == null) {
			save(data);
		}
		else {
			merged = registrationDao.merge(found);
		}
		
		logger.info("AFTER - " + method);
		
		return merged;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#isRegisteringFirstTime(com.livescribe.web.registration.controller.RegistrationData)
	 */
	@Override
	@Transactional("registration")
	public boolean isRegisteringFirstTime(RegistrationData data) {
		
		String method = "isRegisteringFirstTime()";
		
		logger.info("BEFORE - " + method);

		PenId penId;

		try {
			penId = data.getPenId();
			
			// Look up in 'warranty' table to determine whether the pen is registering for the first time
			Warranty warranty = warrantyDao.findByPenSerial(String.valueOf(penId.getId()));
			
		} catch (InvalidParameterException e) {
			logger.info(method + " Error when converting penSerial '" + data.getPenSerial() + "'to PenId object.");
			
		} catch (RegistrationNotFoundException e) {
			return true;
			
		} catch (MultipleRecordsFoundException e) {
			return false;
		}
		
		logger.info("AFTER - " + method);

		// Found a warranty record of the pen, so it's already registered before
		return false;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#register(com.livescribe.web.registration.controller.RegistrationData)
	 */
	@Override
	@Transactional("registration")
	public Registration register(RegistrationData data) 
			throws RegistrationAlreadyExistedException, MultipleRecordsFoundException, DeviceNotFoundException, UnsupportedPenTypeException {
		
		String method = "register()";
		
		logger.info("BEFORE - " + method);

		// Check if the the pen is currently registered
		Registration registration = find(data);
		if (registration != null) {
			throw new RegistrationAlreadyExistedException("The pen " + data.getPenSerial() + " is already registered to appId=" + data.getAppId());
		}
		
		// Ensure the given display ID represents a valid pen.
		PenId penId;
		try {
			penId = data.getPenId();
			boolean valid = isPenSerialNumberValid(String.valueOf(penId.getId()));
			if (!valid) {
				throw new DeviceNotFoundException("Pen Serial Number '" + String.valueOf(penId.getId()) + "' not found in database.");
			}


            String penTypeName = getPenTypeNameByPenSerialNumber( String.valueOf(penId.getId()) );
            emailNotifications.pushEmail("SmartpenRegistration", data, penTypeName);

		} catch (InvalidParameterException e) {
			logger.info(method + " Error when converting penSerial '" + data.getPenSerial() + "'to PenId object.");
			throw new DeviceNotFoundException("Pen Serial Number '" + data.getPenSerial() + "' not found in database.");
			
		} catch (UnsupportedPenTypeException e) {
			logger.info(method + e.getMessage());
			throw e;
		}
		
		
		// Save the registration
		registration = save(data);
		
		// Determine whether this is the first time the pen is registered
		if (isRegisteringFirstTime(data)) {
			// For the first time the pen get registered, we need to save the registration in to warranty table also
			Warranty warranty = new Warranty();
			warranty.setRegistrationId(registration.getRegistrationId());
			warranty.setAppId(registration.getAppId());
			warranty.setEdition(registration.getEdition());
			warranty.setPenSerial(registration.getPenSerial());
			warranty.setDisplayId(registration.getDisplayId());
			warranty.setPenName(registration.getPenName());
			warranty.setFirstName(registration.getFirstName());
			warranty.setLastName(registration.getLastName());
			warranty.setEmail(registration.getEmail());
			warranty.setLocale(registration.getLocale());
			warranty.setCreated(new Date());
			warranty.setLastModified(warranty.getCreated());
			warranty.setLastModifiedBy("LS Registration Service");
			warrantyDao.merge(warranty);
		}
		
		logger.info("AFTER - " + method);

		return registration;
	}

	/*
	 * (non-Javadoc)
	 * @see com.livescribe.web.registration.service.RegistrationService#unregister(com.livescribe.web.registration.controller.RegistrationData)
	 */
	@Override
	@Transactional("registration")
	public void unregister(RegistrationData data) throws RegistrationNotFoundException, MultipleRecordsFoundException {
		
		String method = "unregister()";
		
		logger.info("BEFORE - " + method);
		
		PenId penId;
		try {
			penId = data.getPenId();
		} catch (InvalidParameterException e) {
			logger.info(method + " Error when converting penSerial '" + data.getPenSerial() + "'to PenId object.");
			throw new RegistrationNotFoundException("No registration found for penSerial '" + data.getPenSerial() + "'.");
		}
		
		// Find registration
		Registration registration = registrationDao.find(data.getAppId(), String.valueOf(penId.getId()), data.getEmail());
		
		// Create new registration history record
		RegistrationHistory regHistory = new RegistrationHistory();
		regHistory.setRegistrationId(registration.getRegistrationId());
		regHistory.setAppId(registration.getAppId());
		regHistory.setEdition(registration.getEdition());
		regHistory.setPenSerial(registration.getPenSerial());
		regHistory.setDisplayId(registration.getDisplayId());
		regHistory.setPenName(registration.getPenName());
		regHistory.setFirstName(registration.getFirstName());
		regHistory.setLastName(registration.getLastName());
		regHistory.setEmail(registration.getEmail());
		regHistory.setLocale(registration.getLocale());
		regHistory.setCountry(registration.getCountry());
		regHistory.setOptIn(registration.getOptIn());
		regHistory.setRegistrationDate(registration.getCreated());
		regHistory.setCreated(new Date());
		regHistory.setLastModified(regHistory.getCreated());
		regHistory.setLastModifiedBy("LS Registration Service");
		registrationHistoryDao.merge(regHistory);
		
		// Delete the registration record
		registrationDao.delete(registration);
		
		logger.info("AFTER - " + method);
	}
}

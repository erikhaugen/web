package com.livescribe.web.registration.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.vectordb.CustomWarrantyDao;
import com.livescribe.framework.orm.vectordb.Warranty;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.util.PenId;

public class WarrantyServiceImpl implements WarrantyService {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private CustomWarrantyDao warrantyDao;
	
	@Override
	@Transactional("registration")
	public Warranty find(String appId, String penSerialNumber, String email) throws RegistrationNotFoundException, MultipleRecordsFoundException {

		String method = "find()";
		
		PenId penId = null;
		try {
			penId = PenId.getPenIdObject(penSerialNumber);
			
		} catch (InvalidParameterException e) {
			logger.info(method + " Error when converting penSerial to PenId object.");
			return warrantyDao.find(appId, penSerialNumber, email);
		}
		
		return warrantyDao.find(appId, String.valueOf(penId.getId()), email);
	}

	@Override
	@Transactional("registration")
	public Warranty findByPenSerial(String penSerialNumber) throws RegistrationNotFoundException, MultipleRecordsFoundException {

		String method = "findByPenSerial()";
		
		PenId penId = null;
		try {
			penId = PenId.getPenIdObject(penSerialNumber);
			logger.debug(method + " - penId.toString() = '" + penId.toString() + "'");
			logger.debug(method + " - penId.getId() = '" + penId.getId() + "'");
		} catch (InvalidParameterException e) {
			logger.warn(method + " Error when converting penSerial to PenId object.");
			return warrantyDao.findByDisplayId(penSerialNumber);
		}
		
		return warrantyDao.findByPenSerial(String.valueOf(penId.getId()));
	}

	@Override
	@Transactional("registration")
	public List<Warranty> findByEmail(String email) throws RegistrationNotFoundException {

		return warrantyDao.findByEmail(email);
	}

	@Override
	@Transactional("registration")
	public Warranty findByPenDisplayId(String penDisplayId) throws RegistrationNotFoundException, MultipleRecordsFoundException {
		
		Warranty warranty = warrantyDao.findByDisplayId(penDisplayId);
		return warranty;
	}
}

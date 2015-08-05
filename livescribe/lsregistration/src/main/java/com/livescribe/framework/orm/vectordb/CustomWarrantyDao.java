package com.livescribe.framework.orm.vectordb;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;

public class CustomWarrantyDao extends WarrantyDao {

	/**
	 * <p>Default constructor</p>
	 */
	public CustomWarrantyDao() {
		super();
	}
	
	/**
	 * <p></p>
	 * 
	 * @param appId
	 * @param penDisplayId
	 * @param email
	 * 
	 * @return
	 */
	public Warranty find(String appId, String penSerialNumber, String email) throws RegistrationNotFoundException, MultipleRecordsFoundException {
		
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(Warranty.class)
				.add(Restrictions.eq("appId", appId))
				.add(Restrictions.eq("penSerial", penSerialNumber))
				.add(Restrictions.eq("email", email));
		
		List<Warranty> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}
		
		if (list.size() > 1) {
			throw new MultipleRecordsFoundException("There are multiple Warranty records found for appId=" + appId 
					+ " serialNumber=" + penSerialNumber + " email=" + email);
		}
		
		return list.get(0);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param penDisplayId
	 * @return
	 * @throws RegistrationNotFoundException
	 * @throws MultipleRecordsFoundException
	 */
	public Warranty findByDisplayId(String penDisplayId) throws RegistrationNotFoundException, MultipleRecordsFoundException {
		
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(Warranty.class)
				.add(Restrictions.eq("displayId", penDisplayId));
		
		List<Warranty> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No Warranty found for Pen '" + penDisplayId + "'.");
		}
		
		if (list.size() > 1) {
			throw new MultipleRecordsFoundException("There are multiple Warranty records found for Pen '" + penDisplayId + "'");
		}
		
		return list.get(0);
	}

	/**
	 * <p></p>
	 * 
	 * @param penSerialNumber
	 * @return
	 * @throws RegistrationNotFoundException
	 * @throws MultipleRecordsFoundException
	 */
	public Warranty findByPenSerial(String penSerialNumber) throws RegistrationNotFoundException, MultipleRecordsFoundException {
		
		logger.debug("looking up '" + penSerialNumber + "'");
		
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(Warranty.class)
				.add(Restrictions.eq("penSerial", penSerialNumber));
		
		List<Warranty> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No Warranty found for Pen '" + penSerialNumber + "'.");
		}
		
		if (list.size() > 1) {
			throw new MultipleRecordsFoundException("There are multiple Warranty records found for Pen '" + penSerialNumber + "'");
		}
		
		return list.get(0);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @return
	 * @throws RegistrationNotFoundException
	 */
	public List<Warranty> findByEmail(String email) throws RegistrationNotFoundException {
		
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(Warranty.class)
				.add(Restrictions.eq("email", email));
		
		List<Warranty> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No Warranty found for Email '" + email + "'.");
		}
		
		return list;
	}
}

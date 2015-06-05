/**
 * 
 */
package com.livescribe.admin.dao;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;

import com.livescribe.admin.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.RegisteredDeviceDao;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CustomRegisteredDeviceDao extends RegisteredDeviceDao {

	/**
	 * <p></p>
	 *
	 */
	public CustomRegisteredDeviceDao() {
		super();
	}

//	/**
//	 * <p></p>
//	 * 
//	 * @param penSerial
//	 * 
//	 * @throws MultipleRecordsFoundException
//	 * @throws RegisteredDeviceNotFoundException 
//	 */
//	public void deleteByPenSerial(String penSerial) throws MultipleRecordsFoundException, RegisteredDeviceNotFoundException {
//		
//		RegisteredDevice regDev = findByPenSerial(penSerial);
//		if (regDev == null) {
//			String msg = "No registered device was found with serial number " + penSerial + "'.";
//			throw new RegisteredDeviceNotFoundException(msg);
//		}
//		this.sessionFactoryConsumer.getCurrentSession().delete(regDev);
//	}

	/**
	 * <p></p>
	 * 
	 * @param penSerial The numeric serial number of the pen to lookup.
	 * 
	 * @return a single registered device.
	 * 
	 * @throws MultipleRecordsFoundException if more than one record is found.
	 */
	public RegisteredDevice findByPenSerial(String penSerial) throws MultipleRecordsFoundException {
		
		String method = "findByPenSerial(" + penSerial + "):  ";
		
		Query q = this.sessionFactoryConsumer.getCurrentSession().createQuery("from RegisteredDevice rd where rd.deviceSerialNumber = :penSerial");
		q.setString("penSerial", penSerial);
		
		List<RegisteredDevice> list = q.list();
		
		if (list == null) {
			return null;
		}
		
		if (list.size() > 1) {
			String msg = "Found " + list.size() + " registrations for pen with serial number " + penSerial + ",";
			logger.error(method + msg);
			throw new MultipleRecordsFoundException(msg);
		}
		if (list.size() == 1) {
			logger.debug(method + "Found 1 registered device for serial number '" + penSerial + "'.");
			Hibernate.initialize(list);
			return list.get(0);
		}
		
		return null;
	}
}

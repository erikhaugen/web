/*
 * Created:  Oct 1, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.orm.consumer;

import java.util.List;

import org.hibernate.Query;

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

	/**
	 * <p></p>
	 * 
	 * @param serialNumber
	 * 
	 * @return
	 * 
	 * @throws MultipleRegistrationsFoundException 
	 */
	public RegisteredDevice findBySerialNumber(String serialNumber) {
		
		String method = "findBySerialNumber():  ";
		
//		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("from RegisteredDevice rd where rd.deviceSerialNumber = '" + serialNumber + "'");
//		List<RegisteredDevice> list = q.list();
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("from RegisteredDevice rd where rd.deviceSerialNumber = :serialNumber");
		q.setParameter("serialNumber", serialNumber);
		
		List<RegisteredDevice> list = q.list();
		
		if ((list == null) || (list.isEmpty())) {
			logger.debug(method + "Returning 'null'.");
			return null;
		}
		
		logger.debug(method + "Size of list is:  " + list.size());
		
		if (list.size() > 1) {
			logger.error(method + "More than 1 registered device was found with serial number '" + serialNumber + "'!!!");
			//	TODO:  Need to add handling/notification for this case.
		}
		
		return list.get(0);
	}
	
}

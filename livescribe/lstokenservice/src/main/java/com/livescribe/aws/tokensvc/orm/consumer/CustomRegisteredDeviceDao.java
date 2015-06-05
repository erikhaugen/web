/*
 * Created:  Oct 1, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.orm.consumer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.livescribe.aws.tokensvc.exception.MultipleRegistrationsFoundException;
import com.livescribe.aws.tokensvc.exception.RegistrationNotFoundException;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.RegisteredDeviceDao;
import com.livescribe.framework.orm.consumer.User;

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
	 * @throws RegistrationNotFoundException 
	 * @throws MultipleRegistrationsFoundException 
	 */
	public RegisteredDevice findBySerialNumber(String serialNumber) throws RegistrationNotFoundException, MultipleRegistrationsFoundException {
		
		String method = "findByPenAndUser():  ";
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("from RegisteredDevice rd where rd.deviceSerialNumber = '" + serialNumber + "'");
		List<RegisteredDevice> list = q.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException();
		}
		logger.debug("Size of list is:  " + list.size());
		
		if (list.size() > 1) {
			String msg = "Found '" + list.size() + "' RegisteredDevice records for pen with serial number " + serialNumber + ".";
			logger.error(method + msg);
			throw new MultipleRegistrationsFoundException(msg);
		}
		
		return list.get(0);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param pen
	 * @param user
	 * 
	 * @return
	 */
	public RegisteredDevice findBySerialNumberAndUserId(String serialNumber, long userId) {
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("from RegisteredDevice rd where rd.user.userId = " + userId + " and rd.deviceSerialNumber = '" + serialNumber + "'");
//		q.setParameter("user", user);
//		q.setParameter("pen", pen);
		List<RegisteredDevice> list = q.list();
		
		if ((list == null) || (list.isEmpty())) {
			return null;
		}
		
		if (list.size() > 1) {
			logger.error("findByPenAndUser():  Found more than 1 RegisteredDevice for User ID " + userId + " and pen serial number " + serialNumber + ".  Returning first in the List.");
		}
		
		return list.get(0);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param expDate
	 * 
	 * @return
	 */
	public List<RegisteredDevice> findExpiredCodes(Date expDate) {
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("from RegisteredDevice rd where rd.regCode is not null and created < :expDate");
//		Criteria crit = sessionFactoryConsumer.getCurrentSession().createCriteria(RegisteredDevice.class).add(Restrictions.isNotNull("regCode")); // .add(Restrictions.lt("created", expDate));
		q.setTimestamp("expDate", expDate);
//		logger.debug(q.getQueryString());
		List<RegisteredDevice> list = q.list();
//		List<RegisteredDevice> list = crit.list();
		
		return list;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param code
	 * @return
	 * @throws MultipleRegistrationsFoundException
	 */
	public RegisteredDevice findByRegistrationCode(String code) throws MultipleRegistrationsFoundException, RegistrationNotFoundException {
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("from RegisteredDevice rd where rd.regCode = :code");
		q.setString("code", code);
		
		List<RegisteredDevice> list = q.list();
		if ((list == null) || (list.isEmpty())) {
			String msg = "No RegisteredDevice could be found using code '" + code + "'.";
			logger.info(msg);
			throw new RegistrationNotFoundException(msg);
		}
		if (list.size() > 1) {
			String msg = "Found " + list.size() + " records in the 'registered_device' table with registration code '" + code + "'.";
			logger.error("findByRegistrationToken():  " + msg);
			
			//	TODO:  Need to do more here.
			throw new MultipleRegistrationsFoundException();
		}
		return list.get(0);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param token
	 * 
	 * @return
	 * @throws MultipleRegistrationsFoundException 
	 * @throws RegistrationNotFoundException 
	 */
	public RegisteredDevice findByRegistrationToken(String token) throws MultipleRegistrationsFoundException, RegistrationNotFoundException {
		
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("from RegisteredDevice rd where rd.regToken = :token");
		q.setString("token", token);
		
		List<RegisteredDevice> list = q.list();
		if ((list == null) || (list.isEmpty())) {
			String msg = "No RegisteredDevice could be found using token '" + token + "'.";
			logger.info(msg);
			throw new RegistrationNotFoundException(msg);
		}
		if (list.size() > 1) {
			String msg = "More than 1 RegisteredDevice found with registration token '" + token + "'.";
			logger.error("findByRegistrationToken():  " + msg);
			
			//	TODO:  Need to do more here.
			throw new MultipleRegistrationsFoundException();
		}
		return list.get(0);
	}
	
	/**
	 * <p></p>
	 * 
	 * @param user
	 * 
	 * @return
	 * 
	 * @throws RegistrationNotFoundException
	 */
	public List<RegisteredDevice> findByUser(User user) throws RegistrationNotFoundException {
		
		Query q = sessionFactoryConsumer.getCurrentSession().createQuery("from RegisteredDevice rd where rd.user = :user");
		q.setParameter("user", user);
		
		List<RegisteredDevice> list = q.list();
		
		return list;
	}
}

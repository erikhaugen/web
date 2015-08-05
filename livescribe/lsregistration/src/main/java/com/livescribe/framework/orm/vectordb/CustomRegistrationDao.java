/**
 * Created:  Aug 15, 2013 1:56:18 PM
 */
package com.livescribe.framework.orm.vectordb;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @author Mohammad M. Naqvi (added methods to search registrations by 'partial' parameters)
 * @version 1.2
 */
public class CustomRegistrationDao extends RegistrationDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public CustomRegistrationDao() {
	}

	/**
	 * <p></p>
	 * 
	 * @param appId
	 * @param penSerialNumber The 14-character display ID or numeric serial number of the pen.
	 * @param email
	 * 
	 * @throws MultipleRecordsFoundException 
	 */
	public void delete(String appId, String penSerialNumber, String email) throws RegistrationNotFoundException, MultipleRecordsFoundException {
		
		Registration registration = find(appId, penSerialNumber, email);
		
		sessionFactoryVectorDB.getCurrentSession().delete(registration);
	}
	
	/**
	 * <p>Find a unique registration by a combination of [appId, penDisplayId, email]</p>
	 * 
	 * @param appId
	 * @param penSerialNumber
	 * @param email
	 * 
	 * @return
	 * 
	 * @throws MultipleRecordsFoundException 
	 */
	public Registration find(String appId, String penSerialNumber, String email) throws RegistrationNotFoundException, MultipleRecordsFoundException {
		
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(Registration.class)
				.add(Restrictions.eq("appId", appId))
				.add(Restrictions.eq("penSerial", penSerialNumber))
				.add(Restrictions.eq("email", email));
		
		@SuppressWarnings("unchecked")
		List<Registration> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}
		
		if (list.size() > 1) {
			throw new MultipleRecordsFoundException();
		}
		
		return list.get(0);
	}
	
	/**
	 * <p>Find a registration by penSerial</p>
	 * 
	 * @param penSerial
	 * @return list of matching registration(s)
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByPenSerial(String penSerial) throws RegistrationNotFoundException {

		logger.debug("findByPenSerial() invoked with penSerial " + penSerial);
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(Registration.class)
				.add(Restrictions.eq("penSerial", penSerial))
				.addOrder(Order.desc("created"));
		
		@SuppressWarnings("unchecked")
		List<Registration> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}

		logger.debug("findByPenSerial() for penSerial " + penSerial + " returning " + list.size() + " registration(s).");
		return list;
	}
	
	/**
	 * <p>Find a registration by partialPenSerial</p>
	 * 
	 * @param partialPenSerial
	 * @return list of matching registration(s)
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByPartialPenSerial(String partialPenSerial) throws RegistrationNotFoundException {

		logger.debug("findByPartialPenSerial() invoked with partialPenSerial " + partialPenSerial);

		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(Registration.class)
				.add(Restrictions.like("penSerial", partialPenSerial, MatchMode.ANYWHERE))
				.addOrder(Order.desc("created"));
		
		@SuppressWarnings("unchecked")
		List<Registration> list = crit.list();
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}
		logger.debug("findByPartialPenSerial() for partialPenSerial " + partialPenSerial + " returning " + list.size() + " registration(s).");
		return list;
	}
	
	/**
	 * <p>Find a registration by displayId</p>
	 * 
	 * @param displayId
	 * @return list of matching registration(s)
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByDisplayId(String displayId) throws RegistrationNotFoundException {

		logger.debug("findByDisplayId() invoked with displayId " + displayId);
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(Registration.class)
				.add(Restrictions.eq("displayId", displayId))
				.addOrder(Order.desc("created"));
		
		@SuppressWarnings("unchecked")
		List<Registration> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}

		logger.debug("findByDisplayId() for displayId " + displayId + " returning " + list.size() + " registration(s).");
		return list;
	}
	
	/**
	 * <p>Find a registration by partial displayId</p>
	 * 
	 * @param partialDisplayId
	 * @return list of matching registration(s)
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByPartialDisplayId(String partialDisplayId) throws RegistrationNotFoundException {

		logger.debug("findByPartialDisplayId() invoked with partialDisplayId " + partialDisplayId);

		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(Registration.class)
				.add(Restrictions.like("displayId", partialDisplayId, MatchMode.ANYWHERE))
				.addOrder(Order.desc("created"));
		
		@SuppressWarnings("unchecked")
		List<Registration> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}
		logger.debug("findByPartialDisplayId() for partialDisplayId " + partialDisplayId + " returning " + list.size() + " registration(s).");	
		return list;
	}
	
	/**
	 * <p>Find a registration by appId</p>
	 * 
	 * @param appId
	 * @return list of matching registration(s)
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByAppId(String appId) throws RegistrationNotFoundException {

		logger.debug("findByAppId() invoked with appId " + appId);
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(Registration.class)
				.add(Restrictions.eq("appId", appId))
				.addOrder(Order.desc("created"));
		
		@SuppressWarnings("unchecked")
		List<Registration> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}

		logger.debug("findByAppId() for appId " + appId + " returning " + list.size() + " registration(s).");
		return list;
	}
	
	/**
	 * <p>Find a registration by partial email</p>
	 * 
	 * @param partialEmail
	 * @return list of matching registration(s)
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByPartialEmail(String partialEmail) throws RegistrationNotFoundException {

		logger.debug("findByPartialDisplayId() invoked with partialDisplayId " + partialEmail);
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(Registration.class)
				.add(Restrictions.like("email", partialEmail, MatchMode.ANYWHERE))
				.addOrder(Order.desc("created"));
		
		@SuppressWarnings("unchecked")
		List<Registration> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}
		logger.debug("findByPartialEmail() for partialEmail " + partialEmail + " returning " + list.size() + " registration(s).");	
		return list;
	}
	
	/**
	 * <p>Find a registration by email</p>
	 * 
	 * @param email
	 * @return list of matching registration(s)
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByEmail(String email) throws RegistrationNotFoundException {

		logger.debug("findByEmail() invoked with email " + email);
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(Registration.class)
				.add(Restrictions.eq("email", email))
				.addOrder(Order.desc("created"));
		
		@SuppressWarnings("unchecked")
		List<Registration> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}

		logger.debug("findByEmail() for email " + email + " returning " + list.size() + " registration(s).");
		return list;
	}
}

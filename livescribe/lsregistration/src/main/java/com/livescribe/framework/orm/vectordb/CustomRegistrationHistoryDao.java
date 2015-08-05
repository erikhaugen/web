package com.livescribe.framework.orm.vectordb;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.livescribe.web.registration.exception.RegistrationNotFoundException;

public class CustomRegistrationHistoryDao extends RegistrationHistoryDao {

	/**
	 * <p>Default constructor</p>
	 */
	public CustomRegistrationHistoryDao() {
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
	public List<RegistrationHistory> find(String appId, String penSerialNumber, String email) throws RegistrationNotFoundException {
		
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(RegistrationHistory.class)
				.add(Restrictions.eq("appId", appId))
				.add(Restrictions.eq("penSerial", penSerialNumber))
				.add(Restrictions.eq("email", email))
				.addOrder(Order.desc("registrationDate"));
		
		List<RegistrationHistory> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}
		
		return list;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param penSerial
	 * @return
	 * @throws RegistrationNotFoundException
	 */
	public List<RegistrationHistory> findByPenSerial(String penSerial) throws RegistrationNotFoundException {
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(RegistrationHistory.class)
				.add(Restrictions.eq("penSerial", penSerial))
				.addOrder(Order.desc("registrationDate"));
		
		List<RegistrationHistory> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}
		
		return list;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @return
	 * @throws RegistrationNotFoundException
	 */
	public List<RegistrationHistory> findByEmail(String email) throws RegistrationNotFoundException {
		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(RegistrationHistory.class)
				.add(Restrictions.eq("email", email))
				.addOrder(Order.desc("registrationDate"));
		
		List<RegistrationHistory> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}
		
		return list;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @return
	 * @throws RegistrationNotFoundException
	 */
	public List<RegistrationHistory> findByPartialEmail(String email) throws RegistrationNotFoundException {

		Criteria crit = sessionFactoryVectorDB.getCurrentSession().createCriteria(RegistrationHistory.class)
				.add(Restrictions.like("email", email, MatchMode.ANYWHERE))
				.addOrder(Order.desc("registrationDate"));
		
		@SuppressWarnings("unchecked")
		List<RegistrationHistory> list = crit.list();
		
		if ((list == null) || (list.isEmpty())) {
			throw new RegistrationNotFoundException("No registration found.");
		}
		
		return list;
	}
}

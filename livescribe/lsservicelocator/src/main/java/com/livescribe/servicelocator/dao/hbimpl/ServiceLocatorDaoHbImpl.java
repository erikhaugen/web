package com.livescribe.servicelocator.dao.hbimpl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.livescribe.base.UnableToCompleteOperationException;
import com.livescribe.servicelocator.dao.ServiceLocatorDao;
import com.livescribe.servicelocator.dao.data.ServiceLocator;

public class ServiceLocatorDaoHbImpl implements ServiceLocatorDao {
	
	private SessionFactory factory;

	public ServiceLocatorDaoHbImpl(SessionFactory factory) {
		this.factory = factory;
	}
	
	private static final String HQL_ALL_SERVICES = "FROM " + ServiceLocator.class.getName();
	public List<ServiceLocator> getAllServices() throws UnableToCompleteOperationException {
		try {
			Session session = factory.getCurrentSession();
			session.beginTransaction();
			Query query = session.createQuery(HQL_ALL_SERVICES);

			@SuppressWarnings("unchecked")
			List<ServiceLocator> list = query.list();
			session.getTransaction().commit();
			return list;
		} catch (Exception ex ) {
			throw new UnableToCompleteOperationException("retrieve services from the database.", ex);
		}
	}
	
	private static final String HQL_ALL_SERVICES_DOMAIN = "FROM " + ServiceLocator.class.getName() + " where domain=:domain";
	public List<ServiceLocator> getAllServicesByDomain(String domain) throws UnableToCompleteOperationException {
		try {
			Session session = factory.getCurrentSession();
			session.beginTransaction();
			Query query = session.createQuery(HQL_ALL_SERVICES_DOMAIN);
			query.setParameter("domain", domain);

			@SuppressWarnings("unchecked")
			List<ServiceLocator> list = query.list();
			session.getTransaction().commit();
			return list;
		} catch (Exception ex ) {
			throw new UnableToCompleteOperationException("retrieve services from the database.", ex);
		}
	}
	
	private static final String HQL_SERVICES_BY_NAME_IN_DOMAIN = "FROM " + 
		ServiceLocator.class.getName() + " where domain=:domain and name=:name";
	public List<ServiceLocator> getAllServicesByNameInDomain(String domain, String serviceName) throws UnableToCompleteOperationException {
		try {
			Session session = factory.getCurrentSession();
			session.beginTransaction();
			Query query = session.createQuery(HQL_SERVICES_BY_NAME_IN_DOMAIN);
			query.setParameter("domain", domain);
			query.setParameter("name", serviceName);

			@SuppressWarnings("unchecked")
			List<ServiceLocator> list = query.list();
			session.getTransaction().commit();
			return list;
		} catch (Exception ex ) {
			throw new UnableToCompleteOperationException("retrieve services from the database.", ex);
		}
	}
	
	public long insertServiceLocator(ServiceLocator locator) throws UnableToCompleteOperationException {
		try {
			Session session = factory.getCurrentSession();
			session.beginTransaction();
			session.saveOrUpdate(locator);
			session.getTransaction().commit();
			return locator.getId();
		} catch (Exception ex ) {
			throw new UnableToCompleteOperationException("Unable to insert ServiceLocator, " + locator.toString(),
					ex);
		}
	}
}

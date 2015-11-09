package com.livescribe.servicelocator.dao.hbimpl;

import org.hibernate.SessionFactory;

import com.livescribe.servicelocator.dao.DaoFactory;
import com.livescribe.servicelocator.dao.ServiceLocatorDao;

public class HbDaoFactory extends DaoFactory {
	
	private ServiceLocatorDao serviceLocatorDao;

	public void setSessionFactoryServiceLocator(SessionFactory sessionFactoryServiceLocator) {
		this.serviceLocatorDao = new ServiceLocatorDaoHbImpl(sessionFactoryServiceLocator);
	}

	@Override
	public ServiceLocatorDao getServiceLocatorDao() {
		return serviceLocatorDao;
	}

}

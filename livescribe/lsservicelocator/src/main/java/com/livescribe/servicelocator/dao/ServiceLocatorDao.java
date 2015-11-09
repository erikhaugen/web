package com.livescribe.servicelocator.dao;

import java.util.List;

import com.livescribe.base.UnableToCompleteOperationException;
import com.livescribe.servicelocator.dao.data.ServiceLocator;

public interface ServiceLocatorDao {
	public List<ServiceLocator> getAllServices() throws UnableToCompleteOperationException;
	
	public List<ServiceLocator> getAllServicesByDomain(String domain) throws UnableToCompleteOperationException;
	
	public List<ServiceLocator> getAllServicesByNameInDomain(String domain, String serviceName) throws UnableToCompleteOperationException;
	
	public long insertServiceLocator(ServiceLocator locator) throws UnableToCompleteOperationException;
}

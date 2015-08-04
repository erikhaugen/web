package com.livescribe.aws.heartbeat.orm.dao;

import java.util.List;

public interface GenericDao<T> {
	public void persist(T transientInstance);
	
	public void attachDirty(T instance);
	
	public void attachClean(T instance);
	
	public void delete(T persistentInstance);
	
	public T merge(T detachedInstance);
	
	public T findById(Long id);
	
	public List<T> findByExample(T instance);
}

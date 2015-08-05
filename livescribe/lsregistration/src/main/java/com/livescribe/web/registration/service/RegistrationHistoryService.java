package com.livescribe.web.registration.service;

import java.util.List;

import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;

public interface RegistrationHistoryService {

	public List<RegistrationHistory> find(String appId, String penSerial, String email) throws RegistrationNotFoundException;
	
	public List<RegistrationHistory> findByPenSerial(String penSerial) throws RegistrationNotFoundException;
	
	public List<RegistrationHistory> findByEmail(String email) throws RegistrationNotFoundException;
	
	/**
	 * <p>Saves the given registration record in the 
	 * <code>registration_history</code> table.</p>
	 * 
	 * @param registration The registration to save.
	 */
	public void save(Registration registration);
}

/**
 * Created:  Aug 15, 2013 10:07:37 AM
 */
package com.livescribe.web.registration.service;

import java.util.List;

import org.apache.camel.Exchange;

import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.web.registration.controller.RegistrationData;
import com.livescribe.web.registration.exception.DeviceNotFoundException;
import com.livescribe.web.registration.exception.RegistrationAlreadyExistedException;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.exception.UnsupportedPenTypeException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @author Mohammad M. Naqvi (added methods to search registrations by 'partial' parameters)
 * @version 1.2
 */
public interface RegistrationService {

	/**
	 * <p></p>
	 * 
	 * @param appId
	 * @param penDisplayId
	 * @param email
	 * 
	 * @throws MultipleRecordsFoundException 
	 */
	public void delete(String appId, String penSerialNumber, String email) throws RegistrationNotFoundException, MultipleRecordsFoundException;
	
	/**
	 * <p>Deletes all registration records owned by the user with the given
	 * email address.</p>
	 * 
	 * @param email The email address to use.
	 */
	public void deleteByEmail(String email);
	
	/**
	 * <p></p>
	 * 
	 * @param exchange
	 * @param exception
	 */
	public void find(Exchange exchange);
	
	/**
	 * <p>Finds an existing registration with the given application 
	 * ID, pen display ID, and email.</p>
	 * 
	 * @param appId
	 * @param penDisplayId The 14-character ID of the pen.
	 * @param email
	 * 
	 * @return a registration record from the database.
	 * 
	 * @throws MultipleRecordsFoundException if more than 1 record is found. 
	 */
	public Registration find(String appId, String penSerialNumber, String email) throws RegistrationNotFoundException, MultipleRecordsFoundException;

	/**
	 * <p></p>
	 * 
	 * @param data
	 * 
	 * @return
	 */
	public Registration find(RegistrationData data) throws MultipleRecordsFoundException;
	
	/**
	 * <p>Find a registration by penDisplayId</p>
	 * 
	 * @param penSerial
	 * @return
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByPenSerial(String penSerial) throws RegistrationNotFoundException;
	
	/**
	 * <p>Find a registration by displayId</p>
	 * 
	 * @param displayId
	 * @return
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByDisplayId(String displayId) throws RegistrationNotFoundException;
	
	/**
	 * <p>Find a registration by appId</p>
	 * 
	 * @param appId
	 * @return
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByAppId(String appId) throws RegistrationNotFoundException;
	
	/**
	 * <p>Find a registration by email</p>
	 * 
	 * @param email
	 * @return
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByEmail(String email) throws RegistrationNotFoundException;
	
	/**
	 * <p>Finds all Registrations having the primary email that <i>contains</i> the given string.</p>
	 * 
	 * @param email partial email id
	 * @return list of matching Registrations
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByPartialEmail(String email) throws RegistrationNotFoundException;
	
	/**
	 * <p>Finds all Registrations having the pen serial that <i>contains</i> the given string.</p>
	 * 
	 * @param partialPenSerial partialPenSerial
	 * @return list of matching Registrations
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByPartialPenSerial(String partialPenSerial) throws RegistrationNotFoundException;
	
	/**
	 * <p>Finds all Registrations having the pen displayId that <i>contains</i> the given string.</p>
	 * 
	 * @param partialPenDisplayId partialPenDisplayId
	 * @return list of matching Registrations
	 * @throws RegistrationNotFoundException
	 */
	public List<Registration> findByPartialPenDisplayId(String partialPenDisplayId) throws RegistrationNotFoundException;
	
	/**
	 * <p>Returns whether the given display ID is valid.</p>
	 * 
	 * @param displayId The 14-character ID of the pen.
	 * 
	 * @return <code>true</code> if the given display ID exists in the <code>corp_manufacturing.pen</code> database table; <code>false</code> if not.
	 * 
	 * @throws MultipleRecordsFoundException
	 */
	public boolean isPenSerialNumberValid(String penSerialNumber) throws MultipleRecordsFoundException, UnsupportedPenTypeException;

	/**
	 * <p></p>
	 * 
	 * @param exchange
	 */
	public void save(Exchange exchange);

	/**
	 * <p></p>
	 * 
	 * @param data The form data from the request.
	 * 
	 * @return the persisted instance from the database.
	 */
	public Registration save(RegistrationData data);
	
	/**
	 * <p>Saves or updates a record in the DB.</p>
	 * 
	 * <p>If the record exists, merges the given information with the DB 
	 * record matching the given application ID, pen display 
	 * ID and email.  Otherwise, the information is persisted as a new instance.</p>
	 * 
	 * @param data The form data from the request.
	 * 
	 * @return the resulting persisted instance from the database.
	 * @throws MultipleRecordsFoundException 
	 */
	public Registration saveOrUpdate(RegistrationData data) throws RegistrationNotFoundException, MultipleRecordsFoundException;
	
	/**
	 * <p></p>
	 * 
	 * @param data
	 * @return
	 */
	public boolean isRegisteringFirstTime(RegistrationData data);
	
	/**
	 * 
	 * 
	 * @param data
	 * @return
	 */
	public Registration register(RegistrationData data) throws RegistrationAlreadyExistedException, MultipleRecordsFoundException, DeviceNotFoundException, UnsupportedPenTypeException;
	
	
	/**
	 * 
	 * 
	 * @param data
	 */
	public void unregister(RegistrationData data) throws RegistrationNotFoundException, MultipleRecordsFoundException;
}

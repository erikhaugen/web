/**
 * 
 */
package com.livescribe.admin.service;

import java.io.IOException;
import java.util.List;

import com.livescribe.admin.controller.dto.RegisteredUserDto;
import com.livescribe.admin.exception.MultipleRecordsFoundException;
import com.livescribe.admin.exception.UserNotFoundException;
import com.livescribe.admin.lookup.LookupCriteria;
import com.livescribe.admin.lookup.LookupCriteriaList;
import com.livescribe.afp.AFPException;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.orm.consumer.User;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface UserService {
	
	/**
	 * Finds a list of users that contains given string in their email addresses.
	 * @param partialEmail
	 * @return
	 * @throws UserNotFoundException
	 */
	public List<User> findByPartialEmail(String partialEmail) throws UserNotFoundException;

	/**
	 * <p></p>
	 * 
	 * @param email
	 * @return
	 * @throws UserNotFoundException 
	 * @throws MultipleRecordsFoundException 
	 */
	public User findByEmail(String email) throws UserNotFoundException, MultipleRecordsFoundException;
	
	/**
	 * <p></p>
	 * 
	 * @param UID
	 * @return
	 */
	public User findByUID(String UID);
	
	/**
	 * <p></p>
	 * 
	 * @param displayId
	 * @return
	 * @throws AFPException 
	 * @throws MultipleRecordsFoundException 
	 */
	public User findByPenDisplayId(String displayId) throws AFPException, MultipleRecordsFoundException;
	
	/**
	 * <p></p>
	 * 
	 * @return
	 * @throws MultipleRecordsFoundException 
	 */
	public User findByPenSerialNumber(String penSerial) throws MultipleRecordsFoundException;
	
	/**
	 * <p></p>
	 * 
	 * @param criteria
	 * @return
	 */
	public List<User> findByCriteria(LookupCriteria criteria);
	
	/**
	 * <p></p>
	 * 
	 * @param criteriaList
	 * @return
	 */
	public List<User> findByCriteriaList(LookupCriteriaList criteriaList);
	
	/**
	 * 
	 * @return
	 */
	public List<User> findByAuthorization();
	
	/**
	 * 
	 * @param email
	 * @throws UserNotFoundException
	 */
	public void deleteConsumerUser(String email) throws UserNotFoundException, ClientException;
	
	/**
	 * Returns the uid of the owner of the document identified by the given docId
	 * 
	 * @param docId id of the document
	 * @return uid of the document owner
	 */
	public String getUIDByDocumentId(Long docId);

	public List<RegisteredUserDto> findUserDtoByCriteriaList(LookupCriteriaList criteriaList);
}

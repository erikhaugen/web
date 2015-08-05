package com.livescribe.web.registration.client;

import java.io.IOException;

import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.controller.RegistrationData;
import com.livescribe.web.registration.exception.DeviceNotFoundException;
import com.livescribe.web.registration.exception.RegistrationAlreadyExistedException;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.exception.UnsupportedPenTypeException;
import com.livescribe.web.registration.response.RegistrationHistoryListResponse;
import com.livescribe.web.registration.response.RegistrationListResponse;
import com.livescribe.web.registration.response.RegistrationResponse;
import com.livescribe.web.registration.response.UserListResponse;
import com.livescribe.web.registration.response.WarrantyListResponse;
import com.livescribe.web.registration.response.WarrantyResponse;

public interface RegistrationAPI {

	public ServiceResponse register(RegistrationData data) throws InvalidParameterException, DeviceNotFoundException, UnsupportedPenTypeException, RegistrationAlreadyExistedException, ClientException;
	
	public ServiceResponse unregister(String appId, String penSerial, String email) throws InvalidParameterException, RegistrationNotFoundException, MultipleRecordsFoundException, ClientException;
	
	
	public RegistrationResponse findUniqueRegistration(String appId, String penSerial, String email) throws InvalidParameterException, RegistrationNotFoundException, MultipleRecordsFoundException, ClientException, IllegalStateException, IOException;

	// Find RegistrationList
	public RegistrationListResponse findRegistrationsListByAppId(String appId) throws InvalidParameterException, RegistrationNotFoundException, ClientException;

	public RegistrationListResponse findRegistrationsListByPenSerial(String penSerial) throws InvalidParameterException, RegistrationNotFoundException, ClientException, IllegalStateException, IOException;

	public RegistrationListResponse findRegistrationsListByPartialPenSerial(String penSerial) throws InvalidParameterException, RegistrationNotFoundException, ClientException, IllegalStateException, IOException;

	public RegistrationListResponse findRegistrationsListByPenDisplayId(String penDisplayId) throws InvalidParameterException, RegistrationNotFoundException, ClientException, IllegalStateException, IOException;

	public RegistrationListResponse findRegistrationsListByPartialPenDisplayId(String penDisplayId) throws InvalidParameterException, RegistrationNotFoundException, ClientException, IllegalStateException, IOException;
		
	public RegistrationListResponse findRegistrationsListByEmail(String email) throws InvalidParameterException, RegistrationNotFoundException, ClientException;
	
	public RegistrationListResponse findRegistrationsListByPartialEmail(String email) throws RegistrationNotFoundException, ClientException, InvalidParameterException;

	
	// Find RegistrationHistoryList
	public RegistrationHistoryListResponse findRegistrationHistoryByPenSerial(String penSerial) throws InvalidParameterException, RegistrationNotFoundException, ClientException;
	
	public RegistrationHistoryListResponse findRegistrationHistoryByEmail(String email) throws InvalidParameterException, RegistrationNotFoundException, ClientException;
	
//	public WarrantyResponse findWarranty(String appId, String penSerial, String email) throws RegistrationNotFoundException, MultipleRecordsFoundException;
	
	public WarrantyResponse findWarrantyByPenSerial(String penSerialNumber) throws InvalidParameterException, RegistrationNotFoundException, MultipleRecordsFoundException, ClientException;
	
	public WarrantyListResponse findWarrantyByEmail(String email) throws InvalidParameterException, RegistrationNotFoundException, ClientException;
	
	
	
	// Find UsersList
	public UserListResponse findUsersByEmail(String email) throws InvalidParameterException, ClientException;
	public UserListResponse findUsersByPartialEmail(String email) throws InvalidParameterException, ClientException;

}

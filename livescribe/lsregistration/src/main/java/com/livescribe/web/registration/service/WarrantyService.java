package com.livescribe.web.registration.service;

import java.util.List;

import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.vectordb.Warranty;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;

public interface WarrantyService {

	public Warranty find(String appId, String penSerialNumber, String email) throws RegistrationNotFoundException, MultipleRecordsFoundException;
	
	public Warranty findByPenSerial(String penSerialNumber) throws RegistrationNotFoundException, MultipleRecordsFoundException;
	
	public List<Warranty> findByEmail(String email) throws RegistrationNotFoundException;
	
	public Warranty findByPenDisplayId(String penDisplayId) throws RegistrationNotFoundException, MultipleRecordsFoundException;
}

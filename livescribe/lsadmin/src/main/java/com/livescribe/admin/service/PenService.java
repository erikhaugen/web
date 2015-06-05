package com.livescribe.admin.service;

import java.util.List;

import com.livescribe.admin.controller.dto.DeviceDto;
import com.livescribe.admin.controller.dto.RegisteredUserDto;
import com.livescribe.admin.lookup.LookupCriteriaList;
import com.livescribe.afp.AFPException;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.manufacturing.Pen;

public interface PenService {

	/**
	 * Find pen by ID, id could be number/alphabet format
	 * @param id
	 * @return
	 */
    
	Pen findPenById(String id) throws IllegalArgumentException;
	
	/**
	 * Find register device by pen's serial number (DEC)
	 * @param id
	 * @return
	 * @throws IllegalArgumentException
	 * @throws AFPException 
	 */
	RegisteredDevice findRegisterDeviceById(String id) throws IllegalArgumentException, AFPException;

	/**
	 * Finds pens that 'contains' the given string in serial number.
	 * @param serialNumber partialSerialNumber
	 * @return all matching Pens
	 */
    List<Pen> findPensByPartialSerialNumber(String serialNumber);

	/**
	 * Finds pens that 'contains' the given string in display id.
	 * @param displayId partialdisplayId
	 * @return all matching Pens
	 */
    List<Pen> findPensByPartialDisplayId(String displayId);
    
    /**
     * Finds the pen having serial number exactly as the given String.
     * @param serialNumber serialNumber
     * @return matching pen.
     */
    Pen findPenBySerialNumber(String serialNumber);
    
    /**
     * Finds the pen having serial number exactly as the given String.
     * 
     * @param displayId
     * @return matching pen.
     */
    Pen findPenByDisplayId(String displayId);

    /**
     * Finds all Registered Users based on given criteria.
     * @param criteriaList
     * @return matching registered users.
     */
	List<RegisteredUserDto> findRegisteredUserDtoByCriteriaList(LookupCriteriaList criteriaList);
	
	/**
     * Finds all UN-Registered Devices/Pens based on given criteria.
	 * 
	 * @param criteriaList
	 * @return
	 */
	List<DeviceDto> findUnregisteredDeviceDtoByCriteriaList(LookupCriteriaList criteriaList);
}

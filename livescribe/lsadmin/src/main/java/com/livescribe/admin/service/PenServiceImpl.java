package com.livescribe.admin.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.admin.controller.dto.DeviceDto;
import com.livescribe.admin.controller.dto.RegisteredUserDto;
import com.livescribe.admin.dao.CustomPenDao;
import com.livescribe.admin.dao.CustomRegisteredDeviceDao;
import com.livescribe.admin.lookup.Comparator;
import com.livescribe.admin.lookup.LookupCriteriaList;
import com.livescribe.admin.lookup.QueryField;
import com.livescribe.afp.AFPException;
import com.livescribe.afp.PenID;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.manufacturing.Pen;

public class PenServiceImpl implements PenService {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private CustomPenDao customPenDao;
	
	@Autowired
	private CustomRegisteredDeviceDao registeredDeviceDao;
	
	@Transactional("txManufacturing")
	public Pen findPenById(String id) throws IllegalArgumentException {
		Pen result = null;
		if (id == null)
			throw new IllegalArgumentException("Id cannot be null");
		
		//determine id is in alphabet format or number format
		if (id.contains("-")) {
			//yes, it is in alphabet format
			Pen sample = new Pen();
			sample.setDisplayId(id);
			List<Pen> pens = customPenDao.findByExample(sample);
			if (pens == null || pens.size() == 0)
				return null;
			
			result = pens.get(0);
		} else {
			//this case, it is in number format
			Pen sample = new Pen();
			if (id.indexOf("0x") == 0)
				sample.setSerialnumberHex(id.substring(2));
			else
			    sample.setSerialnumber(id);
//			else throw new IllegalArgumentException("Invalid pen ID");
			
			List<Pen> pens = customPenDao.findByExample(sample);
			if (pens == null || pens.size() == 0)
				return null;
			
			result = pens.get(0);
		}
		
		return result;
	}

	@Transactional("txConsumer")
	public RegisteredDevice findRegisterDeviceById(String id) throws IllegalArgumentException, AFPException {
		
		RegisteredDevice device = null;
		
		if (id == null)
			throw new IllegalArgumentException("Id cannot be null");
		
		String pidStr = null;
		
		//	Using this Pattern captures cases where a dash '-' character is
		//	present or not.
		Pattern pattern = Pattern.compile("[a-zA-Z-]");
		boolean isAlphabetic = pattern.matcher(id).find();
		
		//	Determine if 'id' is in alphabetic format or number format
//		if (id.contains("-")) {
		if (isAlphabetic) {
			//	Yes, it is in alphabetic format.
			//	Change it to a numeric serial number.
			PenID penId = new PenID(id);
			long pid = penId.getId();
			pidStr = String.valueOf(pid);
		}
		else {
			//	... otherwise, use what was given.
			pidStr = id;
		}
		RegisteredDevice sample = new RegisteredDevice();
		sample.setDeviceSerialNumber(pidStr);
		
		List<RegisteredDevice> result = registeredDeviceDao.findByExample(sample);
		if (result != null && result.size() > 0)
			device = result.get(0);
		
		return device;
	}



	@Override
	@Transactional("txManufacturing")
	public List<Pen> findPensByPartialSerialNumber(String serialNumber) {
		return customPenDao.findPensByPartialSerailNumber(serialNumber);
	}

	@Override
	@Transactional("txManufacturing")
	public List<Pen> findPensByPartialDisplayId(String displayId) {
		return customPenDao.findPensByPartialDisplayId(displayId);
	}

	@Override
	@Transactional("txManufacturing")
	public Pen findPenBySerialNumber(String serialNumber) {
		return customPenDao.findPenBySerailNumber(serialNumber);
	}

	@Override
	@Transactional("txManufacturing")
	public Pen findPenByDisplayId(String displayId) {
		return customPenDao.findPenByDisplayId(displayId);
	}

	@Override
	public List<DeviceDto> findUnregisteredDeviceDtoByCriteriaList(LookupCriteriaList criteriaList) {
		
		QueryField searchKeyQueryField = criteriaList.getCriteriaList().get(0).getQueryField();
		Comparator searchComparator = criteriaList.getCriteriaList().get(0).getComparator();
		String searchString = criteriaList.getCriteriaList().get(0).getValue();
		List<DeviceDto> unregisteredPens = new ArrayList<DeviceDto>();

		switch (searchKeyQueryField) {
			case PEN_DISPLAY_ID:
				switch (searchComparator) {
					case CONTAINS:
						List<Pen> pensList = findPensByPartialDisplayId(searchString);
						unregisteredPens = findUnregisteredPens(pensList);
						break;
					case IS_EQUAL_TO:
						Pen pen = findPenByDisplayId(searchString);
						if (null != pen && !isRegistered(pen)) {
							unregisteredPens.add(new DeviceDto(pen));
						}
						break;
					default:
						break;
				}
				break;
			case PEN_SERIAL_NUMBER:
				switch (searchComparator) {
				case CONTAINS:
					List<Pen> pensList = findPensByPartialSerialNumber(searchString);
					unregisteredPens = findUnregisteredPens(pensList);
					break;
				case IS_EQUAL_TO:
					Pen pen = findPenBySerialNumber(searchString);
					if (null != pen && !isRegistered(pen)) {
						unregisteredPens.add(new DeviceDto(pen));
					}
					break;
				default:
					break;
			}
			break;
			default:
				break;
		}
		return unregisteredPens;
	}

	@Override
	public List<RegisteredUserDto> findRegisteredUserDtoByCriteriaList(LookupCriteriaList criteriaList) {
		
		QueryField searchKeyQueryField = criteriaList.getCriteriaList().get(0).getQueryField();
		Comparator searchComparator = criteriaList.getCriteriaList().get(0).getComparator();
		String searchString = criteriaList.getCriteriaList().get(0).getValue();
		List<RegisteredUserDto> registeredUsers = new ArrayList<RegisteredUserDto>();

		switch (searchKeyQueryField) {
			case PEN_DISPLAY_ID:
				switch (searchComparator) {
					case CONTAINS:
						List<Pen> pensList = findPensByPartialDisplayId(searchString);
						registeredUsers = findRegisteredUsersByPens(pensList);
						break;
					case IS_EQUAL_TO:
						Pen pen = findPenByDisplayId(searchString);
						if (null != pen && isRegistered(pen)) {
							registeredUsers.add(findRegisteredUserByPen(pen));
						}
						break;
					default:
						break;
				}
				break;
			case PEN_SERIAL_NUMBER:
				switch (searchComparator) {
				case CONTAINS:
					List<Pen> pensList = findPensByPartialSerialNumber(searchString);
					registeredUsers = findRegisteredUsersByPens(pensList);
					break;
				case IS_EQUAL_TO:
					Pen pen = findPenBySerialNumber(searchString);
					if (null != pen && isRegistered(pen)) {
						registeredUsers.add(findRegisteredUserByPen(pen));
					}
					break;
				default:
					break;
			}
			break;
			default:
				break;
		}
		
		return registeredUsers;
	}
	
	private List<RegisteredUserDto> findRegisteredUsersByPens(List<Pen> pensList) {

		//Using 'Set' instead of 'List' in order to keep the collection unique.
		Set<RegisteredUserDto> registeredUsersSet = new HashSet<RegisteredUserDto>();
		for (Pen pen : pensList) {
			try {
				RegisteredDevice registeredDevice = findRegisterDeviceById(pen.getSerialnumber());
	        	if (registeredDevice != null && registeredDevice.getUser() != null) {
	        		//This pen is registered..
	        		User registredUser = registeredDevice.getUser();
	        		registeredUsersSet.add(new RegisteredUserDto(registredUser));
	        	}
			} catch (Exception e) {
				// Should not happen
				logger.warn("Caught Exception: ", e);
			}
			
		}
		
		List<RegisteredUserDto> registeredUsersList = new ArrayList<RegisteredUserDto>();
		registeredUsersList.addAll(registeredUsersSet);
		return registeredUsersList;
	}	

	private RegisteredUserDto findRegisteredUserByPen(Pen pen) {
		try {
			RegisteredDevice registeredDevice = findRegisterDeviceById(pen.getSerialnumber());
        	if (registeredDevice != null && registeredDevice.getUser() != null) {
        		//This pen is registered..
        		User registredUser = registeredDevice.getUser();
        		return new RegisteredUserDto(registredUser);
        	}
		} catch (Exception e) {
			// Should not happen
			logger.warn("Caught Exception: ", e);
		}
		return null;
	}

	private boolean isRegistered(Pen pen) {		
		try {
			RegisteredDevice registeredDevice = findRegisterDeviceById(pen.getSerialnumber());
        	if (registeredDevice != null && registeredDevice.getUser() != null) {
        		//This pen is registered..
        		return true;
        	}
		} catch (Exception e) {
			// Should not happen
			logger.warn("Caught Exception: ", e);
		}
		return false;
	}

	private List<DeviceDto> findUnregisteredPens(List<Pen> pensList) {

		List<DeviceDto> unregisteredPens = new ArrayList<DeviceDto>();
        
        for (Pen pen : pensList) {
			try {
				RegisteredDevice registeredDevice = findRegisterDeviceById(pen.getSerialnumber());
	        	if (registeredDevice == null || registeredDevice.getUser() == null) {
	        		//This pen is NOT registered..
	        		unregisteredPens.add(new DeviceDto(pen));
	        	}
			} catch (Exception e) {
				// Should not happen
				logger.warn("Caught Exception: ", e);
			}
        }
        return unregisteredPens;
	}
 
}

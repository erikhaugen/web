/**
 * 
 */
package com.livescribe.admin.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.admin.controller.dto.DeviceDto;
import com.livescribe.admin.controller.dto.InfoLookupResultDto;
import com.livescribe.admin.controller.dto.RegisteredUserDto;
import com.livescribe.admin.lookup.LookupCriteriaList;
import com.livescribe.admin.lookup.QueryField;

/**
 * @author Mohammad M. Naqvi
 * @since 1.3
 *
 */
public class InfoLookupServiceImpl implements InfoLookupService {

	private Logger logger = Logger.getLogger(InfoLookupServiceImpl.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PenService penService;

	/**
	 * 
	 */
	public InfoLookupServiceImpl() {
	}

	@Override
	public InfoLookupResultDto doInfoLookupByCriteria(LookupCriteriaList criteriaList) {
		
		String methodName = "doInfoLookupByCriteria():	";
		logger.debug(methodName + "called with " + criteriaList.getCriteriaList().get(0).toString());
		
		
		QueryField queryField = criteriaList.getCriteriaList().get(0).getQueryField();
		List<RegisteredUserDto> userInfo = null;
		List<DeviceDto> unregisteredDevicesInfo = null;
		
		
		if (!QueryField.PEN_DISPLAY_ID.equals(queryField) && !QueryField.PEN_SERIAL_NUMBER.equals(queryField)) {
			logger.debug(methodName + " Should search by USER service");
			// NOT searching by Pen Display/Serial..
			userInfo = userService.findUserDtoByCriteriaList(criteriaList);
			if (null == userInfo || userInfo.isEmpty()) {
				logger.debug("Found NO user(s)");
			} else {
				logger.debug("Found " + userInfo.size() + " user(s)");
			}
		} else {
			// searching by Pen Display/Serial..
			logger.debug(methodName + " Should search by PEN service");
			
			userInfo = penService.findRegisteredUserDtoByCriteriaList(criteriaList);
			if (null == userInfo || userInfo.isEmpty()) {
				logger.debug("Found NO user(s)");
			} else {
				logger.debug("Found " + userInfo.size() + " user(s)");
			}
			
			unregisteredDevicesInfo = penService.findUnregisteredDeviceDtoByCriteriaList(criteriaList);
			if (null == unregisteredDevicesInfo || unregisteredDevicesInfo.isEmpty()) {
				logger.debug("Found NO Un-registered device(s)");
			} else {
				logger.debug("Found " + unregisteredDevicesInfo.size() + " Un-registered device(s)");
			}
		}
		
		InfoLookupResultDto resultDto = new InfoLookupResultDto();
		resultDto.setUsersInfo(userInfo);
		resultDto.setUnregisteredDevicesInfo(unregisteredDevicesInfo );
		return resultDto;
	}
}

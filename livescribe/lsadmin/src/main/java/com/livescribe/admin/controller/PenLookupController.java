package com.livescribe.admin.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.admin.service.PenService;
import com.livescribe.afp.AFPException;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.web.lssettingsservice.client.LSSettingsServiceClient;
import com.livescribe.web.lssettingsservice.client.exception.InvalidSettingDataException;
import com.livescribe.web.lssettingsservice.client.exception.LSSettingsServiceConnectionException;
import com.livescribe.web.lssettingsservice.client.exception.NoRegisteredDeviceFoundException;
import com.livescribe.web.lssettingsservice.client.exception.NoSettingFoundException;
import com.livescribe.web.lssettingsservice.client.exception.ProcessingErrorException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Controller(value = "PenLookupController")
@RequestMapping("/penLookup.htm")
public class PenLookupController {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	PenService penService;
	
	/**
	 * <p></p>
	 * 
	 * @param model
	 * @param response
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String initForm(ModelMap model, HttpServletResponse response) {

		return "penLookup";
	}

	/**
	 * <p></p>
	 * 
	 * @param serialNumber
	 * @param map
	 * 
	 * @return
	 */
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, params = "pageAction=lookup")
	public String onSubmit(@RequestParam String penID, ModelMap map) {
		
		Pen pen = null;
		RegisteredDevice device = null;
		String penName = null;
		StringBuilder error = new StringBuilder(); 
		try {
			pen = penService.findPenById(penID);
			if (pen != null) {
				device = penService.findRegisterDeviceById(pen.getSerialnumber());
				if (device != null) {
					LSSettingsServiceClient client = new LSSettingsServiceClient();
					penName = client.getPenName(pen.getSerialnumber());
				}
			} else {
				error.append("No pen found with pen ID = " + penID + ".");
			}
		} catch (IllegalArgumentException e) {
			error.append(e.getMessage());
		} catch (InvalidSettingDataException e) {
			error.append(e.getMessage());
		} catch (LSSettingsServiceConnectionException e) {
			error.append(e.getMessage());
		} catch (NoSettingFoundException e) {
			error.append("Pen's name has not been set for this pen.");
		} catch (NoRegisteredDeviceFoundException e) {
			error.append(e.getMessage());
		} catch (ProcessingErrorException e) {
			error.append(e.getMessage());
		} catch (IOException e) {
			error.append(e.getMessage());
		} catch (AFPException e) {
			error.append("Invalid pen serial number or display ID.");
		}
		if (error.length() != 0)
			map.addAttribute("errorMessage", error.toString());
		map.addAttribute("pen", pen);
		map.addAttribute("regDevice", device);
		map.addAttribute("penName", penName);
		return "penLookup";
	}
}

package com.livescribe.web.registration.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.response.RegistrationHistoryListResponse;
import com.livescribe.web.registration.service.RegistrationHistoryService;
import com.livescribe.web.registration.validation.ValidationUtil;

@Controller
public class FindRegistrationHistoryController {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static String VIEW_XML_RESPONSE = "xmlResponseView";
	
	@Autowired
	private RegistrationHistoryService registrationHistoryService;
	
	/**
	 * <p>Default constructor</p>
	 */
	public FindRegistrationHistoryController() {
		
	}
	
	/**
	 * <p>Find registration history by penDisplayId</p>
	 * 
	 * @param penSerial
	 * @return
	 */
	@RequestMapping(value = "/history/pen/{penSerial}", method = RequestMethod.GET)
	public ModelAndView findRegistrationHistoryByPenSerial(@PathVariable String penSerial) {
		
		String method = "findRegistrationHistoryByPenSerial()";
		
		ServiceResponse response = null;
		ModelAndView mv = new ModelAndView();
		
		logger.info("BEFORE - " + method);
		long start = System.currentTimeMillis();
		
		// Validate penSerial param
		if (penSerial == null || penSerial.isEmpty()) {
			String msg = "Missing 'penSerial' parameter.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
		}

		// Validate penSerial or displayId
		if (!ValidationUtil.isValidPenID(penSerial)) {
			String msg = "Invalid penSerial or pen displayId format.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
		}
		
		List<RegistrationHistory> registrations = null;
		try {
			registrations = registrationHistoryService.findByPenSerial(penSerial);
			response = new RegistrationHistoryListResponse(ResponseCode.SUCCESS, registrations);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No registration history record found for pen '" + penSerial + "'.";
			logger.info(method + " - " + msg);
			response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} finally {
			long end = System.currentTimeMillis();
			long duration = end - start;
			logger.info("AFTER - " + method + " - Completed after " + duration + " milliseconds.");
		}
		
	}
	
	/**
	 * <p>Find registration history by email</p>
	 * 
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/history/email/{email}", method = RequestMethod.GET)
	public ModelAndView findRegistrationHistoryByEmail(@PathVariable String email) {
		
		String method = "findRegistrationHistoryByEmail()";
		
		ServiceResponse response = null;
		ModelAndView mv = new ModelAndView();
		
		logger.info("BEFORE - " + method);
		long start = System.currentTimeMillis();
		
		// Validate email param
		if (email == null || email.isEmpty()) {
			String msg = "Missing 'email' parameter.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
		}

		// Validate email format
		if (!ValidationUtil.isValidEmailFormat(email)) {
			String msg = "Invalid email format.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
		}

		List<RegistrationHistory> registrations = null;
		try {
			registrations = registrationHistoryService.findByEmail(email);
			response = new RegistrationHistoryListResponse(ResponseCode.SUCCESS, registrations);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No registration history record found for email '" + email + "'.";
			logger.info(method + " - " + msg);
			response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} finally {
			long end = System.currentTimeMillis();
			long duration = end - start;
			logger.info("AFTER - " + method + " - Completed after " + duration + " milliseconds.");
		}
		
	}
}

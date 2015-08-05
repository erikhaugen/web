package com.livescribe.web.registration.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.response.RegistrationListResponse;
import com.livescribe.web.registration.response.RegistrationResponse;
import com.livescribe.web.registration.service.RegistrationService;
import com.livescribe.web.registration.validation.ValidationUtil;

@Controller
public class FindRegistrationController {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static String VIEW_XML_RESPONSE = "xmlResponseView";
	
	@Autowired
	private RegistrationService registrationService;
	
	/**
	 * <p>Default constructor</p>
	 */
	public FindRegistrationController() {
		
	}
	
	/**
	 * <p>Find a unique registration by a combination of penDisplayId, appId and email</p>
	 * 
	 * @param displayId
	 * @param appId
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/find/unique/{displayId}/{appId}/{email}", method = RequestMethod.GET)
	public ModelAndView findUniqueRegistration(@PathVariable String displayId, @PathVariable String appId, @PathVariable String email) {
		
		String method = "findUniqueRegistration()";
		
		ServiceResponse response = null;
		ModelAndView mv = new ModelAndView();
		
		logger.info("BEFORE - " + method);
		long start = System.currentTimeMillis();
		
		// Validate penSerial param
		if (displayId == null || displayId.isEmpty()) {
			String msg = "Missing 'penSerial' parameter.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
		}
		
		// Validate penSerial or displayId
		if (!ValidationUtil.isValidPenID(displayId)) {
			String msg = "Invalid penSerial or pen displayId format.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
		}
		
		// Validate appId param
		if (appId == null || appId.isEmpty()) {
			String msg = "Missing 'appId' parameter.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
		}
		
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
		
		Registration registration = null;
		try {
			registration = registrationService.find(appId, displayId, email);
			response = new RegistrationResponse(ResponseCode.SUCCESS, registration);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No registration record found for display ID '" + displayId + "'.";
			logger.info(method + " - " + msg);
			response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (MultipleRecordsFoundException e) {
			String msg = "Multiple registration records found for display ID '" + displayId + "'.";
			logger.info(method + " - " + msg);
			response = new ErrorResponse(ResponseCode.DUPLICATE_REGISTRATION_FOUND, msg);
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
	 * <p>Find registrations by penDisplayId</p>
	 * 
	 * @param displayId
	 * @return
	 */
	@RequestMapping(value = "/find/reg/pendisplayid/equals/{displayId}", method = RequestMethod.GET)
	public ModelAndView findRegistrationsByPenDisplayIdEquals(@PathVariable String displayId) {
		
		String method = "findRegistrationByDisplayIdEquals()";
		
		ServiceResponse response = null;
		ModelAndView mv = new ModelAndView();
		
		logger.info("BEFORE - " + method);
		long start = System.currentTimeMillis();
		
		// Validate penSerial param
		if (displayId == null || displayId.isEmpty()) {
			String msg = "Missing 'displayId' parameter.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
		}
		
		// Validate displayId format
		if (!ValidationUtil.isValidPenDisplayIdFormat(displayId)) {
			String msg = "Invalid pen displayId format.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
		}
		
		List<Registration> registrations = null;
		try {
			registrations = registrationService.findByPenSerial(displayId);
			response = new RegistrationListResponse(ResponseCode.SUCCESS, registrations);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No registration record found for display ID '" + displayId + "'.";
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
	 * <p>Find registrations by penDisplayId</p>
	 * 
	 * @param displayId
	 * @return
	 */
	@RequestMapping(value = "/find/reg/pendisplayid/contains/{displayId}", method = RequestMethod.GET)
	public ModelAndView findRegistrationsByPenDisplayIdContains(@PathVariable String displayId) {
		
		String method = "findRegistrationsByDisplayIdContains(" + displayId + ")";
		
		ServiceResponse response = null;
		ModelAndView mv = new ModelAndView();
		
		logger.info("BEFORE - " + method);
		long start = System.currentTimeMillis();
		
		// Validate penSerial param
		if (displayId == null || displayId.isEmpty()) {
			String msg = "Missing 'penSerial' parameter.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
		}
		
		List<Registration> registrations = null;
		try {
			registrations = registrationService.findByPartialPenDisplayId(displayId);
			response = new RegistrationListResponse(ResponseCode.SUCCESS, registrations);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No registration record found for display ID '" + displayId + "'.";
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
	 * <p>Find registrations by penSerial</p>
	 * 
	 * @param penSerial
	 * @return
	 */
	@RequestMapping(value = "/find/reg/penserial/equals/{penSerial}", method = RequestMethod.GET)
	public ModelAndView findRegistrationsByPenSerialEquals(@PathVariable String penSerial) {
		
		String method = "findRegistrationsByPenSerialEquals(" + penSerial + ")";
		
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
		
		// Validate penSerial
		if (!ValidationUtil.isValidPenSerialFormat(penSerial)) {
			String msg = "Invalid penSerial format.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
		}
		
		List<Registration> registrations = null;
		try {
			registrations = registrationService.findByPenSerial(penSerial);
			response = new RegistrationListResponse(ResponseCode.SUCCESS, registrations);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No registration record found for device ID '" + penSerial + "'.";
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
	

	@RequestMapping(value = "/find/reg/penserial/contains/{penSerial}", method = RequestMethod.GET)
	public ModelAndView findRegistrationsByPenSerialContains(@PathVariable String penSerial) {
		
		String method = "findRegistrationByPenSerialContains(" + penSerial + ")";
		
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
		
		List<Registration> registrations = null;
		try {
			registrations = registrationService.findByPartialPenSerial(penSerial);
			response = new RegistrationListResponse(ResponseCode.SUCCESS, registrations);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No registration record found for device ID '" + penSerial + "'.";
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
	 * <p>Find registrations by appId</p>
	 * 
	 * @param appId
	 * @return
	 */
	@RequestMapping(value = "/find/app/{appId}", method = RequestMethod.GET)
	public ModelAndView findRegistrationByAppId(@PathVariable String appId) {
		
		String method = "findRegistrationByAppId()";
		
		ServiceResponse response = null;
		ModelAndView mv = new ModelAndView();
		
		logger.info("BEFORE - " + method);
		long start = System.currentTimeMillis();
		
		// Validate appId param
		if (appId == null || appId.isEmpty()) {
			String msg = "Missing 'appId' parameter.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
		}
		
		List<Registration> registrations = null;
		try {
			registrations = registrationService.findByAppId(appId);
			response = new RegistrationListResponse(ResponseCode.SUCCESS, registrations);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No registration record found for app ID '" + appId + "'.";
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
	 * <p>Find registrations by email</p>
	 * 
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/find/reg/email/equals/{email}", method = RequestMethod.GET)
	public ModelAndView findRegistrationsByEmailEquals(@PathVariable String email) {
		
		String method = "findRegistrationsByEmailEquals(" + email + ")";
		
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
		
		List<Registration> registrations = null;
		try {
			registrations = registrationService.findByEmail(email);
			response = new RegistrationListResponse(ResponseCode.SUCCESS, registrations);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No registration record found for email '" + email + "'.";
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
	 * <p>Find registrations by email</p>
	 * 
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/find/reg/email/contains/{email}", method = RequestMethod.GET)
	public ModelAndView findRegistrationsByEmailContains(@PathVariable String email) {
		
		String method = "findRegistrationsByEmailContains(" + email + ")";
		
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

		List<Registration> registrations = null;
		try {
			registrations = registrationService.findByPartialEmail(email);
			response = new RegistrationListResponse(ResponseCode.SUCCESS, registrations);
			logger.debug(response);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No registration record found for email '" + email + "'.";
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

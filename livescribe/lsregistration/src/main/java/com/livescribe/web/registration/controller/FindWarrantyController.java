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
import com.livescribe.framework.orm.vectordb.Warranty;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.response.WarrantyListResponse;
import com.livescribe.web.registration.response.WarrantyResponse;
import com.livescribe.web.registration.service.WarrantyService;
import com.livescribe.web.registration.validation.ValidationUtil;

@Controller
public class FindWarrantyController {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static String VIEW_XML_RESPONSE = "xmlResponseView";
	
	@Autowired
	private WarrantyService warrantyService;
	
	/**
	 * <p>Default constructor</p>
	 */
	public FindWarrantyController() {
		
	}
	
	/**
	 * <p>Find warranty by a combination of penDisplayId, appId and email</p>
	 * 
	 * @param penSerial
	 * @param appId
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/find/warranty/{penSerial}/{appId}/{email}", method = RequestMethod.GET)
	public ModelAndView findWarranty(@PathVariable String penSerial, @PathVariable String appId, @PathVariable String email) {
		
		String method = "findWarranty()";
		
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

		Warranty warranty = null;
		try {
			warranty = warrantyService.find(appId, penSerial, email);
			response = new WarrantyResponse(ResponseCode.SUCCESS, warranty);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No warranty record found for pen '" + penSerial + "'.";
			logger.info(method + " - " + msg);
			response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (MultipleRecordsFoundException e) {
			String msg = "Multiple warranty records found for pen '" + penSerial + "'.";
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
	
	@RequestMapping(value = "/find/warranty/pen/{penSerial}", method = RequestMethod.GET)
	public ModelAndView findWarrantyByPenSerial(@PathVariable String penSerial) {
		
		String method = "findWarrantyByPenSerial()";
		
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
		
		Warranty warranty = null;
		try {
			warranty = warrantyService.findByPenSerial(penSerial);
			response = new WarrantyResponse(ResponseCode.SUCCESS, warranty);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No warranty record found for pen '" + penSerial + "'.";
			logger.info(method + " - " + msg);
			response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, msg);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (MultipleRecordsFoundException e) {
			String msg = "Multiple warranty records found for pen '" + penSerial + "'.";
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
	
	@RequestMapping(value = "/find/warranty/email/{email}", method = RequestMethod.GET)
	public ModelAndView findWarrantyByEmail(@PathVariable String email) {
		
		String method = "findWarrantyByEmail()";
		
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
		
		List<Warranty> warrantyList = null;
		try {
			warrantyList = warrantyService.findByEmail(email);
			response = new WarrantyListResponse(ResponseCode.SUCCESS, warrantyList);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			String msg = "No warranty record found for email '" + email + "'.";
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

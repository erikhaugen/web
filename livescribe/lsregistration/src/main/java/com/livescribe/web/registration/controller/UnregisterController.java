package com.livescribe.web.registration.controller;

import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.service.RegistrationService;
import com.livescribe.web.registration.validation.UnregisterValidator;
import com.livescribe.web.registration.validation.ValidationUtil;

@Controller
public class UnregisterController {
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static String VIEW_XML_RESPONSE = "xmlResponseView";
	
	@Autowired
	private RegistrationService registrationService;
	
	/**
	 * <p></p>
	 * 
	 */
	public UnregisterController() {
		
	}
	
	/**
	 * <p>Initializes this <code>Controller</code> with a 
	 * <code>Validator</code> for use when a <code>@Valid</code>-annotated method
	 * parameter is encountered.</p>
	 * 
	 * @param binder
	 */
	@InitBinder("registrationData")
	protected void initBinder(WebDataBinder binder) {
		
		String method = "initBinder()";
		
		logger.debug(method + " - Adding validators ...");
		binder.addValidators(new UnregisterValidator());
	}
	
	@RequestMapping(value = "/unregister", method = RequestMethod.POST)
	public ModelAndView unregister(@Valid RegistrationData data, BindingResult result) {
		String method = "unregister()";
		
		logger.info("BEFORE - " + method);
		long start = System.currentTimeMillis();
		
		ServiceResponse response = null;
		ModelAndView mv = new ModelAndView();
				
		int errorCount = result.getErrorCount();
		
		//--------------------------------------------------
		//	Handle parameter errors.
		if (errorCount > 0) {
			logger.error(method + " - " + errorCount);
			List<ObjectError> errors = result.getAllErrors();
			Iterator<ObjectError> errorIter = errors.iterator();
			while (errorIter.hasNext()) {
				ObjectError err = errorIter.next();
				String message = err.getDefaultMessage();
				logger.error(method + " - " + message);
			}
			ObjectError error = errors.get(0);
			response = ValidationUtil.getErrorResponse(error);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			
			long end = System.currentTimeMillis();
			long duration = end - start;
			logger.info("AFTER - " + method + " - Completed after " + duration + " milliseconds.");
	
			return mv;
			
			
		} else {
			if ((result != null) && (data != null)) {
				logger.debug(method + " - appId:  " + data.getAppId());
				logger.debug(method + " - penSerial:  " + data.getPenSerial());
				logger.debug(method + " - email:  " + data.getEmail());
			}
			
			try {
				registrationService.unregister(data);
				
			} catch (RegistrationNotFoundException e) {
				logger.info(method + " - " + e.getMessage());
				response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, e.getMessage());
				mv.setViewName(VIEW_XML_RESPONSE);
				mv.addObject("response", response);
				return mv;
				
			} catch (MultipleRecordsFoundException e) {
				String msg = "Multiple registration records found for display ID '" + data.getPenSerial() + "'.";
				logger.info(method + " - " + msg);
				response = new ErrorResponse(ResponseCode.DUPLICATE_REGISTRATION_FOUND, msg);
				mv.setViewName(VIEW_XML_RESPONSE);
				mv.addObject("response", response);
				return mv;
			}
			
			response = new ServiceResponse(ResponseCode.SUCCESS);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
			
			return mv;
		}
				
	}
}

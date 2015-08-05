package com.livescribe.web.registration.controller;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.dto.UserDto;
import com.livescribe.web.registration.response.UserListResponse;
import com.livescribe.web.registration.service.UserService;
import com.livescribe.web.registration.validation.ValidationUtil;

@Controller
public class FindUsersController {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static String VIEW_XML_RESPONSE = "xmlResponseView";
	
	@Autowired
	private UserService userService;
	
	/**
	 * <p>Default constructor</p>
	 */
	public FindUsersController() {
		
	}
	
	/**
	 * <p>Find registrations by email</p>
	 * 
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/find/users/email/equals/{email}", method = RequestMethod.GET)
	public ModelAndView findUserByEmailEquals(@PathVariable String email) {
		
		String method = "findUserByEmailEquals(" + email + ")";
		
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
		
		Collection<UserDto> users = null;
		try {
			users = userService.findVectorUsersByEmail(email);
			if ((users == null) || (users.isEmpty())) {
				response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, "No users found with email address '" + email + "'.");
			} else {
				response = new UserListResponse(ResponseCode.SUCCESS, users);
				logger.debug(method + " received UserListResponse: " + response.toString());
			}
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
	@RequestMapping(value = "/find/users/email/contains/{email}", method = RequestMethod.GET)
	public ModelAndView findUserByEmailContains(@PathVariable String email) {
		
		String method = "findUserByEmailContains(" + email + ")";
		
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

		Collection<UserDto> users = null;
		try {
			users = userService.findVectorUsersByPartialEmail(email);
			if ((users == null) || (users.isEmpty())) {
				response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, "No users found with partial email address '" + email + "'.");
			} else {
				response = new UserListResponse(ResponseCode.SUCCESS, users);
				logger.debug(response);
			}
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

package com.livescribe.aws.tokensvc.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.livescribe.aws.tokensvc.exception.IncorrectPasswordException;
import com.livescribe.aws.tokensvc.exception.LoginException;
import com.livescribe.aws.tokensvc.exception.UserNotFoundException;
import com.livescribe.aws.tokensvc.response.ErrorResponse;
import com.livescribe.aws.tokensvc.response.ResponseCode;
import com.livescribe.aws.tokensvc.response.ServiceResponse;
import com.livescribe.aws.tokensvc.service.UserService;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.InvalidParameterException;

@Controller
@RequestMapping("/login.htm")
public class PenSetupLoginController {
	private static final String COOKIE_NAME = "tk";
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(ModelMap model, HttpServletResponse response) {
		
		return "loginView";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView onSubmit(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
		ServiceResponse  serviceResponse;
		
		// Validating email & password
		if (email == null || email.isEmpty()) {
			serviceResponse = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "Email was empty.");
			return new ModelAndView("loginView", "errResponse", serviceResponse);
		}
		
		if (password == null || password.isEmpty()) {
			serviceResponse = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "Password was empty.");
			return new ModelAndView("loginView", "errResponse", serviceResponse);
		}
		
		String loginToken = "";
		try {
			loginToken = userService.login(email, password);
			
		} catch (LoginException e) {
			serviceResponse = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			return new ModelAndView("loginView", "errResponse", serviceResponse);
			
		} catch (ClientException e) {
			serviceResponse = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			return new ModelAndView("loginView", "errResponse", serviceResponse);
			
		} catch (InvalidParameterException e) {
			serviceResponse = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			return new ModelAndView("loginView", "errResponse", serviceResponse);
			
		} catch (UserNotFoundException e) {
			serviceResponse = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			return new ModelAndView("loginView", "errResponse", serviceResponse);
			
		} catch (IncorrectPasswordException e) {
			serviceResponse = new ErrorResponse(ResponseCode.INCORRECT_PASSWORD, e.getMessage());
			return new ModelAndView("loginView", "errResponse", serviceResponse);
		}
		
		Cookie cookie = new Cookie(COOKIE_NAME, loginToken);
		cookie.setPath("/");
		cookie.setMaxAge(300);
		
		response.addCookie(cookie);
		
		return new ModelAndView(new RedirectView("penSetup.htm"));
	}
}

package com.livescribe.aws.login;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.livescribe.framework.exception.DuplicateEmailAddressException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.login.exception.IncorrectPasswordException;
import com.livescribe.framework.login.exception.LoginException;
import com.livescribe.framework.login.service.LoginService;
import com.livescribe.framework.login.service.result.LoginServiceResult;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;

@Controller(value = "LSLoginServiceTest")
@RequestMapping("/login.htm")
public class TestLoginController {
	private static final String COOKIE_NAME = "tk";
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private LoginService loginService;
	
	public TestLoginController() {

	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(ModelMap model, HttpServletResponse response) {
		
		return "loginView";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView onSubmit(
			@RequestParam String email, 
			@RequestParam String password, 
			@RequestParam(value = "reAuthorized", required = false) Boolean reAuthorized,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			HttpServletResponse response) {
		
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
		
		if (reAuthorized == null) {
			reAuthorized = false;
		}
		
		LoginServiceResult loginResult = null;
		try {
			loginResult = loginService.loginWifiUser(email, password, "WEB", null);
			
		} catch (IncorrectPasswordException e) {
			serviceResponse = new ErrorResponse(ResponseCode.INCORRECT_PASSWORD, "Incorrect password");
			return new ModelAndView("loginView", "errResponse", serviceResponse);
			
		} catch (LoginException e) {
			serviceResponse = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			return new ModelAndView("loginView", "errResponse", serviceResponse);
			
		} catch (DuplicateEmailAddressException e) {
			serviceResponse = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, e.getMessage());
			return new ModelAndView("loginView", "errResponse", serviceResponse);
			
		} catch (UserNotFoundException e) {
			serviceResponse = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			return new ModelAndView("loginView", "errResponse", serviceResponse);
			
		} catch (InvalidParameterException e) {
			serviceResponse = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			return new ModelAndView("loginView", "errResponse", serviceResponse);
			
		}
		
		Cookie cookie = new Cookie(COOKIE_NAME, loginResult.getLoginToken());
		cookie.setPath("/");
		cookie.setMaxAge(300);

		response.addCookie(cookie);
		
		logger.debug("Now redirecting to: " + "doEvernoteOAuth.xml?reAuthorized=" + reAuthorized.toString() + "&redirectUrl=" + redirectUrl);
		return new ModelAndView(new RedirectView("doEvernoteOAuth.do?reAuthorized=" + reAuthorized.toString() + "&redirectUrl=" + redirectUrl));
	}
}

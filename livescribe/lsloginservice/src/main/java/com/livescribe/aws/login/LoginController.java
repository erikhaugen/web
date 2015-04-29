/**
 * 
 */
package com.livescribe.aws.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import com.livescribe.aws.login.response.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.afp.AFPException;
import com.livescribe.aws.login.lsds.LSDSClient;
import com.livescribe.aws.login.mail.EmailFactory;
import com.livescribe.aws.login.mail.LSMailSenderImpl;
import com.livescribe.aws.login.util.AuthorizationType;
import com.livescribe.framework.config.AppConstants;
import com.livescribe.framework.dto.UserDTO;
import com.livescribe.framework.exception.DuplicateEmailAddressException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.LSDSClientException;
import com.livescribe.framework.exception.LocaleException;
import com.livescribe.framework.exception.LogoutException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.login.exception.AuthenticationException;
import com.livescribe.framework.login.exception.AuthorizationException;
import com.livescribe.framework.login.exception.EmailAlreadyTakenException;
import com.livescribe.framework.login.exception.IncorrectCaptchaException;
import com.livescribe.framework.login.exception.IncorrectPasswordException;
import com.livescribe.framework.login.exception.InsufficientPrivilegeException;
import com.livescribe.framework.login.exception.LoginException;
import com.livescribe.framework.login.exception.RegistrationNotFoundException;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.login.response.LoginResponse;
import com.livescribe.framework.login.service.LoginService;
import com.livescribe.framework.login.service.result.LoginServiceResult;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.framework.web.validation.EmailValidator;
import com.sun.mail.smtp.SMTPMessage;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Controller(value = "LSLoginService")
public class LoginController {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static String DEFAULT_LOCALE	= "en-US";
	private static String VIEW_ERROR		= "errorView";
	private static String VIEW_LOGIN		= "loginView";
	private static String VIEW_XML			= "xmlResponseView";
	
	@Autowired
	private EmailValidator emailValidator;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private LSMailSenderImpl mailService;

	@Autowired
	private LSDSClient lsdsClient;
	
	/**
	 * <p>Default class constructor.</p>
	 *
	 */
	public LoginController() {
		
	}

	/**
	 * <p>Changes a user&apos;s password.</p>
	 * 
	 * Checks that the old password matches what is in the database prior to
	 * making the change.
	 * 
	 * @param email the user&apos;s email address.
	 * @param oldPassword the user&apos;s old password.
	 * @param newPassword the user&apos;s new password.
	 * 
	 * @return an XML response with a <code>SUCCESS</code> code.
	 */
	@RequestMapping(value = "/chgpasswd", method = RequestMethod.POST)
	public ModelAndView changePassword(
			@RequestParam("email") String email,
			@RequestParam("old") String oldPassword,
			@RequestParam("new") String newPassword) {
		
		ModelAndView mv = new ModelAndView();
		
		//	Check for empty or 'null' parameters.
		ArrayList<String> paramList = new ArrayList<String>();
		paramList.add(email);
		paramList.add(oldPassword);
		paramList.add(newPassword);
		ServiceResponse response = null;
		try {
			response = validateParameters(paramList);
		}
		catch (InvalidParameterException ipe) {
			String msg = ipe.getMessage();
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		//	Validate password
		try {
			validatePassword(newPassword);
			
		} catch (InvalidParameterException e) {
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		boolean success = false;
		try {
			success = loginService.changePassword(email, oldPassword, newPassword);
		}
		catch (UserNotFoundException unfe) {
			String msg = unfe.getMessage();
			response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (AuthenticationException ae) {
			String msg = ae.getMessage();
			response = new ErrorResponse(ResponseCode.INCORRECT_PASSWORD, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		if (success) {
			response = new ServiceResponse(ResponseCode.SUCCESS);
			mv.setViewName(VIEW_XML);
			mv.addObject("response", response);
			return mv;
		}
		
		String msg = "Unable to update user's password.";
		response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
		mv.setViewName(VIEW_ERROR);
		mv.addObject("response", response);
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param trimmedEmail
	 * @param trimmedPassword
	 * @param locale
	 * @param occupation
	 * @param loginDomain
	 * 
	 * @return
	 */
	@RequestMapping(value = "/newWifiAccount", method = RequestMethod.POST)
	public ModelAndView createWifiAccount(
			@RequestParam("email") String email, 
			@RequestParam("password") String password, 
			@RequestParam("locale") String locale, 
			@RequestParam("occupation") String occupation, 
			@RequestParam("loginDomain") String loginDomain,
			@RequestParam("loginToken") String loginToken,
			@RequestParam(value = "sendDiagnostics", required = false) Boolean sendDiagnostics,
			@RequestParam("optIn") Boolean optIn) {
		
		ModelAndView mv = new ModelAndView();

		//	Check for empty or 'null' parameters.
		ArrayList<String> paramList = new ArrayList<String>();
		paramList.add(email);
		paramList.add(password);
		//	NOT required.
//		paramList.add(locale);
		paramList.add(occupation);
		paramList.add(loginDomain);
		paramList.add(optIn == null ? "" : String.valueOf(optIn));
		ServiceResponse response = null;
		try {
			response = validateParameters(paramList);
		}
		catch (InvalidParameterException ipe) {
			String msg = ipe.getMessage();
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		String trimmedEmail = email.trim();
		String trimmedPassword = password.trim();
		
		//	Validate the email address syntax.
		boolean validEmail = emailValidator.validate(trimmedEmail);
		if (!validEmail) {
			String msg = "The given email address [" + trimmedEmail + "] was invalid.";
			response = new ErrorResponse(ResponseCode.INVALID_EMAIL, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		// Valiate password
		try {
			validatePassword(trimmedPassword);
			
		} catch (InvalidParameterException e) {
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		User user = null;
		try {
			user = loginService.createWifiAccount(trimmedEmail, trimmedPassword, locale, occupation, loginDomain, optIn, sendDiagnostics);
		}
		catch (InvalidParameterException ipe) {
			String msg = ipe.getMessage();
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (EmailAlreadyTakenException eate) {
			String msg = eate.getMessage();
			response = new ErrorResponse(ResponseCode.EMAIL_ALREADY_IN_USE, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		} 
		catch (LocaleException le) {
			String msg = le.getMessage();
			response = new ErrorResponse(ResponseCode.INVALID_LOCALE, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		if (user == null) {
			String msg = "Unable to create account for user with email address '" + trimmedEmail + "'.";
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		//	Log the user in with the specified loginToken (provided by LSDesktopServer).
		LoginServiceResult loginResult = null;
		try {
			loginResult = loginService.loginWifiUser(trimmedEmail, trimmedPassword, loginDomain, loginToken);
		}
		catch (LoginException le) {
			String msg = le.getMessage();
			response = new ErrorResponse(ResponseCode.UNABLE_TO_LOG_USER_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (DuplicateEmailAddressException deae) {
			String msg = deae.getMessage();
			response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (UserNotFoundException unfe) {
			String msg = unfe.getMessage();
			response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (InvalidParameterException ipe) {
			String msg = ipe.getMessage();
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		response = new LoginResponse(ResponseCode.SUCCESS, loginResult.getLoginToken(), user.getUid());
		mv.setViewName(VIEW_LOGIN);
		mv.addObject("response", response);
	
		return mv;
	}
	
	/**
	 * <p>This method is used to create a new account in both Wifi world and WOApps world</p>
	 * 
	 * @param trimmedEmail
	 * @param trimmedPassword
	 * @param locale
	 * @param occupation
	 * @param loginDomain
	 * @param sendDiagnostics
	 * @param optIn
	 * @return
	 */
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public ModelAndView createAccount(		@RequestParam("email") String email, 
											@RequestParam("password") String password, 
											@RequestParam("locale") String locale, 
											@RequestParam(value = "occupation", required = false) String occupation, 
											@RequestParam("loginDomain") String loginDomain,
											@RequestParam(value = "sendDiagnostics", required = false) Boolean sendDiagnostics,
											@RequestParam("optIn") Boolean optIn) {
		
		String method = "createAccount(" + email + "):  ";
		
		long start = System.currentTimeMillis();
		
		ModelAndView mv = new ModelAndView();

		//	Check for empty or 'null' parameters.
		ArrayList<String> paramList = new ArrayList<String>();
		paramList.add(email);
		paramList.add(password);
		paramList.add(loginDomain);
		paramList.add(optIn == null ? "" : String.valueOf(optIn));
		ServiceResponse response = null;
		try {
			response = validateParameters(paramList);
			
		} catch (InvalidParameterException ipe) {
			String msg = ipe.getMessage();
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		String trimmedEmail = email.trim();
		String trimmedPassword = password.trim();
		
		//	Validate the email address syntax.
		logger.debug(method + "Validating email:" + trimmedEmail);
		boolean validEmail = emailValidator.validate(trimmedEmail);
		if (!validEmail) {
			String msg = "The given email address [" + trimmedEmail + "] was invalid.";
			response = new ErrorResponse(ResponseCode.INVALID_EMAIL, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		// Valiate password
		try {
			validatePassword(trimmedPassword);
			
		} catch (InvalidParameterException e) {
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		// Create new user and log that user in in both WiFi and WOApps worlds
		LoginServiceResult loginResult = null;
		try {
			loginResult = loginService.createAccount(trimmedEmail, trimmedPassword, locale, occupation, loginDomain, optIn, sendDiagnostics);
			
		} catch (InvalidParameterException ipe) {
			String msg = ipe.getMessage();
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
			
		} catch (EmailAlreadyTakenException eate) {
			String msg = eate.getMessage();
			response = new ErrorResponse(ResponseCode.EMAIL_ALREADY_IN_USE, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
			
		} catch (LocaleException le) {
			String msg = le.getMessage();
			response = new ErrorResponse(ResponseCode.INVALID_LOCALE, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
			
		} catch (XmlRpcException xe) {
			String msg = xe.getMessage();
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
			
		} catch (LSDSClientException lsdsce) {
			String msg = lsdsce.getMessage();
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
			
		} catch (LoginException le) {
			String msg = le.getMessage();
			response = new ErrorResponse(ResponseCode.UNABLE_TO_LOG_USER_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		} 
		
		if (loginResult == null) {
			String msg = "Unable to create account for user with email address '" + trimmedEmail + "'.";
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		response = new LoginResponse(ResponseCode.SUCCESS, loginResult.getLoginToken(), loginResult.getUser().getUid());
		mv.setViewName(VIEW_LOGIN);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
		
		long end = System.currentTimeMillis();
		logger.debug(method + "completed in " + (end - start) + " ms");

		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param trimmedEmail
	 * @param trimmedPassword
	 * @param locale
	 * @param occupation
	 * @param loginDomain
	 * @param sendDiagnostics
	 * @param optIn
	 * @param challenge
	 * @param resp
	 * @param request
	 * 
	 * @return
	 */
	@RequestMapping(value = "/newusercap", method = RequestMethod.POST)
	public ModelAndView createUser(
		@RequestParam("email") String email, 
		@RequestParam("password") String password, 
		@RequestParam("locale") String locale, 
		@RequestParam("occupation") String occupation, 
		@RequestParam("loginDomain") String loginDomain,
		@RequestParam(value = "sendDiagnostics", required = false) Boolean sendDiagnostics,
		@RequestParam("optIn") Boolean optIn,
		@RequestParam("recaptcha_challenge_field") String challenge,
		@RequestParam("recaptcha_response_field") String resp,
		HttpServletRequest request) {
		
		String method = "createUser() [/newusercap]:  ";
		
		String CHARSET			= "UTF-8";
		String MIME_TYPE_FORM	= "application/x-www-form-urlencoded";
		String PRIVATE_KEY		= "6Lf76s8SAAAAAKxwmbo54__xiyDWhgMJBx52pCfH";
		
		ModelAndView mv = new ModelAndView();
		StringBuilder sb = new StringBuilder();
		sb.append("\n- - - - - - - - - - - - - - - - - - - -\n");
		sb.append("  Captcha Details\n\n");
		
		//	Check for empty or 'null' parameters.
		ArrayList<String> paramList = new ArrayList<String>();
		paramList.add(email);
		sb.append("         email:  ").append(email).append("\n");
		paramList.add(password);
//		sb.append("      password:  ").append(password).append("\n");
		paramList.add(occupation);
		sb.append("    occupation:  ").append(occupation).append("\n");
		paramList.add(loginDomain);
		sb.append("  login domain:  ").append(loginDomain).append("\n");
		paramList.add(optIn == null ? "false" : String.valueOf(optIn));
		paramList.add(challenge);
		sb.append("     challenge:  ").append(challenge).append("\n");
		paramList.add(resp);
		sb.append("      response:  ").append(resp).append("\n");
		String remoteIP = request.getRemoteAddr();
		sb.append("     remote IP:  ").append(remoteIP).append("\n");
		
		ServiceResponse response = null;
		try {
			response = validateParameters(paramList);
		}
		catch (InvalidParameterException ipe) {
			String msg = ipe.getMessage();
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}		
		
		String trimmedEmail = email.trim();
		String trimmedPassword = password.trim();
		
		//	TODO:  Refactor this 'try/catch' section to a separate method and/or class.
		try {
			//	Prepare the parameters to verify the provided captch.
			URI uri = new URI("http://www.google.com/recaptcha/api/verify");
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("privatekey", PRIVATE_KEY));
			params.add(new BasicNameValuePair("remoteip", remoteIP));
			params.add(new BasicNameValuePair("challenge", challenge));
			params.add(new BasicNameValuePair("response", resp));
			String paramStr = URLEncodedUtils.format(params, CHARSET);
			StringEntity paramsEntity = new StringEntity(paramStr);
			
			//	Create the HTTP client and request.
			HttpClient client = new DefaultHttpClient();
			HttpPost postMethod = new HttpPost(uri);
			postMethod.setEntity(paramsEntity);
			logger.debug(method + "Posting captcha to:  " + postMethod.getURI().toString());
			
			//	Add HTTP headers to the request.
			BasicHeader charsetHeader = new BasicHeader(HttpHeaders.ACCEPT_CHARSET, CHARSET);
			BasicHeader contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, MIME_TYPE_FORM);
			postMethod.addHeader(charsetHeader);
			postMethod.addHeader(contentTypeHeader);
			HttpResponse httpResponse = client.execute(postMethod);
			
			//	Get the HTTP response.
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				
				//	Read the response content.
				InputStream in = entity.getContent();
				BufferedReader buffReader = new BufferedReader(new InputStreamReader(in, CHARSET));
				String line = null;
				ArrayList<String> lineList = new ArrayList<String>();
				while ((line = buffReader.readLine()) != null) {
					lineList.add(line);
				}
				buffReader.close();
				
				sb.append("\n       success:  ").append(lineList.get(0)).append("\n");
				sb.append("- - - - - - - - - - - - - - - - - - - -\n");
				logger.debug(method + sb.toString());
				
				//	Parse the content, which should either be "true", or "false".
				boolean success = Boolean.parseBoolean(lineList.get(0));
				if (!success) {
					logger.debug(method + "(" + lineList.get(0) + ") Captcha Failed!  Throwning exception ...");
					throw new IncorrectCaptchaException();
				}
				else
					logger.debug(method + "createUser():  Captcha PASSED!  Creating user ...");
					return createAccount(trimmedEmail, trimmedPassword, locale, occupation, loginDomain, sendDiagnostics, optIn);
				
			}
			else {
				sb.append("\n       success:  FAIL!!!").append("\n");
				sb.append("- - - - - - - - - - - - - - - - - - - -\n\n");
				logger.debug(sb.toString());
				logger.debug(method + "The HttpEntity from the HttpResponse was 'null'.");
				throw new IncorrectCaptchaException();
			}
		}
		catch (IncorrectCaptchaException cnme) {
			String msg = "reCAPTCHA failed!";
			logger.error(method + msg, cnme);
			response = new ErrorResponse(ResponseCode.INCORRECT_CAPTCHA, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (UnsupportedEncodingException uee) {
			String msg = "The server is configured to use an unsupported character encoding.";
			logger.error(method + msg, uee);
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (ClientProtocolException cpe) {
			String msg = "The server is configured to use an incorrect protocol.";
			logger.error(method + msg, cpe);
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (URISyntaxException use) {
			String msg = "The server is configured to use an incorrect URI syntax.";
			logger.error(method + msg, use);
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (IOException ioe) {
			String msg = "The server encountered an IO error.";
			logger.error(msg, ioe);
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}

	}
	
	/**
	 * <p>This method creates a new user account, but <b>does not</b> log that user in.</p>
	 * 
	 * This method is intended for use by other Services.
	 * 
	 * @param trimmedEmail
	 * @param trimmedPassword
	 * @param locale
	 * @param occupation
	 * @param loginDomain
	 * @param optIn
	 * 
	 * @return the newly created <code>User</code> object.
	 */
	@RequestMapping(value = "/newuser", method = RequestMethod.POST)
	public ModelAndView createUser(
		@RequestParam("email") String email, 
		@RequestParam("password") String password, 
		@RequestParam("locale") String locale, 
		@RequestParam("occupation") String occupation, 
		@RequestParam("loginDomain") String loginDomain,
		@RequestParam(value = "sendDiagnostics", required = false) Boolean sendDiagnostics,
		@RequestParam("optIn") Boolean optIn) {
		
		ModelAndView mv = new ModelAndView();
		
		//	Check for empty or 'null' parameters.
		ArrayList<String> paramList = new ArrayList<String>();
		paramList.add(email);
		paramList.add(password);
		//	NOT required.
//		paramList.add(locale);
		paramList.add(occupation);
		paramList.add(loginDomain);
		paramList.add(optIn == null ? "false" : String.valueOf(optIn));
		ServiceResponse response = null;
		try {
			response = validateParameters(paramList);
		}
		catch (InvalidParameterException ipe) {
			String msg = ipe.getMessage();
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		String trimmedEmail = email.trim();
		String trimmedPassword = password.trim();
		
		//	Validate the email address syntax.
		boolean validEmail = emailValidator.validate(trimmedEmail);
		if (!validEmail) {
			String msg = "The given email address [" + trimmedEmail + "] was invalid.";
			response = new ErrorResponse(ResponseCode.INVALID_EMAIL, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
	
		// Valiate password
		try {
			validatePassword(trimmedPassword);
			
		} catch (InvalidParameterException e) {
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		User user = null;

		try {
			user = loginService.createWifiAccount(trimmedEmail, trimmedPassword, locale, occupation, loginDomain, optIn, sendDiagnostics);
		}
		catch (InvalidParameterException ipe) {
			String msg = ipe.getMessage();
			logger.error(msg, ipe);
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (EmailAlreadyTakenException eate) {
			String msg = eate.getMessage();
			logger.error(msg, eate);
			response = new ErrorResponse(ResponseCode.EMAIL_ALREADY_IN_USE, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (LocaleException le) {
			String msg = le.getMessage();
			response = new ErrorResponse(ResponseCode.INVALID_LOCALE, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		if (user == null) {
			String msg = "Unable to create account for user with email address '" + trimmedEmail + "'.";
			logger.error(msg);
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		response = new NewUserResponse(ResponseCode.SUCCESS, user.getUid());
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		return mv;
	}

	/**
	 * <p>Returns information about a user identified by the given email address.</p>
	 * 
	 * @param email The email address to use in looking up the user.
	 * 
	 * @return 
	 */
	@RequestMapping(value = "/user/email/{email}", method = RequestMethod.GET)
	public ModelAndView findUserByEmail(@PathVariable("email") String email) {
		
		String method = "findUserByEmail():  ";
		
		ModelAndView mv = new ModelAndView();

		//	Validating 'email' param
		if (email == null || email.isEmpty()) {
			String msg = "Missing 'email' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		User user = null;
		try {
			user = this.loginService.findUserWithEmail(email);
		}
		catch (DuplicateEmailAddressException deae) {
			String msg = "Multiple 'User' accounts were found with the same email address: '" + email + "'.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (UserNotFoundException unfe) {
			String msg = "No user accounts were found with email address: '" + email + "'.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		UserInfoResponse response = new UserInfoResponse(ResponseCode.SUCCESS, user);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		return mv;
	}

	/**
	 * <p>Returns information about a user identified by the given email address.</p>
	 * 
	 * @param email The email address to use in looking up the user.
	 * 
	 * @return 
	 */
	@RequestMapping(value = "/user/uid/{uid}", method = RequestMethod.GET)
	public ModelAndView findUserByUId(@PathVariable("uid") String uid) {
		
		String method = "findUserByUId():  ";
		
		ModelAndView mv = new ModelAndView();

		//	Validating 'email' param
		if (uid == null || uid.isEmpty()) {
			String msg = "Missing 'uid' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		UserDTO user = null;
		try {
			user = this.loginService.findUserByUId(uid);
		}
		catch (UserNotFoundException unfe) {
			String msg = "No user accounts were found with uid: '" + uid + "'.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		UserInfoResponse response = new UserInfoResponse(ResponseCode.SUCCESS, user);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param serialNumber
	 * @return
	 */
	@RequestMapping(value = "/finduseruidbypendisplayid", method = RequestMethod.POST)
	public ModelAndView findUserUIDByPenDisplayId(@RequestParam("displayId") String displayId) {
		
		String method = "findUserUIDByPenDisplayId():  ";
		
		ModelAndView mv = new ModelAndView();

		//	Validating penDisplayId param
		if (displayId == null || displayId.isEmpty()) {
			String msg = "Missing 'displayId' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	TODO:  Add regular expression Pattern matching to validate correct format.
		if (displayId.length() != 14) {
			String msg = "The given 'displayId' parameter was invalid.  '" + displayId + "'";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		String uid = null;
		try {
			uid = loginService.findUserUIDByPenDisplayId(displayId);
		}
		catch (RegistrationNotFoundException rnfe) {
			String msg = rnfe.getMessage();
			logger.error(rnfe);
			ErrorResponse response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (AFPException ae) {
			String msg = ae.getMessage();
			logger.error(ae);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		//	If this happens, we have a bigger problem!  Like, how did we get
		//	a 'null' UID without throwing an exception?
		if (uid == null) {
			String msg = "A server error has occurred.";
			logger.error(msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		UIDResponse response = new UIDResponse(ResponseCode.SUCCESS, uid);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param serialNumber
	 * @return
	 */
	@RequestMapping(value = "/finduseruidbyserialnumber", method = RequestMethod.POST)
	public ModelAndView findUserUIDBySerialNumber(@RequestParam("serialNumber") String serialNumber) {
		
		String method = "findUserUIDBySerialNumber():  ";
		
		ModelAndView mv = new ModelAndView();

		//	Validating penDisplayId param
		if (serialNumber == null || serialNumber.isEmpty()) {
			String msg = "Missing 'displayId' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		String uid = null;
		try {
			uid = loginService.findUserUIDBySerialNumber(serialNumber);
		}
		catch (RegistrationNotFoundException rnfe) {
			String msg = rnfe.getMessage();
			logger.error(rnfe);
			ErrorResponse response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		//	If this happens, we have a bigger problem!  Like, how did we get
		//	a 'null' UID without throwing an exception?
		if (uid == null) {
			String msg = "A server error has occurred.";
			logger.error(msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		UIDResponse response = new UIDResponse(ResponseCode.SUCCESS, uid);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		return mv;
	}
	
	/**
	 * <p>Returns a token for use in authorizing access to a third-party site.</p>
	 * 
	 * Typically, this is an OAuth "access token".&nbsp;&nbsp;Supported third-parties
	 * are Evernote, and Facebook.
	 * 
	 * @param email
	 * @param authType
	 * 
	 * @return an XML response containing the OAuth access token.
	 * <pre>
	 * 	<response>
	 * 		<responseCode>SUCCESS</responseCode>
	 * 		<authorizationToken></authorizationToken>
	 * 	</response>
	 * </pre>
	 * 
	 * @throws InvalidParameterException
	 * 
	 * @see com.livescribe.aws.login.util.AuthorizationType
	 * @see com.livescribe.aws.login.response.AuthorizationTokenResponse
	 */
	public ModelAndView getAuthorizationToken(String email, AuthorizationType authType) {
		
		String method = "getAuthorizationToken():  ";
		
		ModelAndView mv = new ModelAndView();

		ServiceResponse response = null;
		if ((email == null) || ("".equals(email))) {
			String msg = method + "A required parameter was either empty or 'null'.";
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		if (authType == null) {
			String msg = method + "A required parameter was either empty or 'null'.";
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		String authToken = null;
		
		try {
			authToken = loginService.getAuthorizationToken(email, authType);
		}
		catch (InvalidParameterException ipe) {
			String msg = ipe.getMessage();
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (UserNotFoundException unfe) {
			String msg = unfe.getMessage();
			response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (AuthorizationException ae) {
			String msg = ae.getMessage();
			response = new ErrorResponse(ResponseCode.UNAUTHORIZED, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		response = new AuthorizationResponse(ResponseCode.SUCCESS);
		((AuthorizationResponse)response).setOauthAccessToken(authToken);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		return mv;
	}
	
	/**
	 * <p>Checks that all parameters in the given <code>List</code> are not
	 * empty or <code>null</code>.</p>
	 * 
	 * @param list The <code>List</code> of parameters to validate.
	 * 
	 * @throws InvalidParameterException if a parameter is found to be empty or <code>null</code>.
	 */
	private ServiceResponse validateParameters(List<String> list) throws InvalidParameterException {
		
		ServiceResponse response = null;
		
		for (String param : list) {
			if ((param == null) || ("".equals(param))) {
				String msg = "A required parameter was either empty or 'null'.";
				logger.debug("validateParameters():  The '" + param + "' parameter was 'null' or empty.");
				throw new InvalidParameterException(msg);
			}
		}
		return response;
	}
	
	/**
	 * <p>Returns whether the requesting user is logged in.</p>
	 * 
	 * @return <code>SUCCESS</code> if the user is logged in; 
	 * <code>USER_NOT_LOGGED_IN</code> if not.
	 */
	@RequestMapping(value = "/isloggedin")
	public ModelAndView isLoggedIn(@RequestParam("tk") String token, @RequestParam("loginDomain") String loginDomain) {
		
		ModelAndView mv = new ModelAndView();
		
		//	Find the login token in the list of cookies.
//		String token = null;
//		Cookie[] cookies = request.getCookies();

//		if (cookies == null) {
//			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN);
//			mv.setViewName(VIEW_ERROR);
//			mv.addObject("response", response);
//		}
//		
//		for (Cookie cookie : cookies) {
//			if ("tk".equals(cookie.getName())) {
//				token = cookie.getValue();
//				break;
//			}
//		}
		
		if (token == null) {
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
		}
		
//		String loginDomain = request.getParameter(PARAM_LOGIN_DOMAIN);
		
		boolean loggedIn = loginService.isLoggedIn(token, loginDomain);
		if (loggedIn) {
			User user = loginService.getUserInfoByLoginToken(token, loginDomain);
			
			LoginResponse response = new LoginResponse(ResponseCode.SUCCESS);
			response.setUid(user.getUid());
			mv.setViewName(VIEW_LOGIN);
			mv.addObject("response", response);
		}
		else {
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
		}
		return mv;
	}
	
	/**
	 * <p>This service is intended to be called by LSDesktopServer to login a user in WiFi system.</p>
	 * 
	 * @param email The user&apos;s email address.
	 * @param password The user&apos;s password.
	 * @param loginDomain The login domain to log the user into.
	 * @param loginToken the loginToken provided by LSDesktopServer
	 * 
	 * @return a login token string.
	 */
	@RequestMapping(value = "/loginWifiUser", method = RequestMethod.POST)
	public ModelAndView loginWifiUser(	@RequestParam("email") String email, 
										@RequestParam("password") String password, 
										@RequestParam("loginDomain") String loginDomain,
										@RequestParam("loginToken") String loginToken) {
		
		ModelAndView mv = new ModelAndView();

		LoginServiceResult loginResult = null;
		try {
			loginResult = loginService.loginWifiUser(email, password, loginDomain, loginToken);
			
		}
		catch (IncorrectPasswordException ipe) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INCORRECT_PASSWORD);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (LoginException e) {
			ErrorResponse response = new ErrorResponse(ResponseCode.UNABLE_TO_LOG_USER_IN);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (DuplicateEmailAddressException e) {
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (UserNotFoundException e) {
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (InvalidParameterException e) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (loginResult == null) {
			ErrorResponse response = new ErrorResponse(ResponseCode.UNABLE_TO_LOG_USER_IN);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		LoginResponse response = new LoginResponse(ResponseCode.SUCCESS, loginResult.getLoginToken(), loginResult.getUser().getUid());
		mv.setViewName(VIEW_LOGIN);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
		return mv;
	}
	
	/**
	 * <p>login a user in both wifi world and woapps world</p>
	 * 
	 * @param email
	 * @param password
	 * @param loginDomain
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("loginDomain") String loginDomain) {
		long start = System.currentTimeMillis();
		
		String method = "login(" + email + "):  ";
		
		ModelAndView mv = new ModelAndView();

		//	Verify all parameters have been sent.
		if ((email == null) || (email.isEmpty())) {
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		if ((password == null) || (password.isEmpty())) {
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		if ((loginDomain == null) || (loginDomain.isEmpty())) {
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		LoginServiceResult loginResult = null;
		try {
			loginResult = loginService.login(email, password, loginDomain);
//			loginResult = loginService.loginNewVersion(email, password, loginDomain);
		}
		catch (IncorrectPasswordException ipe) {
			String msg = "The provided password was incorrect.";
			logger.info(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INCORRECT_PASSWORD, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (LoginException le) {
			String msg = "An exception occurred when logging user '" + email + "' in.";
			logger.info(method + msg, le);
			ErrorResponse response = new ErrorResponse(ResponseCode.UNABLE_TO_LOG_USER_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (DuplicateEmailAddressException e) {
			String msg = "Duplicate email addresses were found for email '" + email + "'.";
			logger.info(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (UserNotFoundException e) {
			logger.info(method + e.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (InvalidParameterException ipe) {
			logger.info(method + ipe.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, ipe.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (XmlRpcException xre) {
			String msg = "XmlRpcException thrown when logging user '" + email + "' in.";
			logger.info(method + msg, xre);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (LSDSClientException e) {
			String msg = "LSDSClientException thrown when logging user '" + email + "' in.";
			logger.info(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (loginResult == null) {
			String msg = "A 'null' response was returned.";
			logger.info(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.UNABLE_TO_LOG_USER_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		String token = loginResult.getLoginToken();
		String uid = loginResult.getUser().getUid();
		
		LoginResponse response = new LoginResponse(ResponseCode.SUCCESS, token, uid);
		mv.setViewName(VIEW_LOGIN);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
		long end = System.currentTimeMillis();
		logger.debug(method + "completed in " + (end - start) + " ms");
		
		return mv;
	}
	
	/**
	 * <p>login a user in both wifi world and woapps world</p>
	 * 
	 * @param email
	 * @param password
	 * @param loginDomain
	 * @return
	 */
//	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login2(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("loginDomain") String loginDomain) {
		long start = System.currentTimeMillis();
		
		String method = "login(" + email + "):  ";
		
		ModelAndView mv = new ModelAndView();
		LoginServiceResult loginResult = null;
		
		//--------------------------------------------------
		//	Verify all parameters have been sent.
		if ((email == null) || (email.isEmpty())) {
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		if ((password == null) || (password.isEmpty())) {
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		if ((loginDomain == null) || (loginDomain.isEmpty())) {
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		String wifiDomain = loginDomain;
		
		//	If 'loginDomain' is an LS Desktop version, save it as "LD" in the WiFi world.
		if (loginDomain.matches(AppConstants.DESKTOP_VERSION_REGEX)) {
			wifiDomain = "LD";
		}

		//--------------------------------------------------
		//	Find the user account in the MySQL database.
		User user = null;
		try {
			user = loginService.findUserWithEmail(email);
		} catch (DuplicateEmailAddressException deae) {
			String msg = "Duplicate email address found in database:  '" + email + "'";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (UserNotFoundException unfe) {
			logger.warn(method + " - User '" + email + "' was not found in MySQL database.");
		}
		
		if (user == null) {
			
			boolean existsInLSDS = false;
			
			//--------------------------------------------------
			//	Determine if the user has an account in the 
			//	FrontBase database.
			try {
				existsInLSDS = loginService.doesUserExistInLSDS(email, password);
			} catch (UserNotFoundException unfe) {
				logger.warn(method + "User '" + email + "' not found in FrontBase.");

				//--------------------------------------------------
				//	Check if the user exists in WOApps, but returned 
				//	UserNotFoundException due to incorrect password.
				try {
					boolean isUserExistedWithWrongPassword = lsdsClient.isUserExisted(email, null);
					if (isUserExistedWithWrongPassword) {
						String msg = "Incorrect password when logging user '" + email + "' in WOApps.";
						logger.warn(method + msg);
						ErrorResponse response = new ErrorResponse(ResponseCode.INCORRECT_PASSWORD, msg);
						mv.setViewName(VIEW_ERROR);
						mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
						return mv;
					}
				} catch (XmlRpcException xre) {
					String msg = "XmlRpcException thrown when determining if user account '" + email + "' exists in FrontBase.";
					logger.error(method + msg);
					ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
					mv.setViewName(VIEW_ERROR);
					mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
					return mv;
				}
			} catch (XmlRpcException xre) {
				String msg = "XmlRpcException thrown when looking up user '" + email + "' in FrontBase.";
				logger.error(method + msg);
				ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
				mv.setViewName(VIEW_ERROR);
				mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
				return mv;
			}
			
			if (existsInLSDS) {
				
				//--------------------------------------------------
				//	Create the user in MySQL and log the user in.
				try {
					
					//	Get the 'shortId' of the user account in FrontBase.
					String fbUserShortId = lsdsClient.getExistingUserShortId(email, null);
					
					//	Create Wifi user with the 'shortId' (UID) retrieved from WOApps world
					loginService.createWifiUser(email, password, DEFAULT_LOCALE, "NA", wifiDomain, false, false, fbUserShortId);
					
					//	Log this user into WiFi world
					loginResult = loginService.loginWifiUser(email, password, wifiDomain, null);
					
				} catch (XmlRpcException xre) {
					String msg = "XmlRpcException thrown when attempting to get 'shortId' of user account '" + email + "' from FrontBase.";
					logger.error(method + msg);
					ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
					mv.setViewName(VIEW_ERROR);
					mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
					return mv;
				} catch (InvalidParameterException ipe) {
					String msg = "InvalidParameterException thrown when attempting to create user account '" + email + "' or log the user in.";
					logger.warn(method + msg);
					ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
					mv.setViewName(VIEW_ERROR);
					mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
					return mv;
				} catch (EmailAlreadyTakenException eate) {
					String msg = "EmailAlreadyTakenException thrown";
					//	This exception is unlikely to happen due to the UserNotFoundException above
				} catch (LocaleException le) {
					String msg = "LocaleException thrown";
					//	Unlikely to happen since we're using hard-coded default locale
				} catch (LoginException le) {
					String msg = "LoginException thrown when logging '" + email + "' into the Wifi database.  " + le.getMessage();
					logger.warn(method + msg);
					ErrorResponse response = new ErrorResponse(ResponseCode.UNABLE_TO_LOG_USER_IN, msg);
					mv.setViewName(VIEW_ERROR);
					mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
					return mv;
				} catch (DuplicateEmailAddressException deae) {
					String msg = "DuplicateEmailAddressException thrown";
					logger.error(method + msg);
					ErrorResponse response = new ErrorResponse(ResponseCode.UNABLE_TO_LOG_USER_IN, msg);
					mv.setViewName(VIEW_ERROR);
					mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
					return mv;
				} catch (UserNotFoundException unfe) {
					String msg = "Unable to find user '" + email + "' in Wifi database.";
					logger.warn(method + msg);
					ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, msg);
					mv.setViewName(VIEW_ERROR);
					mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
					return mv;
				}

			} else {
				String msg = "User '" + email + "' exists in WOApps world but incorrect password was provided.";
				logger.warn(method + msg);
				ErrorResponse response = new ErrorResponse(ResponseCode.INCORRECT_PASSWORD, msg);
				mv.setViewName(VIEW_ERROR);
				mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
				return mv;
			}
			
			if (loginResult != null) {
				
				String loginToken = loginResult.getLoginToken();
				User wifiUser = loginResult.getUser();
				
				//--------------------------------------------------
				//	Log the user into WOApps/FrontBase world.
				try {
					lsdsClient.login(email, password, loginResult.getLoginToken(), "WEB");
				} catch (XmlRpcException xre) {
					String msg = "XmlRpcException thrown";
				} catch (InvalidParameterException ipe) {
					String msg = "InvalidParameterException thrown";
				} catch (UserNotFoundException unfe) {					
					
					//--------------------------------------------------
					//	Create the user account in FrontBase.
					try {
						loginService.createFrontBaseAccount(email, password, wifiUser, loginToken);
					} catch (Exception e) {
						String msg = "Exception thrown";
						//	Trying to remove account because the creation could be ok 
						//	but a network problem makes a fake failure.
						try {
							boolean ret = lsdsClient.removeUser(email);
							if (!ret) {
								logger.error("Fail to rollback user (" + email + ")");
							}
						} catch (XmlRpcException e1) {
							logger.error("Looks like that it failed to rollback user (" + email + "): " + e1.getMessage(), e1);
						}
					}
					
				} catch (LSDSClientException lce) {
					String msg = "LSDSClientException thrown";
				}
			} else {
				String msg = "Unable to log user '" + email + "' into Wifi database due to an unknown error.";
				logger.warn(method + msg);
				ErrorResponse response = new ErrorResponse(ResponseCode.UNABLE_TO_LOG_USER_IN, msg);
				mv.setViewName(VIEW_ERROR);
				mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
				return mv;
			}
		}
		
		String token = loginResult.getLoginToken();
		String uid = loginResult.getUser().getUid();
		
		LoginResponse response = new LoginResponse(ResponseCode.SUCCESS, token, uid);
		mv.setViewName(VIEW_LOGIN);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
		long end = System.currentTimeMillis();
		logger.debug(method + "completed in " + (end - start) + " ms");

		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param tk
	 * @param loginDomain
	 * 
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public ModelAndView logout(@RequestParam("tk") String loginToken, @RequestParam("loginDomain") String loginDomain) {
		
		String method = "logout():  ";
		
		ModelAndView mv = new ModelAndView();
		
		try {
			loginService.logout(loginToken, loginDomain);
			
		} catch (InvalidParameterException e) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (LogoutException e) {
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (HibernateException he) {
			logger.error(method + "HibernateException thrown when attempting to log user out with login token '" + loginToken + "'.", he);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, he.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (XmlRpcException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} 
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_LOGIN);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * 
	 * @return
	 */
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public ModelAndView removeAccount(String email) {
		
		String method = "removeAccount():  ";
		
		ModelAndView mv = new ModelAndView();

		if ((email == null) || ("".equals(email))) {
			String msg = method + "No email address was provided.";
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_EMAIL, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		//	Validate the email address.
		boolean validEmail = emailValidator.validate(email);
		if (!validEmail) {
			String msg = "Email address '" + email + "' is not valid.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_EMAIL, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		try {
			loginService.removeAccount(email);
		}
		catch (UserNotFoundException unfe) {
			String msg = "User not found with email address '" + email + "'.";
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (HibernateException he) {
			logger.error(method + "HibernateException when attempting to remove account '" + email + "'.", he);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, he.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		return mv;
	}
	
	/**
	 * <p>ping to see if LSLoginService is alive</p>
	 * 
	 * @return
	 */
	@RequestMapping(value = "/ping", method = RequestMethod.POST)
	public ModelAndView ping() {
		ModelAndView mv = new ModelAndView();
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		return mv;
	}
	
	/**
	 * <p></p>
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/forgotpassword", method = RequestMethod.POST)
	public ModelAndView forgotPassword(@RequestParam("email") String email) {
		ModelAndView mv = new ModelAndView();
		
		// trim trailing spaces
		email = email.trim();
		
		// Validate if email is empty
		if (email == null || email.isEmpty()) {
			String msg = "Email address cannot be emtpy.";
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		// Validate the email address syntax.
		boolean validEmail = emailValidator.validate(email);
		if (!validEmail) {
			String msg = "The given email address [" + email + "] was invalid.";
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_EMAIL, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		// Validate email existence
		boolean isEmailTaken = loginService.isEmailAddressTaken(email);
		if (!isEmailTaken) {
			String msg = "The given email address [" + email + "] does not exist.";
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		// Prepare sending email
		SMTPMessage message = null;
		
		logger.debug("Creating email ...");
		
		try {
			message = mailService.createSMTPMessage();
			InternetAddress toAddr = new InternetAddress(email);
			toAddr.setPersonal("Livescribe Customer");
			message.addRecipient(Message.RecipientType.TO, toAddr);
			message.setSubject("Reset your password");
			
			String emailContent = EmailFactory.createForgotPasswordMail();
			
			message.setContent(emailContent, "text/html");
			
			mailService.send(message);
		}
		catch (AddressException ae) {
			String msg = "AddressException thrown while attempting to create email address '" + email + "'.";
			logger.error(msg, ae);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (MessagingException me) {
			String msg = "MessagingException thrown while attempting to create email address '" + email + "'.";
			logger.error(msg, me);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (UnsupportedEncodingException uee) {
			String msg = "UnsupportedEncodingException thrown while attempting to create email address '" + email + "'.";
			logger.error(msg, uee);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		return mv;
	}
	
	@RequestMapping(value = "/getuserinfo", method = RequestMethod.POST)
	public ModelAndView getUserInfo(@RequestParam("tk") String loginToken, @RequestParam("loginDomain") String loginDomain) {
		
		String method = "getUserInfo():  ";
		
		ModelAndView mv = new ModelAndView();
		
		if (loginToken == null || loginDomain == null) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		User user = null;
		try {
			user = loginService.getUserInfoByLoginToken(loginToken, loginDomain);
		} catch (Exception e) {
			String msg = "There's error occured when finding loginToken=" + loginToken + " loginDomain=" + loginDomain + " in database.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (user == null) {
			String msg = "Could not find user with loginToken=" + loginToken + " loginDomain=" + loginDomain;
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		UserInfoResponse response = new UserInfoResponse(user);
		response.setResponseCode(ResponseCode.SUCCESS);
		
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		return mv;
	}
	

	
	@RequestMapping(value = "/changeUserEmailByLoginToken", method = RequestMethod.POST)
	public ModelAndView changeUserEmail(@CookieValue("tk") String loginToken, @RequestParam("currentEmail") String currentEmail, @RequestParam("newEmail") String newEmail) {
		ModelAndView mv = new ModelAndView();
		
		if ((loginToken == null) || (loginToken.isEmpty())) {
			String msg = "loginToken cookie value is mandatory, but missing.";
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_TOKEN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		// validate currentEmail param
		if (currentEmail == null || currentEmail.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'currentEmail' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		// validate newEmail param
		if (newEmail == null || newEmail.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'newEmail' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		// Validate the currentEmil address syntax.
		if (!emailValidator.validate(currentEmail)) {
			String msg = "The given email address [" + currentEmail + "] was invalid.";
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_EMAIL, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		// Validate the newEmail address syntax
		if (!emailValidator.validate(newEmail)) {
			String msg = "The new email address [" + newEmail + "] was invalid.";
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_EMAIL, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		try {
			loginService.changeUserEmail(loginToken, currentEmail, newEmail);
			
		} catch (UserNotLoggedInException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (EmailAlreadyTakenException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.EMAIL_ALREADY_IN_USE, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (DuplicateEmailAddressException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (UserNotFoundException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (XmlRpcException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (LSDSClientException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (HibernateException he) {
			logger.error("changeUserEmail():  HibernateException when attempting to change email address for user '" + currentEmail + "'.", he);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, he.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		
		return mv;
	}
	
	/**
	 * Changes a user's primary email in the Mysql consumer DB as and in the legacy database.
	 * 
	 * @deprecated as of LSLogin 1.4. This is replaced by {@link #changeUserEmail(String, String, String)()} 
	 * @param currentEmail
	 * @param newEmail
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/changeUserEmail", method = RequestMethod.POST)
	public ModelAndView changeUserEmail(@RequestParam("currentEmail") String currentEmail, @RequestParam("newEmail") String newEmail) {
		ModelAndView mv = new ModelAndView();
		
		// validate currentEmail param
		if (currentEmail == null || currentEmail.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'currentEmail' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		// validate newEmail param
		if (newEmail == null || newEmail.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'newEmail' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		// Validate the currentEmil address syntax.
		if (!emailValidator.validate(currentEmail)) {
			String msg = "The given email address [" + currentEmail + "] was invalid.";
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_EMAIL, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		// Validate the newEmail address syntax
		if (!emailValidator.validate(newEmail)) {
			String msg = "The new email address [" + newEmail + "] was invalid.";
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_EMAIL, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		try {
			loginService.changeUserEmail(currentEmail, newEmail);
			
		} catch (UserNotLoggedInException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (EmailAlreadyTakenException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.EMAIL_ALREADY_IN_USE, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (DuplicateEmailAddressException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (UserNotFoundException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (XmlRpcException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (LSDSClientException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (HibernateException he) {
			logger.error("changeUserEmail():  HibernateException when attempting to change email address for user '" + currentEmail + "'.", he);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, he.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		
		return mv;
	}
	
	@RequestMapping(value = "/changeUserPassword", method = RequestMethod.POST)
	public ModelAndView changeUserPassword(@RequestParam("email") String email, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
		ModelAndView mv = new ModelAndView();
		
		if (email == null || email.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'email' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (oldPassword == null || oldPassword.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'oldPassword' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (newPassword == null || newPassword.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'newPassword' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		try {
			loginService.changeUserPassword(email, oldPassword, newPassword);
			
		} catch (IncorrectPasswordException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INCORRECT_PASSWORD, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (UserNotFoundException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (DuplicateEmailAddressException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (AuthenticationException e) {
			logger.error("Error when hashing user password", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, "Error when hashing user password");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (XmlRpcException e) {
			logger.error("Error occured when making XML-RPC call to LSDS", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, "Error occured when making XML-RPC call to LSDS");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (HibernateException he) {
			logger.error("changeUserPassword():  HibernateException when attempting to change password for user '" + email + "'.", he);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, he.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		
		return mv;	
	}
	
	@RequestMapping(value = "/changeUserPasswordWithLoginToken", method = RequestMethod.POST)
	public ModelAndView changeUserPasswordWithLoginToken(
			@RequestParam("loginToken") String loginToken,
			@RequestParam("email") String email,
			@RequestParam("newPassword") String newPassword) {
		
		ModelAndView mv = new ModelAndView();
		
		if (loginToken == null || loginToken.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'loginToken' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (email == null || email.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'email' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (newPassword == null || newPassword.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'newPassword' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		try {
			loginService.changeUserPasswordWithLoginToken(loginToken, email, newPassword);
			
		} catch (UserNotFoundException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (AuthenticationException e) {
			logger.error("Error when hashing user password", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, "Error when hashing user password");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (XmlRpcException e) {
			logger.error("Error occured when making XML-RPC call to LSDS", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, "Error occured when making XML-RPC call to LSDS");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (UserNotLoggedInException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (HibernateException he) {
			logger.error("changeUserPasswordWithLoginToken():  HibernateException when attempting to change password for user '" + email + "'.", he);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, he.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		
		return mv;	
	}
	
	@RequestMapping(value = "/changeUserPasswordForSupportUser", method = RequestMethod.POST)
	public ModelAndView changeUserPasswordForSupportUser(
			@RequestParam("supportUserLoginToken") String supportUserLoginToken,
			@RequestParam("userEmail") String userEmail,
			@RequestParam("userPassword") String userPassword) {
		
		ModelAndView mv = new ModelAndView();
		
		// validate supportUserLoginToken
		if (supportUserLoginToken == null || supportUserLoginToken.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'supportUserLoginToken' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (userEmail == null || userEmail.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'userEmail' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (userPassword == null || userPassword.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'userPassword' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		try {
			loginService.changeUserPasswordForSupportUser(supportUserLoginToken, userEmail, userPassword);
			
		} catch (UserNotLoggedInException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (InsufficientPrivilegeException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INSUFFICIENT_PRIVILEGE, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (DuplicateEmailAddressException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (UserNotFoundException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (AuthenticationException e) {
			logger.error("Error when hashing user password", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, "Error when hashing user password");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (XmlRpcException e) {
			logger.error("Error occured when making XML-RPC call to LSDS", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, "Error occured when making XML-RPC call to LSDS");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		
		return mv;
	}
	
	@RequestMapping(value = "/changePasswordWithoutUserTokenAndOldPassword", method = RequestMethod.POST)
	public ModelAndView changePasswordWithoutUserTokenAndOldPassword(
			@RequestParam("email") String email,
			@RequestParam("newPassword") String newPassword) {
		
		ModelAndView mv = new ModelAndView();
		
		if (email == null || email.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'email' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (newPassword == null || newPassword.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, "'newPassword' parameter is required.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		try {
			loginService.changePasswordWithoutUserTokenAndOldPassword(email, newPassword);
			
		} catch (UserNotFoundException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (DuplicateEmailAddressException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (XmlRpcException e) {
			logger.error("Error occured when making XML-RPC call to LSDS", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, "Error occured when making XML-RPC call to LSDS");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (AuthenticationException e) {
			logger.error("Error when hashing user password", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, "Error when hashing user password");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		
		return mv;
	}
	@RequestMapping(value = "/subscribeCustomer", method = RequestMethod.POST)
	public ModelAndView subscribeCustomer(				
				@RequestParam("tk") String loginToken) {
		
		ModelAndView mv = new ModelAndView();
				
		boolean isSubscribed = false;		
		try {			
			isSubscribed = loginService.subscribeCustomer(loginToken);			
		} catch (XmlRpcException e) {
			logger.error("Error occured when making XML-RPC call to LSDS", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (AuthenticationException e) {
			logger.error("Error occured, invalid token", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_TOKEN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (UserNotLoggedInException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		SubscribedResponse response = new SubscribedResponse(ResponseCode.SUCCESS, isSubscribed);		
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		
		return mv;
	}
	
	@RequestMapping(value = "/unsubscribeCustomer", method = RequestMethod.POST)
	public ModelAndView unsubscribeCustomer(				
				@RequestParam("tk") String loginToken) {
		
		ModelAndView mv = new ModelAndView();
			
		boolean isSubscribed = false;
		try {			
			isSubscribed = loginService.unsubscribeCustomer(loginToken);			
		} catch (XmlRpcException e) {
			logger.error("Error occured when making XML-RPC call to LSDS", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (AuthenticationException e) {
			logger.error("Error occured, invalid token", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_TOKEN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (UserNotLoggedInException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		SubscribedResponse response = new SubscribedResponse(ResponseCode.SUCCESS, isSubscribed);		
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		
		return mv;
	}
	
	@RequestMapping(value = "/isCustomerSubscribed", method = RequestMethod.POST)
	public ModelAndView isCustomerSubscribed(				
				@RequestParam("tk") String loginToken) {
		
		ModelAndView mv = new ModelAndView();
			
		boolean isSubscribed = false;	
		try {			
			isSubscribed = loginService.isCustomerSubscribed(loginToken);			
		} catch (XmlRpcException e) {
			logger.error("Error occured when making XML-RPC call to LSDS", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (AuthenticationException e) {
			logger.error("Error occured, invalid token", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_TOKEN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (UserNotLoggedInException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
				
		SubscribedResponse response = new SubscribedResponse(ResponseCode.SUCCESS, isSubscribed);			
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
				
		return mv;
	}
	
	@RequestMapping(value = "/subscribeGuest", method = RequestMethod.POST)
	public ModelAndView subscribeGuest(				
				@RequestParam("email") String email,
				@RequestParam("name") String name,
				@RequestParam("country") String country) {
		
		ModelAndView mv = new ModelAndView();
		
		boolean isSubscribed = false;
		try {			
			isSubscribed = loginService.subscribeGuest(email, name, country);			
		} catch (XmlRpcException e) {
			logger.error("Error occured when making XML-RPC call to LSDS", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (AuthenticationException e) {
			logger.error("Error occured, invalid token", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_TOKEN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
				
		SubscribedResponse response = new SubscribedResponse(ResponseCode.SUCCESS, isSubscribed);			
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
				
		return mv;
	}
	
	@RequestMapping(value = "/unsubscribeGuest", method = RequestMethod.POST)
	public ModelAndView unsubscribeGuest(				
				@RequestParam("email") String email) {
		
		ModelAndView mv = new ModelAndView();
		
		boolean isUnsubscribed = true;
		try {			
			isUnsubscribed = loginService.unsubscribeGuest(email);			
		} catch (XmlRpcException e) {
			logger.error("Error occured when making XML-RPC call to LSDS", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (AuthenticationException e) {
			logger.error("Error occured, invalid token", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_TOKEN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
				
		SubscribedResponse response = new SubscribedResponse(ResponseCode.SUCCESS, isUnsubscribed);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
				
		return mv;
	}

    @RequestMapping(value = "/getSupportAccessLink", method = RequestMethod.POST)
    public ModelAndView getSupportAccessLink(
            @CookieValue("tk") String loginToken) {

        ModelAndView mv = new ModelAndView();

        String ptaString = "";
        try {
            ptaString = loginService.composePTAUrl(loginToken);
        } catch (AuthenticationException e) {
            logger.error("Error occured, invalid token", e);
            ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_TOKEN, e.getMessage());
            mv.setViewName(VIEW_ERROR);
            mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
            return mv;
        } catch (InvalidParameterException e) {
            logger.error(e.getMessage(), e);
            ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
            mv.setViewName(VIEW_ERROR);
            mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
            return mv;
        } catch (UserNotLoggedInException e) {
            logger.error(e.getMessage(), e);
            ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
            mv.setViewName(VIEW_ERROR);
            mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
            return mv;
        } catch (UserNotFoundException e){
            logger.error(e.getMessage(), e);
            ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
            mv.setViewName(VIEW_ERROR);
            mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
            return mv;
        }

        ServiceResponse response = new PTAResponse(ResponseCode.SUCCESS, ptaString);

        mv.setViewName(VIEW_XML);
        mv.addObject("response", response);
        return mv;
    }
	
	@RequestMapping(value = "/isUserExisted", method = RequestMethod.POST)
	public ModelAndView isUserExisted(@RequestParam("email") String email) {
		ModelAndView mv = new ModelAndView();
		
		boolean isUserExisted = loginService.isEmailAddressTaken(email);
		
		ServiceResponse response = new ServiceResponse();
		
		if (isUserExisted) {
			response.setResponseCode(ResponseCode.USER_ALREADY_EXISTS);
		} else {
			response.setResponseCode(ResponseCode.USER_NOT_FOUND);
		}
		
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		return mv;
	}
	
	/**
	 * Validate user password
	 * 
	 * @param password
	 * @throws InvalidParameterException
	 */
	private static void validatePassword(String password) throws InvalidParameterException {
		int length = password != null ? password.trim().length() : 0;

		if (length == 0) {
			throw new InvalidParameterException("Empty password");
		}
		
		// Check if too short
		if (length < 4) {
			throw new InvalidParameterException("Password too short (4 characters at least)");
		}
		
		// check if too long
		if (length > 64) {
			throw new InvalidParameterException("Password too long (64 characters max)");
		}
		
		// check it does not start with a space
		if (password.startsWith(" ")) {
			throw new InvalidParameterException("Password can not start with space");
		}
		
		// check this is an ASCII password
		if (!isPrintableAsciiString(password)) {
			throw new InvalidParameterException("Password is not US-ASCII");
		}
	}
	
	private static boolean isPrintableAsciiString(String str) {
		int len = str.length();
		char codePoint;
		char[] chars = str.toCharArray();
		for (int i=0; i < len; i++)  {
			codePoint = chars[i];
			// ASCII Printable characters are between 32 to 126 (included)
			if (codePoint >= 127 || codePoint < 32) { 
				return false; 
			}
		}
		return true; //All are ascii
    }
}

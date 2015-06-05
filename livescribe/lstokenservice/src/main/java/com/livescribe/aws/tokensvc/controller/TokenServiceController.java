/*
 * Created:  Sep 15, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
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
import com.livescribe.afp.PenID;
import com.livescribe.aws.tokensvc.auth.DigestAuthentication;
import com.livescribe.aws.tokensvc.config.AppConstants;
import com.livescribe.aws.tokensvc.config.AppProperties;
import com.livescribe.aws.tokensvc.dto.PenDTO;
import com.livescribe.aws.tokensvc.dto.RegistrationState;
import com.livescribe.aws.tokensvc.dto.TemporaryCredentialsDTO;
import com.livescribe.aws.tokensvc.exception.DeviceAlreadyRegisteredException;
import com.livescribe.aws.tokensvc.exception.DeviceNotFoundException;
import com.livescribe.aws.tokensvc.exception.DuplicateEmailAddressException;
import com.livescribe.aws.tokensvc.exception.DuplicateSerialNumberException;
import com.livescribe.aws.tokensvc.exception.InvalidParameterException;
import com.livescribe.aws.tokensvc.exception.LSDSClientException;
import com.livescribe.aws.tokensvc.exception.MultipleRegistrationsFoundException;
import com.livescribe.aws.tokensvc.exception.RegistrationNotFoundException;
import com.livescribe.aws.tokensvc.exception.UserNotFoundException;
import com.livescribe.aws.tokensvc.response.CredentialResponse;
import com.livescribe.aws.tokensvc.response.ErrorResponse;
import com.livescribe.aws.tokensvc.response.PenListResponse;
import com.livescribe.aws.tokensvc.response.RegistrationStatus;
import com.livescribe.aws.tokensvc.response.RegistrationStatusResponse;
import com.livescribe.aws.tokensvc.response.ResponseCode;
import com.livescribe.aws.tokensvc.response.ServiceResponse;
import com.livescribe.aws.tokensvc.response.UnregistrationResponse;
import com.livescribe.aws.tokensvc.service.ManufacturingService;
import com.livescribe.aws.tokensvc.service.RegistrationService;
import com.livescribe.aws.tokensvc.service.TokenService;
import com.livescribe.aws.tokensvc.service.UserService;
import com.livescribe.aws.tokensvc.util.PenId;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.framework.web.validation.EmailValidator;
import com.livescribe.web.lssettingsservice.client.exception.InvalidSettingDataException;
import com.livescribe.web.lssettingsservice.client.exception.LSSettingsServiceConnectionException;
import com.livescribe.web.lssettingsservice.client.exception.NoRegisteredDeviceFoundException;
import com.livescribe.web.lssettingsservice.client.exception.NoSettingFoundException;
import com.livescribe.web.lssettingsservice.client.exception.ProcessingErrorException;

/**
 * <p>Entry point for HTTP client requests.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Controller
public class TokenServiceController implements AppConstants {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static String VIEW_ERROR							= "errorView";
	private static String VIEW_REGISTRATION						= "registrationView";
	private static String VIEW_XML_RESPONSE						= "xmlResponseView";
	private static String PURPOSE_DATA_METRICS					= "metrics";
	private static String PURPOSE_USER							= "user";
	private static String PURPOSE_DUMP							= "dump";
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private EmailValidator emailValidator;
	
	@Autowired
	private ManufacturingService manufacturingService;
	
	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * <p></p>
	 * 
	 */
	public TokenServiceController() {
		
		//GSK 10/29/12 - important set a hook for shutdown, otherwise there is be a thread resource leak
		// The current framework does not provide a end of Tomcat notification to the application
		logger.info("TokenServiceController(): Created");
	}

	/**
	 * <p>When the token service is being shut down then this api should be called to cleanup</p>
	 * 
	 */
	@PreDestroy
	public void windUp()
	{
		logger.info("windUp(): Destroyed");
		DigestAuthentication.stop();
	}

	/**
	 * <p>Completes the registration of a device.</p>
	 * 
	 * @param serialNumber The numeric serial number of the calling device.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/registration/complete")
	public ModelAndView complete(@RequestParam("deviceId") String serialNumber) {
		String method = "complete():  ";
		
		ModelAndView mv = new ModelAndView();
		ServiceResponse response;
		
		// Check for null or empty parameter
		if (serialNumber == null || serialNumber.isEmpty()) {
			String msg = "No pen serial number was provided.";
			logger.error(msg);
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		// Validate if serialNumber is valid
		//Pen pen = null;
		try {
			/*pen =*/ manufacturingService.lookupDevice(serialNumber);
			
		} catch (DuplicateSerialNumberException dsne) {
			logger.error(method + "More than one device found in database with deviceId = '" + serialNumber + "'.",dsne);
			response = new ErrorResponse(ResponseCode.DUPLICATE_SERIAL_NUMBER_FOUND, dsne.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (DeviceNotFoundException dnfe) {
			logger.error(method + "No device found in database with deviceId = '" + serialNumber + "'.",dnfe);
			response = new ErrorResponse(ResponseCode.DEVICE_NOT_FOUND, dnfe.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		try {
			registrationService.complete(serialNumber);
			
		} catch (RegistrationNotFoundException e) {
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (MultipleRegistrationsFoundException e) {
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.DUPLICATE_REGISTRATION_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (DeviceAlreadyRegisteredException e) {
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.DEVICE_ALREADY_REGISTERED, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (XmlRpcException e) {
			logger.error(method + "Error when connecting to LSDesktopServer to send email.");
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, "Error when connecting to LSDesktopServer to send email.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (LSDSClientException e) {
			logger.error(method + "Error when connecting to LSDesktopServer to send email.");
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, "Error when connecting to LSDesktopServer to send email.");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (UserNotFoundException e) {
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_REGISTRATION);
		mv.addObject("response", response);
		
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param deviceId
	 * 
	 * @return
	 */
	@RequestMapping(value = "/registration/status/{deviceId}")
	public ModelAndView getRegistrationStatus(@PathVariable("deviceId") String deviceId) {
		
		String method = "getRegistrationStatus(): ";
		
		ModelAndView mv = new ModelAndView();
		
		//==============================
		//	Check for 'null' or empty parameters.
		//==============================
		if ((deviceId == null) || ("".equals(deviceId))) {
			String msg = "No device ID was provided.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		//==============================
		//	Look up device.
		//==============================
		//Pen pen = null;
		try {
			/*pen =*/ manufacturingService.lookupDevice(deviceId);
		}
		catch (DuplicateSerialNumberException dsne) {
			logger.error(method + "More than one device found in database with deviceId = '" + deviceId + "'.",dsne);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_SERIAL_NUMBER_FOUND, dsne.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (DeviceNotFoundException dnfe) {
			logger.error(method + "No device found in database with deviceId = '" + deviceId + "'.",dnfe);
			ErrorResponse response = new ErrorResponse(ResponseCode.DEVICE_NOT_FOUND, dnfe.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		//==============================
		//	Determine registration state.
		//==============================
		RegistrationState regState = null;
		try {
			regState = registrationService.getRegistrationState(deviceId);
			logger.debug(method + "registration state:  " + regState.toString());
		}
		catch (MultipleRegistrationsFoundException mrfe) {
			logger.error(method + mrfe.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_REGISTRATION_FOUND, mrfe.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		RegistrationStatusResponse response = new RegistrationStatusResponse(ResponseCode.SUCCESS);
		if (!regState.isPenRegistrationStarted()) {
			response.setStatus(RegistrationStatus.UNREGISTERED);
			
		} else if ((regState.isPenRegistrationStarted()) && (!regState.isPenRegistrationComplete())) {
			response.setStatus(RegistrationStatus.IN_PROGRESS);
			
		} else if (regState.isPenRegistrationComplete()) {
			response.setStatus(RegistrationStatus.COMPLETE);
			
		} else {
			logger.error(method + "An error occurred when attempting to determine registration status for device '" + deviceId + "'.");
			ErrorResponse errorResponse = new ErrorResponse(ResponseCode.SERVER_ERROR);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", errorResponse);
			return mv;
		}
		
		// Find email used to register with the device
		if (response.getStatus() == RegistrationStatus.IN_PROGRESS || response.getStatus() == RegistrationStatus.COMPLETE) {
			try {
				User user = registrationService.findUserRegisteredWithDevice(deviceId);
				response.setEmail(user.getPrimaryEmail());
				
			} catch (MultipleRegistrationsFoundException e) {
				logger.error(method + e.getMessage());
				ErrorResponse errResponse = new ErrorResponse(ResponseCode.DUPLICATE_REGISTRATION_FOUND, e.getMessage());
				mv.setViewName(VIEW_ERROR);
				mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", errResponse);
				return mv;
			} catch (RegistrationNotFoundException e) {
				logger.error(method + e.getMessage());
				ErrorResponse errResponse = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, e.getMessage());
				mv.setViewName(VIEW_ERROR);
				mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", errResponse);
				return mv;
			} catch (UserNotFoundException e) {
				logger.error(method + e.getMessage());
				ErrorResponse errResponse = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
				mv.setViewName(VIEW_ERROR);
				mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", errResponse);
				return mv;
			}
		}
		
		mv.setViewName(VIEW_REGISTRATION);
		mv.addObject("response", response);
		return mv;
	}
	
	//@RequestMapping(value = "/newtoken/{purpose}", method = RequestMethod.POST)
	public ModelAndView getAwsCredentialsWithDeviceId(@RequestParam("deviceId") String deviceId, @PathVariable String purpose) {
		
		String method = "getAwsCredentials(): ";
		
		logger.debug(method + "------------------------- S T A R T -------------------------");
		
		ModelAndView mv = new ModelAndView();
		
		//==============================
		//	Check for 'null' or empty parameters.
		//==============================
		if ((deviceId == null) || ("".equals(deviceId))) {
			String msg = "No 'deviceId' was provided.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		if ((purpose == null) || ("".equals(purpose))) {
			String msg = "No 'purpose' was provided.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		
		boolean isValidPen = manufacturingService.isValidDeviceId(deviceId);
		
		if (!isValidPen) {
			String msg = "The device ID provided was invalid.  [" + deviceId + "]";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		TemporaryCredentialsDTO tempCredentials = null;

		//	Get keys to the Data Metrics SQS queue.
		if (PURPOSE_DATA_METRICS.equals(purpose)) {
			tempCredentials = tokenService.getCredentialsForDataMetrics();
		}
		//	Get keys to the user's information on S3 and SQS.
		else if (PURPOSE_USER.equals(purpose)) {
//			tempCredentials = tokenService.getCredentialsForUser(regToken);
			tempCredentials = null;
		}
		else if (PURPOSE_DUMP.equals(purpose)) {
//			tempCredentials = tokenService.getCredentialsForPortalCapture(regToken);
			tempCredentials = tokenService.getCredentialsForPortalCaptureWithDeviceId(deviceId);
		}
		else {
			//	TODO:  Implement.
		}
		
		logger.debug(method + tempCredentials.toString());
		mv.setViewName("xmlResponseView");
		CredentialResponse credResponse = new CredentialResponse(tempCredentials);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", credResponse);
		
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param guidStr The registration token previously assigned to the requesting
	 * device.
	 * 
	 * @return 
	 */
	@RequestMapping(value = "/newtoken/{purpose}", method = RequestMethod.POST)
	public ModelAndView getAwsCredentials(@RequestParam("regToken") String regToken, @PathVariable String purpose) {
		
		String method = "getAwsCredentials(): ";
		
		logger.debug(method + "------------------------- S T A R T -------------------------");
		
		ModelAndView mv = new ModelAndView();
		
		//==============================
		//	Check for 'null' or empty parameters.
		//==============================
		if ((purpose == null) || ("".equals(purpose))) {
			String msg = "No 'purpose' was provided.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		TemporaryCredentialsDTO tempCredentials = null;

		//	Get keys to the Data Metrics SQS queue.
		if (PURPOSE_DATA_METRICS.equals(purpose)) {
			tempCredentials = tokenService.getCredentialsForDataMetrics();
		}
		//	Get keys to the user's information on S3 and SQS.
		else if (PURPOSE_USER.equals(purpose)) {
			tempCredentials = tokenService.getCredentialsForUser(regToken);
		}
		else if (PURPOSE_DUMP.equals(purpose)) {
			tempCredentials = tokenService.getCredentialsForPortalCapture(regToken);
		}
		else {
			//	TODO:  Implement.
		}
		
		logger.debug(method + tempCredentials.toString());
		mv.setViewName("xmlResponseView");
		CredentialResponse credResponse = new CredentialResponse(tempCredentials);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", credResponse);
		
		return mv;
	}

	/**
	 * <p>Registers a device, identified by the given code, to the user whose 
	 * login token is provided in the HTTP cookie.</p>
	 * 
	 * @param code The 8-character code displayed by the pen to the user.
	 * @param penName A name provided by the user.
	 * @param request The HTTP request containing the <code>tk</code> HTTP
	 * cookie with the login token. 
	 * 
	 * @return SUCCESS, if the registration was successful.
	 */
	@RequestMapping(value = "/register.xml", method = RequestMethod.POST)
	public ModelAndView registerDeviceWithCode(@RequestParam("code") String code, @RequestParam("penName") String penName, HttpServletRequest request) {
		
		//	TODO:  Replace HttpServletRequest with parameter below.
//		@CookieValue("tk") String loginToken
		
		String method = "registerDeviceWithCode():  ";

		ModelAndView mv = new ModelAndView();
		ServiceResponse response;
		
		//	Convert 8-char code to pen display id
		String serialNumber = "";
		try {
			logger.debug("code:  " + code);
			String displayId = PenId.convert8CharCodeToPenId(code);
			logger.debug("display ID:  " + displayId);
			serialNumber = String.valueOf(PenId.toId(displayId));
			
		} catch (IllegalArgumentException iae) {
			String msg = "Invalid or bad format Pen ID.";
			logger.error(method + iae.getMessage(), iae);
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		//	Validate serial number
		Pen pen = null;
		try {
			pen = manufacturingService.lookupDevice(serialNumber);
			
		} catch (DuplicateSerialNumberException dsne) {
			String msg = "More than one device found in database with deviceId = '" + serialNumber + "'.";
			logger.error(method + msg,dsne);
			response = new ErrorResponse(ResponseCode.DUPLICATE_SERIAL_NUMBER_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (DeviceNotFoundException dnfe) {
			String msg = "Smartpen with deviceId = '" + serialNumber + "' is not found in the system.";
			logger.error(method + msg,dnfe);
			response = new ErrorResponse(ResponseCode.DEVICE_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Validate pen name
		if (penName == null || penName.length() > 40 || penName.length() == 0) {
			String msg = "Invalid pen name. Pen name cannot be empty and must not exceed 40 characters.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Get login_token from Http Cookies
		Cookie cookies[] = request.getCookies();
		if (cookies == null) {
			String msg = "User is not logged in.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "errResponse", response);
			return mv;
		}
		
		String loginToken = null;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("tk")) {
				loginToken = cookie.getValue();
				break;
			}
		}
		
		//	If no login token is found, return a USER_NOT_LOGGED_IN error.
		if ((loginToken == null) || ("".equals(loginToken))) {
			String msg = "User is not logged in.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "errResponse", response);
			return mv;
		}
		
		//	Find logged in user email by login_token
		User user = null;
		try {
			//	TODO:  Create 'findByLoginToken()' method.
//			String loggedInEmail = userService.getLoggedInUserEmail(loginToken, "WEB");
			user = userService.findUserByLoginToken(loginToken);
//			user = userService.findByEmail(loggedInEmail);
			
//		} catch (UserNotFoundException e) {
//			String msg = "The user does not exist in the system.";
//			logger.error(method + e.getMessage());
//			response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, msg);
//			mv.setViewName(VIEW_ERROR);
//			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
//			return mv;
//		} catch (DuplicateEmailAddressException e) {
//			String msg = "The user already exists in the system.";
//			logger.error(method + e.getMessage());
//			response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, msg);
//			mv.setViewName(VIEW_ERROR);
//			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
//			return mv;
//		} catch (IOException e) {
//			String msg = "There's error occured in when calling LSLoginService.";
//			logger.error(method + e.getMessage());
//			response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
//			mv.setViewName(VIEW_ERROR);
//			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
//			return mv;
//			
//		} catch (com.livescribe.framework.exception.InvalidParameterException e) {
//			logger.error(method + e.getMessage());
//			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, e.getMessage());
//			mv.setViewName(VIEW_ERROR);
//			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
//			return mv;
//			
		} catch (UserNotLoggedInException e) {
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
//		} catch (ClientException e) {
//			logger.error(method + e.getMessage());
//			response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
//			mv.setViewName(VIEW_ERROR);
//			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
//			return mv;
//		}
		
		// register device
		try {
			/*RegisteredDevice regDev =*/ registrationService.registerDevice(pen, user);
			
		} catch (DeviceAlreadyRegisteredException dare) {
			String msg = "Smartpen is already registered.";
			logger.error(method + dare);
			response = new ErrorResponse(ResponseCode.DEVICE_ALREADY_REGISTERED, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (InvalidParameterException ipe) {
			String msg = "Missing required parameter.";
			logger.error(method + ipe);
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Save pen name into device_setting table.
		try {
			registrationService.savePenNameInDeviceSettings(penName, pen.getSerialnumber());
			
		} catch (InvalidSettingDataException e) {
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.INVALID_SETTING_DATA, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (LSSettingsServiceConnectionException e) {
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (NoSettingFoundException e) {
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.NO_SETTING_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (NoRegisteredDeviceFoundException e) {
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (ProcessingErrorException e) {
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (IOException e) {
			logger.error(method + e.getMessage());
			response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML_RESPONSE);
		mv.addObject("response", response);

		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param regToken
	 * @param loginDomain
	 * 
	 * @return
	 */
//	@RequestMapping(value = "/resetByEmail.xml")
	public ModelAndView resetByEmail(@RequestParam("e") String email) {
		
		String method = "resetByEmail():  ";
		
		logger.debug(method + "------------------------- S T A R T -------------------------");

		//	TODO:  Secure this method to be available in-house ONLY!!!
		
		ModelAndView mv = new ModelAndView();
		
		//==============================
		//	Check for 'null' or empty parameters.
		//==============================
		if ((email == null) || ("".equals(email))) {
			String msg = "No email address was provided.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
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
			registrationService.resetByEmail(email);
			
		}
		catch (UserNotFoundException unfe) {
			String msg = "UserNotFoundException thrown when attempting to reset database with email '" + email + ".";
			logger.debug(msg, unfe);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (RegistrationNotFoundException rnfe) {
			String msg = "RegistrationNotFoundException thrown when attempting to reset database with email '" + email + ".";
			logger.debug(msg, rnfe);
			ErrorResponse response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (DuplicateEmailAddressException deae) {
			deae.printStackTrace();
			logger.error(method + deae.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, deae.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML_RESPONSE);
		mv.addObject("response", response);
		return mv;
	}
	
	/**
	 * <p>Unregisters a device identified by the given device ID.</p>
	 * 
	 * @param deviceId The unique ID of the device being unregistered.
	 * 
	 * @return An XML response with a <code>ResponseCode<code> of SUCCESS. 
	 */
	@RequestMapping(value = "/unregister/{deviceId}", method = RequestMethod.POST)
	public ModelAndView unregister(@PathVariable("deviceId") String deviceId) {
		
		String method = "unregisterDevice():  ";
		
		ModelAndView mv = new ModelAndView();
		ServiceResponse response = null;
		
		//==============================
		//	Check for 'null' or empty parameters.
		//==============================
		if ((deviceId == null) || ("".equals(deviceId))) {
			String msg = "No device ID was provided.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//boolean success = false;
		try {
			/*success =*/ registrationService.unregister(deviceId);
		}
		catch (RegistrationNotFoundException rnfe) {
			/* 
			 * Per pen team requirement: when trying to deregister an unregistered pen, we will treat this case as SUCCESS.
			 * So we will not return error code in this case. 
			*/ 
			logger.info("Deregistering pen=" + deviceId + " which was not yet registered.");
			
//			String msg = "No registration was found.";
//			logger.error(method + msg);
//			response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, msg);
//			mv.setViewName(VIEW_ERROR);
//			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
//			return mv;
		}
		catch (MultipleRegistrationsFoundException mrfe) {
			String msg = "Multiple registrations were found for pen with serial number '" + deviceId + "'.";
			logger.error(method + msg);
			response = new ErrorResponse(ResponseCode.DUPLICATE_REGISTRATION_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		response = new UnregistrationResponse(ResponseCode.SUCCESS);
		mv.setViewName("xmlResponseView");
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
		return mv;
	}
	
	/**
	 * <p>This api would provide the pen device to get the current nonce that canbe used to build authentication response.</p>
	 * 
	 * @param deviceId
	 * 
	 * @return nonce as a string in the message response
	 * 
	 * @author Gurmeet Kalra
	 */
	@RequestMapping(value = "/auth/getNonce/{deviceId}")
	public ModelAndView getAuthCurrentNonce(@PathVariable("deviceId") String deviceId, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		
		String method = "getAuthCurrentNonce(): ";

		//cheating to have jsessionid cookie on response
		httpRequest.getSession().getServletContext();
		
		ModelAndView mv = new ModelAndView();
		
		//==============================
		//	Check for 'null' or empty parameters.
		//==============================
		if ((deviceId == null) || ("".equals(deviceId))) {
			String msg = "No device ID was provided.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		// ensure to have numeric device id.
		String numericDeviceId = convertToNumericId(deviceId);

		//==============================
		//	Look up device.
		//  This is optional for now as we are not taking any specific action
		//  related to the device id that was send to us, we simply accepting
		//  based if it it is be present in our database.
		//  Later we may connect the nonce send to the devie id provided.
		//  For now to speed up we will disable even the validation of the pen id.
		//==============================
		Pen pen = null;
		try {
			pen = manufacturingService.lookupDevice(numericDeviceId);
			logger.info(method + "PEN DATA deviceId = '" + pen.getDisplayId() + ", pen=" + pen.getKeyTransport());
		}
		catch (DuplicateSerialNumberException dsne) {
			logger.error(method + "More than one device found in database with deviceId = '" + deviceId + "'.",dsne);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_SERIAL_NUMBER_FOUND, dsne.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (DeviceNotFoundException dnfe) {
			logger.error(method + "No device found in database with deviceId = '" + deviceId + "'.",dnfe);
			ErrorResponse response = new ErrorResponse(ResponseCode.DEVICE_NOT_FOUND, dnfe.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (Exception ex) {
			// This could be ignored, but should be recorded in the log
			logger.info(method + "lookupDevice FAILED msg=" + ex.getMessage());
		}

		//==============================
		//	Get the current nonce in a 
		//  authentication data text for
		//  adding in the header
		//==============================
		String headerField = "";
		try {
			DigestAuthentication auth = new DigestAuthentication();
			headerField = auth.getAuthDataHeader();

			// add the www-authenticate name value token to the header
			httpResponse.addHeader(DigestAuthentication.HEADER_AUTHENTICATE, headerField);
			
			// success
			ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
			//mv.addObject(DigestAuthentication.HEADER_AUTHENTICATE, headerField);
			mv.setViewName(VIEW_XML_RESPONSE);
			mv.addObject("response", response);
		}
		catch (Exception ex) {
			logger.error(method + ex.getMessage());
			ErrorResponse errResponse = new ErrorResponse(ResponseCode.FAILED_TO_PROVIDE_NONCE_DATA, ex.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", errResponse);
			return mv;
		}
		
		return mv;
	}

	/**
	 * <p>This api would validate the authorization info send by the pen device with the message.</p>
	 * 
	 * @param deviceId
	 * 
	 * @return HTTP_OK or HTTP_UNAUTHORIZED return codes in the message response
	 * 
	 * @author Gurmeet Kalra
	 */
	@RequestMapping(value = "/auth/validateDigest/{deviceId}")
	public ModelAndView validateDigest(@PathVariable("deviceId") String deviceId, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		
		final String METHOD_NAME = "validateDigest(): ";
		
		ModelAndView mv = new ModelAndView();
		
		//==============================
		//	Check for 'null' or empty parameters.
		//==============================
		if ((deviceId == null) || ("".equals(deviceId))) {
			String msg = "No device ID was provided.";
			logger.error(METHOD_NAME + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		// ensure to have numeric device id.
		String numericDeviceId = convertToNumericId(deviceId);

		//==============================
		//	Look up device.
		//  This is optional for now as we are not taking any specific action
		//  related to the device id that was send to us, we simply accepting
		//  based if it it is be present in our database.
		//  Later we may connect the nonce send to the devie id provided.
		//  For now to speed up we will disable even the validation of the pen id.
		//==============================
		Pen pen = null;
		try {
			pen = manufacturingService.lookupDevice(numericDeviceId);
			//logger.info(METHOD_NAME + "PEN DATA deviceId = '" + pen.getDisplayId() + ", pen=" + pen.getKeyTransport());
		}
		catch (DuplicateSerialNumberException dsne) {
			logger.error(METHOD_NAME + "More than one device found in database with deviceId = '" + deviceId + "'.",dsne);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_SERIAL_NUMBER_FOUND, dsne.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (DeviceNotFoundException dnfe) {
			logger.error(METHOD_NAME + "No device found in database with deviceId = '" + deviceId + "'.",dnfe);
			ErrorResponse response = new ErrorResponse(ResponseCode.DEVICE_NOT_FOUND, dnfe.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		//==============================
		//	Get the current nonce in a 
		//  authentication data text for
		//  adding in the header
		//==============================
		try {
			
			DigestAuthentication auth = new DigestAuthentication();
			if (auth.authenticate(httpRequest, httpResponse, deviceId)) {  //TODO REPLACE numericDeviceId with pen.getKeyTransport()*/

				// success
				ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
				mv.setViewName(VIEW_XML_RESPONSE);
				mv.addObject("response", response);

			} else {
				
				// Failure, hence provide the new auth header data 
				// Already was set inside the 'authenticate' function
				//String headerField = auth.getAuthDataHeader();
				// add the www-authenticate name value token to the header
				//httpResponse.addHeader(DigestAuthentication.HEADER_AUTHENTICATE, headerField);

				ServiceResponse response = new ServiceResponse(ResponseCode.INVALID_AUTH_DATA);
				mv.setViewName(VIEW_XML_RESPONSE);
				mv.addObject("response", response);

			}
		}
		catch (Exception ex) {
			logger.error(METHOD_NAME + ex.getMessage());
			ErrorResponse errResponse = new ErrorResponse(ResponseCode.FAILED_TO_PROVIDE_NONCE_DATA, ex.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", errResponse);
			return mv;
		}
		
		return mv;
	}

	/**
	 * <p>This method will return a list of registered pens a user owns</p>
	 * 
	 * @param loginToken A login token provided in the cookie of the HTTP request
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getUserPenList.xml")
	public ModelAndView getUserPenList(@CookieValue("tk") String loginToken) {
		
		String method = "getUserPenList():  ";
		ModelAndView mv = new ModelAndView();
		
		if ((loginToken == null) || (loginToken.isEmpty())) {
			String msg = "User is not logged in.";
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		List<PenDTO> pens = null;
		try {
			pens = registrationService.getWiFiPenList(loginToken);
			
		} catch (UserNotLoggedInException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (UserNotFoundException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (DeviceNotFoundException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.DEVICE_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (DuplicateSerialNumberException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_SERIAL_NUMBER_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (IOException e) {
			String msg = "There is error occurs while creating LSSettingsService Client object.";
			logger.error(method + msg, e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (InvalidSettingDataException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_SETTING_DATA, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (LSSettingsServiceConnectionException e) {
			String msg = "There is error occurs when making service call to LSSettingsService.";
			logger.error(method + msg, e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (NoSettingFoundException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.NO_SETTING_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (NoRegisteredDeviceFoundException e) {
			String msg = "LSSettingsService could not find the specified device.";
			logger.error(method + msg, e);
			ErrorResponse response = new ErrorResponse(ResponseCode.DEVICE_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (ProcessingErrorException e) {
			String msg = "LSSettingsService cannot process settings.";
			logger.error(method + msg, e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		PenListResponse response = new PenListResponse(ResponseCode.SUCCESS);
		response.setPens(pens);
		
		mv.setViewName(VIEW_XML_RESPONSE);
		mv.addObject("response", response);
		return mv;
	}
	
	@RequestMapping(value = "/saveSetting.xml", method = RequestMethod.POST)
	public ModelAndView savePenName(
			@CookieValue("tk") String loginToken,
			@RequestParam("namespace") String namespace,
			@RequestParam("name") String name,
			@RequestParam("displayId") String displayId,
			@RequestParam("type") String type,
			@RequestParam("value") String value) {
		
		String method = "savePenName():  ";
		ModelAndView mv = new ModelAndView();
		
		// Validate loginToken cookie
		if ((loginToken == null) || (loginToken.isEmpty())) {
			String msg = "User is not logged in.";
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		// Validate namespace
		if (namespace == null || namespace.isEmpty()) {
			String msg = "No 'namespace' was provided.";
			logger.error(msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		// Validate name
		if (name == null || name.isEmpty()) {
			String msg = "No 'name' was provided.";
			logger.error(msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		// Validate displayId
		if (displayId == null || displayId.isEmpty()) {
			String msg = "No 'displayId' was provided.";
			logger.error(msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		// Validate type
		if (type == null || type.isEmpty()) {
			String msg = "No 'type' was provided.";
			logger.error(msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		// Validate value
		if (value == null || value.isEmpty()) {
			String msg = "No 'value' was provided.";
			logger.error(msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		try {
			registrationService.saveDeviceSettings(loginToken, displayId, namespace, name, value, type);
			
		} catch (UserNotLoggedInException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (RegistrationNotFoundException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (DuplicateSerialNumberException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_SERIAL_NUMBER_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (DeviceNotFoundException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.DEVICE_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (IOException e) {
			logger.error(method + "There is error occured when calling to LSSettingsService", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (InvalidSettingDataException e) {
			logger.error(method + e.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_SETTING_DATA, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (LSSettingsServiceConnectionException e) {
			logger.error(method + e.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (NoSettingFoundException e) {
			logger.error(method + e.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.NO_SETTING_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (NoRegisteredDeviceFoundException e) {
			logger.error(method + e.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (ProcessingErrorException e) {
			logger.error(method + e.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		}
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML_RESPONSE);
		mv.addObject("response", response);
		return mv;
	}
	
	private String convertToNumericId(String deviceId) {

		// first test is we already have numeric device Id i.e. only digit in the string, then we take it as is 
		String numericDeviceId = deviceId;
		if (!(deviceId.matches("[0-9]+") && deviceId.length() > 4)) {
			try {
				PenID penid = new PenID(deviceId);
				numericDeviceId = Long.toString(penid.getId());
			} catch (AFPException e) {
				e.printStackTrace();
			}
			
		}
		
		return numericDeviceId;
	}

}

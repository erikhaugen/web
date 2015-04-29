package com.livescribe.aws.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.evernote.edam.type.PremiumInfo;
import com.livescribe.afp.AFPException;
import com.livescribe.afp.PenID;
import com.livescribe.aws.login.dto.AuthorizationDto;
import com.livescribe.aws.login.response.AuthorizationListResponse;
import com.livescribe.aws.login.response.AuthorizationResponse;
import com.livescribe.aws.login.util.AuthorizationType;
import com.livescribe.aws.login.util.Utils;
import com.livescribe.framework.config.AppProperties;
import com.livescribe.framework.exception.DuplicateEmailAddressException;
import com.livescribe.framework.exception.ServerException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.login.exception.AuthorizationException;
import com.livescribe.framework.login.exception.AuthorizationExpiredException;
import com.livescribe.framework.login.exception.AuthorizationNotFoundException;
import com.livescribe.framework.login.exception.RegistrationNotFoundException;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.login.service.LoginService;
import com.livescribe.framework.oauth.service.AuthorizationService;
import com.livescribe.framework.oauth.service.EvernoteUserService;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.lsshareapi.service.ShareService;

@Controller(value = "AuthorizationController")
public class AuthorizationController {

	private static String VIEW_ERROR		= "errorView";
	private static String VIEW_XML			= "xmlResponseView";
	
	private static final String WEB = "WEB";
	private static final String EVERNOTE = "EN";
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private AuthorizationService authorizationService;
	
	@Autowired
	private EvernoteUserService evernoteUserService;
	
	@Autowired
	private ShareService shareService;
	
	@Autowired
	private AppProperties appProperties;
	
	/**
	 * Default constructor
	 */
	public AuthorizationController() {
		
	}
	
	@RequestMapping(value="/saveOAuthToken.xml", method=RequestMethod.GET)
	public ModelAndView saveAccessToken(HttpServletRequest request,
			@CookieValue("tk") String loginToken,
			@RequestParam("accessToken") String accessToken,
			@RequestParam(value = "enShardId", required = false) String enShardId,
			@RequestParam("provider") String provider,
			@RequestParam(value = "expirationDate", required = false) String expirationDate,
			@RequestParam("redirectUrl") String redirectUrl,
			@RequestParam(value = "reAuthorized", required = false) Boolean reAuthorized) {

		String method = "saveAccessToken():  ";
		logger.debug(method + "called with loginToken: " + loginToken + ", provider: " + provider + ", and expiration date: " + expirationDate);
		ModelAndView mv = new ModelAndView();
		
		if (accessToken == null || accessToken.isEmpty()) {
			String msg = "Missing 'accessToken' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (provider == null || provider.isEmpty()) {
			String msg = "Missing 'provider' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} 
		
		if (redirectUrl == null || redirectUrl.isEmpty()) {
			String msg = "Missing 'redirectUrl' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (!isValidOAuthProvider(provider)) {
			String msg = "Unsupported provider: " + provider + ".";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		// Find user by the loginToken
		User user = loginService.getUserInfoByLoginToken(loginToken, WEB);
		
		// if user is not logged in, redirect to login page
		//	TODO:  Need to make this configurable.
		if (user == null) {
			return new ModelAndView(new RedirectView("login.htm"));
		}
		
		// Check if the authorization provider is Evernote
		if (provider.equalsIgnoreCase(EVERNOTE)) {
			
			// parse expiration date
			Date expDate = getOAuthExpirationDate(expirationDate);

			try {
				// get Evernote user info
				logger.debug("Retrieving Evernote User info..");
				com.evernote.edam.type.User evernoteUser = evernoteUserService.getCurrentUser(accessToken);
				
				// call service to save (and/or update) evernote authorizations
				logger.debug("Calling authorizationService to save/update auth in the database..");
				authorizationService.saveEvernoteAuthorization(user, evernoteUser, accessToken, expDate);
				logger.debug("*************** saveEvernoteAuthorization completed successfully..");
			} catch (UserNotFoundException e) {
				logger.error(method + e.getMessage(), e);
				ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
				mv.setViewName(VIEW_ERROR);
				mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
				return mv;
			} catch (DuplicateEmailAddressException e) {
				logger.error(method + e.getMessage(), e);
				ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, e.getMessage());
				mv.setViewName(VIEW_ERROR);
				mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
				return mv;
			} catch (Exception e) {
				logger.error(method + e.getMessage(), e);
				ErrorResponse response = new ErrorResponse(ResponseCode.UNAUTHORIZED, e.getMessage());
				mv.setViewName(VIEW_ERROR);
				mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
				return mv;
			}
		} else {
			// TO DO Handle for any other specific providers..
			logger.warn("Encountered an unsupported provider: " + provider);
			ErrorResponse response = new ErrorResponse(ResponseCode.NOT_IMPLEMENTED, "Encountered an unsupported provider: " + provider);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		return new ModelAndView(new RedirectView(redirectUrl));
	}
	
	/**
	 * <p>Returns a list of Authorizations (AuthorizationListReponse) for the given providerUser and providerType.</p>
	 * 
	 * @param enId providerUserId
	 * @param provider The 2-character code representing the provider.  (e.g.  &quot;EN&quot;)
	 * 
	 * @return list of matching authorization(s)
	 */
	@RequestMapping(value="/findAuthorizationByProviderUserId.xml", method=RequestMethod.POST)
	public ModelAndView findAuthorizationByProviderUserId(@RequestParam("userId") String enId, @RequestParam("authType") String provider) {
		
		String method = "findAuthorizationByProviderUserId(" + enId + ", " + provider + "):	" ;
		logger.info("BEGIN - " + method);
		long start = System.currentTimeMillis();
		
		ModelAndView mv = new ModelAndView();
		
		//	Validating userId param
		if (enId == null || enId.isEmpty()) {
			String msg = "Missing 'enId' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Validating enId format
		try {
			Integer.parseInt(enId);
		} catch (NumberFormatException nfe) {
			String msg = "Invalid userId format.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Validating provider param
		if (provider == null || provider.isEmpty()) {
			String msg = "Missing 'provider' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Validating if the specified provider is valid
		AuthorizationType authType = AuthorizationType.fromString(provider.toUpperCase());
		if (authType == null) {
			String msg = "The provided provider type [" + provider.toUpperCase() + "] is invalid.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		logger.debug(method + "calling authorizationService to findAuthorizationList..");
		List<AuthorizationDto> authorizationDtoList = authorizationService.findAuthorizationListByProviderUserId(enId, authType);
		if (null == authorizationDtoList || authorizationDtoList.isEmpty()) {
			String msg = "No Authorization found with provider userId " + enId + " and provider type " + provider.toUpperCase() + ".";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		}

		logger.debug(method + "received the authList, now building the response..");
		AuthorizationListResponse authResponseList = new AuthorizationListResponse(ResponseCode.SUCCESS, authorizationDtoList);
		
		mv.setViewName(VIEW_XML);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", authResponseList);
		
		long end = System.currentTimeMillis();
		long duration = end - start;
		logger.info("AFTER - " + method + " - Completed in " + duration + " milliseconds.");

		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param request
	 * @param penDisplayId
	 * @param provider The 2-character code representing the provider.  (e.g.  &quot;EN&quot;)
	 * 
	 * @return
	 */
	@RequestMapping(value="/findAuthorizationByPenDisplayId.xml", method=RequestMethod.POST)
	public ModelAndView findAuthorizationByPenDisplayId(HttpServletRequest request,
			@RequestParam("penDisplayId") String penDisplayId, 
			@RequestParam("provider") String provider) {
		
		String method = "findAuthorizationByPenDisplayId():  ";
		ModelAndView mv = new ModelAndView();
		
		// validating penDisplayId param
		if (penDisplayId == null || penDisplayId.isEmpty()) {
			String msg = "Missing 'penDisplayId' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		// validating provider param
		if (provider == null || provider.isEmpty()) {
			String msg = "Missing 'provider' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		}
		
		// validating if the specified provider is valid
		AuthorizationType authType = AuthorizationType.fromString(provider);
		if (authType == null) {
			String msg = "The provided 'provider' is invalid.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		// Convert pen display id to serial number and validate if pen display id is valid
		String serialNumber = "";
		try {
			PenID penId = new PenID(penDisplayId);
			serialNumber = String.valueOf(penId.getId());
		} catch (AFPException afpe) {
			logger.error(method + afpe.getMessage(), afpe);
			String msg = "AFPException thrown when converting pen display ID '" + penDisplayId + "' into serial number.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		Authorization auth = null;
		try {
			logger.debug(method + "Finding authorization for pen with serial number '" + serialNumber + "'.");
			auth = authorizationService.findByPenSerialNumber(serialNumber, authType);
			logger.debug(method + "Found authorization for pen with '" + serialNumber + "'.");
			logger.debug(method + "OAuth access token:  " + Utils.obfuscateOAuthToken(auth.getOauthAccessToken()));
		}
		catch (RegistrationNotFoundException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.REGISTRATION_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (AuthorizationException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.UNAUTHORIZED, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (UserNotFoundException e) {
			logger.error(method + e.getMessage(), e);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (Exception e) {
			String msg = "An unknown Exception was thrown when attempting to find Authorization by pen serial number.";
			logger.error(method + e.getMessage(), e);
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		logger.debug(method + "Constructing view ...");
		
		mv.setViewName(VIEW_XML);
		AuthorizationResponse response = new AuthorizationResponse(auth);
		response.setResponseCode(ResponseCode.SUCCESS);
		mv.addObject("response", response);
		
		logger.debug(method + response.toString());
		
		return mv;
	}
	
	/**
	 * <p>Finds the <b><u>primary</u></b> authorization for the user identified by
	 * the given UID.</p>
	 * 
	 * @param provider The 2-character code representing the provider.  (e.g.  &quot;EN&quot;)
	 * @param uid
	 * 
	 * @return
	 */
//	@RequestMapping(value = "/auth/{provider}/{uid}", method = RequestMethod.GET)
//	public ModelAndView findAuthorizationByUID(@PathVariable("provider") String provider, @PathVariable("uid") String uid) {
	
	@RequestMapping(value = "findAuthorizationByUID.xml", method = RequestMethod.POST)
	public ModelAndView findAuthorizationByUID(	@RequestParam("provider") String provider,
												@RequestParam("uid") String uid) {
		
		String method = "findAuthorizationByUID():  ";

		ModelAndView mv = new ModelAndView();

		//	Validating penDisplayId param
		if (uid == null || uid.isEmpty()) {
			String msg = "Missing 'uid' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		//	Validating provider param
		if (provider == null || provider.isEmpty()) {
			String msg = "Missing 'provider' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Validating if the specified provider is valid
		AuthorizationType authType = AuthorizationType.fromString(provider);
		if (authType == null) {
			String msg = "The provided provider is invalid.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		logger.debug(method + "Finding authorization for user with UID '" + uid + "'.");
		
		Authorization auth = null;
		try {
			auth = authorizationService.findPrimaryAuthByUId(uid, authType);
		}
		catch (UserNotFoundException unfe) {
			logger.error(method + unfe.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, unfe.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		catch (AuthorizationException ae) {
			logger.error(method + ae.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.UNAUTHORIZED, ae.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		if (auth == null) {
			String msg = "No authorization record was found for user with UID '" + uid + "'.";
			ErrorResponse response = new ErrorResponse(ResponseCode.UNAUTHORIZED, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		logger.debug(method + "Found token '" + auth + "'.");
		
		AuthorizationResponse response = new AuthorizationResponse(auth);
		response.setResponseCode(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
		
		logger.debug(method + response.toString());
		
		return mv;
	}

	/**
	 * <p>Returns a <code>List</code> of authorizations (i.e.  OAuth access tokens)
	 * for the Livescribe user identified by the given UID.</p>
	 * 
	 * @param provider The 2-character code representing the provider.  (e.g.  &quot;EN&quot;)
	 * @param uid The unique ID of the Livescribe user from the <code>consumer.user</code> table.
	 * 
	 * @return a list of authorizations (i.e.  OAuth access tokens). 
	 */
	@RequestMapping(value = "/auth/{provider}/{uid}", method = RequestMethod.GET)
	public ModelAndView findAuthorizationsByUid(@PathVariable String provider, @PathVariable String uid) {
		
		String method = "findAuthorizationsByUid()";
		
		ModelAndView mv = new ModelAndView();
		
		//	Validate all parameter values are present.
		if (uid == null || uid.isEmpty()) {
			String msg = "Missing 'uid' parameter.";
			logger.error(method + " - " + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		if (provider == null || provider.isEmpty()) {
			String msg = "Missing 'provider' parameter.";
			logger.error(method + " - " + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Verify the given 'provider' is supported.
		AuthorizationType authType = AuthorizationType.fromString(provider);
		if (authType == null) {
			String msg = "The provided 'provider' is invalid.";
			logger.error(method + " - " + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		List<AuthorizationDto> list = authorizationService.findAuthorizationsByUid(uid, AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		
		AuthorizationListResponse response = new AuthorizationListResponse(ResponseCode.SUCCESS, list);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		
		return mv;
	}
	
	/**
	 * Finds an <code>Authorization</code> record based on given uid, user id by provider and provider type.
	 * 
	 * @param provider provider tpye - such as 'EN', FB' etc.
	 * @param providerUserId id of the user by provider
	 * @param uid uid of the user in Livescribe
	 * @return Authorization matching the given parameters
	 */
	@RequestMapping(value = "findAuthorizationByUIDAndProviderUserId.xml", method = RequestMethod.POST)
	public ModelAndView findAuthorizationByUIDAndProviderUserId(@RequestParam("provider") String provider,
												@RequestParam("providerUserId") String providerUserId,
												@RequestParam("uid") String uid) {
		
		logger.debug("findAuthorizationByUIDAndProviderUserId called with provider: " + provider + ", providerUserId: " + providerUserId + ", uid: " + uid);
		
		String method = "findAuthorizationByUIDAndProviderUserId():  ";
		logger.info("BEGIN - " + method);
		long start = System.currentTimeMillis();

		ModelAndView mv = new ModelAndView();

		//	Validating uid param
		if (uid == null || uid.isEmpty()) {
			String msg = "Missing 'uid' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
    
		//	Validating providerUserId param
		if (providerUserId == null || providerUserId.isEmpty()) {
			String msg = "Missing 'providerUserId' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Validating provider param
		if (provider == null || provider.isEmpty()) {
			String msg = "Missing 'provider' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Validating if the specified provider is valid
		AuthorizationType authType = AuthorizationType.fromString(provider);
		if (authType == null) {
			String msg = "The provided provider is invalid.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		Authorization auth = null;
		try {
			Long providerUserIdLong = Long.parseLong(providerUserId);
			auth = authorizationService.findAuthByUIDAndProviderUserId(uid, providerUserIdLong, authType);
		} catch (NumberFormatException nfe) {
			String msg = "'providerUserId' is not valid - It must be a number!";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (UserNotFoundException unfe) {
			logger.error(method + unfe.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, unfe.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (AuthorizationException ae) {
			logger.error(method + ae.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.UNAUTHORIZED, ae.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (ServerException se) {
			logger.error(method + se.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, se.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		if (auth == null) {
			String msg = "No authorization record was found for given parameters.";
			ErrorResponse response = new ErrorResponse(ResponseCode.UNAUTHORIZED, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		logger.debug(method + "Found authorization token '" + auth + "'.");
		
		AuthorizationResponse response = new AuthorizationResponse(auth);
		response.setResponseCode(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
		
		logger.debug(method + response.toString());
		
		long end = System.currentTimeMillis();
		long duration = end - start;
		logger.info("AFTER - " + method + " - Completed in " + duration + " milliseconds.");

		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param loginToken
	 * 
	 * @return
	 */
	@RequestMapping(value="/enUsername.xml", method=RequestMethod.GET)
	public ModelAndView findAuthorizationByLoginToken(@CookieValue("tk") String loginToken) {
		
		String method = "findAuthorizationByLoginToken():  ";
		
		ModelAndView mv = new ModelAndView();
		
		if ((loginToken == null) || (loginToken.isEmpty())) {
			String msg = "User is not logged in.";
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		Authorization auth = null;
		try {
			auth = authorizationService.findPrimaryAuthByLoginToken(loginToken);
		}
		catch (UserNotLoggedInException e) {
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (AuthorizationException e) {
			ErrorResponse response = new ErrorResponse(ResponseCode.UNAUTHORIZED, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		AuthorizationResponse response = new AuthorizationResponse(auth);
		response.setResponseCode(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		
		logger.debug(method + response.toString());
		
		return mv;
	}
	
	/**
	 * <p>Returns a list of Authorizations for the LS user identified by the givem loginToken.</p>
	 * 
	 * @param loginToken
	 * @return list of Authorizations
	 * @author Mohammad M. Naqvi
	 */
	@RequestMapping(value="/enUsersInfo", method=RequestMethod.POST)
	public ModelAndView findAuthorizationListByLoginToken(@CookieValue("tk") String loginToken) {
		
		String method = "findAuthorizationListByLoginToken( " + loginToken + "):  ";
		logger.debug(method + "called");
		ModelAndView mv = new ModelAndView();
		
		if ((loginToken == null) || (loginToken.isEmpty())) {
			String msg = "User is not logged in.";
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		List<AuthorizationDto> authDtoList = new ArrayList<AuthorizationDto>();
		try {
			authDtoList = authorizationService.findAuthorizationListByLoginToken(loginToken);
		}
		catch (UserNotLoggedInException e) {
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (AuthorizationException e) {
			ErrorResponse response = new ErrorResponse(ResponseCode.UNAUTHORIZED, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (UserNotFoundException e) {
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		catch (DuplicateEmailAddressException e) {
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		if (null == authDtoList || authDtoList.isEmpty()) {
			ErrorResponse response = new ErrorResponse(ResponseCode.UNAUTHORIZED, method + "No Authorizations found!");
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
			
		}
		logger.debug("Received " + authDtoList.size() + " authorization(s) for this user!");
		AuthorizationListResponse response = new AuthorizationListResponse(ResponseCode.SUCCESS, authDtoList);
		mv.addObject("response", response);
		logger.debug(method + response.toString());
		return mv;
	}

	/**
	 * Makes the Evernote account - identified by the given enUserName, <i>primary/syncing</i> for the user identified by the given loginToken.
	 * 
	 * @param loginToken
	 * @param enUserName
	 * @param redirectUrl
	 * @author Mohammad M. Naqvi
	 */
	@RequestMapping(value="/switchPrimaryEn", method=RequestMethod.POST)
	public ModelAndView switchPrimaryEvernote(@CookieValue("tk") String loginToken,
												@RequestParam(value = "enUserName", required = true) String enUserName,
												@RequestParam("redirectUrl") String redirectUrl) {
		
		String method = "switchPrimaryEvernote( ):  ";
		logger.debug(method + "called with enUserName: " + enUserName + ", and redirectUrl: " + redirectUrl);
		ModelAndView mv = new ModelAndView();
		
		if ((loginToken == null) || (loginToken.isEmpty())) {
			String msg = "User is not logged in.";
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		if (enUserName == null || enUserName.isEmpty()) {
			String msg = "Missing 'enUserName' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} 
		
		if (redirectUrl == null || redirectUrl.isEmpty()) {
			String msg = "Missing 'redirectUrl' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.MISSING_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		try {
			authorizationService.switchPrimaryEvernoteAccount(loginToken, enUserName);
			logger.debug(method + "switch primary to " + enUserName + " completed");
		} catch (AuthorizationNotFoundException e) {
			logger.error(method + "No Authorization found for given user with the EN-Username: " + enUserName, e);
			ErrorResponse response = new ErrorResponse(ResponseCode.AUTH_BY_EN_USERNAME_NOT_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (AuthorizationExpiredException e) {
			logger.error(method + " The requested EN Authorization is expired, cannot be switched to Primary/Syncing!", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.EN_AUTHORIZATION_IS_EXPIRED, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (UserNotLoggedInException e) {
			logger.error(method + e.getMessage());
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (AuthorizationException e) {
			logger.error(method + "None Evernote Authorization found for the given user!", e);
			ErrorResponse response = new ErrorResponse(ResponseCode.NO_EN_AUTHORIZATIONS_FOUND, e.getMessage());
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}

		logger.debug(method + "redirecting to to " + redirectUrl);
		return new ModelAndView(new RedirectView(redirectUrl));
		
	}
	
	/**
	 * <p></p>
	 * 
	 * @param loginToken
	 * @param code
	 * 
	 * @return
	 */
	@RequestMapping(value="/upgrade/en.xml", method=RequestMethod.POST)
	public ModelAndView saveEvernotePremiumPromoCode(@RequestParam("tk") String loginToken, @RequestParam("code") String code) {
		
		String method = "saveEvernotePremiumPromoCode()  ";
		
		ModelAndView mv = new ModelAndView();
		
		//	Validating 'tk' param
		if (loginToken == null || loginToken.isEmpty()) {
			String msg = "Missing 'tk' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Validating code param
		if (code == null || code.isEmpty()) {
			String msg = "Missing 'code' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Get user from the loginToken
		User user = loginService.getUserInfoByLoginToken(loginToken, null); 
		if (user == null) {
			String msg = "Cannot find user with loginToken=" + loginToken;
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		String uid = user.getUid();
		
		logger.debug(method + "Finding authorization for user with UID '" + uid + "'.");
		
		//	Get Authorization for the user
		Authorization auth = null;
		try {
			auth = authorizationService.findPrimaryAuthByUId(uid, AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
			
		} catch (UserNotFoundException e) {
			String msg = "Cannot find user with UID '" + uid + "'.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (AuthorizationException e) {
			String msg = "The user '" + user.getPrimaryEmail() + "' has not registered with Evernote.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.UNAUTHORIZED, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Make GET HTTP call to Evernote to redeem coupon code
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(appProperties.getProperty("oauth.evernote.base.url"));
			sb.append("/RedeemCouponJSON.action?");
			sb.append("authToken=");
			sb.append(URLEncoder.encode(auth.getOauthAccessToken(), "UTF-8"));
			sb.append("&code=");
			sb.append(URLEncoder.encode(code, "UTF-8"));
			
			HttpURLConnection connection = (HttpURLConnection) (new URL(sb.toString())).openConnection();
			int responseCode = connection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				String msg = "Evernote server returned error code: " + responseCode + " " + connection.getResponseMessage();
				logger.error(method + msg);
				ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
				mv.setViewName(VIEW_ERROR);
				mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
				return mv;
			}
			
			// Parse json response from Evernote
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String result = bufferedReader.readLine();
			logger.debug(method + "RedeemCouponJSON response: " + result);
			JSONObject json = (JSONObject) JSONSerializer.toJSON(result);
			boolean isSuccess = json.getBoolean("success");
			if (!isSuccess) {
				JSONArray errors = json.getJSONArray("errors");
				JSONObject errorCodeObj = errors.getJSONObject(0);
				String errorCode = errorCodeObj.getString("code");
				ErrorResponse response = null;
				
				if (ResponseCode.INVALID_AUTH.name().equals(errorCode)) {
					response = new ErrorResponse(ResponseCode.INVALID_AUTH, ResponseCode.INVALID_AUTH.getMessage());
					
				} else if (ResponseCode.INVALID_PURCHASE_CODE.name().equals(errorCode)) {
					response = new ErrorResponse(ResponseCode.INVALID_PURCHASE_CODE, ResponseCode.INVALID_PURCHASE_CODE.getMessage());
					
				} else if (ResponseCode.ALREADY_REDEEMED_BY_USER.name().equals(errorCode)) {
					response = new ErrorResponse(ResponseCode.ALREADY_REDEEMED_BY_USER, ResponseCode.ALREADY_REDEEMED_BY_USER.getMessage());
					
				} else if (ResponseCode.ALREADY_REDEEMED_BY_OTHER_USER.name().equals(errorCode)) {
					response = new ErrorResponse(ResponseCode.ALREADY_REDEEMED_BY_OTHER_USER, ResponseCode.ALREADY_REDEEMED_BY_OTHER_USER.getMessage());
					
				} else if (ResponseCode.INELIGIBLE_SPONSOR_OWNER.name().equals(errorCode)) {
					response = new ErrorResponse(ResponseCode.INELIGIBLE_SPONSOR_OWNER, ResponseCode.INELIGIBLE_SPONSOR_OWNER.getMessage());
					
				} else if (ResponseCode.INELIGIBLE_OTHER.name().equals(errorCode)) {
					response = new ErrorResponse(ResponseCode.INELIGIBLE_OTHER, ResponseCode.INELIGIBLE_OTHER.getMessage());
					
				} else if (ResponseCode.INTERNAL_ERROR.name().equals(errorCode)) {
					response = new ErrorResponse(ResponseCode.INTERNAL_ERROR, ResponseCode.INTERNAL_ERROR.getMessage());
					
				} else {
					response = new ErrorResponse(ResponseCode.SERVER_ERROR, "There is error occured when redeeming your Evernote premium code.");
				}
				mv.setViewName(VIEW_ERROR);
				mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
				return mv;
			}
	
		} catch (UnsupportedEncodingException ee) {
			String msg = "Error when encoding Evernote coupon redemption url.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (MalformedURLException e) {
			String msg = "Invalid Evernote coupon redemption url.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
			
		} catch (IOException e) {
			String msg = "An IO Exception has occured while requesting Evernote coupon redemption url.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		logger.debug(method + "Saving Evernote premium code '" + code + "'.");
		
		//	Redemption is successful, save the coupon code
		try {
			authorizationService.saveEvernotePremiumCode(user, code);
		} catch (Exception e) {
			String msg = "There's error when saving Evernote Premium code in the system.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		ServiceResponse response = new ServiceResponse(ResponseCode.SUCCESS);
		mv.setViewName(VIEW_XML);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param loginToken
	 * 
	 * @return
	 */
	@RequestMapping(value = "/isAuthorized")
	public ModelAndView isAuthorized(@CookieValue("tk") String loginToken) {
		
		String method = "isAuthorized():  ";
		
		ModelAndView mv = new ModelAndView();
		
		if ((loginToken == null) || (loginToken.isEmpty())) {
			String msg = "User is not logged in.";
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		boolean authorized = false;
		try {
			authorized = authorizationService.isAuthorized(loginToken);
		}
		catch (UserNotLoggedInException e) {
			String msg = "User is not logged in.";
			logger.debug(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_LOGGED_IN, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject("response", response);
			return mv;
		}
		
		AuthorizationResponse response = new AuthorizationResponse(ResponseCode.SUCCESS);
		response.setAuthorized(authorized);
		mv.setViewName(VIEW_XML);
		mv.addObject("response", response);
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * 
	 * @return
	 */
	@RequestMapping(value="/isEvernotePremiumUser.xml", method=RequestMethod.POST)
	public ModelAndView isEvernotePremiumUser(@RequestParam("email") String email) {
		
		String method = "isEvernotePremiumUser()  ";
		
		ModelAndView mv = new ModelAndView();
		
		// Validating email param
		if (email == null || email.isEmpty()) {
			String msg = "Missing 'email' parameter.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_PARAMETER, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		// Get Authorization for the user
		Authorization auth = null;
		try {
			auth = authorizationService.findPrimaryAuthByEmail(email, AuthorizationType.EVERNOTE_OAUTH_ACCESS_TOKEN);
		} catch (UserNotFoundException e) {
			String msg = "User '" + email + "' is not found.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.USER_NOT_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		} catch (DuplicateEmailAddressException e) {
			String msg = "Multiple Users found by email - '" + email;
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.DUPLICATE_EMAIL_ADDRESS_FOUND, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	If the user has not registered with Evernote
		if (auth == null) {
			String msg = "User '" + email + "' has not registered with Evernote.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.UNAUTHORIZED, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		//	Get PremiumInfo from Evernote
		PremiumInfo premiumInfo = null;
		try {
			premiumInfo = evernoteUserService.getUserPremiumInfo(auth.getOauthAccessToken());
			
		} catch (Exception e) {
			String msg = "An error has occured when retrieving premium info from Evernote for '" + email + "'.";
			logger.error(method + msg);
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, msg);
			mv.setViewName(VIEW_ERROR);
			mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
			return mv;
		}
		
		ServiceResponse response = new ServiceResponse();
		if (premiumInfo.isPremium()) {
			response.setResponseCode(ResponseCode.PREMIUM_EVERNOTE_USER);
		} else {
			response.setResponseCode(ResponseCode.NON_PREMIUM_EVERNOTE_USER);
		}
		
		mv.setViewName(VIEW_XML);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
		return mv;
	}
	
	/**
	 * <p>Validates if the provider is valid and supported by the system.</p>
	 * 
	 * @param provider The 2-character code of a provider.
	 * 
	 * @return <code>true</code>, if the given provider is valid; <code>false</code> if not.
	 */
	private boolean isValidOAuthProvider(String provider) {
		
		for (AuthorizationType authType : AuthorizationType.values()) {
			if (authType.getCode().equals(provider)) {
				return true;
			}
		}
		
		return false;
	}
		
	
	/**
	 * <p>Get 1 year from now as default expiration date.</p>
	 * 
	 * @return a <code>Date</code> 1 year from &quot;now&quot;.
	 */
	private Date getDefaultExpirationDate() {
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		return cal.getTime();
	}
	
	
	/**
	 * <p>Parse oauth expiration date</p>
	 * 
	 * @param expirationDate
	 * @return
	 */
	private Date getOAuthExpirationDate(String expirationDate) {
		Date expDate;
		
		//	If expiration date is not specified, default to 1 year
		if (expirationDate == null || expirationDate.isEmpty()) {
			expDate = getDefaultExpirationDate();
			
		} else {
			try {
				expDate = new Date(Long.parseLong(expirationDate));
				
			} catch (NumberFormatException e) {
				logger.error("Error when parsing expiration date: " + expirationDate);
				expDate = getDefaultExpirationDate();
			}
		}
		
		return expDate;
	}
}

package com.livescribe.aws.login;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.livescribe.aws.evernote.oauth.SimpleOAuthRequest;
import com.livescribe.framework.config.AppConstants;
import com.livescribe.framework.config.AppProperties;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.login.service.LoginService;
import com.livescribe.framework.oauth.service.AuthorizationService;
import com.livescribe.framework.oauth.service.EvernoteUserService;
import com.livescribe.framework.orm.consumer.User;

@Controller
@RequestMapping("/doEvernoteOAuth.do")
public class EvernoteOAuthController implements AppConstants {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static String SERVICES_PATH		= "/services";
	private static String SERVLET_PATH		= "/lsloginservice/doEvernoteOAuth.do";
	private static String SAVE_OAUTH_SERVLET_PATH = "/lsloginservice/saveOAuthToken.xml";
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private EvernoteUserService evernoteUserService;
	
	@Autowired
	private LoginService loginService;
	
	private String redirectUrl;
	
	private Boolean reAuthorized;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
								 @RequestParam(value = "reAuthorized", required = false) Boolean reAuthorized,
								 @CookieValue("tk") String loginToken,
								 HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String method = "showForm():  ";
		
		ModelAndView mv = null;
		
		// if redirectUrl param is not specified, use default redirectUrl defined in app.properties
		if (redirectUrl == null || redirectUrl.isEmpty()) {
			redirectUrl = appProperties.getProperty(PROP_OAUTH_EN_REDIRECT_URL);
		}
		this.redirectUrl = redirectUrl;
		
		// If reAuthorized param is not specified, defaulting it to false (which means we are doing authorization from OOBE flow)
		if (reAuthorized == null) {
			reAuthorized = false;
		}
		this.reAuthorized = reAuthorized;
		
		// Get logged-in user
		User user = null;
		try {
			user = getUserFromLoginToken(loginToken);
			
		} catch (UserNotLoggedInException unlie) {
			throw unlie; //TODO:  Need to decide if AJAX will perform redirect.
		}
		
		// if user is not logged in, redirect to login page
		if (user == null) {
			return new ModelAndView(new RedirectView("login.htm"));
		}
		
		
		String accessToken = null;
		String enShardId = null;
		String expirationDate = null;
		
		String enUsername = "";
		boolean enPreferRegistration = Boolean.valueOf(appProperties.getProperty(PROP_OAUTH_EN_PREFER_REGISTRATION));

		// Do OAuth authorization to get Evernote access token
		mv = doOAuth(request, enUsername, enPreferRegistration);

		// Redirect to Evernote to ask for user's authorization
		if (mv != null) {
			return mv;
		}

		// If Evernote authorization is done, accessToken, shardId and edam_expires should exist in session
		accessToken = (String)request.getSession().getAttribute("accessToken");
		enShardId = (String)request.getSession().getAttribute("shardId");
		expirationDate = (String)request.getSession().getAttribute("edam_expires");

		// If the user declined to authorize Livescribe with Evernote
		if (accessToken == null || enShardId == null || expirationDate == null) {
			logger.debug(method + "User clicked 'Cancel' on Evernote authorization page.");
			return new ModelAndView("closePopupView");
		}

		// We only need to redirect to /saveOAuthToken.xml when we get new Evernote access token
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("https://");

		String host = request.getServerName();
		urlBuilder.append(host);

		//	Check if the direct URL was used to access this Service, or if
		//	the URL was proxied by the Web server.
		int port = request.getServerPort();
		String portStr = this.appProperties.getProperty(PROP_HTTP_PORT);
		int configPort = Integer.parseInt(portStr);
		if (configPort == port) {
			String servletPath = request.getServletPath();
			logger.debug("servletPath:  " + servletPath);

			urlBuilder.append(SAVE_OAUTH_SERVLET_PATH);
		}
		else {
			urlBuilder.append(SERVICES_PATH);
			urlBuilder.append(SAVE_OAUTH_SERVLET_PATH);
		}

		urlBuilder.append("?");
		urlBuilder.append("accessToken=").append(accessToken).append("&");
		urlBuilder.append("enShardId=").append(enShardId).append("&");
		urlBuilder.append("provider=EN").append("&");
		urlBuilder.append("expirationDate=").append(expirationDate).append("&");
		
		// Update the Redirect URL with the current Evernote user name
		// This is needed because the user might have logged in as a different EN Username, but later on the 
		// Evernote Pop-up window, logged-in as a different user. We want to ensure that the correct Evernote username 
		// is used.
		com.evernote.edam.type.User evernoteUser = evernoteUserService.getCurrentUser(accessToken);
		String enUserName = evernoteUser.getUsername();
		String updatedRedirectUrl = updateEnUserNameInRedirectURL(redirectUrl, enUserName);
		

		urlBuilder.append("redirectUrl=").append(updatedRedirectUrl).append("&");
		urlBuilder.append("reAuthorized=").append(reAuthorized.toString());
		
		logger.debug("URL string: " + urlBuilder.toString());

		// redirect to /saveOAuthToken.xml to save Evernote authorization
		return new ModelAndView(new RedirectView(urlBuilder.toString()));
	}

	private String updateEnUserNameInRedirectURL(String redirectUrl, String enUserName) {
		
		logger.debug("updateEnUserNameInRedirectURL called with redirectUrl: " + redirectUrl + ", and enUserName: " + enUserName); 

		if (null == redirectUrl || redirectUrl.isEmpty()) {
			return null;
		}
		StringBuilder updatedUrlBuilder = new StringBuilder("");
		String[] params = redirectUrl.split("\\?");
		for(String param: params) {
			logger.debug(param);
			if (param.startsWith("enUserName=")) {
				param = "enUserName=" + enUserName;
			}
			updatedUrlBuilder.append(param).append("?");
		}
		updatedUrlBuilder.deleteCharAt(updatedUrlBuilder.length() - 1 );   // remove the trailinig '?'
		String updatedUrl = updatedUrlBuilder.toString();
		logger.debug("updateEnUserNameInRedirectURL returning updatedUrl: " + updatedUrl); 
		return updatedUrl;
	}

	/**
	 * <p>Find User by loginToken Cookie</p>
	 * 
	 * @param loginToken
	 * @return
	 * @throws UserNotLoggedInException
	 */
	private User getUserFromLoginToken(String loginToken) throws UserNotLoggedInException {
		String method = "getUserFromLoginToken():  ";
		
		if ((loginToken == null) || ("".equals(loginToken))) {
			String msg = "User must be logged in to perform this operation.";
			logger.error(method + msg);
			throw new UserNotLoggedInException(msg);
		}
		
		// Find user by loginToken
		User user = loginService.getUserInfoByLoginToken(loginToken, "WEB");
		
		return user;
	}
	
	/**
	 * Do a 2-stage Evernote authorization: get request token and get access token
	 * 
	 * @param request
	 * 
	 * @return null when EN authorization is complete. A RedirectView when the service needs to redirect to Evernote to ask 
	 * for user's authorization
	 * 
	 * @throws Exception
	 */
	private ModelAndView doOAuth(HttpServletRequest request, String enUsername, boolean enPreferRegistration) throws Exception {
		
		String method = "doOAuth():  ";
		logger.debug(method + request.getRequestURL());
		
		String requestToken = (String)request.getSession().getAttribute("requestToken");
		String accessToken = (String)request.getSession().getAttribute("accessToken");
		
		// Get request token
		if (requestToken == null && accessToken == null) {
			getRequestToken(request);
			requestToken = (String)request.getSession().getAttribute("requestToken");
		}
		
		String verifier = (String)request.getSession().getAttribute("verifier");
		
		// Get authorization at Evernote site
		if (requestToken != null && verifier == null && accessToken == null) {
			// if callbackReturn
			if ("callbackReturn".equals(request.getParameter("action"))) {
				// now the Evernote callback to us with the oauth_token and oauth_verifier params included
				request.getSession().setAttribute("requestToken", request.getParameter("oauth_token"));
				request.getSession().setAttribute("verifier", request.getParameter("oauth_verifier"));
				verifier = request.getParameter("oauth_verifier");
				
			} else {
				StringBuilder sb = new StringBuilder();
				String authUrl = appProperties.getProperty(PROP_OAUTH_EN_AUTHORIZATION_URL);
				sb.append(authUrl);
				sb.append("?username=").append(enUsername);
				sb.append("&oauth_token=").append(requestToken);
				sb.append("&preferRegistration=").append(String.valueOf(enPreferRegistration));
				
				// redirect to Evernote for authorization
//				String authorizationUrl = appProperties.getProperty(PROP_OAUTH_EN_AUTHORIZATION_URL) + "?oauth_token=" + requestToken;
				String authorizationUrl = sb.toString();
				return new ModelAndView(new RedirectView(authorizationUrl));
			}
			
		}
		
		// Get access token
		if (requestToken != null && verifier != null && accessToken == null) {
			getAccessToken(request);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param request
	 * 
	 * @throws IOException
	 */
	private void getRequestToken(HttpServletRequest request) throws IOException {
		String method = "getRequestToken():  ";
		
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("https://");
		
		String host = request.getServerName();
		urlBuilder.append(host);
				
		//	Check if the direct URL was used to access this Service, or if
		//	the URL was proxied by the Web server.
		int port = request.getServerPort();
		String portStr = this.appProperties.getProperty(PROP_HTTP_PORT);
		int configPort = Integer.parseInt(portStr);
		if (configPort == port) {
			String servletPath = request.getServletPath();
			logger.debug("servletPath:  " + servletPath);
			
			urlBuilder.append(SERVLET_PATH);
		}
		else {
			urlBuilder.append(SERVICES_PATH);
			urlBuilder.append(SERVLET_PATH);
		}
		urlBuilder.append("?action=callbackReturn").append("&");
		urlBuilder.append("redirectUrl=").append(this.redirectUrl).append("&");
		urlBuilder.append("reAuthorized=").append(this.reAuthorized.toString());
		
		String callbackUrl = urlBuilder.toString();
		logger.debug(method + "callbackUrl:  " + callbackUrl);
		
//		String callbackUrl = request.getRequestURL() + "?action=callbackReturn";
		SimpleOAuthRequest oAuthReq = new SimpleOAuthRequest(
				appProperties.getProperty(PROP_OAUTH_EN_REQUEST_TOKEN_URL), 
				appProperties.getProperty(PROP_OAUTH_EN_CONSUMER_KEY), 
				appProperties.getProperty(PROP_OAUTH_EN_SECRET_KEY), 
				null);
		
		oAuthReq.setParameter("oauth_callback", callbackUrl);
		logger.debug(method + "Request Token Url: " + oAuthReq.encode());
		logger.debug(method + "Callback Url: " + callbackUrl);
		
		Map<String,String> reply = oAuthReq.sendRequest();
		logger.debug(method + "Returned param: " + reply);
		
		request.getSession().setAttribute("requestToken", reply.get("oauth_token"));
	}
	
	/**
	 * 
	 * @param request
	 * @throws IOException
	 */
	private void getAccessToken(HttpServletRequest request) throws IOException {
		String method = "getAccessToken():  ";
		
		SimpleOAuthRequest oAuthReq = new SimpleOAuthRequest(
				appProperties.getProperty(PROP_OAUTH_EN_REQUEST_TOKEN_URL), 
				appProperties.getProperty(PROP_OAUTH_EN_CONSUMER_KEY), 
				appProperties.getProperty(PROP_OAUTH_EN_SECRET_KEY), 
				null);
		
		oAuthReq.setParameter("oauth_token", (String)request.getSession().getAttribute("requestToken"));
		oAuthReq.setParameter("oauth_verifier", (String)request.getSession().getAttribute("verifier"));
		logger.debug(method + "Making OAuth Request to get the access token... Access Token Url: " + oAuthReq.encode());
		Map<String,String> reply = oAuthReq.sendRequest();

		request.getSession().setAttribute("shardId", reply.get("edam_shard"));
		request.getSession().setAttribute("accessToken", reply.get("oauth_token"));
		request.getSession().setAttribute("edam_expires", reply.get("edam_expires"));
	}
}

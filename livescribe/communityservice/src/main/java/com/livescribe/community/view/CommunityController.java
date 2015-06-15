/**
 * 
 */
package com.livescribe.community.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.authservice.api.impl.LSAuthenticationServiceClientWrapper;
import com.livescribe.authservice.exception.InvalidParameterListException;
import com.livescribe.authservice.exception.UserNotLoggedInException;
import com.livescribe.base.constants.AppNames;
import com.livescribe.community.service.ResponseStatus;
import com.livescribe.community.service.ServiceResponse;
import com.livescribe.community.view.LoginFormData;
import com.livescribe.community.CommunityConstants;
import com.livescribe.community.service.PencastService;
import com.livescribe.community.service.SearchService;
import com.livescribe.community.service.UserService;
import com.livescribe.community.view.vo.PencastVo;

/**
 * <p>Handles all Community-related requests for pencasts.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Controller
public class CommunityController {
	
	private static final String PARAM_PAGE			= "page";
	private static final String PARAM_START			= "start";
	private static final String PARAM_FETCH_SIZE	= "fetchSize";
	
	private static String ATTRIB_PENCAST_LIST		= "PencastList";
	private static String ATTRIB_PAGE_NUMBER		= "pageNum";
	private static String ATTRIB_PAGE_COUNT			= "pageCount";
	private static String ATTRIB_ERROR				= "error";
	private static String VIEW_CATEGORY_LIST		= "categoryListView";
	private static String VIEW_PENCAST_LIST			= "pencastListView";
	private static String VIEW_PENCAST_ITEM			= "pencastItemView";
	private static String VIEW_FORWARD				= "forward:";
	private static String VIEW_ERROR				= "error";
	private static String VIEW_SEARCH				= "searchView";
	private static String VIEW_SEARCH_ERROR			= "searchError";
	private static String MV_NAME_CATEGORY_LIST		= "categoryList";
	private static String MV_NAME_PATH				= "path";
	private static String MV_NAME_PENCAST_LIST		= "pencastList";
	private static String MV_NAME_PENCAST_ITEM		= "pencastItem";
	private static String MV_NAME_FACET_RESULTS		= "facetResults";
	private static String MV_NAME_SEARCH_RESULTS	= "searchResults";
	private static String PARAM_PAGE_NEXT			= "next";
	private static String PARAM_PAGE_PREV			= "prev";
	private static int    DEFAULT_FETCH_SIZE		= 20;
	private static String TOKEN_NAME				= "tk";

	public static final String FACETS_KEY			= "facets_key";
	public static final String DOC_LIST_KEY			= "doc_list_key";

	private static final String VIEW_SUCCESS	= "success";
	private static final String VIEW_FAILED		= "failed";
	private static final String MESSAGE_PARAM	= "message";
	private static String RESPONSE_PARAMS	= "responseParams";
	private static int COOKIE_EXPIRATION_SECONDS	= 180000;

	private static int RESPONSE_STATUS_OK	= 0;
	
	@Autowired
	private PencastService pencastService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SearchService searchService;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public CommunityController() {}
	
	@RequestMapping(value = "/createAccount", method = RequestMethod.POST)
	public ModelAndView createAccount(LoginFormData params, HttpServletRequest req, HttpServletResponse resp) {
		
		String email = params.getEmail();
		String password = params.getPassword();
		
		ModelAndView mv = new ModelAndView();

		if ((email == null) || (email.equals(""))) {
			String msg = "No e-mail address was given.";
			mv.setViewName(VIEW_FAILED);
			mv.addObject(MESSAGE_PARAM, msg);
			logger.info(msg);
			return mv;
		}
		if ((password == null) || (password.equals(""))) {
			String msg = "No password was given.";
			mv.setViewName(VIEW_FAILED);
			mv.addObject(MESSAGE_PARAM, msg);
			logger.info(msg);
			return mv;
		}

		Map<String, Object> mappedResponse = userService.createAccount(email, password);
		
		Integer returnCode = (Integer)mappedResponse.get("returnCode");
		
		if (RESPONSE_STATUS_OK == returnCode) {
			logger.info("Account created!");
			mv.setViewName(VIEW_SUCCESS);
			mv.addObject(RESPONSE_PARAMS, mappedResponse);
			Cookie cookie = new Cookie("tk", (String)mappedResponse.get("token"));
			String host = req.getServerName();
			cookie.setDomain(host);
			cookie.setPath("/");
			cookie.setMaxAge(COOKIE_EXPIRATION_SECONDS);
			resp.addCookie(cookie);
		}
		else {
			String message = "Account creation failed!";
			logger.info(message);
			mv.setViewName(VIEW_FAILED);
			mv.addObject(MESSAGE_PARAM, message);
		}
		
		return mv;
	}

	/**
	 * @param email
	 * @param desktopVersion
	 * @return
	 */
	private HashMap<String, String> createParameterMap(String email, String desktopVersion) {
		
		HashMap<String, String> standardParams = new HashMap<String, String>();
		standardParams.put("userEmail", email);
		standardParams.put("clientCulture", "en-US");
		standardParams.put("clientGUID", "12345");
		standardParams.put("clientVersion", desktopVersion);
		standardParams.put("clientApplication", AppNames.LSCommunityService.toString());
		return standardParams;
	}
	
	/**
	 * <p></p>
	 * 
	 * @return
	 */
//	@RequestMapping(value = "/pencast", method = RequestMethod.GET)
	public ModelAndView getAllPencasts(HttpServletRequest request) {
		
		String method = "getAllPencasts():  ";
		
		ModelAndView mv = new ModelAndView();
		
		String startParam = request.getParameter(PARAM_START);
		String fetchSizeParam = request.getParameter(PARAM_FETCH_SIZE);
		
		int start = 0;
		int fetchSize = DEFAULT_FETCH_SIZE;
		if (startParam != null) {
			try {
				start = Integer.parseInt(startParam);
			}
			catch (NumberFormatException nfe) {
				logger.error(method + "The '" + PARAM_START + "' parameter was not a number.");
//				mv.setViewName(VIEW_ERROR);
//				mv.addObject(ATTRIB_ERROR, new String("The '" + PARAM_START + "' parameter was not a number."));
				return mv;
			}
		}
		
		if (fetchSizeParam != null) {
			try {
				fetchSize = Integer.parseInt(fetchSizeParam);
			}
			catch (NumberFormatException nfe) {
				logger.error(method + "The '" + PARAM_FETCH_SIZE + "' parameter was not a number.");
//				mv.setViewName(VIEW_ERROR);
//				mv.addObject(ATTRIB_ERROR, new String("The '" + PARAM_FETCH_SIZE + "' parameter was not a number."));
				return mv;
			}
		}
		
		List<PencastVo> list = pencastService.getAllPencasts(start, fetchSize);

		mv.setViewName(VIEW_PENCAST_LIST);
		mv.addObject(MV_NAME_PENCAST_LIST, list);
		
		return mv;
	}

	/**
	 * <p>Returns the list of pencast categories.</p>
	 * 
	 * @return the list of pencast categories.
	 */
	@RequestMapping(value = "/pencast/categories", method = RequestMethod.GET)
	public ModelAndView getCategoryList() {
		
		List<String> list = pencastService.getCategoryList();
		ModelAndView mv = new ModelAndView();
		mv.setViewName(VIEW_CATEGORY_LIST);
		mv.addObject(MV_NAME_CATEGORY_LIST, list);
		
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param request
	 * 
	 * @return
	 */
	@RequestMapping(value = "/pencast/mostviewed", method = RequestMethod.GET)
	public ModelAndView  getMostViewedPencasts(HttpServletRequest request) {
		
		String method = "getMostViewedPencasts():  ";
		
		ModelAndView mv = new ModelAndView();
		
		String startParam = request.getParameter(PARAM_START);
		String fetchSizeParam = request.getParameter(PARAM_FETCH_SIZE);
		
		int start = 0;
		int fetchSize = DEFAULT_FETCH_SIZE;
		if (startParam != null) {
			try {
				start = Integer.parseInt(startParam);
			}
			catch (NumberFormatException nfe) {
				logger.error(method + "The '" + PARAM_START + "' parameter was not a number.");
//				mv.setViewName(VIEW_ERROR);
//				mv.addObject(ATTRIB_ERROR, new String("The '" + PARAM_START + "' parameter was not a number."));
				return mv;
			}
		}
		
		if (fetchSizeParam != null) {
			try {
				fetchSize = Integer.parseInt(fetchSizeParam);
			}
			catch (NumberFormatException nfe) {
				logger.error(method + "The '" + PARAM_FETCH_SIZE + "' parameter was not a number.");
//				mv.setViewName(VIEW_ERROR);
//				mv.addObject(ATTRIB_ERROR, new String("The '" + PARAM_FETCH_SIZE + "' parameter was not a number."));
				return mv;
			}
		}
		
		List<PencastVo> list = pencastService.getMostViewedPencasts(start, fetchSize);
		
		mv.setViewName(VIEW_PENCAST_LIST);
		mv.addObject(MV_NAME_PENCAST_LIST, list);
		
		return mv;
	}
	
	/**
	 * <p><span style="color: red">Not implemented!</span></p>
	 * 
	 * @return
	 */
	@RequestMapping(value = "/pencast/author/{authorName}", method = RequestMethod.GET)
	public ModelAndView getPencastsByAuthor(String authorName) {
		
		ModelAndView mv = new ModelAndView();
		
		return mv;
	}
	
	/**
	 * <p>Returns a list of pencasts tagged with the given category name.</p>
	 * 
	 * @param categoryName The name of the category to search on.
	 * 
	 * @return a list of pencasts tagged with the given category name.
	 */
	@RequestMapping(value = "/pencast/category/{categoryName}", method = RequestMethod.GET)
	public ModelAndView getPencastByCategoryName(@PathVariable String categoryName, HttpServletRequest request) {
		
		String method = "getPencastByCategoryName():  ";
		
		ModelAndView mv = new ModelAndView();
		
		String startParam = request.getParameter(PARAM_START);
		String fetchSizeParam = request.getParameter(PARAM_FETCH_SIZE);
		
		int start = 0;
		int fetchSize = DEFAULT_FETCH_SIZE;
		if (startParam != null) {
			try {
				start = Integer.parseInt(startParam);
			}
			catch (NumberFormatException nfe) {
				logger.warn(method + "The '" + PARAM_START + "' parameter was not a number.");
//				mv.setViewName(VIEW_ERROR);
//				mv.addObject(ATTRIB_ERROR, new String("The '" + PARAM_START + "' parameter was not a number."));
//				return mv;
			}
		}
		
		if (fetchSizeParam != null) {
			try {
				fetchSize = Integer.parseInt(fetchSizeParam);
			}
			catch (NumberFormatException nfe) {
				logger.warn(method + "The '" + PARAM_FETCH_SIZE + "' parameter was not a number.");
//				mv.setViewName(VIEW_ERROR);
//				mv.addObject(ATTRIB_ERROR, new String("The '" + PARAM_FETCH_SIZE + "' parameter was not a number."));
//				return mv;
			}
		}
		
		List<PencastVo> list = pencastService.getPencastsByCategoryName(categoryName, start, fetchSize);
		
		mv.setViewName(VIEW_PENCAST_LIST);
		mv.addObject(MV_NAME_PENCAST_LIST, list);
		
		return mv;
	}
	
	/**
	 * <p>Returns a pencast identified by the given primary key.</p>
	 * 
	 * This method is not currently (2010/07/14) mapped to a URL pattern and
	 * cannot be accessed by external clients.
	 * 
	 * @param primaryKey 
	 * 
	 * @return 
	 */
//	@RequestMapping(value = "/pencast/{primaryKey}", method = RequestMethod.GET)
	public ModelAndView getPencastByPrimaryKey(@PathVariable byte[] primaryKey) {
		
		ModelAndView mv = new ModelAndView();
		
		PencastVo pencast = pencastService.findByPrimaryKey(primaryKey);
		
		mv.setViewName(VIEW_PENCAST_ITEM);
		mv.addObject(MV_NAME_PENCAST_ITEM, pencast);
		
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param id 
	 * 
	 * @return 
	 */
	@RequestMapping(value = "/pencast/{id}", method = RequestMethod.GET)
	public ModelAndView getPencastByShortId(@PathVariable String id) {
		
		ModelAndView mv = new ModelAndView();
		
		PencastVo pencast = pencastService.findByShortId(id);
		ArrayList<PencastVo> list = new ArrayList<PencastVo>();
		list.add(pencast);
		
		mv.setViewName(VIEW_PENCAST_ITEM);
		mv.addObject(MV_NAME_PENCAST_LIST, list);
		
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param request 
	 * 
	 * @return 
	 */
	@RequestMapping(value = "/pencast/user", method = RequestMethod.GET)
	public ModelAndView getPencastsByUser(HttpServletRequest request) {
		
//	TODO:  Could use @CookieValue here.  Need to investigate flow if cookie is NOT present.  Currently, an exception is thrown!

		ModelAndView mv = new ModelAndView();
		boolean authenticated = false;
		
		LSAuthenticationServiceClientWrapper client = new LSAuthenticationServiceClientWrapper("LIVESCRIBE");
		HashMap<String, String> stdParams = createParameterMap("comm_svc@ls.com", "2.1.0");
		
		try {
			authenticated = client.isUserLoggedInByToken(stdParams, request, null);
		}
		catch (InvalidParameterListException iple) {
			logger.debug("InvalidParameterListException thrown while calling isUserLoggedInByToken().");
			iple.printStackTrace();
		}
		catch (UserNotLoggedInException unlie) {
			logger.debug("UserNotLoggedInException thrown while calling isUserLoggedInByToken().");
			unlie.printStackTrace();
		}
		catch (XmlRpcException xre) {
			logger.error("XmlRpcException thrown while calling isUserLoggedInByToken().");
			logger.error(xre.getMessage());
			xre.printStackTrace();
		}
		
		Cookie[] cookies = request.getCookies();
		
		if (cookies == null) {
			mv.setViewName(VIEW_ERROR);
			mv.addObject(ATTRIB_ERROR, new String("No cookies found in request."));
			return mv;
		}
		
		String token = getTokenFromCookie(cookies);
		
		if ("".equals(token)) {
			logger.warn("getPencastsByUser():  No user token was found in cookies!");
			mv.setViewName(VIEW_ERROR);
			mv.addObject(ATTRIB_ERROR, new String("No user token found in cookies."));
			return mv;
		}

		//	TODO:  Decide if login check happens here or in PencastService.getUserPencasts().
		if (!authenticated) {			
			mv.setViewName(VIEW_ERROR);
			mv.addObject(ATTRIB_ERROR, new String("User is not logged in."));
			return mv;
		}
		List<PencastVo> list = pencastService.getUserPencasts(token);
		mv.setViewName(VIEW_PENCAST_LIST);
		mv.addObject(MV_NAME_PENCAST_LIST, list);

		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param id 
	 * 
	 * @return
	 */
	@RequestMapping(value = "/pencast/preview/{id}.{type}", method = RequestMethod.GET)
	public ModelAndView getPreviewImage(@PathVariable String id, @PathVariable String type, HttpServletRequest request) {
		
		String path = "";
		
		//	Determine if user is logged in.
		Cookie[] cookies = request.getCookies();
		String token = getTokenFromCookie(cookies);
		
		//	Determine which resource is being requested.
		if ("flash".equals(id)) {
			logger.debug("Found 'flash.xml'!");
		}
		
		if (CommunityConstants.EXT_AAC.equals(type)) {
			
		}
		else if (CommunityConstants.EXT_AFD.equals(type)) {
			
		}
		else if (CommunityConstants.EXT_GIF.equals(type)) {
			logger.debug("Found GIF!");
//			pencastService.getLinkToFile(CommunityConstants.EXT_GIF);
			path = "http://localhost:8080/services/community/derivatives/content.gif";
		}
		else if (CommunityConstants.EXT_PCC.equals(type)) {
			
		}
		else if (CommunityConstants.EXT_PDF.equals(type)) {
			
		}
		else if (CommunityConstants.EXT_XML.equals(type)) {
			
		}
		
		//	Generate the view to handle the rendering 
		//	(or streaming) of the resource.
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName(VIEW_FORWARD + path);
//		mv.addObject(MV_NAME_PATH, path);
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @return
	 */
	@RequestMapping(value = "/pencast/recent", method = RequestMethod.GET)
	public ModelAndView getRecentPencasts(HttpServletRequest request) {
		
		String method = "getRecentPencasts():  ";
		
		ModelAndView mv = new ModelAndView();
		
		String startParam = request.getParameter(PARAM_START);
		String fetchSizeParam = request.getParameter(PARAM_FETCH_SIZE);
		
		int start = 0;
		int fetchSize = DEFAULT_FETCH_SIZE;
		if (startParam != null) {
			try {
				start = Integer.parseInt(startParam);
			}
			catch (NumberFormatException nfe) {
				logger.warn(method + "The '" + PARAM_START + "' parameter was not a number.");
//				mv.setViewName(VIEW_ERROR);
//				mv.addObject(ATTRIB_ERROR, new String("The '" + PARAM_START + "' parameter was not a number."));
				return mv;
			}
		}
		
		if (fetchSizeParam != null) {
			try {
				fetchSize = Integer.parseInt(fetchSizeParam);
			}
			catch (NumberFormatException nfe) {
				logger.warn(method + "The '" + PARAM_FETCH_SIZE + "' parameter was not a number.");
//				mv.setViewName(VIEW_ERROR);
//				mv.addObject(ATTRIB_ERROR, new String("The '" + PARAM_FETCH_SIZE + "' parameter was not a number."));
				return mv;
			}
		}
		
		List<PencastVo> list = pencastService.getRecentPencasts(start, fetchSize);
		
		mv.setViewName(VIEW_PENCAST_LIST);
		mv.addObject(MV_NAME_PENCAST_LIST, list);
		
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param cookies
	 * 
	 * @return
	 */
	public String getTokenFromCookie(Cookie[] cookies) {
		
		if (cookies != null) {
			String token = "";
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				String name = cookie.getName();
				logger.debug("Found cookie: " + name);
				if (TOKEN_NAME.equals(name.toLowerCase())) {
					token = cookie.getValue();
					break;
				}
			}
			return token;
		}
		return "";
	}
	
	/**
	 * <p></p>
	 * 
	 * @param request
	 * 
	 * @return 
	 */
	@RequestMapping(value = "/pencast/top", method = RequestMethod.GET)
	public ModelAndView getTopPencasts(HttpServletRequest request) {
		
		String method = "getTopPencasts():  ";
		
		ModelAndView mv = new ModelAndView();
		
		String startParam = request.getParameter(PARAM_START);
		String fetchSizeParam = request.getParameter(PARAM_FETCH_SIZE);
		
		int start = 0;
		int fetchSize = DEFAULT_FETCH_SIZE;
		if (startParam != null) {
			try {
				start = Integer.parseInt(startParam);
			}
			catch (NumberFormatException nfe) {
				logger.warn(method + "The '" + PARAM_START + "' parameter was not a number.");
//				mv.setViewName(VIEW_ERROR);
//				mv.addObject(ATTRIB_ERROR, new String("The '" + PARAM_START + "' parameter was not a number."));
				return mv;
			}
		}
		
		if (fetchSizeParam != null) {
			try {
				fetchSize = Integer.parseInt(fetchSizeParam);
			}
			catch (NumberFormatException nfe) {
				logger.warn(method + "The '" + PARAM_FETCH_SIZE + "' parameter was not a number.");
//				mv.setViewName(VIEW_ERROR);
//				mv.addObject(ATTRIB_ERROR, new String("The '" + PARAM_FETCH_SIZE + "' parameter was not a number."));
				return mv;
			}
		}
		
		List<PencastVo> list = pencastService.getTopPencasts(start, fetchSize);
		
		mv.setViewName(VIEW_PENCAST_LIST);
		mv.addObject(MV_NAME_PENCAST_LIST, list);
		
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param params
	 * @param req
	 * @param resp
	 * 
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(LoginFormData params, HttpServletRequest req, HttpServletResponse resp) {
		
		String email = params.getEmail();
		String password = params.getPassword();
		
		ModelAndView mv = new ModelAndView();
		
		if ((email == null) || (email.equals(""))) {
			String msg = "No e-mail address was given.";
			mv.setViewName(VIEW_FAILED);
			mv.addObject(MESSAGE_PARAM, msg);
			logger.info(msg);
			return mv;
		}
		if ((password == null) || (password.equals(""))) {
			String msg = "No password was given.";
			mv.setViewName(VIEW_FAILED);
			mv.addObject(MESSAGE_PARAM, msg);
			logger.info(msg);
			return mv;
		}

		Map<String, Object> mappedResponse = userService.logUserInWithEmail(email, password);
		
		Integer returnCode = (Integer)mappedResponse.get("returnCode");
		
		if (RESPONSE_STATUS_OK == returnCode) {
			logger.info("Login succeeded!");
			mv.setViewName(VIEW_SUCCESS);
			mv.addObject(RESPONSE_PARAMS, mappedResponse);
			Cookie cookie = new Cookie("tk", (String)mappedResponse.get("token"));
//			String host = req.getServerName();
//			cookie.setDomain(host);
			cookie.setPath("/community");
//			cookie.setMaxAge(COOKIE_EXPIRATION_SECONDS);
			resp.addCookie(cookie);
		}
		else {
			String message = "Login failed!";
			logger.info(message);
			mv.setViewName(VIEW_FAILED);
			mv.addObject(MESSAGE_PARAM, message);
		}
		
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param token
	 * 
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(@CookieValue("tk") String token, HttpServletRequest req, HttpServletResponse resp) {
		
		ModelAndView mv = new ModelAndView();
		
		if ((token == null) || ("".equals(token))) {
			String msg = "Logout failed!  No token found.";
			logger.info(msg);
			mv.setViewName(VIEW_FAILED);
			mv.addObject(MESSAGE_PARAM, msg);
		}
		
		Map<String, Object> mappedResponse = userService.logUserOut(token);
		
		Integer returnCode = (Integer)mappedResponse.get("returnCode");
		
		if (RESPONSE_STATUS_OK == returnCode) {
			mv.setViewName(VIEW_SUCCESS);
			mv.addObject(RESPONSE_PARAMS, mappedResponse);
			Cookie[] cookies = req.getCookies();
			for (int i = 0; i < cookies.length; i++) {
				String name = cookies[i].getName();
				if ("tk".equals(name)) {
					cookies[i].setMaxAge(0);
					cookies[i].setPath("/");
					resp.addCookie(cookies[i]);
				}
			}
		}
		else {
			String msg = "Logout failed!";
			mv.setViewName(VIEW_FAILED);
			mv.addObject(MESSAGE_PARAM, msg);
		}
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param keywords
	 * 
	 * @return
	 */
	@RequestMapping(value = "/pencast/search", method = RequestMethod.GET)
	public ModelAndView search(@RequestParam("keywords") String keywords) {
		
		ModelAndView mv = new ModelAndView();

		ArrayList<String> facetList = new ArrayList<String>();
		ArrayList<String> resultList = new ArrayList<String>();
		
		//	Execute the search.
		Map<String, Object> results = searchService.findPencasts(keywords);
		
		if (results != null) {
			Map<String, SortedMap<String, Long>> facetResults = (Map<String, SortedMap<String, Long>>)results.get(FACETS_KEY);
	
			Set frKeys = facetResults.keySet();
			Iterator<String> frKeyIter = frKeys.iterator();
			
			//	Iterate over the facets.
			while (frKeyIter.hasNext()) {
				String frKey = frKeyIter.next();
				
				logger.debug("facet key:  " + frKey);
				
				//	Get the facet results.
				Map<String, Long> ffMap = facetResults.get(frKey);
				Set ffKeys = ffMap.keySet();
				Iterator<String> ffKeyIter = ffKeys.iterator();
				while (ffKeyIter.hasNext()) {
					String ffKey = ffKeyIter.next();
					Long count = ffMap.get(ffKey);
					String facetEntry = ffKey + " (" + count + ")";
					facetList.add(facetEntry);
				}
			}
			ArrayList<String> titleList = new ArrayList<String>();
			SolrDocumentList docs = (SolrDocumentList)results.get(DOC_LIST_KEY);
			Iterator<SolrDocument> docIter = docs.iterator();
			while (docIter.hasNext()) {
				SolrDocument doc = docIter.next();
				String title = (String)doc.getFirstValue("fileDisplayName");
				titleList.add(title);
			}
			mv.setViewName(VIEW_SEARCH);
			mv.addObject(MV_NAME_FACET_RESULTS, facetList);
			mv.addObject(MV_NAME_SEARCH_RESULTS, titleList);
		}
		else {
			mv.setViewName(VIEW_SEARCH_ERROR);
		}
		return mv;
	}
}

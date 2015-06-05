package com.livescribe.admin.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.admin.controller.dto.InfoLookupResultDto;
import com.livescribe.admin.exception.ParameterParsingException;
import com.livescribe.admin.lookup.Comparator;
import com.livescribe.admin.lookup.LookupCriteria;
import com.livescribe.admin.lookup.LookupCriteriaBuilder;
import com.livescribe.admin.lookup.LookupCriteriaList;
import com.livescribe.admin.lookup.QueryField;
import com.livescribe.admin.service.InfoLookupService;
import com.livescribe.admin.service.PenService;
import com.livescribe.admin.service.UserService;

@Controller(value = "InfoLookupController")
@RequestMapping("/infoLookup.htm")
public class InfoLookupController {
	
	@Autowired
	private InfoLookupService infoLookupService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PenService penService;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(HttpServletResponse response,
			@RequestParam(value="keyParam", required=false) String[] keys, 
			@RequestParam(value="comparatorParam", required=false) String[] comparators, 
			@RequestParam(value="valueParam", required=false) String[] values) throws Exception {
		ModelAndView mv = new ModelAndView();
		LookupCriteriaList criteriaList = null;
		
		if (keys == null || keys.length == 0) {
			LookupCriteria criteria = new LookupCriteria(QueryField.PRIMARY_EMAIL, Comparator.CONTAINS, "");
			criteriaList = new LookupCriteriaList();
			criteriaList.addCriteria(criteria);
			mv.addObject("criteriaList", criteriaList.getCriteriaList());
			
		} else {
			mv = onSubmit(response, keys, comparators, values);
		}

		mv.setViewName("infoLookup");
		return mv;
	}
	
	@RequestMapping(method = {RequestMethod.POST})
	public ModelAndView onSubmit(HttpServletResponse response, 
			@RequestParam("keyParam") String[] keys, 
			@RequestParam("comparatorParam") String[] comparators, 
			@RequestParam("valueParam") String[] values) throws Exception {
		
		ModelAndView mv = new ModelAndView();

		// Construct the criteria list
		LookupCriteriaList criteriaList = null;
		try {
		    /**
		     * There is special treat for penID. To recognize the value of penID as serialNumberHex,
		     * user needs to add prefix "0x" to the effective value. The prefix will be removed when
		     * needed in other code.
		     */
			criteriaList = buildLookupCriteriaList(keys, comparators, values);
			logger.debug("LookupCriteriaList: " + criteriaList.getConditionalJoinSQL());
			
		} catch(ParameterParsingException ppe) {
			logger.debug(ppe.getMessage());
			LookupCriteria criteria = new LookupCriteria(LookupCriteriaBuilder.findQueryField(keys[0]), LookupCriteriaBuilder.findComparator(comparators[0]), values[0]);
			criteriaList = new LookupCriteriaList();
			criteriaList.addCriteria(criteria);
			
			mv.addObject("criteriaList", criteriaList.getCriteriaList());
			mv.addObject("errorMessage", ppe.getMessage());
			return mv;
		}
		
		// Simplified the code, introduced InfoLookupSrvice class and removed complex logic from the controller.. -MN (10-JAN-2014)
		InfoLookupResultDto infoLookupResult = infoLookupService.doInfoLookupByCriteria(criteriaList);
		if (null != infoLookupResult) {
			mv.addObject("resultsFound", true);
			mv.addObject("users", infoLookupResult.getUsersInfo());
			mv.addObject("unregisteredDevices", infoLookupResult.getUnregisteredDevicesInfo());
		} else {
			mv.addObject("resultsFound", false);
			mv.addObject("errorMessage", "Unable to perform info lookup based on the given criteria!");
		}

		// Send the submitted values back to JSP for use when needed
		mv.addObject("submittedKeyParam", arrayToParamString("keyParam", keys));
		mv.addObject("submittedComparatorParam", arrayToParamString("comparatorParam", comparators));
		mv.addObject("submittedValueParam", arrayToParamString("valueParam", values));
		
		mv.addObject("criteriaList", criteriaList.getCriteriaList());
		return mv;
	}
	
	/**
	 * <p>Build LookupCriteriaList object from the submitted params</p>
	 * 
	 * @param keys
	 * @param comparators
	 * @param values
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	private LookupCriteriaList buildLookupCriteriaList(String keys[], String comparators[], String values[]) throws Exception {
		LookupCriteriaList criteriaList = new LookupCriteriaList();
		
		// Construct the criteria list
		for (int i = 0; i < keys.length; i++) {
			LookupCriteria criteria = LookupCriteriaBuilder.buildLookupCriteria(keys[i], comparators[i], values[i]);
			logger.debug(criteria.toString());
			logger.debug(criteria.getConditionalSQLStatement());
			
			criteriaList.addCriteria(criteria);
		}
		
		return criteriaList;
	}
	
	/**
	 * <p>Converts an array of strings into a parameter String with values concatenated by ",".
	 * For example: keyParam=value1&keyParam=value2&keyParam=value3
	 * </p>
	 * 
	 * @param array
	 * 
	 * @return
	 */
	private String arrayToParamString(String paramName, String valueArray[]) {
		StringBuffer sb = new StringBuffer();
		
		for (String value : valueArray) {
			sb.append(paramName);
			sb.append("=");
			sb.append(value);
			sb.append("&");
		}
		
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1); // trim the last "&"
		}
		
		return "";
	}
}
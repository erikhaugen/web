package com.livescribe.admin.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.admin.exception.ParameterParsingException;
import com.livescribe.admin.lookup.Comparator;
import com.livescribe.admin.lookup.LookupCriteria;
import com.livescribe.admin.lookup.LookupCriteriaBuilder;
import com.livescribe.admin.lookup.LookupCriteriaList;
import com.livescribe.admin.lookup.QueryField;
import com.livescribe.admin.service.PenService;
import com.livescribe.admin.service.UserService;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.web.registration.client.RegistrationClient;
import com.livescribe.web.registration.dto.RegistrationDTO;
import com.livescribe.web.registration.dto.UserDto;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.response.RegistrationListResponse;
import com.livescribe.web.registration.response.UserListResponse;

@Controller(value = "VectorInfoLookupController")
@RequestMapping("/vectorInfoLookup.htm")
public class VectorInfoLookupController {
	
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

		mv.setViewName("vectorInfoLookup");
		return mv;
	}
	
	@RequestMapping(method = {RequestMethod.POST})
	public ModelAndView onSubmit(HttpServletResponse response, 
			@RequestParam("keyParam") String[] keys, 
			@RequestParam("comparatorParam") String[] comparators, 
			@RequestParam("valueParam") String[] values) {
		
		final ModelAndView mv = new ModelAndView();
		RegistrationClient registrationClient = null;

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
		
		try {
			registrationClient = new RegistrationClient();
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
			mv.addObject("criteriaList", criteriaList.getCriteriaList());
			mv.addObject("errorMessage", ex.getMessage());
			return mv;
		}
		
		UserListResponse vectorUsersListResponse = null;
        RegistrationListResponse registrationResponse = null;
        List<UserDto> users = null;
        List<RegistrationDTO> vectorRegistrationList = null;
        List<Pen> allPens = new ArrayList<Pen>();
        boolean isSearchByEmail = false;

		switch (criteriaList.getCriteriaList().get(0).getQueryField()) {
			case PRIMARY_EMAIL:
				isSearchByEmail = true;
	            String emailSearchString = criteriaList.getCriteriaList().get(0).getValue();
				switch (criteriaList.getCriteriaList().get(0).getComparator()) {
					case CONTAINS:
						try {
							vectorUsersListResponse = registrationClient.findUsersByPartialEmail(emailSearchString);
							registrationResponse = registrationClient.findRegistrationsListByPartialEmail(emailSearchString);
						} catch (RegistrationNotFoundException ex) {
							// No Registrations found is not really an error!!
							logger.info("No Registration found by given parameters");
						} catch (InvalidParameterException ex) {
							logger.error("Encountered a InvalidParameterException " , ex);
							mv.addObject("errorMessage", ex.getMessage());
						} catch (ClientException ex) {
							logger.error("Encountered a client exception " , ex);
							mv.addObject("errorMessage", ex.getMessage());
						}
						break;
					case IS_EQUAL_TO:
						try {
							vectorUsersListResponse = registrationClient.findUsersByEmail(emailSearchString);
							registrationResponse = registrationClient.findRegistrationsListByEmail(emailSearchString);
						} catch (InvalidParameterException ex) {
							logger.error("Encountered a InvalidParameterException exception ", ex);
							mv.addObject("errorMessage", ex.getMessage());
						} catch (RegistrationNotFoundException e1) {
							logger.info("No Registration found by given parameters");
							// No Registrations found, empty result.. This is not really an error!!
						} catch (ClientException ex) {
							logger.error("Encountered a client exceptiop ", ex);
							mv.addObject("errorMessage", ex.getMessage());
						}
						break;
					default:
						break;
	            }
				if (registrationResponse != null) {
					logger.debug("Received registrations from the Regisration service: \n " + registrationResponse.getRegistrations());
	                vectorRegistrationList = registrationResponse.getRegistrations();
	            }
				if (vectorUsersListResponse != null) {
					logger.debug("Received vector users from the Regisration service: \n " + vectorUsersListResponse.getUsers());
					users = vectorUsersListResponse.getUsers();
	            }
				mv.addObject("users", users);
				mv.addObject("vectorRegistrationList", vectorRegistrationList);
	            mv.addObject("resultsFound", true);
			    break;
			case PEN_SERIAL_NUMBER:
			    String penSerialSearchString = criteriaList.getCriteriaList().get(0).getValue();
				switch (criteriaList.getCriteriaList().get(0).getComparator()) {
					case CONTAINS:
						try {
							registrationResponse = registrationClient.findRegistrationsListByPartialPenSerial(penSerialSearchString);
							allPens = penService.findPensByPartialSerialNumber(penSerialSearchString);
						} catch (RegistrationNotFoundException ex) {
							// No Registrations found is not really an error!!
							logger.info("No Registration found by given parameters");
						} catch (IOException ex) {
							logger.error("Encountered an IOException ", ex);
							mv.addObject("errorMessage", ex.getMessage());
						} catch (InvalidParameterException ex) {
							logger.error("Encountered a InvalidParameterException " , ex);
							mv.addObject("errorMessage", ex.getMessage());
						} catch (ClientException ex) {
							logger.error("Encountered a client exception " , ex);
							mv.addObject("errorMessage", ex.getMessage());
						}
						break;
					case IS_EQUAL_TO:
						try {
							registrationResponse = registrationClient.findRegistrationsListByPenSerial(penSerialSearchString);
							allPens.add(penService.findPenBySerialNumber(penSerialSearchString));
						} catch (InvalidParameterException ex) {
							logger.error("Encountered a InvalidParameterException exception ", ex);
							mv.addObject("errorMessage", ex.getMessage());
						} catch (RegistrationNotFoundException e1) {
							logger.info("No Registration found by given parameters");
							// No Registrations found, empty result.. This is not really an error!!
						} catch (IOException ex) {
							logger.error("Encountered an IOException ", ex);
							mv.addObject("errorMessage", ex.getMessage());
						} catch (ClientException ex) {
							logger.error("Encountered a client exceptiop ", ex);
							mv.addObject("errorMessage", ex.getMessage());
						}
						break;
					default:
						break;
				}
				if (registrationResponse != null) {
					logger.debug("Received registrations from the Regisration service: \n " + registrationResponse.getRegistrations());
	                vectorRegistrationList = registrationResponse.getRegistrations();
	            }
				mv.addObject("vectorRegistrationList", vectorRegistrationList);
	            mv.addObject("resultsFound", true);
			    break;
			case PEN_DISPLAY_ID:
			    String penDisplayIdSearchString = criteriaList.getCriteriaList().get(0).getValue();
				switch (criteriaList.getCriteriaList().get(0).getComparator()) {
					case CONTAINS:
						try {
							registrationResponse = registrationClient.findRegistrationsListByPartialPenDisplayId(penDisplayIdSearchString);
							allPens = penService.findPensByPartialDisplayId(penDisplayIdSearchString);
						} catch (RegistrationNotFoundException ex) {
							// No Registrations found is not really an error!!
							logger.info("No Registration found by given parameters");
						} catch (ClientException ex) {
							logger.error("Encountered a client exception " , ex);
							mv.addObject("errorMessage", ex.getMessage());
						} catch (IOException ex) {
							logger.error("Encountered an IOException ", ex);
							mv.addObject("errorMessage", ex.getMessage());
						} catch (InvalidParameterException ex) {
							logger.error("Encountered InvalidParameterException ", ex);
							mv.addObject("errorMessage", ex.getMessage());
						}
						break;
					case IS_EQUAL_TO:
						try {
							registrationResponse = registrationClient.findRegistrationsListByPenDisplayId(penDisplayIdSearchString);
							allPens.add(penService.findPenByDisplayId(penDisplayIdSearchString));
						} catch (InvalidParameterException ex) {
							logger.error("Encountered a InvalidParameterException exception ", ex);
							mv.addObject("errorMessage", ex.getMessage());
						} catch (RegistrationNotFoundException e1) {
							logger.info("No Registration found by given parameters");
							// No Registrations found, empty result.. This is not really an error!!
						} catch (ClientException ex) {
							logger.error("Encountered a client exceptiop ", ex);
							mv.addObject("errorMessage", ex.getMessage());
						} catch (IllegalStateException ex) {
							logger.error("Encountered an IllegalStateException ", ex);
							mv.addObject("errorMessage", ex.getMessage());
						} catch (IOException ex) {
							logger.error("Encountered an IOException ", ex);
							mv.addObject("errorMessage", ex.getMessage());
						}
						break;
					default:
						break;
				}
				if (registrationResponse != null) {
					logger.debug("Received registrations from the Regisration service: \n " + registrationResponse.getRegistrations());
	                vectorRegistrationList = registrationResponse.getRegistrations();
	            }
				mv.addObject("vectorRegistrationList", vectorRegistrationList);
	            mv.addObject("resultsFound", true);
			    break;
			default:
				break;
		}
		
		// build list of registered users, if search was done by Pen display/serial (No need to do this if searched by email)..
		if (!isSearchByEmail && null != vectorRegistrationList && !vectorRegistrationList.isEmpty()) {
			users = new ArrayList<UserDto>();
			for (RegistrationDTO registrationDto : vectorRegistrationList) {
				UserDto user = new UserDto();
				user.setEmail(registrationDto.getEmail());
				user.setFirstName(registrationDto.getFirstName());
				user.setLastName(registrationDto.getLastName());
				if (!users.contains(user)) {
					users.add(user);
				}
			}
			mv.addObject("users", users);
		}
		
		// build list of un-registered pens..
		if (!allPens.isEmpty()) {
			List<Pen> unregisteredPens = new ArrayList<Pen>();
			for (Pen pen : allPens) {
				
				if (null == pen || !"Vector".equalsIgnoreCase(pen.getPenType())) {
					// Only show vector pens...
					continue;
				}
				
				if (!isRegisrered(pen.getSerialnumber(), vectorRegistrationList)) {
					unregisteredPens.add(pen);
				}
			}
			mv.addObject("unregisteredDevices", unregisteredPens);
		}

		// Send the submitted values back to JSP for use when needed
		mv.addObject("submittedKeyParam", arrayToParamString("keyParam", keys));
		mv.addObject("submittedComparatorParam", arrayToParamString("comparatorParam", comparators));
		mv.addObject("submittedValueParam", arrayToParamString("valueParam", values));
		mv.addObject("criteriaList", criteriaList.getCriteriaList());
		return mv;
	}

	private boolean isRegisrered(String serialnumber, List<RegistrationDTO> vectorRegistrationList) {
		if (null == vectorRegistrationList || vectorRegistrationList.isEmpty()) {
			return false;
		}
		if (null == serialnumber) {
			return false;
		}
		for (RegistrationDTO reg : vectorRegistrationList) {
			if (serialnumber.equals(reg.getPenSerial())) {
				return true; // Found the match
			}
		}
		return false;
	}

	/**
	 * <p>Build LookupCriteriaList object from the submitted params</p>
	 * 
	 * @param keys
	 * @param comparators
	 * @param values
	 * 
	 * @return
	 * @throws ParameterParsingException 
	 * 
	 * @throws Exception
	 */
	private LookupCriteriaList buildLookupCriteriaList(String keys[], String comparators[], String values[]) throws ParameterParsingException {
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

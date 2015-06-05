package com.livescribe.admin.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.admin.service.PenService;
import com.livescribe.admin.service.UserService;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.web.registration.client.RegistrationClient;
import com.livescribe.web.registration.dto.RegistrationHistoryDTO;
import com.livescribe.web.registration.dto.WarrantyDTO;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;
import com.livescribe.web.registration.response.RegistrationHistoryListResponse;
import com.livescribe.web.registration.response.WarrantyListResponse;
import com.livescribe.web.registration.response.WarrantyResponse;

@Controller(value = "VectorInfoController")
public class VectorInfoController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PenService penService;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@RequestMapping(value = "/registration/history/pen/{penSerial}.htm", method = RequestMethod.GET)
	public ModelAndView warrantyAndHistoryByPenSerial(HttpServletResponse response,
	        @PathVariable("penSerial") String penSerial,
	        @RequestParam(value="backURL", required=false) String backURL) {
		
		String method = "warrantyAndHistoryByPenSerial()";
		
		ModelAndView mv = new ModelAndView();

		try {
		    RegistrationClient client = new RegistrationClient();
		    
		    //	Get the Registration History.
		    RegistrationHistoryListResponse res = client.findRegistrationHistoryByPenSerial(penSerial);
		    List<RegistrationHistoryDTO> registrationHistory = res.getRegistrationHistories();
		    
		    //	Get the Warranty information.
		    WarrantyResponse warrantyRes = client.findWarrantyByPenSerial(penSerial);
		    WarrantyDTO warrantyDTO = warrantyRes.getWarrantyDto();
		    
		    ArrayList<WarrantyDTO> warranties = new ArrayList<WarrantyDTO>();
		    warranties.add(warrantyDTO);
		    
		    mv.addObject("warranties", warranties);
		    mv.addObject("registrations", registrationHistory);
		} catch (RegistrationNotFoundException rnfe) {
			String msg = "Registration not found for pen '" + penSerial + "'.";
		    logger.error(method + " - " + msg, rnfe);
		    mv.addObject("errorMessage", msg);
		    mv.setViewName("vectorInfo");
		    return mv;
		} catch (ClientException ce) {
			String msg = "ClientException thrown during communication with LS Registration Service.  ";
			logger.error(method + " - " + penSerial + " - " + msg, ce);
		    mv.addObject("errorMessage", msg + ce.getMessage());
		    mv.setViewName("vectorInfo");
		    return mv;
		} catch (IOException ioe) {
			String msg = "IOException thrown.  ";
			logger.error(method + " - " + penSerial + " - " + msg, ioe);
		    mv.addObject("errorMessage", msg + ioe.getMessage());
		    mv.setViewName("vectorInfo");
		    return mv;
		} catch (InvalidParameterException ipe) {
			String msg = "InvalidParameterException thrown.  ";
			logger.error(method + " - " + penSerial + " - " + msg, ipe);
		    mv.addObject("errorMessage", msg + ipe.getMessage());
		    mv.setViewName("vectorInfo");
		    return mv;
		} catch (MultipleRecordsFoundException mrfe) {
			String msg = "MultipleRecordsFoundException thrown.  ";
			logger.error(method + " - " + penSerial + " - " + msg, mrfe);
		    mv.addObject("errorMessage", msg + mrfe.getMessage());
		    mv.setViewName("vectorInfo");
		    return mv;
		}
		mv.addObject("backURL", backURL);
		mv.addObject("penSerial", penSerial);
		mv.setViewName("vectorInfo");
		return mv;
	}
	
   @RequestMapping(value = "/registration/history/email/{email}", method = RequestMethod.GET)
    public ModelAndView warrantyAndHistoryByEmail(HttpServletResponse response,
            @PathVariable("email") String email,
            @RequestParam(value="backURL", required=false) String backURL) throws Exception {
       ModelAndView mv = new ModelAndView();
       RegistrationClient client = new RegistrationClient();
       try {
           RegistrationHistoryListResponse res = client.findRegistrationHistoryByEmail(email);
           List<RegistrationHistoryDTO> registrationHistory = res.getRegistrationHistories();
           mv.addObject("registrations", registrationHistory);
       } catch (RegistrationNotFoundException e) {
           logger.error("Registration not found for user " + email, e);
       }
       try {
           WarrantyListResponse warrantyRes = client.findWarrantyByEmail(email);
           List<WarrantyDTO> warrantyDTO = warrantyRes.getWarranties();
           mv.addObject("warranties", warrantyDTO);
       } catch (RegistrationNotFoundException e) {
           logger.error("Registration not found for user " + email, e);
       }
       mv.addObject("backURL", backURL);
       mv.addObject("email", email);
       mv.setViewName("vectorInfo");
       return mv;
    }
	    
}

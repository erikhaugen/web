package com.livescribe.servicelocator.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.servicelocator.LSServiceLocatorManager;

@Controller
public class ServiceLocatorController {
	
	private String xsdContents = "";
	
	@RequestMapping("/services.xml")
	public ModelAndView servicesXmlListing(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("xml", LSServiceLocatorManager.getInstance().getServicesDocument());
		
		return new ModelAndView("xmlView", model);
	}
	
	@RequestMapping("/servicelocator.xsd")
	public ModelAndView servicesXsd(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String error = null;
		if ( xsdContents.equals("") ) {
			InputStream is = getClass().getResourceAsStream("/servicelocator.xsd");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			try {
				while ( (line=reader.readLine()) != null ){
					xsdContents = xsdContents + line;
				}
			} catch ( IOException iox ) {
				error = "There was an error processing your request. Please try again later.";
			}
		}
		PrintWriter writer = response.getWriter();
		if ( error != null ) {
			writer.append(error);
		} else {
			writer.append(xsdContents);
		}
		
		return null;
	}
	
	@RequestMapping("/services.admin")
	public ModelAndView servicesView(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("xml", LSServiceLocatorManager.getInstance().getServicesDocument());
		
		return new ModelAndView("xmlView", model);
	}
	
	@RequestMapping("/services.refresh")
	public ModelAndView servicesRefresh(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO : Please add an authentication scheme 
		// here when LSAuthenticationService is "Ready For Prime"
		boolean result = LSServiceLocatorManager.getInstance().refreshServices();
		PrintWriter writer = response.getWriter();
		if ( result ) {
			writer.append("The services have been successfully reloaded.");
		} else {
			writer.append("Services cannot be reloaded, please restart the application to reload.");
		}
		return null;
	}
}

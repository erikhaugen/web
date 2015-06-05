package com.livescribe.aws.tokensvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.aws.tokensvc.response.ErrorResponse;
import com.livescribe.aws.tokensvc.response.ResponseCode;
import com.livescribe.aws.tokensvc.response.ServiceResponse;

@Controller
@RequestMapping("/penSetup.htm")
public class PenRegistrationController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response) {
		
		return new ModelAndView("setupPenView");
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView onSubmit(@RequestParam("code") String code, @RequestParam("penName") String penName, 
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = null;
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext("token-context.xml");
		
		TokenServiceController tkController = (TokenServiceController)appContext.getBean("tokenServiceController");
		
		mv = tkController.registerDeviceWithCode(code, penName, request);
		
		ServiceResponse res = (ServiceResponse)mv.getModel().get("response");
		if (res == null) {
			res = (ErrorResponse)mv.getModel().get("errResponse");
		}
		
		if (res.getResponseCode() == ResponseCode.SUCCESS) {
			return new ModelAndView("connectPenView");
			
		} else {
			return new ModelAndView("setupPenView", "errResponse", res);
			
		}
	}
}

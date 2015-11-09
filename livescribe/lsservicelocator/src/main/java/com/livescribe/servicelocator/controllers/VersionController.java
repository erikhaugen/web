/*
 * Created:  Mar 28, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.servicelocator.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.framework.services.VersionService;
import com.livescribe.framework.services.response.VersionResponse;
import com.livescribe.lsconfig.LSConfigurationFactory;
import com.livescribe.servicelocator.ConfigClient;
import com.livescribe.servicelocator.dao.DaoFactory;

/**
 * <p>Handles requests for version information.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Controller
public class VersionController {

	protected Logger logger = Logger.getLogger(this.getClass().getName());
		
	private static String VIEW_VERSION				= "versionView";
	
	private VersionService versionService;
	
	/**
	 * <p></p>
	 */
	public VersionController() {
		versionService = LSConfigurationFactory.getBean(ConfigClient.getName(), "versionService", VersionService.class);
	}

	/**
	 * <p></p>
	 * 
	 * @param req
	 * @param resp
	 * 
	 * @return
	 */
	@RequestMapping(value = "/version")
	public ModelAndView getVersion(HttpServletRequest req, HttpServletResponse resp) {
		
		ModelAndView mv = new ModelAndView();
		
		VersionResponse response = versionService.getVersion(req);

		mv.setViewName(VIEW_VERSION);

		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);

		return mv;
	}

	/**
	 * @return the versionService
	 */
	public VersionService getVersionService() {
		return versionService;
	}

	/**
	 * @param versionService the versionService to set
	 */
	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	}
}

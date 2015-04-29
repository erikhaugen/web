/*
 * Created:  Mar 28, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.version.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.framework.lsconfiguration.AppProperties;
import com.livescribe.framework.lsconfiguration.Env;
import com.livescribe.framework.version.dto.VersionDTO;
import com.livescribe.framework.version.exception.JarManifestNotFoundException;
import com.livescribe.framework.version.response.AllConfigResponse;
import com.livescribe.framework.version.response.ConfigResponse;
import com.livescribe.framework.version.response.VersionResponse;
import com.livescribe.framework.version.response.ConfigResponse.Config;
import com.livescribe.framework.version.service.VersionService;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;

/**
 * <p>
 * Handles requests for version information.
 * </p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Controller
public class ConfigurationController {

	protected Logger logger = Logger.getLogger(this.getClass().getName());

	private static String VIEW_ERROR = "errorView";
	private static String VIEW_VERSION = "versionView";

	@Autowired
	private AppProperties appProperties;

	/**
	 * <p>
	 * </p>
	 */
	public ConfigurationController() {

	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param req
	 * @param resp
	 * 
	 * @return
	 */
	@RequestMapping(value = "/configInfo")
	public ModelAndView getConfigs(HttpServletRequest req,
			HttpServletResponse resp) {

		logger.debug("getConfigInfo");
		String filter = req.getParameter("filter");
		String noGlobalProps = req.getParameter("noGlobal");
		
		ModelAndView mv = new ModelAndView();

		ConfigResponse response = new ConfigResponse();

		if (appProperties.getRunningEnvironment() != Env.PRODUCTION) {
			response.setEnv(appProperties.getRunningEnvironment().getEnvName());

			Configuration confs = appProperties.getConfiguration();

			Iterator keys = confs.getKeys(response.getEnv());
			while (keys.hasNext()) {
				String key = (String) keys.next();
				
				//filter our password/secret stuffs
				String key1 = key.toLowerCase();
    				if (key1.contains("pwd") || key1.contains("passwd") 
    					|| key1.contains("password") || key1.contains("secret"))
    					continue;
				
				key1 = key.replace(response.getEnv() + ".", "");
				if (filter != null && !key1.contains(filter))
					continue;

				response.getConfigs().add(
						new Config(key1, (String) confs.getProperty(key)));
			}

			if (noGlobalProps == null || !noGlobalProps.equals("true")) {
				keys = confs.getKeys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					
					//filter our password/secret stuffs
					String key1 = key.toLowerCase();
	    				if (key1.contains("pwd") || key1.contains("passwd") 
	    					|| key1.contains("password") || key1.contains("secret"))
	    					continue;
					
					if (!key.startsWith(Env.LOCAL.getEnvName())
							&& !key.startsWith(Env.DEVELOPMENT.getEnvName())
							&& !key.startsWith(Env.QA.getEnvName())
							&& !key.startsWith(Env.STAGE.getEnvName())
							&& !key.startsWith(Env.PRODUCTION.getEnvName())) {
						if (filter != null && !key.contains(filter))
							continue;
	
						response.getConfigs().add(
								new Config(key, (String) confs.getProperty(key)));
					}
				}
			}
		}

		mv.setViewName(VIEW_VERSION);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);

		return mv;
	}

	@RequestMapping(value = "/allConfigInfo")
	public ModelAndView getAllConfigs(HttpServletRequest req,
			HttpServletResponse resp) {
		logger.debug("getAllConfigs");
		String filter = req.getParameter("filter");
		String envFilter = req.getParameter("envFilter");

		ModelAndView mv = new ModelAndView();

		AllConfigResponse response = new AllConfigResponse();

		Set<String> keyset = new HashSet<String>();

		if (appProperties.getRunningEnvironment() != Env.PRODUCTION) {
			Configuration confs = appProperties.getConfiguration();

			for (Env env : Env.values()) {
				if (envFilter != null && !envFilter.contains(env.getEnvName()))
					continue;
				
				Iterator keys = confs.getKeys(env.getEnvName());
				ConfigResponse config = new ConfigResponse();
				config.setEnv(env.getEnvName());

				while (keys.hasNext()) {
					String key = (String) keys.next();
					keyset.add(key);
					
					//filter our password/secret stuffs
					String key1 = key.toLowerCase();
	    				if (key1.contains("pwd") || key1.contains("passwd") 
	    					|| key1.contains("password") || key1.contains("secret"))
	    					continue;
					
					key1 = key.replace(config.getEnv() + ".", "");
					if (filter != null && !key1.contains(filter))
						continue;
					config.getConfigs().add(
							new Config(key1, (String) confs.getProperty(key)));
				}
				response.getAllConfigs().add(config);
			}

			if (envFilter == null || envFilter.contains("global")) {
				ConfigResponse config = new ConfigResponse();
				config.setEnv("global");
				response.getAllConfigs().add(config);
				Iterator keys = confs.getKeys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					
					//filter our password/secret stuffs
					String key1 = key.toLowerCase();
	    				if (key1.contains("pwd") || key1.contains("passwd") 
	    					|| key1.contains("password") || key1.contains("secret"))
	    					continue;
					
					if (!keyset.contains(key)) {
						if (filter != null && !key.contains(filter))
							continue;
	
						config.getConfigs().add(
								new Config(key, (String) confs.getProperty(key)));
					}
				}
			}
		}

		mv.setViewName(VIEW_VERSION);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);

		return mv;
	}

}

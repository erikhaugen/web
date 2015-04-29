/*
 * Created:  Mar 28, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.version.controller;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Container;
import org.apache.catalina.Engine;
import org.apache.catalina.Server;
import org.apache.catalina.ServerFactory;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.framework.version.dto.VersionDTO;
import com.livescribe.framework.version.exception.JarManifestNotFoundException;
import com.livescribe.framework.version.response.VersionResponse;
import com.livescribe.framework.version.service.VersionService;
import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;

/**
 * <p>Handles requests for version information.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Controller
public class VersionController {

	protected Logger logger = Logger.getLogger(this.getClass().getName());
		
	private static String VIEW_ERROR				= "errorView";
	private static String VIEW_VERSION				= "versionView";
	
	@Autowired
	private VersionService versionService;

    private VersionResponse response;

	/**
	 * <p></p>
	 */
	public VersionController() {
		
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
		
		logger.debug("getVersion():  ");
		
		ModelAndView mv = new ModelAndView();
		
		if (response == null) {
            VersionDTO version = null;
            try {
                version = versionService.getVersion(req);
                logger.debug(version);
            } catch (JarManifestNotFoundException jmnfe) {
                String msg = jmnfe.getMessage();
                ErrorResponse response = new ErrorResponse(
                        ResponseCode.SERVER_ERROR, msg);
                mv.setViewName(VIEW_ERROR);
                mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response",
                        response);
                return mv;
            } catch (IOException ioe) {
                String msg = ioe.getMessage();
                ErrorResponse response = new ErrorResponse(
                        ResponseCode.SERVER_ERROR, msg);
                mv.setViewName(VIEW_ERROR);
                mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response",
                        response);
                return mv;
            }

            String localhostname = "unknown";
            try {
                localhostname = java.net.InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int serverPort = -1;
            String jvmRoute = null;
            Server server = ServerFactory.getServer();
            Service[] services = server.findServices();
            //we only have one service
            for (Service service : services) {
                for (Connector connector : service.findConnectors()) {
                    if (connector.getProtocolHandlerClassName().contains("Http11")) {
                        serverPort = connector.getPort();
                    }
                }
                Engine engine = (Engine) service.getContainer();
                jvmRoute = engine.getJvmRoute();
            }
            response = new VersionResponse(version, localhostname, serverPort, jvmRoute == null?"unknown":jvmRoute);
		}
		mv.setViewName(VIEW_VERSION);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);

		return mv;
	}

//	/**
//	 * @return the versionService
//	 */
//	public VersionService getVersionService() {
//		return versionService;
//	}
//
//	/**
//	 * @param versionService the versionService to set
//	 */
//	public void setVersionService(VersionService versionService) {
//		this.versionService = versionService;
//	}
}

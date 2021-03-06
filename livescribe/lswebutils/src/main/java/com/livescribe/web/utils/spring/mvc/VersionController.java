/**
 * Created:  Dec 20, 2010 11:14:56 AM
 */
package com.livescribe.web.utils.spring.mvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Controller
public class VersionController {

	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static final String META_INF_MANIFEST_MF = "META-INF/MANIFEST.MF";
	private static final String WEB_INF_LIB_DIR		 = "WEB-INF/lib";
	
	private static String VIEW_VERSION				= "versionView";

	private static final String BUILD_DATE			= "Build-Date";
	private static final String SVN_REVISION		= "SVN-Revision";
	private static final String HUDSON_BUILD_NUMBER	= "Hudson-Build-Number";
	
	private static final String NODE_GROUP_ID		= "groupId";
	private static final String NODE_ARTIFACT_ID	= "artifactId";
	private static final String NODE_VERSION		= "version";
	
	private static final String GROUP_ID_LIVESCRIBE = "com.livescribe";
	
	/**
	 * <p></p>
	 * 
	 */
	public VersionController() {
	}

	@RequestMapping(value = "/version")
	public ModelAndView getVersion(HttpServletRequest req, HttpServletResponse resp) {
		
		ModelAndView mv = new ModelAndView();
		
		Manifest mf = new Manifest();
		ServletContext context = req.getSession().getServletContext();
		String appRoot = context.getRealPath("/");
		String name = context.getServletContextName();
		File manifestFile = new File(appRoot, META_INF_MANIFEST_MF);
		try {
			mf.read(new FileInputStream(manifestFile));
		}
		catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		Attributes mainAttributes = mf.getMainAttributes();
		
		String version = String.valueOf(mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION));
		String buildDate = String.valueOf(mainAttributes.getValue(BUILD_DATE));
		String svnRevision = String.valueOf(mainAttributes.getValue(SVN_REVISION));
		String hudsonBuildNumber = String.valueOf(mainAttributes.getValue(HUDSON_BUILD_NUMBER));
		
		mv.setViewName(VIEW_VERSION);
		VersionResponse response = new VersionResponse();
		response.setName(name);
		response.setBuildDate(buildDate);
		response.setHudsonBuildNumber(hudsonBuildNumber);
		response.setSvnRevision(svnRevision);
		response.setVersion(version);
		
//		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "version", version);
//		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "buildDate", buildDate);
//		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "svnRevision", svnRevision);
//		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "hudsonBuildNumber", hudsonBuildNumber);
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + "response", response);
		
		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param appRoot
	 * @return
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private List<VersionResponse> getLivescribeDependencies(String appRoot) throws ParserConfigurationException, SAXException, IOException {
		
//		ArrayList<String> dependencies = new ArrayList<String>();
		ArrayList<VersionResponse> dependencies = new ArrayList<VersionResponse>();
		
		File pomFile = new File(appRoot, "pom.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(pomFile);
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getElementsByTagName("dependency");
		int nodeCount = nodeList.getLength();
		for (int i = 0; i < nodeCount; i++) {
			Node node = nodeList.item(i);
			NodeList details = node.getChildNodes();
			String groupId = details.item(0).getNodeValue();
			if (NODE_GROUP_ID.equals(groupId)) {
				if (GROUP_ID_LIVESCRIBE.equals(groupId)) {
					String artifactId = details.item(1).getNodeValue();
					String version = details.item(1).getNodeValue();
					String jarName = artifactId + "-" + version + ".jar";
					VersionResponse depResp = getDependencyDetails(appRoot + "/" + WEB_INF_LIB_DIR, jarName);
					if (depResp != null) {
						depResp.setName(jarName);
						dependencies.add(depResp);
					}
				}
			}
		}
		return dependencies;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param path
	 * @param jarName
	 * 
	 * @return
	 */
	private VersionResponse getDependencyDetails(String path, String jarName) {
		
		String method = "getDependencyDetails():  ";
		
		File jarPath = new File(path, jarName);
		if (!jarPath.exists()) {
			//	TODO:  Return an error/exception!
			logger.debug(method + "Could not locate '" + path + "/" + jarName + "'");
		}
		
		VersionResponse response = null;
		
		Manifest mf = null;
		try {
			JarFile jar = new JarFile(path);
			mf = jar.getManifest();
			
			Attributes mainAttributes = mf.getMainAttributes();
			
			String version = String.valueOf(mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION));
			String buildDate = String.valueOf(mainAttributes.getValue(BUILD_DATE));
			String svnRevision = String.valueOf(mainAttributes.getValue(SVN_REVISION));
			String hudsonBuildNumber = String.valueOf(mainAttributes.getValue(HUDSON_BUILD_NUMBER));
			
			response = new VersionResponse();
			response.setName(jarName);
			response.setBuildDate(buildDate);
			response.setHudsonBuildNumber(hudsonBuildNumber);
			response.setSvnRevision(svnRevision);
			response.setVersion(version);
			
		}
		catch (IOException ioe) {
			logger.debug(method + "IOException thrown while attempting to get JarFile manifest for '" + path + "/" + jarName + "'");
			ioe.printStackTrace();
		}

		return response;
	}
}

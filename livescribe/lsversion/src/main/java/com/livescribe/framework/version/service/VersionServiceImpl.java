/*
 * Created:  Mar 30, 2011
 *      By:  kmurdoff
 */
package com.livescribe.framework.version.service;

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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.livescribe.framework.lsconfiguration.AppProperties;
import com.livescribe.framework.lsconfiguration.Env;
import com.livescribe.framework.orm.versions.Version;
import com.livescribe.framework.orm.versions.VersionHistory;
import com.livescribe.framework.version.config.VersionConstants;
import com.livescribe.framework.version.dao.CustomVersionDao;
import com.livescribe.framework.version.dao.CustomVersionHistoryDao;
import com.livescribe.framework.version.dto.VersionDTO;
import com.livescribe.framework.version.exception.JarManifestNotFoundException;
import com.livescribe.framework.version.response.VersionResponse;
import com.livescribe.framework.version.tx.VersionTxCallback;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Component("versionService")
public class VersionServiceImpl implements VersionService, ApplicationListener<ContextRefreshedEvent>, ServletContextAware, VersionConstants {

	protected Logger logger = Logger.getLogger(this.getClass().getName());

    private Manifest mf;

    @Autowired
    private ServletContext servletContext;
	
	private static final String META_INF_MANIFEST_MF = "META-INF/MANIFEST.MF";
//	private static final String META_INF_MANIFEST_MF = "MANIFEST.MF";
	private static final String WEB_INF_LIB_DIR		 = "WEB-INF/lib";


	@Autowired
	private AppProperties appProperties;
	
    @Autowired
    private CustomVersionDao versionDao;

    @Autowired
    private CustomVersionHistoryDao versionHistoryDao;

//    @Autowired
//    @Qualifier("txVersions")
//    protected PlatformTransactionManager txManager;

    @Autowired
    private TransactionTemplate txTmpl;
    
    @Autowired
    private String hostname;
    
    private String appName;
    private Env env;
    private static boolean isVersionPosted = false;
    
    /**
	 * 
	 */
	public VersionServiceImpl() {
		
		logger.debug("VersionService construction complete.");
	}

	/**
	 * <p>Gets the version information from the running Service&apos;s
	 * manifest file.</p>
	 * 
	 * <p>This method is used during startup to find the version information.  
	 * It uses the <code>ServletContext</code> set during startup.</p>
	 * 
	 * @return the version information of the running Service.
	 * @throws JarManifestNotFoundException 
	 * @throws IOException 
	 */
	public Version init() throws IOException, JarManifestNotFoundException {
		
		if (mf == null) {
			String appRoot = this.servletContext.getRealPath("/");
			mf = readManifest(appRoot);
		}
		
		Attributes mainAttributes = mf.getMainAttributes();
		VersionDTO versionDto = new VersionDTO(mainAttributes);
		
		return null;
	}
	
	public VersionResponse getVersion(String appRootPath) throws IOException, JarManifestNotFoundException {
		
        if (mf == null) {
            mf = readManifest(appRootPath);
        }

		Attributes mainAttributes = mf.getMainAttributes();
		
		String version = String.valueOf(mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION));
		String buildDate = String.valueOf(mainAttributes.getValue(BUILD_DATE));
		String svnRevision = String.valueOf(mainAttributes.getValue(SVN_REVISION));
		String hudsonBuildNumber = String.valueOf(mainAttributes.getValue(HUDSON_BUILD_NUMBER));
		
//		VersionResponse response = new VersionResponse(ResponseCode.OK);
		VersionResponse response = new VersionResponse();
		response.setBuildDate(buildDate);
		response.setBuildNumber(hudsonBuildNumber);
		response.setScmRevision(svnRevision);
		response.setVersion(version);

		return response;
	}
	
	public VersionDTO getVersion(HttpServletRequest req) throws JarManifestNotFoundException, IOException {
		
		if (mf == null) {
            mf = new Manifest();
            ServletContext context = req.getSession().getServletContext();
            String appRoot = context.getRealPath("/");
            mf = readManifest(appRoot);
		}

		Attributes mainAttributes = mf.getMainAttributes();
		VersionDTO versionDTO = new VersionDTO(mainAttributes);
		
		return versionDTO;
	}

	/**
	 * <p></p>
	 * 
	 * @param appRoot
	 * @throws IOException
	 * @throws JarManifestNotFoundException
	 */
	private Manifest readManifest(String appRoot) throws IOException, JarManifestNotFoundException {
		
		File manifestFile = new File(appRoot, META_INF_MANIFEST_MF);
		FileInputStream is = null;
		Manifest mf = new Manifest();
		
		try {
		    is = new FileInputStream(manifestFile);
		    mf.read(is);
		} catch (FileNotFoundException fnfe) {
		    String msg = "Unable to locate '" + appRoot + "/" + META_INF_MANIFEST_MF;
		    logger.error(msg, fnfe);
		    throw new JarManifestNotFoundException(msg);
		} finally {
		    if (is != null) {
		        try {
		            is.close();
		        } catch (IOException e) {
		            logger.error("cannot close file stream of MANIFEST.MF", e);
		        }
		    }
		}
		return mf;
	}
	
	//	To be used to get version information of dependency JARs.
	private VersionResponse getVersion(JarFile jarFile) {
		
		try {
			Manifest mf = jarFile.getManifest();
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * <p></p>
	 * 
	 * To be used to get version information of dependency JARs.
	 * 
	 * @param appRoot
	 * 
	 * @return
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private List<VersionResponse> getLivescribeDependencies(String appRoot) throws ParserConfigurationException, SAXException, IOException {
		
		ArrayList<VersionResponse> dependencies = new ArrayList<VersionResponse>();
		
		//	Read in the 'pom.xml' file.
		File pomFile = new File(appRoot, "pom.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(pomFile);
		doc.getDocumentElement().normalize();
		
		//	Find the 'dependency' nodes.
		NodeList nodeList = doc.getElementsByTagName("dependency");
		
		//	For each dependency ...
		int nodeCount = nodeList.getLength();
		for (int i = 0; i < nodeCount; i++) {
			Node node = nodeList.item(i);
			NodeList details = node.getChildNodes();
			String groupId = details.item(0).getNodeValue();
			if (NODE_GROUP_ID.equals(groupId)) {
				
				//	... find the 'com.livescribe' groupId and add another
				//	VersionResponse to the list of dependencies.
				if (GROUP_ID_LIVESCRIBE.equals(groupId)) {
					String artifactId = details.item(1).getNodeValue();
					String version = details.item(1).getNodeValue();
					String jarName = artifactId + "-" + version + ".jar";
					VersionResponse depResp = getDependencyDetails(appRoot + "/" + WEB_INF_LIB_DIR, jarName);
					if (depResp != null) {
						depResp.setAppName(jarName);
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
			
//			response = new VersionResponse(ResponseCode.OK);
			response = new VersionResponse();
			response.setAppName(jarName);
			response.setBuildDate(buildDate);
			response.setBuildNumber(hudsonBuildNumber);
			response.setScmRevision(svnRevision);
			response.setVersion(version);
			
		}
		catch (IOException ioe) {
			logger.debug(method + "IOException thrown while attempting to get JarFile manifest for '" + path + "/" + jarName + "'");
			ioe.printStackTrace();
		}

		return response;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.framework.version.service.VersionService#postVersion()
	 */
//	@PostConstruct
	@Transactional(value = "txVersions", propagation = Propagation.REQUIRED)
	public void postVersion() {
		
		String method = "postVersion()";
		
		logger.info("BEFORE - " + method);
		
		env = appProperties.getRunningEnvironment();
		String realPath = this.servletContext.getRealPath("/");
		logger.debug(method + " - realPath: " + realPath);
		
		Manifest mf = null;
		try {
			mf = readManifest(realPath);
		} catch (IOException ioe) {
			String msg = "IOException thrown when attempting to read Manifest file:  " + realPath;
			logger.error(method + " - " + msg);
			return;
		} catch (JarManifestNotFoundException jmnfe) {
			String msg = "JarManifestNotFoundException thrown when attempting to read Manifest file:  " + realPath;
			logger.error(method + " - " + msg);
			return;
		}
		
		if (mf == null) {
			logger.error(method + " - Unable to read Manifest file.  Version information unavailable to update database.");
			return;
		}
		
		Attributes mainAttributes = mf.getMainAttributes();
		VersionDTO versionDto = new VersionDTO(mainAttributes);
		versionDto.setDeployEnv(env.getEnvName());
		versionDto.setAppServer(hostname);
		
		logger.debug(method + " - " + versionDto.toString());
		
		String servletContextName = this.servletContext.getServletContextName();
		String contextName = this.servletContext.getContextPath();
		
		//--------------------------------------------------
		//	Look up existing version.
		appName = mainAttributes.getValue(APP_NAME);

//		List<Version> versionList = versionDao.findByAppEnvironmentServer(appName, env, hostname);
		
//		txTmpl.execute(new TransactionCallbackWithoutResult() {
//		VersionTxCallback vtcbk = new VersionTxCallback(appName, env);
//		txTmpl.execute(new VersionTxCallback() {
//				
//			}
//			List<Version> versionList = null;
//					
//			@Override
//			protected void doInTransactionWithoutResult(TransactionStatus status) {
//				versionList = versionDao.findByAppEnvironmentServer(appName, env, hostname);
//			}
//		});
		
//		List<Version> versionList = txTmpl.execute(vtcbk);
		
		List<Version> versionList = txTmpl.execute(new VersionTxCallback() {
			@Override
			public List<Version> doInTransaction(TransactionStatus status) {
				List<Version> versionList = versionDao.findByAppEnvironmentServer(appName, env, hostname);
				return versionList;
			}
		});
		
		//--------------------------------------------------
		//	'First-time' deployment.
		if ((versionList == null) || (versionList.isEmpty())) {
			
			logger.info(method + " - First-time deployment of " + appName + ".");
			
			final Version version = VersionFactory.create(versionDto);
			
			txTmpl.execute(new TransactionCallbackWithoutResult() {
				
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					versionDao.persist(version);
				}
			});
			
			isVersionPosted = true;
			return;
			
		//--------------------------------------------------
		//	Handle multiple versions from database.
		} else if (versionList.size() > 1) {
			
			logger.info(method + " - Found " + versionList.size() + " versions of " + appName + " in the database.");
			
			for (Version ver : versionList) {
				
				//	If the current Version (ver) matches the one read from the 
				//	manifest ...
				if ((ver.getAppVersion().equals(versionDto.getVersion())) 
						&& (ver.getBuildNumber().equals(versionDto.getBuildNumber()))
						&& (ver.getSvnRevision().equals(versionDto.getScmRevision()))) {
					
					//	TODO:  Save 'restart'
					
				} else {
					
					//	Copy 'version' to 'version_history'.
					
					//	INSERT new 'version'.
				}
			}
			//	TODO:  ???
		
			//--------------------------------------------------
			//	Copy the current version into the 'version_history' table.
			Version versionRecord = versionList.get(0);
			VersionHistory versionHistory = VersionHistoryFactory.create(versionRecord);
			versionHistoryDao.persist(versionHistory);
		
		} else {
			logger.info(method + " - Found single version of " + appName + " in the database.");
			//	TODO:  ???
		}
		
		
		
		
		logger.info("AFTER - " + method);
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
	 */
	@Override
	public void setServletContext(ServletContext servletContext) {
		
		String method = "setServletContext()";
		
		this.servletContext = servletContext;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		String method = "onApplicationEvent()";
		
		logger.debug(method + " - ContextRefreshedEvent occurred.");
		
		//	If context is refreshed AFTER startup, do not repost the
		//	version information.
		if (!isVersionPosted) {
			postVersion();
		}
	}
}

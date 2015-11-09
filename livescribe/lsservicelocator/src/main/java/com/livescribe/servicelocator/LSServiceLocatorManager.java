package com.livescribe.servicelocator;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;

import com.livescribe.base.UnableToCompleteOperationException;
import com.livescribe.base.constants.LSConstants;
import com.livescribe.base.utils.MiscEncryptionUtils;
import com.livescribe.framework.services.request.RequestValidation;
import com.livescribe.framework.services.response.ServiceResponse;
import com.livescribe.framework.services.response.ServiceResponse.ResponseCode;
import com.livescribe.lsconfig.LSConfigurationFactory;
import com.livescribe.servicelocator.dao.DaoFactory;
import com.livescribe.servicelocator.dao.ServiceLocatorDao;
import com.livescribe.servicelocator.dao.data.ServiceLocator;

public class LSServiceLocatorManager {

	private static final Logger logger = Logger.getLogger(LSServiceLocatorManager.class);

	private static final LSServiceLocatorManager _self = new LSServiceLocatorManager();
	
	private List<ServiceLocator> serviceLocators;
	
	private Document servicesDocument;
	
	private ServiceLocatorDao serviceLocatorDao;

	private LSServiceLocatorManager() {
		init();
	}
	
	public static LSServiceLocatorManager getInstance() {
		return _self;
	}
	
	public Document getServicesDocument() {
		return this.servicesDocument;
	}
	
	private void init() {
		DaoFactory daoFactory = LSConfigurationFactory.getBean(ConfigClient.getName(), "daoFactory", DaoFactory.class); 
		serviceLocatorDao = daoFactory.getServiceLocatorDao();
//		initServiceListings();
//		initServicesDocument();
		initServiceLocators();			
	}
	
	private void initServiceLocators() {
		try {
			serviceLocators = Collections.unmodifiableList(serviceLocatorDao.getAllServices());
			
			if (serviceLocators != null) {
				logger.debug("Number of services found in DB:  " + serviceLocators.size());
			}
			
			DocumentFactory factory = DocumentFactory.getInstance();
			
			// create the Root Element
			Element documentRoot = factory.createElement("Services");
			
			// create the document
			servicesDocument = factory.createDocument(documentRoot);
			// set encoding
			servicesDocument.setXMLEncoding("utf-8");
			
			// set name space on root element
			//documentRoot.add(new Namespace("", "http://www.livescribe.com/services/locator"));
			documentRoot.add(new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
			documentRoot.addAttribute("xsi:schemaLocation", "http://www.livescribe.com/services/locator http://www.livescribe.com/services/locator/servicelocator.xsd");
			
			// Add the services one by one
			
			for ( ServiceLocator locator : serviceLocators ) {
				if ( locator.isActive() ) {
					Element serviceElement = factory.createElement("Service");			
				
					serviceElement.add(factory.createElement("Name").addText(locator.getName()));
					serviceElement.add(factory.createElement("Domain").addText(locator.getDomain()));
					serviceElement.add(factory.createElement("URI").addText(locator.getUri()));
					serviceElement.add(factory.createElement("Protocol").addText(locator.getProtocol()));
				
					documentRoot.add(serviceElement);
				}
			}
		} catch (UnableToCompleteOperationException e) {
			logger.fatal("Unable to load Service Listings from database. Terminating.", e);
			throw new RuntimeException("Unable to get Service listings from the database. Aborting start up. ", e);
		}	
	}
	
	public List<ServiceLocator> getAllServices() {
		return serviceLocators;
	}

	public boolean refreshServices() {
		if ( ConfigClient.getCanRefresh() ) {
			init();
			return true;
		}
		return false;
	}

	public ServiceResponse verifySignature(String input, String id, String signature) throws Exception {
		logger.debug("verifySignature(String input, String id, String signature) invoked");
		ServiceResponse response = null;
		/* Validate input parameter is provided */
		logger.debug("verifySignature(String input, String id, String signature) Validate input parameter is provided");
		response = RequestValidation.validateParamNotMissing("input", input);
		/* Validate id parameter is provided */
		logger.debug("verifySignature(String input, String id, String signature) Validate id parameter is provided");
		if (response == null) response = RequestValidation.validateParamNotMissing("id", id);
		/* Validate signature parameter is provided */
		logger.debug("verifySignature(String input, String id, String signature) Validate signature parameter is provided");
		if (response == null) response = RequestValidation.validateParamNotMissing("signature", signature);
		if (response != null) logger.error("verifySignature(String input, String id, String signature) error:\t" + response.getServerMessage());
		if ( response == null ) {
			//Get sharedSecretKey for id
			//Right now it will be a system property defined at launch
			String idSecretKey = System.getProperty(LSConstants.SHARED_SECRET_RUNTIME_KEY);
			//create secondSignature of the input based on sharedSecretKey
			String secondSignature = MiscEncryptionUtils.getBase64HMacSha1Signature(input, idSecretKey);
			//Compare secondSignature to signature
			if (secondSignature.equals(signature)){
				//return true if match
				logger.debug("verifySignature(String input, String id, String signature) authenticatRequest success");
				response = new ServiceResponse(ResponseCode.OK, null);
			}
		}
		return response;
	}
}

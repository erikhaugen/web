package com.livescribe.servicelocator;

import java.util.Map;

import org.apache.log4j.Logger;

import com.livescribe.base.anno.DataMapUtility;
import com.livescribe.framework.services.AbstractLSService;
import com.livescribe.framework.services.annotation.LSService;
import com.livescribe.framework.services.annotation.Publish;
import com.livescribe.framework.services.response.ResponseUtils;
import com.livescribe.framework.services.response.ServiceResponse;

@LSService("LSServiceLocator")
public class LSServiceLocatorWebServiceImpl extends AbstractLSService {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private LSServiceLocatorManager manager = LSServiceLocatorManager.getInstance();

	@Publish
	public String ping() {
		return "LSServiceLocatorService is okay";
	}
	
	@Publish
	public Map<String, Object> verifySignature(String input, String id, String signature) {
		logger.info("verifySignature(String input, String id, String signature) invoked");
		try{
			ServiceResponse response = manager.verifySignature(input, id, signature);
			logger.info("verifySignature(String input, String id, String signature) completed");
			return DataMapUtility.convertToMap(response);
		} catch (Exception e) {
			logger.error("verifySignature(String input, String id, String signature) error:\t" + e.getMessage());
			return ResponseUtils.returnServerErrorResponse("", e);
		}
		
	}
}

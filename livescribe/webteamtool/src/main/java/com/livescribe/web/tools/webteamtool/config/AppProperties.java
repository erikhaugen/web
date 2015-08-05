package com.livescribe.web.tools.webteamtool.config;

import org.apache.log4j.Logger;

public class AppProperties extends com.livescribe.framework.lsconfiguration.AppProperties {

	private	Logger _logger = Logger.getLogger(AppProperties.class);
	
	private static AppProperties appProperties = null;
	
	static public AppProperties getInstance() {
		if (appProperties == null) {
			appProperties = new AppProperties();
		}
        return appProperties;
    }
	
	/**
	 * <p></p>
	 * 
	 */
	public AppProperties() {
		super();
		_logger.debug("Init called");
		if (appProperties != null)
            throw new IllegalStateException("There is already an instance of this class.");
        appProperties = this;
	}
}

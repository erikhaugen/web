package com.livescribe.aws.heartbeat.config;

import org.apache.log4j.xml.DOMConfigurator;

/**
 * <p></p>
 * 
 * @author <a href="mailto:mnaqvi@livescribe.com">Mohammad M Naqvi</a>
 * @version 1.0
 */
public class AppProperties extends com.livescribe.framework.lsconfiguration.AppProperties {

	
	/**
	 * <p></p>
	 *
	 */
	public AppProperties() {
		super();
		DOMConfigurator.configureAndWatch("log4j.xml");
	}
}


/**
 * 
 */
package com.livescribe.community.config;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.livescribe.lsconfig.LSConfiguration;
import com.livescribe.lsconfig.LSConfigurationFactory;

public class ConfigClient {

	private static final Logger logger = Logger.getLogger(ConfigClient.class);

	private  static LSConfiguration config;

	static {
		logger.info("Starting initialization the Config Client");
		try {
			config = LSConfigurationFactory.getConfiguration("communityconfig.xml");
			config.loadBeans();
			logger.info("Initialized the Config Client, with ENV=" +getEnv());
		} catch ( Exception ex ) {
			logger.info("Exception during initialization of the Config Client " + ex.getMessage());
			logger.debug("", ex);
		}
	}

	public static List<String> getMemcacheServerList(String key) {
		return config.getList("memcache.serverlist."+key, Collections.singletonList("app1-test.pensoft.local:11211"));
	}

	public static int getKeepPencastInCachePeriod() {
		return config.getInt("pencast.cache.interval.default", 15);
	}
	
	public static int getMaxFetchSize() {
		return config.getInt("pencast.list.fetchSize.max", 100);
	}
	
	public static int getDefaultFetchSize() {
		return config.getInt("pencast.list.fetchSize.default", 20);
	}
	
	public static String getActiveUserMemcacheGroupName() {
		return config.getString("memcache.woapps.activeUserMemcacheGroupName", "activeuser");
	}

	public static String getAuthServiceUrl() {
		return config.getString("services.auth.url");
	}
	
	/**
	 * <p>Provides access to a bean from the Spring Application Context being created by the 
	 *  config. Please note that this is different from the Context created by spring if you 
	 *  are using Spring MVC in your web application.</p>
	 * 
	 * @param <T>
	 * @param name
	 * @param clazz
	 * 
	 * @return
	 */
	public static <T> T getBean(String name, Class<T> clazz) {
		return config.getBean(name, clazz);
	}

	/**
	 * <p>Provides access to the environment key being used for the config selection</p>
	 * 
	 * @return
	 */
	public static String getEnv() {
		return config.getConfigurationContext().getEnv();
	}
	
	/**
	 * <p>Returns the URL where user&apos;s must login.</p>
	 * 
	 * @return a URL string.
	 */
	public static String getLoginUrl() {
		return config.getString("security.loginUrl");
	}
	
	/**
	 * <p>Returns a <code>List</code> of URLs that require authentication prior to access.</p>
	 * 
	 * @return a list of URL strings.
	 */
	public static List<String> getSecureUrls() {
		return config.getList("security.secureUrls.url");
	}
	
	public static String getSolrUrl() {
		return config.getString("services.solr.url");
	}
	
	public static int getTimeout(String timeoutKey) {
		return config.getInt(timeoutKey);
	}
}

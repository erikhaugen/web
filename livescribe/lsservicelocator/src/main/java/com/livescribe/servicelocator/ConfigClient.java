package com.livescribe.servicelocator;

import java.util.Properties;

import com.livescribe.lsconfig.AbstractConfigClient;

public class ConfigClient extends AbstractConfigClient {

	public static final String configKey = "servicelocator-config.xml";

	private static final ConfigClient _self = new ConfigClient();
	
	private ConfigClient() {
		super(configKey);
	}
   
    public static String getHost(String domain, String hostKey) {
    	return _self.config.getString(domain+"."+hostKey);
    }
    
    public static String getWwwHost() {
    	return _self.config.getString("livescribe.www-host", "www.livescribe.com");
    }
    
    public static String getAdminHost() {
    	return _self.config.getString("livescribe.admin-host", "admin.livescribe.com");
    }
    
    public static Properties getWwwServicesListings() {
    	return _self.config.getProperties("livescribe.services.www-host.service");
    }
    
    public static Properties getAdminServicesListings() {
    	return _self.config.getProperties("livescribe.services.admin-host.service");
    }
    
    public static String getName() {
    	return _self.config.getConfigurationContext().getName();
    }
    
    public static boolean getCanRefresh() {
    	return _self.config.getBoolean("services.allowRefresh", false);
    }
}

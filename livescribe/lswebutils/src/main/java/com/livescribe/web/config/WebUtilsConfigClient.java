package com.livescribe.web.config;

import com.livescribe.lsconfig.LSConfiguration;
import com.livescribe.lsconfig.LSConfigurationFactory;

public class WebUtilsConfigClient {
	public static final String configKey = "webutils-config.xml";

    private static LSConfiguration config;

    static {
    	config = LSConfigurationFactory.getConfiguration(configKey);
    }
    
    public static String getWwwDomainName() {
    	return config.getString("livescribe.domains.www.name");
    }
    
    public static String getWwwAuthUser() {
    	return  config.getString("livescribe.domains.www.auth.user");
    }
    
    public static String getWwwAuthPassword() {
    	return  config.getString("livescribe.domains.www.auth.password");
    }
    
    public static String getAdminDomainName() {
    	return config.getString("livescribe.domains.admin.name");
    }
    
    public static String getAdminAuthUser() {
    	return  config.getString("livescribe.domains.admin.auth.user");
    }
    
    public static String getAdminAuthPassword() {
    	return  config.getString("livescribe.domains.admin.auth.password");
    }
    
}

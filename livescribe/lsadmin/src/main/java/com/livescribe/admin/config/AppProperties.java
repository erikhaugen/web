/*
 * Created:  Sep 7, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.admin.config;

import java.io.File;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * <p>
 * </p>
 * 
 * @author <a href="mailto:tnguyen@livescribe.com">Truong Nguyen</a>
 * @version 1.0
 */
public class AppProperties extends com.livescribe.framework.lsconfiguration.AppProperties {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    static private AppProperties appProperties = null;
    
    public Map<String,String> loginToken = new ConcurrentHashMap<String, String>();

    private AppProperties() {
        super();
        if (appProperties != null)
            throw new IllegalStateException("There is already an instance of this class.");
        appProperties = this;
    }

    static public AppProperties getIntance() {
        return appProperties;
    }

    public String getHostname() {
        try {
            java.net.InetAddress  addr = java.net.InetAddress.getLocalHost();
            String hostname = addr.getHostName();
            return hostname;
        } catch (UnknownHostException e) {
        }
        return "Unknown";
    }
    
    public void setExtendedProperty(String key, String value) {
        System.setProperty(key, value);
    }
    
    public String getExtendedProperty(String key) {
        return System.getProperty(key);
    }
}

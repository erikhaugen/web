/*
 * Created:  Sep 7, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.lsconfiguration;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.livescribe.base.StringUtils;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class AppProperties extends PropertyPlaceholderConfigurer {

	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	private Env env;
	private Properties props;
	private boolean returnGlobalWhenEnvVarIsEmpty;
	private String servletContextName;	
	private long startMilliseconds;	 
	
	/**
	 * <p>Running environment will be determined as follow: get value from environment variable 'env', 
	 * if the value is blank, try to get parameter 'env' of the application, if it is still blank,
	 * running environment is set to dev</p>
	 * 
	 */
	public AppProperties() {
		super();
		
		String envStr = System.getProperty("env");
		if (envStr == null)
			envStr = System.getProperty("ENV");
		
		logger.debug("envStr:  " + envStr);
		
//		We decided not to use environment variable
//		if (StringUtils.isBlank(envStr)) {
//		    envStr = System.getenv("env");
//		    if (envStr == null)
//		    		envStr = System.getenv("ENV");
//		}
		
//		We decided not to use default env
//		if (StringUtils.isBlank(envStr))
//		    envStr = defaultEnv;
		
		env = Env.getEnv(envStr);
		
		logger.debug("env:  " + env);
		
		if (env == null)
			throw new IllegalStateException("Cannot determine running env. " +
					"Please specify -DENV=[local|test|dev|qa|stage|prod] in starting command.");
		
		logger.info("Loading properties for ENV = " + this.env);
	}

	/**
	 * <p>This method is overridden to capture the <code>Properties</code>
	 * object and make use of it elsewhere.</p>
	 * 
	 * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#processProperties(org.springframework.beans.factory.config.ConfigurableListableBeanFactory, java.util.Properties)
	 */
	@Override
	public void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
		this.props = props;
		returnGlobalWhenEnvVarIsEmpty = Boolean.parseBoolean(
                props.getProperty(ConfigKey.RETURN_GLOBAL_WHEN_ENV_EMPTY.getKeyName(), 
                                  ConfigKey.RETURN_GLOBAL_WHEN_ENV_EMPTY.getDefaultValue()));
		logger.info(ConfigKey.RETURN_GLOBAL_WHEN_ENV_EMPTY.getKeyName() + " = " + returnGlobalWhenEnvVarIsEmpty);
		super.processProperties(beanFactoryToProcess, props);
		logger.debug("Loaded " + props.size() + " properties.");
		printProperties(env, null, true);
	}
	
	/**
	 * This method is overridden for resolving env-specific properties 
	 */
	@Override
    protected String resolvePlaceholder(String placeholder, Properties props) {
        String result = super.resolvePlaceholder(this.env + "." + placeholder, props);
        if (returnGlobalWhenEnvVarIsEmpty && result == null)
            result = super.resolvePlaceholder(placeholder, props);
        return result;
    }
	
	/**
	 * <p>Returns a property value for the given key.</p>
	 * 
	 * This method prepends an environment prefix to the given key to 
	 * provide the correct value for the current environment. If it cannot
	 * find specific env value, it will return value of exact key if 
	 * property <code>lsconfiguration.returnGlobalWhenEnvEmpty</code> is set 
	 * to true.
	 *
	 * 
	 *  e.g.  If the key is <code>jdbc.url</code> and this is running in
	 *        &quot;DEV&quot; environment, resulting key 
	 *        <code>dev.jdbc.url</code> is used to look up the value. If property 
	 *        <code>dev.jdbc.url</code> cannot be found and 
	 *        <code>lsconfiguration.returnGlobalWhenEnvEmpty</code> is set to true, 
	 *        property <code>jdbc.url</code> will be looked up. 
	 *         
	 * @param key
	 * 
	 * @return
	 */
	public String getProperty(String key) {
	    return resolvePlaceholder(key, props);
	}
	
	/**
	 * Functioning like <code>getProperty(String)</code>, except that if no value is 
	 * found, <code>defaultValue</code> will be returned.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(String key, String defaultValue){
	    String result = getProperty(key);
	    if (result == null)
	        result = defaultValue;
	    return result;
	}
	
	/**
	 * <p>Calculates the uptime of the servlet context.</p>
	 * 
	 * @param duration The duration in milliseconds.
	 * 
	 * @return a <code>String</code> formatted as:  <days> - <hours> - <minutes> - <seconds>
	 */
	private String calcUptime(long duration) {
		
		long secondsInMillis = 1000;
		long minuteInMillis = secondsInMillis * 60;
		long hourInMillis = minuteInMillis * 60;
		long dayInMillis = hourInMillis * 24;
		
		long days = duration / dayInMillis;
		duration = duration % dayInMillis;
		
		long hours = duration / hourInMillis;
		duration = duration % hourInMillis;
		
		long minutes = duration / minuteInMillis;
		duration = duration % minuteInMillis;
		
		long seconds = duration / secondsInMillis;
		duration = duration % secondsInMillis;
		
		StringBuilder sb = new StringBuilder();
		sb.append(days).append(" - ").append(hours).append(" - ");
		sb.append(minutes).append(" - ").append(seconds);
		
		return sb.toString();
	}

	/**
	 * @return the servletContextName
	 */
	public String getServletContextName() {
		return servletContextName;
	}

	/**
	 * @param servletContextName the servletContextName to set
	 */
	public void setServletContextName(String servletContextName) {
		this.servletContextName = servletContextName;
	}

	/**
	 * Print all properties to log at INFO level
	 */
	
	public void printProperties(){
	    for(Object key: props.keySet()){
	        logger.info(key.toString() + "=" + props.get(key));
	    }
	}
	
	public void printProperties(Env env, String filter, boolean filterPassword){
		logger.info("==========Properties for " + env.getEnvName().toUpperCase() + "==========");
	    for(Object o: props.keySet()){
	    		String key = o.toString();
	    		
	    		if (filterPassword) {
	    			String key1 = key.toLowerCase();
	    			if (key1.contains("pwd") || key1.contains("passwd") 
	    					|| key1.contains("password") || key1.contains("secret"))
	    				continue;
	    		}
	    		
	    		boolean isEnvSpecificProp = false;
	    		for(Env e : Env.values()) {
	    			if (key.startsWith(e.getEnvName())) {
    					isEnvSpecificProp= true;
    					break;
	    			}
	    		}
	    		
	    		if (isEnvSpecificProp) {
		    		if (env != null && !key.startsWith(env.getEnvName()))
		    			continue;
		    		if (filter != null && !key.contains(filter))
		    			continue;
	    		}
	    		logger.info(key + "=" + props.get(key));
	    }
	    logger.info("========End Properties for " + env.getEnvName().toUpperCase() + "========");
	}

	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("\n--------------------------------------------------\n");
		builder.append("    ENV:  " + this.env);
		Set<Object> keys = props.keySet();
		Iterator<Object> keyIter = keys.iterator();
		while (keyIter.hasNext()) {
			Object key = keyIter.next();
			String property = (String)props.get(key);
			for (int i = (46 - property.length()); i > 0; i--) {
				builder.append(" ");
			}
			builder.append(    (String)key + ":  " + property + "\n");
		}
		builder.append("--------------------------------------------------\n");

		return builder.toString();
	}
	
	/**
	 * <p>Returns clone of the properties configured for the application. Do not allow to change
	 * properties after loading.</p>
	 * 
	 * @return the props the properties configured for the application. 
	 */
	public Configuration getConfiguration() {
	    Configuration result = new PropertiesConfiguration();
	    for (Object key : props.keySet()) {
	        result.addProperty(key.toString(), new String(props.get(key).toString()));
        }
		return result;
	}
	/**
	 * 
	 * @return running environment
	 */
	public Env getRunningEnvironment(){
	    return env;
	}
	
	/**
	 * 
	 * @return application name
	 */
	
	public String getAppName(){
	    return getProperty(ConfigKey.APP_NAME.getKeyName());
	}
	
	/**
	 * This method is mostly empty. Currently, LSMUS is the only one need this method.
	 * @return application group name
	 */
	public String getAppGroupName(){
        return getProperty(ConfigKey.APP_GROUP.getKeyName());
    }

	/**
	 * <p>Initializes instance by setting the current system time in milliseconds.</p>
	 * 
	 */
	public void init() {
		
		logger.info("STARTED - " + this.servletContextName + " ");
		
		this.startMilliseconds = System.currentTimeMillis();
	}
	
	/**
	 * <p>Calculates the uptime by subtracting the current system time in milliseconds
	 * from the stored start time in milliseconds.</p>
	 * 
	 */
	public void destroy() {
		
		long endMilliseconds = System.currentTimeMillis();
		long duration = endMilliseconds - this.startMilliseconds;
		String uptime = calcUptime(duration);
		
		logger.info("SHUTDOWN - " + this.servletContextName + " - " + uptime + " [<days> - <hours> - <minutes> - <seconds>]\n\n\n");
	}	
}

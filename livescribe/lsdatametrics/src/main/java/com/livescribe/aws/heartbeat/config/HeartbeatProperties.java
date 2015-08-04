/*
 * Created:  Jul 21, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.heartbeat.config;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class HeartbeatProperties extends PropertyPlaceholderConfigurer {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private String env;
	private Properties props;
	
	/**
	 * <p></p>
	 * 
	 */
	public HeartbeatProperties() {
		
		super();
//		this.env = System.getenv("ENV").toLowerCase();		//	<-- Causes errors.
		this.env = System.getProperty("ENV", "dev");		//	Defaults to DEV environment.
//		this.env = "dev";
		logger.debug("Environment set to '" + env + "'.");
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
		super.processProperties(beanFactoryToProcess, props);
		logger.debug("Loaded " + props.size() + " properties.");
	}
	
	/**
	 * <p>Returns a property value for the given key.</p>
	 * 
	 * This method prepends an environment prefix to the given key to 
	 * provide the correct value for the current environment.
	 * 
	 *  e.g.  If the key is <code>jdbc.url</code> and this is running in a
	 *        &quot;DEV&quot; environment, the resulting key 
	 *        <code>dev.jdbc.url</code> is used to look up the value.
	 *         
	 * @param key
	 * 
	 * @return
	 */
	public String getProperty(String key) {
		
		String envKey = this.env + "." + key;
		return this.props.getProperty(envKey);
	}

	/**
	 * <p></p>
	 * 
	 * @return the props
	 */
	public Properties getProps() {
		return props;
	}

	/**
	 * <p></p>
	 * 
	 * @param props the props to set
	 */
	public void setProps(Properties props) {
		this.props = props;
	}

	/**
	 * <p></p>
	 * 
	 * @return the env
	 */
	public String getEnv() {
		return env;
	}
}

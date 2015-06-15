/**
 * 
 */
package com.livescribe.community.config;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CommunityProperties extends PropertyPlaceholderConfigurer {
	
	private Properties props;
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public CommunityProperties() {}
	
	/**
	 * <p>Returns the value of a property identified by the given key.</p>
	 * 
	 * @param key The unique key to use in locating the property value.
	 * 
	 * @return the property&apos;s value.
	 */
	public String getProperty(String key) {
		
		return props.getProperty(key);
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#processProperties(org.springframework.beans.factory.config.ConfigurableListableBeanFactory, java.util.Properties)
	 */
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
		
		this.props = props;
		super.processProperties(beanFactoryToProcess, props);
	}

}

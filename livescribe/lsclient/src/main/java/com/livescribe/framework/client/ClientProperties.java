/**
 * 
 */
package com.livescribe.framework.client;

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ClientProperties extends Properties {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private String env;

	/**
	 * <p>Default class constructor.</p>
	 * 
	 * Sets the name of the environment using the <code>ENV</code> system 
	 * property.
	 */
	public ClientProperties() {
		
		super();
		
		//	Look for the 'ENV' environment variable.  If not set,
		//	look for the lower case version: 'env'.  Use 'dev' as a 
		//	default when no value is found.
		this.env = System.getProperty("ENV");
		if ((this.env == null) || ("".equals(this.env))) {
			this.env = System.getProperty("env", "dev").toLowerCase();		//	Defaults to DEV environment.
		}
		
		logger.debug("ENV = " + this.env);
	}

	/**
	 * <p></p>
	 *
	 * @param defaults
	 */
	public ClientProperties(Properties defaults) {
		super(defaults);
	}

	/**
	 * <p>Returns the property for the given key.</p>
	 * 
	 * Overrides {@link java.util.Properties#getProperty(java.lang.String)} and
	 * prepends the name of the environment (<code>local</code>, 
	 * <code>test</code>, <code>qa</code>, <code>stage</code>, or 
	 * <code>prod</code>) to the given key to select the environment-specific 
	 * value from the <code>loginclient.properties</code> file.
	 * 
	 * @see java.util.Properties#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String key) {
		
		String envKey = this.env + "." + key;
		String prop = super.getProperty(envKey);
		
		return prop;
	}

	/**
	 * <p>Returns the currently configured environment name.</p>
	 * 
	 * @return the configured environment name.
	 */
	public String getEnv() {
		return env;
	}
}

/*
 * Created:  Jul 21, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.config;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

/**
 * <p>Represents a <code>DataSource</code> to a single database schema.</p>
 * 
 * When adding this class as a <code>DataSource</code> bean to a Spring context, 
 * remember to set the <code>schemaName</code> property in the XML file.
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class TokenServiceDataSource extends SimpleDriverDataSource implements AppConstants {

	//	TODO:  Use ComboPooledDataSource instead.
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private AppProperties appProperties;
	
	private Driver driver;
	private String schemaName;
	
	/**
	 * <p></p>
	 * 
	 */
	public TokenServiceDataSource() {
		super();
	}

	/**
	 * <p>Returns the database <code>Connection</code> for the configured
	 * database schema.</p>
	 * 
	 * Database connection details are configured in the <code>app.properties</code>
	 * file located in the <code>/WEB-INF/classes</code> directory.  The format
	 * of the entries is:
	 * 
	 * < env >.< db_schema >.jdbc.url
	 * 
	 * <ul>
	 * <li><code>env</code> - Represents the environment the property is used 
	 * in.  Values can be one of <code>local</code>, <code>dev</code>,
	 * <code>qa</code>, or <code>prod</code>.</li>
	 * <li>db_schema </li> - Represents the name of the database schema the 
	 * property relates to.</li>
	 * </ul>
	 * 
	 * @return an environment-specific database connection.
	 * 
	 * @throws SQLException
	 */
	private Connection getEnvConnection() throws SQLException {
		
		logger.debug("getEnvConnection(): schemaName = " + this.schemaName);
		
		if (this.appProperties == null) {
			logger.debug("getEnvConnection(): appProperties was 'null'.");
		}
		
		try {
			String driverClassName = this.appProperties.getProperty(this.schemaName + "." + PROP_JDBC_DRIVER_CLASS_NAME);
			logger.debug("getEnvConnection(): driverClassName = " + driverClassName);
			Class<?> driverClass = Class.forName(driverClassName);
			
			this.driver = (Driver) driverClass.newInstance();
		} 
		catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return null;
		}
		catch (IllegalAccessException iae) {
			iae.printStackTrace();
			return null;
		}
		catch (InstantiationException ie) {
			ie.printStackTrace();
			return null;
		}
		
		DriverManager.registerDriver(this.driver);
		Connection conn = DriverManager.getConnection(this.appProperties
				.getProperty(this.schemaName + "." + PROP_JDBC_URL), 
				this.appProperties
					.getProperty(this.schemaName + "." + PROP_JDBC_USERNAME),
				this.appProperties
					.getProperty(this.schemaName + "." + PROP_JDBC_PASSWORD));
		return conn;
	}
	
	@Override
	public Connection getConnectionFromDriver(Properties props) throws SQLException {
		Connection conn = getEnvConnection();
		return conn;
	}

	@Override
	public Driver getDriver() {
		return this.driver;
	}

	@Override
	public void setDriverClass(Class driverClass) {
		super.setDriverClass(this.driver.getClass());
	}

	@Override
	public void setDriver(Driver driver) {
		super.setDriver(this.driver);
	}

	/**
	 * <p></p>
	 * 
	 * @return the schemaName
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * <p></p>
	 * 
	 * @param schemaName the schemaName to set
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

}

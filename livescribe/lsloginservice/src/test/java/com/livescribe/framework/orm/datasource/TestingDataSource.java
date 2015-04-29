/**
 * 
 */
package com.livescribe.framework.orm.datasource;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.livescribe.framework.config.AppConstants;
import com.livescribe.framework.config.AppProperties;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class TestingDataSource extends SimpleDriverDataSource implements AppConstants {

	@Autowired
	private AppProperties appProperties;
	
	private Driver driver;
	private String schemaName;
	
	/**
	 * @return the schemaName
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaName the schemaName to set
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * <p>Default class constructor.</p>
	 */
	public TestingDataSource() {
		super();
	}

	/**
	 * <p></p>
	 * 
	 * @return
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
	
	/* (non-Javadoc)
	 * @see org.springframework.jdbc.datasource.SimpleDriverDataSource#getConnectionFromDriver(java.util.Properties)
	 */
	@Override
	public Connection getConnectionFromDriver(Properties props) throws SQLException {
		Connection conn = getEnvConnection();
		return conn;
	}
}

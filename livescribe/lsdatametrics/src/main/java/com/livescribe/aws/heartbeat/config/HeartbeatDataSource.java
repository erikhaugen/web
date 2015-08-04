/*
 * Created:  Jul 21, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.heartbeat.config;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.livescribe.aws.heartbeat.HeartbeatConstants;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class HeartbeatDataSource extends SimpleDriverDataSource implements HeartbeatConstants {

	//	TODO:  Use ComboPooledDataSource instead.
	
	@Autowired
	private HeartbeatProperties heartbeatProperties;
	
	private String env;
	private Driver driver;
	
	/**
	 * <p></p>
	 * 
	 */
	public HeartbeatDataSource() {
		super();
	}

	private Connection getEnvConnection() throws SQLException {
		
		try {
			Class driverClass = Class.forName(this.heartbeatProperties
					.getProperty(PROP_JDBC_DRIVER_CLASS_NAME));
			
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
		Connection conn = DriverManager.getConnection(this.heartbeatProperties
				.getProperty(PROP_JDBC_URL), 
				this.heartbeatProperties
					.getProperty(PROP_JDBC_USERNAME),
				this.heartbeatProperties
					.getProperty(PROP_JDBC_PASSWORD));
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

}

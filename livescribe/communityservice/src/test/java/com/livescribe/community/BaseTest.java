/**
 * 
 */
package com.livescribe.community;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ReplacementDataSet;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.livescribe.base.WebUtils;
import com.livescribe.community.config.ConfigClient;
import com.livescribe.lsconfig.LSConfigurationFactory;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@ContextConfiguration(locations={"classpath:communityservice-test-context.xml", "classpath:community-servlet.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseTest {
	
	protected static String DB_SCHEMA_NAME = "_SYSTEM";
	
	protected static boolean skipTests = false;
	
	static {
		System.setProperty("configPath", "/Livescribe/LSConfiguration/");
		System.setProperty("env", "test");
		try {
			String host = WebUtils.getHostname();
			if (host.equals("svn.livescribe.com")) {
				skipTests = true;
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	protected Logger logger = Logger.getLogger(getClass());
	
	protected ConfigClient configClient;
	
	@Autowired
	protected SessionFactory testSessionFactory;
	
	/** Used to populate database with test data. */
//	@Autowired
//	protected JdbcTemplate jdbcTemplate;

//	static { 
//		Configuration config = new Configuration()
//			.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
//			.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver")
//			.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:local.consumer")
//			.setProperty("hibernate.connection.username", "sa")
//			.setProperty("hibernate.connection.password", "")
//			.setProperty("hibernate.connection.pool_size", "1")
//			.setProperty("hibernate.connection.autocommit", "true")
//			.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider")
////			.setProperty("hibernate.hbm2ddl.auto", "create-drop")
//			.setProperty("hibernate.show_sql", "true")
//		HibernateUtil.setSessionFactory(config.buildSessionFactory()); 
//		}		
	/**
	 * <p>Default class constructor.
	 */
	public BaseTest() {
		
		DOMConfigurator.configureAndWatch("log4j.xml");
	}
	
	/**
	 * <p></p>
	 *
	 * @return
	 * @throws CannotGetJdbcConnectionException
	 * @throws DatabaseUnitException
	 * @throws SQLException
	 */
	protected ReplacementDataSet fetchResultDataSet() throws CannotGetJdbcConnectionException, DatabaseUnitException, SQLException {
		
		//====================================================================
		//	Obtain result data.
		//====================================================================
//		DataSource rsltDS = this.jdbcTemplate.getDataSource();
//		Connection rsltConn = DataSourceUtils.getConnection(rsltDS);
//
//		IDatabaseConnection connection = new DatabaseConnection(rsltConn, DB_SCHEMA_NAME);
//		DatabaseConfig config = connection.getConfig();
//		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
//
//		// Fetch database data after execution
//		IDataSet resultDataSet = connection.createDataSet();
//		ReplacementDataSet replacedDataSet = new ReplacementDataSet(resultDataSet);
//		replacedDataSet.addReplacementObject("[null]", null);
//		replacedDataSet.addReplacementObject("", null);
//		
//		return replacedDataSet;
		return null;
	}

	protected long startMeter(String methodName) {
		
		Runtime rt = Runtime.getRuntime();
		
		long maxMemory = rt.maxMemory();
		long allocatedMemory = rt.totalMemory();
		long freeMemory = rt.freeMemory();
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("\n\n*** " + methodName + "\n");
		builder.append("------------ Before Call --------------\n");
		builder.append("      Free (KB): " + freeMemory/1024 + "\n");
		builder.append(" Allocated (KB): " + allocatedMemory/1024 + "\n");
		builder.append("       Max (KB): " + maxMemory/1024 + "\n");
		builder.append("Total Free (KB): " + (freeMemory + (maxMemory - allocatedMemory))/1024 + "\n");
		long start = System.currentTimeMillis();
		
		logger.debug(builder.toString());
		
		return start;
	}
	
	protected void stopMeter(String methodName, long start) {
		
		Runtime rt = Runtime.getRuntime();
		
		long maxMemory = rt.maxMemory();
		long allocatedMemory = rt.totalMemory();
		long freeMemory = rt.freeMemory();
		
		StringBuilder builder = new StringBuilder();
		
		long end = System.currentTimeMillis();
		maxMemory = rt.maxMemory();
		allocatedMemory = rt.totalMemory();
		freeMemory = rt.freeMemory();
		
		builder.append("\n\n------------ After Call --------------\n");
		builder.append("      Free (KB): " + freeMemory/1024 + "\n");
		builder.append(" Allocated (KB): " + allocatedMemory/1024 + "\n");
		builder.append("       Max (KB): " + maxMemory/1024 + "\n");
		builder.append("Total Free (KB): " + (freeMemory + (maxMemory - allocatedMemory))/1024 + "\n\n");
		builder.append("Time Spent: " + (end - start) + " milliseconds\n\n");
		
		logger.debug(builder.toString());
	}
	
	
}

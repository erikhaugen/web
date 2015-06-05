/*
 * Created:  Sep 19, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p>Base class for all unit tests.</p>
 * 
 * Has methods to load and unload the <code>consumer</code> and 
 * <code>manufacturing</code> databases with test data prior to each test run.
 * 
 * Instantiates a Log4J logger.
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@ContextConfiguration(locations={"classpath:token-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseTest {

	public Logger logger = Logger.getLogger(this.getClass().getName());

	public static String SCHEMA_CONSUMER		= "consumer";
	public static String SCHEMA_MANUFACTURING	= "manufacturing";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private JdbcTemplate jdbcTemplateM;

	/*
	 * These two data sets are used to load and unload test data to and from
	 * the database.  They are declared here so both setUp() and tearDown()
	 * methods can have access to the exact same data set.
	 */
	protected ReplacementDataSet consumerTestDataSet;
	protected ReplacementDataSet manufacturingTestDataSet;
	
	
	protected ReplacementDataSet expectedDataSet;
	protected ReplacementDataSet actualDataSet;

	/**
	 * <p></p>
	 * 
	 */
	public BaseTest() {
		DOMConfigurator.configureAndWatch("log4j.xml");
	}

	protected ReplacementDataSet fetchConsumerExpectedDataSet() throws MalformedURLException, DataSetException {
		
		// Initialize the dataset
		IDataSet fxds = null;
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		File setupUser = new File("src/test/resources/data/setup/user.xml"); 
		fxds = builder.build(setupUser);

		//	Replace any '[null]' strings with actual 'null' values. 
		ReplacementDataSet expDataSet = new ReplacementDataSet(fxds);
		expDataSet.addReplacementObject("[null]", null);
		
		return expDataSet;
	}
	
	protected ReplacementDataSet fetchConsumerResultDataSet() throws CannotGetJdbcConnectionException, SQLException, DatabaseUnitException {

		//====================================================================
		//	Obtain result data.
		//====================================================================
		DataSource rsltDS = this.jdbcTemplate.getDataSource();
		Connection rsltConn = DataSourceUtils.getConnection(rsltDS);
		
		IDatabaseConnection connection = new DatabaseConnection(rsltConn, SCHEMA_CONSUMER);
		DatabaseConfig config = connection.getConfig();
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
		
		// Fetch database data after execution
		IDataSet resultDataSet = connection.createDataSet();
		ReplacementDataSet replacedDataSet = new ReplacementDataSet(resultDataSet);
		replacedDataSet.addReplacementObject("[null]", null);
		replacedDataSet.addReplacementObject("", null);
		
		return replacedDataSet;
	}

	/**
	 * <p>Sets up test data into the <code>consumer</code> database.</p>
	 * 
	 * @throws Exception
	 */
	protected void setUpConsumer() throws Exception {
		
		Connection con = getDBConnection();
		IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
		dbUnitCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new MySqlDataTypeFactory());
		
		// Initialize the dataset
		IDataSet fxds = null;
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		File setupUser = new File("src/test/resources/data/setup/user.xml"); 
		fxds = builder.build(setupUser);

		//	Replace any '[null]' strings with actual 'null' values. 
		consumerTestDataSet = new ReplacementDataSet(fxds);
		consumerTestDataSet.addReplacementObject("[null]", null);
		
		try {
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, consumerTestDataSet);
		}
		finally {
//			closeConnection(con);
		}
	}
	
	/**
	 * <p>Sets up test data into the <code>manufacturing</code> database.</p>
	 * 
	 * @throws Exception
	 */
	protected void setUpManufacturingData() throws Exception {
		
		Connection con = DataSourceUtils.getConnection(jdbcTemplateM.getDataSource());
		IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
		dbUnitCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new MySqlDataTypeFactory());
		
		// Initialize the dataset
		IDataSet fxds = null;
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();		
		File setupUser = new File("src/test/resources/data/setup/pen.xml"); 
		fxds = builder.build(setupUser);

		//	Replace any '[null]' strings with actual 'null' values. 
		manufacturingTestDataSet = new ReplacementDataSet(fxds);
		manufacturingTestDataSet.addReplacementObject("[null]", null);
		
		try {
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, manufacturingTestDataSet);
		}
		finally {
//			closeConnection(con);
		}
	}
	
	/**
	 * <p></p>
	 * 
	 * @throws Exception
	 */
	protected void tearDownConsumer() throws Exception {
		
		Connection con = getDBConnection();
		IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
		dbUnitCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new MySqlDataTypeFactory());
		
		try {
			DatabaseOperation.DELETE.execute(dbUnitCon, consumerTestDataSet);
		}
		finally {
//			closeConnection(con);
		}
	}
	
	/**
	 * <p></p>
	 * 
	 * @throws Exception
	 */
	protected void tearDownManufacturingData() throws Exception {
		
		Connection con = DataSourceUtils.getConnection(jdbcTemplateM.getDataSource());
		IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
		dbUnitCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new MySqlDataTypeFactory());

		try {
			DatabaseOperation.DELETE.execute(dbUnitCon, manufacturingTestDataSet);
		}
		finally {
			
		}
	}
	
	protected Connection getDBConnection() throws Exception {
		Connection con = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
		return con;
	}
	
	private void closeConnection(Connection con) {
		DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
	}
	
}

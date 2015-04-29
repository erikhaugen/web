/*
 * Created:  Sep 19, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.login;

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
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
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
 * <p>Has methods to load and unload the <code>consumer</code> database with 
 * test data prior to each test run.</p>
 * 
 * <p>Instantiates a Log4J logger.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@ContextConfiguration(locations={"classpath:lslogin-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseTest {

	public Logger logger = Logger.getLogger(this.getClass().getName());

	public static String SCHEMA_CONSUMER		= "consumer";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/*
	 * These two data sets are used to load and unload test data to and from
	 * the database.  They are declared here so both setUp() and tearDown()
	 * methods can have access to the exact same data set.
	 */
	protected ReplacementDataSet consumerTestDataSet;
	
	
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
		File setupUser = new File("src/test/resources/data/expected/user.xml"); 
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
		
//		listTableData(consumerTestDataSet);
		
		try {
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, consumerTestDataSet);
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
	
	protected Connection getDBConnection() throws Exception {
		Connection con = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
		return con;
	}
	
	private void closeConnection(Connection con) {
		DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
	}
	
	private void listTableData(ReplacementDataSet consumerTestDataSet) throws DataSetException {
		
		if (consumerTestDataSet == null) {
			return;
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append("\n");
		ITableIterator tableIter = consumerTestDataSet.iterator();
		while (tableIter.next()) {
			ITable table = tableIter.getTable();
			String tableName = table.getTableMetaData().getTableName();
			builder.append("----------------------------------------\n");
			builder.append("Table:  " + tableName + "\n");
			builder.append("[row, column_name]:  value\n");
			builder.append("----------------------------------------\n");
			Column[] columns = table.getTableMetaData().getColumns();
			int rows = table.getRowCount();
			for (int r = 0; r < rows; r++) {
				for (Column c : columns) {
					Object obj = table.getValue(r, c.getColumnName());
//					logger.debug("[" + r + ", " + c + "]:  " + obj.toString());
					builder.append("[" + r + ", " + c.getColumnName() + "]:  " + obj.toString() + "\n");
				}
			}
		}
		logger.debug(builder.toString());
	}

	/**
	 * <p>Calculates the uptime of the servlet context.</p>
	 * 
	 * @param duration The duration in milliseconds.
	 * 
	 * @return a <code>String</code> formatted as:  <days> - <hours> - <minutes> - <seconds>
	 */
	protected String calcUptime(long duration) {
		
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
}

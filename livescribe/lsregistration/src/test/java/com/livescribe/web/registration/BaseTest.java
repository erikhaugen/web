/**
 * Created:  Aug 27, 2013 12:57:40 PM
 */
package com.livescribe.web.registration;

import java.io.File;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.CompositeTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredTableMetaData;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p>Base class for all unit tests.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@ContextConfiguration("classpath:test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseTest {

	protected Logger logger = Logger.getLogger(this.getClass().getName());

	protected static final String TABLE_NAME_REGISTRATION			= "registration";
	protected static final String TABLE_NAME_REGISTRATION_HISTORY	= "registration_history";
	protected static final String TABLE_NAME_WARRANTY				= "warranty";
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Autowired
	protected JdbcTemplate jdbcTemplateManufacturing;

	/*
	 * This data set is used to load and unload test data to and from
	 * the database.  It is declared here so both setUp() and tearDown()
	 * methods can have access to the exact same data set.
	 */
	protected ReplacementDataSet registrationTestDataSet;
	protected ReplacementDataSet registrationHistoryTestDataSet;
	protected ReplacementDataSet warrantyTestDataSet;
	protected ReplacementDataSet penTestDataSet;

	/**
	 * <p></p>
	 * 
	 */
	public BaseTest() {
		DOMConfigurator.configureAndWatch("log4j.xml");
	}

	protected void closeConnection(Connection con) {
		DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
	}
	
	/**
	 * <p>Used to filter columns from both &apos;expected&apos; and 
	 * &apos;actual&apos; tables before comparing them.</p>
	 * 
	 * @param originalTable The original table to be filtered.
	 * 
	 * @return a table with columns filtered out.
	 * 
	 * @throws DataSetException
	 */
	protected ITable filterColumns(ITable originalTable) throws DataSetException {
		
		DefaultColumnFilter columnFilter = new DefaultColumnFilter();
		columnFilter.excludeColumn("registration_id");
		columnFilter.excludeColumn("pen_name");
		columnFilter.excludeColumn("locale");
		columnFilter.excludeColumn("created");
		columnFilter.excludeColumn("last_modified");
		columnFilter.excludeColumn("last_modified_by");

		String tableName = originalTable.getTableMetaData().getTableName();
		if (TABLE_NAME_REGISTRATION.equals(tableName)) {
			columnFilter.excludeColumn("country");
			columnFilter.excludeColumn("opt_in");
		}
		else if (TABLE_NAME_REGISTRATION_HISTORY.equals(tableName)) {
			columnFilter.excludeColumn("country");
			columnFilter.excludeColumn("opt_in");
		}
		else if (TABLE_NAME_WARRANTY.equals(tableName)) {

		}
		FilteredTableMetaData metaData = new FilteredTableMetaData(originalTable.getTableMetaData(), columnFilter);
		ITable filteredTable = new CompositeTable(metaData, originalTable);
		
		return filteredTable;
	}
	
	protected IDatabaseConnection getDBConnection() throws Exception {
		
		Connection con = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
		IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
		dbUnitCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new MySqlDataTypeFactory());
		return dbUnitCon;
	}
	
	protected IDatabaseConnection getDBConnectionManufacturing() throws Exception {

		Connection con = DataSourceUtils.getConnection(jdbcTemplateManufacturing.getDataSource());
		IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
		dbUnitCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new MySqlDataTypeFactory());
		return dbUnitCon;
	}

	private IDataSet getDataSet(String filename) throws Exception {
		
		String method = "loadPenTable()";
		
		IDatabaseConnection dbUnitCon = getDBConnectionManufacturing();
		
		// Initialize the dataset
		IDataSet fxds = null;
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		File setupUser = new File(filename); 
		fxds = builder.build(setupUser);

		//	Replace any '[null]' strings with actual 'null' values. 
		ReplacementDataSet replacementDataSet = new ReplacementDataSet(fxds);
		replacementDataSet.addReplacementObject("[null]", null);
		
		return replacementDataSet;
	}
	
	protected ITable getActualTable(String tableName) throws Exception {
		
		IDatabaseConnection dbUnitCon = getDBConnection();
		IDataSet dataSet = dbUnitCon.createDataSet();
		ITable tbl = dataSet.getTable(tableName);
		ITable filteredTable = filterColumns(tbl);
		
		return filteredTable;
	}
	
	protected ITable getExpectedTable(String tableName) throws Exception {
		
		IDataSet fxds = null;
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		File setupUser = new File("src/test/resources/data/expected/registration.xml"); 
		fxds = builder.build(setupUser);
		ITable table = fxds.getTable(tableName);
		ITable filteredTable = filterColumns(table);
		
		return filteredTable;
	}
	
	/**
	 * <p>Loads test data into the <code>pen<code> table.</p>
	 * 
	 * @throws Exception 
	 */
	protected void loadPenTable() throws Exception {
		
		String method = "loadPenTable()";
		
		IDatabaseConnection dbUnitCon = getDBConnectionManufacturing();
		
		penTestDataSet = (ReplacementDataSet)getDataSet("src/test/resources/data/setup/pen.xml");
		
		try {
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, penTestDataSet);
		}
		finally {
//			closeConnection(con);
		}
	}

	/**
	 * <p>Unloads test data from the <code>pen</code> table.</p>
	 * 
	 * @throws Exception
	 */
	protected void unloadPenTable() throws Exception {
		
		IDatabaseConnection dbUnitCon = getDBConnectionManufacturing();
		
		penTestDataSet = (ReplacementDataSet)getDataSet("src/test/resources/data/setup/pen.xml");
		
		try {
			DatabaseOperation.DELETE.execute(dbUnitCon, penTestDataSet);
		}
		finally {
//			closeConnection(con);
		}
	}
	
	/**
	 * <p>Removes test setup data from the <code>registration</code> table.</p>
	 * 
	 * @throws Exception
	 */
	protected void unloadRegistrationTable() throws Exception {
		
		IDatabaseConnection dbUnitCon = getDBConnection();
		
		try {
			DatabaseOperation.DELETE.execute(dbUnitCon, registrationTestDataSet);
		}
		finally {
//			closeConnection(con);
		}
	}
	
	/**
	 * <p>Removes test setup data from the <code>registration_history</code> table.</p>
	 * 
	 * @throws Exception
	 */
	protected void unloadRegistrationHistoryTable() throws Exception {
		
		IDatabaseConnection dbUnitCon = getDBConnection();
		
		try {
			DatabaseOperation.DELETE.execute(dbUnitCon, registrationHistoryTestDataSet);
		}
		finally {
//			closeConnection(con);
		}
	}
	
	/**
	 * <p>Removes test setup data from the <code>warranty</code> table.</p>
	 * 
	 * @throws Exception
	 */
	protected void unloadWarrantyTable() throws Exception {
		
		IDatabaseConnection dbUnitCon = getDBConnection();
		
		try {
			DatabaseOperation.DELETE.execute(dbUnitCon, warrantyTestDataSet);
		}
		finally {
//			closeConnection(con);
		}
	}
	
	/**
	 * <p>Sets up test data into the <code>registration</code> table.</p>
	 * 
	 * @throws Exception
	 */
	protected void setUpRegistrationTable() throws Exception {
		
		IDatabaseConnection dbUnitCon = getDBConnection();
		
		// Initialize the dataset
		IDataSet fxds = null;
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		File setupUser = new File("src/test/resources/data/setup/registration.xml"); 
		fxds = builder.build(setupUser);

		//	Replace any '[null]' strings with actual 'null' values. 
		registrationTestDataSet = new ReplacementDataSet(fxds);
		registrationTestDataSet.addReplacementObject("[null]", null);
		
		try {
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, registrationTestDataSet);
		}
		finally {
//			closeConnection(con);
		}
	}


	/**
	 * <p>Sets up test data into the <code>warranty</code> table.</p>
	 * 
	 * @throws Exception
	 */
	protected void setUpWarrantyTable() throws Exception {
		
		IDatabaseConnection dbUnitCon = getDBConnection();
		
		// Initialize the dataset
		IDataSet fxds = null;
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		File setupUser = new File("src/test/resources/data/setup/warranty.xml"); 
		fxds = builder.build(setupUser);

		//	Replace any '[null]' strings with actual 'null' values. 
		warrantyTestDataSet = new ReplacementDataSet(fxds);
		warrantyTestDataSet.addReplacementObject("[null]", null);
		
		try {
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, warrantyTestDataSet);
		}
		finally {
//			closeConnection(con);
		}
	}
	
	/**
	 * <p>Sets up test data into the <code>registration_history</code> table.</p>
	 * 
	 * @throws Exception
	 */
	protected void setUpRegistrationHistoryTable() throws Exception {
		
		IDatabaseConnection dbUnitCon = getDBConnection();
		
		// Initialize the dataset
		IDataSet fxds = null;
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		File setupUser = new File("src/test/resources/data/setup/registration_history.xml"); 
		fxds = builder.build(setupUser);

		//	Replace any '[null]' strings with actual 'null' values. 
		registrationHistoryTestDataSet = new ReplacementDataSet(fxds);
		registrationHistoryTestDataSet.addReplacementObject("[null]", null);
		
		try {
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, registrationHistoryTestDataSet);
		}
		finally {
//			closeConnection(con);
		}
	}
}

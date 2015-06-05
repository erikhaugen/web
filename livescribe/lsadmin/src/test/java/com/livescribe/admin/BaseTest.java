/**
 * Created:  Sep 5, 2013 2:24:52 PM
 */
package com.livescribe.admin;

import java.io.File;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
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
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@ContextConfiguration(locations={"classpath:test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseTest {

	protected Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	/*
	 * This data set is used to load and unload test data to and from
	 * the database.  It is declared here so both setUp() and tearDown()
	 * methods can have access to the exact same data set.
	 */
	protected ReplacementDataSet enDbTestDataSet;

	/**
	 * <p></p>
	 * 
	 */
	public BaseTest() {
		DOMConfigurator.configureAndWatch("log4j.xml");
	}

	protected Connection getDBConnection() throws Exception {
		Connection con = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
		return con;
	}
	
	/**
	 * <p>Sets up test data into the <code>lsevernotedb</code> database.</p>
	 * 
	 * @throws Exception
	 */
	protected void setUpEvernoteDB() throws Exception {
		
		Connection con = getDBConnection();
		IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
		dbUnitCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new MySqlDataTypeFactory());
		
		// Initialize the dataset
		IDataSet fxds = null;
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		File setupUser = new File("src/test/resources/data/setup/registration.xml"); 
		fxds = builder.build(setupUser);

		//	Replace any '[null]' strings with actual 'null' values. 
		enDbTestDataSet = new ReplacementDataSet(fxds);
		enDbTestDataSet.addReplacementObject("[null]", null);
		
		try {
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, enDbTestDataSet);
		}
		finally {
//			closeConnection(con);
		}
	}
}

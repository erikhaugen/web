package com.livescribe.community.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.dbunit.IDatabaseTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.base.utils.WOAppMigrationUtils;
import com.livescribe.community.BaseTest;
import com.livescribe.community.config.CommunityProperties;
import com.livescribe.community.view.vo.PencastVo;

/**
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PencastDaoTest extends BaseTest {
	
	private IDatabaseTester databaseTester;

	/** Used to populate database with test data. */
//	@Autowired
//	protected JdbcTemplate jdbcTemplate;

	@Autowired
	private PencastDao pencastDao;
	
	@Autowired
	private CommunityProperties communityProperties;
	
	/**
	 * <p></p>
	 * 
	 */
	public PencastDaoTest() {
		super();
	}
	
	@Before
	public void setUp() throws Exception {
		
////		DataSource dataSource = jdbcTemplate.getDataSource();
////		databaseTester = new DataSourceDatabaseTester(dataSource);
////		databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
//		
//		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
//		builder.setColumnSensing(true);
//		IDataSet dataSet = builder.build(new File("src/test/resources/test-data.xml"));
//		
//		ReplacementDataSet replacedSet = new ReplacementDataSet(dataSet); 
//		replacedSet.addReplacementObject("[null]", null);
//		
////		databaseTester.setDataSet(replacedSet);
////		databaseTester.onSetup();
//
//		Connection conn = getDBConnection();
//		logger.debug("DB Conn catalog: " + conn.getCatalog());
//		DatabaseMetaData dmd = conn.getMetaData();
//		logger.debug("JDBC URL: " + dmd.getURL());
//		ResultSet tt = dmd.getTableTypes();
//		tt.last();
//		int ttcount = tt.getRow();
//		logger.debug("Table type count: " + ttcount);
//		
//		String[] tabletypes = {"TABLE"};
//		ResultSet rset = dmd.getTables("PUBLIC", "*", "*", tabletypes);
//		rset.last();
//		int lastrow = rset.getRow();
//		logger.debug("Last row: " + lastrow);
//		
//		String[] tableNames = replacedSet.getTableNames();
//		logger.debug("First table name: " + tableNames[0]);
//		
//		IDatabaseConnection dbUnitConn = new DatabaseConnection(conn);
//		dbUnitConn.getConfig().setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
//		
//		try {
//			DatabaseOperation.CLEAN_INSERT.execute(dbUnitConn, replacedSet);
//		}
//		finally {
//			closeConnection(conn);
//		}	
	}

	@After
	public void tearDown() throws Exception {
	
		
	}
	
//	private void closeConnection(Connection con) {
//
//		if (!skipTests) {
//			DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
//		}
//	}
//
//	private Connection getDBConnection() throws Exception {
//
//		if (!skipTests) {
//			Connection con = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
//			Connection connection = testSessionFactory.getCurrentSession().connection();
//			return con;
//		}
//		return null;
//	}

	/**
	 * 
	 */
	@Test
	@Transactional
	public void testFindByPencastShortId() {
		
		if (!skipTests) {
			String TEST_PENCAST_SHORT_ID = communityProperties.getProperty("PencastDaoTest.testFindByPencastShortId.test.id");	//6cQ6Tl1dMgSw
			
			PencastVo pencast = pencastDao.findByPencastShortId(TEST_PENCAST_SHORT_ID);
			
			assertNotNull("The returned Pencast object was 'null'.", pencast);
		}
	}

	@Test
	@Transactional
	public void testRecentUserPencastsExist() {
		
		if (!skipTests) {
			GregorianCalendar testCal = new GregorianCalendar(2008, 3, 19, 13, 5, 0);
			Date testNow = testCal.getTime();
			
			String upk = "0000C0A80115000009C63200000001191FE431D618CD8310";
			byte[] pk = WOAppMigrationUtils.convertStringToPrimaryKey(upk);
			
			boolean exist = pencastDao.recentUserPencastsExist(pk, testNow);
			
			assertTrue("No recent user pencasts were found to exist.", exist);
		}
	}
	
	/**
	 * 
	 */
	@Test
	@Transactional
	public void testGetMostViewedPencasts() {
		
		if (!skipTests) {
			String methodName = new Exception().getStackTrace()[0].getMethodName();
			String startPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.start";
			String startStr = communityProperties.getProperty(startPropKey);
			int TEST_START = Integer.parseInt(startStr);
			
			String numPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.fetchSize";
			String numStr = communityProperties.getProperty(numPropKey);
			int TEST_FETCH_SIZE = Integer.parseInt(numStr);
			
			long start = startMeter(methodName);
			
			List<PencastVo> list = pencastDao.getMostViewedPencasts(TEST_START, TEST_FETCH_SIZE);
	
			stopMeter(methodName, start);
			
			String expCountPropKey = this.getClass().getSimpleName() + "." + methodName + ".expected.count";
			String countStr = communityProperties.getProperty(expCountPropKey);
			int EXPECTED_PENCAST_COUNT = Integer.parseInt(countStr);
					
			assertNotNull("The returned List of pencasts was 'null'.", list);
			assertEquals("The number of items returned in the List object was incorrect.", EXPECTED_PENCAST_COUNT, list.size());
		}
	}
	
	/**
	 * 
	 */
	@Test
	@Transactional
	public void testGetPencastsByCategoryName() {
		
		if (!skipTests) {
			String methodName = new Exception().getStackTrace()[0].getMethodName();
			String propertyKey = this.getClass().getSimpleName() + "." + methodName + ".test.categoryName";
			String TEST_PENCAST_CATEGORY_NAME = communityProperties.getProperty(propertyKey);
	
	//		List<PencastVo> list = pencastDao.getPencastsByCategoryName(TEST_PENCAST_CATEGORY_NAME);
			Runtime rt = Runtime.getRuntime();
			
			long maxMemory = rt.maxMemory();
			long allocatedMemory = rt.totalMemory();
			long freeMemory = rt.freeMemory();
			
			logger.debug(methodName);
			logger.debug("------------ Before Call --------------");
			logger.debug("      Free (KB): " + freeMemory/1024);
			logger.debug(" Allocated (KB): " + allocatedMemory/1024);
			logger.debug("       Max (KB): " + maxMemory/1024);
			logger.debug("Total Free (KB): " + (freeMemory + (maxMemory - allocatedMemory))/1024);
			long start = System.currentTimeMillis();
			
			List<PencastVo> list = pencastDao.getPencastsByCategoryName(TEST_PENCAST_CATEGORY_NAME, 20, 20);
			
			long end = System.currentTimeMillis();
			maxMemory = rt.maxMemory();
			allocatedMemory = rt.totalMemory();
			freeMemory = rt.freeMemory();
			
			logger.debug("------------ After Call --------------");
			logger.debug("      Free (KB): " + freeMemory/1024);
			logger.debug(" Allocated (KB): " + allocatedMemory/1024);
			logger.debug("       Max (KB): " + maxMemory/1024);
			logger.debug("Total Free (KB): " + (freeMemory + (maxMemory - allocatedMemory))/1024);
			logger.debug("");
			logger.debug("Time Spent: " + (end - start) + " milliseconds");
			
	//		List<PencastVo> list = pencastDao.getPencastsByCategory(TEST_PENCAST_CATEGORY_NAME, 20, 20);
			assertNotNull("The returned List of pencasts was 'null'.", list);
			assertTrue("The returned List object was empty.", list.size() > 0);
		}
	}

	/**
	 * 
	 */
	@Test
	@Transactional
	public void testGetRecentPencasts() {
		
		if (!skipTests) {
			//	Get the name of the current method.
			String methodName = new Exception().getStackTrace()[0].getMethodName();
			
			//	Get the 'start' number for testing.
			String startPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.start";
			String startStr = communityProperties.getProperty(startPropKey);
			int TEST_START = Integer.parseInt(startStr);
			
			//	Get the number to return for testing.
			String numPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.fetchSize";
			String numStr = communityProperties.getProperty(numPropKey);
			int TEST_FETCH_SIZE = Integer.parseInt(numStr);
			
			Runtime rt = Runtime.getRuntime();
			
			long maxMemory = rt.maxMemory();
			long allocatedMemory = rt.totalMemory();
			long freeMemory = rt.freeMemory();
			
			logger.debug(methodName);
			logger.debug("------------ Before Call --------------");
			logger.debug("      Free (KB): " + freeMemory/1024);
			logger.debug(" Allocated (KB): " + allocatedMemory/1024);
			logger.debug("       Max (KB): " + maxMemory/1024);
			logger.debug("Total Free (KB): " + (freeMemory + (maxMemory - allocatedMemory))/1024);
			long start = System.currentTimeMillis();
			
			List<PencastVo> list = pencastDao.getRecentPencasts(TEST_START, TEST_FETCH_SIZE);
			
			long end = System.currentTimeMillis();
			maxMemory = rt.maxMemory();
			allocatedMemory = rt.totalMemory();
			freeMemory = rt.freeMemory();
			
			logger.debug("------------ After Call --------------");
			logger.debug("      Free (KB): " + freeMemory/1024);
			logger.debug(" Allocated (KB): " + allocatedMemory/1024);
			logger.debug("       Max (KB): " + maxMemory/1024);
			logger.debug("Total Free (KB): " + (freeMemory + (maxMemory - allocatedMemory))/1024);
			logger.debug("");
			logger.debug("Time Spent: " + (end - start) + " milliseconds");
			
			//	Get the expected number of returned pencasts.
			String propertyKey = this.getClass().getSimpleName() + "." + methodName + ".expected.fetchSize";
			String fetchSize = communityProperties.getProperty(propertyKey);
			int EXPECTED_PENCAST_FETCH_SIZE = Integer.parseInt(fetchSize);
	
			assertNotNull("The returned List of pencasts was 'null'.", list);
			assertTrue("The returned List object did not have 10 items.", list.size() == EXPECTED_PENCAST_FETCH_SIZE);
		}
	}
		
	/**
	 * 
	 */
	@Test
	@Transactional
	public void testGetTopPencasts() {
		
		if (!skipTests) {
			String methodName = new Exception().getStackTrace()[0].getMethodName();
			String propertyKey = this.getClass().getSimpleName() + "." + methodName + ".test.fetchSize";
	
			//	Get the test 'num' parameter and convert it to an Integer.
			String numStr = communityProperties.getProperty(propertyKey);
			logger.debug(methodName + ": numStr: " + numStr);
			
			try {
				int TEST_FETCH_SIZE = Integer.parseInt(numStr);
				logger.debug(methodName + ": TEST_FETCH_SIZE: " + TEST_FETCH_SIZE);
	
				Runtime rt = Runtime.getRuntime();
				
				long maxMemory = rt.maxMemory();
				long allocatedMemory = rt.totalMemory();
				long freeMemory = rt.freeMemory();
				
				logger.debug(methodName);
				logger.debug("------------ Before Call --------------");
				logger.debug("      Free (KB): " + freeMemory/1024);
				logger.debug(" Allocated (KB): " + allocatedMemory/1024);
				logger.debug("       Max (KB): " + maxMemory/1024);
				logger.debug("Total Free (KB): " + (freeMemory + (maxMemory - allocatedMemory))/1024);
				long start = System.currentTimeMillis();
				
				List<PencastVo> list = pencastDao.getTopPencasts(TEST_FETCH_SIZE);
	
				long end = System.currentTimeMillis();
				maxMemory = rt.maxMemory();
				allocatedMemory = rt.totalMemory();
				freeMemory = rt.freeMemory();
				
				logger.debug("------------ After Call --------------");
				logger.debug("      Free (KB): " + freeMemory/1024);
				logger.debug(" Allocated (KB): " + allocatedMemory/1024);
				logger.debug("       Max (KB): " + maxMemory/1024);
				logger.debug("Total Free (KB): " + (freeMemory + (maxMemory - allocatedMemory))/1024);
				logger.debug("");
				logger.debug("Time Spent: " + (end - start) + " milliseconds");
				
				assertNotNull("The returned List object was 'null'.", list);
	//			assertTrue("The returned List object was empty.", list.size() == 7);
	//			assertEquals("Incorrect number of pencasts found.", 1, list.size());
			}
			catch (NumberFormatException nfe) {
				logger.error(methodName + ": The given 'num' parameter was not a number!");
				assertTrue(false);
				return;
			}
		}
	}

	/**
	 * 
	 */
	@Test
	@Transactional
	public void testGetTopPencasts2() {
		
		if (!skipTests) {
			String methodName = new Exception().getStackTrace()[0].getMethodName();
	
			String startPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.start";
			String startStr = communityProperties.getProperty(startPropKey);
			int TEST_START = Integer.parseInt(startStr);
			
			String numPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.fetchSize";
			String numStr = communityProperties.getProperty(numPropKey);
			int TEST_FETCH_SIZE = Integer.parseInt(numStr);
	
			Runtime rt = Runtime.getRuntime();
			
			long maxMemory = rt.maxMemory();
			long allocatedMemory = rt.totalMemory();
			long freeMemory = rt.freeMemory();
			
			logger.debug(methodName);
			logger.debug("------------ Before Call --------------");
			logger.debug("      Free (KB): " + freeMemory/1024);
			logger.debug(" Allocated (KB): " + allocatedMemory/1024);
			logger.debug("       Max (KB): " + maxMemory/1024);
			logger.debug("Total Free (KB): " + (freeMemory + (maxMemory - allocatedMemory))/1024);
			long start = System.currentTimeMillis();
			
			List<PencastVo> list = pencastDao.getTopPencasts(TEST_START, TEST_FETCH_SIZE);
	
			long end = System.currentTimeMillis();
			maxMemory = rt.maxMemory();
			allocatedMemory = rt.totalMemory();
			freeMemory = rt.freeMemory();
			
			logger.debug("------------ After Call --------------");
			logger.debug("      Free (KB): " + freeMemory/1024);
			logger.debug(" Allocated (KB): " + allocatedMemory/1024);
			logger.debug("       Max (KB): " + maxMemory/1024);
			logger.debug("Total Free (KB): " + (freeMemory + (maxMemory - allocatedMemory))/1024);
			logger.debug("");
			logger.debug("Time Spent: " + (end - start) + " milliseconds");
			
			String expCountPropKey = this.getClass().getSimpleName() + "." + methodName + ".expected.count";
			String countStr = communityProperties.getProperty(expCountPropKey);
			int EXPECTED_PENCAST_COUNT = Integer.parseInt(countStr);
					
			assertNotNull("The returned List object was 'null'.", list);
			assertTrue("The returned List object was empty.", list.size() > 0);
			assertEquals("Incorrect number of pencasts found.", EXPECTED_PENCAST_COUNT, list.size());
		}
	}

	@Test
	@Transactional
	public void testGetTopPencasts3() {
		
		if (!skipTests) {
			List<PencastVo> list = pencastDao.getTopPencasts2(0, 20);
			
			assertNotNull("The returned List object was 'null'.", list);
			assertTrue("The returned List object was empty.", list.size() > 0);
		}
	}
	
	/**
	 * 
	 */
	@Test
	@Transactional
	public void testGetUserPencasts() {
		
		if (!skipTests) {
			String methodName = new Exception().getStackTrace()[0].getMethodName();
			String propertyKey = this.getClass().getSimpleName() + "." + methodName + ".test.id";
	
			//	Get the test primary key and convert it to a byte[].
			String idStr = communityProperties.getProperty(propertyKey);
			byte[] TEST_USER_PK = WOAppMigrationUtils.convertStringToPrimaryKey(idStr);
	
			List<PencastVo> list = pencastDao.getUserPencasts(TEST_USER_PK);
	
			assertNotNull("The returned List object was 'null'.", list);
			assertTrue("The returned List object was empty.", list.size() > 0);
	//		assertEquals("Incorrect number of pencasts found.", 1, list.size());
		}
	}

	/**
	 * 
	 */
	@Test
	@Transactional
	public void testGetUserPencasts2() {
		
		if (!skipTests) {
			String methodName = new Exception().getStackTrace()[0].getMethodName();
			String propertyKey = this.getClass().getSimpleName() + "." + methodName + ".test.id";
	
			//	Get the test primary key and convert it to a byte[].
			String idStr = communityProperties.getProperty(propertyKey);
			byte[] TEST_USER_PK = WOAppMigrationUtils.convertStringToPrimaryKey(idStr);
	
			String startPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.start";
			String startStr = communityProperties.getProperty(startPropKey);
			int TEST_START = Integer.parseInt(startStr);
			
			String numPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.fetchSize";
			String numStr = communityProperties.getProperty(numPropKey);
			int TEST_FETCH_SIZE = Integer.parseInt(numStr);
	
			List<PencastVo> list = pencastDao.getUserPencasts(TEST_USER_PK, TEST_START, TEST_FETCH_SIZE);
	
			String expCountPropKey = this.getClass().getSimpleName() + "." + methodName + ".expected.count";
			String countStr = communityProperties.getProperty(expCountPropKey);
			int EXPECTED_PENCAST_COUNT = Integer.parseInt(countStr);
					
			assertNotNull("The returned List object was 'null'.", list);
			assertTrue("The returned List object was empty.", list.size() > 0);
			assertEquals("Incorrect number of pencasts found.", EXPECTED_PENCAST_COUNT, list.size());
		}
	}
}

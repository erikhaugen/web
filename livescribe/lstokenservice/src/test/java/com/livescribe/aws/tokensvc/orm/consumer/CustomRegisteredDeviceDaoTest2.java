/**
 * 
 */
package com.livescribe.aws.tokensvc.orm.consumer;

import static junit.framework.Assert.*;
import java.io.File;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.tokensvc.BaseTest;
import com.livescribe.framework.orm.consumer.RegisteredDevice;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CustomRegisteredDeviceDaoTest2 extends BaseTest {

	@Autowired
	private CustomRegisteredDeviceDao registeredDeviceDao;
	
	/**
	 * <p></p>
	 *
	 */
	public CustomRegisteredDeviceDaoTest2() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		
		Connection con = getDBConnection();
		IDatabaseConnection dbUnitCon = new DatabaseConnection(con);
		dbUnitCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new MySqlDataTypeFactory());
		
		// Initialize the dataset
		IDataSet fxds = null;
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		File setupUser = new File("src/test/resources/data/setup/registered_device.xml"); 
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
	
	@Test
	@Transactional("consumer")
	public void testFindExpiredCodes() {
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -5);
//		Date expDate = cal.getTime();
//		logger.debug("expDate:  " + expDate.toString());
		
		logger.debug("Creating Calendar as 3/2/2012 06:08:00.");
		
		Calendar staticExpDate = Calendar.getInstance();
		staticExpDate.set(Calendar.MONTH, Calendar.MARCH);
		staticExpDate.set(Calendar.DAY_OF_MONTH, 2);
		staticExpDate.set(Calendar.HOUR_OF_DAY, 6);
		staticExpDate.set(Calendar.MINUTE, 8);
		SimpleTimeZone utc = new SimpleTimeZone(0, "UTC");
		staticExpDate.setTimeZone(utc);
		
		Date expDate = staticExpDate.getTime();
		logger.debug("expDate (UTC):  " + expDate.toString());
		
//		List<RegisteredDevice> list = registeredDeviceDao.findExpiredCodes(expDate);
		RegisteredDevice example = new RegisteredDevice();
		List<RegisteredDevice> list = registeredDeviceDao.findByExample(example);
		assertNotNull("The returned List was 'null'.", list);
		
		for (RegisteredDevice dev : list) {
			logger.debug(dev.getRegisteredDeviceId() + ", " + dev.getCreated().toString());
		}
	}
}

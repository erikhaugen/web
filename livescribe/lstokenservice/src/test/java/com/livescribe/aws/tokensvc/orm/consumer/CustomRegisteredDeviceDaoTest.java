/*
 * Created:  Oct 1, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.orm.consumer;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.tokensvc.BaseTest;
import com.livescribe.aws.tokensvc.exception.MultipleRegistrationsFoundException;
import com.livescribe.aws.tokensvc.exception.RegistrationNotFoundException;
import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.framework.orm.consumer.RegisteredDevice;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CustomRegisteredDeviceDaoTest extends BaseTest {

	@Autowired
	private CustomRegisteredDeviceDao registeredDeviceDao;
	
	/**
	 * <p></p>
	 * 
	 */
	public CustomRegisteredDeviceDaoTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		
		setUpConsumer();
	}
	
	@Test
	@Transactional("consumer")
	public void testFindByPenAndUser() {
				
		RegisteredDevice regDev = registeredDeviceDao.findBySerialNumberAndUserId("2594160246933", 1L);
		assertNotNull("The returned RegisteredDevice object was 'null'.", regDev);
	}
	
	@Test
	@Transactional("consumer")
	public void testFindByRegistrationToken() {
		
		RegisteredDevice regDev = null;
		try {
			regDev = registeredDeviceDao.findByRegistrationToken("9766d7617c405ed94981ad75f0eb43a2");
		}
		catch (MultipleRegistrationsFoundException mrfe) {
			mrfe.printStackTrace();
		}
		catch (RegistrationNotFoundException rnfe) {
			rnfe.printStackTrace();
		}
		assertNotNull("The returned RegisteredDevice object was 'null'.", regDev);
	}
}

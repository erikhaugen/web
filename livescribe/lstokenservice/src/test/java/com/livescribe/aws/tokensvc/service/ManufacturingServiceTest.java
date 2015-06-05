/*
 * Created:  Sep 29, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.tokensvc.BaseTest;
import com.livescribe.aws.tokensvc.exception.DeviceNotFoundException;
import com.livescribe.framework.orm.manufacturing.Pen;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ManufacturingServiceTest extends BaseTest {

	@Autowired
	private ManufacturingService manufacturingService;
	
	/**
	 * <p></p>
	 * 
	 */
	public ManufacturingServiceTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		
		setUpManufacturingData();
	}
	
	@Test
	@Transactional("manufacturing")
	public void testLookupDevice_Success() {
		
		String serialNumber = "7823948029286";
		Pen pen = null;
		try {
			pen = manufacturingService.lookupDevice(serialNumber);
		}
		catch (Exception e) {
			
		}
		assertNotNull("The returned Pen was 'null'.", pen);
		assertEquals("The displayId column did not match.", "QBX-018-XVR-QQ", pen.getDisplayId());
	}
	
	@Test
	@Transactional("manufacturing")
	public void testLookupDevice_Fail() {
		
		String serialNumber = "25941602474444";
		Pen pen = null;
		try {
			pen = manufacturingService.lookupDevice(serialNumber);
		}
		catch (Exception e) {
			Class clazz = e.getClass();
			assertEquals("Wrong exception thrown.", DeviceNotFoundException.class, clazz);
		}
		assertNull("The returned Pen was NOT 'null'.", pen);
	}
}

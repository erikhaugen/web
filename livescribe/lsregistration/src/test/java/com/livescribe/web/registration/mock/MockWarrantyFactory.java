/**
 * Created:  Nov 20, 2013 4:48:56 PM
 */
package com.livescribe.web.registration.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.livescribe.framework.orm.vectordb.Warranty;
import com.livescribe.web.registration.TestConstants;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MockWarrantyFactory implements TestConstants {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p>Default class constructor.</p>
	 * 
	 */
	public MockWarrantyFactory() {
	}

	/**
	 * <p>Returns a <code>Warranty</code> object corresponding to the data
	 * loaded from the <code>warranty.xml</code> file.</p>
	 * 
	 * @return a single <code>Warranty</code> object.
	 */
	public static Warranty create() {
		
		Warranty warranty = new Warranty();
		warranty.setAppId("RegistrationClientTest");
		warranty.setDisplayId(XML_LOADED_WARRANTY_DISPLAY_ID_1);
		warranty.setEdition(0);
		warranty.setEmail(XML_LOADED_WARRANTY_EMAIL_1);
		warranty.setFirstName(XML_LOADED_WARRANTY_FIRST_NAME_1);
		warranty.setLastName(XML_LOADED_WARRANTY_LAST_NAME_1);
		warranty.setLocale(XML_LOADED_WARRANTY_LOCALE_1);
		warranty.setPenName(XML_LOADED_WARRANTY_PEN_NAME_1);
		warranty.setPenSerial(XML_LOADED_WARRANTY_PEN_SERIAL_1);
		
		return warranty;
	}
	
	/**
	 * <p>Returns a <code>List</code> of <code>count</code> number of 
	 * <code>Warranty</code> objects</p>
	 * 
	 * @param count The number of <code>Warranty</code> objects to create.
	 * 
	 * @return a list of warranty objects.
	 */
	public static List<Warranty> create(int count) {
		
		ArrayList<Warranty> list = new ArrayList<Warranty>();
		for (int i = 0; i < count; i++) {
			MockWarranty mockWarranty = new MockWarranty();
			list.add(mockWarranty);
		}
		return list;
	}
}

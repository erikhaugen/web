/**
 * Created:  Nov 21, 2013 1:42:34 PM
 */
package com.livescribe.web.registration.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.livescribe.framework.orm.vectordb.RegistrationHistory;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MockRegistrationHistoryFactory {

	/**
	 * <p></p>
	 * 
	 */
	public MockRegistrationHistoryFactory() {
	}

	/**
	 * <p></p>
	 * 
	 * @param count
	 * @return
	 */
	public static List<RegistrationHistory> create(int count) {
		
		ArrayList<RegistrationHistory> list = new ArrayList<RegistrationHistory>();
		
		for (int i = 0; i < count; i++) {
			MockRegistrationHistory rh = new MockRegistrationHistory();
			list.add(rh);
		}
		return list;
	}
}

/**
 * Created:  Jan 7, 2014 5:17:21 PM
 */
package com.livescribe.web.registration.util;

import java.util.Date;

import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.framework.orm.vectordb.RegistrationHistory;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegistrationHistoryFactory {

	/**
	 * <p>Creates a registration history record from the given registration
	 * record.</p>
	 * 
	 * @param registration The registration record to convert.
	 * 
	 * @return a registration history record.
	 */
	public static RegistrationHistory create(Registration registration) {
		
		RegistrationHistory regHistory = new RegistrationHistory();
		regHistory.setRegistrationId(registration.getRegistrationId());
		regHistory.setAppId(registration.getAppId());
		regHistory.setEdition(registration.getEdition());
		regHistory.setPenSerial(registration.getPenSerial());
		regHistory.setDisplayId(registration.getDisplayId());
		regHistory.setPenName(registration.getPenName());
		regHistory.setFirstName(registration.getFirstName());
		regHistory.setLastName(registration.getLastName());
		regHistory.setEmail(registration.getEmail());
		regHistory.setLocale(registration.getLocale());
		regHistory.setCountry(registration.getCountry());
		regHistory.setOptIn(registration.getOptIn());
		regHistory.setRegistrationDate(registration.getCreated());
		regHistory.setCreated(new Date());
		regHistory.setLastModified(regHistory.getCreated());
		regHistory.setLastModifiedBy("LS Registration Service");
		return regHistory;
	}
}

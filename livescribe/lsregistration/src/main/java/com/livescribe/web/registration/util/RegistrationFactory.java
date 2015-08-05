/**
 * Created:  Aug 14, 2013 11:35:49 AM
 */
package com.livescribe.web.registration.util;

import java.util.Date;
import java.util.Map;

import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.web.registration.controller.RegistrationData;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegistrationFactory {

	/**
	 * <p></p>
	 * 
	 * @param details
	 * 
	 * @return
	 */
	public static Registration create(Map<String, String> details) {
		
		Registration registration = new Registration();
		registration.setAppId(details.get("appId"));
		registration.setDisplayId(details.get("displayId"));
		String editionStr = details.get("edition");
		Integer edition = Integer.parseInt(editionStr);
		registration.setEdition(edition);
		registration.setEmail(details.get("email"));
		registration.setFirstName(details.get("firstName"));
		registration.setLastName(details.get("lastName"));
		registration.setLocale(details.get("locale"));
		registration.setCountry(details.get("country"));
		registration.setPenName(details.get("penName"));
		registration.setPenSerial(details.get("penSerial"));
		Boolean optIn = Boolean.parseBoolean(details.get("optIn"));
		registration.setOptIn(optIn);
		Date now = new Date();
		registration.setCreated(now);
		registration.setLastModified(now);
		
		return registration;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param data
	 * 
	 * @return
	 */
	public static Registration create(RegistrationData data) {
		Registration registration = new Registration();
		
		try {
			PenId penId = PenId.getPenIdObject(data.getPenSerial());
			Date now = new Date();
			
			registration.setAppId(data.getAppId());
			registration.setDisplayId(data.getDisplayId());
			registration.setEdition(data.getEdition());
			registration.setEmail(data.getEmail());
			registration.setFirstName(data.getFirstName());
			registration.setLastName(data.getLastName());
			registration.setLocale(data.getLocale());
			registration.setCountry(data.getCountry());
			registration.setOptIn(data.getOptIn());
			registration.setPenName(data.getPenName());
			registration.setPenSerial(String.valueOf(penId.getId()));
			registration.setDisplayId(penId.toString());
			registration.setCreated(now);
			registration.setLastModified(now);
			registration.setLastModifiedBy("LS Registration Service");
			
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		}
		
		return registration;
	}
}

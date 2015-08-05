/**
 * Created:  Nov 6, 2013 12:02:27 AM
 */
package com.livescribe.web.registration.mock;

import java.util.Random;

import org.apache.log4j.Logger;

import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.web.registration.controller.RegistrationData;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MockRegistrationFactory {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static Random random = new Random();
	
	/**
	 * <p></p>
	 * 
	 */
	public MockRegistrationFactory() {
	}

	/**
	 * <p></p>
	 * 
	 * @param appId The ID of the application registering the pen.
	 * @param displayId The 14-character display ID of the pen.
	 * @param email The email address of the user.
	 * 
	 * @return
	 */
	public static Registration create(String appId, String displayId, String email) {
		
		Registration registration = new Registration();
		
		int index = random.nextInt(100);
		int localeIndex = random.nextInt(5);
		String locale = null;
		String country = null;
		switch (localeIndex) {
		case 0:
			locale = "en_US";
			country = "United States";
			break;
		case 1:
			locale = "fr_FR";
			country = "Français (France)";
			break;
		case 2:
			locale = "de_DE";
			country = "Deutsch (Deutschland)";
			break;
		case 3:
			locale = "es_ES";
			country = "Español (España)";
			break;
		case 4:
			locale = "it_IT";
			country = "Italiano (Italia)";
			break;
		default:
			locale = "";
			country = "";
			break;
		}

		registration.setAppId(appId);
		registration.setCountry(country);
		registration.setEdition(0);
		registration.setEmail(email);
		registration.setFirstName("Jack");
		registration.setLastName("Straw");
		registration.setLocale(locale);
		registration.setOptIn(new Boolean(false));
		registration.setPenName("Random Vector Pen #" + index);
		registration.setPenSerial(displayId);
		
		return registration;
	}
}

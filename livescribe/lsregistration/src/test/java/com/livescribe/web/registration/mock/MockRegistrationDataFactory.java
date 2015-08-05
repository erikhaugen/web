/**
 * Created:  Nov 5, 2013 2:07:24 PM
 */
package com.livescribe.web.registration.mock;

import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.livescribe.web.registration.controller.RegistrationData;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MockRegistrationDataFactory {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String PEN_DISPLAY_ID_1	= "AYE-ASX-DWY-UY";
	private static final String PEN_SERIAL_1		= "2594172913044";
	private static final String APP_ID_1			= "com.livescribe.web.KFMTestApp-JMeter";
	private static Random random = new Random();
	
	/**
	 * <p></p>
	 * 
	 */
	public MockRegistrationDataFactory() {
	}

	/**
	 * <p>Creates a new <code>RegistrationData</code> object with a new UUID as
	 * the <code>appId</code> parameter.</p>
	 * 
	 * <p>This method calls {@link MockRegistrationDataFactory#create(String, String, String)}
	 * with a newly generated UUID.</p>
	 * 
	 * @param displayId
	 * @param email
	 * 
	 * @return
	 */
	public static RegistrationData create(String displayId, String email) {
		
		UUID uuid = UUID.randomUUID();
		
		RegistrationData regData = create(uuid.toString(), displayId, email);
		
		return regData;
	}
	
	/**
	 * <p>Creates a new <code>RegistrationData</code> object.</p>
	 * 
	 * <p>Randomly selects a country and locale, and the pen name includes 
	 * a random integer.</p>
	 * 
	 * @param appId The ID of the application registering the pen.
	 * @param displayId The 14-character display ID of the pen.
	 * @param email The email address of the user.
	 * 
	 * @return
	 */
	public static RegistrationData create(String appId, String displayId, String email) {
		
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
		RegistrationData regData = new RegistrationData();
		regData.setAppId(appId);
		regData.setCountry(country);
		regData.setEdition(0);
		regData.setEmail(email);
		regData.setFirstName("Jack");
		regData.setLastName("Straw");
		regData.setLocale(locale);
		regData.setOptIn(new Boolean(false));
		regData.setPenName("Random Vector Pen #" + index);
		regData.setPenSerial(displayId);
		
		return regData;
	}
}

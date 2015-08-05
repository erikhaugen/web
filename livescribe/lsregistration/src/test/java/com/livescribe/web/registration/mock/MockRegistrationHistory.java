/**
 * Created:  Nov 21, 2013 1:08:33 PM
 */
package com.livescribe.web.registration.mock;

import java.util.Date;
import java.util.Random;

import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.livescribe.web.registration.RandomDate;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MockRegistrationHistory extends RegistrationHistory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1927218610624115451L;
	
	private static Random random = new Random();
	private static final String[] EMAIL			= {"lester02@ls.com", "bob@hotmail.com", "jerry@terrapin.com", "phil@philzone.com", "bill@backbone.com"};
	
	//	Each display ID corresponds to the pen serial number in the same array index.
	private static final String[] DISPLAY_ID	= {"AYE-ASW-KBA-EH", "AYE-ASW-AED-HB", "AYE-ASW-ABK-EH", "AYE-ASX-CM4-KB", "AYE-ASW-AF9-J7"};
	private static final String[] PEN_SERIAL	= {"2594160247175", "2594172882907", "2594172882823", "2594172911908", "2594172882963"};
	
	private static final String[] LOCALE		= {"en_US", "fr_FR", "de_DE", "es_ES", "it_IT"};
	private static final String[] COUNTRY		= {"United States", "France", "Germany", "Spain", "Italy"};
	
	private static final String[] FIRST_NAME	= {"Jack", "Bob", "Jerry", "Phil", "Bill"};
	private static final String[] LAST_NAME		= {"Straw", "Weir", "Garcia", "Lesh", "Kreutzman"};
	private static final String[] PEN_NAME		= {"Test Pen #1", "Test Pen #2", "Test Pen #3", "Test Pen #4", "Test Pen #5"};
	

	/**
	 * <p></p>
	 * 
	 */
	public MockRegistrationHistory() {
		init();
	}

	/**
	 * <p></p>
	 * 
	 * @param appId
	 * @param displayId
	 * @param penSerial
	 * @param email
	 * @param country
	 * @param registrationDate
	 * @param created
	 */
	public MockRegistrationHistory(String appId, String displayId,
			String penSerial, String email, String country,
			Date registrationDate, Date created) {
		super(appId, displayId, penSerial, email, country, registrationDate,
				created);
	}

	/**
	 * <p></p>
	 * 
	 * @param appId
	 * @param displayId
	 * @param edition
	 * @param penSerial
	 * @param penName
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param locale
	 * @param country
	 * @param optIn
	 * @param registrationDate
	 * @param created
	 * @param lastModified
	 * @param lastModifiedBy
	 */
	public MockRegistrationHistory(String appId, String displayId,
			Integer edition, String penSerial, String penName,
			String firstName, String lastName, String email, String locale,
			String country, Boolean optIn, Date registrationDate, Date created,
			Date lastModified, String lastModifiedBy) {
		super(appId, displayId, edition, penSerial, penName, firstName,
				lastName, email, locale, country, optIn, registrationDate,
				created, lastModified, lastModifiedBy);
	}

	/**
	 * <p>Initializes with random data.</p>
	 * 
	 */
	private void init() {
		
		setEdition(0);
		setAppId("RegistrationClientTest");
		
		int idIdx = random.nextInt(5);
		setDisplayId(DISPLAY_ID[idIdx]);
		setPenSerial(PEN_SERIAL[idIdx]);
		
		int emailIdx = random.nextInt(5);
		setEmail(EMAIL[emailIdx]);
		
		int localeIndex = random.nextInt(5);
		setLocale(LOCALE[localeIndex]);
		setCountry(COUNTRY[localeIndex]);
		
		int nameIndex = random.nextInt(5);
		setFirstName(FIRST_NAME[nameIndex]);
		setLastName(LAST_NAME[nameIndex]);
		
		int penNameIndex = random.nextInt(5);
		setPenName(PEN_NAME[penNameIndex]);
		
		RandomDate date = new RandomDate(2010, 2014);
		setRegistrationDate(date);
		
		int trueOrFalse = (int)Math.round(Math.random());
		boolean optIn = (trueOrFalse == 0) ? false : true;
		setOptIn(optIn);
		
		setLastModifiedBy("Registration Client Test");
	}
}

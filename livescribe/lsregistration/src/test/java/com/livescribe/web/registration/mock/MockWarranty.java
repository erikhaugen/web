/**
 * Created:  Nov 21, 2013 11:29:15 AM
 */
package com.livescribe.web.registration.mock;

import java.util.Date;
import java.util.Random;

import com.livescribe.framework.orm.vectordb.Warranty;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MockWarranty extends Warranty {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1622666601802888015L;
	
	private static Random random = new Random();
	private static final String[] EMAIL			= {"lester02@ls.com", "bob@hotmail.com", "jerry@terrapin.com", "phil@philzone.com", "bill@backbone.com"};
	
	//	Each display ID corresponds to the pen serial number in the same array index.
	private static final String[] DISPLAY_ID	= {"AYE-ASW-KBA-EH", "AYE-ASW-AED-HB", "AYE-ASW-ABK-EH", "AYE-ASX-CM4-KB", "AYE-ASW-AF9-J7"};
	private static final String[] PEN_SERIAL	= {"2594160247175", "2594172882907", "2594172882823", "2594172911908", "2594172882963"};
	
	private static final String[] LOCALE		= {"en_US", "fr_FR", "de_DE", "es_ES", "it_IT"};
	private static final String[] FIRST_NAME	= {"Jack", "Bob", "Jerry", "Phil", "Bill"};
	private static final String[] LAST_NAME		= {"Straw", "Weir", "Garcia", "Lesh", "Kreutzman"};
	private static final String[] PEN_NAME		= {"Test Pen #1", "Test Pen #2", "Test Pen #3", "Test Pen #4", "Test Pen #5"};
	
	/**
	 * <p>Randomly generates data for each property.</p>
	 * 
	 */
	public MockWarranty() {
		init();
	}

	/**
	 * <p></p>
	 * 
	 * @param appId
	 * @param displayId
	 * @param penSerial
	 * @param email
	 * @param created
	 */
	public MockWarranty(String appId, String displayId, String penSerial,
			String email, Date created) {
		super(appId, displayId, penSerial, email, created);
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
	 * @param created
	 * @param lastModified
	 * @param lastModifiedBy
	 */
	public MockWarranty(String appId, String displayId, Integer edition,
			String penSerial, String penName, String firstName,
			String lastName, String email, String locale, Date created,
			Date lastModified, String lastModifiedBy) {
		super(appId, displayId, edition, penSerial, penName, firstName,
				lastName, email, locale, created, lastModified, lastModifiedBy);
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
		
		int nameIndex = random.nextInt(5);
		setFirstName(FIRST_NAME[nameIndex]);
		setLastName(LAST_NAME[nameIndex]);
		
		int penNameIndex = random.nextInt(5);
		setPenName(PEN_NAME[penNameIndex]);
		
		setCreated(new Date());
		
		setLastModifiedBy("Registration Client Test");
	}
}

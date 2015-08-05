/**
 * Created:  Nov 21, 2013 1:13:34 PM
 */
package com.livescribe.web.registration;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <p>Represents a random date between 1900 and 2020.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RandomDate extends Date {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4850675581810545525L;

	/**
	 * <p>Generates random date between 1900 and 2020.</p>
	 * 
	 */
	public RandomDate() {
		
		this(1900, 2020);
	}

	/**
	 * <p>Generates random date between the two given years.</p>
	 * 
	 * @param startYear The starting year.
	 * @param endYear The ending year.
	 */
	public RandomDate(int startYear, int endYear) {
		
		GregorianCalendar gc = new GregorianCalendar();
		
		int year = randomBetween(startYear, endYear);
		gc.set(Calendar.YEAR, year);
		
		int maxDayOfYear = gc.getActualMaximum(Calendar.DAY_OF_YEAR);
		int dayOfYear = randomBetween(1, maxDayOfYear);
		gc.set(Calendar.DAY_OF_YEAR, dayOfYear);
		
		long milliseconds = gc.getTimeInMillis();
		setTime(milliseconds);
	}
	
	/**
	 * <p>Generates an integer between the two given integers.</p>
	 * 
	 * @param start The starting integer.
	 * @param end The ending integer.
	 * 
	 * @return an integer between the two given integers.
	 */
	private int randomBetween(int start, int end) {
		
		int r = (int)Math.round(Math.random() * (end - start));
		return r;
	}
}

package com.livescribe.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {
	
	
	public static final long SECONDS_IN_A_DAY = 86400;
	public static final long MINUTES_IN_A_DAY = 1440;
	
	public static final long SECONDS_IN_AN_HOUR = 3600;

	public static DateFormat getSimpleDateOnlyDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	public static DateFormat getAllFieldsDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd G HH:mm:ss:S z");
	}

	public static DateFormat getDateOnlyDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	public static DateFormat getTimeOnlyDateFormat() {
		return new SimpleDateFormat("HH:mm:ss z");
	}

	public static DateFormat getIso8601DateFormat() {
		// 19980717T14:08:55 // YYYY-MM-DDThh:mm:ss.sTZD
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.Sz");
	}
	
	/**
	 * Return Yesterday's Date, again not very intelligent works
	 * for now. Use at your own risk
	 */
	public static Date getYesterday() {
		return getNDaysBefore(1);
	}

	/**
	 * Return Yesterday's Date, again not very intelligent works
	 * for now. Use at your own risk
	 */
	public static Date getNDaysBefore(int amount) {
		return getNDaysAfter(-1 * amount);
	}
	
	/**
	 * Return Yesterday's Date, again not very intelligent works
	 * for now. Use at your own risk
	 */
	public static Date getNDaysAfter(int amount) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, amount);
		c.clear(Calendar.DAY_OF_MONTH);
		return c.getTime();
	}
	
	/**
	 * Return provided date, after adding the "amount" hours
	 * for now. Use at your own risk
	 */
	public static Date addHours(Date date, int amount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR, amount);
		return c.getTime();
	}

	/**
	 * Return provided date, after adding the "amount" minutes
	 * for now. Use at your own risk
	 */
	public static Date addMinutes(Date date, int amount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, amount);
		return c.getTime();
	}
	
	/**
	 * This method could be named better, Here is what it does
	 * If you want to schedule a task every 5 minutes, it is easier
	 * to track it if it happens every hour at 5, 10, 15, 20, 25, 30...
	 * rather than 5 minutes from the time it is triggered 
	 *  
	 *  If you want to skip the first run to make sure there is 
	 *  a minimum delay of "amount", set skipNext to true
	 * 
	 * @param amount
	 * @return
	 */
	public static long getDelayToNextMinute(int amount, boolean skipNext) {
		if ( amount <= 0 || amount > 30 || (60%amount!=0) ) {
			throw new IllegalArgumentException("The amount must be between 1 and 30 and should be a divisor of 60");
		}
		Calendar c = Calendar.getInstance();
		long currentTime = c.getTimeInMillis();
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		
		int min = c.get(Calendar.MINUTE);
		int offset = amount - (min % amount);
		if ( skipNext ) {
			offset += amount;
		}
		c.add(Calendar.MINUTE, offset);		
		return  (c.getTimeInMillis() - currentTime);
	}
		

	/**
	 * This method could be named better, Here is what it does
	 * If you want to schedule a task every 2 hours, it is easier
	 * to track it if it happens at 2 am, 4 am, 6 am, 8 am .... 
	 * rather than 2 hours from the time it is triggered 
	 *  
	 *  If you want to skip the first run to make sure there is 
	 *  a minimum delay of "amount" hours, set skipNext to true
	 * 
	 * @param amount
	 * @return
	 */
	public static long getDelayToNextHour(int amount, boolean skipNext) {
		if ( amount <= 0 || amount > 12 ) {
			throw new IllegalArgumentException("The amount must be between 1 and 12 and be a divisor of 24");
		}
		Calendar c = Calendar.getInstance();
		long currentTime = c.getTimeInMillis();
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		
		int hour = c.get(Calendar.HOUR);
		int offset = amount - (hour % amount);
		if ( skipNext ) {
			offset += amount;
		}
		c.add(Calendar.HOUR, offset);		
		return  (c.getTimeInMillis() - currentTime);
	}
	
	/**
	 * This method could be named better, Here is what it does
	 * If you want to schedule a task every 5 minutes, it is easier
	 * to track it if it happens every hour at 5, 10, 15, 20, 25, 30...
	 * rather than 5 minutes from the time it is triggered 
	 *  
	 *  If you want to skip the first run to make sure there is 
	 *  a minimum delay of "amount", set skipNext to true
	 * 
	 * @param amount
	 * @return
	 */
	public static Date getDateToNextMinute(int amount) {
		Calendar c = Calendar.getInstance();
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		
		int min = c.get(Calendar.MINUTE);
		int offset = amount - (min % amount);
		
		c.add(Calendar.MINUTE, offset);		
		return c.getTime();
	}
		

	/**
	 * This method could be named better, Here is what it does
	 * If you want to schedule a task every 2 hours, it is easier
	 * to track it if it happens at 2 am, 4 am, 6 am, 8 am .... 
	 * rather than 2 hours from the time it is triggered 
	 *  
	 *  If you want to skip the first run to make sure there is 
	 *  a minimum delay of "amount" hours, set skipNext to true
	 * 
	 * @param amount
	 * @return
	 */
	public static Date getDateToNextHour(int amount) {
		Calendar c = Calendar.getInstance();
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		
		int hour = c.get(Calendar.HOUR);
		int offset = amount - (hour % amount);
		c.add(Calendar.HOUR, offset);		
		return  c.getTime();
	}
	
	/**
	 * This method gives you the date for the next 00:00 am
	 * 
	 * @param amount
	 * @return
	 */
	public static Date getFloorAfterDays(int amount) {
		Calendar c = Calendar.getInstance();
		c.clear(Calendar.HOUR_OF_DAY);
		c.clear(Calendar.HOUR);
		c.clear(Calendar.AM_PM);
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
				
		c.add(Calendar.DATE, amount);		
		return  c.getTime();
	}
	
	/**
	 * Gives you the beginning of the day
	 * @param date
	 * @return
	 */
	public static Date getFloorDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.clear(Calendar.HOUR_OF_DAY);
		c.clear(Calendar.HOUR);
		c.clear(Calendar.AM_PM);
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
				
		return  c.getTime();
	}
	
	/**
	 * Gives you the end of the day
	 * @param date
	 * @return
	 */
	public static Date getCeilingDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.clear(Calendar.HOUR);
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		c.add(Calendar.MINUTE, -1);
		return  c.getTime();
	}
	
	/**
	 * Checks if it is the same day in both dates 
	 * @param from
	 * @param to
	 * @return
	 */
	public static boolean isSameDay(Date day1, Date day2) {
		if (day1 == null || day1 == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(day1);
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(day2);
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
				cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
				cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}

	/**
	 * Returns all dates between from and to, not very intelligent 
	 * when time is different, can be optimized
	 * @param from
	 * @param to
	 * @return
	 */
	public static List<Date> getAllDatesBetween(Date from, Date to) {
		if (from == null) {
			throw new IllegalArgumentException("The from date must not be null");
		}
		List<Date> dates = new ArrayList<Date>();
		if ( to == null || isSameDay(from, to) ) {
			dates.add(from);
		} else {
			Calendar c = Calendar.getInstance();
			c.setTime(from);
			Date next = from;
			while ( to.after(next) ) {
				dates.add(next);
				c.add(Calendar.DATE, 1);
				next = c.getTime();
			}
		}
		return dates;
	}

	public static void main(String[] argv) {
		DateFormat df = getAllFieldsDateFormat();
		Date date = new Date();
		System.out.println(df.format(date));
		
		System.out.println(df.format(getDateToNextMinute(5)));
		System.out.println(df.format(getDateToNextHour(1)));
		System.out.println(df.format(getFloorAfterDays(1)));
		System.out.println(df.format(getFloorDay(date)));
		System.out.println(df.format(getCeilingDay(date)));
	}
}

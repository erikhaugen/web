package com.livescribe.concurrent.utils;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.livescribe.base.DateUtils;

/**
 * Quick convenience class that can be used to run scheduled jobs.
 * This cannot be queried or jobs cancelled after they have been
 * submitted. That can be added on later to either this class or a 
 * newer class.
 * 
 * @author smukker
 *
 */
public class SchedulerUtils {

	private final ScheduledExecutorService scheduler;

	public SchedulerUtils() {
		this(5);
	}

	public SchedulerUtils(int size) {
		scheduler = Executors.newScheduledThreadPool(size);
	}

	public void submitForDailyExecution(Runnable runnable) {
		Date now = new Date();
		Date firstExecution = DateUtils.getFloorAfterDays(1);
		long initialDelay = (firstExecution.getTime() - now.getTime())/1000;
		scheduler.scheduleAtFixedRate(runnable, initialDelay, DateUtils.SECONDS_IN_A_DAY, TimeUnit.SECONDS);
	}

	public void submitForHourlyExecution(Runnable runnable, boolean skipNext) {
		submitForHourlyExecution(1, runnable, skipNext);
	}
	
	public void submitForHourlyExecution(int amount, Runnable runnable, boolean skipNext) {
		long initialDelay = DateUtils.getDelayToNextHour(amount, skipNext) / 1000;
		scheduler.scheduleAtFixedRate(runnable, initialDelay, DateUtils.SECONDS_IN_AN_HOUR, TimeUnit.SECONDS);
	}
	
	public void submitForQuarterHourlyExecution(Runnable runnable, boolean skipNext) {
		long initialDelay = DateUtils.getDelayToNextMinute(15, skipNext) / 1000;
		scheduler.scheduleAtFixedRate(runnable, initialDelay, 900, TimeUnit.SECONDS);
	}
	
	public void submitForHalfHourlyExecution(Runnable runnable, boolean skipNext) {
		long initialDelay = DateUtils.getDelayToNextMinute(30, skipNext) /1000;
		scheduler.scheduleAtFixedRate(runnable, initialDelay, 1800, TimeUnit.SECONDS);
	}
	
	public void submitForMinutelyExecution(int amount, Runnable runnable, boolean skipNext) {
		long initialDelay = DateUtils.getDelayToNextMinute(amount, skipNext) / 1000;
		scheduler.scheduleAtFixedRate(runnable, initialDelay, amount*60, TimeUnit.SECONDS); 
	}
	
	public void cleanup(boolean forceNow) {
		if ( scheduler != null ) {
			if ( forceNow ) {
				scheduler.shutdownNow();
			} else {
				scheduler.shutdown();
			}
		}
	}
	
	public static void main (String[] argv) {
		
		long delay = DateUtils.getDelayToNextMinute(5, false) / 1000;
		
		System.out.println("DateUtils.getDelayToNextMinute(5, false) / 1000 = " + delay);
		
		//*
		SchedulerUtils su = new SchedulerUtils(10);
		
		su.submitForHalfHourlyExecution(new ScheduledRunnableTest("submitForHalfHourlyExecution"), false);
		
		su.submitForHourlyExecution(new ScheduledRunnableTest("submitForHourlyExecution"), false);
		
		su.submitForMinutelyExecution(5, new ScheduledRunnableTest("submitForMinutelyExecution"), false);
		
		su.submitForQuarterHourlyExecution(new ScheduledRunnableTest("submitForQuarterHourlyExecution"), false);
		//*/
	}
	
	private static class ScheduledRunnableTest implements Runnable {
		
		private String name;
		ScheduledRunnableTest(String name) {
			this.name = name;
		}
		
		@Override
		public void run() {
			System.out.println("Executing " + name + " @ " + DateUtils.getAllFieldsDateFormat().format(new Date()));
		}
	}
}

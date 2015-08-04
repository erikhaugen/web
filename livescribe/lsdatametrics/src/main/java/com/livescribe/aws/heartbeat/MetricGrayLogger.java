package com.livescribe.aws.heartbeat;

import org.apache.log4j.Logger;

public class MetricGrayLogger {
	private static  Logger logger = Logger.getLogger(MetricGrayLogger.class.getName());
	
	/**
	 * <p>A util method to log data metric to GrayLog</p>
	 * 
	 * @param metric
	 */
	public static void logMetric(String metric) {
		logger.info(metric);
	}
}

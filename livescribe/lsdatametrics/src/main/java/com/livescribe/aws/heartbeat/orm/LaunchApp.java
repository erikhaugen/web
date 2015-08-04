package com.livescribe.aws.heartbeat.orm;

import java.util.Date;

import com.livescribe.aws.heartbeat.annotation.MetricNodeAttribute;

public class LaunchApp extends DataMetric {

	
	@MetricNodeAttribute(name="name", dataType="java.lang.String")
	private String appName;
	
	@MetricNodeAttribute(name="reason", dataType="java.lang.String")
	private String reason;
	
	@MetricNodeAttribute(name="test", dataType="java.lang.Boolean")
	private Boolean isTestData;
	
	@MetricNodeAttribute(name="time", dataType="java.util.Date")
	private Date eventTime;

	public LaunchApp() {
	}

	public String getAppName() {
		return this.appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Boolean getIsTestData() {
		return this.isTestData;
	}

	public void setIsTestData(Boolean isTestData) {
		this.isTestData = isTestData;
	}

	public Date getEventTime() {
		return this.eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "LaunchApp[" +
				" messageId=" + getMessageId() +
				" metricVersion=" + getMetricVersion() +
				" deviceId=" + getDeviceId() +
				" deviceType=" + getDeviceType() +
				" fwVersion=" + getFwVersion() +
				" appName=" + this.appName +
				" reason=" + this.reason +
				" isTestData=" + isTestData +
				" eventTime=" + eventTime.toString() +
				"]";
	}
}

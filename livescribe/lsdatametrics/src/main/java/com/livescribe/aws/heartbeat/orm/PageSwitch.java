package com.livescribe.aws.heartbeat.orm;

// Generated Oct 18, 2011 2:32:03 PM by Hibernate Tools 3.2.2.GA

import java.util.Date;

import com.livescribe.aws.heartbeat.annotation.MetricNodeAttribute;

/**
 * PageSwitch generated by hbm2java
 */
public class PageSwitch extends DataMetric {

	@MetricNodeAttribute(name="PA", dataType="java.lang.String")
	private String pageAddress;
	
	@MetricNodeAttribute(name="page", dataType="java.lang.Integer")
	private Integer page;
	
	@MetricNodeAttribute(name="copy", dataType="java.lang.Integer")
	private Integer copy;
	
	@MetricNodeAttribute(name="test", dataType="java.lang.Boolean")
	private Boolean isTestData;
	
	@MetricNodeAttribute(name="time", dataType="java.util.Date")
	private Date eventTime;

	public PageSwitch() {
	}


	public String getPageAddress() {
		return pageAddress;
	}

	public void setPageAddress(String pageAddress) {
		this.pageAddress = pageAddress;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getCopy() {
		return copy;
	}

	public void setCopy(Integer copy) {
		this.copy = copy;
	}

	public Date getEventTime() {
		return this.eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public Boolean getIsTestData() {
		return isTestData;
	}

	public void setIsTestData(Boolean isTestData) {
		this.isTestData = isTestData;
	}

	@Override
	public String toString() {
		return "PageSwitch[" +
				" messageId=" + getMessageId() +
				" metricVersion=" + getMetricVersion() +
				" deviceId=" + getDeviceId() +
				" deviceType=" + getDeviceType() +
				" fwVersion=" + getFwVersion() +
				" pageAddress=" + this.pageAddress +
				" page=" + this.page +
				" copy=" + this.copy +
				" isTestData=" + isTestData +
				" eventTime=" + eventTime.toString() +
				"]";
	}
}

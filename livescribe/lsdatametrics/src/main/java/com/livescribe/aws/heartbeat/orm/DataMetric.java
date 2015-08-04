package com.livescribe.aws.heartbeat.orm;

import java.util.Date;

/**
 * Abstract class for any metric type
 * @author kle
 *
 */
public abstract class DataMetric implements java.io.Serializable {

	private Long id;
	
	private String messageId;
	private String metricVersion;
	private String deviceId;
	private String deviceType;
	private String fwVersion;
	
	private Date metricSent;
	private Date created;
	private MetricGroup metricGroup;
	
	// Abstract methods
	public abstract Boolean getIsTestData();
	public abstract void setIsTestData(Boolean isTestData);
	public abstract Date getEventTime();
	public abstract void setEventTime(Date eventTime);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMetricVersion() {
		return metricVersion;
	}

	public void setMetricVersion(String metricVersion) {
		this.metricVersion = metricVersion;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public MetricGroup getMetricGroup() {
		return metricGroup;
	}

	public void setMetricGroup(MetricGroup metricGroup) {
		this.metricGroup = metricGroup;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getMetricSent() {
		return metricSent;
	}
	public void setMetricSent(Date metricSent) {
		this.metricSent = metricSent;
	}
	public String getFwVersion() {
		return fwVersion;
	}
	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}

}

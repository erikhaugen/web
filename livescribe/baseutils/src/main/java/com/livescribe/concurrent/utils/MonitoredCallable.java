package com.livescribe.concurrent.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

public abstract class MonitoredCallable<T> implements Callable<T> {

	private String batch;
	private String name;
	private Date scheduledAt;
	private Date startedAt;
	private Date finishedAt;
	protected T result = null;
	
	public MonitoredCallable() {
		this(null);
	}
	
	public MonitoredCallable(String name) {
		this(name, null);
	}
	
	public MonitoredCallable(String name, String batch) {
		this.batch = batch;
		this.name = name;
		this.scheduledAt = new Date();
	}
	
	public final T call() {
		this.startedAt = new Date();
		try {
			result = monitoredCall();
		} catch ( Exception ex ) {
			logException(new Exception("Unable to complete the job due to excpetion:" + ex.getMessage(), ex));
		}
		synchronized (this) {
			finishedAt = new Date();
		}
		return result;
	}
	
	public abstract T monitoredCall();
	
	protected void logException(Exception ex) {
		
	}

	public boolean isFinished() {
		return (finishedAt != null);
	}
	
	public Date getScheduledAt() {
		return scheduledAt;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public Date getFinishedAt() {
		return finishedAt;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public T getResult() {
		return result;
	}
	
	public String getBatch() {
		return batch;
	}
	
	public String getStats() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd G HH:mm:ss z");
		StringBuilder sb = new StringBuilder();
		sb.append("Job["+name);
		sb.append("]-startedAt"+ df.format(startedAt));
		sb.append(",scheduledAt"+ df.format(scheduledAt));
		if ( finishedAt != null ) {
			sb.append(",finishedAt"+ df.format(finishedAt));
		}
		return sb.toString();
	}
}

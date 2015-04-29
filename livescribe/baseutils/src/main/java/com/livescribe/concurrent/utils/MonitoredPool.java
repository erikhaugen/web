package com.livescribe.concurrent.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.livescribe.base.DateUtils;
import com.livescribe.base.StringUtils;

/** 
 * This is a wrapper around an ExecutorService. You are still responsible for creating the Executor Service
 * and passing the reference to this class. But you will not submit tasks to the Service Directly, instead 
 * the tasks will be submitted to this class and it will keep track of all the tasks, so that they can be queried.
 * 
 * PLEASE DONOT USE THIS CLASS YET. IT IS NOT COMPLETE, IT WILL LEAD TO RESOURCE LEAKS, IT NEEDS SOME WORK TO BE
 * DONE. THIS WILL BE ADDED TO SVN TO PREVENT IT FROM BEING LOST.  SMukker
 * 
 * @author smukker
 *
 */
public class MonitoredPool {

	private ExecutorService executorService;
	
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
		
	@SuppressWarnings("unchecked")
	Map<String, MonitoredCallable> taskMap = new HashMap<String, MonitoredCallable>();
	
	public MonitoredPool(ExecutorService executorService) {
		this.executorService = executorService;
		
		// We want to purge jobs every 5 minutes 		
		scheduledExecutorService.schedule( new Runnable() {
			public void run() {
				purgeOldJobs();
			}
		}, 5*60, TimeUnit.SECONDS);
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}
	
	/** 
	 * 
	 * @param task
	 * @return the jobId that should be used to check upon this task
	 */
	@SuppressWarnings("unchecked")
	public String submit(MonitoredCallable task) {
		executorService.submit(task);
		String jobId = task.getName();
		jobId = (jobId==null ? DateUtils.getAllFieldsDateFormat().format(new Date()) : jobId);
		int i = 0;
		String suffix = "";
		MonitoredCallable callable = taskMap.get(jobId);
		while ( callable != null ) {
			suffix = "-" + ++i;
			callable = taskMap.get(jobId+suffix);
		} 
		jobId = jobId + suffix;
		taskMap.put(jobId, task);
		return jobId;
	}
	
	@SuppressWarnings("unchecked")
	public String getStatus(String taskId) {
		MonitoredCallable task = taskMap.get(taskId);
		String status =  "Unknown Task, It may have finished executing and purged";
		
		if (task!=null) {
			status = task.getStats();
			if ( task.isFinished() ) {
				taskMap.remove(taskId);
			}
		}
		
		return status;
	}
	
	@SuppressWarnings("unchecked")
	public MonitoredCallable getJob(String taskId) {
		return taskMap.get(taskId);
	}
	
	@SuppressWarnings("unchecked")
	public boolean purgeJob(String taskId) {
		MonitoredCallable task = taskMap.get(taskId);
		if ( task != null && task.isFinished() ) {
			taskMap.remove(taskId);
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public String getStatus(String batchId, String separator) {
		boolean ignoreBatchId = false;
		if ( StringUtils.isBlank(batchId) ) {
			ignoreBatchId  = true;
		}
		separator = ( StringUtils.isBlank(separator) ? "\n" : separator);
		StringBuilder sb = new StringBuilder();
		Set<Map.Entry<String, MonitoredCallable>> tasks = taskMap.entrySet();
	
		for (Map.Entry<String, MonitoredCallable> task : tasks ) {
			MonitoredCallable callable = task.getValue();
			if ( ignoreBatchId || callable.getBatch().equals(batchId) ) {
				sb.append(task.getKey() + ": " + callable.getStats());
				sb.append(separator);
			}
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	public void purgeCompletedJobs(String batchId) {
		Set<Map.Entry<String, MonitoredCallable>> tasks = taskMap.entrySet();

		boolean ignoreBatchId = false;
		if ( StringUtils.isBlank(batchId) ) {
			ignoreBatchId  = true;
		}

		List<String> keysToRemove = new ArrayList<String>();
		for (Map.Entry<String, MonitoredCallable> task : tasks ) {
			MonitoredCallable callable = task.getValue();
			if ( ignoreBatchId || callable.getBatch().equals(batchId) ) {
				if ( callable.isFinished() ) {
					keysToRemove.add(task.getKey());
				}
			}
		}
		
		for ( String key : keysToRemove ) {
			taskMap.remove(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void purgeOldJobs() {
		Date purgeBefore = DateUtils.addHours(new Date(), -2);
		Set<Map.Entry<String, MonitoredCallable>> tasks = taskMap.entrySet();
		List<String> keysToRemove = new ArrayList<String>();
		for (Map.Entry<String, MonitoredCallable> task : tasks ) {
			MonitoredCallable callable = task.getValue();
			Date finishTime = callable.getFinishedAt();
			if ( finishTime != null && purgeBefore.after(finishTime) ) {
				keysToRemove.add(task.getKey());
			}
		}
		for ( String key : keysToRemove ) {
			taskMap.remove(key);
		}
	}
	
	public boolean isTerminated() {
		return executorService.isTerminated();
	}
	
	public void shutdown() {
		executorService.shutdown();
	}
	
}

package com.livescribe.importsecretkeytool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.livescribe.importsecretkeytool.cli.Configuration;

public class EmailTask implements Runnable {
	private static Logger log = Logger.getLogger(EmailTask.class);
	private static ExecutorService execSvc = Executors.newFixedThreadPool(5);
	
	private List<String> emails;
	private String subject;
	private String content;
	
	public static void exec(EmailTask task) {
		execSvc.execute(task);
	}
	
	public static void shutdown() throws InterruptedException {
		execSvc.shutdown();
		while (!execSvc.awaitTermination(30, TimeUnit.SECONDS));
		log.info("Email thread pool is now shutdown graceful.");
	}
	
	public EmailTask(List<String> emails, String subject, String content) {
		this.emails = new ArrayList<String>(emails);
		this.subject = subject + " - " + Configuration.getConfiguration().getConfigName();
		this.content = content;
	}
	
	public void run() {
		Configuration conf = Configuration.getConfiguration();
		if (conf.isMailEnabled()) {
			Utils.sendEmail(conf.getMailSender(), emails, subject, content);
			log.info("Sent mail successfully (subject='" + subject + "').");
		}
	}
}

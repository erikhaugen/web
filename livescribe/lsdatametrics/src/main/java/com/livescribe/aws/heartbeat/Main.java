/*
 * Created:  Jul 20, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.heartbeat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.commons.daemon.support.DaemonLoader;
import org.apache.log4j.xml.DOMConfigurator;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.BasicAWSCredentials;
import com.livescribe.aws.heartbeat.msg.SQSMessage;
import com.livescribe.aws.heartbeat.orm.DataMetric;

/**
 * <p>Application entry point.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Component
public class Main implements HeartbeatConstants {

	private static final ApplicationContext ac = new ClassPathXmlApplicationContext("heartbeat-context.xml");
	
	//	Shutdown delay time in seconds.
	private static final int shutdownTime = 2;
	
	//	TODO:  Repair location of transaction boundary.
//	@Autowired
//	private static DataMetricsTransferServiceImpl txfr;
	
	/**
	 * <p></p>
	 * 
	 */
	public Main() {
		
	}

	/**
	 * <p></p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		if (args.length > 0) {
			//	TODO:  Allow for command-line parameters.
			//	- wait time
		}
		
		DOMConfigurator.configureAndWatch("log4j.xml");

		DataMetricsTransferService txfr = (DataMetricsTransferService)ac.getBean("dataMetricsTxfr");

		Thread t = new Thread(txfr);
		t.start();
		
	}

	/**
	 * <p>Invoked by the JVM runtime during shutdown.</p>
	 * 
	 */
	public static void shutdownHook() {
		
		log("Shutting down ...");
		long start = System.currentTimeMillis();
		while(true) {
			try {
				Thread.sleep(500);
			}
			catch (Exception e) {
				log("Exception thrown:  " + e.toString());
				break;
			}
			if (System.currentTimeMillis() - start > shutdownTime * 1000) {
				break;
			}
		}
		log("Shutdown complete.");
	}
	
	private static void log(String msg) {
		
		System.out.println(getTimestamp() + ":  " + msg);
	}

	private static String getTimestamp() {

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return f.format(new Date());
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	private static String getPid() {
		
		File procSelf = new File("/proc/self");
		if (procSelf.exists()) {
			try {
				return procSelf.getCanonicalFile().getName();

			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		File bash = new File("/bin/bash");
		if (bash.exists()) {
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "echo $PPID");
			try {
				Process p = pb.start();
				BufferedReader rd = new BufferedReader(new InputStreamReader(p.getInputStream()));
				return rd.readLine();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
				return String.valueOf(Thread.currentThread().getId());
			}
		}
		return String.valueOf(Thread.currentThread().getId());
	}
}

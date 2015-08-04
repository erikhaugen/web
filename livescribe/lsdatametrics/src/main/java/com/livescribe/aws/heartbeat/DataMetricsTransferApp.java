/*
 * Created:  Jul 18, 2011
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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.livescribe.aws.heartbeat.config.HeartbeatProperties;
import com.livescribe.aws.heartbeat.msg.SQSMessage;
import com.livescribe.aws.heartbeat.orm.DataMetric;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 * @deprecated Use {@link com.livescribe.aws.heartbeat.DataMetricsTransferService} intstead.
 */
@Component
@Deprecated 
public class DataMetricsTransferApp implements HeartbeatConstants, Daemon {

//	private static final ApplicationContext ac = new ClassPathXmlApplicationContext("heartbeat-context.xml");
	
	private static Logger logger = Logger.getLogger(DataMetricsTransferApp.class.getName());
	
//	private static String PROPERTY_FILE_NAME_HEARTBEAT	= "heartbeat.properties";
//	private static String PROPERTY_FILE_NAME_AWS		= "aws.properties";
//	
//	private static String AWS_ACCESS_KEY_ID	= "AKIAIINJP64RNYF4MKWQ";
//	private static String AWS_SECRET_KEY	= "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";
////	private static String AWS_ACCESS_KEY_ID	= "";
////	private static String AWS_SECRET_KEY	= "";
//
//	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//	
//	private static String ENV = "";
	
//	@Autowired
//	private static DataMetricDao dataMetricDao;
	
//	private static DataMetricDao dataMetricDao = (DataMetricDao)ac.getBean("dataMetricDao");
//	private static SessionFactory sessionFactory = (SessionFactory)ac.getBean("sessionFactory");
	
//	@Autowired
//	@Qualifier("heartbeatProperties")
	private static HeartbeatProperties heartbeatProperties;
	
//	private static Properties props = new Properties();
//	private static Properties awsProps = new Properties();
	private static AmazonSQSClient sqsClient;
	
	/**
	 * <p></p>
	 * 
	 */
	public DataMetricsTransferApp() {
		
	}

	/**
	 * <p></p>
	 * 
	 * @param credentials
	 */
	public DataMetricsTransferApp(BasicAWSCredentials credentials) {
		
		sqsClient = new AmazonSQSClient(credentials);
	}
	
//	/**
//	 * <p></p>
//	 * 
//	 * @param args
//	 */
//	public static void main(String[] args) {
//
////		String pid = getPid();
////		System.out.println("pid:  " + pid);
//		
////		Properties props = new Properties();
////		Properties awsProps = new Properties();
////		FileInputStream fis;
////		FileInputStream awsFis;
//		InputStream in = null;
//		InputStream awsIn = null;
//		try {
//			in = DataMetricsTransferApp.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME_HEARTBEAT);
//			awsIn = DataMetricsTransferApp.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME_AWS);
//			props.load(in);
//			awsProps.load(awsIn);
//		}
//		catch (FileNotFoundException fnfe) {
//			System.out.println("File '" + PROPERTY_FILE_NAME_HEARTBEAT + "' or '" + PROPERTY_FILE_NAME_AWS + "' not found.");
//			fnfe.printStackTrace();
//			return;
//		}
//		catch (IOException ioe) {
//			System.out.println("IOException thrown while attempting to read property files.");
//			ioe.printStackTrace();
//			return;
//		}
//		finally {
//			try {
//				in.close();
//				awsIn.close();
//			}
//			catch (IOException ioe) {
//				System.out.println("IOException thrown while attempting to close property file input streams.");
//				ioe.printStackTrace();
//				return;
//			}
//		}
//
//		String env = System.getenv().get("ENV").toLowerCase();
//		String queueUrl = props.getProperty(env + "." + PROP_QUEUE_URL);
////		AWS_ACCESS_KEY_ID = awsProps.getProperty(env + "." + PROP_AWS_ACCESS_KEY_ID);
////		AWS_SECRET_KEY = awsProps.getProperty(env + "." + PROP_AWS_SECRET_KEY);
//		
//		StringBuilder configStr = new StringBuilder();
//		configStr.append("\n                  env:  " + env + "\n");
//		configStr.append("    Using AccessKeyID:  " + AWS_ACCESS_KEY_ID + "\n");
//		configStr.append("    Using   SecretKey:  " + AWS_SECRET_KEY + "\n");
//		configStr.append("                  URL:  " + queueUrl + "\n\n");
//		System.out.println(configStr.toString());
//		
//		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
//		sqsClient = new AmazonSQSClient(credentials);
//
//		//	Check the queue for waiting messages.
//		int msgCount = checkQueueForMessages(queueUrl);
//		
//		if (msgCount == 0) {
//			System.out.println("\nQueue was empty.");
//			return;
//		}
//		
//		//	Receive messages from the SQS queue.
//		List<SQSMessage> messages = receiveMessageFromQueue(queueUrl, 1);
//		
//		System.out.println("Received " + messages.size() + " messages.");
//		
//		//	Save messages to MySQL.
//		ArrayList<String> handles = new ArrayList<String>();
//		for (SQSMessage msg : messages) {
//			DataMetric metric = DataMetricFactory.createDataMetric(msg);
//			try {
//				saveMetric(metric);
//				handles.add(msg.getReceiptHandle());
//			}
//			catch (HibernateException he) {
//				System.out.println("HibernateException thrown while attempting to save metric to MySQL.");
//				he.printStackTrace();
//			}			
//		}
//		
//		System.out.println("Successfully saved " + handles.size() + " metrics to MySQL.");
//		
//		//	Delete messages that were successfully saved.
//		deleteMessagesFromQueue(queueUrl, handles);
//		
//	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public static int checkQueueForMessages() {
		
		String queueUrl = heartbeatProperties.getProperty(PROP_QUEUE_URL);
		return checkQueueForMessages(queueUrl);
	}
	
	/**
	 * <p>Returns the number of messages in the queue waiting to be received.</p>
	 * 
	 * @param queueUrl The URL of the SQS queue.
	 * 
	 * @return the number of messages in the queue waiting to be received.
	 */
	public static int checkQueueForMessages(String queueUrl) {
		
		GetQueueAttributesRequest gqaReq = new GetQueueAttributesRequest();
		TreeSet<String> attributeNames = new TreeSet<String>();
		attributeNames.add(SQS_ATTRIBUTE_APPROX_NUM_OF_MSGS);
		gqaReq.setAttributeNames(attributeNames);
		gqaReq.setQueueUrl(queueUrl);
		
		GetQueueAttributesResult result = sqsClient.getQueueAttributes(gqaReq);
		
		Map<String, String> resultMap = result.getAttributes();
		String msgCountStr = resultMap.get(SQS_ATTRIBUTE_APPROX_NUM_OF_MSGS);
		int msgCount = Integer.parseInt(msgCountStr);
		
		System.out.println("Queue has " + msgCount + " waiting messages.");
		
		return msgCount;
	}
	
	public static void deleteMessagesFromQueue(List<String> handles) {
		
		String queueUrl = heartbeatProperties.getProperty(PROP_QUEUE_URL);
		deleteMessagesFromQueue(queueUrl, handles);
	}
	

	/**
	 * <p></p>
	 * 
	 * @param queueUrl
	 * @param handles
	 */
	public static void deleteMessagesFromQueue(String queueUrl, List<String> handles) {
		
		DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest();
		deleteMessageRequest.setQueueUrl(queueUrl);
		
		for (String handle : handles) {
			deleteMessageRequest.setReceiptHandle(handle);
			sqsClient.deleteMessage(deleteMessageRequest);
			System.out.println("Deleted '" + handle + "'");
		}
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
	
	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public static List<SQSMessage> receiveMessageFromQueue() {
		
		String queueUrl = heartbeatProperties.getProperty(PROP_QUEUE_URL);
		return receiveMessageFromQueue(queueUrl, 1);
	}
	
	/**
	 * <p>Returns a <code>List</code> of receipt handles, or an empty <code>List</code> if none were received.</p>
	 * 
	 * @param queueUrl
	 * @param msgCount
	 * 
	 * @return a List of receipt handles, or an empty List if none were received.
	 */
	public static List<SQSMessage> receiveMessageFromQueue(String queueUrl, int msgCount) {
		
		ArrayList<SQSMessage> sqsMessages = new ArrayList<SQSMessage>();
		
		if (msgCount == 0) {
			return sqsMessages;
		}
		
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		
		int msgs = msgCount;
		int batch = 99;
		while (msgs > 0) {
			if (msgs % 10 == 0) {
				batch = 10;
				msgs-=10;
			}
			else {
				batch = msgs;
				msgs-=msgs;
			}
			System.out.println("batch = " + batch);
			
			receiveMessageRequest.setMaxNumberOfMessages(batch);
			
			//	Request "All" attributes of the message.
			ArrayList<String> attributeNames = new ArrayList<String>();
			attributeNames.add("All");
			receiveMessageRequest.setAttributeNames(attributeNames);
			
			//	Get the message(s) from the queue.
			List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).getMessages();
			
			//	For each message received, convert it to an SQS Message object.
			for (Message message : messages) {
				SQSMessage sqsMsg = new SQSMessage(message);				
				System.out.println(sqsMsg.toString());
				sqsMessages.add(sqsMsg);
			}
		}
		return sqsMessages;
	}

	/**
	 * <p>Wraps the persistence of a metric in a Hibernate transaction.</p>
	 * 
	 * @param metric
	 */
	@Transactional
	public static void saveMetric(DataMetric metric) throws HibernateException {
		
		logger.debug("Saving ...");
//		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
//		dataMetricDao.persist(metric);
//		tx.commit();
	}

	@Override
	public void destroy() {
		
		
	}

	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		
		
	}

	@Override
	public void start() throws Exception {
		
		
	}

	@Override
	public void stop() throws Exception {
		
		
	}
}

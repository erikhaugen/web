/*
 * Created:  Jul 21, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.heartbeat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.livescribe.aws.heartbeat.config.AppProperties;
import com.livescribe.aws.heartbeat.config.HeartbeatProperties;
import com.livescribe.aws.heartbeat.msg.SQSMessage;
import com.livescribe.aws.heartbeat.orm.DataMetric;
import com.livescribe.aws.heartbeat.orm.MetricGroup;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
//@Service
public class DataMetricsTransferServiceImpl implements DataMetricsTransferService, HeartbeatConstants {

	private Logger logger = Logger.getLogger(DataMetricsTransferService.class.getName());
	
	//	Default URL to SQS queue in DEV environment.
	private String QUEUE_URL = "https://queue.amazonaws.com/811011210524/datametrics";
	
	//	Shutdown delay time in seconds.
	private static final int shutdownTime = 2;

	@Autowired
	private Map<String, GenericDao> daoMap;
	
	@Autowired
	private AppProperties heartbeatProperties;
	
	private AmazonSQSClient sqsClient;
	private String logFileName = "/Livescribe/Tomcat/Logs/heartbeat-service.log";
	
	//	Polling interval in milliseconds.  Default is 1 minute.
	private long pollingInterval = 60000;
	
	private volatile boolean stop = false;
	
	/**
	 * <p>Default class constructor.</p>
	 * 
	 * This constructor does NOT create the SQS client object needed to
	 * communicate with the SQS queue.
	 */
	public DataMetricsTransferServiceImpl() throws IOException {
		
		//	Create a Thread to be used during shutdown of this process.
		Thread runtimeHookThread = new Thread() {
			public void run() {
				shutdownHook();
			}
		};
		
		//	Register shutdown Thread with the runtime.
		Runtime.getRuntime().addShutdownHook (runtimeHookThread);

//  This part is used when invoking this class from Main.main().
//	Comment these two lines out when running this class as a Thread.
//		BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAIINJP64RNYF4MKWQ", "T5oMj+mUenA+67gNled2avx7w6sn8eVQ9oDUXzF5");
//		sqsClient = new AmazonSQSClient(credentials);
		
//		setupLogging(logFileName);
	}

	/**
	 * <p>Constructor that takes AWS credentials as a parameter.</p>
	 * 
	 * Uses a default log file name.
	 * 
	 * @param credentials The combined AWS Access Key ID and Secret Key.
	 * 
	 * @throws IOException If an error occurs when copying the log file for 
	 * backup.
	 */
	public DataMetricsTransferServiceImpl(BasicAWSCredentials credentials) throws IOException {
		this();
		
		//	Overrides properties configuration.
		sqsClient = new AmazonSQSClient(credentials);
		
//		setupLogging(logFileName);
	}

	/**
	 * <p></p>
	 * 
	 * @param credentials The combined AWS Access Key ID and Secret Key.
	 * @param filename Name of the file to use in logging activity.
	 */
	public DataMetricsTransferServiceImpl(BasicAWSCredentials credentials, String filename) throws FileNotFoundException, IOException {
		
		this(credentials);
		this.logFileName = filename;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		String accessKeyId = heartbeatProperties.getProperty(PROP_AWS_ACCESS_KEY_ID);
		String secretKey = heartbeatProperties.getProperty(PROP_AWS_SECRET_KEY);
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretKey);
		sqsClient = new AmazonSQSClient(credentials);

		StringBuilder banner = new StringBuilder();
		banner.append("\n==================================================\n");
		banner.append("                 S e t t i n g s \n");
		//banner.append("    Environment:  " + heartbeatProperties. .getEnv() + "\n");
		banner.append("  Access Key ID:  " + accessKeyId + "\n");
		banner.append("     Secret Key:  " + secretKey + "\n");
		banner.append("          Queue:  " + heartbeatProperties.getProperty(PROP_QUEUE_URL) + "\n");
		banner.append("==================================================\n");

		logger.debug(banner.toString());
		
		//	Run until told to stop.
		while (!stop) {
			
			int waitingMsgs = checkQueueForMessages();

			if (waitingMsgs < 0) {
				logger.error("An error has occurred.  Shutting down ...");
				break;
			}
			
			if (waitingMsgs == 0) {
				logger.debug("Queue was empty.");
			}
			
			//-------------------------------------------------
			//	Keep running until we have cleared the queue.
			//-------------------------------------------------
			while (waitingMsgs > 0) {
				
				logger.info("Found " + waitingMsgs + " waiting messages in the queue.");
				
				//	Receive messages from the SQS queue.
				List<SQSMessage> messages = null;
				try {
					messages = receiveMessageFromQueue();
				}
				catch (AmazonServiceException ase) {
					logger.error("AmazonServiceException thrown while receiving messages from the SQS queue.", ase);
					break;
				}
				catch (AmazonClientException ace) {
					logger.error("AmazonClientException thrown while receiving messages from the SQS queue.", ace);
					break;
				}
				
				logger.info("Received " + messages.size() + " messages from queue.");
				
				int totalObtained = 0;
				int totalSaved = 0;
				
				//	Save messages to MySQL.
				ArrayList<String> handles = new ArrayList<String>();
				for (SQSMessage msg : messages) {
					// Skip message that contains no metrics
					if (msg.getMetricList() != null && msg.getMetricList().size() == 0) {
						handles.add(msg.getReceiptHandle());
						continue;
					}

					totalObtained += msg.getMetricList().size();
					
					//GSK 02/06/2013: DE6719 - Wifi DataMetrics crashes with invalid date
					// capture any exception if that is thrown while processing
					try {
						
						// Create metric group
						MetricGroup metricGroup = new MetricGroup(msg);
						GenericDao metricGroupDao = daoMap.get(MetricGroup.class.getName());
						metricGroupDao.persist(metricGroup);
	
						// Get metric list
						List<DataMetric> metricList = msg.getMetricList();
						int metricCount = metricList.size();
						
						int savedCount = 0;
	
						//	If the SQS Message contains metrics parsed from the 
						//	message body, save them.
						if (!msg.getMetricList().isEmpty()) {
							for (DataMetric metric : metricList) {
								try {
									metric.setMetricGroup(metricGroup);
									metric.setCreated(new Date());
									saveMetric(metric);
									MetricGrayLogger.logMetric(metric.toString());	// log data metric to GrayLog
									savedCount++;
								}
								catch (Exception he) {
									//GSK (02/06/13): DE6719 - Ignoring the persist exception or any other, if any 
									StringBuilder builder = new StringBuilder();
									builder.append("\n--------------------\n");
									builder.append("  HibernateException thrown while attempting to save metric to MySQL.\n");
									builder.append("      Message ID:  " + msg.getMessageId() + "\n");
									builder.append("  Receipt Handle:  " + msg.getReceiptHandle() + "\n");
									builder.append("    Message Body:  " + msg.getBody() + "\n");
									builder.append("--------------------\n");
									logger.error(builder.toString(), he);
									
									if (!handles.contains(msg.getReceiptHandle())) {
										handles.add(msg.getReceiptHandle());
									}
								}
							}
							totalSaved += savedCount;
						}
						
						//	Only delete the message when ALL of the metrics have
						//	been saved successfully.
						if (savedCount ==  metricCount) {
							handles.add(msg.getReceiptHandle());
						} else {
							//	TODO:  Send an alert!  Store the failures somewhere.
							logger.warn("Saved only " + savedCount + " metrics out of " + metricCount + " metrics.");
						}
					}
					catch (Exception ex) {
						//GSK (02/06/13): DE6719 - Ignoring the persist exception or any other, if any 
						logger.fatal("Exception occured during metric - ignoring the metric message.", ex);
						// remove the current metric block form the list
						if (!handles.contains(msg.getReceiptHandle())) {
							handles.add(msg.getReceiptHandle());
						}
					}
				}

				logger.debug("Successfully saved " + totalSaved + " metrics and skipped " + (totalObtained-totalSaved) + " metrics from " + handles.size() + " messages.");
				
				//	Delete messages.  We logged message bodies that caused 
				//	errors if we need them later.
				deleteMessagesFromQueue(handles);
				
				waitingMsgs = checkQueueForMessages();
			}

			//-------------------------------------------------
			//	Sleep for the polling interval
			//-------------------------------------------------
			try {
				Thread.sleep(pollingInterval);
			}
			catch (InterruptedException ie) {
				logger.error("InterruptedException thrown during 'sleep'.", ie);
			}				
		}
	}

		
	/**
	 * <p>Returns the number of messages in the queue waiting to be received.</p>
	 * 
	 * Calls <code>checkQueueForMessages(String)</code> with a default URL to 
	 * an SQS queue configured in the <code>heartbeat.properties</code> file.
	 * 
	 * @return the number of messages in the queue waiting to be received.
	 * 
	 * @see DataMetricsTransferServiceImpl.checkQueueForMessages(String)
	 */
	public int checkQueueForMessages() {
		
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
	public int checkQueueForMessages(String queueUrl) {
		
		if ((queueUrl == null) || ("".equals(queueUrl))) {
			logger.error("checkQueueForMessages():  No queue URL was provided.");
			return -1;
		}
		
		GetQueueAttributesRequest gqaReq = new GetQueueAttributesRequest();
		TreeSet<String> attributeNames = new TreeSet<String>();
		attributeNames.add(SQS_ATTRIBUTE_APPROX_NUM_OF_MSGS);
		gqaReq.setAttributeNames(attributeNames);
		gqaReq.setQueueUrl(queueUrl);
		
		if (sqsClient == null) {
			logger.error("checkQueueForMessages():  Connection to SQS could not be established.");
			return -2;
		}
		
		GetQueueAttributesResult result = sqsClient.getQueueAttributes(gqaReq);
		
		Map<String, String> resultMap = result.getAttributes();
		String msgCountStr = resultMap.get(SQS_ATTRIBUTE_APPROX_NUM_OF_MSGS);
		int msgCount = Integer.parseInt(msgCountStr);
		
//		log("Queue has " + msgCount + " waiting messages.");
		
		return msgCount;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.heartbeat.DataMetricsTransferService#deleteMessagesFromQueue(java.util.List)
	 */
	public void deleteMessagesFromQueue(List<String> handles) {
		
		String queueUrl = heartbeatProperties.getProperty(PROP_QUEUE_URL);
		deleteMessagesFromQueue(queueUrl, handles);
	}
	
	/**
	 * <p>Deletes messages identified by the given handles from the given
	 * SQS queue.</p>
	 * 
	 * @param queueUrl The URL to the SQS queue to delete messages from. 
	 * @param handles The unique &apos;handles&apos; of the messages to delete.
	 */
	public void deleteMessagesFromQueue(String queueUrl, List<String> handles) {
		
		DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest();
		deleteMessageRequest.setQueueUrl(queueUrl);
		logger.debug("queueUrl:  " + queueUrl);
		
		for (String handle : handles) {
			logger.debug("Deleting handle:  " + handle);
			deleteMessageRequest.setReceiptHandle(handle);
			sqsClient.deleteMessage(deleteMessageRequest);
			logger.debug("Deleted '" + handle + "'");
		}
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public List<SQSMessage> receiveMessageFromQueue() throws AmazonClientException, AmazonServiceException {
		
		String queueUrl = heartbeatProperties.getProperty(PROP_QUEUE_URL);
		return receiveMessageFromQueue(queueUrl);
	}
	
	/**
	 * <p>Returns a <code>List</code> of receipt handles, or an empty 
	 * <code>List</code> if none were received.</p>
	 * 
	 * @param queueUrl The URL to the SQS queue to receive messages from. 
	 * @param msgCount The number of messages to receive (maximum is 10).
	 * 
	 * @return a List of receipt handles, or an empty List if none were received.
	 */
	public List<SQSMessage> receiveMessageFromQueue(String queueUrl) throws AmazonClientException, AmazonServiceException {
		
		ArrayList<SQSMessage> sqsMessages = new ArrayList<SQSMessage>();
		
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		
		//	Request the maximum of 10 messages from the queue.
		receiveMessageRequest.setMaxNumberOfMessages(10);
		
		//	Request "All" attributes of the message.
		ArrayList<String> attributeNames = new ArrayList<String>();
		attributeNames.add("All");
		receiveMessageRequest.setAttributeNames(attributeNames);
		
		//	Get the message(s) from the queue.
		List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).getMessages();
		
		//	For each message received, convert it to an SQS Message object.
		for (Message message : messages) {
			SQSMessage sqsMsg = new SQSMessage(message);				
			logger.debug(sqsMsg.toString());
			sqsMessages.add(sqsMsg);
		}
		
		return sqsMessages;
	}

	/**
	 * <p>Wraps the persistence of a metric in a Hibernate transaction.</p>
	 * 
	 * @param metric
	 */
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveMetric(DataMetric metric) throws HibernateException {
//		logger.debug("Saving ...");
		String className = metric.getClass().getName();
		
		GenericDao dao = daoMap.get(className);
		if (dao == null) {
			throw new HibernateException("Could not find DAO class for " + className);
		}
		dao.persist(metric);
		
		//	TODO:  Move transaction boundary here.
//		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
//		dataMetricDao.persist(metric);
//		tx.commit();
	}

	/**
	 * <p>Invoked by the JVM runtime during shutdown.</p>
	 * 
	 */
	public void shutdownHook() {
		
		logger.info("Shutting down ...");
		long start = System.currentTimeMillis();
		while(true) {
			try {
				Thread.sleep(500);
			}
			catch (Exception e) {
				logger.error("Exception thrown:  " + e.toString());
				break;
			}
			if (System.currentTimeMillis() - start > shutdownTime * 1000) {
				break;
			}
		}
		logger.info("Shutdown complete.");
	}

	/**
	 * <p></p>
	 * 
	 * @return the pollingInterval
	 */
	public long getPollingInterval() {
		return pollingInterval;
	}

	/**
	 * <p></p>
	 * 
	 * @param pollingInterval the pollingInterval to set
	 */
	public void setPollingInterval(long pollingInterval) {
		this.pollingInterval = pollingInterval;
	}

	public void setDaoMap(Map<String, GenericDao> daoMap) {
		this.daoMap = daoMap;
	}

	public Map<String, GenericDao> getDaoMap() {
		return daoMap;
	}
	
}

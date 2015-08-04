/*
 * Created:  Jul 21, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.heartbeat;

import java.util.List;

import org.hibernate.HibernateException;

import com.livescribe.aws.heartbeat.msg.SQSMessage;
import com.livescribe.aws.heartbeat.orm.DataMetric;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface DataMetricsTransferService extends Runnable {

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
	public int checkQueueForMessages();
	
	/**
	 * <p>Deletes messages identified by the given handles from the given
	 * SQS queue.</p>
	 * 
	 * Calls <code>deleteMessagesFromQueue(String, String)</code> with a default 
	 * URL to an SQS queue configured in the <code>heartbeat.properties</code>
	 * file.
	 * 
	 * @param queueUrl The URL to the SQS queue to delete messages from. 
	 * @param handles The unique &apos;handles&apos; of the messages to delete.
	 */
	public void deleteMessagesFromQueue(List<String> handles);
	
	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public List<SQSMessage> receiveMessageFromQueue();
	
	/**
	 * <p></p>
	 * 
	 * @param metric
	 * 
	 * @throws HibernateException
	 */
	public void saveMetric(DataMetric metric) throws HibernateException;
}

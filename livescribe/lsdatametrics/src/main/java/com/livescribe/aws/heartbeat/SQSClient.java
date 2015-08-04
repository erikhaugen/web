/*
 * Created:  Jul 12, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.heartbeat;

import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class SQSClient {

	private static String AWS_ACCESS_KEY_ID	= "AKIAIDHHQFORHE4BAOJA";
	private static String AWS_SECRET_KEY	= "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";
	private static String AWS_QUEUE_URL		= "https://queue.amazonaws.com/783265720945/datametrics";
	
	private static AmazonSQSClient sqsClient;
	
	/**
	 * <p></p>
	 * 
	 */
	public SQSClient() {
	}

	/**
	 * <p></p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
//		if (args.length < 2) {
//			System.out.println("USAGE:  java -jar sqs-util.jar < option > < text >");
//			return;
//		}
		
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
		sqsClient = new AmazonSQSClient(credentials);
		
		receiveMessageFromQueue();
	}

	public static void receiveMessageFromQueue() {
		
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(AWS_QUEUE_URL);
		receiveMessageRequest.setMaxNumberOfMessages(10);
		List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).getMessages();
		for (Message message : messages) {
			System.out.println("  Message");
			System.out.println("    MessageId:     " + message.getMessageId());
			System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
			System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
			System.out.println("    Body:          " + message.getBody());
			for (Entry<String, String> entry : message.getAttributes().entrySet()) {
				System.out.println("  Attribute");
				System.out.println("    Name:  " + entry.getKey());
				System.out.println("    Value: " + entry.getValue());
			}
		}
	}
}

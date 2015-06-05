package com.livescribe.aws;

import java.util.TreeSet;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;

public class LSSQSQueueResource extends Resource {
	public static final String SQS_ARN_ATTR = "QueueArn";
	
	public LSSQSQueueResource(String queueUrl, String accessKey, String secretKey) {
		super(getSqsArn(queueUrl, accessKey, secretKey));
	}
    
	private static String getSqsArn(String queueUrl, String accessKey, String secretKey) {		
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AmazonSQSClient client = new AmazonSQSClient(credentials);
		
		TreeSet<String> attributeNames = new TreeSet<String>();
		attributeNames.add(SQS_ARN_ATTR);
		
		GetQueueAttributesRequest req = new GetQueueAttributesRequest();
		req.setQueueUrl(queueUrl);
		req.setAttributeNames(attributeNames);
		
		GetQueueAttributesResult result = client.getQueueAttributes(req);
		
		return result.getAttributes().get(SQS_ARN_ATTR);
	}
}

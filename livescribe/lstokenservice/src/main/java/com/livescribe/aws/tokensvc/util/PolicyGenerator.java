/**
 * 
 */
package com.livescribe.aws.tokensvc.util;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.auth.policy.Action;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.Statement.Effect;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.auth.policy.actions.SQSActions;
import com.amazonaws.auth.policy.resources.S3BucketResource;
import com.amazonaws.auth.policy.resources.S3ObjectResource;
import com.amazonaws.auth.policy.resources.SQSQueueResource;
import com.livescribe.aws.LSSQSQueueResource;
import com.livescribe.aws.tokensvc.config.AppConstants;
import com.livescribe.aws.tokensvc.config.AppProperties;

/**
 * <p>Generates AWS Policy documents for use in obtaining temporary credentials
 * from Amazon&apos;s Security Token Service (STS).</p>
 * 
 * Each method generates a policy for a specific need.
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PolicyGenerator implements AppConstants {

	@Autowired
	private AppProperties appProperties;
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public PolicyGenerator() {}

	/**
	 * <p><span style="color: red"><b>WARNING</b>:  Not completed!</span></p>
	 * 
	 * @return
	 */
	public String generateUserPolicy(String userId, String deviceId) {
		
		Policy policy = new Policy();

		ArrayList<Statement> statements = new ArrayList<Statement>();
		
		Statement sqsStmt = new Statement(Effect.Allow);

		//	Add SQS Actions.
		ArrayList<Action> sqsActionList = new ArrayList<Action>();
		sqsActionList.add(SQSActions.DeleteMessage);
		sqsActionList.add(SQSActions.GetQueueAttributes);
		sqsActionList.add(SQSActions.GetQueueUrl);
		sqsActionList.add(SQSActions.ReceiveMessage);
		sqsStmt.setActions(sqsActionList);
		
		//	Add pen's SQS Queue as Resource.
		String acctId = appProperties.getProperty(PROP_AWS_ACCOUNT_ID);
		SQSQueueResource sqsPenQueue = new SQSQueueResource(acctId, deviceId);
		ArrayList<Resource> sqsResourceList = new ArrayList<Resource> ();
		sqsResourceList.add(sqsPenQueue);
		sqsStmt.setActions(sqsActionList);
		
		Statement s3stmt = new Statement(Effect.Allow);
		
		//	Add S3 Actions to Statement.
		ArrayList<Action> s3ActionList = new ArrayList<Action>();
		s3ActionList.add(S3Actions.DeleteObject);
		s3ActionList.add(S3Actions.DeleteObjectVersion);
		s3ActionList.add(S3Actions.GetObject);
		s3ActionList.add(S3Actions.GetObjectAcl);
		s3ActionList.add(S3Actions.GetObjectVersion);
		s3ActionList.add(S3Actions.GetObjectVersionAcl);
		s3ActionList.add(S3Actions.ListObjects);
		s3ActionList.add(S3Actions.ListObjectVersions);
		s3ActionList.add(S3Actions.PutObject);
		s3ActionList.add(S3Actions.SetObjectAcl);
		s3ActionList.add(S3Actions.SetObjectVersionAcl);
		s3stmt.setActions(s3ActionList);
		
		//	Add S3 Resources to Statement.
		String userBucket = appProperties.getProperty(PROP_S3_BUCKET_USER_DATA);
		S3BucketResource userDataBucket = new S3BucketResource(userBucket);
		String uploadBucketName = appProperties.getProperty(PROP_S3_BUCKET_UPLOAD);
		S3BucketResource uploadBucket = new S3BucketResource(uploadBucketName);
		String sendUploadBucketName = appProperties.getProperty(PROP_S3_BUCKET_SEND_UPLOAD);
		S3BucketResource sendUploadBucket = new S3BucketResource(sendUploadBucketName);
		
		//	TODO:  Add user-specific area of S3 bucket.
		S3ObjectResource s3Object = new S3ObjectResource(userBucket, "*");
		
		ArrayList<Resource> s3ResourceList = new ArrayList<Resource>();
		s3ResourceList.add(userDataBucket);
		s3ResourceList.add(uploadBucket);
		s3ResourceList.add(sendUploadBucket);
		
		s3ResourceList.add(s3Object);
		s3stmt.setResources(s3ResourceList);
		
		//	Add all of the Statements to the Policy.
		statements.add(s3stmt);
		statements.add(sqsStmt);
		policy.setStatements(statements);
		
		return policy.toJson();
	}
	
	/**
	 * <p>Generates a JSON AWS Policy document allowing "write" access to the
	 * data metrics SQS queue.</p>
	 * 
	 * Gets information about the SQS queue (e.g.  name, account ID) from
	 * the <code>app.properties</code> file.
	 * 
	 * Allows the following SQS Actions:
	 * <ul>
	 * <li><code>GetQueueAttributes</code></li>
	 * <li><code>GetQueueUrl</code></li>
	 * <li><code>SendMessage</code></li>
	 * <li><code>SendMessageBatch</code></li>
	 * </ul>
	 * @return a JSON representation of an AWS Policy document.
	 */
	public String generateDataMetricsPolicy(String accessKey, String secretKey) {
		
		Policy policy = new Policy();
		
		ArrayList<Statement> statements = new ArrayList<Statement>();
		Statement stmt = new Statement(Effect.Allow);
		
		//	Add SQS Actions to policy
		ArrayList<Action> actionList = new ArrayList<Action>();
		actionList.add(SQSActions.GetQueueAttributes);
		actionList.add(SQSActions.GetQueueUrl);
		actionList.add(SQSActions.SendMessage);
		actionList.add(SQSActions.SendMessageBatch);
		stmt.setActions(actionList);
		
		//	Add SQS Queue resource to policy.
		String acctId = appProperties.getProperty(PROP_AWS_ACCOUNT_ID);
		String qName = appProperties.getProperty(PROP_QUEUE_DATAMETRICS_NAME);
		String queueUrl = PROP_SQS_BASE_URL + "/" + acctId + "/" + qName;
		
		ArrayList<Resource> resourceList = new ArrayList<Resource>();
		LSSQSQueueResource sqsQueue = new LSSQSQueueResource(queueUrl, accessKey, secretKey);
		resourceList.add(sqsQueue);
		stmt.setResources(resourceList);
		
		//	Add all of the Statements to the Policy.
		statements.add(stmt);
		policy.setStatements(statements);
		
		return policy.toJson();
	}
	
	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public String generatePortalCapturePolicy(String accessKey, String secretKey) {
		
		Policy policy = new Policy();

		ArrayList<Statement> statements = new ArrayList<Statement>();
		Statement s3stmt = new Statement(Effect.Allow);

		//	Add S3 Actions to Statement.
		ArrayList<Action> s3ActionList = new ArrayList<Action>();
		s3ActionList.add(S3Actions.DeleteObject);
		s3ActionList.add(S3Actions.DeleteObjectVersion);
		s3ActionList.add(S3Actions.GetObject);
		s3ActionList.add(S3Actions.GetObjectAcl);
		s3ActionList.add(S3Actions.GetObjectVersion);
		s3ActionList.add(S3Actions.GetObjectVersionAcl);
		s3ActionList.add(S3Actions.ListObjects);
		s3ActionList.add(S3Actions.ListObjectVersions);
		s3ActionList.add(S3Actions.PutObject);
		s3ActionList.add(S3Actions.SetObjectAcl);
		s3ActionList.add(S3Actions.SetObjectVersionAcl);
		s3stmt.setActions(s3ActionList);

		Statement sqsStmt = new Statement(Effect.Allow);
		
		//	Add SQS Actions to policy
		ArrayList<Action> actionList = new ArrayList<Action>();
		actionList.add(SQSActions.GetQueueAttributes);
		actionList.add(SQSActions.GetQueueUrl);
		actionList.add(SQSActions.SendMessage);
		actionList.add(SQSActions.SendMessageBatch);
		sqsStmt.setActions(actionList);
		
		//	Add SQS Queue resource to policy.
		String acctId = appProperties.getProperty(PROP_AWS_ACCOUNT_ID);
		ArrayList<Resource> resourceList = new ArrayList<Resource>();
		String qName = appProperties.getProperty(PROP_QUEUE_DATAMETRICS_NAME);
		String queueUrl = PROP_SQS_BASE_URL + "/" + acctId + "/" + qName;
		LSSQSQueueResource sqsQueue = new LSSQSQueueResource(queueUrl, accessKey, secretKey);
		resourceList.add(sqsQueue);
		sqsStmt.setResources(resourceList);
		
		//	Add S3 Resources to Statement.
		String bucketName = appProperties.getProperty(PROP_S3_BUCKET_PEN_CRASH_DUMP);
		S3BucketResource crashDumpBucket = new S3BucketResource(bucketName);
		
		//	This means all S3 objects in the S3 bucket.
		S3ObjectResource s3Object = new S3ObjectResource(bucketName, "*");

		ArrayList<Resource> s3ResourceList = new ArrayList<Resource>();
		s3ResourceList.add(crashDumpBucket);
		s3ResourceList.add(s3Object);
		
		s3stmt.setResources(s3ResourceList);

		//	Add all of the Statements to the Policy.
		statements.add(s3stmt);
		statements.add(sqsStmt);
		policy.setStatements(statements);
		
		return policy.toJson();
	}
	
	/**
	 * <p></p>
	 * 
	 * @param policyString
	 * 
	 * @return
	 */
	public String prettyPrintPolicyString(String policyString) {
		
		StringBuilder builder = new StringBuilder();
		
		int indent = 0;
		for (int i = 0; i < policyString.length(); i++) {
			char ch = policyString.charAt(i);
			if (ch == '{') {
				indent++;
				builder.append(ch).append("\n");
				for (int j = 0; j < indent; j++) {
					builder.append("\t");
				}
			}
			else if (ch == '}') {
				indent--;
				builder.append("\n");
				for (int j = 0; j < indent; j++) {
					builder.append("\t");
				}
				builder.append(ch);
				if ((i + 1 != policyString.length()) && (policyString.charAt(i + 1) != ']')) {
					builder.append("\n");
				}
			}
			else if (ch == '[') {
				indent++;
				builder.append(ch);
			}
			else if (ch == ']') {
				indent--;
				builder.append(ch);
				if (policyString.charAt(i + 1) != ',') {
					builder.append("\n");
				}
			}
			else if (ch == ',') {
				builder.append(ch).append("\n");
				for (int j = 0; j < indent; j++) {
					builder.append("\t");
				}
			}
			else {
				builder.append(ch);
			}
		}
		return builder.toString();
	}
}

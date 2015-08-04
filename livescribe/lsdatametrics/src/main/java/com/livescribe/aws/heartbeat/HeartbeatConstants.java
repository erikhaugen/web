/*
 * Created:  Jul 18, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.heartbeat;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface HeartbeatConstants {
	
	public static String PROP_QUEUE_URL					= "queue.url";
	public static String PROP_VISIBILITY_TIMEOUT		= "visibility.timeout";
	public static String PROP_POLLING_INTERVAL			= "polling.interval";
	public static String PROP_RETENTION_PERIOD			= "retention.period";
	public static String PROP_NUMBER_TO_FETCH			= "number.to.fetch";
	
	public static String PROP_AWS_ACCESS_KEY_ID			= "access.key.id";
	public static String PROP_AWS_SECRET_KEY			= "secret.key";
	
	public static String PROP_JDBC_DRIVER_CLASS_NAME	= "jdbc.driverClassName";
	public static String PROP_JDBC_URL					= "jdbc.url";
	public static String PROP_JDBC_USERNAME				= "jdbc.username";
	public static String PROP_JDBC_PASSWORD				= "jdbc.password";

	public static String SQS_ATTRIBUTE_VISIBILITY_TIMEOUT				= "VisibilityTimeout";
	public static String SQS_ATTRIBUTE_APPROX_NUM_OF_MSGS				= "ApproximateNumberOfMessages";
	public static String SQS_ATTRIBUTE_APPROX_NUM_OF_MSGS_NOT_VISIBLE	= "ApproximateNumberOfMessagesNotVisible";
	public static String SQS_ATTRIBUTE_CREATED_TIMESTAMP				= "CreatedTimestamp";
	public static String SQS_ATTRIBUTE_LAST_MODIFIED_TIMESTAMP			= "LastModifiedTimestamp";
	public static String SQS_ATTRIBUTE_QUEUE_ARN						= "QueueArn";
	public static String SQS_ATTRIBUTE_MAX_MESSAGE_SIZE					= "MaximumMessageSize";
	public static String SQS_ATTRIBUTE_MESSAGE_RETENTION_PERIOD			= "MessageRetentionPeriod";	
}

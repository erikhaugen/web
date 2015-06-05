/*
 * Created:  Sep 12, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.config;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface AppConstants {

	public static String PROP_AWS_ACCOUNT_ID					= "aws.account.id";
	public static String PROP_AWS_ACCESS_KEY_ID					= "access.key.id";
	public static String PROP_AWS_SECRET_KEY					= "secret.key";
	
	public static String PROP_PEN_SQS_BASE_USER_NAME			= "user.pen_sqs";
	public static String PROP_PEN_SQS_USER_COUNT				= "user.pen_sqs.count";
	
	public static String PROP_JDBC_DRIVER_CLASS_NAME			= "jdbc.driverClassName";
	public static String PROP_JDBC_URL							= "jdbc.url";
	public static String PROP_JDBC_USERNAME						= "jdbc.username";
	public static String PROP_JDBC_PASSWORD						= "jdbc.password";
	
	public static String PROP_MNFCTNG_JDBC_DRIVER_CLASS_NAME	= "manufacturing.jdbc.driverClassName";
	public static String PROP_MNFCTNG_JDBC_URL					= "manufacturing.jdbc.url";
	public static String PROP_MNFCTNG_JDBC_USERNAME				= "manufacturing.jdbc.username";
	public static String PROP_MNFCTGN_JDBC_PASSWORD				= "manufacturing.jdbc.password";
	
	public static String PROP_EVERNOTE_JDBC_DRIVER_CLASS_NAME	= "lsevernotedb.jdbc.driverClassName";
	public static String PROP_EVERNOTE_JDBC_URL					= "lsevernotedb.jdbc.url";
	public static String PROP_EVERNOTE_JDBC_USERNAME			= "lsevernotedb.jdbc.username";
	public static String PROP_EVERNOTE_JDBC_PASSWORD			= "lsevernotedb.jdbc.password";
	
	public static String PROP_CRYPTO_SALT						= "crypto.salt";
	public static String PROP_CRYPTO_PASSWORD					= "crypto.password";
	public static String PROP_CRYPTO_ITERATIONS					= "crypto.iterations";
	public static String PROP_CRYPTO_KEY_PATH					= "crypto.key.path";
	public static String PROP_CRYPTO_CERT_PATH					= "crypto.cert.path";
	public static String PROP_CRYPTO_REG_CODE_LENGTH			= "crypto.regcode.length";
	public static String PROP_CRYPTO_REG_CODE_EXPIRATION		= "crypto.regcode.expiration";

	public static String PROP_MAIL_ADDRESS_MAINTENANCE			= "mail.address.maintenance";
	
	public static String MIME_TYPE_TEXT							= "text/plain";
	public static String MIME_TYPE_HTML							= "text/html";
	
	public static String LOGIN_DOMAIN_WEB						= "WEB";
	public static String LOGIN_DOMAIN_DEVICE					= "DEVICE";
	
	public static String PROP_SQS_BASE_URL						= "https://queue.amazonaws.com";
	public static String PROP_SQS_MESSAGE_RETENTION_PERIOD		= "queue.pen.messageretentionperiod";
	public static String PROP_SQS_PEN_QUEUE_PREFIX				= "queue.pen.prefix";
	public static String PROP_QUEUE_DATAMETRICS_NAME			= "queue.datametrics.name";
	
	public static String PROP_S3_BUCKET_USER_DATA				= "s3.bucketname.userdata";
	public static String PROP_S3_BUCKET_UPLOAD					= "s3.bucketname.upload";
	public static String PROP_S3_BUCKET_SEND_UPLOAD				= "s3.bucketname.sendupload";
	public static String PROP_S3_BUCKET_PEN_CRASH_DUMP			= "s3.bucketname.pencrashdump";
}

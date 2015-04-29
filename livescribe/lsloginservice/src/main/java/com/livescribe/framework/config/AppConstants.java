/*
 * Created:  Sep 12, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.config;

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
	
	public static String PROP_CRYPTO_SALT						= "crypto.salt";
	public static String PROP_CRYPTO_PASSWORD					= "crypto.password";
	public static String PROP_CRYPTO_ITERATIONS					= "crypto.iterations";
	public static String PROP_CRYPTO_KEY_PATH					= "crypto.key.path";
	public static String PROP_CRYPTO_CERT_PATH					= "crypto.cert.path";

	public static String MIME_TYPE_TEXT							= "text/plain";
	public static String MIME_TYPE_HTML							= "text/html";
	
	public static String LOGIN_DOMAIN_WEB						= "WEB";
	public static String LOGIN_DOMAIN_DEVICE					= "DEVICE";
	
	public static String PROP_SQS_BASE_URL						= "https://queue.amazonaws.com";
	public static String PROP_SQS_MESSAGE_RETENTION_PERIOD		= "queue.pen.messageretentionperiod";
	public static String PROP_SQS_PEN_QUEUE_PREFIX				= "queue.pen.prefix";
	
	public static String PROP_MAIL_ADDRESS_MAINTENANCE			= "mail.address.maintenance";
	
	public static final String DESKTOP_VERSION_REGEX 			= "\\d+(\\.(\\d)+)*";
	
	public static final String PROP_LSDS_USER_SERVICE_URL		= "lsds.userservice.url";
	
	public static final String PROP_EVERNOTE_REQUEST_TOKEN_URL		= "evernote.url.request.token";
	public static final String PROP_EVERNOTE_ACCESS_TOKEN_URL		= "evernote.url.access.token";
	public static final String PROP_EVERNOTE_REGISTRATION_URL		= "evernote.url.registration";
	public static final String PROP_EVERNOTE_LOGIN_URL				= "evernote.url.login";
	public static final String PROP_EVERNOTE_AUTHORIZATION_URL		= "evernote.url.oauth.action";
	public static final String PROP_EVERNOTE_OAUTH_CONSUMER_KEY		= "evernote.oauth.consumer.key";
	public static final String PROP_EVERNOTE_OAUTH_CONSUMER_SECRET	= "evernote.oauth.consumer.secret";
	public static final String PROP_OAUTH_CALLBACK_URL				= "oauth.callback.url";
	
	public static final String PROP_OAUTH_EN_CONSUMER_KEY			= "oauth.evernote.consumer.key";
	public static final String PROP_OAUTH_EN_SECRET_KEY				= "oauth.evernote.secret.key";
	public static final String PROP_OAUTH_EN_REQUEST_TOKEN_URL		= "oauth.evernote.requesttoken.url";
	public static final String PROP_OAUTH_EN_AUTHORIZATION_URL		= "oauth.evernote.authorization.url";
	public static final String PROP_OAUTH_EN_REDIRECT_URL			= "oauth.evernote.redirect.url";
	public static final String PROP_OAUTH_EN_BASE_URL				= "oauth.evernote.base.url";
	public static final String PROP_OAUTH_EN_PREFER_REGISTRATION	= "oauth.evernote.preferregistration";
	
	public static final String PROP_HTTP_PORT						= "http.port";
}

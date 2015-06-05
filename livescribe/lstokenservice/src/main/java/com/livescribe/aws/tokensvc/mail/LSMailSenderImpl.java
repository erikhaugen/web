/*
 * Created:  Oct 3, 2011
 *      By:  kmurdoff
 */
package com.livescribe.aws.tokensvc.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.livescribe.aws.tokensvc.config.AppConstants;
import com.livescribe.aws.tokensvc.config.AppProperties;
import com.sun.mail.smtp.SMTPMessage;

/**
 * <p>Wrapper class for environment-specific configuration of mail sender.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class LSMailSenderImpl extends JavaMailSenderImpl {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static final String SMTP_HOST		= "mail.smtp.host";
	private static final String SMTP_USERNAME	= "mail.smtp.username";
	private static final String SMTP_PASSWORD	= "mail.smtp.password";
	private static final String SMTP_FROM		= "mail.smtp.from";
	private static final String SMTP_PORT		= "mail.smtp.port";
	private static String EMAIL_SUBJECT_ERROR	= "ERROR!  LSTokenService";
	
	private AppProperties appProperties;
	
	/**
	 * <p>Constructor that takes an <code>AppProperties</code> object as
	 * a parameter.</p>
	 * 
	 * Uses the <code>AppProperties</code> object to set environment-specific
	 * parameters on the wrapped <code>JavaMailSenderImpl</code> class.
	 * 
	 * @param appProperties The <code>AppProperties</code> object to use.
	 */
	public LSMailSenderImpl(AppProperties appProperties) {
		
		this.appProperties = appProperties;
		setHost(appProperties.getProperty(SMTP_HOST));
		String username = appProperties.getProperty(SMTP_USERNAME);
		String password = appProperties.getProperty(SMTP_PASSWORD);
		setUsername(username);
		setPassword(password);
		String portStr = appProperties.getProperty(SMTP_PORT);
		
		//	This section for Google SMTP server config.
		if (!"25".equals(portStr)) {
			int port = Integer.parseInt(portStr);
			setPort(port);
			setProtocol("smtps");
			Properties props = new Properties();
//			props.put("mail.smtp.starttls.enable", true);
			props.put("mail.smtp.socketFactory.port", port);
			props.put("mail.smtp.auth", true);
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//			props.put("mail.smtp.socketFactory.fallback", false);
			SmtpAuthenticator authenticator = new SmtpAuthenticator(username, password);
			Session session = Session.getDefaultInstance(props, authenticator);
			setSession(session);
//			setJavaMailProperties(props);
		}
		
		logger.debug("Initialized.");
	}
	
	/**
	 * <p></p>
	 * 
	 * @return
	 * 
	 * @throws MessagingException 
	 */
	public SMTPMessage createSMTPMessage() throws MessagingException {
		
		Session session = getSession();
		session.setDebug(true);
		SMTPMessage message = new SMTPMessage(session);
		message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS | SMTPMessage.NOTIFY_FAILURE | SMTPMessage.NOTIFY_DELAY);
		
		String fromString = this.appProperties.getProperty(SMTP_FROM);
		InternetAddress fromAddr = new InternetAddress(fromString);
		message.setFrom(fromAddr);
		
		logger.debug("createSMTPMessage():  Returning message.");
		
		return message;
	}

	/**
	 * <p>Sends notification of an error to &apos;maintenance&apos; email address
	 * configured in the <code>app.properties</code> file.</p>
	 * 
	 * @param content The content of the message to be sent.
	 */
	public void sendErrorEmail(String content) {
		
		SMTPMessage message = null;
		String email = appProperties.getProperty(AppConstants.PROP_MAIL_ADDRESS_MAINTENANCE);

		try {
			message = createSMTPMessage();
			InternetAddress toAddr = new InternetAddress(email);
			toAddr.setPersonal("Application Support");
			message.addRecipient(Message.RecipientType.TO, toAddr);
			message.setSubject(EMAIL_SUBJECT_ERROR + "");			
			message.setContent(content, AppConstants.MIME_TYPE_HTML);
			send(message);
		}
		catch (AddressException ae) {
			logger.error("AddressException thrown while attempting to create email address '" + email + "'.", ae);
		}
		catch (UnsupportedEncodingException uee) {
			logger.error("UnsupportedEncodingException thrown while attempting to send email to '" + email + "'.", uee);
		}
		catch (MessagingException msge) {
			logger.error("MessagingException thrown while attempting to send email to '" + email + "'.", msge);
		}
	}
	
}

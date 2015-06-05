/*
 * Created:  Oct 3, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.listener;

import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EmailDeliveryListener implements ApplicationContextAware,
		TransportListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private ApplicationContext applicationContext;
	
	/**
	 * <p></p>
	 * 
	 */
	public EmailDeliveryListener() {
	}

	/* (non-Javadoc)
	 * @see javax.mail.event.TransportListener#messageDelivered(javax.mail.event.TransportEvent)
	 */
	@Override
	public void messageDelivered(TransportEvent e) {
		
		logger.debug("messageDelivered():  ");
		//	TODO:  Send confirmation of email delivery to pen.
	}

	/* (non-Javadoc)
	 * @see javax.mail.event.TransportListener#messageNotDelivered(javax.mail.event.TransportEvent)
	 */
	@Override
	public void messageNotDelivered(TransportEvent e) {
		
		logger.debug("messageDelivered():  ");		
		//	TODO:  Retry delivery.
		
	}

	/* (non-Javadoc)
	 * @see javax.mail.event.TransportListener#messagePartiallyDelivered(javax.mail.event.TransportEvent)
	 */
	@Override
	public void messagePartiallyDelivered(TransportEvent e) {
		
		logger.debug("messageDelivered():  ");		
		//	TODO:  Handle this ... but how?
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		
		this.applicationContext = applicationContext;
		logger.debug("Application context set.");
	}
}

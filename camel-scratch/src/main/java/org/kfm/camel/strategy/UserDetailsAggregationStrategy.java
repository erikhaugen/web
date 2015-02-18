/**
 * Created:  Jul 19, 2013 5:26:00 PM
 */
package org.kfm.camel.strategy;

import org.apache.camel.BytesSource;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.CompletionAwareAggregationStrategy;
import org.apache.log4j.Logger;
import org.kfm.camel.converter.AfdConverter;

import com.livescribe.afp.Afd;

/**
 * <p>Aggregates the UID with the paper replay XML.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UserDetailsAggregationStrategy implements AggregationStrategy, CompletionAwareAggregationStrategy {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public UserDetailsAggregationStrategy() {
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.processor.aggregate.AggregationStrategy#aggregate(org.apache.camel.Exchange, org.apache.camel.Exchange)
	 */
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		String method = "aggregate()";

		if (oldExchange == null) {
			
			Object obj = newExchange.getIn().getBody();
			logger.debug(method + " - New Exchange body is of type: " + obj.getClass().getName());
			printExchange(newExchange);
			
			logger.debug(method + " - Returning new Exchange.");

			return newExchange;
		}
		
		Object oldBody = oldExchange.getIn().getBody();
		Object newBody = newExchange.getIn().getBody();
		if (oldBody != null) {
			logger.debug(method + " - Old Body is of type: " + oldBody.getClass().getCanonicalName());
			printExchange(oldExchange);
		} else {
			logger.warn(method + " - Old Body is 'null'.");
		}
		if (newBody != null) {
			logger.debug(method + " - New Body is of type: " + newBody.getClass().getCanonicalName());
			printExchange(newExchange);
			if (newBody instanceof Afd) {
				logger.debug(method + " - Setting body of oldExchange to Afd of newExchange.");
				oldExchange.getIn().setBody(newBody);
			}
//			oldExchange.getIn().setBody(newBody);
		} else {
			logger.debug(method + " - New Body is 'null'.");
		}
		
		//	Get the headers from the new Exchange.
		updateExchangeHeaders(oldExchange, newExchange);
		
		return oldExchange;
	}

	/**
	 * <p></p>
	 * 
	 * @param oldExchange
	 * @param newExchange
	 */
	private void updateExchangeHeaders(Exchange oldExchange,
			Exchange newExchange) {
		String displayId = newExchange.getIn().getHeader("displayId", String.class);
		String uid = newExchange.getIn().getHeader("uid", String.class);
		String accessToken = newExchange.getIn().getHeader("accessToken", String.class);
		String enUserId = newExchange.getIn().getHeader("enUserId", String.class);
		Long penSerial = newExchange.getIn().getHeader("penSerial", Long.class);
		
		//	Update the header value on the *old* Exchange only if there is a non-empty value.
		if ((displayId != null) && (!displayId.isEmpty())) {
			oldExchange.getIn().setHeader("displayId", displayId);
		}
		if ((uid != null) && (!uid.isEmpty())) {
			oldExchange.getIn().setHeader("uid", uid);
		}
		if ((accessToken != null) && (!accessToken.isEmpty())) {
			oldExchange.getIn().setHeader("accessToken", accessToken);
		}
		if ((enUserId != null) && (!enUserId.isEmpty())) {
			oldExchange.getIn().setHeader("enUserId", enUserId);
		}
		if (penSerial != null) {
			oldExchange.getIn().setHeader("penSerial", penSerial);
		}
	}

	/**
	 * <p></p>
	 * 
	 * @param exchng
	 * @param method
	 */
	private void printExchange(Exchange exchng) {
		
		String method = "printExchange()";
		
		String newHeaderDisplayId = exchng.getIn().getHeader("displayId", String.class);
		logger.debug(method + " - header [displayId]:  " + newHeaderDisplayId);

		String newHeaderUid = exchng.getIn().getHeader("uid", String.class);
		logger.debug(method + " - header [uid]:  " + newHeaderUid);

		String newHeaderAccessToken = exchng.getIn().getHeader("accessToken", String.class);
		logger.debug(method + " - header [accessToken]:  " + newHeaderAccessToken);

		String newHeaderEnUserId = exchng.getIn().getHeader("enUserId", String.class);
		logger.debug(method + " - header [enUserId]:  " + newHeaderEnUserId);
	}

	@Override
	public void onCompletion(Exchange exchange) {
		
		String method = "onCompletion()";
		
		logger.debug(method + " - Called.");
		
		printExchange(exchange);
		
		Object exchObj = exchange.getIn().getBody();
		logger.debug(method + " - Body is of type: " + exchObj.getClass().getCanonicalName());
		
		logger.debug(method + " - Aggregation complete.");
	}
}

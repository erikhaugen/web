/*
 * Created:  Jul 20, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.heartbeat.msg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.amazonaws.services.sqs.model.Message;
import com.livescribe.aws.heartbeat.DataMetricFactory;
import com.livescribe.aws.heartbeat.orm.DataMetric;

/**
 * <p>Represents an AWS SQS {@link com.amazonaws.services.sqs.model.Message} 
 * with a <u>parsed</u> message body.</p>
 * 
 * Uses {@link com.livescribe.aws.heartbeat.DataMetricFactory} to parse the
 * XML body of the <code>Message</code>.&nbsp;&nbsp;If the XML cannot be 
 * parsed, or is not present, an empty <code>List</code> of metrics is created.
 * 
 * Dates are parsed using the one of the following formats (in order):
 * <ol>
 * <li>yyyy-MM-dd'T'HH:mm:ss.SSSZ</li>
 * <li>yyyy-MM-dd'T'HH:mm:ssZ</li>
 * </ol>
 * If the data string could not be parsed, the current date is used.
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class SQSMessage extends Message {

	private static Logger logger = Logger.getLogger(SQSMessage.class.getName());
	
	private String body = "";
	private Date firstAttemptSend;
	private List<DataMetric> metricList = new ArrayList<DataMetric>();
	
	/**
	 * <p>Default class constructor.</p>
	 * 
	 */
	public SQSMessage() {
		
		super();
	}

	/**
	 * <p>Wraps an AWS SQS Message and parses the XML in the message body into
	 * a <code>List</code> of {@link com.livescribe.aws.heartbeat.orm.DataMetric} objects.</p>
	 * 
	 * @param message the SQS Message to wrap.
	 */
	public SQSMessage(Message message) {
		
		super();
		
		this.setAttributes(message.getAttributes());
		this.setBody(message.getBody());
		this.setMD5OfBody(message.getMD5OfBody());
		this.setMessageId(message.getMessageId());
		this.setReceiptHandle(message.getReceiptHandle());
		
		this.body = message.getBody();
		
		// parse metric list
		List<DataMetric> metrics = DataMetricFactory.createDataMetricList(message);
		this.metricList = metrics;
		
		// get firstAttemptSend
		Document document = null;
		try {
			document = DocumentHelper.parseText(this.body);
			String firstAttemptSend = document.valueOf("/metrics/@firstAttemptSend");
			this.firstAttemptSend = DataMetricFactory.parseDate(firstAttemptSend);
		} catch (DocumentException de) {
			logger.error("DocumentException thrown when attempting to parse body of given SQS Message object.  Returning empty List.", de);
			this.firstAttemptSend = new Date();
		}
	}
	
	
	/**
	 * <p>Adds the given metric to the list of data metrics represented by this
	 * message.</p>
	 * 
	 * @param metric The <code>DataMetric</code> to add.
	 */
	public void addMetric(DataMetric metric) {
		this.metricList.add(metric);
	}
	
	/* (non-Javadoc)
	 * @see com.amazonaws.services.sqs.model.Message#toString()
	 */
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("\n--------------------------------------------------\n");
		builder.append("      Message ID:  " + this.getMessageId() + "\n");
		builder.append("     MD5 of Body:  " + this.getMD5OfBody() + "\n");
		Map<String, String> attMap = this.getAttributes();
		for (Entry<String, String> entry : attMap.entrySet()) {
			builder.append("        Attribute\n");
			builder.append("          Name:  " + entry.getKey() + "\n");
			builder.append("         Value:  " + entry.getValue() + "\n");
		}
		builder.append(this.body);
		builder.append("  Receipt Handle:  " + this.getReceiptHandle() + "\n");
		builder.append("--------------------------------------------------\n");
		
		return builder.toString();
	}

	/**
	 * <p></p>
	 * 
	 * @return the metricList
	 */
	public List<DataMetric> getMetricList() {
		return metricList;
	}

	/**
	 * <p></p>
	 * 
	 * @param metricList the metricList to set
	 */
	public void setMetricList(List<DataMetric> metricList) {
		this.metricList = metricList;
	}

	public Date getFirstAttemptSend() {
		return firstAttemptSend;
	}
	public void setFirstAttemptSend(Date firstAttemptSend) {
		this.firstAttemptSend = firstAttemptSend;
	}
}

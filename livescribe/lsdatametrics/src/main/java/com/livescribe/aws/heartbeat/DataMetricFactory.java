/*
 * Created:  Jul 20, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.heartbeat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import com.amazonaws.services.sqs.model.Message;
import com.livescribe.aws.heartbeat.annotation.MetricNodeAttribute;
import com.livescribe.aws.heartbeat.orm.DataMetric;

/**
 * <p>Creates <code>DataMetric</code> objects for use in saving to a database.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class DataMetricFactory {

	private static Logger logger = Logger.getLogger(DataMetricFactory.class);
	private static Map metricTypeMap;


	/**
	 * <p>Creates a <code>List</code> of <code>DataMetric</code> objects
	 * from the given SQS <code>Message</code>.</p>
	 * 
	 * @param message The SQS <code>Message</code> to use.
	 * 
	 * @return a <code>List</code> of <code>DataMetric</code> objects.
	 */
	public static List<DataMetric> createDataMetricList(Message message) {
		
		String method = "createDataMetricList():  ";
		
		ArrayList<DataMetric> metrics = new ArrayList<DataMetric>();
		
		String messageId = message.getMessageId();
		String messageBody = message.getBody();
		
		Document document = null;
		try {
			document = DocumentHelper.parseText(messageBody);
		}
		catch (DocumentException de) {
			logger.error(method + "DocumentException thrown when attempting to parse body of given SQS Message object.  Returning empty List.", de);
			return metrics;
		}
		
		//	Parse the parent <metrics> element attributes.
		String metricVersion = document.valueOf("/metrics/@version");
		String deviceId = document.valueOf("/metrics/@unique_id");
		String deviceType = document.valueOf("/metrics/@device");
		String fwVersion = document.valueOf("/metrics/@fw_version");
		String firstAttemptSend = document.valueOf("/metrics/@firstAttemptSend");
		
		List<Node> metricList = document.selectNodes("/metrics/metric");
		
		logger.debug(method + "Found " + metricList.size() + " metrics in SQS message.");

		for (Node node : metricList) {
			
			//	Parse the individual <metric> element.
			DataMetric metric = parseMetric(node);
			// Skip unsupported metric type
			if (metric == null) {
				continue;
			}
			
			metric.setMessageId(messageId);
			
			//	Add the attributes of the parent <metrics> element.
			metric.setMetricVersion(metricVersion);
			metric.setDeviceId(deviceId);
			metric.setDeviceType(deviceType);
			metric.setFwVersion(fwVersion);
			metric.setMetricSent(parseDate(firstAttemptSend));
			
			metrics.add(metric);
		}

		return metrics;
	}
	
	/**
	 * <p>Parses an individual <code><metric></code> XML node and returns
	 * a <code>DataMetric</code> object.</p>
	 * 
	 * @param node The <code><metric></code> node to parse.
	 * 
	 * @return a <code>DataMetric</code> object.
	 */
	private static DataMetric parseMetric(Node node) {
		
		logger.debug("parseMetric():  " + node.asXML());
		
		String metricType = node.valueOf("@type");
		String testStr = node.valueOf("@test");
		
		boolean isTest = parseBooleanValue(testStr);
		
		DataMetric metric = buildDataMetricInstance(metricType, node);
		
		// Override parsed value with the processed value
		if (metric != null) {
			metric.setIsTestData(isTest);
		}
		
		return metric;
	}
	
	/**
	 * Instantiating DataMetric object and populating fields with values from Node
	 * 
	 * @param attributeMap
	 * @param node
	 * @return
	 */
	private static DataMetric buildDataMetricInstance(String metricType, Node node) {
		DataMetric metric = null;
		
		try {
			Class aClass = Class.forName(String.valueOf(metricTypeMap.get(metricType)));
			metric = (DataMetric) aClass.newInstance();
			
			// Get field list
			Field fields[] = aClass.getDeclaredFields();
			for (Field f : fields) {
				MetricNodeAttribute metricNodeAttr = f.getAnnotation(MetricNodeAttribute.class);
				if (metricNodeAttr == null) {
					continue;
				}
				
				// Get setter method for the field
				Method setterMethod = getSetterMethod(aClass, f.getName(), metricNodeAttr.dataType());
				if (setterMethod == null) {
					logger.warn("Field " + f.getName() + " in class " + aClass.getName() + " does not have setter method.");
					continue;
				}
				
				// Set value using setter method
				Object dataValue = buildDataTypeInstance(Class.forName(metricNodeAttr.dataType()), node.valueOf("@" + metricNodeAttr.name()));
				setterMethod.invoke(metric, dataValue);
			}
			
		} catch (ClassNotFoundException cnfe) {
			logger.error(cnfe.getMessage());
		} catch (IllegalAccessException iae) {
			logger.error(iae.getMessage());
		} catch (InstantiationException ie) {
			logger.error(ie.getMessage());
		} catch (InvocationTargetException ite) {
			logger.error(ite.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}

		return metric;
	}
	
	/**
	 * build data type wrapper instance i.e Integer, Long, Boolean, Date...
	 * 
	 * @param dataTypeClass
	 * @param value
	 * @return
	 */
	private static Object buildDataTypeInstance(Class dataTypeClass, String value) {
		
		// handling Date data type
		if (dataTypeClass.equals(java.util.Date.class)) {
			return parseDate(value);
		} 
		
		// handling Boolean data type
		if (dataTypeClass.equals(java.lang.Boolean.class)) {
			return parseBooleanValue(value);
		}
		
		// TODO: Data type other than primitive wrappers may define here
		
		// handling primitive wrapper data type
		try {
			Constructor constructor = dataTypeClass.getConstructor(String.class);
			return constructor.newInstance(value);
			
		} catch (NoSuchMethodException nsme) {
			logger.error(nsme.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Get setter method from a Class using reflection
	 * 
	 * @param aClass Class to which getting the setter method
	 * @param fieldName fieldName to which getting setter method
	 * @return
	 */
	private static Method getSetterMethod(Class aClass, String fieldName, String dataType) {
		Method method = null;
		String methodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		
		try {
			method = aClass.getMethod(methodName, Class.forName(dataType));
			
		} catch (NoSuchMethodException nsme) {
			logger.error(aClass.toString() + " does not have method " + methodName + "(" + dataType + ")");
			return null;
		} catch (ClassNotFoundException cnfe) {
			logger.error(dataType + " not found in method " + methodName);
		}
		
		return method;
	}
	
	/**
	 * <p>Parses the given date/time string.</p>
	 * 
	 * @param sendTime The date/time string to parse.
	 * 
	 * @return a <code>Date</code> representing the given date/time string.
	 */
	public static Date parseDate(String sendTime) {
		Date date = null;
		try {
			date = parseISO8601Date(sendTime);
		} catch (Exception e) {
			// no action, try another simple date time format which is being used
			//logger.error("*** 1 Date parsing failed to parse '" + sendTime + "'. Err=" + e.getMessage());
			try {
				date = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).parse(sendTime);
			} catch (Exception e1) {
				// no action, try another simple date time format which is being used
				//logger.error("*** 2 Date parsing failed to parse '" + sendTime + "'. Err=" + e1.getMessage());
				try {
					date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(sendTime);
				} catch (Exception e2) {
					logger.error("*** Date parsing failed to parse '" + sendTime + "'. This date was not parsed nor saved. Date not set." + " Err=" + e2.getMessage());
					date = null;
				}
			}
		}
		
		return date;
	}

	/**
	 * Parses an ISO&nbsp;8601 date.
	 * 
	 * @param s parse this string
	 * @return a new {@link Date} object.
	 * @throws IllegalArgumentException if the date is malformed.
	 * @see <a href="http://tools.ietf.org/html/rfc3339">RFC3339, Date and Time
	 *      on the Internet: Timestamps</a>
	 * @author Shawn Silverman
	 */
	private static Date parseISO8601Date(String s) {
		// 1 2
		// 012345678901234567890
		// yyyy-MM-ddTHH:mm:ss(.(S)+)?Z

		try {
			if (s.charAt(4) != '-' || s.charAt(7) != '-' || s.charAt(13) != ':'
					|| s.charAt(16) != ':'
					|| Character.toUpperCase(s.charAt(10)) != 'T') {
				throw new IllegalArgumentException();
			}

			int year = Integer.parseInt(s.substring(0, 4));
			int month = Integer.parseInt(s.substring(5, 7));
			int date = Integer.parseInt(s.substring(8, 10));
			int hour = Integer.parseInt(s.substring(11, 13));
			int min = Integer.parseInt(s.substring(14, 16));
			int sec = Integer.parseInt(s.substring(17, 19));

			if (year < 0 || month < 0 || date < 0 || hour < 0 || min < 0
					|| sec < 0) {
				throw new IllegalArgumentException("Bad numbers: " + s);
			}

			if ((month < 1 || 12 < month) || (23 < hour) || (59 < min)
					|| (60 < sec)) {
				throw new IllegalArgumentException("Bad values: " + s);
			}

			int index = 19;
			int millis = 0;

			if (s.charAt(19) == '.') {
				// Parse milliseconds

				while (true) {
					char c = s.charAt(++index);
					if (c < '0' || '9' < c) {
						if (index <= 20) { // Zero-length milliseconds
							throw new IllegalArgumentException(
									"Missing milliseconds: " + s);
						}
						break;
					}
				}

				millis = Integer.parseInt(s.substring(20, index));
				// NOTE No need to check for negative because the above code
				// only allows digits
			}

			// Parse the timezone

			char c = s.charAt(index);
			int tzOffset = 0;
			if (Character.toUpperCase(c) == 'Z') {
				index++;
			} else {
				if (c != '+' && c != '-')
					throw new IllegalArgumentException("Bad time zone1: " + s);
				index++;

				boolean neg = (c == '-');
				int tzHour = Integer.parseInt(s.substring(index, index += 2));
				if (s.charAt(index++) != ':')
					throw new IllegalArgumentException("Bad time zone2: " + s);
				int tzMin = Integer.parseInt(s.substring(index, index += 2));
				if (tzHour < 0 || tzMin < 0)
					throw new IllegalArgumentException("Bad time zone3: " + s);

				if (23 < tzHour || 59 < tzMin)
					throw new IllegalArgumentException("Bad time zone4: " + s);

				tzOffset = (tzHour * 60 + tzMin) * 60 * 1000;
				if (neg)
					tzOffset = -tzOffset;
			}

			// Last, check the length

			if (s.length() > index)
				throw new IllegalArgumentException("String too long: " + s + " index=" + index);

			// Set all the fields

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);
			cal.set(Calendar.DAY_OF_MONTH, date);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, min);
			cal.set(Calendar.SECOND, sec);
			cal.set(Calendar.MILLISECOND, millis);

			// There's no easy way to do TZ processing here, so offset manually

			return new Date(cal.getTime().getTime() - tzOffset);
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalArgumentException("Bad date: " + s);
		}
	}
	
	/**
	 * Parse boolean value from a String
	 * @param value
	 * @return
	 */
	private static Boolean parseBooleanValue(String value) {
		int intValue = -1;
		boolean boolValue = false;
		
		if (!"".equals(value)) {
			try {
				intValue = Integer.parseInt(value);
			}
			catch (NumberFormatException nfe) {
				logger.error("");
			}
		}

		if (intValue > -1) {
			switch (intValue) {
			case 0:
				boolValue = false;
				break;
			case 1:
				boolValue = true;
				break;
			default:
				boolValue = false;
				break;
			}
		} else {
			if ("true".equals(value)) {
				boolValue = true;
			}
			else if ("false".equals(value)) {
				boolValue = false;
			}
		}
		
		return new Boolean(boolValue);
	}
	
	public void setMetricTypeMap(Map metricTypeMap) {
		this.metricTypeMap = metricTypeMap;
	}
	
}

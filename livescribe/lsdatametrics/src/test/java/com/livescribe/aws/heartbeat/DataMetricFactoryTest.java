/*
 * Created:  Sep 6, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.heartbeat;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.amazonaws.services.sqs.model.Message;
import com.livescribe.aws.heartbeat.msg.SQSMessage;
import com.livescribe.aws.heartbeat.orm.DataMetric;
import com.livescribe.aws.heartbeat.orm.MetricGroup;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @author <a href="mailto:mnaqvi@livescribe.com">Mohammad M. Naqvi</a>
 * @version 1.0
 */
public class DataMetricFactoryTest extends BaseTest {
	private Message msg;
	private List<DataMetric> metrics;
	private static final String MESSAGE_ID = "MSG-ID-1";
	
	private static final ApplicationContext ac = new ClassPathXmlApplicationContext("test-context.xml");
	private static DataMetricsTransferServiceImpl txfr = (DataMetricsTransferServiceImpl)ac.getBean("dataMetricsTxfr");
	private static Map<String, GenericDao> daoMap = txfr.getDaoMap();
	
	/**
	 * <p></p>
	 * 
	 */
	public DataMetricFactoryTest() {
		super();
	}

	@Before
	public void setUp() {	
		URL fileUrl = this.getClass().getClassLoader().getResource("metric-example.xml");
		String filename = fileUrl.getFile();
		File file = new File(filename);
		BufferedReader br = null;
		
		StringBuilder xml = new StringBuilder();
		if (file.exists()) {
			try {
				br = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = br.readLine()) != null) {
					xml.append(line);
				}
			}
			catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		msg = new Message();
		msg.setBody(xml.toString());
		msg.setMessageId(MESSAGE_ID);
		msg.setReceiptHandle("MSG-RCPT-HANDLE-1");
		
		// create metric list
		metrics = DataMetricFactory.createDataMetricList(msg);
	}
	
	@Test
	public void testCreateDataMetricList() {
		assertNotNull("The returned List was 'null'.", metrics);
		assertEquals("The returned List contained the wrong number of metrics.", 17, metrics.size());

		for (DataMetric metric : metrics) {
			System.out.print("Asserting " + metric.getClass().getName() + "...");
			assertEquals("metricId was not as expected", MESSAGE_ID, metric.getMessageId());
			assertNotNull("metricVersion was 'null'", metric.getMetricVersion());
			assertNotNull("deviceId was 'null'", metric.getDeviceId());
			assertNotNull("deviceType was 'null'", metric.getDeviceType());
			assertNotNull("fwVersion was 'null'", metric.getFwVersion());
			assertNotNull("eventTime was 'null'", metric.getEventTime());
			assertNotNull("metricSent was 'null'", metric.getMetricSent());
			assertEquals("isTestData was not as expected", Boolean.TRUE, metric.getIsTestData());
			System.out.println("done");
			System.out.println(metric.toString() + "\n");
		}
	}
	
	@Test
	public void testSaveMetricsToDB() {
		assertNotNull("The returned List was 'null'.", metrics);
		assertEquals("The returned List contained the wrong number of metrics.", 17, metrics.size());
		
		// save MetricGroup
		long startTime = System.currentTimeMillis();
		MetricGroup metricGroup = new MetricGroup(new SQSMessage(msg));
		GenericDao metricGroupDao = daoMap.get(MetricGroup.class.getName());
		metricGroupDao.persist(metricGroup);
		
		// save Metrics
		for (DataMetric metric : metrics) {
			try {
				metric.setMetricGroup(metricGroup);
				metric.setCreated(new Date());
				saveMetric(metric);
			}
			catch (HibernateException he) {
				he.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Saved " + metrics.size() + " metrics into db took: " + (endTime - startTime) + "ms");

	}
	
	public void saveMetric(DataMetric metric) throws HibernateException {		
		String className = metric.getClass().getName();
		
		GenericDao dao = daoMap.get(className);
		if (dao == null) {
			throw new HibernateException("Could not find DAO class for " + className);
		}
		dao.persist(metric);
	}
}

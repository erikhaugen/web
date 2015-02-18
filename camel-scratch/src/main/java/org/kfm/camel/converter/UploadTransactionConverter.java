/**
 * Created:  Dec 16, 2014 4:32:07 PM
 */
package org.kfm.camel.converter;

import java.io.InputStream;

import org.apache.camel.Converter;
import org.apache.log4j.Logger;
import org.kfm.camel.entity.UploadTransaction;

import com.thoughtworks.xstream.XStream;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Converter
public class UploadTransactionConverter {

	private static Logger logger = Logger.getLogger(UploadTransactionConverter.class.getName());

	/**
	 * <p></p>
	 * 
	 */
	public UploadTransactionConverter() {
	}

	@Converter
	public static UploadTransaction fromInputStream(InputStream in) {
		
		logger.debug("fromInputStream() - Converting InputStream to UploadTransaction ...");
		XStream xstream = new XStream();
		xstream.processAnnotations(UploadTransaction.class);
		UploadTransaction upTx = null;
		Object obj = xstream.fromXML(in);
		if (obj instanceof InputStream) {
			logger.debug("fromInputStream() - Parsed InputStream into an InputStream (???) ...");
		} else if (obj instanceof UploadTransaction) {
			logger.debug("fromInputStream() - Parsed InputStream into an UploadTransaction.");
			upTx = (UploadTransaction)obj;
		}
		
		return upTx;
	}

	@Converter
	public static UploadTransaction fromXml(String xml) {
		logger.debug("fromXml() - Converting XML to UploadTransaction ...");
		XStream xstream = new XStream();
		xstream.processAnnotations(UploadTransaction.class);
		UploadTransaction upTx = (UploadTransaction)xstream.fromXML(xml);
		return upTx;
	}
	
	@Converter
	public static String toXml(UploadTransaction upTx) {
		logger.debug("toXml() - Converting UploadTransaction to XML ...");
		XStream xstream = new XStream();
		xstream.processAnnotations(UploadTransaction.class);
		String xml = xstream.toXML(upTx);
		return xml;
	}
}

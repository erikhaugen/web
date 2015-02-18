/**
 * Created:  Dec 4, 2014 10:38:21 AM
 */
package org.kfm.camel.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.camel.BytesSource;
import org.apache.camel.Converter;
import org.apache.log4j.Logger;

import com.livescribe.afp.Afd;
import com.thoughtworks.xstream.XStream;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Converter
public class AfdConverter {

	private static Logger logger = Logger.getLogger(AfdConverter.class.getName());

	/**
	 * <p></p>
	 * 
	 */
	public AfdConverter() {
	}

	@Converter(allowNull = true)
	public static String toString(Afd afd) {
		logger.debug("toString() - Converting AFD to XML string ...");
		return afd.toString();
	}
	
	@Converter(allowNull = true)
	public static Afd fromByteSource(BytesSource bytes) {
		logger.debug("fromByteSource() - Converting BytesSource to AFD ...");
		
		byte[] responseBytes = "<response>".getBytes();
		byte[] data = bytes.getData();
		
		byte[] front = Arrays.copyOf(data, 10);
		if (Arrays.equals(responseBytes, front)) {
			logger.debug("fromByteSource() - Non-AFD BytesSource found.  Returning 'null'.");
			return null;
		}
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
//		xstream.processAnnotations(Afd.class);
		InputStream is = bytes.getInputStream();
		Afd afd = (Afd)xstream.fromXML(is);
		
		return afd;
	}
	
	@Converter(allowNull = true)
	public static Afd fromString(String xml) {
		logger.debug("fromString() - Converting XML string to AFD ...");
		
		if (xml.startsWith("<response>")) {
			logger.debug("fromString() - Non-AFD XML string found.  Returning 'null'.");
			return null;
		}
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
//		xstream.processAnnotations(Afd.class);
		Afd afd = (Afd)xstream.fromXML(xml);
		
		return afd;
	}
	
	@Converter(allowNull = true)
	public static Afd fromInputStream(InputStream stream) {
		
		//	TODO:  May need to read stream in first, determine if correct content, and proceed after that.
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
//		xstream.processAnnotations(Afd.class);
		Afd afd = (Afd)xstream.fromXML(stream);
		
		return afd;
	}
	
	@Converter(allowNull = true)
	public static InputStream toInputStream(Afd afd) {
		
		String xml = afd.toString();
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
		InputStream stream = null;
		try {
			stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		}
		return stream;
	}
}

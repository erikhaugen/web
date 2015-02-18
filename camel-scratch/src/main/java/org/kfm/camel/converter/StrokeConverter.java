/**
 * Created:  Dec 18, 2014 3:37:25 PM
 */
package org.kfm.camel.converter;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.camel.Converter;
import org.apache.log4j.Logger;
import org.kfm.camel.entity.evernote.XMLCoordinate;
import org.kfm.camel.entity.evernote.XMLStroke;
import org.kfm.camel.entity.evernote.XMLStrokes;

import com.evernote.edam.type.Resource;
import com.livescribe.afp.stf.STFSample;
import com.livescribe.afp.stf.STFStroke;
import com.thoughtworks.xstream.XStream;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Converter
public class StrokeConverter {

	private static Logger logger = Logger.getLogger(StrokeConverter.class.getName());

	/**
	 * <p></p>
	 * 
	 */
	public StrokeConverter() {
	}

	@Converter
	public static String fromByteArray(byte[] bytes) {
		String xmlBytes = new String(bytes);
		return xmlBytes;
	}
	
	@Converter
	public static XMLStrokes fromBytesToXMLStrokes(byte[] bytes) {
		XStream xstream = new XStream();
		xstream.processAnnotations(XMLStrokes.class);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		XMLStrokes xmlStrokes = (XMLStrokes)xstream.fromXML(bais);
		return xmlStrokes;
	}
	
	@Converter
	public static XMLStrokes fromResource(Resource resource) {
		
		if (resource == null) {
			return null;
		}
		
		byte[] strokeBytes = resource.getData().getBody();
		XMLStrokes xmlStrokes = fromBytesToXMLStrokes(strokeBytes);
		return xmlStrokes;
	}
	
	@Converter
	public static XMLStrokes fromSTFStrokes(Set<STFStroke> stfStrokes) {
		
		XMLStrokes xmlStrokes = new XMLStrokes();
		List<XMLStroke> xmlStrokeList = new ArrayList<XMLStroke>();
		TreeSet<STFStroke> sortedStfStrokes = (TreeSet<STFStroke>)stfStrokes;
		Iterator<STFStroke> stfStrokeIter = sortedStfStrokes.iterator();
		while (stfStrokeIter.hasNext()) {
			STFStroke stfStroke = stfStrokeIter.next();
			long time = stfStroke.getTime();
			XMLStroke xmlStroke = new XMLStroke();
			xmlStroke.setTimestamp(BigInteger.valueOf(time));
			List<XMLCoordinate> xmlCoords = new ArrayList<XMLCoordinate>();
			Vector<STFSample> samples = stfStroke.getSamples();
			for (STFSample sample : samples) {
				XMLCoordinate xmlCoord = new XMLCoordinate();
				xmlCoord.setX((float)sample.x);
				xmlCoord.setY((float)sample.y);
				xmlCoord.setD((float)sample.d);
				xmlCoords.add(xmlCoord);
			}
			xmlStroke.setCoords(xmlCoords);
			xmlStrokeList.add(xmlStroke);
		}
		logger.debug("XMLStroke count: " + xmlStrokeList.size());
		xmlStrokes.setList(xmlStrokeList);
		return xmlStrokes;
	}
	
	@Converter
	public static byte[] toByteArray(String xml) {
		return xml.getBytes();
	}
	
	@Converter
	public static String toXmlString(XMLStrokes xmlStrokes) {
		
		XStream xstream = new XStream();
		xstream.processAnnotations(XMLStrokes.class);
		String xml = xstream.toXML(xmlStrokes);
		return xml;
	}
}

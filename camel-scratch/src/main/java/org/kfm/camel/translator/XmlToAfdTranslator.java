/**
 * Created:  Dec 3, 2014 3:32:17 PM
 */
package org.kfm.camel.translator;

import org.apache.log4j.Logger;

import com.livescribe.afp.Afd;
import com.livescribe.afp.DocumentInfo;
import com.livescribe.afp.PageTemplate;
import com.livescribe.afp.SyncTimesInfo;
import com.thoughtworks.xstream.XStream;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class XmlToAfdTranslator {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public XmlToAfdTranslator() {
	}

	public Afd translate(String xml) {
		
		logger.debug("translate() - xml = " + xml);
		
		XStream xstream = new XStream();
//		xstream.autodetectAnnotations(true);
		xstream.processAnnotations(Afd.class);
		xstream.processAnnotations(DocumentInfo.class);
		xstream.processAnnotations(SyncTimesInfo.class);
		xstream.processAnnotations(PageTemplate.class);
		Afd response = (Afd)xstream.fromXML(xml);
		if (response != null) {
			logger.debug("translate() - response = " + response.toString());
		}
		return response;
	}
}

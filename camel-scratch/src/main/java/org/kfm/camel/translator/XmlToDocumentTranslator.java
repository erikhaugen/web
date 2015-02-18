/**
 * Created:  Mar 12, 2014 4:07:47 PM
 */
package org.kfm.camel.translator;

import org.apache.log4j.Logger;
import org.kfm.camel.entity.DocumentDTO;
import org.kfm.camel.entity.TemplateDTO;
import org.kfm.camel.response.DocumentListResponse;

import com.thoughtworks.xstream.XStream;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class XmlToDocumentTranslator {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public XmlToDocumentTranslator() {
		
	}

	/**
	 * <p></p>
	 * 
	 * @param afdFile
	 * @return
	 */
	public DocumentListResponse translate(String docXml) {
		
		String method = "translate()";
		
		logger.info("BEFORE - " + method);
		
		XStream xStream = new XStream();
//		xStream.autodetectAnnotations(true);
		xStream.processAnnotations(DocumentListResponse.class);
		xStream.processAnnotations(DocumentDTO.class);
		xStream.processAnnotations(TemplateDTO.class);
		
		xStream.alias("response", DocumentListResponse.class);
		xStream.alias("json-document", DocumentDTO.class);
		xStream.alias("json-template", TemplateDTO.class);
		
		DocumentListResponse response = (DocumentListResponse)xStream.fromXML(docXml);
		
		logger.info("AFTER - " + method);
		
		return response;
	}
}

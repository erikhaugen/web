/**
 * Created:  Nov 18, 2014 6:49:02 PM
 */
package org.kfm.camel.translator;

import org.apache.log4j.Logger;
import org.kfm.camel.response.DocumentListResponse;
import org.kfm.jpa.Document;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class DocumentListResponseToDocumentTranslator {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public DocumentListResponseToDocumentTranslator() {
	}

	public Document translate(DocumentListResponse reponse) {
		return null;
	}
}

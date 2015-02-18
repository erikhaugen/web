/**
 * Created:  Jul 11, 2013 3:23:35 PM
 */
package org.kfm.camel.translator;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;

import com.livescribe.framework.paperreplay.PaperReplay;

/**
 * <p>Parses a Paper Replay AFD into a <code>PaperReplay</code> object and
 * marshals it into XML.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class AfdToXmlTranslator {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * <p></p>
	 * 
	 */
	public AfdToXmlTranslator() {
		
	}

	/**
	 * <p></p>
	 * 
	 * @param afdFile
	 */
	public String translate(File afdFile) {
		
		String method = "translate()";
		
		logger.info("BEFORE - " + method);
		
		String afdPath = afdFile.getAbsolutePath();

		PaperReplay paperReplay = null;
		try {
			
			//--------------------------------------------------
			//	Create the Paper Replay Document from the given File.
			paperReplay = new PaperReplay(afdFile);

			logger.debug("     display ID:  " + paperReplay.getDisplayId());
			logger.debug("request list ID:  " + paperReplay.getRequestListId());

		} catch (ZipException ze) {
			//	TODO:  Send message to error queue.
			String msg = "ZipException thrown while creating PaperReplay object from AFD.";
			logger.error(method + " - " + msg, ze);
			return null;
		} catch (IOException ioe) {
			//	TODO:  Send message to error queue.
			String msg = "IOException thrown while creating PaperReplay object from AFD.";
			logger.error(method + " - " + msg, ioe);
			return null;
		}

		logger.info("AFTER - " + method);

		return paperReplay.toString();
	}
}

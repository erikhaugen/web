/**
 * Created:  Jun 18, 2013 7:03:50 PM
 */
package org.kfm.camel.afd;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import com.livescribe.afp.Document;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class DocumentFactory {

	private static Logger logger = Logger.getLogger(DocumentFactory.class.getName());
	
	/**
	 * <p></p>
	 * 
	 */
	public DocumentFactory() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * <p>Creates an AFD <code>Document</code> from the given
	 * <code>File</code>.</p>
	 * 
	 * @param file The <code>File</code> to create the <code>Document</code> 
	 * object from.
	 */
	public static Document create(File file) {
		
		String method = "createAfdDocument()";
		
//		File file = exchg.getIn().getBody(File.class);
		if ((file == null) || (!file.exists())) {
			logger.error(method + " - ZipFile was 'null'.  Unable to create Document.");
			return null;
		}

		//	Get the display ID from the ZIP filename.
		String inFilePath = file.getName();
		int idx = inFilePath.lastIndexOf("/") + 1;
		String penDisplayId = inFilePath.substring(idx, idx + 14);

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
		} catch (ZipException ze) {
			logger.error(method + " - " + penDisplayId + " - ZipFile was 'null'.  Unable to create Document.", ze);
			return null;
		} catch (IOException ioe) {
			logger.error(method + " - " + penDisplayId + " - IOException.", ioe);
			return null;
		}
		
		Document document = null;
		try {
			document = new Document(zipFile);
		} catch (IOException ioe) {
			String msg = "IOException thrown when creating new Document from AFD.";
			logger.error(method + " - " + penDisplayId + " - " + msg, ioe);
			return null;
		}
		return document;
	}
}

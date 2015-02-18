/**
 * Created:  Jul 13, 2013 9:22:57 PM
 */
package org.kfm.camel.afd;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.kfm.camel.util.Utils;

import com.livescribe.afp.Document;
import com.livescribe.framework.paperreplay.PaperReplay;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PaperReplayFactory {

	private static Logger logger = Logger.getLogger(PaperReplayFactory.class.getName());
	
	private Document document;
	
	/**
	 * <p></p>
	 * 
	 * @param file
	 * 
	 * @return
	 */
	public static PaperReplay create(File file) {
		
		String method = "create(File)";
		
		if (file == null) {
			logger.error(method + " - ZipFile was 'null'.  Unable to create Document.");
			return null;
		}

		//	Get the display ID from the ZIP filename.
		String inFilePath = file.getAbsolutePath();
		String penDisplayId = Utils.getDisplayIdFromFilePath(inFilePath);

		Document document = DocumentFactory.create(file);
		
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
		
		return null;
	}
}

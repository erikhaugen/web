/**
 * Created:  Jul 13, 2013 6:51:55 PM
 */
package org.kfm.camel.pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kfm.camel.message.AudioFileEntry;
import org.kfm.camel.message.AudioFileMessage;
import org.kfm.camel.message.ParsedAfdMessage;
import org.kfm.camel.message.SessionInfoEntry;
import org.kfm.camel.message.SessionInfoMessage;
import org.kfm.camel.message.XmlMessage;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PaperReplaySplitterBean {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * <p></p>
	 * 
	 */
	public PaperReplaySplitterBean() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * 
	 * @return
	 */
	public Iterator<XmlMessage> split(ParsedAfdMessage message) {
		
		String method = "split()";
		
		ArrayList<XmlMessage> list = new ArrayList<XmlMessage>();
		
		logger.debug(method + " - Splitting audio file entries ...");
		
//		List<AudioFileEntry> afeList = message.getAudioFileEntries();
//		Iterator<AudioFileEntry> afeIter = afeList.iterator();
//		while (afeIter.hasNext()) {
//			AudioFileEntry afe = afeIter.next();
//			AudioFileMessage afm = new AudioFileMessage(afe.getFilename());
//			list.add(afm);
//		}
		
		logger.debug(method + " - Splitting session info entries ...");
		
//		List<SessionInfoEntry> sieList = message.getSessionInfoEntries();
//		Iterator<SessionInfoEntry> sieIter = sieList.iterator();
//		while (sieIter.hasNext()) {
//			SessionInfoEntry sie = sieIter.next();
//			SessionInfoMessage sim = new SessionInfoMessage(sie.getFilename());
//			list.add(sim);
//		}
		
		logger.debug(method + " - Returning " + list.size() + " entries.");
				
		return list.iterator();
	}
}

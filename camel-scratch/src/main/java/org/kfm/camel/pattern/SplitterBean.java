/**
 * Created:  Jul 13, 2013 6:40:52 PM
 */
package org.kfm.camel.pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kfm.camel.message.AfdEntry;
import org.kfm.camel.message.ParsedAfdMessage;
import org.kfm.camel.message.SessionInfoEntry;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class SplitterBean {

	/**
	 * <p></p>
	 * 
	 */
	public SplitterBean() {
	}

	/**
	 * <p></p>
	 * 
	 * @param message
	 * 
	 * @return
	 */
	public Iterator<AfdEntry> split(ParsedAfdMessage message) {
		
		ArrayList<AfdEntry> list = new ArrayList<AfdEntry>();
		
//		List<SessionInfoEntry> sieList = message.getSessionInfoEntries();
//		message.getAudioFileEntries();
		
		return list.iterator();
	}
}

/**
 * Created:  Jul 1, 2013 12:01:51 AM
 */
package org.kfm.camel.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("session-info-entry")
public class SessionInfoEntry extends AfdEntry {

	@XStreamAlias("filename")
	@XStreamAsAttribute
	private String filename;
	
	/**
	 * <p></p>
	 * 
	 */
	public SessionInfoEntry() {
	}

	/**
	 * <p></p>
	 * 
	 * @param filename
	 */
	public SessionInfoEntry(String filename) {
		this.filename = filename;
	}

	/**
	 * <p></p>
	 * 
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * <p></p>
	 * 
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

}

/**
 * Created:  Jul 13, 2013 6:53:46 PM
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
@XStreamAlias("audio-file")
public class AudioFileMessage extends XmlMessage {

	@XStreamAlias("filename")
	@XStreamAsAttribute
	private String filename;
	
	/**
	 * <p></p>
	 * 
	 * @param filename
	 */
	public AudioFileMessage(String filename) {
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

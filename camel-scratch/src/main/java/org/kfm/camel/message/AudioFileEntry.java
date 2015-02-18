/**
 * Created:  Jun 30, 2013 11:52:50 PM
 */
package org.kfm.camel.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * <p>Represents a <code>ZipEntry</code> in an AFD.</p>
 * 
 * <p>Used as a message between Camel components.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("audio-file-entry")
public class AudioFileEntry extends AfdEntry {

	@XStreamAlias("filename")
	@XStreamAsAttribute
	private String filename;
	
	/**
	 * <p></p>
	 * 
	 */
	public AudioFileEntry() {
	}

	/**
	 * <p></p>
	 * 
	 * @param filename
	 */
	public AudioFileEntry(String filename) {
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

/**
 * Created:  Jul 2, 2013 11:14:14 AM
 */
package org.kfm.camel.message;

import com.livescribe.framework.paperreplay.PaperReplay;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("parsed-paper-replay")
public class ParsedAfdMessage extends XmlMessage {

	@XStreamAlias("display-id")
	private String displayId;
	
	@XStreamAlias("file-path")
	private String filePath;
	
	@XStreamAlias("uid")
	private String uid;
	
	@XStreamAlias("paper-replay")
	private PaperReplay paperReplay;
	
	/**
	 * <p></p>
	 * 
	 * @param afdLocation
	 */
	public ParsedAfdMessage(String displayId, String filePath) {
		this.displayId = displayId;
		this.filePath = filePath;
	}

	/**
	 * <p></p>
	 * 
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * <p></p>
	 * 
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		XStream xStream = new XStream();
		xStream.alias("paper-replay", PaperReplay.class);
		
		xStream.processAnnotations(PaperReplay.class);
		
		String pamStr = xStream.toXML(this);
		
		sb.append(pamStr);
		
		return sb.toString();
	}

	/**
	 * <p></p>
	 * 
	 * @return the displayId
	 */
	public String getDisplayId() {
		return displayId;
	}

	/**
	 * <p></p>
	 * 
	 * @param displayId the displayId to set
	 */
	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * <p></p>
	 * 
	 * @return the paperReplay
	 */
	public PaperReplay getPaperReplay() {
		return paperReplay;
	}

	/**
	 * <p></p>
	 * 
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * <p></p>
	 * 
	 * @param paperReplay the paperReplay to set
	 */
	public void setPaperReplay(PaperReplay paperReplay) {
		this.paperReplay = paperReplay;
	}
}

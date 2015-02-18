/**
 * Created:  Mar 12, 2014 5:20:57 PM
 */
package org.kfm.camel.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("json-audio-file")
public class AudioFileDTO extends FileDTO {

	//	This property need in Playback Service when writing
	//	M4A file to disk.  See AudioOGGView class.
	@XStreamAlias("uid")
	private String uid = "";
	
	@XStreamAlias("audio-id")
	private String audioId;
	
	@XStreamAlias("session-id")
	private String sessionId;

	/**
	 * <p></p>
	 * 
	 */
	public AudioFileDTO() {
		super();
		this.mimeType = "audio/mp4";
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
	 * @return the audioId
	 */
	public String getAudioId() {
		return audioId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
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
	 * @param audioId the audioId to set
	 */
	public void setAudioId(String audioId) {
		this.audioId = audioId;
	}

	/**
	 * <p></p>
	 * 
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}

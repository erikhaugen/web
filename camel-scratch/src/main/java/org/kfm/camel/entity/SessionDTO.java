/**
 * Created:  Mar 12, 2014 4:15:20 PM
 */
package org.kfm.camel.entity;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class SessionDTO {

	@XStreamAlias("id")
	protected long id = 0;
	
	@XStreamAlias("documentid")
	protected long documentId = 0;
	
	@XStreamAlias("evernoteGuidNote")
	protected String evernoteGuidNote = "";
	
	@XStreamAlias("evernoteHashUISettingResource")
	protected String evernoteHashUISettingResource = "";
	
	@XStreamAlias("evernoteHashLogoInactiveFrostResource")
	protected String evernoteHashLogoInactiveFrostResource = "";
	
	@XStreamAlias("starttime")
	protected long startTime = 0;
	
	@XStreamAlias("endtime")
	protected long endTime = 0;
	
	@XStreamAlias("name")
	protected String name = "";
    
	@XStreamAlias("json-audio-session")
	@XStreamImplicit
	protected List<AudioSessionDTO> audioSessions = new ArrayList<AudioSessionDTO>();
	
	@XStreamAlias("json-annot-session")
	@XStreamImplicit
	protected List<AnnotSessionDTO> annotSessions = new ArrayList<AnnotSessionDTO>();
	
	@XStreamAlias("json-audio")
	@XStreamImplicit
	protected List<AudioDTO> audios = new ArrayList<AudioDTO>();	

	/**
	 * <p></p>
	 * 
	 */
	public SessionDTO() {
	}

	/**
	 * <p></p>
	 * 
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * <p></p>
	 * 
	 * @return the documentId
	 */
	public long getDocumentId() {
		return documentId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the evernoteGuidNote
	 */
	public String getEvernoteGuidNote() {
		return evernoteGuidNote;
	}

	/**
	 * <p></p>
	 * 
	 * @return the evernoteHashUISettingResource
	 */
	public String getEvernoteHashUISettingResource() {
		return evernoteHashUISettingResource;
	}

	/**
	 * <p></p>
	 * 
	 * @return the evernoteHashLogoInactiveFrostResource
	 */
	public String getEvernoteHashLogoInactiveFrostResource() {
		return evernoteHashLogoInactiveFrostResource;
	}

	/**
	 * <p></p>
	 * 
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * <p></p>
	 * 
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * <p></p>
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p></p>
	 * 
	 * @return the audioSessions
	 */
	public List<AudioSessionDTO> getAudioSessions() {
		return audioSessions;
	}

	/**
	 * <p></p>
	 * 
	 * @return the annotSessions
	 */
	public List<AnnotSessionDTO> getAnnotSessions() {
		return annotSessions;
	}

	/**
	 * <p></p>
	 * 
	 * @return the audios
	 */
	public List<AudioDTO> getAudios() {
		return audios;
	}

	/**
	 * <p></p>
	 * 
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * <p></p>
	 * 
	 * @param documentId the documentId to set
	 */
	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	/**
	 * <p></p>
	 * 
	 * @param evernoteGuidNote the evernoteGuidNote to set
	 */
	public void setEvernoteGuidNote(String evernoteGuidNote) {
		this.evernoteGuidNote = evernoteGuidNote;
	}

	/**
	 * <p></p>
	 * 
	 * @param evernoteHashUISettingResource the evernoteHashUISettingResource to set
	 */
	public void setEvernoteHashUISettingResource(
			String evernoteHashUISettingResource) {
		this.evernoteHashUISettingResource = evernoteHashUISettingResource;
	}

	/**
	 * <p></p>
	 * 
	 * @param evernoteHashLogoInactiveFrostResource the evernoteHashLogoInactiveFrostResource to set
	 */
	public void setEvernoteHashLogoInactiveFrostResource(
			String evernoteHashLogoInactiveFrostResource) {
		this.evernoteHashLogoInactiveFrostResource = evernoteHashLogoInactiveFrostResource;
	}

	/**
	 * <p></p>
	 * 
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * <p></p>
	 * 
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * <p></p>
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * <p></p>
	 * 
	 * @param audioSessions the audioSessions to set
	 */
	public void setAudioSessions(List<AudioSessionDTO> audioSessions) {
		this.audioSessions = audioSessions;
	}

	/**
	 * <p></p>
	 * 
	 * @param annotSessions the annotSessions to set
	 */
	public void setAnnotSessions(List<AnnotSessionDTO> annotSessions) {
		this.annotSessions = annotSessions;
	}

	/**
	 * <p></p>
	 * 
	 * @param audios the audios to set
	 */
	public void setAudios(List<AudioDTO> audios) {
		this.audios = audios;
	}

}

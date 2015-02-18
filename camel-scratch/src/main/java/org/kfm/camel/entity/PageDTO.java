/**
 * Created:  Mar 12, 2014 4:15:08 PM
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
@XStreamAlias("json-page")
public class PageDTO {

	@XStreamAlias("id")
	private long id = 0;
	
	@XStreamAlias("documentid")
	private long documentId = 0;
	
	@XStreamAlias("templateid")
	private long templateId = 0;
	
	@XStreamAlias("evernoteGuidNote")
	private String evernoteGuidNote = "";
	
	@XStreamAlias("strokeid")
	private String strokeId = null; //evernoteGuidStrokeResource
	
	@XStreamAlias("evernoteHashStrokeResource")
	private String evernoteHashStrokeResource = "";
	
	@XStreamAlias("evernoteGuidImageResource")
	private String evernoteGuidImageResource = "";
	
	@XStreamAlias("evernoteHashImageResource")
	private String evernoteHashImageResource = "";
	
	@XStreamAlias("evernoteHashUISettingResource")
	private String evernoteHashUISettingResource = "";
	
	@XStreamAlias("evernoteHashLogoInactiveFrostResource")
	private String evernoteHashLogoInactiveFrostResource = "";
	
	@XStreamAlias("index")
	private int index = 0;
	
	@XStreamAlias("label")
	private String label = "";
	
	@XStreamAlias("starttime")
	private long startTime = 0;
	
	@XStreamAlias("endtime")
	private long endTime = 0;
	
	@XStreamAlias("templateindex")
	private int templateIndex = 0;
	
	@XStreamAlias("laststroketime")
	private long lastStrokeTime = 0;
    
	@XStreamAlias("json-session")
	@XStreamImplicit
	private List<SessionDTO> sessions = new ArrayList<SessionDTO>();

	/**
	 * <p></p>
	 * 
	 */
	public PageDTO() {
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
	 * @return the templateId
	 */
	public long getTemplateId() {
		return templateId;
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
	 * @return the strokeId
	 */
	public String getStrokeId() {
		return strokeId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the evernoteHashStrokeResource
	 */
	public String getEvernoteHashStrokeResource() {
		return evernoteHashStrokeResource;
	}

	/**
	 * <p></p>
	 * 
	 * @return the evernoteGuidImageResource
	 */
	public String getEvernoteGuidImageResource() {
		return evernoteGuidImageResource;
	}

	/**
	 * <p></p>
	 * 
	 * @return the evernoteHashImageResource
	 */
	public String getEvernoteHashImageResource() {
		return evernoteHashImageResource;
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
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * <p></p>
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
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
	 * @return the templateIndex
	 */
	public int getTemplateIndex() {
		return templateIndex;
	}

	/**
	 * <p></p>
	 * 
	 * @return the lastStrokeTime
	 */
	public long getLastStrokeTime() {
		return lastStrokeTime;
	}

	/**
	 * <p></p>
	 * 
	 * @return the sessions
	 */
	public List<SessionDTO> getSessions() {
		return sessions;
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
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
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
	 * @param strokeId the strokeId to set
	 */
	public void setStrokeId(String strokeId) {
		this.strokeId = strokeId;
	}

	/**
	 * <p></p>
	 * 
	 * @param evernoteHashStrokeResource the evernoteHashStrokeResource to set
	 */
	public void setEvernoteHashStrokeResource(String evernoteHashStrokeResource) {
		this.evernoteHashStrokeResource = evernoteHashStrokeResource;
	}

	/**
	 * <p></p>
	 * 
	 * @param evernoteGuidImageResource the evernoteGuidImageResource to set
	 */
	public void setEvernoteGuidImageResource(String evernoteGuidImageResource) {
		this.evernoteGuidImageResource = evernoteGuidImageResource;
	}

	/**
	 * <p></p>
	 * 
	 * @param evernoteHashImageResource the evernoteHashImageResource to set
	 */
	public void setEvernoteHashImageResource(String evernoteHashImageResource) {
		this.evernoteHashImageResource = evernoteHashImageResource;
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
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * <p></p>
	 * 
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
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
	 * @param templateIndex the templateIndex to set
	 */
	public void setTemplateIndex(int templateIndex) {
		this.templateIndex = templateIndex;
	}

	/**
	 * <p></p>
	 * 
	 * @param lastStrokeTime the lastStrokeTime to set
	 */
	public void setLastStrokeTime(long lastStrokeTime) {
		this.lastStrokeTime = lastStrokeTime;
	}

	/**
	 * <p></p>
	 * 
	 * @param sessions the sessions to set
	 */
	public void setSessions(List<SessionDTO> sessions) {
		this.sessions = sessions;
	}

}

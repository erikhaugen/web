/**
 * 
 */
package com.livescribe.admin.controller.dto;

import java.util.Date;

import com.livescribe.framework.orm.lsevernotedb.Session;

/**
 * @author mnaqvi
 *
 */
public class SessionDTO {
	private Long sessionId;
	private long documentId;
	private String evernoteGuidNote;
	private Date startTime;
	private Date endTime;
	private Date created;
	private Date lastModified;
	private String lastModifiedBy;

	/**
	 * The Default Constructor
	 */
	public SessionDTO() {
	}
	
	public SessionDTO(Session session) {
		sessionId = session.getSessionId();
		documentId = session.getDocumentId();
		evernoteGuidNote = session.getEvernoteGuidNote();
		if (0l < session.getStartTime()) {
			startTime = new java.util.Date(session.getStartTime());
		}
		if (0l < session.getEndTime()) {
			endTime = new java.util.Date(session.getEndTime());
		}
		created = session.getCreated();
		lastModified = session.getLastModified();
		lastModifiedBy = session.getLastModifiedBy();
	}

	/**
	 * @return the sessionId
	 */
	public Long getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the documentId
	 */
	public long getDocumentId() {
		return documentId;
	}

	/**
	 * @param documentId the documentId to set
	 */
	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	/**
	 * @return the evernoteGuidNote
	 */
	public String getEvernoteGuidNote() {
		return evernoteGuidNote;
	}

	/**
	 * @param evernoteGuidNote the evernoteGuidNote to set
	 */
	public void setEvernoteGuidNote(String evernoteGuidNote) {
		this.evernoteGuidNote = evernoteGuidNote;
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the lastModified
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the lastModifiedBy
	 */
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	/**
	 * @param lastModifiedBy the lastModifiedBy to set
	 */
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
}

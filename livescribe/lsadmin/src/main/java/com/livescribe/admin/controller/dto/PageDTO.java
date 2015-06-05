/**
 * 
 */
package com.livescribe.admin.controller.dto;

import java.util.Date;

import com.livescribe.framework.orm.lsevernotedb.Page;

/**
 * @author mnaqvi
 *
 */
public class PageDTO {
	
    private Long pageId;
    private long documentId;
    private long templateId;
    private String evernoteGuidNote;
    private String evernoteGuidStrokeResource;
    private String evernoteHashStrokeResource;
    private String evernoteGuidImageResource;
    private String evernoteHashImageResource;
    private int pageIndex;
    private String label;
    private Date startTime;
    private Date endTime;
    private Date created;
    private Date lastModified;
    private String lastModifiedBy;

	/**
	 * The Default Contructor
	 */
	public PageDTO() {
	}

	public PageDTO(Page page) {
		if (null != page) {
			pageId = page.getPageId();
			documentId = page.getDocumentId();
			templateId = page.getTemplateId();
			evernoteGuidNote = page.getEvernoteGuidNote();
			evernoteGuidStrokeResource = page.getEvernoteGuidStrokeResource();
			evernoteGuidImageResource = page.getEvernoteGuidImageResource();
			evernoteHashImageResource = page.getEvernoteHashImageResource();
			pageIndex = page.getPageIndex();
			label = page.getLabel();
			if (0l < page.getStartTime()) {
				startTime = new java.util.Date(page.getStartTime());
			}
			if (0l < page.getEndTime()) {
				endTime = new java.util.Date(page.getEndTime());
			}
	        created = page.getCreated();
	        lastModified = page.getLastModified();
	        lastModifiedBy = page.getLastModifiedBy();
		}
		
	}

	/**
	 * @return the pageId
	 */
	public Long getPageId() {
		return pageId;
	}

	/**
	 * @param pageId the pageId to set
	 */
	public void setPageId(Long pageId) {
		this.pageId = pageId;
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
	 * @return the templateId
	 */
	public long getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
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
	 * @return the evernoteGuidStrokeResource
	 */
	public String getEvernoteGuidStrokeResource() {
		return evernoteGuidStrokeResource;
	}

	/**
	 * @param evernoteGuidStrokeResource the evernoteGuidStrokeResource to set
	 */
	public void setEvernoteGuidStrokeResource(String evernoteGuidStrokeResource) {
		this.evernoteGuidStrokeResource = evernoteGuidStrokeResource;
	}

	/**
	 * @return the evernoteHashStrokeResource
	 */
	public String getEvernoteHashStrokeResource() {
		return evernoteHashStrokeResource;
	}

	/**
	 * @param evernoteHashStrokeResource the evernoteHashStrokeResource to set
	 */
	public void setEvernoteHashStrokeResource(String evernoteHashStrokeResource) {
		this.evernoteHashStrokeResource = evernoteHashStrokeResource;
	}

	/**
	 * @return the evernoteGuidImageResource
	 */
	public String getEvernoteGuidImageResource() {
		return evernoteGuidImageResource;
	}

	/**
	 * @param evernoteGuidImageResource the evernoteGuidImageResource to set
	 */
	public void setEvernoteGuidImageResource(String evernoteGuidImageResource) {
		this.evernoteGuidImageResource = evernoteGuidImageResource;
	}

	/**
	 * @return the evernoteHashImageResource
	 */
	public String getEvernoteHashImageResource() {
		return evernoteHashImageResource;
	}

	/**
	 * @param evernoteHashImageResource the evernoteHashImageResource to set
	 */
	public void setEvernoteHashImageResource(String evernoteHashImageResource) {
		this.evernoteHashImageResource = evernoteHashImageResource;
	}

	/**
	 * @return the pageIndex
	 */
	public int getPageIndex() {
		return pageIndex;
	}

	/**
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
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

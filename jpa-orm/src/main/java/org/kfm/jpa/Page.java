package org.kfm.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the page database table.
 * 
 */
@Entity
@NamedQuery(name="Page.findAll", query="SELECT p FROM Page p")
public class Page implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="page_id")
	private String pageId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name="en_image_resource_guid")
	private String enImageResourceGuid;

	@Column(name="en_image_resource_hash")
	private String enImageResourceHash;

	@Column(name="en_ls_logo_resource_hash")
	private String enLsLogoResourceHash;

	@Column(name="en_ls_ui_set_resource_hash")
	private String enLsUiSetResourceHash;

	@Column(name="en_note_guid")
	private String enNoteGuid;

	@Column(name="en_stroke_resource_guid")
	private String enStrokeResourceGuid;

	@Column(name="en_stroke_resource_hash")
	private String enStrokeResourceHash;

	@Column(name="end_time")
	private BigInteger endTime;

	private String label;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_modified")
	private Date lastModified;

	@Column(name="last_modified_by")
	private String lastModifiedBy;

	@Column(name="page_index")
	private int pageIndex;

	@Column(name="start_time")
	private BigInteger startTime;

	@Column(name="update_counter")
	private BigInteger updateCounter;

	//bi-directional many-to-one association to ContentAccess
	@OneToMany(mappedBy="page")
	private List<ContentAccess> contentAccesses;

	//bi-directional many-to-one association to AfdImage
	@ManyToOne
	@JoinColumn(name="afd_image_id")
	private AfdImage afdImage;

	//bi-directional many-to-one association to Document
	@ManyToOne
	@JoinColumn(name="document_id")
	private Document document;

	//bi-directional many-to-one association to Template
	@ManyToOne
	@JoinColumn(name="template_id")
	private Template template;

	//bi-directional many-to-one association to TimeMap
	@OneToMany(mappedBy="page")
	private List<TimeMap> timeMaps;

	public Page() {
	}

	public String getPageId() {
		return this.pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getEnImageResourceGuid() {
		return this.enImageResourceGuid;
	}

	public void setEnImageResourceGuid(String enImageResourceGuid) {
		this.enImageResourceGuid = enImageResourceGuid;
	}

	public String getEnImageResourceHash() {
		return this.enImageResourceHash;
	}

	public void setEnImageResourceHash(String enImageResourceHash) {
		this.enImageResourceHash = enImageResourceHash;
	}

	public String getEnLsLogoResourceHash() {
		return this.enLsLogoResourceHash;
	}

	public void setEnLsLogoResourceHash(String enLsLogoResourceHash) {
		this.enLsLogoResourceHash = enLsLogoResourceHash;
	}

	public String getEnLsUiSetResourceHash() {
		return this.enLsUiSetResourceHash;
	}

	public void setEnLsUiSetResourceHash(String enLsUiSetResourceHash) {
		this.enLsUiSetResourceHash = enLsUiSetResourceHash;
	}

	public String getEnNoteGuid() {
		return this.enNoteGuid;
	}

	public void setEnNoteGuid(String enNoteGuid) {
		this.enNoteGuid = enNoteGuid;
	}

	public String getEnStrokeResourceGuid() {
		return this.enStrokeResourceGuid;
	}

	public void setEnStrokeResourceGuid(String enStrokeResourceGuid) {
		this.enStrokeResourceGuid = enStrokeResourceGuid;
	}

	public String getEnStrokeResourceHash() {
		return this.enStrokeResourceHash;
	}

	public void setEnStrokeResourceHash(String enStrokeResourceHash) {
		this.enStrokeResourceHash = enStrokeResourceHash;
	}

	public BigInteger getEndTime() {
		return this.endTime;
	}

	public void setEndTime(BigInteger endTime) {
		this.endTime = endTime;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getLastModifiedBy() {
		return this.lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public int getPageIndex() {
		return this.pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public BigInteger getStartTime() {
		return this.startTime;
	}

	public void setStartTime(BigInteger startTime) {
		this.startTime = startTime;
	}

	public BigInteger getUpdateCounter() {
		return this.updateCounter;
	}

	public void setUpdateCounter(BigInteger updateCounter) {
		this.updateCounter = updateCounter;
	}

	public List<ContentAccess> getContentAccesses() {
		return this.contentAccesses;
	}

	public void setContentAccesses(List<ContentAccess> contentAccesses) {
		this.contentAccesses = contentAccesses;
	}

	public ContentAccess addContentAccess(ContentAccess contentAccess) {
		getContentAccesses().add(contentAccess);
		contentAccess.setPage(this);

		return contentAccess;
	}

	public ContentAccess removeContentAccess(ContentAccess contentAccess) {
		getContentAccesses().remove(contentAccess);
		contentAccess.setPage(null);

		return contentAccess;
	}

	public AfdImage getAfdImage() {
		return this.afdImage;
	}

	public void setAfdImage(AfdImage afdImage) {
		this.afdImage = afdImage;
	}

	public Document getDocument() {
		return this.document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Template getTemplate() {
		return this.template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public List<TimeMap> getTimeMaps() {
		return this.timeMaps;
	}

	public void setTimeMaps(List<TimeMap> timeMaps) {
		this.timeMaps = timeMaps;
	}

	public TimeMap addTimeMap(TimeMap timeMap) {
		getTimeMaps().add(timeMap);
		timeMap.setPage(this);

		return timeMap;
	}

	public TimeMap removeTimeMap(TimeMap timeMap) {
		getTimeMaps().remove(timeMap);
		timeMap.setPage(null);

		return timeMap;
	}

}
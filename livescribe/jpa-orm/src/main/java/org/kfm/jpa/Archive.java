package org.kfm.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;


/**
 * The persistent class for the archive database table.
 * 
 */
@Entity
@NamedQuery(name="Archive.findAll", query="SELECT a FROM Archive a")
public class Archive implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="archive_id")
	private String archiveId;

	@Column(name="archived_notebook_name")
	private String archivedNotebookName;

	@Column(name="authorization_id")
	private BigInteger authorizationId;

	private String comment;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_deleted_by_user")
	private Date dateDeletedByUser;

	@Column(name="end_time")
	private BigInteger endTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_modified")
	private Date lastModified;

	@Column(name="last_modified_by")
	private String lastModifiedBy;

	@Column(name="start_time")
	private BigInteger startTime;

	//bi-directional many-to-one association to Document
	@ManyToOne
	@JoinColumn(name="document_id")
	private Document document;

	//bi-directional many-to-one association to SyncTimesInfo
	@ManyToOne
	@JoinColumn(name="sync_times_info_id")
	private SyncTimesInfo syncTimesInfo;

	public Archive() {
	}

	public String getArchiveId() {
		return this.archiveId;
	}

	public void setArchiveId(String archiveId) {
		this.archiveId = archiveId;
	}

	public String getArchivedNotebookName() {
		return this.archivedNotebookName;
	}

	public void setArchivedNotebookName(String archivedNotebookName) {
		this.archivedNotebookName = archivedNotebookName;
	}

	public BigInteger getAuthorizationId() {
		return this.authorizationId;
	}

	public void setAuthorizationId(BigInteger authorizationId) {
		this.authorizationId = authorizationId;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getDateDeletedByUser() {
		return this.dateDeletedByUser;
	}

	public void setDateDeletedByUser(Date dateDeletedByUser) {
		this.dateDeletedByUser = dateDeletedByUser;
	}

	public BigInteger getEndTime() {
		return this.endTime;
	}

	public void setEndTime(BigInteger endTime) {
		this.endTime = endTime;
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

	public BigInteger getStartTime() {
		return this.startTime;
	}

	public void setStartTime(BigInteger startTime) {
		this.startTime = startTime;
	}

	public Document getDocument() {
		return this.document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public SyncTimesInfo getSyncTimesInfo() {
		return this.syncTimesInfo;
	}

	public void setSyncTimesInfo(SyncTimesInfo syncTimesInfo) {
		this.syncTimesInfo = syncTimesInfo;
	}

}
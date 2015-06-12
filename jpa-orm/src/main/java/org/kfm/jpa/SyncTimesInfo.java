package org.kfm.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the sync_times_info database table.
 * 
 */
@Entity
@Table(name="sync_times_info")
@NamedQuery(name="SyncTimesInfo.findAll", query="SELECT s FROM SyncTimesInfo s")
public class SyncTimesInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="sync_times_info_id")
	private String syncTimesInfoId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name="doc_name")
	private String docName;

	@Column(name="end_time")
	private BigInteger endTime;

	private String guid;

	@Column(name="in_progress_time")
	private BigInteger inProgressTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_modified")
	private Date lastModified;

	@Column(name="last_modified_by")
	private String lastModifiedBy;

	@Column(name="pen_display_id")
	private String penDisplayId;

	@Column(name="start_time")
	private BigInteger startTime;

	private String user;

	//bi-directional many-to-one association to Archive
	@OneToMany(mappedBy="syncTimesInfo")
	private List<Archive> archives;

	//bi-directional many-to-one association to Document
	@ManyToOne
	@JoinColumn(name="document_id")
	private Document document;

	public SyncTimesInfo() {
	}

	public String getSyncTimesInfoId() {
		return this.syncTimesInfoId;
	}

	public void setSyncTimesInfoId(String syncTimesInfoId) {
		this.syncTimesInfoId = syncTimesInfoId;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getDocName() {
		return this.docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public BigInteger getEndTime() {
		return this.endTime;
	}

	public void setEndTime(BigInteger endTime) {
		this.endTime = endTime;
	}

	public String getGuid() {
		return this.guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public BigInteger getInProgressTime() {
		return this.inProgressTime;
	}

	public void setInProgressTime(BigInteger inProgressTime) {
		this.inProgressTime = inProgressTime;
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

	public String getPenDisplayId() {
		return this.penDisplayId;
	}

	public void setPenDisplayId(String penDisplayId) {
		this.penDisplayId = penDisplayId;
	}

	public BigInteger getStartTime() {
		return this.startTime;
	}

	public void setStartTime(BigInteger startTime) {
		this.startTime = startTime;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<Archive> getArchives() {
		return this.archives;
	}

	public void setArchives(List<Archive> archives) {
		this.archives = archives;
	}

	public Archive addArchive(Archive archive) {
		getArchives().add(archive);
		archive.setSyncTimesInfo(this);

		return archive;
	}

	public Archive removeArchive(Archive archive) {
		getArchives().remove(archive);
		archive.setSyncTimesInfo(null);

		return archive;
	}

	public Document getDocument() {
		return this.document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

}
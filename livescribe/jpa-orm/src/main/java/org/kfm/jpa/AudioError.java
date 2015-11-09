package org.kfm.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the audio_error database table.
 * 
 */
@Entity
@Table(name="audio_error")
@NamedQuery(name="AudioError.findAll", query="SELECT a FROM AudioError a")
public class AudioError implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="audio_error_id")
	private String audioErrorId;

	@Column(name="aac_size")
	private int aacSize;

	private Timestamp created;

	@Column(name="error_date")
	private Timestamp errorDate;

	@Column(name="file_system_path")
	private String fileSystemPath;

	@Column(name="last_modified")
	private Timestamp lastModified;

	@Column(name="last_modified_by")
	private String lastModifiedBy;

	@Column(name="pen_display_id")
	private String penDisplayId;

	private String uid;

	//bi-directional many-to-one association to Document
	@ManyToOne
	@JoinColumn(name="document_id")
	private Document document;

	//bi-directional many-to-one association to Session
	@ManyToOne
	@JoinColumn(name="session_id")
	private Session session;

	public AudioError() {
	}

	public String getAudioErrorId() {
		return this.audioErrorId;
	}

	public void setAudioErrorId(String audioErrorId) {
		this.audioErrorId = audioErrorId;
	}

	public int getAacSize() {
		return this.aacSize;
	}

	public void setAacSize(int aacSize) {
		this.aacSize = aacSize;
	}

	public Timestamp getCreated() {
		return this.created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Timestamp getErrorDate() {
		return this.errorDate;
	}

	public void setErrorDate(Timestamp errorDate) {
		this.errorDate = errorDate;
	}

	public String getFileSystemPath() {
		return this.fileSystemPath;
	}

	public void setFileSystemPath(String fileSystemPath) {
		this.fileSystemPath = fileSystemPath;
	}

	public Timestamp getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(Timestamp lastModified) {
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

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Document getDocument() {
		return this.document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Session getSession() {
		return this.session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}
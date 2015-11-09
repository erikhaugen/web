package org.kfm.jpa;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;


/**
 * The persistent class for the document database table.
 * 
 */
@Entity
@NamedQuery(name="Document.findAll", query="SELECT d FROM Document d")
public class Document implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="document_id")
	private String documentId;

	private BigInteger archive;

	private BigInteger copy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name="doc_name")
	private String docName;

	@Column(name="en_notebook_guid")
	private String enNotebookGuid;

	@Column(name="en_user_id")
	private BigInteger enUserId;

	private String guid;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_modified")
	private Date lastModified;

	@Column(name="last_modified_by")
	private String lastModifiedBy;

	@Column(name="pen_display_id")
	private String penDisplayId;

	private String uid;

	//bi-directional many-to-one association to Archive
	@OneToMany(mappedBy="document")
	private List<Archive> archives;

	//bi-directional many-to-one association to AudioError
	@OneToMany(mappedBy="document")
	private List<AudioError> audioErrors;

	//bi-directional many-to-one association to ContentAccess
	@OneToMany(mappedBy="document")
	private List<ContentAccess> contentAccesses;

	//bi-directional many-to-one association to SyncConfig
	@ManyToOne
	@JoinColumn(name="sync_config_id")
	private SyncConfig syncConfig;

	//bi-directional many-to-one association to Page
	@OneToMany(mappedBy="document")
	private List<Page> pages;

	//bi-directional many-to-one association to Session
	@OneToMany(mappedBy="document")
	private List<Session> sessions;

	//bi-directional many-to-one association to SyncTimesInfo
	@OneToMany(mappedBy="document")
	private List<SyncTimesInfo> syncTimesInfos;

	//bi-directional many-to-one association to Template
	@OneToMany(mappedBy="document")
	private List<Template> templates;

	public Document() {
	}

	public String getDocumentId() {
		return this.documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public BigInteger getArchive() {
		return this.archive;
	}

	public void setArchive(BigInteger archive) {
		this.archive = archive;
	}

	public BigInteger getCopy() {
		return this.copy;
	}

	public void setCopy(BigInteger copy) {
		this.copy = copy;
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

	public String getEnNotebookGuid() {
		return this.enNotebookGuid;
	}

	public void setEnNotebookGuid(String enNotebookGuid) {
		this.enNotebookGuid = enNotebookGuid;
	}

	public BigInteger getEnUserId() {
		return this.enUserId;
	}

	public void setEnUserId(BigInteger enUserId) {
		this.enUserId = enUserId;
	}

	public String getGuid() {
		return this.guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
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

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public List<Archive> getArchives() {
		return this.archives;
	}

	public void setArchives(List<Archive> archives) {
		this.archives = archives;
	}

	public Archive addArchive(Archive archive) {
		getArchives().add(archive);
		archive.setDocument(this);

		return archive;
	}

	public Archive removeArchive(Archive archive) {
		getArchives().remove(archive);
		archive.setDocument(null);

		return archive;
	}

	public List<AudioError> getAudioErrors() {
		return this.audioErrors;
	}

	public void setAudioErrors(List<AudioError> audioErrors) {
		this.audioErrors = audioErrors;
	}

	public AudioError addAudioError(AudioError audioError) {
		getAudioErrors().add(audioError);
		audioError.setDocument(this);

		return audioError;
	}

	public AudioError removeAudioError(AudioError audioError) {
		getAudioErrors().remove(audioError);
		audioError.setDocument(null);

		return audioError;
	}

	public List<ContentAccess> getContentAccesses() {
		return this.contentAccesses;
	}

	public void setContentAccesses(List<ContentAccess> contentAccesses) {
		this.contentAccesses = contentAccesses;
	}

	public ContentAccess addContentAccess(ContentAccess contentAccess) {
		getContentAccesses().add(contentAccess);
		contentAccess.setDocument(this);

		return contentAccess;
	}

	public ContentAccess removeContentAccess(ContentAccess contentAccess) {
		getContentAccesses().remove(contentAccess);
		contentAccess.setDocument(null);

		return contentAccess;
	}

	public SyncConfig getSyncConfig() {
		return this.syncConfig;
	}

	public void setSyncConfig(SyncConfig syncConfig) {
		this.syncConfig = syncConfig;
	}

	public List<Page> getPages() {
		return this.pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	public Page addPage(Page page) {
		getPages().add(page);
		page.setDocument(this);

		return page;
	}

	public Page removePage(Page page) {
		getPages().remove(page);
		page.setDocument(null);

		return page;
	}

	public List<Session> getSessions() {
		return this.sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	public Session addSession(Session session) {
		getSessions().add(session);
		session.setDocument(this);

		return session;
	}

	public Session removeSession(Session session) {
		getSessions().remove(session);
		session.setDocument(null);

		return session;
	}

	public List<SyncTimesInfo> getSyncTimesInfos() {
		return this.syncTimesInfos;
	}

	public void setSyncTimesInfos(List<SyncTimesInfo> syncTimesInfos) {
		this.syncTimesInfos = syncTimesInfos;
	}

	public SyncTimesInfo addSyncTimesInfo(SyncTimesInfo syncTimesInfo) {
		getSyncTimesInfos().add(syncTimesInfo);
		syncTimesInfo.setDocument(this);

		return syncTimesInfo;
	}

	public SyncTimesInfo removeSyncTimesInfo(SyncTimesInfo syncTimesInfo) {
		getSyncTimesInfos().remove(syncTimesInfo);
		syncTimesInfo.setDocument(null);

		return syncTimesInfo;
	}

	public List<Template> getTemplates() {
		return this.templates;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}

	public Template addTemplate(Template template) {
		getTemplates().add(template);
		template.setDocument(this);

		return template;
	}

	public Template removeTemplate(Template template) {
		getTemplates().remove(template);
		template.setDocument(null);

		return template;
	}

}
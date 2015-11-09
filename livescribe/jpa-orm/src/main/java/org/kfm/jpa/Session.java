package org.kfm.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the session database table.
 * 
 */
@Entity
@NamedQuery(name="Session.findAll", query="SELECT s FROM Session s")
public class Session implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="session_id")
	private String sessionId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name="en_hash_ls_logo_resource")
	private String enHashLsLogoResource;

	@Column(name="en_hash_ls_ui_set_resource")
	private String enHashLsUiSetResource;

	@Column(name="end_time")
	private BigInteger endTime;

	@Column(name="evernote_guid_note")
	private String evernoteGuidNote;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_modified")
	private Date lastModified;

	@Column(name="last_modified_by")
	private String lastModifiedBy;

	@Column(name="start_time")
	private BigInteger startTime;

	//bi-directional many-to-one association to Audio
	@OneToMany(mappedBy="session")
	private List<Audio> audios;

	//bi-directional many-to-one association to AudioError
	@OneToMany(mappedBy="session")
	private List<AudioError> audioErrors;

	//bi-directional many-to-one association to ContentAccess
	@OneToMany(mappedBy="session")
	private List<ContentAccess> contentAccesses;

	//bi-directional many-to-one association to Document
	@ManyToOne
	@JoinColumn(name="document_id")
	private Document document;

	//bi-directional many-to-one association to TimeMap
	@OneToMany(mappedBy="session")
	private List<TimeMap> timeMaps;

	public Session() {
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getEnHashLsLogoResource() {
		return this.enHashLsLogoResource;
	}

	public void setEnHashLsLogoResource(String enHashLsLogoResource) {
		this.enHashLsLogoResource = enHashLsLogoResource;
	}

	public String getEnHashLsUiSetResource() {
		return this.enHashLsUiSetResource;
	}

	public void setEnHashLsUiSetResource(String enHashLsUiSetResource) {
		this.enHashLsUiSetResource = enHashLsUiSetResource;
	}

	public BigInteger getEndTime() {
		return this.endTime;
	}

	public void setEndTime(BigInteger endTime) {
		this.endTime = endTime;
	}

	public String getEvernoteGuidNote() {
		return this.evernoteGuidNote;
	}

	public void setEvernoteGuidNote(String evernoteGuidNote) {
		this.evernoteGuidNote = evernoteGuidNote;
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

	public List<Audio> getAudios() {
		return this.audios;
	}

	public void setAudios(List<Audio> audios) {
		this.audios = audios;
	}

	public Audio addAudio(Audio audio) {
		getAudios().add(audio);
		audio.setSession(this);

		return audio;
	}

	public Audio removeAudio(Audio audio) {
		getAudios().remove(audio);
		audio.setSession(null);

		return audio;
	}

	public List<AudioError> getAudioErrors() {
		return this.audioErrors;
	}

	public void setAudioErrors(List<AudioError> audioErrors) {
		this.audioErrors = audioErrors;
	}

	public AudioError addAudioError(AudioError audioError) {
		getAudioErrors().add(audioError);
		audioError.setSession(this);

		return audioError;
	}

	public AudioError removeAudioError(AudioError audioError) {
		getAudioErrors().remove(audioError);
		audioError.setSession(null);

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
		contentAccess.setSession(this);

		return contentAccess;
	}

	public ContentAccess removeContentAccess(ContentAccess contentAccess) {
		getContentAccesses().remove(contentAccess);
		contentAccess.setSession(null);

		return contentAccess;
	}

	public Document getDocument() {
		return this.document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public List<TimeMap> getTimeMaps() {
		return this.timeMaps;
	}

	public void setTimeMaps(List<TimeMap> timeMaps) {
		this.timeMaps = timeMaps;
	}

	public TimeMap addTimeMap(TimeMap timeMap) {
		getTimeMaps().add(timeMap);
		timeMap.setSession(this);

		return timeMap;
	}

	public TimeMap removeTimeMap(TimeMap timeMap) {
		getTimeMaps().remove(timeMap);
		timeMap.setSession(null);

		return timeMap;
	}

}
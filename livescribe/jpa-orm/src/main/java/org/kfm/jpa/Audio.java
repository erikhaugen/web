package org.kfm.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;


/**
 * The persistent class for the audio database table.
 * 
 */
@Entity
@NamedQuery(name="Audio.findAll", query="SELECT a FROM Audio a")
public class Audio implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="audio_id")
	private String audioId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name="en_audio_resource_guid")
	private String enAudioResourceGuid;

	@Column(name="en_audio_resource_hash")
	private String enAudioResourceHash;

	@Column(name="end_time")
	private BigInteger endTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_modified")
	private Date lastModified;

	@Column(name="last_modified_by")
	private String lastModifiedBy;

	@Column(name="start_time")
	private BigInteger startTime;

	//bi-directional many-to-one association to Session
	@ManyToOne
	@JoinColumn(name="session_id")
	private Session session;

	public Audio() {
	}

	public String getAudioId() {
		return this.audioId;
	}

	public void setAudioId(String audioId) {
		this.audioId = audioId;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getEnAudioResourceGuid() {
		return this.enAudioResourceGuid;
	}

	public void setEnAudioResourceGuid(String enAudioResourceGuid) {
		this.enAudioResourceGuid = enAudioResourceGuid;
	}

	public String getEnAudioResourceHash() {
		return this.enAudioResourceHash;
	}

	public void setEnAudioResourceHash(String enAudioResourceHash) {
		this.enAudioResourceHash = enAudioResourceHash;
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

	public Session getSession() {
		return this.session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}
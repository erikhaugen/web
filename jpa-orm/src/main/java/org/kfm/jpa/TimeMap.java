package org.kfm.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;


/**
 * The persistent class for the time_map database table.
 * 
 */
@Entity
@Table(name="time_map")
@NamedQuery(name="TimeMap.findAll", query="SELECT t FROM TimeMap t")
public class TimeMap implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="time_map_id")
	private String timeMapId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name="end_time")
	private BigInteger endTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_modified")
	private Date lastModified;

	@Column(name="last_modified_by")
	private String lastModifiedBy;

	@Column(name="map_time")
	private BigInteger mapTime;

	@Column(name="start_time")
	private BigInteger startTime;

	//bi-directional many-to-one association to Page
	@ManyToOne
	@JoinColumn(name="page_id")
	private Page page;

	//bi-directional many-to-one association to Session
	@ManyToOne
	@JoinColumn(name="session_id")
	private Session session;

	public TimeMap() {
	}

	public String getTimeMapId() {
		return this.timeMapId;
	}

	public void setTimeMapId(String timeMapId) {
		this.timeMapId = timeMapId;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
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

	public BigInteger getMapTime() {
		return this.mapTime;
	}

	public void setMapTime(BigInteger mapTime) {
		this.mapTime = mapTime;
	}

	public BigInteger getStartTime() {
		return this.startTime;
	}

	public void setStartTime(BigInteger startTime) {
		this.startTime = startTime;
	}

	public Page getPage() {
		return this.page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public Session getSession() {
		return this.session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}
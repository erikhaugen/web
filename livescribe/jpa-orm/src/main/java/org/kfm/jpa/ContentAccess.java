package org.kfm.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;


/**
 * The persistent class for the content_access database table.
 * 
 */
@Entity
@Table(name="content_access")
@NamedQuery(name="ContentAccess.findAll", query="SELECT c FROM ContentAccess c")
public class ContentAccess implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cak_id")
	private String cakId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name="en_user_id")
	private BigInteger enUserId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_modified")
	private Date lastModified;

	@Column(name="last_modified_by")
	private String lastModifiedBy;

	@Column(name="other_data")
	private String otherData;

	private String uid;

	//bi-directional many-to-one association to Document
	@ManyToOne
	@JoinColumn(name="document_id")
	private Document document;

	//bi-directional many-to-one association to Page
	@ManyToOne
	@JoinColumn(name="page_id")
	private Page page;

	//bi-directional many-to-one association to Session
	@ManyToOne
	@JoinColumn(name="session_id")
	private Session session;

	//bi-directional many-to-one association to Template
	@ManyToOne
	@JoinColumn(name="template_id")
	private Template template;

	public ContentAccess() {
	}

	public String getCakId() {
		return this.cakId;
	}

	public void setCakId(String cakId) {
		this.cakId = cakId;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public BigInteger getEnUserId() {
		return this.enUserId;
	}

	public void setEnUserId(BigInteger enUserId) {
		this.enUserId = enUserId;
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

	public String getOtherData() {
		return this.otherData;
	}

	public void setOtherData(String otherData) {
		this.otherData = otherData;
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

	public Template getTemplate() {
		return this.template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

}
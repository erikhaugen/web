package org.kfm.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the sync_config database table.
 * 
 */
@Entity
@Table(name="sync_config")
@NamedQuery(name="SyncConfig.findAll", query="SELECT s FROM SyncConfig s")
public class SyncConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="sync_config_id")
	private String syncConfigId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_modified")
	private Date lastModified;

	@Column(name="last_modified_by")
	private String lastModifiedBy;

	@Column(name="lsp_name")
	private String lspName;

	@Column(name="lsp_params")
	private String lspParams;

	@Column(name="lsp_url")
	private String lspUrl;

	//bi-directional many-to-one association to Document
	@OneToMany(mappedBy="syncConfig")
	private List<Document> documents;

	public SyncConfig() {
	}

	public String getSyncConfigId() {
		return this.syncConfigId;
	}

	public void setSyncConfigId(String syncConfigId) {
		this.syncConfigId = syncConfigId;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
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

	public String getLspName() {
		return this.lspName;
	}

	public void setLspName(String lspName) {
		this.lspName = lspName;
	}

	public String getLspParams() {
		return this.lspParams;
	}

	public void setLspParams(String lspParams) {
		this.lspParams = lspParams;
	}

	public String getLspUrl() {
		return this.lspUrl;
	}

	public void setLspUrl(String lspUrl) {
		this.lspUrl = lspUrl;
	}

	public List<Document> getDocuments() {
		return this.documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public Document addDocument(Document document) {
		getDocuments().add(document);
		document.setSyncConfig(this);

		return document;
	}

	public Document removeDocument(Document document) {
		getDocuments().remove(document);
		document.setSyncConfig(null);

		return document;
	}

}
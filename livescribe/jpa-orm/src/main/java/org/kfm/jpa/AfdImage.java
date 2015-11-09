package org.kfm.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the afd_image database table.
 * 
 */
@Entity
@Table(name="afd_image")
@NamedQuery(name="AfdImage.findAll", query="SELECT a FROM AfdImage a")
public class AfdImage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="image_id")
	private String imageId;

	@Column(name="afd_guid")
	private String afdGuid;

	@Column(name="afd_version")
	private String afdVersion;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Lob
	private byte[] data;

	private double height;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_modified")
	private Date lastModified;

	@Column(name="last_modified_by")
	private String lastModifiedBy;

	@Column(name="mime_type")
	private String mimeType;

	@Column(name="template_index")
	private BigInteger templateIndex;

	private double width;

	//bi-directional many-to-one association to Page
	@OneToMany(mappedBy="afdImage")
	private List<Page> pages;

	public AfdImage() {
	}

	public String getImageId() {
		return this.imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getAfdGuid() {
		return this.afdGuid;
	}

	public void setAfdGuid(String afdGuid) {
		this.afdGuid = afdGuid;
	}

	public String getAfdVersion() {
		return this.afdVersion;
	}

	public void setAfdVersion(String afdVersion) {
		this.afdVersion = afdVersion;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public byte[] getData() {
		return this.data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public double getHeight() {
		return this.height;
	}

	public void setHeight(double height) {
		this.height = height;
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

	public String getMimeType() {
		return this.mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public BigInteger getTemplateIndex() {
		return this.templateIndex;
	}

	public void setTemplateIndex(BigInteger templateIndex) {
		this.templateIndex = templateIndex;
	}

	public double getWidth() {
		return this.width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public List<Page> getPages() {
		return this.pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	public Page addPage(Page page) {
		getPages().add(page);
		page.setAfdImage(this);

		return page;
	}

	public Page removePage(Page page) {
		getPages().remove(page);
		page.setAfdImage(null);

		return page;
	}

}
package org.kfm.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the template database table.
 * 
 */
@Entity
@NamedQuery(name="Template.findAll", query="SELECT t FROM Template t")
public class Template implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="template_id")
	private String templateId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private double height;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_modified")
	private Date lastModified;

	@Column(name="last_modified_by")
	private String lastModifiedBy;

	@Column(name="template_index")
	private int templateIndex;

	private double width;

	private double x;

	private double y;

	//bi-directional many-to-one association to ContentAccess
	@OneToMany(mappedBy="template")
	private List<ContentAccess> contentAccesses;

	//bi-directional many-to-one association to Image
	@OneToMany(mappedBy="template")
	private List<Image> images;

	//bi-directional many-to-one association to Page
	@OneToMany(mappedBy="template")
	private List<Page> pages;

	//bi-directional many-to-one association to Document
	@ManyToOne
	@JoinColumn(name="document_id")
	private Document document;

	public Template() {
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
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

	public int getTemplateIndex() {
		return this.templateIndex;
	}

	public void setTemplateIndex(int templateIndex) {
		this.templateIndex = templateIndex;
	}

	public double getWidth() {
		return this.width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public List<ContentAccess> getContentAccesses() {
		return this.contentAccesses;
	}

	public void setContentAccesses(List<ContentAccess> contentAccesses) {
		this.contentAccesses = contentAccesses;
	}

	public ContentAccess addContentAccess(ContentAccess contentAccess) {
		getContentAccesses().add(contentAccess);
		contentAccess.setTemplate(this);

		return contentAccess;
	}

	public ContentAccess removeContentAccess(ContentAccess contentAccess) {
		getContentAccesses().remove(contentAccess);
		contentAccess.setTemplate(null);

		return contentAccess;
	}

	public List<Image> getImages() {
		return this.images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public Image addImage(Image image) {
		getImages().add(image);
		image.setTemplate(this);

		return image;
	}

	public Image removeImage(Image image) {
		getImages().remove(image);
		image.setTemplate(null);

		return image;
	}

	public List<Page> getPages() {
		return this.pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	public Page addPage(Page page) {
		getPages().add(page);
		page.setTemplate(this);

		return page;
	}

	public Page removePage(Page page) {
		getPages().remove(page);
		page.setTemplate(null);

		return page;
	}

	public Document getDocument() {
		return this.document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

}
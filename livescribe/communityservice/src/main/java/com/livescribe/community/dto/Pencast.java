/**
 * 
 */
package com.livescribe.community.dto;

import java.util.Date;

import org.dom4j.Document;

/**
 * <p>Represents pencast information for viewing.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class Pencast {
	
	private String categoryName;
	private String description;
	private String displayName;
	private Date fileDate;
	private Integer numberOfViews;
	private byte[] primaryKey;
//	private String publishDate;
	private Double rating;
	private String shortId;
	private String derivativePath;
	private Document flashXmlDom;
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public Pencast() {
		
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getDisplayName() {
		return displayName;
	}

	/**
	 * <p>Returns the absolute path on the filesystem to the 
	 * location of the pencasts files.</p>
	 * 
	 * @return the absolute path to the location of the pencasts files.
	 */
	public String getDerivativePath() {
		return derivativePath;
	}

	public String getDescription() {
		return description;
	}

	public Date getFileDate() {
		return fileDate;
	}

	public Document getFlashXmlDom() {
		return flashXmlDom;
	}

	public Integer getNumberOfViews() {
		return numberOfViews;
	}

	public byte[] getPrimaryKey() {
		return primaryKey;
	}

//	public String getPublishDate() {
//		return publishDate;
//	}

	public Double getRating() {
		return rating;
	}

	public String getShortId() {
		return shortId;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setDerivativePath(String derivativePath) {
		this.derivativePath = derivativePath;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}

	public void setFlashXmlDom(Document flashXmlDom) {
		this.flashXmlDom = flashXmlDom;
	}

	public void setNumberOfViews(Integer numberOfViews) {
		this.numberOfViews = numberOfViews;
	}

	public void setPrimaryKey(byte[] primaryKey) {
		this.primaryKey = primaryKey;
	}

//	public void setPublishDate(String publishDate) {
//		this.publishDate = publishDate;
//	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public void setShortId(String shortId) {
		this.shortId = shortId;
	}
}

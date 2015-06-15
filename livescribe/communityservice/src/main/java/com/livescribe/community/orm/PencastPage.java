/**
 * 
 */
package com.livescribe.community.orm;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PencastPage {

	private Integer pageId;
	private String label;
	private Integer width;
	private Integer height;
	private String background;
	private String format;
	private Long fileSize;
	private String filePath;
	private String fileUrl;
	private boolean exists;
	
	/**
	 * 
	 */
	public PencastPage() {
		
	}
	
	public PencastPage(PencastPage source) {
		this.pageId = source.pageId;
		this.label = source.label;
		this.width = source.width;
		this.height = source.height;
		this.background = source.background;
		this.format = source.format;
		this.fileSize = source.fileSize;
		this.filePath = source.filePath;
		this.fileUrl = source.fileUrl;
		this.exists = source.exists;
	}

	/**
	 * @return the exists
	 */
	public boolean exists() {
		return exists;
	}
	
	/**
	 * @return the background
	 */
	public String getBackground() {
		return background;
	}
	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}
	/**
	 * @return the fileSize
	 */
	public Long getFileSize() {
		return fileSize;
	}
	/**
	 * @return the fileUrl
	 */
	public String getFileUrl() {
		return fileUrl;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * @return the height
	 */
	public Integer getHeight() {
		return height;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @return the pageId
	 */
	public Integer getPageId() {
		return pageId;
	}

	/**
	 * @return the width
	 */
	public Integer getWidth() {
		return width;
	}
	/**
	 * @param background the background to set
	 */
	public void setBackground(String background) {
		this.background = background;
	}

	/**
	 * @param exists the exists to set
	 */
	public void setExists(boolean exists) {
		this.exists = exists;
	}
	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	/**
	 * @param fileUrl the fileUrl to set
	 */
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(Integer height) {
		this.height = height;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @param pageId the pageId to set
	 */
	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(Integer width) {
		this.width = width;
	}
}

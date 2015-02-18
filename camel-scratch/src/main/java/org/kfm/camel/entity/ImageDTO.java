/**
 * Created:  Mar 12, 2014 4:24:37 PM
 */
package org.kfm.camel.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ImageDTO {

	@XStreamAlias("imageid")
	private long imageId = 0;

	@XStreamAlias("templateid")
	private long templateId = 0;
	
	@XStreamAlias("templateIndex")
	private long templateIndex = -1;
	
	@XStreamAlias("x")
	private double x = 0;
	
	@XStreamAlias("y")
	private double y = 0;
	
	@XStreamAlias("z")
	private double z = 0;
	
	@XStreamAlias("width")
	private double width = 0;
	
	@XStreamAlias("height")
	private double height = 0;

	@XStreamAlias("json-image-file")
    public ImageFileDTO file = new ImageFileDTO();

	/**
	 * <p></p>
	 * 
	 */
	public ImageDTO() {
	}

	/**
	 * <p></p>
	 * 
	 * @return the imageId
	 */
	public long getImageId() {
		return imageId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the templateId
	 */
	public long getTemplateId() {
		return templateId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the templateIndex
	 */
	public long getTemplateIndex() {
		return templateIndex;
	}

	/**
	 * <p></p>
	 * 
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * <p></p>
	 * 
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * <p></p>
	 * 
	 * @return the z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * <p></p>
	 * 
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * <p></p>
	 * 
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * <p></p>
	 * 
	 * @return the file
	 */
	public ImageFileDTO getFile() {
		return file;
	}

	/**
	 * <p></p>
	 * 
	 * @param imageId the imageId to set
	 */
	public void setImageId(long imageId) {
		this.imageId = imageId;
	}

	/**
	 * <p></p>
	 * 
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	/**
	 * <p></p>
	 * 
	 * @param templateIndex the templateIndex to set
	 */
	public void setTemplateIndex(long templateIndex) {
		this.templateIndex = templateIndex;
	}

	/**
	 * <p></p>
	 * 
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * <p></p>
	 * 
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * <p></p>
	 * 
	 * @param z the z to set
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * <p></p>
	 * 
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * <p></p>
	 * 
	 * @param height the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * <p></p>
	 * 
	 * @param file the file to set
	 */
	public void setFile(ImageFileDTO file) {
		this.file = file;
	}

}

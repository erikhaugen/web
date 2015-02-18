/**
 * Created:  Mar 12, 2014 4:14:22 PM
 */
package org.kfm.camel.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("json-template")
public class TemplateDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3887287279540700153L;

	@XStreamAlias("id")
    private long id = 0;
	
	@XStreamAlias("documentid")
	private long documentId = 0;
	
	@XStreamAlias("index")
	private int index = 0;
	
	@XStreamAlias("x")
	private double x = 0;
	
	@XStreamAlias("y")
	private double y = 0;
	
	@XStreamAlias("width")
	private double width = 0;
	
	@XStreamAlias("height")
	private double height = 0;
    
    @XStreamAlias("json-image")
    @XStreamImplicit
    public List<ImageDTO> images = new ArrayList<ImageDTO>();
    
	/**
	 * <p></p>
	 * 
	 */
	public TemplateDTO() {
	}

	/**
	 * <p></p>
	 * 
	 * @param imageDto
	 */
	public void add(ImageDTO imageDto) {
		this.images.add(imageDto);
	}
	
	/**
	 * <p></p>
	 * 
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * <p></p>
	 * 
	 * @return the documentId
	 */
	public long getDocumentId() {
		return documentId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
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
	 * @return the images
	 */
	public List<ImageDTO> getImages() {
		return images;
	}

	/**
	 * <p></p>
	 * 
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * <p></p>
	 * 
	 * @param documentId the documentId to set
	 */
	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	/**
	 * <p></p>
	 * 
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
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
	 * @param images the images to set
	 */
	public void setImages(List<ImageDTO> images) {
		this.images = images;
	}

}

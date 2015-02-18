package org.kfm.camel.entity.evernote;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("coord")
public class XMLCoordinate implements Serializable {
	
	/**
	 * 
	 */
	@XStreamOmitField
	private static final long serialVersionUID = 2408185691137076329L;

	@XStreamAlias("x")
    @XStreamAsAttribute
    public float x = 0;
	
	@XStreamAlias("y")
    @XStreamAsAttribute
    public float y = 0;
	
	@XStreamAlias("d")
    @XStreamAsAttribute
    public float d = 0;

	public XMLCoordinate() {
		
	}

	/**
	 * <p></p>
	 * 
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * <p></p>
	 * 
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * <p></p>
	 * 
	 * @return the d
	 */
	public float getD() {
		return d;
	}

	/**
	 * <p></p>
	 * 
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * <p></p>
	 * 
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * <p></p>
	 * 
	 * @param d the d to set
	 */
	public void setD(float d) {
		this.d = d;
	}
	
//	/* (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		builder.append("XMLCoordinate <x=");
//		builder.append(x);
//		builder.append(", y=");
//		builder.append(y);
//		builder.append(", d=");
//		builder.append(d);
//		builder.append(">");
//		return builder.toString();
//	}
//
}

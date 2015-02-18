package org.kfm.camel.entity.evernote;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("stroke")
public class XMLStroke implements Serializable {
	
	/**
	 * 
	 */
	@XStreamOmitField
	private static final long serialVersionUID = 7483081750543600260L;

	@XStreamAlias("timestamp")
    @XStreamAsAttribute
    public BigInteger timestamp = new BigInteger("0");

    @XStreamImplicit
    public List<XMLCoordinate> coords = new ArrayList<XMLCoordinate>();
    
    public XMLStroke() {
    	
    }

	/**
	 * <p></p>
	 * 
	 * @return the timestamp
	 */
	public BigInteger getTimestamp() {
		return timestamp;
	}

	/**
	 * <p></p>
	 * 
	 * @return the coords
	 */
	public List<XMLCoordinate> getCoords() {
		return coords;
	}

	/**
	 * <p></p>
	 * 
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(BigInteger timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * <p></p>
	 * 
	 * @param coords the coords to set
	 */
	public void setCoords(List<XMLCoordinate> coords) {
		this.coords = coords;
	}
    
    
//	/* (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		builder.append("XMLStroke <timestamp=");
//		builder.append(timestamp);
//		builder.append(", coords=");
//		builder.append(coords);
//		builder.append(">");
//		return builder.toString();
//	}
//    
}

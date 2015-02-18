package org.kfm.camel.entity.evernote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("strokes")
public class XMLStrokes implements Serializable {
	
    /**
	 * 
	 */
	@XStreamOmitField
	private static final long serialVersionUID = -8587141961096142831L;
	
	@XStreamImplicit
    public List<XMLStroke> list = new ArrayList<XMLStroke>();

	public XMLStrokes() {
		
	}
	
	/**
	 * <p></p>
	 * 
	 * @return the list
	 */
	public List<XMLStroke> getList() {
		return list;
	}

	/**
	 * <p></p>
	 * 
	 * @param list the list to set
	 */
	public void setList(List<XMLStroke> list) {
		this.list = list;
	}
    
}

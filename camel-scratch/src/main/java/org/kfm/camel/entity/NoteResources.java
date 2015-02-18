/**
 * Created:  Dec 22, 2014 2:14:27 PM
 */
package org.kfm.camel.entity;

import org.apache.log4j.Logger;

import com.evernote.edam.type.Resource;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class NoteResources {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private Resource strokeResource;
	private Resource imageResource;
	
	/**
	 * <p></p>
	 * 
	 */
	public NoteResources() {
	}

	/**
	 * <p></p>
	 * 
	 * @return the strokeResource
	 */
	public Resource getStrokeResource() {
		return strokeResource;
	}

	/**
	 * <p></p>
	 * 
	 * @return the imageResource
	 */
	public Resource getImageResource() {
		return imageResource;
	}

	/**
	 * <p></p>
	 * 
	 * @param strokeResource the strokeResource to set
	 */
	public void setStrokeResource(Resource strokeResource) {
		this.strokeResource = strokeResource;
	}

	/**
	 * <p></p>
	 * 
	 * @param imageResource the imageResource to set
	 */
	public void setImageResource(Resource imageResource) {
		this.imageResource = imageResource;
	}

}

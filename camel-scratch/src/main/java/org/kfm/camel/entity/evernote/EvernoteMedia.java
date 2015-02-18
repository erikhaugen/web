/**
 * Created:  Dec 23, 2014 5:55:47 PM
 */
package org.kfm.camel.entity.evernote;

import org.apache.log4j.Logger;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EvernoteMedia {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private String hash;
	private String type;

	/**
	 * <p></p>
	 * 
	 */
	public EvernoteMedia(String hash, String type) {
		this.hash = hash;
		this.type = type;
	}

	/**
	 * <p></p>
	 * 
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * <p></p>
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * <p></p>
	 * 
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * <p></p>
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}

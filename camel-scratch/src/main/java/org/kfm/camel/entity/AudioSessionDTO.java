/**
 * Created:  Mar 12, 2014 5:15:34 PM
 */
package org.kfm.camel.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("json-audio-session")
public class AudioSessionDTO extends SessionDTO {

	@XStreamAlias("pageid")
	private long pageId = 0;

	/**
	 * <p></p>
	 * 
	 */
	public AudioSessionDTO() {
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return ((this.startTime == ((AudioSessionDTO)obj).startTime) 
				&& (this.endTime == ((AudioSessionDTO)obj).endTime) 
				&& (this.pageId == ((AudioSessionDTO)obj).pageId));
	}

	/**
	 * <p></p>
	 * 
	 * @return the pageId
	 */
	public long getPageId() {
		return pageId;
	}

	/**
	 * <p></p>
	 * 
	 * @param pageId the pageId to set
	 */
	public void setPageId(long pageId) {
		this.pageId = pageId;
	}
}

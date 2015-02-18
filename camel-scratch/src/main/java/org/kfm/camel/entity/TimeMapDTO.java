/**
 * Created:  Mar 12, 2014 4:25:03 PM
 */
package org.kfm.camel.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("json-timemap")
public class TimeMapDTO {

	@XStreamAlias("docguid")
	private String docGuid = "";
	
	@XStreamAlias("doccopy")
	private long docCopy = Long.MIN_VALUE;
	
	@XStreamAlias("pageindex")
	private int pageIndex = 0;
	
	@XStreamAlias("starttime")
	private long startTime = 0;
	
	@XStreamAlias("endtime")
	private long endTime = 0;
	
	@XStreamAlias("maptime")
	private long mapTime = 0;
	
	@XStreamAlias("pageid")
	private long pageId = 0;
	
	@XStreamAlias("sessionid")
	private long sessionId = 0;

	/**
	 * <p></p>
	 * 
	 */
	public TimeMapDTO() {
	}

	/**
	 * <p></p>
	 * 
	 * @return the docGuid
	 */
	public String getDocGuid() {
		return docGuid;
	}

	/**
	 * <p></p>
	 * 
	 * @return the docCopy
	 */
	public long getDocCopy() {
		return docCopy;
	}

	/**
	 * <p></p>
	 * 
	 * @return the pageIndex
	 */
	public int getPageIndex() {
		return pageIndex;
	}

	/**
	 * <p></p>
	 * 
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * <p></p>
	 * 
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * <p></p>
	 * 
	 * @return the mapTime
	 */
	public long getMapTime() {
		return mapTime;
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
	 * @return the sessionId
	 */
	public long getSessionId() {
		return sessionId;
	}

	/**
	 * <p></p>
	 * 
	 * @param docGuid the docGuid to set
	 */
	public void setDocGuid(String docGuid) {
		this.docGuid = docGuid;
	}

	/**
	 * <p></p>
	 * 
	 * @param docCopy the docCopy to set
	 */
	public void setDocCopy(long docCopy) {
		this.docCopy = docCopy;
	}

	/**
	 * <p></p>
	 * 
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	/**
	 * <p></p>
	 * 
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * <p></p>
	 * 
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * <p></p>
	 * 
	 * @param mapTime the mapTime to set
	 */
	public void setMapTime(long mapTime) {
		this.mapTime = mapTime;
	}

	/**
	 * <p></p>
	 * 
	 * @param pageId the pageId to set
	 */
	public void setPageId(long pageId) {
		this.pageId = pageId;
	}

	/**
	 * <p></p>
	 * 
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

}

/**
 * Created:  Nov 23, 2014 4:16:13 PM
 */
package org.kfm.camel.entity;

import org.apache.log4j.Logger;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public abstract class TimeBasedDTO {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private long startTime;
	private long endTime;
	
	/**
	 * <p></p>
	 * 
	 */
	public TimeBasedDTO() {
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

}

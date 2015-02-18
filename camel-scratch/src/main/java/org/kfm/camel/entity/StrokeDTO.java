/**
 * Created:  Mar 12, 2014 4:25:19 PM
 */
package org.kfm.camel.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("json-stroke")
public class StrokeDTO {

	@XStreamAlias("docguid")
	private String docGuid = "";
	
	@XStreamAlias("doccopy")
	private long docCopy = Long.MIN_VALUE;
	
	@XStreamAlias("pageindex")
	private int pageIndex = 0;
	
	@XStreamAlias("json-stroke-file")
	private StrokeFileDTO file = new StrokeFileDTO();	

	/**
	 * <p></p>
	 * 
	 */
	public StrokeDTO() {
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
	 * @return the file
	 */
	public StrokeFileDTO getFile() {
		return file;
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
	 * @param file the file to set
	 */
	public void setFile(StrokeFileDTO file) {
		this.file = file;
	}

}

/**
 * Created:  Mar 12, 2014 5:09:43 PM
 */
package org.kfm.camel.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class FileDTO {

	@XStreamAlias("mimetype")
	protected String mimeType = "";
	
	@XStreamAlias("data")
	protected byte[] data = null;
	
	@XStreamAlias("fileName")
	protected String fileName = "";
	
	@XStreamAlias("fileSize")
	protected long fileSize = 0;
	
	@XStreamAlias("snippetNumber")
	protected int snippetNumber = 0;

	/**
	 * <p></p>
	 * 
	 */
	public FileDTO() {
	}

	/**
	 * <p></p>
	 * 
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * <p></p>
	 * 
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * <p></p>
	 * 
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * <p></p>
	 * 
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * <p></p>
	 * 
	 * @return the snippetNumber
	 */
	public int getSnippetNumber() {
		return snippetNumber;
	}

	/**
	 * <p></p>
	 * 
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * <p></p>
	 * 
	 * @param data the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * <p></p>
	 * 
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * <p></p>
	 * 
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * <p></p>
	 * 
	 * @param snippetNumber the snippetNumber to set
	 */
	public void setSnippetNumber(int snippetNumber) {
		this.snippetNumber = snippetNumber;
	}

}

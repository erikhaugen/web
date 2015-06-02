/**
 * Created: May 21, 2015 3:27:06 PM
 *      by: kmurdoff
 */
package biz.neustar.webmetrics.plugins.neustar_s3_maven_plugin;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
public class UploadFile {

	/** The file path to the source file. */
	@Parameter(property = "s3-upload.files.file.source")
	private String source;

	/** The file path to the target location. */
	@Parameter(property = "s3-upload.files.file.target")
	private String target;
	
	/**
	 * 
	 */
	public UploadFile() {
		
	}

	/**
	 * @return
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @param target
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("source = ").append(source).append("\n");
		sb.append("target = ").append(target).append("\n");
		return sb.toString();
	}
}

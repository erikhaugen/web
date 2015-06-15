/**
 * Created:  Dec 13, 2010 11:25:12 PM
 */
package com.livescribe.community.view.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class VersionResponse {

	private String version;
	private String buildDate;
	private String svnRevision;
	private String hudsonBuildNumber;
	
	/**
	 * <p></p>
	 * 
	 */
	public VersionResponse() {
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBuildDate() {
		return buildDate;
	}

	public String getSvnRevision() {
		return svnRevision;
	}

	public String getHudsonBuildNumber() {
		return hudsonBuildNumber;
	}

	public void setBuildDate(String buildDate) {
		this.buildDate = buildDate;
	}

	public void setSvnRevision(String svnRevision) {
		this.svnRevision = svnRevision;
	}

	public void setHudsonBuildNumber(String hudsonBuildNumber) {
		this.hudsonBuildNumber = hudsonBuildNumber;
	}

}

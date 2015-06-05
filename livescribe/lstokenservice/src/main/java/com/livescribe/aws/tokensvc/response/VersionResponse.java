/**
 * Created:  Dec 13, 2010 11:25:12 PM
 */
package com.livescribe.aws.tokensvc.response;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class VersionResponse {	//	extends ServiceResponse {
	
	private String jarName;
	private String version;
	private String buildDate;
	private String svnRevision;
	private String hudsonBuildNumber;
	
	public VersionResponse() {
		
	}
	
	public String getBuildDate() {
		return buildDate;
	}

	public String getHudsonBuildNumber() {
		return hudsonBuildNumber;
	}

	/**
	 * @return the jarName
	 */
	public String getJarName() {
		return jarName;
	}

	public String getSvnRevision() {
		return svnRevision;
	}

	public String getVersion() {
		return version;
	}

	public void setBuildDate(String buildDate) {
		this.buildDate = buildDate;
	}

	public void setHudsonBuildNumber(String hudsonBuildNumber) {
		this.hudsonBuildNumber = hudsonBuildNumber;
	}

	/**
	 * @param jarName the jarName to set
	 */
	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	public void setSvnRevision(String svnRevision) {
		this.svnRevision = svnRevision;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}

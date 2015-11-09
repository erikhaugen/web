/**
 * Created:  Dec 20, 2010 11:15:41 AM
 */
package com.livescribe.web.utils.spring.mvc;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class VersionResponse {

	private String name;
	private String version;
	private String buildDate;
	private String svnRevision;
	private String hudsonBuildNumber;
	private ArrayList<VersionResponse> dependencies = new ArrayList<VersionResponse>();
	
	/**
	 * <p></p>
	 * 
	 */
	public VersionResponse() {
	}

	public void addDependency(VersionResponse resp) {
		this.dependencies.add(resp);
	}
	
	public String getBuildDate() {
		return buildDate;
	}

	public ArrayList<VersionResponse> getDependencies() {
		return dependencies;
	}

	public String getHudsonBuildNumber() {
		return hudsonBuildNumber;
	}

	public String getName() {
		return name;
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

	public void setDependencies(ArrayList<VersionResponse> dependencies) {
		this.dependencies = dependencies;
	}

	public void setHudsonBuildNumber(String hudsonBuildNumber) {
		this.hudsonBuildNumber = hudsonBuildNumber;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSvnRevision(String svnRevision) {
		this.svnRevision = svnRevision;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}

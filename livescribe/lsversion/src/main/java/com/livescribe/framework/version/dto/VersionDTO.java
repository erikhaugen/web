/**
 * 
 */
package com.livescribe.framework.version.dto;

import java.util.jar.Attributes;

import com.livescribe.framework.version.config.VersionConstants;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class VersionDTO implements VersionConstants {

	private String appName;
	private String version;
	private String buildDate;
	private String scmRevision;
	private String scmTag;
	private String buildNumber;
	private String appServer;
	private String deployEnv;
	
	/**
	 * <p></p>
	 *
	 */
	public VersionDTO() {
		
	}

	/**
	 * <p>Constructs a new <code>VersionDTO</code> object using the given
	 * attributes taken from the <code>MANIFEST.MF</code> file.</p>
	 * 
	 * @param attr
	 */
	public VersionDTO(Attributes attr) {
		
		appName = String.valueOf(attr.getValue(APP_NAME));
		version = String.valueOf(attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION));
		buildDate = String.valueOf(attr.getValue(BUILD_DATE));
		scmRevision = String.valueOf(attr.getValue(SVN_REVISION));
		scmTag = String.valueOf(attr.getValue(SVN_TAG));
		buildNumber = String.valueOf(attr.getValue(HUDSON_BUILD_NUMBER));
	}
	
	/**
	 * <p></p>
	 *
	 * @param appName
	 * @param version
	 * @param buildDate
	 * @param svnRevision
	 * @param hudsonBuildNumber
	 */
	public VersionDTO(String appName, String version, String buildDate, String svnRevision, String hudsonBuildNumber, String svnTag) {
		
		this.appName = appName;
		this.version = version;
		this.buildDate = buildDate;
		this.scmRevision = svnRevision;
		this.scmTag = svnTag;
		this.buildNumber = hudsonBuildNumber;
	}
	
	/**
	 * @return the jarName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the buildDate
	 */
	public String getBuildDate() {
		return buildDate;
	}

	/**
	 * @return the svnRevision
	 */
	public String getScmRevision() {
		return scmRevision;
	}

	/**
	 * @return the hudsonBuildNumber
	 */
	public String getBuildNumber() {
		return buildNumber;
	}

	/**
	 * @param appName the jarName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @param buildDate the buildDate to set
	 */
	public void setBuildDate(String buildDate) {
		this.buildDate = buildDate;
	}

	/**
	 * @param svnRevision the svnRevision to set
	 */
	public void setScmRevision(String scmRevision) {
		this.scmRevision = scmRevision;
	}

	/**
	 * @param hudsonBuildNumber the hudsonBuildNumber to set
	 */
	public void setBuildNumber(String hudsonBuildNumber) {
		this.buildNumber = hudsonBuildNumber;
	}

	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("\n--------------------------------------------------\n");
		builder.append("           JAR Name:  " + this.appName + "\n");
		builder.append("            Version:  " + this.version + "\n");
		builder.append("         Build Date:  " + this.buildDate + "\n");
		builder.append("       SCM Revision:  " + this.scmRevision + "\n");
		builder.append("       Build Number:  " + this.buildNumber + "\n");
		builder.append("--------------------------------------------------\n");
		
		return builder.toString();
	}

	/**
	 * @return the scmTag
	 */
	public String getScmTag() {
		return scmTag;
	}

	/**
	 * @param scmTag the scmTag to set
	 */
	public void setScmTag(String scmTag) {
		this.scmTag = scmTag;
	}

	/**
	 * <p></p>
	 * 
	 * @return the appServer
	 */
	public String getAppServer() {
		return appServer;
	}

	/**
	 * <p></p>
	 * 
	 * @return the deployEnv
	 */
	public String getDeployEnv() {
		return deployEnv;
	}

	/**
	 * <p></p>
	 * 
	 * @param appServer the appServer to set
	 */
	public void setAppServer(String appServer) {
		this.appServer = appServer;
	}

	/**
	 * <p></p>
	 * 
	 * @param deployEnv the deployEnv to set
	 */
	public void setDeployEnv(String deployEnv) {
		this.deployEnv = deployEnv;
	}
}

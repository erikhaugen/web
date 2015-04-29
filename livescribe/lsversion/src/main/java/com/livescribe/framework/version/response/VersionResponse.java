/**
 * Created:  Dec 13, 2010 11:25:12 PM
 */
package com.livescribe.framework.version.response;

import com.livescribe.framework.version.dto.VersionDTO;
import com.livescribe.framework.web.response.ServiceResponse;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class VersionResponse extends ServiceResponse {
	
	private String appName;
	private String version;
	private String buildDate;
	private String svnRevision;
	private String buildNumber;
	private String hostname;
	private String port;
	private String jvmRoute;
	
    public VersionResponse() {
		super();
	}
	
	public VersionResponse(VersionDTO versionDTO) {
		super();
		this.appName = versionDTO.getAppName();
		this.version = versionDTO.getVersion();
		this.buildDate = versionDTO.getBuildDate();
		this.svnRevision = versionDTO.getScmRevision();
		this.buildNumber = versionDTO.getBuildNumber();
	}
	
	public VersionResponse(VersionDTO versionDTO, String hostname, int port, String jvmRoute) {
	    this(versionDTO);
	    this.hostname = hostname;
	    this.port = String.valueOf(port);
	    this.jvmRoute = jvmRoute;
	}
	
//	/**
//	 * <p></p>
//	 * 
//	 * @param responseCode 
//	 */
//	public VersionResponse(ResponseCode responseCode) {
//		super(responseCode);
//		logger.debug("VersionResponse construction complete.");
//	}

	public String getBuildDate() {
		return buildDate;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	/**
	 * @return the jarName
	 */
	public String getJarName() {
		return appName;
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

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	/**
	 * @param appName the jarName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setScmRevision(String scmRevision) {
		this.svnRevision = scmRevision;
	}

	public void setVersion(String version) {
		this.version = version;
	}

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getJvmRoute() {
        return jvmRoute;
    }

    public void setJvmRoute(String jvmRoute) {
        this.jvmRoute = jvmRoute;
    }
}

/**
 * Created:  Apr 10, 2014 5:09:55 PM
 */
package com.livescribe.framework.version.service;

import java.util.Date;

import com.livescribe.framework.orm.versions.Version;
import com.livescribe.framework.version.dto.VersionDTO;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class VersionFactory {

	/**
	 * <p></p>
	 * 
	 * @param versionDto
	 * 
	 * @return
	 */
	public static Version create(VersionDTO versionDto) {
		
		Version version = new Version();
		version.setAppName(versionDto.getAppName());
		version.setAppVersion(versionDto.getVersion());
		Date now = new Date();
		version.setCreated(now);
		version.setLastModified(now);
		version.setSvnRevision(versionDto.getScmRevision());
		version.setAppServer(versionDto.getAppServer());
		version.setDeployEnv(versionDto.getDeployEnv());
		
		return version;
	}

}

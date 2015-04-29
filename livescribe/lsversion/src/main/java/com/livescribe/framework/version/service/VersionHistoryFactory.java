/**
 * Created:  Oct 30, 2013 10:20:02 PM
 */
package com.livescribe.framework.version.service;

import org.apache.log4j.Logger;

import com.livescribe.framework.orm.versions.Version;
import com.livescribe.framework.orm.versions.VersionHistory;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class VersionHistoryFactory {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public VersionHistoryFactory() {
	}

	/**
	 * <p></p>
	 * 
	 * @param version
	 * 
	 * @return
	 */
	public static VersionHistory create(Version version) {
		
		VersionHistory versionHistory = new VersionHistory();
		versionHistory.setAppName(version.getAppName());
		versionHistory.setAppVersion(version.getAppVersion());
		versionHistory.setDeployDate(version.getCreated());
		versionHistory.setDeployEnv(version.getDeployEnv());
		
		return versionHistory;
	}
}

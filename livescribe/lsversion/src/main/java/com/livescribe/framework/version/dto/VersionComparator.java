/**
 * Created:  Oct 30, 2013 10:02:06 PM
 */
package com.livescribe.framework.version.dto;

import java.util.Comparator;

import org.apache.log4j.Logger;

import com.livescribe.framework.orm.versions.Version;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class VersionComparator implements Comparator<Version>{

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public VersionComparator() {
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Version version0, Version version1) {
		
		String method = "compare()";
		
		String appVersion0 = version0.getAppVersion();
		String v0 = stripSnapshot(appVersion0);
		String[] va0 = v0.split(".");
		
		String appVersion1 = version1.getAppVersion();
		String v1 = stripSnapshot(appVersion1);
		String[] va1 = v1.split(".");
		
		int len = 0;
		if (va0.length < va1.length) {
			len = va0.length;
		} else if (va0.length > va1.length) {
			len = va1.length;
		} else {
			len = va0.length;
		}
		
		int major0 = Integer.valueOf(va0[0]);
		int major1 = Integer.valueOf(va1[0]);
		
		if (major0 < major1) {
			return -1;
		} else if (major0 > major1) {
			return 1;
		} else {
			
			int minor0 = Integer.valueOf(va0[1]);
			int minor1 = Integer.valueOf(va1[1]);
			
			if (minor0 < minor1) {
				return -1;
			} else if (minor0 > minor1) {
				return 1;
			} else {
				
			}
		}
		
		return 0;
	}

//	private int compareVersionComponents(int[] v1, int[] v2, int i) {
//		
//		if (v1[i] == v2[i]) {
//			compareVersionComponents()
//		}
//	}
	
	private String getMajor(String version) {
		
		int dotIdx = version.indexOf(".");
		String major = version.substring(0, dotIdx);
		return major;
	}
	
	private String getMinor(String version) {
		
		int dotIdx = version.indexOf(".");
		int dotIdx2 = version.indexOf(".", dotIdx);
		String minor = version.substring(dotIdx, dotIdx2);
		return minor;
	}
	
	private String stripSnapshot(String appVersion) {
		
		StringBuilder newVersion = new StringBuilder();
		
		if (appVersion.contains("SNAPSHOT")) {
		
			int snpsht = appVersion.indexOf("-SNAPSHOT");
			String ver = appVersion.substring(0, snpsht);
			newVersion.append(ver);
			
			if (appVersion.contains("_")) {
				int underscore = appVersion.indexOf("_");
				String bn = appVersion.substring(underscore + 1);
				newVersion.append(".").append(bn);
			}
		}
		logger.debug(newVersion.toString());
		return newVersion.toString();
	}
}

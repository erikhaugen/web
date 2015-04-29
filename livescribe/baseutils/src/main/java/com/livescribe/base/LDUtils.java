package com.livescribe.base;

public class LDUtils {

	public static boolean isLDVersionGreaterThan(String desktopVersion, String major, String minor) {
		if ( desktopVersion != null && !"".equals(desktopVersion) ) {
			String majorVersion = "";
			String minorVersion = "";
			int indexOfDot1= desktopVersion.indexOf(".");
			if ( indexOfDot1 > 0 ) {
				majorVersion = desktopVersion.substring(0, indexOfDot1);
				int indexOfDot2 = desktopVersion.indexOf(".", indexOfDot1+1);
				if ( indexOfDot2 > 0 ) {
					minorVersion = desktopVersion.substring(indexOfDot1+1, indexOfDot2);
				} else {
					minorVersion = desktopVersion.substring(indexOfDot1+1);
				}
			}
			return (majorVersion.compareTo(major) >= 0 && minorVersion.compareTo(minor) >= 0);
		}
		return false;
	}

	public static void main (String[] argv) {
		String[] desktopVersions = new String[] {"2.1", "2.1.XX.X", "2.1.0", "2.2", "2.2.XX.X", 
				"2.2.0", "2.3", "2.3.XX.X", "2.3.0", "2.4", "2.4.XX.X", "2.4.0"};
		
		for ( String desktopVersion : desktopVersions ) {
			System.out.println(desktopVersion + " >= 2.3 is " + isLDVersionGreaterThan(desktopVersion, "2", "3"));
		}
	}
}

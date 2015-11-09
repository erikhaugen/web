package com.livescribe.web.locatorservice;

import junit.framework.TestCase;

public abstract class BaseTest extends TestCase {

	static {
		String envVal = System.getProperty("env");
		if ( envVal == null || "".equals(envVal.trim()) ) {
			System.setProperty("env", "test");
		}
		String configPathVal = System.getProperty("configPath");
		if ( configPathVal == null || "".equals(configPathVal.trim()) ) {
			System.setProperty("configPath", "/Livescribe/LSConfiguration");
		}
	}
}

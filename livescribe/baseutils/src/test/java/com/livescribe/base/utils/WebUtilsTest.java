package com.livescribe.base.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.livescribe.base.WebUtils;

import junit.framework.TestCase;


public class WebUtilsTest extends TestCase {
	
	public void test_isMatch() {
		List<String> ips = Arrays.asList(new String[] {"127.0.0.1", "192.168.*","10.2.*","10.1.4.*","10.1.8.*"});
		int index = 0;
		Pattern[] patterns = new Pattern[ips.size()];
		for ( String allowedIP : ips ) {
			patterns[index++] = Pattern.compile(allowedIP);
		}
		
		assertTrue(WebUtils.isMatch(patterns, "127.0.0.1"));
		assertFalse(WebUtils.isMatch(patterns, "127.0.0.10"));
	}
}

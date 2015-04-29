package com.livescribe.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebUtils {

	public static String getHostnameFromInetAddress () throws UnknownHostException {
		String hostname = null;
		InetAddress addr = InetAddress.getLocalHost();
		hostname = addr.getHostName();
		return hostname;
	}

	public static String getHostnameFromCommand() throws InterruptedException, IOException{
		String hostname = null;
		String command = "hostname";
		Process process = Runtime.getRuntime().exec(command);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line=null;
		StringBuilder sb = new StringBuilder();
		while((line=input.readLine()) != null) {
			sb.append(line);
		}
		hostname = sb.toString();
		process.waitFor();
		return hostname;
	}

	public static String getHostname() throws IOException, InterruptedException {
		String hostname = null;
		try {
			hostname = getHostnameFromInetAddress();
		} catch (UnknownHostException unhe) {
			hostname = getHostnameFromCommand();
		}
		return hostname;
	}
	
	private static final Map<String, String> HEADERS = new HashMap<String, String>();
	static {
		//HTML_HEADERS.put("Content-Type", "text/html");
		HEADERS.put("Pragma", "No-cache");
		HEADERS.put("Expires", "0");
		HEADERS.put("Cache-Control", "no-cache");
	}
	
	public static Map<String, String> getStandardHttpHeader(String contentType) {
		if ( StringUtils.isNotBlank(contentType) ) {
			HEADERS.put("Content-Type", contentType);
		}
		return HEADERS;
	}
	
	private static final String IPADDRESS_PATTERN = 
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
 
	private static final Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
	
	public static boolean isIpAddress(String ip) {
		return (StringUtils.isNotBlank(ip) &&
				pattern.matcher(ip).matches() );	  
	}
	
	public static boolean isMatch (Pattern[] patterns, String input) {
		if ( StringUtils.isNotBlank(input) ) {
			for ( Pattern pattern : patterns ) {
				Matcher matcher = pattern.matcher(input);
				if ( matcher.matches() ) {
					return true;
				}
			}
		}
		return false;
	}
}

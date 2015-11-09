package com.livescribe.servicelocator.dao.data;

import java.util.StringTokenizer;

import com.livescribe.base.StringUtils;
import com.livescribe.base.dao.LSAuditableBaseObject;
import com.livescribe.servicelocator.ConfigClient;

public class ServiceLocator extends LSAuditableBaseObject {

	private String domain;
	private String uriSuffix;
	private String name;
	private String hostKey;
	private String protocol = "XML-RPC";
	private boolean active;
	private boolean secure;
	
	public String getDomain() {
		return (StringUtils.isBlank(domain) ? "LIVESCRIBE" : domain);
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getUriSuffix() {
		return uriSuffix;
	}
	
	/**
	 * <p>Sets the URI suffix for this entry.</p>
	 * 
	 * If the given URI does not start with either &quot;/&quot;, 
	 * &quot;http&quot;, or &quot;https&quot;, the URI is prepended with 
	 * &quot;/&quot;.
	 * 
	 * @param uri The URI to set.
	 */
	public void setUriSuffix(String uri) {
		if ( !uri.startsWith("/") && !uri.startsWith("http") && !uri.startsWith("https")) {
			this.uriSuffix = "/" + uri;
		} else {
			this.uriSuffix = uri;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * <p>Returns the <code>hostKey</code> column from the table.</p>
	 * 
	 * @return the <code>hostKey</code> column from the table.  If 
	 * <code>null</code>, returns &quot;WWW&quot;.
	 */
	public String getHostKey() {
		return (StringUtils.isBlank(hostKey) ? "WWW" : hostKey);
	}

	public void setHostKey(String hostKey) {
		this.hostKey = hostKey;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public String getUri() {
		return getUri(this);
	}
	
	public String toString() {
		return print(this, ",");
	}
	
	/**
	 * <p></p>
	 * 
	 * @param locator
	 * 
	 * @return
	 */
	public static String getUri(ServiceLocator locator) {
		String uSuff = locator.getUriSuffix();
		
		// TODO : FUTURE ENHANCMENT - Identify other absolute uri's also
		// For now let us stick with http
		if ( !uSuff.startsWith("http") && !uSuff.startsWith("https") ) {
			StringBuilder sb = new StringBuilder();
			sb.append(locator.isSecure() ? "https://" : "http://");
			String host = ConfigClient.getHost(locator.getDomain(), locator.getHostKey());
			sb.append(host).append(locator.getUriSuffix());
			uSuff = sb.toString();
		}
		return uSuff;
	}
	
	private static final String PRINT_FMT_STR = "%s,%s,%s,%s,%b,%b";

	private static final int TOKEN_COUNT = StringUtils.countOccurances(PRINT_FMT_STR, ",");
	
	public static String print(ServiceLocator locator, String separator) {
		String formatStr = PRINT_FMT_STR;
		if ( separator != null && !separator.equals(",") ) {
			formatStr = PRINT_FMT_STR.replace(",", separator);
		}
		return String.format(formatStr, locator.getName(), locator.getDomain(), 
				locator.getUriSuffix(), locator.getHostKey(), locator.isSecure(), locator.isActive());
	}
	
	public static ServiceLocator parse(String str, String separator) {
		ServiceLocator locator = null;
		
		if ( StringUtils.isBlank(separator) ) {
			separator = ",";
		}
		StringTokenizer tokenizer = new StringTokenizer(str, separator);
		
		if ( tokenizer.countTokens() == TOKEN_COUNT ) {
			locator = new ServiceLocator();
			locator.setName(tokenizer.nextToken());
			locator.setDomain(tokenizer.nextToken());
			locator.setUriSuffix(tokenizer.nextToken());
			locator.setHostKey(tokenizer.nextToken());
			locator.setSecure(Boolean.parseBoolean(tokenizer.nextToken()));
			locator.setActive(Boolean.parseBoolean(tokenizer.nextToken()));
		}
		return locator;
	}
	
	@Override
	public boolean equals(Object object) {
		if ( object instanceof ServiceLocator ) {
			ServiceLocator locator = (ServiceLocator)object;
			return name.equals(locator.name) && domain.equals(locator.domain) &&
			uriSuffix.equals(locator.uriSuffix) && hostKey.equals(locator.hostKey) &&
			active == locator.active && secure == locator.secure;
		}
		return false;
	}
}

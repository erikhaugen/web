/**
 * Created:  Nov 18, 2010 5:18:18 PM
 */
package com.livescribe.framework.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.livescribe.framework.services.ResponseStatus;
import com.livescribe.base.anno.Mappable;
import com.livescribe.base.anno.MappedElement;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Mappable
//@XStreamAlias("response")
public class ServiceResponse {

	@MappedElement(key = "returnCode", value = "returnCode")
	private ResponseStatus status;
	
	@MappedElement(key = "token")
	private String token;
	
	@MappedElement(key = "uniqueID")
	private String uniqueId;

	@MappedElement(key = "userCertificate")
	private String userCertificate;
	
	@MappedElement(key = "communityUrl")
	private String communityUrl;
	
	@MappedElement(key = "returnUrl")
	private String returnUrl;
	
	/**
	 * <p></p>
	 * 
	 */
	public ServiceResponse() {}

	/**
	 * <p></p>
	 * 
	 * @param status
	 */
	public ServiceResponse(ResponseStatus status) {
		this.status = status;
	}
	
	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public Map<String, Object> toMap() {
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("returnCode", status.code());
		map.put("token", token);
		map.put("uniqueID", uniqueId);
		map.put("userCertificate", userCertificate);
		map.put("communityURL", communityUrl);
		map.put("returnUrl", returnUrl);
		
		return map;
	}

	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("\n----------------------------------------\n");
		Map<String, Object> map = toMap();
		Set<String> keys = map.keySet();
		Iterator<String> keyIter = keys.iterator();
		while (keyIter.hasNext()) {
			String key = keyIter.next();
			if (key.equals("returnCode")) {
				Integer value = (Integer)map.get(key);
				builder.append("    " + key + " : " + value.toString() + "\n");
			}
			else {
				String value = (String)map.get(key);
				builder.append("    " + key + " : " + value + "\n");
			}
		}
		builder.append("----------------------------------------\n");
		return builder.toString();
	}
	
	/**
	 * <p></p>
	 * 
	 * @return the status
	 */
	public ResponseStatus getStatus() {
		return status;
	}

	/**
	 * <p></p>
	 * 
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * <p></p>
	 * 
	 * @return the uniqueId
	 */
	public String getUniqueId() {
		return uniqueId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the userCertificate
	 */
	public String getUserCertificate() {
		return userCertificate;
	}

	/**
	 * <p></p>
	 * 
	 * @return the communityUrl
	 */
	public String getCommunityUrl() {
		return communityUrl;
	}

	/**
	 * <p></p>
	 * 
	 * @return the returnUrl
	 */
	public String getReturnUrl() {
		return returnUrl;
	}

	/**
	 * <p></p>
	 * 
	 * @param status the status to set
	 */
	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	/**
	 * <p></p>
	 * 
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * <p></p>
	 * 
	 * @param uniqueId the uniqueId to set
	 */
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	/**
	 * <p></p>
	 * 
	 * @param userCertificate the userCertificate to set
	 */
	public void setUserCertificate(String userCertificate) {
		this.userCertificate = userCertificate;
	}

	/**
	 * <p></p>
	 * 
	 * @param communityUrl the communityUrl to set
	 */
	public void setCommunityUrl(String communityUrl) {
		this.communityUrl = communityUrl;
	}

	/**
	 * <p></p>
	 * 
	 * @param returnUrl the returnUrl to set
	 */
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
}

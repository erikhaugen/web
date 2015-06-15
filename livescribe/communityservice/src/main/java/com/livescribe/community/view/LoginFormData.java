/**
 * Created:  Nov 19, 2010 11:13:19 AM
 */
package com.livescribe.community.view;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class LoginFormData {

	private String clientCulture;
	private String clientGUID;
	private String clientVersion;
	private String clientApp;
	private String email;
	private String password;
	
	/**
	 * <p></p>
	 * 
	 */
	public LoginFormData() {
	}

	/**
	 * <p></p>
	 * 
	 * @return the clientApp
	 */
	public String getClientApp() {
		return clientApp;
	}

	/**
	 * <p></p>
	 * 
	 * @return the clientCulture
	 */
	public String getClientCulture() {
		return clientCulture;
	}

	/**
	 * <p></p>
	 * 
	 * @return the clientGUID
	 */
	public String getClientGUID() {
		return clientGUID;
	}

	/**
	 * <p></p>
	 * 
	 * @return the clientVersion
	 */
	public String getClientVersion() {
		return clientVersion;
	}

	/**
	 * <p></p>
	 * 
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * <p></p>
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * <p></p>
	 * 
	 * @param clientApp the clientApp to set
	 */
	public void setClientApp(String clientApp) {
		this.clientApp = clientApp;
	}

	/**
	 * <p></p>
	 * 
	 * @param clientCulture the clientCulture to set
	 */
	public void setClientCulture(String clientCulture) {
		this.clientCulture = clientCulture;
	}

	/**
	 * <p></p>
	 * 
	 * @param clientGUID the clientGUID to set
	 */
	public void setClientGUID(String clientGUID) {
		this.clientGUID = clientGUID;
	}

	/**
	 * <p></p>
	 * 
	 * @param clientVersion the clientVersion to set
	 */
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	/**
	 * <p></p>
	 * 
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * <p></p>
	 * 
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}

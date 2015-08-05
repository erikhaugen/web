/**
 * Created:  Jun 14, 2013 12:57:39 PM
 */
package com.livescribe.web.tools.webteamtool.cli;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.livescribe.web.tools.webteamtool.DisplayIdValidator;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CommandOptions {

	@Parameter(names = "-d", description = "Pen Display ID", validateWith = DisplayIdValidator.class)
	private String displayId;
	
	@Parameter(names = "-e", description = "Validate OAuth access token by e-mail address.")
	private String emailAddress;
	
	@Parameter(names = "-s", description = "Pen Serial Number")
	private Long serialNumber;
	
	@Parameter(names = "-o", description = "OAuth Access Token")
	private String accessToken;
	
	@Parameter(names = "-p", description = "List system properties")
	private String systemProperties;
	
	@Parameter(names = "-q", description = "User upload quota")
	private String quotaEmailAddress;
	
	@Parameter(names = "-t", description = "Find OAuth Access Token by email address.")
	private String accessTokenEmailAddress;
	
	@Parameter(names = "-w", description = "Hash the given password string (MySQL).")
	private String passwordString;
	
	@Parameter(names = "-f", description = "Hash the given password string used by WebObjects/FrontBase.")
	private String frontBasePasswordString;
	
	@Parameter(names = "-g", description = "Generate classes from the given XML Schema.")
	private String xmlSchema;

	@Parameter(names = "-tp", description = "Package to place generated class files into.")
	private String targetPackage;

	@Parameter(names = "-fbpk", description = "FrontBase primary key string.")
	private String frontBasePrimaryKey;
	
	/**
	 * <p></p>
	 * 
	 */
	public CommandOptions() {
		
	}

	/**
	 * <p></p>
	 * 
	 * @return the displayId
	 */
	public String getDisplayId() {
		return displayId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the serialNumber
	 */
	public Long getSerialNumber() {
		return serialNumber;
	}

	/**
	 * <p></p>
	 * 
	 * @param displayId the displayId to set
	 */
	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	/**
	 * <p></p>
	 * 
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(Long serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * <p></p>
	 * 
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * <p></p>
	 * 
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * <p></p>
	 * 
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * <p></p>
	 * 
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * <p></p>
	 * 
	 * @return the quotaEmailAddress
	 */
	public String getQuotaEmailAddress() {
		return quotaEmailAddress;
	}

	/**
	 * <p></p>
	 * 
	 * @param quotaEmailAddress the quotaEmailAddress to set
	 */
	public void setQuotaEmailAddress(String quotaEmailAddress) {
		this.quotaEmailAddress = quotaEmailAddress;
	}

	/**
	 * <p></p>
	 * 
	 * @return the accessTokenEmailAddress
	 */
	public String getAccessTokenEmailAddress() {
		return accessTokenEmailAddress;
	}

	/**
	 * <p></p>
	 * 
	 * @param accessTokenEmailAddress the accessTokenEmailAddress to set
	 */
	public void setAccessTokenEmailAddress(String accessTokenEmailAddress) {
		this.accessTokenEmailAddress = accessTokenEmailAddress;
	}

	/**
	 * <p></p>
	 * 
	 * @return the systemProperties
	 */
	public String getSystemProperties() {
		return systemProperties;
	}

	/**
	 * <p></p>
	 * 
	 * @param systemProperties the systemProperties to set
	 */
	public void setSystemProperties(String systemProperties) {
		this.systemProperties = systemProperties;
	}

	/**
	 * <p></p>
	 * 
	 * @return the passwordString
	 */
	public String getPasswordString() {
		return passwordString;
	}

	/**
	 * <p></p>
	 * 
	 * @param passwordString the passwordString to set
	 */
	public void setPasswordString(String passwordString) {
		this.passwordString = passwordString;
	}

	/**
	 * <p></p>
	 * 
	 * @return the frontBasePasswordString
	 */
	public String getFrontBasePasswordString() {
		return frontBasePasswordString;
	}

	/**
	 * <p></p>
	 * 
	 * @param frontBasePasswordString the frontBasePasswordString to set
	 */
	public void setFrontBasePasswordString(String frontBasePasswordString) {
		this.frontBasePasswordString = frontBasePasswordString;
	}

	/**
	 * <p></p>
	 * 
	 * @return the xmlSchema
	 */
	public String getXmlSchema() {
		return xmlSchema;
	}

	/**
	 * <p></p>
	 * 
	 * @return the targetPackage
	 */
	public String getTargetPackage() {
		return targetPackage;
	}

	/**
	 * <p></p>
	 * 
	 * @param xmlSchema the xmlSchema to set
	 */
	public void setXmlSchema(String xmlSchema) {
		this.xmlSchema = xmlSchema;
	}

	/**
	 * <p></p>
	 * 
	 * @param targetPackage the targetPackage to set
	 */
	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

	/**
	 * <p></p>
	 * 
	 * @return the frontBasePrimaryKey
	 */
	public String getFrontBasePrimaryKey() {
		return frontBasePrimaryKey;
	}

	/**
	 * <p></p>
	 * 
	 * @param frontBasePrimaryKey the frontBasePrimaryKey to set
	 */
	public void setFrontBasePrimaryKey(String frontBasePrimaryKey) {
		this.frontBasePrimaryKey = frontBasePrimaryKey;
	}

}

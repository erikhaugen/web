/**
 * Created:  Aug 23, 2013 2:49:10 PM
 */
package com.livescribe.web.registration.dto;

import java.util.Date;

import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.web.registration.controller.RegistrationData;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("registration")
public class RegistrationDTO {

	@XStreamAlias("appId")
    private String appId;
	@XStreamAlias("edition")
	private Integer edition;
	@XStreamAlias("penSerial")
    private String penSerial;
	@XStreamAlias("displayId")
    private String displayId;
	@XStreamAlias("penName")
    private String penName;
	@XStreamAlias("firstName")
    private String firstName;
	@XStreamAlias("lastName")
    private String lastName;
	@XStreamAlias("email")
    private String email;
	@XStreamAlias("locale")
    private String locale;
	@XStreamAlias("country")
    private String country;
	@XStreamAlias("optIn")
    private Boolean optIn;
	@XStreamAlias("created")
	private Date created;

    /**
	 * <p></p>
	 * 
	 */
	public RegistrationDTO() {
	}

	/**
	 * <p></p>
	 * 
	 * @param data
	 */
	public RegistrationDTO(RegistrationData data) {
		
		appId = data.getAppId();
		edition = data.getEdition();
		penSerial = data.getPenSerial();
		penName = data.getPenName();
		firstName = data.getFirstName();
		lastName = data.getLastName();
		email = data.getEmail();
		locale = data.getLocale();
		country = data.getCountry();
		optIn = data.getOptIn();
	}

	/**
	 * <p></p>
	 * 
	 * @param data
	 */
	public RegistrationDTO(Registration registration) {
		
		appId = registration.getAppId();
		edition = registration.getEdition();
		penSerial = registration.getPenSerial();
		displayId = registration.getDisplayId();
		penName = registration.getPenName();
		firstName = registration.getFirstName();
		lastName = registration.getLastName();
		email = registration.getEmail();
		locale = registration.getLocale();
		country = registration.getCountry();
		optIn = registration.getOptIn();
		created = registration.getCreated();
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n--------------------------------------------------\n");
		sb.append("        appId:  ").append(appId).append("\n");
		sb.append("    penSerial:  ").append(penSerial).append("\n");
		sb.append("        email:  ").append(email).append("\n");
		sb.append("    firstName:  ").append(firstName).append("\n");
		sb.append("     lastName:  ").append(lastName).append("\n");
		sb.append("      edition:  ").append(edition).append("\n");
		sb.append("      penName:  ").append(penName).append("\n");
		sb.append("       locale:  ").append(locale).append("\n");
		sb.append("      country:  ").append(country).append("\n");
		sb.append("        optIn:  ").append(optIn).append("\n");
		sb.append("--------------------------------------------------\n");

		return sb.toString();
	}
	
	/**
	 * <p></p>
	 * 
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the penSerial
	 */
	public String getPenSerial() {
		return penSerial;
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public String getDisplayId() {
		return displayId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * <p></p>
	 * 
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
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
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * <p></p>
	 * 
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * <p></p>
	 * 
	 * @return the optIn
	 */
	public Boolean getOptIn() {
		return optIn;
	}

	/**
	 * <p></p>
	 * 
	 * @return the edition
	 */
	public Integer getEdition() {
		return edition;
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public String getPenName() {
		return penName;
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public Date getCreated() {
		return created;
	}

	protected void setAppId(String appId) {
		this.appId = appId;
	}

	protected void setEdition(Integer edition) {
		this.edition = edition;
	}

	protected void setPenSerial(String penSerial) {
		this.penSerial = penSerial;
	}

	protected void setDisplayId(String displayId) {
		this.displayId = displayId;
	}
	
	protected void setPenName(String penName) {
		this.penName = penName;
	}

	protected void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	protected void setLastName(String lastName) {
		this.lastName = lastName;
	}

	protected void setEmail(String email) {
		this.email = email;
	}

	protected void setLocale(String locale) {
		this.locale = locale;
	}

	protected void setCountry(String country) {
		this.country = country;
	}

	protected void setOptIn(Boolean optIn) {
		this.optIn = optIn;
	}

	protected void setCreated(Date created) {
		this.created = created;
	}
	
	
}

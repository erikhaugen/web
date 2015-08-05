package com.livescribe.web.registration.dto;

import java.util.Date;

import com.livescribe.framework.orm.vectordb.Warranty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("warranty")
public class WarrantyDTO {

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
	 */
	public WarrantyDTO() {
		
	}
	
	/**
	 * <p></p>
	 * 
	 * @param warranty
	 */
	public WarrantyDTO(Warranty warranty) {
		
		setAppId(warranty.getAppId());
		setEdition(warranty.getEdition());
		setPenSerial(warranty.getPenSerial());
		setDisplayId(warranty.getDisplayId());
		setPenName(warranty.getPenName());
		setFirstName(warranty.getFirstName());
		setLastName(warranty.getLastName());
		setEmail(warranty.getEmail());
		setLocale(warranty.getLocale());
		setCreated(warranty.getCreated());
		setCountry(null);
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
	 * @return the edition
	 */
	public Integer getEdition() {
		return edition;
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
	 * @return the displayId
	 */
	public String getDisplayId() {
		return displayId;
	}

	/**
	 * <p></p>
	 * 
	 * @return the penName
	 */
	public String getPenName() {
		return penName;
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
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * <p></p>
	 * 
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * <p></p>
	 * 
	 * @param edition the edition to set
	 */
	public void setEdition(Integer edition) {
		this.edition = edition;
	}

	/**
	 * <p></p>
	 * 
	 * @param penSerial the penSerial to set
	 */
	public void setPenSerial(String penSerial) {
		this.penSerial = penSerial;
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
	 * @param penName the penName to set
	 */
	public void setPenName(String penName) {
		this.penName = penName;
	}

	/**
	 * <p></p>
	 * 
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * <p></p>
	 * 
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * <p></p>
	 * 
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * <p></p>
	 * 
	 * @param optIn the optIn to set
	 */
	public void setOptIn(Boolean optIn) {
		this.optIn = optIn;
	}

	/**
	 * <p></p>
	 * 
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
}

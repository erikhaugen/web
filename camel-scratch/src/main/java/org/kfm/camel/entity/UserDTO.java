/**
 * 
 */
package org.kfm.camel.entity;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>Represents user data transferable between Services.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("user")
public class UserDTO {

	@XStreamAlias("uid")
	private String uid;
	
	@XStreamAlias("primaryEmail")
	private String primaryEmail;
	
	@XStreamAlias("address")
	private String address;
	
	@XStreamAlias("city")
	private String city;
	
	@XStreamAlias("usState")
	private String usState;
	
	@XStreamAlias("country")
	private String country;
	
	@XStreamAlias("postalCode")
	private String postalCode;
	
	@XStreamAlias("firstName")
	private String firstName;
	
	@XStreamAlias("middleName")
	private String middleName;
	
	@XStreamAlias("lastName")
	private String lastName;

	@XStreamAlias("gradYear")
	private Integer gradYear;

	@XStreamAlias("birthYear")
	private Integer birthYear;
	
	@XStreamAlias("major")
	private String major;
	
	@XStreamAlias("phone")
	private String phone;
	
	@XStreamAlias("organization")
	private String organization;

	@XStreamAlias("occupation")
	private String occupation;
	
	@XStreamAlias("sex")
	private Character sex;
	
	@XStreamAlias("university")
	private String university;
	
	@XStreamAlias("locale")
	private String locale;
	
	@XStreamAlias("sendEmail")
	private Boolean sendEmail;
	
	@XStreamAlias("password")
	private String password;
	
	@XStreamAlias("confirmed")
	private Boolean confirmed;
	
	@XStreamAlias("confirmationDate")
	private Date confirmationDate;

	@XStreamAlias("enabled")
	private Boolean enabled;
	
	/**
	 * <p>Default class constructor.</p>
	 *
	 */
	public UserDTO() {}
	
	
	/**
	 * <p>Constructor that creates a basic UserDTO object with basic attributes.</p>
	 *
	 * @param uid uid of the user
	 * @param email email of the user
	 * @param password password of the user
	 * @param confirmed confirmed setting  of the user
	 * @param sendEmail sendMail setting  of the user
	 */
    public UserDTO(String uid, String primaryEmail, String password, boolean confirmed, boolean sendEmail) {
        this.uid = uid;
        this.primaryEmail = primaryEmail;
        this.password = password;
        this.confirmed = confirmed;
		this.sendEmail = sendEmail;
    }
	
	/**
	 * <p>Constructor that takes a <code>User</code> object as a parameter.</p>
	 *
	 * @param user The <code>User</code> object to use.
	 */
	public UserDTO(String uid,
			String primaryEmail,
			String password,
			String firstName,
			String middleName,
			String lastName,
			Integer gradYear,
			Integer birthYear,
			String major,
			String phone,
			String organization,
			String occupation,
			Character sex,
			String university,
			String locale,
			Boolean sendEmail,
			Boolean confirmed,
			Date confirmationDate,
			Boolean enabled) {
		
		this.uid = uid;
		this.primaryEmail = primaryEmail;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.gradYear = gradYear;
		this.birthYear = birthYear;
		this.major = major;
		this.phone = phone;
		this.organization = organization;
		this.occupation = occupation;
		this.sex = sex;
		this.university = university;
		this.locale = locale;
		this.sendEmail = sendEmail;
		this.password = password;
		this.confirmed = confirmed;
		this.confirmationDate = confirmationDate;
		this.enabled = enabled;
	}

	/**
	 * @return the birthYear
	 */
	public Integer getBirthYear() {
		return birthYear;
	}

	/**
	 * @return the comfirmationDate
	 */
	public Date getConfirmationDate() {
		return confirmationDate;
	}

	/**
	 * @return the confirmed
	 */
	public Boolean getConfirmed() {
		return confirmed;
	}

	/**
	 * @return the enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return the gradYear
	 */
	public Integer getGradYear() {
		return gradYear;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * @return the major
	 */
	public String getMajor() {
		return major;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @return the occupation
	 */
	public String getOccupation() {
		return occupation;
	}

	/**
	 * @return the organization
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @return the primaryEmail
	 */
	public String getPrimaryEmail() {
		return primaryEmail;
	}

	/**
	 * @return the sendEmail
	 */
	public Boolean getSendEmail() {
		return sendEmail;
	}

	/**
	 * @return the sex
	 */
	public Character getSex() {
		return sex;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @return the university
	 */
	public String getUniversity() {
		return university;
	}

	/**
	 * @param comfirmationDate the comfirmationDate to set
	 */
	public void setConfirmationDate(Date comfirmationDate) {
		this.confirmationDate = comfirmationDate;
	}

	/**
	 * @param confirmed the confirmed to set
	 */
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @param occupation the occupation to set
	 */
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/**
	 * @param birthYear the birthYear to set
	 */
	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @param gradYear the gradYear to set
	 */
	public void setGradYear(Integer gradYear) {
		this.gradYear = gradYear;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @param major the major to set
	 */
	public void setMajor(String major) {
		this.major = major;
	}

	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param primaryEmail the primaryEmail to set
	 */
	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
	}

	/**
	 * @param sendEmail the sendEmail to set
	 */
	public void setSendEmail(Boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(Character sex) {
		this.sex = sex;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @param university the university to set
	 */
	public void setUniversity(String university) {
		this.university = university;
	}


	/**
	 * <p></p>
	 * 
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}


	/**
	 * <p></p>
	 * 
	 * @return the usState
	 */
	public String getUsState() {
		return usState;
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
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}


	/**
	 * <p></p>
	 * 
	 * @param usState the usState to set
	 */
	public void setUsState(String usState) {
		this.usState = usState;
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
	 * @return the city
	 */
	public String getCity() {
		return city;
	}


	/**
	 * <p></p>
	 * 
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}


	/**
	 * <p></p>
	 * 
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}


	/**
	 * <p></p>
	 * 
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
}

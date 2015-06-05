/*
 * Created:  Jan 10, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.orm.fb.corp;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CorpUserProfile {

	private byte[] primaryKey;
	private byte[] addressKey;
	private Integer birthYear;
	private String email;
	private String firstName;
	private String lastName;
	private String userPassword;
	
	/**
	 * <p></p>
	 */
	public CorpUserProfile() {
		
	}

	/**
	 * <p></p>
	 * 
	 * @param primaryKey
	 * @param addressKey
	 * @param birthYear
	 * @param email
	 * @param firstName
	 * @param lastName
	 * @param userPassword
	 */
	public CorpUserProfile(byte[] primaryKey, byte[] addressKey, Integer birthYear, String email, String firstName, String lastName, String userPassword) {
		
		this.primaryKey = primaryKey;
		this.addressKey = addressKey;
		this.birthYear = birthYear;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userPassword = userPassword;
	}
	
    public Object clone() {
    	
    	CorpUserProfile obj = new CorpUserProfile();
    	obj.setAddressKey(this.addressKey);
    	obj.setBirthYear(this.birthYear);
    	obj.setEmail(this.email);
    	obj.setFirstName(this.firstName);
    	obj.setLastName(this.lastName);
     	obj.setPrimaryKey(this.primaryKey);
    	obj.setUserPassword(this.userPassword);
    	
    	return obj;
    }
    
	/**
	 * @return the primaryKey
	 */
	public byte[] getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @return the addressKey
	 */
	public byte[] getAddressKey() {
		return addressKey;
	}

	/**
	 * @return the birthYear
	 */
	public Integer getBirthYear() {
		return birthYear;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @return the userPassword
	 */
	public String getUserPassword() {
		return userPassword;
	}

	/**
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(byte[] primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * @param addressKey the addressKey to set
	 */
	public void setAddressKey(byte[] addressKey) {
		this.addressKey = addressKey;
	}

	/**
	 * @param birthYear the birthYear to set
	 */
	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @param userPassword the userPassword to set
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
}

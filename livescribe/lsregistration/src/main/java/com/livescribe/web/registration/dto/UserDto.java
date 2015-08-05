/**
 * 
 */
package com.livescribe.web.registration.dto;

import com.livescribe.framework.orm.vectordb.Registration;
import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Mohammad M. Naqvi
 * @since 1.3
 *
 */
@XStreamAlias("userDto")
public class UserDto {
	
	@XStreamAlias("firstName")
	private String firstName;
	@XStreamAlias("lastName")
	private String lastName;
	@XStreamAlias("email")
	private String email;

	/**
	 * 
	 */
	public UserDto() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	public UserDto(Registration registration) {
		if (null != registration) {
			firstName = registration.getFirstName();
			lastName = registration.getLastName();
			email = registration.getEmail();
		}
	}

	public UserDto(RegistrationHistory regHistory) {
		if (null != regHistory) {
			firstName = regHistory.getFirstName();
			lastName = regHistory.getLastName();
			email = regHistory.getEmail();
		}
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}



	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}



	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}



	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}



	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserDto [firstName=");
		builder.append(firstName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append(", email=");
		builder.append(email);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		UserDto other = (UserDto) obj;
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!email.equals(other.email)) {
			return false;
		}
		if (firstName == null) {
			if (other.firstName != null) {
				return false;
			}
		} else if (!firstName.equals(other.firstName)) {
			return false;
		}
		if (lastName == null) {
			if (other.lastName != null) {
				return false;
			}
		} else if (!lastName.equals(other.lastName)) {
			return false;
		}
		return true;
	}

}

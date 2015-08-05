package com.livescribe.web.registration.dto;

import java.util.Date;

import com.livescribe.framework.orm.vectordb.RegistrationHistory;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("registration")
public class RegistrationHistoryDTO extends RegistrationDTO {

	@XStreamAlias("registrationDate")
	private Date registrationDate;
	
	/**
	 * <p></p>
	 */
	public RegistrationHistoryDTO() {
		
	}
	
	/**
	 * <p></p>
	 * 
	 * @param registrationHistory
	 */
	public RegistrationHistoryDTO(RegistrationHistory registrationHistory) {
		setAppId(registrationHistory.getAppId());
		setEdition(registrationHistory.getEdition());
		setPenSerial(registrationHistory.getPenSerial());
		setDisplayId(registrationHistory.getDisplayId());
		setPenName(registrationHistory.getPenName());
		setFirstName(registrationHistory.getFirstName());
		setLastName(registrationHistory.getLastName());
		setEmail(registrationHistory.getEmail());
		setLocale(registrationHistory.getLocale());
		setCountry(registrationHistory.getCountry());
		setOptIn(registrationHistory.getOptIn());
		this.registrationDate = registrationHistory.getRegistrationDate();
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	protected void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
}

/*
 * Created:  Oct 19, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.dto;

import java.util.ArrayList;
import java.util.List;

import com.livescribe.aws.tokensvc.response.ResponseCode;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>Represents information to be returned regarding a user&apos;s incomplete
 * registration.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("response")
public class RegistrationDetails {

	@XStreamAlias("status")
	private ResponseCode status;
	
	@XStreamAlias("email")
	private String emailAddress;
	
	@XStreamAlias("token")
	private String registrationToken;

	//	Commented out to prevent an empty <devices /> tag in the response to
	//	getRegistrationDetails().  Could be used later.  [KFM - 2011-10-26]
//	@XStreamAlias("devices")
//	private List<RegisteredDeviceDTO> registeredDevices = new ArrayList<RegisteredDeviceDTO>();
	
	private String deviceType;
	private String partialSerialNumber;
	
	/**
	 * <p></p>
	 * 
	 */
	public RegistrationDetails() {
	}

	/**
	 * <p></p>
	 * 
	 * @param status
	 * @param emailAddress
	 * @param registrationToken
	 * @param deviceType
	 * @param partialSerialNumber
	 */
	public RegistrationDetails(ResponseCode status, String emailAddress, String registrationToken, String deviceType, String partialSerialNumber) {
		
		this.status = status;
		this.emailAddress = emailAddress;
		this.registrationToken = registrationToken;
		this.deviceType = deviceType;
		this.partialSerialNumber = partialSerialNumber;
	}
	
//	/**
//	 * <p></p>
//	 * 
//	 * @param device
//	 */
//	public void addRegisteredDevice(RegisteredDeviceDTO device) {
//		
//		this.registeredDevices.add(device);
//	}
	
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
	 * @return the registrationToken
	 */
	public String getRegistrationToken() {
		return registrationToken;
	}

	/**
	 * <p></p>
	 * 
	 * @return the deviceType
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * <p></p>
	 * 
	 * @return the partialSerialNumber
	 */
	public String getPartialSerialNumber() {
		return partialSerialNumber;
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
	 * @param registrationToken the registrationToken to set
	 */
	public void setRegistrationToken(String registrationToken) {
		this.registrationToken = registrationToken;
	}

	/**
	 * <p></p>
	 * 
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * <p></p>
	 * 
	 * @param partialSerialNumber the partialSerialNumber to set
	 */
	public void setPartialSerialNumber(String partialSerialNumber) {
		this.partialSerialNumber = partialSerialNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("\n==================================================\n");
		builder.append("    Registration Details\n");
		builder.append("--------------------------------------------------\n");
		builder.append("    Email Address:  " + emailAddress + "\n");
		builder.append("       Reg. Token:  " + registrationToken + "\n");
		builder.append("      Device Type:  " + deviceType + "\n");
		builder.append("       Partial SN:  " + partialSerialNumber + "\n");
		builder.append("==================================================\n");
		
		return builder.toString();
	}

//	/**
//	 * <p></p>
//	 * 
//	 * @return the registeredDevices
//	 */
//	public List<RegisteredDeviceDTO> getRegisteredDevices() {
//		return registeredDevices;
//	}
//
//	/**
//	 * <p></p>
//	 * 
//	 * @param registeredPens the registeredDevices to set
//	 */
//	public void setRegisteredDevices(List<RegisteredDeviceDTO> registeredDevices) {
//		this.registeredDevices = registeredDevices;
//	}
}

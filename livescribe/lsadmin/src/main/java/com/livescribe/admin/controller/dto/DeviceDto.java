/**
 * 
 */
package com.livescribe.admin.controller.dto;

import java.util.Date;

import org.apache.log4j.Logger;

import com.livescribe.afp.PenID;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.web.lssettingsservice.client.LSSettingsServiceClient;

/**
 * @author Mohammad M. Naqvi
 * @since 1.3
 *
 */
public final class DeviceDto {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private String displayId;					// format: AYE-XXX-YYY-ZZ
	private String serialNumber;				// format: XXXXXXXXXX (all decimal)
	private String hexSerialNumber;
	private String type;						// Echo/Pulse/Sky etc..
	private String name;						// Will be empty if this device is NOT registered 
	private Date registrationCompletionDate;	// Will be empty if this device is NOT registered 

	/**
	 * The default constructor.
	 * 
	 */
	public DeviceDto() {
	}
	
	/**
	 * Constructs a DeviceDto object from given RegisteredDevice record.
	 * @param device
	 */
	public DeviceDto(Pen pen) {
		if (null != pen) {
			serialNumber = pen.getSerialnumber();
			hexSerialNumber = pen.getSerialnumberHex();
			displayId = pen.getDisplayId();
			type = pen.getPenType();
		}
	}
	
	/**
	 * Constructs a DeviceDto object from given RegisteredDevice record.
	 * @param device
	 */
	public DeviceDto(RegisteredDevice device) {
		
		if (null != device) {
			serialNumber = device.getDeviceSerialNumber();
			long serialNumberLong = Long.parseLong(serialNumber);
			hexSerialNumber = Long.toHexString(serialNumberLong);
			displayId = new PenID(serialNumberLong).getSerial();
			type = device.getDeviceType();
			registrationCompletionDate = device.getCompletedDate();

			// Fetch the pen name from LSSettings service!
			// In case of an exception, just log it and proceed, No need to abort!!
			logger.debug("Calling Settings service to get the pen name for penSerialId " + serialNumber);
			try {
				LSSettingsServiceClient client = new LSSettingsServiceClient();
				String penName = client.getPenName(serialNumber);
				logger.debug("Got the pen name: " + penName);
				name = penName;
			} catch (Exception e) {
				logger.error("Caught Exception! Cannot get Pen Name from the LSSettings service)", e);
			}
		}
	}

	/**
	 * @return the displayId
	 */
	public String getDisplayId() {
		return displayId;
	}

	/**
	 * @param displayId the displayId to set
	 */
	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * @return the hexSerialNumber
	 */
	public String getHexSerialNumber() {
		return hexSerialNumber;
	}

	/**
	 * @param hexSerialNumber the hexSerialNumber to set
	 */
	public void setHexSerialNumber(String hexSerialNumber) {
		this.hexSerialNumber = hexSerialNumber;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the registrationCompletionDate
	 */
	public Date getRegistrationCompletionDate() {
		return registrationCompletionDate;
	}

	/**
	 * @param registrationCompletionDate the registrationCompletionDate to set
	 */
	public void setRegistrationCompletionDate(Date registrationCompletionDate) {
		this.registrationCompletionDate = registrationCompletionDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((displayId == null) ? 0 : displayId.hashCode());
		result = prime * result
				+ ((hexSerialNumber == null) ? 0 : hexSerialNumber.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((registrationCompletionDate == null) ? 0
						: registrationCompletionDate.hashCode());
		result = prime * result
				+ ((serialNumber == null) ? 0 : serialNumber.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceDto other = (DeviceDto) obj;
		if (displayId == null) {
			if (other.displayId != null)
				return false;
		} else if (!displayId.equals(other.displayId))
			return false;
		if (hexSerialNumber == null) {
			if (other.hexSerialNumber != null)
				return false;
		} else if (!hexSerialNumber.equals(other.hexSerialNumber))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (registrationCompletionDate == null) {
			if (other.registrationCompletionDate != null)
				return false;
		} else if (!registrationCompletionDate
				.equals(other.registrationCompletionDate))
			return false;
		if (serialNumber == null) {
			if (other.serialNumber != null)
				return false;
		} else if (!serialNumber.equals(other.serialNumber))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceDto [displayId=");
		builder.append(displayId);
		builder.append(", serialNumber=");
		builder.append(serialNumber);
		builder.append(", hexSerialNumber=");
		builder.append(hexSerialNumber);
		builder.append(", type=");
		builder.append(type);
		builder.append(", name=");
		builder.append(name);
		builder.append(", registrationCompletionDate=");
		builder.append(registrationCompletionDate);
		builder.append("]");
		return builder.toString();
	}	
}
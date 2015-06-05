/*
 * Created:  Oct 6, 2011
 *      By:  kmurdoff
 */
package com.livescribe.aws.tokensvc.service;

import java.io.IOException;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;

import com.livescribe.aws.tokensvc.dto.PenDTO;
import com.livescribe.aws.tokensvc.dto.RegistrationState;
import com.livescribe.aws.tokensvc.exception.DeviceAlreadyRegisteredException;
import com.livescribe.aws.tokensvc.exception.DeviceNotFoundException;
import com.livescribe.aws.tokensvc.exception.DuplicateEmailAddressException;
import com.livescribe.aws.tokensvc.exception.DuplicateSerialNumberException;
import com.livescribe.aws.tokensvc.exception.InvalidParameterException;
import com.livescribe.aws.tokensvc.exception.InvalidRegistrationTokenException;
import com.livescribe.aws.tokensvc.exception.LSDSClientException;
import com.livescribe.aws.tokensvc.exception.MultipleRegistrationsFoundException;
import com.livescribe.aws.tokensvc.exception.RegistrationNotFoundException;
import com.livescribe.aws.tokensvc.exception.UserNotFoundException;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.web.lssettingsservice.client.exception.InvalidSettingDataException;
import com.livescribe.web.lssettingsservice.client.exception.LSSettingsServiceConnectionException;
import com.livescribe.web.lssettingsservice.client.exception.NoRegisteredDeviceFoundException;
import com.livescribe.web.lssettingsservice.client.exception.NoSettingFoundException;
import com.livescribe.web.lssettingsservice.client.exception.ProcessingErrorException;

/**
 * <p>Represents the API for a registration service implementation.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface RegistrationService {

	/**
	 * <p></p>
	 * 
	 * @param serialNumber
	 * @throws RegistrationNotFoundException
	 * @throws MultipleRegistrationsFoundException
	 */
	public void complete(String serialNumber) 
			throws RegistrationNotFoundException, MultipleRegistrationsFoundException, DeviceAlreadyRegisteredException, XmlRpcException, LSDSClientException, UserNotFoundException;
	
	/**
	 * <p></p>
	 * 
	 * @param serialNumber
	 * 
	 * @return
	 * 
	 * @throws MultipleRegistrationsFoundException
	 * @throws RegistrationNotFoundException
	 */
	public RegisteredDevice findBySerialNumber(String serialNumber) throws MultipleRegistrationsFoundException, RegistrationNotFoundException;

	/**
	 * <p></p>
	 * 
	 * @param serialNumber
	 * @return
	 * @throws MultipleRegistrationsFoundException
	 * @throws RegistrationNotFoundException
	 * @throws UserNotFoundException
	 */
	public User findUserRegisteredWithDevice(String serialNumber) 
			throws MultipleRegistrationsFoundException, RegistrationNotFoundException, UserNotFoundException;
	
	/**
	 * <p></p>
	 * 
	 * @param deviceId
	 * 
	 * @return
	 * 
	 * @throws MultipleRegistrationsFoundException
	 */
	RegistrationState getRegistrationState(String deviceId) throws MultipleRegistrationsFoundException;

	/**
	 * <p></p>
	 * 
	 * @param deviceId
	 * @param email
	 * 
	 * @return
	 * 
	 * @throws MultipleRegistrationsFoundException 
	 * @throws DuplicateEmailAddressException 
	 */
	public RegistrationState getRegistrationState(String deviceId, String email) throws MultipleRegistrationsFoundException, DuplicateEmailAddressException;

	/**
	 * <p></p>
	 * 
	 * @param deviceId
	 * 
	 * @return
	 * @throws MultipleRegistrationsFoundException 
	 */
	public boolean isRegistered(String deviceId);

	/**
	 * <p></p>
	 * 
	 * @param pen The pen to register to the given user.
	 * @param email The user to register with the pen.
	 * 
	 * @return
	 * 
	 * @throws DeviceAlreadyRegisteredException if more than one device is found
	 * with the given <code>deviceId</code>.
	 * @throws InvalidParameterException 
	 */
	public RegisteredDevice registerDevice(Pen pen, User user) throws DeviceAlreadyRegisteredException, InvalidParameterException;

	/**
	 * <p></p>
	 * 
	 * @param penName
	 * @param penSerialNumber
	 * @throws IOException
	 */
	public void savePenNameInDeviceSettings(String penName, String penSerialNumber) 
			throws InvalidSettingDataException, LSSettingsServiceConnectionException, NoSettingFoundException, NoRegisteredDeviceFoundException, ProcessingErrorException, IOException;
	
	/**
	 * <p></p>
	 * 
	 * @param loginToken
	 * @param penDisplayId
	 * @param namespace
	 * @param settingName
	 * @param settingValue
	 * @param settingType
	 * @throws UserNotLoggedInException
	 * @throws RegistrationNotFoundException
	 * @throws DuplicateSerialNumberException
	 * @throws DeviceNotFoundException
	 */
	public void saveDeviceSettings(String loginToken, String penDisplayId, String namespace, String settingName, String settingValue, String settingType) 
			throws UserNotLoggedInException, RegistrationNotFoundException, DuplicateSerialNumberException, DeviceNotFoundException, IOException, InvalidSettingDataException, LSSettingsServiceConnectionException, NoSettingFoundException, NoRegisteredDeviceFoundException, ProcessingErrorException;
	
	/**
	 * <p>Resets the database by logging the user, identified by the given email, 
	 * out, unregistering <b><u>all</u></b> pens, removing the user&apos;s email
	 * addresses, and finally, removing the user.</p>
	 * 
	 * @param email The email address of the user to remove.
	 * @param loginDomain The login domain to use when logging out the user.
	 * 
	 * @throws InvalidRegistrationTokenException
	 * @throws UserNotFoundException
	 * @throws RegistrationNotFoundException 
	 * @throws DuplicateEmailAddressException 
	 */
	public void resetByEmail(String email) throws UserNotFoundException, RegistrationNotFoundException, DuplicateEmailAddressException;

	/**
	 * <p>Unregisters a device identified by the given device ID.</p>
	 * 
	 * @param deviceId The unique ID of the device to unregister.
	 * 
	 * @return <code>true</code>, if the operation was successful; <code>false</code> if not.
	 * 
	 * @throws MultipleRegistrationsFoundException if more than one registration was found with the given device ID. 
	 * @throws RegistrationNotFoundException if no registration could be found in the <code>registered_device</code> table with the given device ID.
	 */
	public boolean unregister(String deviceId) throws RegistrationNotFoundException, MultipleRegistrationsFoundException;

	/**
	 * <p></p>
	 * 
	 * @param loginToken
	 * @return
	 * @throws UserNotLoggedInException
	 * @throws UserNotFoundException
	 * @throws DeviceNotFoundException
	 * @throws DuplicateSerialNumberException
	 */
	public List<PenDTO> getWiFiPenList(String loginToken) 
			throws UserNotLoggedInException, UserNotFoundException, RegistrationNotFoundException, DeviceNotFoundException, 
			DuplicateSerialNumberException, IOException, InvalidSettingDataException, LSSettingsServiceConnectionException, 
			NoSettingFoundException, NoRegisteredDeviceFoundException, ProcessingErrorException;
}

/*
 * Created:  Oct 6, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.livescribe.aws.tokensvc.config.AppConstants;
import com.livescribe.aws.tokensvc.config.AppProperties;
import com.livescribe.aws.tokensvc.dto.PenDTO;
import com.livescribe.aws.tokensvc.dto.RegistrationState;
import com.livescribe.aws.tokensvc.exception.DeviceAlreadyRegisteredException;
import com.livescribe.aws.tokensvc.exception.DeviceNotFoundException;
import com.livescribe.aws.tokensvc.exception.DuplicateEmailAddressException;
import com.livescribe.aws.tokensvc.exception.DuplicateSerialNumberException;
import com.livescribe.aws.tokensvc.exception.InvalidParameterException;
import com.livescribe.aws.tokensvc.exception.LSDSClientException;
import com.livescribe.aws.tokensvc.exception.MultipleRegistrationsFoundException;
import com.livescribe.aws.tokensvc.exception.RegistrationNotFoundException;
import com.livescribe.aws.tokensvc.exception.UserNotFoundException;
import com.livescribe.aws.tokensvc.lsds.LSDSClient;
import com.livescribe.aws.tokensvc.orm.consumer.CustomRegisteredDeviceDao;
import com.livescribe.aws.tokensvc.orm.consumer.RegisteredDeviceFactory;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.web.lssettingsservice.client.LSSettingsServiceClient;
import com.livescribe.web.lssettingsservice.client.exception.InvalidSettingDataException;
import com.livescribe.web.lssettingsservice.client.exception.LSSettingsServiceConnectionException;
import com.livescribe.web.lssettingsservice.client.exception.NoRegisteredDeviceFoundException;
import com.livescribe.web.lssettingsservice.client.exception.NoSettingFoundException;
import com.livescribe.web.lssettingsservice.client.exception.ProcessingErrorException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegistrationServiceImpl implements AppConstants, RegistrationService {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CustomRegisteredDeviceDao registeredDeviceDao;
	
	@Autowired
	private ManufacturingService manufacturingService;
	
	@Autowired
	private LSDSClient lsdsClient;
	
	SecureRandom random = new SecureRandom();

	private static final char[] symbols = new char[31];
	
	static {
		//	Not using '0' (zero) and '1' (one) to avoid confusing them with 
		//	'O' (capital 'O'), 'I' (capital 'i'), and 'L' (capital 'L').
		for (int idx = 0; idx < 8; ++idx) {
			symbols[idx] = (char) ('2' + idx);
		}
		
		int idx = 8;
		char[] alpha = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z'};
		for (char ch : alpha) {
			symbols[idx++] = ch;
		}
	}
	
	/**
	 * <p></p>
	 * 
	 */
	public RegistrationServiceImpl() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.RegistrationService#complete(java.lang.String)
	 */
	@Override
	@Transactional("consumer")
	public void complete(String serialNumber) 
			throws RegistrationNotFoundException, MultipleRegistrationsFoundException, DeviceAlreadyRegisteredException, XmlRpcException, LSDSClientException, UserNotFoundException {
		
		String method = "complete(serialNumber):  ";
		
		RegisteredDevice regDev = registeredDeviceDao.findBySerialNumber(serialNumber);
		if (regDev.getComplete()) {
			throw new DeviceAlreadyRegisteredException("Device with serial number '" + serialNumber + "' is already registered.");
		}
		
		User user = regDev.getUser();
		String email = user.getPrimaryEmail();
		
		regDev.setComplete(true);
		Date date = new Date();
		regDev.setCompletedDate(date);
		regDev.setLastModified(date);
		regDev.setLastModifiedBy("Token Service");
		
		registeredDeviceDao.merge(regDev);
		
		logger.info(method + "Registration complete for pen with serial number '" + serialNumber + "' to user with email '" + email + "'.");
		
		// prepare sending welcome email
		logger.debug(method + "Calling LSDS XML-RPC service to send complete registration email to: " + email);
		HashMap<String, Object> lsdsResultMap = lsdsClient.sendCompleteRegistrationEmail(email, serialNumber, regDev.getDeviceType());
		logger.debug(method + "Completed calling LSDS XML-RPC service with returnCode=" + lsdsResultMap.get("returnCode"));
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.RegistrationService#findByToken(java.lang.String)
	 */
	@Override
	@Transactional("consumer")
	public RegisteredDevice findBySerialNumber(String serialNumber) throws MultipleRegistrationsFoundException, RegistrationNotFoundException {
		
		RegisteredDevice regDev = registeredDeviceDao.findBySerialNumber(serialNumber);
		
		// Fetching primary email field to avoid session closed due to lazy loading.
		String primaryEmail = regDev.getUser().getPrimaryEmail();
		logger.debug("Force fetching primary email = " + primaryEmail);
		
		return regDev;
	}
	
	@Transactional("consumer")
	public User findUserRegisteredWithDevice(String serialNumber) 
			throws MultipleRegistrationsFoundException, RegistrationNotFoundException, UserNotFoundException {
		
		RegisteredDevice regDev = registeredDeviceDao.findBySerialNumber(serialNumber);
		
		User user = userService.findUserById(regDev.getUser().getUserId());
		
		return user;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.RegistrationService#isRegistered(java.lang.String)
	 */
	@Override
	@Transactional("consumer")
	public boolean isRegistered(String deviceId) {
		
		RegisteredDevice regDev = null;
		try {
			regDev = registeredDeviceDao.findBySerialNumber(deviceId);
		}
		catch (RegistrationNotFoundException rnfe) {
			rnfe.printStackTrace();
			return false;
		}
		catch (MultipleRegistrationsFoundException mrfe) {
			mrfe.printStackTrace();
			return true;
		}
		
		if (regDev != null) {
			return true;
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.RegistrationService#getRegistrationState(java.lang.String)
	 */
	@Override
	@Transactional("consumer")
	public RegistrationState getRegistrationState(String deviceId) throws MultipleRegistrationsFoundException {
		
		RegistrationState state = new RegistrationState();
		
		RegisteredDevice regDev = null;
		try {
			regDev = registeredDeviceDao.findBySerialNumber(deviceId);
		}
		catch (RegistrationNotFoundException rnfe) {
			//	The state here is already set to 'false' by default.
		}

		if (regDev != null) {
			state.setPenRegistrationStarted(true);

			//	This part is a "hack" because of Hibernate's mapping of MySQL's 
			//	TINYINT or BIT(1) columns to a Boolean class instead of a 
			//	'boolean' primative.  And I don't have time to research it 
			//	and do it right.  [KFM - 2011-12-08]
			if (regDev.getComplete() == null) {
				//	Set to 'false' by default.
			}
			else if (regDev.getComplete()) {
				state.setPenRegistrationComplete(true);
			}
		}
		
		return state;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.RegistrationService#getRegistrationState(java.lang.String, java.lang.String)
	 */
	@Override
	@Deprecated
	@Transactional("consumer")
	public RegistrationState getRegistrationState(String deviceId, String email) throws MultipleRegistrationsFoundException, DuplicateEmailAddressException {
		
		RegistrationState state = new RegistrationState();
		
		RegisteredDevice regDev = null;
		try {
			regDev = registeredDeviceDao.findBySerialNumber(deviceId);
		}
		catch (RegistrationNotFoundException rnfe) {
			//	The state here is already set to 'false' by default.
		}
		
		//	
		if (regDev != null) {
			state.setPenRegistrationStarted(true);
			
			//	This part is a "hack" because of Hibernate's mapping of MySQL's 
			//	TINYINT or BIT(1) columns to a Boolean class instead of a 
			//	'boolean' primative.  And I don't have time to research it 
			//	and do it right.  [KFM - 2011-12-08]
			if (regDev.getComplete() == null) {
				//	Set to 'false' by default.
			}
			else if (regDev.getComplete()) {
				state.setPenRegistrationComplete(true);
			}
		}
		
		//	The user's account status is no longer needed during registration of a pen.
//		UserDTO user = null;
//		try {
//			user = userService.findByEmail(email);
//		}
//		catch (UserNotFoundException unfe) {
//			//	State set to 'false' by default.
//		}
		
		//	If the user already exists ...
//		if (user != null) {
//			state.setExistingUser(true);
//			//	... and is confirmed ...
//			if (user.isConfirmed()) {
//				state.setUserAccountConfirmed(true);
//			}
//			//	... otherwise, if the pen registration was started, but never
//			//	completed, this is a 'retry'.
//			if ((state.isPenRegistrationStarted()) && (!state.isPenRegistrationComplete())) {
//				state.setRetry(true);
//			}
//		}		
		return state;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.TokenService#registerDevice(com.livescribe.aws.tokensvc.orm.manufacturing.Pen, com.livescribe.aws.tokensvc.orm.consumer.User)
	 */
	@Override
	@Transactional("consumer")
	public RegisteredDevice registerDevice(Pen pen, User user) throws DeviceAlreadyRegisteredException, InvalidParameterException {
		
		String method = "registerDevice():  ";
		
		if ((pen == null) || (user == null)) {
			String msg = "Null parameter found.";
			throw new InvalidParameterException(msg);
		}
		
		//	Determine if the pen is already registered.
		String serialNumber = pen.getSerialnumber();
		RegisteredDevice existingReg = null;
		try {
			existingReg = registeredDeviceDao.findBySerialNumber(serialNumber);
			
		} catch (RegistrationNotFoundException e) {
			logger.info(method + "No registration found for pen '" + pen.getDisplayId() + "'");
			
		} catch (MultipleRegistrationsFoundException e) {
			String email = user.getPrimaryEmail();
			String msg = "Device '" + pen.getDisplayId() + "' is already registered to User '" + email + "'.";
			throw new DeviceAlreadyRegisteredException(msg);
		}

		//	If the registration already exists, throw an exception.
		if (existingReg != null) {
			String msg = "Device '" + pen.getDisplayId() + "' is already registered to User '" + existingReg.getUser().getPrimaryEmail() + "'.";
			throw new DeviceAlreadyRegisteredException(msg);
		}
		
		//	Create a new 'registered_device' record.
		RegisteredDevice regDev = RegisteredDeviceFactory.createRegisteredDevice(pen, user);
		
		StringBuilder builder = new StringBuilder();
		builder.append("\n--------------------------------------------------\n");
		builder.append("  Registered Device\n");
		builder.append("     Pen ID: " + regDev.getDeviceId() + "\n");
		builder.append("    User ID: " + regDev.getUser().getUserId() + "\n");
		builder.append("--------------------------------------------------\n");
		logger.debug(builder.toString());
		
		//	Set 'completed' to be January 1, 1970 to avoid the '0000-00-00 00:00:00'
		//	issue with MySQL.
		Date epoch = getEpochDate();
		regDev.setCompletedDate(epoch);
		
		Date created = new Date();
		regDev.setCreated(created);
		regDev.setLastModified(created);
		regDev.setLastModifiedBy("Token Service");
		
		registeredDeviceDao.persist(regDev);
		
		// if the user is already confirmed (i.e old user registers another pen)
		// we need to post registration token to SQS since he/she won't need to confirm anymore
		//	2013-01-18 - Commented out because this part of the process is no
		//				 longer necessary.
//		if (user.isConfirmed()) {
//			logger.debug("Registered an existing User email = " + user.getPrimaryEmail() + ". Will post regToken to SQS for this device ID = " + serialNumber);
//			postRegistrationTokenToSQS(regDev.getRegToken(), serialNumber);
//		}
		
		return regDev;
	}
	
	/**
	 * 
	 * @param penName
	 * @param penSerialNumber
	 * 
	 * @throws ProcessingErrorException 
	 * @throws NoRegisteredDeviceFoundException 
	 * @throws NoSettingFoundException 
	 * @throws LSSettingsServiceConnectionException 
	 * @throws InvalidSettingDataException 
	 * @throws IOException
	 */
	public void savePenNameInDeviceSettings(String penName, String penSerialNumber) 
			throws InvalidSettingDataException, LSSettingsServiceConnectionException, NoSettingFoundException, NoRegisteredDeviceFoundException, ProcessingErrorException, IOException {
		
		String method = "savePenNameInDeviceSettings():  ";
		
		LSSettingsServiceClient client = new LSSettingsServiceClient();
		
		logger.debug(method + "Calling LSSettingsService Client to save penName='" + penName + "' for pen '" + penSerialNumber + "'");
		client.setPenName(penSerialNumber, penName);
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.RegistrationService#saveDeviceSettings(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional("consumer")
	public void saveDeviceSettings(String loginToken, String penDisplayId, String namespace, String settingName, String settingValue, String settingType) 
			throws UserNotLoggedInException, RegistrationNotFoundException, DuplicateSerialNumberException, DeviceNotFoundException, IOException, InvalidSettingDataException, LSSettingsServiceConnectionException, NoSettingFoundException, NoRegisteredDeviceFoundException, ProcessingErrorException {
		
		String method = "getWiFiPenList():  ";
		
		// Check if the provided loginToken is valid
		User user = userService.findUserByLoginToken(loginToken);
		
		// Check if the logged-in user has any registered pens
		Set<RegisteredDevice> regDevices = user.getRegisteredDevices();
		if (regDevices == null || regDevices.isEmpty()) {
			String msg = "No registered device found for user '" + user.getPrimaryEmail() + "'";
			logger.error(method + msg);
			throw new RegistrationNotFoundException(msg);
		}
		
		Pen pen = manufacturingService.findPenByDisplayId(penDisplayId);
		
		// Check if the provided penDisplayId is among the registered pens
		boolean isProvidedPenRegistered = false;
		for (RegisteredDevice regDevice : regDevices) {
			if (pen.getSerialnumber().equals(regDevice.getDeviceSerialNumber())) {
				isProvidedPenRegistered = true;
				break;
			}
		}
		
		// If the provided penDisplayId is not registered, throw exception
		if (!isProvidedPenRegistered) {
			String msg = "The pen '" + penDisplayId + "' is not registered yet.";
			logger.error(method + msg);
			throw new RegistrationNotFoundException(msg);
		}
		
		// Call LSSettingsService Client to save pen name
		savePenNameInDeviceSettings(settingValue, pen.getSerialnumber());
	}
	
	/**
	 * <p></p>
	 * 
	 * @return
	 */
	private Date getEpochDate() {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(1970, Calendar.JANUARY, 1, 0, 0, 1);
		Date epoch = cal.getTime();
		return epoch;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.RegistrationService#resetByEmail(java.lang.String, java.lang.String)
	 */
	@Override
	@Deprecated
	@Transactional("consumer")
	public void resetByEmail(String email) throws UserNotFoundException, RegistrationNotFoundException, DuplicateEmailAddressException {
		
		String method = "resetByEmail():  ";
		
//		UserDTO user = userService.findByEmail(email);
		
		//	The remainder of this method has been commented out to avoid compilation errors.
		//	This method uses a User object to locate a registered device.  Token Service
		//	should not have access to a User object.  Instead, it should make use of its
		//	UserDTO object.  However, the UserDTO object does not provide the 'user_id' value,
		//	which is needed by the CustomRegisteredDeviceDao class.  All of this
		//	needs to be redesigned - if it is still needed.  [KFM - 2013-01-24]
//		List<RegisteredDevice> regDevList = registeredDeviceDao.findByUser(user);

//		for (RegisteredDevice regDev : regDevList) {
//			String deviceSerialNumber = regDev.getDeviceSerialNumber();
			
			// Clear SQS queue
//			logger.debug(method + "clearing sqs queue for deviceId=" + deviceSerialNumber);
//			clearSQSQueue(deviceSerialNumber);
			
//		}
		
		//	TODO:  Use Login Service to delete a User!!!!!
		// delete user cascade register_device, authenticated, user_mail
//		userService.deleteUser(user);
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.RegistrationService#unregister(java.lang.String)
	 */
	@Override
	@Transactional("consumer")
	public boolean unregister(String deviceId) throws RegistrationNotFoundException, MultipleRegistrationsFoundException {
		
		String method = "unregister():  ";
		
		boolean success = false;
		RegisteredDevice regDev = registeredDeviceDao.findBySerialNumber(deviceId);
		
		try {
			registeredDeviceDao.delete(regDev);
//			clearSQSQueue(deviceId);
			success = true;
		}
		catch (HibernateException he) {
			String msg = "HibernateException thrown when attempting to delete RegisteredDevice with device ID '" + deviceId + "'.";
			logger.error(method + msg, he);
		}
		catch (AmazonServiceException ase) {
			String msg = "AmazonServiceException thrown when attempting to delete RegisteredDevice with device ID '" + deviceId + "'.";
			logger.error(method + msg, ase);
		}
		
		return success;
	}
	
	@Transactional("consumer")
	public List<PenDTO> getWiFiPenList(String loginToken) 
			throws UserNotLoggedInException, UserNotFoundException, RegistrationNotFoundException, DeviceNotFoundException, 
			DuplicateSerialNumberException, IOException, InvalidSettingDataException, LSSettingsServiceConnectionException, 
			NoSettingFoundException, NoRegisteredDeviceFoundException, ProcessingErrorException {
		
		String method = "getWiFiPenList():  ";
		
		User user = userService.findUserByLoginToken(loginToken);
		if (user == null) {
			String msg = "Cannot find user with login token '" + loginToken + "'";
			logger.error(method + msg);
			throw new UserNotFoundException(msg);
		}
		
		Set<RegisteredDevice> regDevices = user.getRegisteredDevices();
		if (regDevices == null || regDevices.isEmpty()) {
			String msg = "No registered device found for user '" + user.getPrimaryEmail() + "'";
			logger.error(method + msg);
			throw new RegistrationNotFoundException(msg);
		}
		
		// Create LSSettingsService client
		LSSettingsServiceClient client = new LSSettingsServiceClient();
		
		List<PenDTO> wifiPens = new ArrayList<PenDTO>();
		for (RegisteredDevice rd : regDevices) {
			Pen pen = manufacturingService.lookupDevice(rd.getDeviceSerialNumber());
			String penName = client.getPenName(rd.getDeviceSerialNumber());
			PenDTO penDTO = new PenDTO(pen.getPenType(), penName, pen.getDisplayId());
			wifiPens.add(penDTO);
		}
		
		return wifiPens;
	}
	
	/**
	 * Clear all messages in a queue for a device identified by the given 
	 * serial number.
	 * 
	 * @param serialNumber The serial number of the device whose queue it to be 
	 * cleared.
	 */
	@Deprecated
	private void clearSQSQueue(String serialNumber) {
		
		String method = "clearSQSQueue(): ";
		String acctId = appProperties.getProperty(PROP_AWS_ACCOUNT_ID);		
		String aki = appProperties.getProperty(PROP_AWS_ACCESS_KEY_ID);
		String sk = appProperties.getProperty(PROP_AWS_SECRET_KEY);
		
		BasicAWSCredentials credentials = new BasicAWSCredentials(aki, sk);
		
		AmazonSQSClient client = new AmazonSQSClient(credentials);
		
		String sqsUrl = PROP_SQS_BASE_URL + "/" + acctId + "/" + serialNumber;
		
		// loop until we delete all messages in queue
		try {
			while (checkQueueForMessages(client, sqsUrl) > 0) {
				/* START receiving all messages from queue */
				ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsUrl);
				receiveMessageRequest.setMaxNumberOfMessages(10);
				
				//	Request "All" attributes of the message.
				ArrayList<String> attributeNames = new ArrayList<String>();
				attributeNames.add("All");
				receiveMessageRequest.setAttributeNames(attributeNames);
				
				List<com.amazonaws.services.sqs.model.Message> messages = client.receiveMessage(receiveMessageRequest).getMessages();
				logger.debug(method + "Found " + messages.size() + " message in queue " + sqsUrl);
				/* END receiving all messages from queue */
				
				/* START deleting messages in queue */
				DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest();
				deleteMessageRequest.setQueueUrl(sqsUrl);
				
				for (com.amazonaws.services.sqs.model.Message message : messages) {
					deleteMessageRequest.setReceiptHandle(message.getReceiptHandle());
					client.deleteMessage(deleteMessageRequest);
					logger.debug(method + "Deleted '" + message.getReceiptHandle() + ";");
				}
				/* END deleting messages in queue */
				
			} // while
		} catch (AmazonServiceException ase) {
			logger.error(method + ase.toString());
			
		} catch (AmazonClientException ace) {
			logger.error(method + ace.toString());
		}
	}
	
	/**
	 * Find number of messages in queue
	 * 
	 * @param client
	 * @param queueUrl
	 * @return
	 */
	@Deprecated
	private int checkQueueForMessages(AmazonSQSClient client, String queueUrl) {
		
		if ((queueUrl == null) || ("".equals(queueUrl))) {
			logger.error("checkQueueForMessages():  No queue URL was provided.");
			return -1;
		}
		
		GetQueueAttributesRequest gqaReq = new GetQueueAttributesRequest();
		TreeSet<String> attributeNames = new TreeSet<String>();
		attributeNames.add("ApproximateNumberOfMessages");
		gqaReq.setAttributeNames(attributeNames);
		gqaReq.setQueueUrl(queueUrl);
		
		if (client == null) {
			logger.error("checkQueueForMessages():  Connection to SQS could not be established.");
			return -2;
		}
		
		GetQueueAttributesResult result = client.getQueueAttributes(gqaReq);
		
		Map<String, String> resultMap = result.getAttributes();
		String msgCountStr = resultMap.get("ApproximateNumberOfMessages");
		int msgCount = Integer.parseInt(msgCountStr);
		
		return msgCount;
	}
}

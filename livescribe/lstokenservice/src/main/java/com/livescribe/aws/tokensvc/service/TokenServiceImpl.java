/*
 * Created:  Sep 16, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetFederationTokenRequest;
import com.amazonaws.services.securitytoken.model.GetFederationTokenResult;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import com.livescribe.aws.tokensvc.config.AppConstants;
import com.livescribe.aws.tokensvc.config.AppProperties;
import com.livescribe.aws.tokensvc.crypto.EncryptionUtils;
import com.livescribe.aws.tokensvc.dto.TemporaryCredentialsDTO;
import com.livescribe.aws.tokensvc.exception.DeviceAlreadyRegisteredException;
import com.livescribe.aws.tokensvc.exception.DuplicateSerialNumberException;
import com.livescribe.aws.tokensvc.exception.MultipleRegistrationsFoundException;
import com.livescribe.aws.tokensvc.exception.RegistrationNotFoundException;
import com.livescribe.aws.tokensvc.orm.consumer.CustomRegisteredDeviceDao;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.aws.tokensvc.orm.consumer.RegisteredDeviceFactory;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.consumer.UserDao;
import com.livescribe.aws.tokensvc.response.CredentialResponse;
import com.livescribe.aws.tokensvc.response.ErrorResponse;
import com.livescribe.aws.tokensvc.response.RegistrationResponse;
import com.livescribe.aws.tokensvc.response.ResponseCode;
import com.livescribe.aws.tokensvc.response.ServiceResponse;
import com.livescribe.aws.tokensvc.response.UnregistrationResponse;
import com.livescribe.aws.tokensvc.util.PolicyGenerator;
import com.livescribe.framework.orm.manufacturing.Pen;

/**
 * <p>Default implementation of a {@link com.livescribe.aws.tokensvc.service.TokenService}.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 * @see com.livescribe.aws.tokensvc.service.TokenService
 */
public class TokenServiceImpl implements TokenService, AppConstants {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private AuthorizationService authorizationService;
	
//	@Autowired
//	private ManufacturingService manufacturingService;
	
	@Autowired
	private PolicyGenerator policyGenerator;
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private EncryptionUtils encryptionUtils;
	
	@Autowired
	private CustomRegisteredDeviceDao registeredDeviceDao;

	@Autowired
	private UserDao userDao;

	/**
	 * <p>Default class constructor.</p>
	 * 
	 */
	public TokenServiceImpl() {
	}

	/**
	 * <p>Decrypts the given registration token.</p>
	 * 
	 * Decodes the given <code>String</code> from hex string into a 
	 * <code>byte[]</code> using the Commons-Codec <code>Hex</code> class 
	 * before performing the decryption.
	 * 
	 * @param regToken An encyrpted registration token encoded as a 
	 * hexidecimal <code>String</code>.
	 * 
	 * @return the unencrypted registration token (i.e. <code>deviceId + &quot;:&quot; + userId</code>)
	 */
	public String decryptRegistrationToken(String regToken) {
		
		String method = "decryptRegistrationToken():  ";
		
		byte[] regTokenBytes = null;
		
		//	Decode String into byte[].
		try {
			Hex hex = new Hex();
			regTokenBytes = (byte[])hex.decode(regToken);
//			guid = (byte[])hex.decode(guidStr);
			logger.debug(method + "Decoded (hex) token size = " + regTokenBytes.length);
		}
		catch (DecoderException de) {
			logger.error(method + "DecoderException thrown when attempting to decode given guid string: " + regToken, de);
			return null;
		}
		byte[] decrypted = encryptionUtils.decrypt(regTokenBytes);
		String decryptedStr = new String(decrypted);
		
		logger.debug(method + "decryptedStr token: " + decryptedStr);
		
		return decryptedStr;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.TokenService#getSecureToken()
	 */
	@Override
	@Transactional("consumer")
	public TemporaryCredentialsDTO getAwsCredentials(String regToken) throws RegistrationNotFoundException {
		
		String method = "getAwsCredentials():  ";
		
//		if ((regToken == null) || ("".equals(regToken))) {
//			return null;
//		}
		
		//	If the request is not a 'registered' request, deny access.
//		RegisteredDevice regDev = null;
//		try {
//			regDev = registeredDeviceDao.findByRegistrationToken(regToken);
//		}
//		catch (MultipleRegistrationsFoundException mrfe) {
//			String msg = "More than one registration record was found using token '" + regToken + "'.";
//			logger.error(method + msg, mrfe);
			//	TODO:  Need to do more here!
//			return null;
//		}
		
//		if (regDev == null) {
//			String msg = "Registration record could not be found with token '" + regToken + "'.";
//			throw new RegistrationNotFoundException(msg);
//		}
		
		//	Decrypt the registration token.
//		String guidStr = decryptRegistrationToken(regToken);
//		
//		String[] guidArray = guidStr.split(":");
//		String penSerial = guidArray[0];
//		String userId = guidArray[1];
		
		String aki = this.appProperties.getProperty(PROP_AWS_ACCESS_KEY_ID);
		String sk = this.appProperties.getProperty(PROP_AWS_SECRET_KEY);
		
		AWSCredentials credentials = new BasicAWSCredentials(aki, sk);
		AWSSecurityTokenServiceClient client = new AWSSecurityTokenServiceClient(credentials);
		
		GetSessionTokenRequest request = new GetSessionTokenRequest();
		request.setDurationSeconds(3600);
		request.setRequestCredentials(credentials);
		
		GetSessionTokenResult result = client.getSessionToken(request);
		Credentials creds = result.getCredentials();
		TemporaryCredentialsDTO tempCredentials = new TemporaryCredentialsDTO(creds);
		
		return tempCredentials;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.TokenService#getCredentialsForDataMetrics()
	 */
	@Override
	public TemporaryCredentialsDTO getCredentialsForDataMetrics() {
		
		//	Construct AWS credentials for 'token_service_user'.
		String aki = appProperties.getProperty(PROP_AWS_ACCESS_KEY_ID);
		String sk = appProperties.getProperty(PROP_AWS_SECRET_KEY);
		BasicAWSCredentials credentials = new BasicAWSCredentials(aki, sk);
				
		//	Build the STS request.
		GetFederationTokenRequest getFedTokenRequest = new GetFederationTokenRequest();
		getFedTokenRequest.setDurationSeconds(3600);
		String name = appProperties.getProperty(PROP_QUEUE_DATAMETRICS_NAME);
		getFedTokenRequest.setName(name);
		
		//	Set the Policy.
		String policyString = policyGenerator.generateDataMetricsPolicy(aki, sk);
		getFedTokenRequest.setPolicy(policyString);
		
		//	Issue the request.
		AWSSecurityTokenServiceClient stsClient = new AWSSecurityTokenServiceClient(credentials);
		GetFederationTokenResult getFedTokenResult = stsClient.getFederationToken(getFedTokenRequest);
		Credentials stsCredentials = getFedTokenResult.getCredentials();

		TemporaryCredentialsDTO tempCreds = new TemporaryCredentialsDTO(stsCredentials);
		
		return tempCreds;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.TokenService#getCredentialsForPortalCaptureWithDeviceId(java.lang.String)
	 */
	@Override
	public TemporaryCredentialsDTO getCredentialsForPortalCaptureWithDeviceId(String deviceId) {
		
		//	Construct AWS credentials for 'token_service_user'.
		String aki = appProperties.getProperty(PROP_AWS_ACCESS_KEY_ID);
		String sk = appProperties.getProperty(PROP_AWS_SECRET_KEY);
		BasicAWSCredentials credentials = new BasicAWSCredentials(aki, sk);
				
		//	Build the STS request.
		GetFederationTokenRequest getFedTokenRequest = new GetFederationTokenRequest();
		getFedTokenRequest.setDurationSeconds(3600);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd-hhMMss");
		String dateString = sdf.format(now);
		getFedTokenRequest.setName("PortalCapture-" + dateString);
		
		String policyString = policyGenerator.generatePortalCapturePolicy(aki, sk);
		getFedTokenRequest.setPolicy(policyString);
		
		//	Issue the request.
		AWSSecurityTokenServiceClient stsClient = new AWSSecurityTokenServiceClient(credentials);
		GetFederationTokenResult getFedTokenResult = stsClient.getFederationToken(getFedTokenRequest);
		Credentials stsCredentials = getFedTokenResult.getCredentials();

		TemporaryCredentialsDTO tempCreds = new TemporaryCredentialsDTO(stsCredentials);
		
		return tempCreds;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.TokenService#getCredentialsForPortalCapture(java.lang.String)
	 */
	@Override
	public TemporaryCredentialsDTO getCredentialsForPortalCapture(String regToken) {
		
		//	TODO:  Add regToken as part of the generated policy.
		//	e.g.  /lspencrashdumpbucketdev/< regToken >/ ...
		
		//	Construct AWS credentials for 'token_service_user'.
		String aki = appProperties.getProperty(PROP_AWS_ACCESS_KEY_ID);
		String sk = appProperties.getProperty(PROP_AWS_SECRET_KEY);
		BasicAWSCredentials credentials = new BasicAWSCredentials(aki, sk);
				
		//	Build the STS request.
		GetFederationTokenRequest getFedTokenRequest = new GetFederationTokenRequest();
		getFedTokenRequest.setDurationSeconds(3600);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd-hhMMss");
		String dateString = sdf.format(now);
		getFedTokenRequest.setName("PortalCapture-" + dateString);
		
		String policyString = policyGenerator.generatePortalCapturePolicy(aki, sk);
		getFedTokenRequest.setPolicy(policyString);
		
		//	Issue the request.
		AWSSecurityTokenServiceClient stsClient = new AWSSecurityTokenServiceClient(credentials);
		GetFederationTokenResult getFedTokenResult = stsClient.getFederationToken(getFedTokenRequest);
		Credentials stsCredentials = getFedTokenResult.getCredentials();

		TemporaryCredentialsDTO tempCreds = new TemporaryCredentialsDTO(stsCredentials);
		
		return tempCreds;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.TokenService#getCredentialsForUser(java.lang.String)
	 */
	@Override
	public TemporaryCredentialsDTO getCredentialsForUser(String regToken) {
		
		String decrypted = decryptRegistrationToken(regToken);
		String[] parts = decrypted.split(":");
		Long uid = Long.parseLong(parts[1]);
		User user = userDao.findById(uid);
		
		return null;
	}
}

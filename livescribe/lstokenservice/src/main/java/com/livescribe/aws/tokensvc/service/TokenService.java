/*
 * Created:  Sep 15, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import com.livescribe.aws.tokensvc.dto.TemporaryCredentialsDTO;
import com.livescribe.aws.tokensvc.exception.DeviceAlreadyRegisteredException;
import com.livescribe.aws.tokensvc.exception.DeviceNotFoundException;
import com.livescribe.aws.tokensvc.exception.DuplicateSerialNumberException;
import com.livescribe.aws.tokensvc.exception.RegistrationNotFoundException;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.aws.tokensvc.response.ServiceResponse;
import com.livescribe.framework.orm.manufacturing.Pen;

/**
 * <p>Provides temporary AWS credentials for use in accessing AWS resources
 * for various purposes.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface TokenService {

	/**
	 * <p></p>
	 * 
	 * @param registrationToken
	 * 
	 * @return
	 */
	public String decryptRegistrationToken(String registrationToken);
	
	/**
	 * <p></p>
	 * 
	 * @param registrationToken
	 * 
	 * @return
	 * @throws RegistrationNotFoundException 
	 */
	public TemporaryCredentialsDTO getAwsCredentials(String registrationToken) throws RegistrationNotFoundException;
	
	/**
	 * <p>Returns temporary AWS credentials allowing write access to the
	 * SQS queue used for collecting data metrics.</p>
	 * 
	 * @return an AWS Access Key ID, Secret Key, and Session Token.
	 */
	public TemporaryCredentialsDTO getCredentialsForDataMetrics();
	
	/**
	 * <p></p>
	 * 
	 * @param deviceId
	 * @return
	 */
	public TemporaryCredentialsDTO getCredentialsForPortalCaptureWithDeviceId(String deviceId);
	
	public TemporaryCredentialsDTO getCredentialsForPortalCapture(String regToken);
	
	/**
	 * <p>Returns temporary AWS credentials allowing access to a user&apos;s
	 * content.</p>
	 * 
	 * No validation of the registration token is made.  Callers are expected
	 * to provide a valid registration token.
	 * 
	 * @param regToken The registration token identifying the user for whom 
	 * to allow access.
	 * 
	 * @return an AWS Access Key ID, Secret Key, and Session Token.
	 */
	public TemporaryCredentialsDTO getCredentialsForUser(String regToken);
}

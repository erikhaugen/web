/*
 * Created:  Nov 28, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetFederationTokenRequest;
import com.amazonaws.services.securitytoken.model.GetFederationTokenResult;
import com.livescribe.aws.tokensvc.config.AppConstants;
import com.livescribe.aws.tokensvc.config.AppProperties;
import com.livescribe.framework.orm.manufacturing.Pen;

/**
 * <p>Encapsulates logic to generate IAM policies and generate temporary
 * credentials from them.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Deprecated
public class AuthorizationServiceImpl implements AppConstants, AuthorizationService {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private AppProperties appProperties;
	
	/**
	 * <p>Default class constructor.</p>
	 * 
	 */
	public AuthorizationServiceImpl() {}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.AuthorizationService#authorizePenAccessToQueue(com.livescribe.framework.orm.manufacturing.Pen)
	 */
	@Override
	public Credentials authorizePenAccessToQueue(Pen pen) {
		
		String method = "authorizePenAccessToQueue():  ";
	
		if (pen == null) {
			//	TODO:  Handle 'null' pen.
		}
		
		//	Construct AWS credentials for 'token_service_user'.
		String aki = appProperties.getProperty(PROP_AWS_ACCESS_KEY_ID);
		String sk = appProperties.getProperty(PROP_AWS_SECRET_KEY);
		BasicAWSCredentials credentials = new BasicAWSCredentials(aki, sk);
				
		//	Build the STS request.
		GetFederationTokenRequest getFedTokenRequest = new GetFederationTokenRequest();
		getFedTokenRequest.setDurationSeconds(3600);
		String name = pen.getDisplayId();
		getFedTokenRequest.setName(name);
		
		//	Construct the IAM policy.
		String serialNumber = pen.getSerialnumber();
		String policyDocument = constructIAMPolicyForSQSQueue(serialNumber);
		logger.debug(method + policyDocument);
		getFedTokenRequest.setPolicy(policyDocument);
		
		//	Issue the request.
		AWSSecurityTokenServiceClient stsClient = new AWSSecurityTokenServiceClient(credentials);
		GetFederationTokenResult getFedTokenResult = stsClient.getFederationToken(getFedTokenRequest);
		Credentials stsCredentials = getFedTokenResult.getCredentials();
		
		return stsCredentials;
	}

	/**
	 * <p>Constructs an IAM policy document for use in defining access 
	 * privileges to an SQS queue.</p>
	 * 
	 * @param serialNumber The decimal, 64-bit serial number of a pen. 
	 * 
	 * @return an IAM policy &quot;document&quot;.
	 */
	private String constructIAMPolicyForSQSQueue(String serialNumber) {
		
		String acctId = appProperties.getProperty(PROP_AWS_ACCOUNT_ID);
		
		StringBuilder builder = new StringBuilder();
		builder.append("{\"Statement\":[{\"Action\":[\"sqs:ReceiveMessage\", \"sqs:DeleteMessage\"], \"Effect\":\"Allow\",");
		builder.append("\"Resource\":\"arn:aws:sqs:*:" + acctId + ":" + serialNumber + "\"}]}");

		return builder.toString();
	}
	
	public Credentials authorizeAccessToAWSResource() {
		
		return null;
	}
	
	public Credentials authorizeUserAccessToAWSResources() {
		
		return null;
	}
}

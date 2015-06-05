/**
 * 
 */
package com.livescribe.aws.tokensvc.util;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetFederationTokenRequest;
import com.amazonaws.services.securitytoken.model.GetFederationTokenResult;
import com.livescribe.aws.tokensvc.BaseTest;
import com.livescribe.aws.tokensvc.config.AppConstants;
import com.livescribe.aws.tokensvc.config.AppProperties;
import com.livescribe.aws.tokensvc.service.TokenService;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PolicyGeneratorTest extends BaseTest implements AppConstants {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
	private String TEST_FILE_NAME = "S3-Test-File.txt";
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private PolicyGenerator policyGenerator;
	
	@Autowired
	private TokenService tokenService;
	
	private String aki;
	private String sk;
	private String s3Bucket;
	private File testFile;
	
	/**
	 * <p></p>
	 *
	 */
	public PolicyGeneratorTest() {
		super();
	}

	@Before
	public void setUp() {
		
		this.aki = appProperties.getProperty(PROP_AWS_ACCESS_KEY_ID);
		this.sk = appProperties.getProperty(PROP_AWS_SECRET_KEY);
		this.s3Bucket = appProperties.getProperty(PROP_S3_BUCKET_PEN_CRASH_DUMP);
		
		URL url = getClass().getClassLoader().getResource(TEST_FILE_NAME);
		String path = url.getPath();
		this.testFile = new File(path);
	}
	
	@Test
	public void testGenerateDataMetricsPolicy() {
		
		String policy = policyGenerator.generateDataMetricsPolicy(this.aki, this.sk);
		assertNotNull("The returned policy document was 'null'.", policy);
		
		String prettyPolicy = policyGenerator.prettyPrintPolicyString(policy);
		logger.debug("\n" + prettyPolicy);
	}
	
	@Test
	public void testGeneratePortalCapturePolicy() {
		
		String method = "testGeneratePortalCapturePolicy():  ";
		
		String policy = policyGenerator.generatePortalCapturePolicy(aki, sk);
		assertNotNull("The returned policy document was 'null'.", policy);
		
		String prettyPolicy = policyGenerator.prettyPrintPolicyString(policy);
		logger.debug("\n" + prettyPolicy);
		
		BasicAWSCredentials credentials = new BasicAWSCredentials(aki, sk);
		
		
		Credentials stsCredentials = getSTSCredentials(policy, credentials);

		BasicSessionCredentials basicCredentials = new BasicSessionCredentials(stsCredentials.getAccessKeyId(), stsCredentials.getSecretAccessKey(), stsCredentials.getSessionToken());
		
		try {
			PutObjectResult rslt = putObjectToS3(basicCredentials);
			String eTag = rslt.getETag();
			assertNotNull("The returned ETag was 'null'.", eTag);
		}
		catch (Exception e) {
			e.printStackTrace();
			assertFalse("An exception was thrown when attempting to put an object on S3.", true);
		}		
	}

	/**
	 * <p>Puts an object on S3 using the given credentials.</p>
	 * 
	 * @param basicCredentials
	 * 
	 * @return
	 * 
	 * @throws AmazonClientException
	 * @throws AmazonServiceException
	 */
	private PutObjectResult putObjectToS3(BasicSessionCredentials basicCredentials) throws AmazonClientException, AmazonServiceException {
		
		AmazonS3Client s3Client = new AmazonS3Client(basicCredentials);
		
		Date now = new Date();
		String dateStr = sdf.format(now);
		
		String key = "PolicyGeneratorTest_" + dateStr;
		logger.debug("Putting '" + this.testFile.getAbsolutePath() + "' in S3 bucket '" + this.s3Bucket + "' as '" + key + "'.");
		PutObjectResult rslt = s3Client.putObject(this.s3Bucket, key, this.testFile);
		return rslt;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param policy
	 * @param credentials
	 */
	private Credentials getSTSCredentials(String policy, BasicAWSCredentials credentials) {
		
		//	Build the STS request.
		GetFederationTokenRequest getFedTokenRequest = new GetFederationTokenRequest();
		getFedTokenRequest.setDurationSeconds(3600);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd-hhMMss");
		String dateString = sdf.format(now);
		getFedTokenRequest.setName("PortalCapture-" + dateString);
		getFedTokenRequest.setPolicy(policy);
		
		//	Issue the request.
		AWSSecurityTokenServiceClient stsClient = new AWSSecurityTokenServiceClient(credentials);
		GetFederationTokenResult getFedTokenResult = stsClient.getFederationToken(getFedTokenRequest);
		Credentials stsCredentials = getFedTokenResult.getCredentials();
		
		return stsCredentials;
	}
	
//	@Test
	public void testGenerateUserPolicy() {
		
		String policy = policyGenerator.generateUserPolicy("", "");
	}

//	@Test
	public void testGenerateUserPolicy_FailWithEmptyParameter() {
		
		String policy = policyGenerator.generateUserPolicy("", "");
	}

//	@Test
	public void testGenerateUserPolicy_FailWithInvalidParameters() {
		
		String decrypted = tokenService.decryptRegistrationToken("845a43647d3f01a1ff11eb41fb9475cd");
		String[] parts = decrypted.split(":");
		
		String policy = policyGenerator.generateUserPolicy(parts[1], parts[0]);
		
		
	}
}

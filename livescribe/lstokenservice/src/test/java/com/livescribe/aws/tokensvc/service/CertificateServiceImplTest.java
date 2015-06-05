/*
 * Created:  Nov 14, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import static junit.framework.Assert.*;
import java.io.File;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.tokensvc.BaseTest;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CertificateServiceImplTest extends BaseTest {

	private static String TEST_CERTIFICATE_FILE = "/src/test/resources/cert/tknsvc.crt";
	
	@Autowired
	private CertificateService certificateService;
	
	/**
	 * <p></p>
	 * 
	 */
	public CertificateServiceImplTest() {
		super();
	}

	/**
	 * <p></p>
	 * 
	 * @throws Exception
	 */
	@Test
	@Transactional("manufacturing")
	public void testReadCertificateFromFile() throws Exception {
		
		String dir = System.getProperty("user.dir");
		
		File certFile = new File(dir + TEST_CERTIFICATE_FILE);
		X509Certificate cert = (X509Certificate)certificateService.readCertificateFromFile(certFile);
		assertNotNull("The returned certificate was 'null'.", cert);
		cert.checkValidity();
		byte[] encodedCert = cert.getEncoded();
		String certStr = Hex.encodeHexString(encodedCert);
		logger.debug("Hex-encoded certificate string:  " + certStr);
		
		PublicKey publicKey = cert.getPublicKey();
		byte[] encodedPubKey = publicKey.getEncoded();
		String pubKeyStr = Hex.encodeHexString(encodedPubKey);
		logger.debug("Hex-encoded public key string:  " + pubKeyStr);
	}
}

/*
 * Created:  Sep 15, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.crypto;

import static junit.framework.Assert.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.aws.tokensvc.BaseTest;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EncryptionUtilsTest extends BaseTest {

	@Autowired
	private EncryptionUtils encryptionUtils;
	
	/**
	 * <p></p>
	 * 
	 */
	public EncryptionUtilsTest() {
		super();
	}

	@Test
	public void testEncryptDecrypt() {
		
		byte[] encrypted = encryptionUtils.encrypt("Kevin Murdoff");
		assertNotNull("The returned encrypted byte[] was 'null'.", encrypted);
		assertTrue("The returned encrypted byte[] was empty.", encrypted.length > 0);
		
		String encStr = Hex.encodeHexString(encrypted);
		logger.debug("encStr: " + encStr);
		
//		System.out.println("encrypted byte count: " + encrypted.length);
		
//		boolean isBase64Array = Base64.isBase64(encrypted);
//		assertFalse("The returned encoded byte[] had valid Base64 characters.", isBase64Array);
//		String encStr = Base64.encodeBase64String(encrypted);
//		System.out.println(encStr);
		
//		byte[] encBytes = Base64.decodeBase64(encStr);
		
		byte[] decrypted = encryptionUtils.decrypt(encrypted);
		assertNotNull("The returned decrypted byte[] was 'null'.", decrypted);
		
		String decStr = new String(decrypted);
		assertEquals("The string version of the decrypted bytes was not expected.", "Kevin Murdoff", decStr);
	}
	
//	@Test
	public void testEncryptDecryptAES() {
		
		byte[] encrypted = encryptionUtils.encrypt("Kevin Murdoff");
		assertNotNull("The returned encrypted byte[] was 'null'.", encrypted);
		assertTrue("The returned encrypted byte[] was empty.", encrypted.length > 0);

		byte[] decrypted = encryptionUtils.decrypt(encrypted);
		assertNotNull("The returned decrypted byte[] was 'null'.", decrypted);
		
		String decStr = new String(decrypted);
		assertEquals("The string version of the decrypted bytes was not expected.", "Kevin Murdoff", decStr);
	}
	
	@Test
	public void testHashValueFor() throws Exception {
		
		String penSerialNumber = "1743756976658";
		StringBuilder builder = encryptionUtils.hashValueFor(penSerialNumber);
		String hash = builder.toString();
		assertEquals("", "173/98/34", hash);
	}
}

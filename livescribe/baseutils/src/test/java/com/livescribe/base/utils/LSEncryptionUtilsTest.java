package com.livescribe.base.utils;

import junit.framework.TestCase;

public class LSEncryptionUtilsTest extends TestCase {

	static {
		System.setProperty("keyFilePath", "/keys.xml");
	}
	
	public void testEncryptionDecryption() throws Exception {
		String text = "This is a test string";
		
		String encryptedString = LSEncryptionUtils.encrypt(text);
		
		String decryptedString = LSEncryptionUtils.decrypt(encryptedString);
		
		assertEquals(text, decryptedString);
		
		String encryt2 = LSEncryptionUtils.encrypt(decryptedString);
		assertEquals(encryptedString, encryt2);
	}
}

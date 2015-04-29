package com.livescribe.base.utils;

import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.livescribe.base.constants.LSConstants;

public class MiscEncryptionUtils {

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	public static String getBase64HMacSha1Signature(String stringToSign, String secretKey)
	throws SignatureException {
		String result;
		try {

			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA1_ALGORITHM);

			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			// Get utf-8 bytes for the string
			byte[] utf8Bytes = stringToSign.getBytes(LSConstants.ENCODING_UTF8);
			
			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(utf8Bytes);

			// base64-encode the hmac
			result = Base64.encodeToString(rawHmac, false);

		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}
	
	public static void main(String[] argv) throws Exception {
		String secretKey = "9jre7pJsTThOqAqJJgcfinn/5YsN79B/t8YOUa7x";
		
		//System.out.println("Sig =" + getBase64HMacSha1Signature("smukker@ls.comen-us0112341.1servicebrowser", secretKey));
		
		String emailPrefix = "jmetertestuser+";
		String emailSuffix = "@livescribe.com";
		String culture="en-US";
		
		String guidPrefix = "0ef7-6785-6aeb-4215-";
		String version = "1.1";
		String clientApp = "jmeter";
		
		for ( int i = 0; i < 500; i++ ) {
			String email = emailPrefix + i + emailSuffix;
			String guid = guidPrefix + i;
			String identityHash = getBase64HMacSha1Signature(email + culture + guid + version + clientApp, secretKey);
			System.out.println ( email + "," + culture + "," + guid + "," + version + "," + clientApp + "," + identityHash) ;
		}
	}
}

/**
 * Created:  Nov 15, 2013 3:28:45 PM
 */
package com.livescribe.web.tools.webteamtool.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EncryptionUtils {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String HMAC_SHA1_ALGORITHM 	= "HmacSHA1";
	private static final String CHAR_ENCODING			= "UTF-8";
	private static final String PROP_CRYPTO_PASSWORD	= "l3tm31n";
	private static final String LSUSERPATHALGORITHM		= "crc32";
	
	/**
	 * <p></p>
	 * 
	 */
	public EncryptionUtils() {
	}

	/**
	 * <p>Converts the given bytes into a Base64 encoded string.</p>
	 * 
	 * @param bytes The given bytes to encode.
	 * 
	 * @return a Base64 encoded string.
	 */
	public String encodeToBase64(byte[] bytes) {
		
		String encoded = Base64.encodeBase64URLSafeString(bytes);
		
		return encoded;
	}

	/**
	 * <p></p>
	 * 
	 * @param stringToSign
	 * 
	 * @return
	 * 
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public String generateBase64EncodedSHAHash(String stringToSign) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		byte[] hashedBytes = generateSHAHash(stringToSign);
		String encoded = encodeToBase64(hashedBytes);
		
		return encoded;
	}

	/**
	 * <p>Generates a SHA-1 hash of the given string.</p>
	 * 
	 * @param stringToSign The string to hash.
	 * 
	 * @return a SHA-1 hash of the given string.
	 * 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws UnsupportedEncodingException 
	 */
	public byte[] generateSHAHash(String stringToSign) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		
		SecretKeySpec signingKey = new SecretKeySpec(PROP_CRYPTO_PASSWORD.getBytes(), HMAC_SHA1_ALGORITHM);
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		byte[] utf8Bytes = stringToSign.getBytes(CHAR_ENCODING);
		byte[] rawHmac = mac.doFinal(utf8Bytes);

		return rawHmac;
	}
	
	/**
	 * <p>Generates a SHA hash used in the Web Objects / FrontBase system.</p>
	 * 
	 * @param plaintext
	 * 
	 * @return
	 * 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 */
	public synchronized String generateFrontBaseHash(String plaintext) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		MessageDigest md = MessageDigest.getInstance("SHA");
		md.update(plaintext.getBytes("UTF-8"));
		byte raw[] = md.digest();
		String hash = Base64.encodeBase64String(raw);
		return hash;
	}

	/**
	 * <p></p>
	 * 
	 * @param userKey The primary key of the user&apos;s Livescribe account
	 * in FrontBase.
	 * 
	 * @return
	 */
	public StringBuilder hashValueFor(String userKey) {
		
		try { 
			AbstractChecksum checksum = null;
			
			//	Select an algorithm (md5 in this case) 
			checksum = JacksumAPI.getChecksumInstance(LSUSERPATHALGORITHM); 
			checksum.reset();
			checksum.update(userKey.getBytes("UTF8"));
			// Let's compute the path now based on the checksum we got
			byte digest[] = checksum.getByteArray();
			int p1 = (int) digest[0] & 0xFF;
			int p2 = ((int) digest[1] & 0xFF) & 0x63;
			int p3 = ((int) digest[2] & 0xFF) & 0x63;
			StringBuilder b = new StringBuilder(16);
			b.append(p1);
			b.append('/');
			if (p2 < 10) b.append('0');
			b.append(p2);
			b.append('/');
			if (p3 < 10) b.append('0');
			b.append(p3);
			return b;
		} catch (java.io.UnsupportedEncodingException ioe) {
			throw new RuntimeException(ioe);
		} catch (NoSuchAlgorithmException nsae) { 
			// algorithm doesn't exist 
			throw new RuntimeException(nsae);
		}
	}
}

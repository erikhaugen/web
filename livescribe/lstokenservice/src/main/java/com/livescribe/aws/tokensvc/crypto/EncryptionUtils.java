/**
 * 
 */
package com.livescribe.aws.tokensvc.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.aws.tokensvc.config.AppConstants;
import com.livescribe.aws.tokensvc.config.AppProperties;

/**
 * @author kmurdoff
 *
 */
public final class EncryptionUtils implements AppConstants {

	private static Logger logger = Logger.getLogger(EncryptionUtils.class.getName());
	
	private static final String CHECKSUM_ALGORITHM	="crc32";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private static final String KEY_ALGORITHM		= "AES";
	private static final String CIPHER_ALGORITHM	= "AES/CBC/PKCS5Padding";
	
	private static final byte[] KEY_VALUE			= new byte[] {'l', 's', 't', '0', 'k', '3', 'n', 'k', '3', '3', 'p', '3', 'r', 'k', '3', 'y'};
	private static final int KEY_LENGTH				= 128;
	private static final String CHAR_ENCODING		= "UTF-8";
	
	@Autowired
	private AppProperties appProperties;
	
	private SecretKey aesSecretKey;
	private Cipher aesCipher;
	
	private Cipher cipher;

	//	Password-based encryption properties.
	private static byte[] salt;
	private static int iterations;
	
	/**
	 * @throws NoSuchAlgorithmException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeyException 
	 */
	public EncryptionUtils() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		
		aesSecretKey = (SecretKey)generateKey();
		aesCipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		
		logger.info("Constructed");
	}

	/**
	 * <p></p>
	 * 
	 * @param appProperties
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 */
	public EncryptionUtils(AppProperties appProperties) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		
		this.appProperties = appProperties;
		aesSecretKey = (SecretKey)generateKey();
		aesCipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		
		logger.info("Constructed");
	}
	
	/**
	 * <p>Encrypts the given string into an array of bytes.</p>
	 * 
	 * Uses an AES key.
	 * 
	 * @param plainText The text to encrypt.
	 * 
	 * @return an array of encrypted bytes.
	 */
	public byte[] encrypt(String plainText) {
		
		String method = "encrypt():  ";

		byte[] encrypted = null;
		
		try {
			byte[] textBytes = plainText.getBytes("UTF-8");
			aesCipher.init(Cipher.ENCRYPT_MODE, aesSecretKey);			
			encrypted = aesCipher.doFinal(textBytes);
		}
		catch (UnsupportedEncodingException uee) {
			logger.error(method + "UnsupportedEncodingException thrown when encrypting String.", uee);
		}
		catch (InvalidKeyException ike) {
			logger.error(method + "InvalidKeyException thrown when initializing Cipher.", ike);
		}
		catch (IllegalBlockSizeException ibse) {
			logger.error(method + "IllegalBlockSizeException thrown when encrypting String.", ibse);
		}
		catch (BadPaddingException bpe) {
			logger.error(method + "BadPaddingException thrown when encrypting String.", bpe);
		}
		
		return encrypted;
	}
	
	/**
	 * <p>Decrypts the given array of bytes.</p>
	 * 
	 * @param encrypted The array of bytes to decrypt.
	 * 
	 * @return an array of decrypted bytes.
	 */
	public byte[] decrypt(byte[] encrypted) {
		
		String method = "decrypt():  ";

		byte[] decrypted = null;
		
		try {
			aesCipher.init(Cipher.DECRYPT_MODE, aesSecretKey);
			decrypted = aesCipher.doFinal(encrypted);
		}
		catch (InvalidKeyException ike) {
			logger.error(method, ike);
		}
		catch (IllegalBlockSizeException ibse) {
			logger.error(method, ibse);
		}
		catch (BadPaddingException bpe) {
			logger.error(method, bpe);
		}

		return decrypted;
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

	private Key generateKey() {
		
		Key key = new SecretKeySpec(KEY_VALUE, KEY_ALGORITHM);
		return key;
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
		
		String secretKey = appProperties.getProperty(PROP_CRYPTO_PASSWORD);
		SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA1_ALGORITHM);
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		byte[] utf8Bytes = stringToSign.getBytes(CHAR_ENCODING);
		byte[] rawHmac = mac.doFinal(utf8Bytes);

		return rawHmac;
	}
	
	/**
	 * <p></p>
	 * 
	 * Copied from LSFoundation project in class com.livescribe.foundation.HashUtil.
	 * Using the convenient checksum implementation from : http://www.jonelo.de/java/jacksum
	 * 
	 * Inside hashed directory:
	 * pen.crt
	 * pen.csr
	 * pen.key
	 * pen.p12
	 * 
	 * Created by Stephane Lunati on 1/7/08.
	 * Copyright 2008 LiveScribe Inc. All rights reserved.
	 * 
	 * @param userKey
	 * 
	 * @return
	 */
	public StringBuilder hashValueFor(String userKey) {
		
		try { 
			AbstractChecksum checksum = null; 
			// select an algorithm (md5 in this case) 
			checksum = JacksumAPI.getChecksumInstance(CHECKSUM_ALGORITHM); 
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
			if (p2 < 10) {
				b.append('0');
			}
			b.append(p2);
			b.append('/');
			if (p3 < 10) {
				b.append('0');
			}
			b.append(p3);
			return b;
		}
		catch (java.io.UnsupportedEncodingException ioe) {
			throw new RuntimeException(ioe);
		}
		catch (NoSuchAlgorithmException nsae) { 
			// algorithm doesn't exist 
			throw new RuntimeException(nsae);
		}
	}
	
	/**
	 * <p></p>
	 * 
	 * @return the appProperties
	 */
	public AppProperties getAppProperties() {
		return appProperties;
	}

	/**
	 * <p></p>
	 * 
	 * @param appProperties the appProperties to set
	 */
	public void setAppProperties(AppProperties appProperties) {
		this.appProperties = appProperties;
	}

}

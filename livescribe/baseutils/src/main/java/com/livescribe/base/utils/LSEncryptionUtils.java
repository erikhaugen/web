package com.livescribe.base.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Please ** DONOT use this class yet ** Any string/text encrypted using this
 * will be unrecoverable as the salt, password key etc will change
 * 
 * @author smukker
 *
 */
public final class LSEncryptionUtils {
	
	// Salt
    private static byte[] salt;
    
    private static char[] pwdKey;

    // Iteration count
    private static int count = 20;
	
    // Create PBE parameter set
    private static PBEParameterSpec pbeParamSpec;

    // create PBE Key Spec
    private static PBEKeySpec pbeKeySpec;
	
    private static SecretKeyFactory keyFac;
    private static SecretKey pbeKey;
    
    // PBE  Cipher for encryption
    private static Cipher pbeEncryptCipher;

    // PBE  Cipher for decryption
    private static Cipher pbeDecryptCipher;
    
    static {
    	init();
    }
    
    private static final void init() {
    	try {
    		
    		String keyFilePath = System.getProperty("keyFilePath", "/Livescribe/LSConfiguration/keys.xml");
    		
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = null;
			
			File keyFile = new File(keyFilePath);

    		if ( !keyFile.exists() ) {
    			InputStream is = LSEncryptionUtils.class.getResourceAsStream(keyFilePath);
    			if ( is != null ) {
    				doc = db.parse(is);
    			}
    		} else {
    			doc = db.parse(keyFile);
    		}
    		
    		
    		if ( doc != null ) {
    			
    			Element keysElement = doc.getDocumentElement();
    			
    			salt = keysElement.getElementsByTagName("salt").item(0).getTextContent().getBytes();
    			
    			pwdKey = keysElement.getElementsByTagName("pwdKey").item(0).getTextContent().toCharArray();
    			
    			//keysElement.n

    			pbeParamSpec = new PBEParameterSpec(salt, count);
	    		
	    		pbeKeySpec = new PBEKeySpec(pwdKey);
	    		
	    		keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
	    		pbeKey = keyFac.generateSecret(pbeKeySpec);
	    		pbeEncryptCipher = Cipher.getInstance("PBEWithMD5AndDES");
	    		// Initialize PBE Cipher with key and parameters
	    		pbeEncryptCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
	    		
	    		pbeDecryptCipher = Cipher.getInstance("PBEWithMD5AndDES");
	    		// Initialize PBE Cipher with key and parameters
	    		pbeDecryptCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
    		} else {
    			throw new FileNotFoundException("Cannot find keys.xml file in the expected location");
    		}
    	} catch ( Exception ex ) {
    		System.out.println("Unable to initialize the Security Keys. Exiting the Java VM ..." + ex.getMessage());
    		System.exit(-1);
    	}
    }
    
    public static final String encrypt(String string) throws Exception {
    	// Our cleartext
        byte[] clearText = string.getBytes();

        // Encrypt the cleartext
        byte[] cipherText = pbeEncryptCipher.doFinal(clearText);

    	return Base64.encodeToString(cipherText, false);
    }
    
    public static final String decrypt(String key) throws Exception {
    	// encypted Key
        byte[] cipherText = Base64.decode(key);

        // Encrypt the cleartext
        byte[] clearText = pbeDecryptCipher.doFinal(cipherText);

    	return new String(clearText);
    }
    
    public static void main(String[] argv) throws Exception {
    	if ( argv.length == 1 ) {
				System.out.println(encrypt(argv[0]));
		} else {
			System.out.println("Utility to encrypt a string");
			System.out.println("USAGE :\n java LSEncryptionUtils <input>");
		}
    }
}

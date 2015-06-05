package com.livescribe.admin.config;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.thoughtworks.xstream.core.util.Base64Encoder;

/**
 * <p>Used to encode plain text passwords into secure hashes.</p>
 * 
 * @author <a href="mailto:">???</a>
 * @version 1.0
 */
public class LSPasswordEncoder implements PasswordEncoder {
	
    /**
     * <p>Encodes the given plain text using a <code>MessageDigest</code> 
     * instance and SHA algorithm.</p>
     * 
     * The hashed bytes are Base64-encoded before returning the <code>String</code>.
     * 
     * This is the same algorithm used by the Web Objects / FrontBase platform.
     * 
     * @param plaintext The plain text password to encode.
     * @param salt
     * 
     * @return a SHA-hashed, Base64-encoded <code>String</code>.
     * 
     * @see org.springframework.security.authentication.encoding.PasswordEncoder#encodePassword(java.lang.String, java.lang.Object)
     */
    @Override
    public String encodePassword(String plaintext, Object salt)
            throws DataAccessException {
    		String result = null;
    		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA"); //step 2
		} catch(NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		try {
			md.update(plaintext.getBytes("UTF-8")); //step 3
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
		byte raw[] = md.digest(); //step 4
		result = "{SHA}" + (new Base64Encoder()).encode(raw); //step 5
        return result;
    }

    /**
     * <p>Checks the validity of the given plain text password.</p>
     * 
     * Calls {@link com.livescribe.admin.config.LSPasswordEncoder.encodePassword()}
     * to hash the plain text password before matching against the saved password.
     * 
     * @param savedPassword The password retrieved from the database.
     * @param enteredRawPassword The user-entered plain text password.
     * @param salt
     * 
     * @see org.springframework.security.authentication.encoding.PasswordEncoder#isPasswordValid(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public boolean isPasswordValid(String savedPassword, String enteredRawPassword, Object salt)
            throws DataAccessException {
	    	if (savedPassword == null) return false;
	    	
	    	String encodedPass = this.encodePassword(enteredRawPassword, salt);
	    	if (encodedPass == null) return false;
	        return savedPassword.equals(encodedPass);
    }
}

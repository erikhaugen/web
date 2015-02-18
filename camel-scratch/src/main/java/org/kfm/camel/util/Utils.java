/**
 * Created:  Jul 13, 2013 2:47:10 PM
 */
package org.kfm.camel.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class Utils {

	//	Constants that define the parts of a page ID
    //	From highest index to lowest index, these go as:
    //	Section.Segment.Shelf.Book.Page
    private static final long[] MASKS  = { 0xfffL, 0xfffL, 0xffffL, 0xfffL, 0xfffL };
    private static final  int[] SHIFTS = {    52 ,    40 ,     24 ,    12 ,    0  };

    //	Index constants
    private static final int SECTION = 0;
    private static final int SEGMENT = 1;
    private static final int SHELF   = 2;
    private static final int BOOK    = 3;
    private static final int PAGE    = 4;

    /**
	 * <p></p>
	 * 
	 */
	public Utils() {
	}

	/**
	 * <p></p>
	 * 
	 * @param filePath
	 * 
	 * @return
	 */
	public static String getDisplayIdFromFilePath(String filePath) {
		
		int idx = filePath.lastIndexOf("/") + 1;
		String nameOfFile = filePath.substring(idx);
		String displayId = nameOfFile.substring(0, 14);
		
		return displayId;
	}

	/**
	 * <p>Obfuscating the oauth token for logging purposes.</p>
	 * 
	 * @param oauthToken
	 * @return
	 */
	public static String obfuscateOAuthToken(String oauthToken) {
		if (null == oauthToken) {
			return "";
		}
		
		if (oauthToken.length() <= 5) {
			return oauthToken;
		}
		
		return oauthToken.substring(0, oauthToken.length() - 5) + "xxxxx";
	}

	public static String asHexString(byte[] md5DigestBytes) {
		
		String hexString = new String(Hex.encodeHex(md5DigestBytes));
		return hexString;
	}
	
	public static byte[] toMD5Hash(byte[] bytes) throws NoSuchAlgorithmException {
		
		MessageDigest tMD5Digest = MessageDigest.getInstance("MD5");
		byte[] hash = tMD5Digest.digest(bytes);
		return hash;
	}
	
	public static byte[] toMD5Hash(String xml) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		byte[] strokeBytes = xml.getBytes("UTF-8");
		MessageDigest tMD5Digest = MessageDigest.getInstance("MD5");
		byte[] hash = tMD5Digest.digest(strokeBytes);
		return hash;
	}
	
	/**
	 * <p>Generates an MD5 hash string from the given array of bytes.</p>
	 * 
	 * @param bytes The bytes to generate hash from.
	 * 
	 * @return an MD5 hash string.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	public static String toMD5HashString(byte[] bytes) throws NoSuchAlgorithmException {
		
		byte[] hash = toMD5Hash(bytes);
		String hexHash = new String(Hex.encodeHex(hash));
		
		return hexHash;
	}
	
	/**
	 * <p>Generates an MD5 hash string from the given XML string.</p>
	 * 
	 * @param xml The string to generate hash from.
	 * 
	 * @return an MD5 hash string.
	 * 
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static String toMD5HashString(String xml) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		
		byte[] strokeBytes = xml.getBytes("UTF-8");
		String strokeHexHash = toMD5HashString(strokeBytes);
		return strokeHexHash;
	}

	/**
	 * <p></p>
	 * 
	 * @param longAddress
	 * 
	 * @return
	 */
	public static String toPageAddressString(long longAddress) {
		
        String addrStr = (int)((longAddress >> SHIFTS[SECTION]) & MASKS[SECTION])
                + "."
                + (int)((longAddress >> SHIFTS[SEGMENT]) & MASKS[SEGMENT])
                + "."
                + (int)((longAddress >> SHIFTS[SHELF]) & MASKS[SHELF])
                + "."
                + (int)((longAddress >> SHIFTS[BOOK]) & MASKS[BOOK])
                + "."
                + (int)((longAddress >> SHIFTS[PAGE]) & MASKS[PAGE]);
        return addrStr;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param addressString
	 * 
	 * @return
	 */
	public static long toPageAddressLong(String addressString) {
		
    	int start;

        String[] parts = addressString.split("\\.");
        if (parts.length == 4) {
        	start = 1;
        } else if (parts.length == 5) {
        	start = 0;
        } else {
            throw new IllegalArgumentException("Need 4 or 5 parts: " + parts.length);
        }

        // Decode each part

        long longAddr = 0;

        for (int i = parts.length; --i >= 0; ) {
            try {
                int part = Integer.decode(parts[i]).intValue();
                longAddr |= (part & MASKS[i + start]) << SHIFTS[i + start];
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        
        return longAddr;
	}
}

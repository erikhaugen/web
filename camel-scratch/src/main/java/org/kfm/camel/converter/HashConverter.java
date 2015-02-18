/**
 * Created:  Dec 5, 2014 6:13:09 PM
 */
package org.kfm.camel.converter;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.camel.Converter;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.livescribe.afp.Afd;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
//@Converter
public class HashConverter {

	private static Logger logger = Logger.getLogger(HashConverter.class.getName());

	/**
	 * <p></p>
	 * 
	 */
	public HashConverter() {
	}

//	@Converter(allowNull = true)
	public static String fromString(String xml) {
		logger.debug("toString() - Converting XML string to hash ...");
		
		String strokeHexHash = null;
		try {
			byte[] strokeBytes = xml.getBytes("UTF-8");
			MessageDigest tMD5Digest = MessageDigest.getInstance("MD5");
			byte[] strokeHash = tMD5Digest.digest(strokeBytes);
			strokeHexHash = new String(Hex.encodeHex(strokeHash));
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		}
		return strokeHexHash;
	}
}

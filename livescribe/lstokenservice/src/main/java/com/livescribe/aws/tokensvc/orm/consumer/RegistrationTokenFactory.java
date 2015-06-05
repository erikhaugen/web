/*
 * Created:  Sep 26, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.orm.consumer;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.aws.tokensvc.crypto.EncryptionUtils;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.manufacturing.Pen;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegistrationTokenFactory {

	@Autowired
	private static EncryptionUtils encryptionUtils;
	
	/**
	 * <p></p>
	 * 
	 */
	public RegistrationTokenFactory() {
	}

	/**
	 * <p></p>
	 * 
	 * @param regDev
	 * 
	 * @return 
	 */
	public static String createLSToken(RegisteredDevice regDev) {
		
		String plaintext = regDev.getDeviceSerialNumber() + ":" + regDev.getUser().getUserId();
		byte[] encrypted = encryptionUtils.encrypt(plaintext);
		String hexed = Hex.encodeHexString(encrypted);
		
		return hexed;
	}

	/**
	 * <p></p>
	 * 
	 * @param pen
	 * @param user
	 * 
	 * @return
	 */
	public static String createLSToken(Pen pen, User user) {
		
		String plaintext = pen.getSerialnumber() + ":" + user.getUserId();
		byte[] encrypted = encryptionUtils.encrypt(plaintext);
		String hexed = Hex.encodeHexString(encrypted);
		
		return hexed;
	}
}

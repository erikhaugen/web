/*
 * Created:  Sep 26, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.orm.consumer;

import org.apache.log4j.Logger;

import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.framework.orm.consumer.RegisteredDevice;
import com.livescribe.framework.orm.consumer.User;

/**
 * <p>Creates new <code>RegisteredDevice</code> objects.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegisteredDeviceFactory {

	private static Logger logger = Logger.getLogger(RegisteredDeviceFactory.class.getName());
	
	/**
	 * <p>Default class constructor.</p>
	 * 
	 */
	public RegisteredDeviceFactory() {
	}

	/**
	 * <p>Creates a new device registration with the given <code>Pen</code> and
	 * <code>User</code>.</p>
	 * 
	 * @param pen The <code>Pen</code> to register to the given <code>User</code>.
	 * @param user The <code>User</code> to register to the given <code>Pen</code>.
	 * 
	 * @return a registered device object.
	 */
	public static RegisteredDevice createRegisteredDevice(Pen pen, User user) {
		
		RegisteredDevice regDev = new RegisteredDevice();
		regDev.setDeviceId(pen.getPenId());
		regDev.setDeviceSerialNumber(pen.getSerialnumber());
		regDev.setDeviceType(pen.getPenType());
		regDev.setUser(user);
		regDev.setComplete(false);
		regDev.setLastModifiedBy("Token Service");
		
		return regDev;
	}
}

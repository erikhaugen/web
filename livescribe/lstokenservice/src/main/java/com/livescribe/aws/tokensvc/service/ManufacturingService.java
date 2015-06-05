/*
 * Created:  Sep 27, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import com.livescribe.aws.tokensvc.exception.DeviceNotFoundException;
import com.livescribe.aws.tokensvc.exception.DuplicateSerialNumberException;
import com.livescribe.framework.orm.manufacturing.Pen;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface ManufacturingService {
	
	/**
	 * <p>Determines if the given <code>deviceId</code> exists in the 
	 * <code>corp_manufacturing</code> database.</p>
	 * 
	 * @param deviceId
	 * 
	 * @return
	 */
	public boolean isValidDeviceId(String deviceId);
	
	/**
	 * <p></p>
	 * 
	 * @param deviceId
	 * 
	 * @return
	 * 
	 * @throws DuplicateSerialNumberException if more than one device is found
	 * with the given <code>deviceId</code>.
	 * @throws DeviceNotFoundException if the <code>deviceId</code> is not found
	 * in the database. 
	 */
	public Pen lookupDevice(String deviceId) throws DuplicateSerialNumberException, DeviceNotFoundException;
	
	/**
	 * <p>Find Pen by displayId</p>
	 * 
	 * @param displayId
	 * @return
	 * @throws DuplicateSerialNumberException
	 * @throws DeviceNotFoundException
	 */
	public Pen findPenByDisplayId(String displayId) throws DuplicateSerialNumberException, DeviceNotFoundException;
}

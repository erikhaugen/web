/*
 * Created:  Sep 27, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.tokensvc.exception.DeviceNotFoundException;
import com.livescribe.aws.tokensvc.exception.DuplicateSerialNumberException;
import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.framework.orm.manufacturing.PenDao;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ManufacturingServiceImpl implements ManufacturingService {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private PenDao penDao;

	/**
	 * <p></p>
	 * 
	 */
	public ManufacturingServiceImpl() {
	}

	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.ManufacturingService#isValidDeviceId(java.lang.String)
	 */
	public boolean isValidDeviceId(String deviceId) {
		
		Pen pen = null;
		
		try {
			pen = lookupDevice(deviceId);
		}
		catch (DuplicateSerialNumberException dsne) {
			logger.debug("DuplicateSerialNumberException thrown while looking up device ID '" + deviceId + "'.");
			return false;
		}
		catch (DeviceNotFoundException dnfe) {
			logger.debug("DeviceNotFoundException thrown while looking up device ID '" + deviceId + "'.");
			return false;
		}
		if (pen == null) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.livescribe.aws.tokensvc.service.ManufacturingService#lookupDevice(java.lang.String)
	 */
	@Override
	@Transactional("manufacturing")
	public Pen lookupDevice(String deviceId) throws DuplicateSerialNumberException, DeviceNotFoundException {
		
		Pen example = new Pen();
		example.setSerialnumber(deviceId);
		
		logger.debug("Looking up pen with deviceId = " + deviceId);
		
		List<Pen>pens = penDao.findByExample(example);
		
		if (pens.size() > 1) {
			String msg = "More than one device found in database with deviceId = '" + deviceId + "'.";
			throw new DuplicateSerialNumberException(msg);
		}
		else if (pens.size() == 0) {
			String msg = "No device found in database with deviceId = '" + deviceId + "'.";
			throw new DeviceNotFoundException(msg);
		}
		
		Pen pen = pens.get(0);
		return pen;
	}
	
	@Override
	@Transactional("manufacturing")
	public Pen findPenByDisplayId(String displayId) throws DuplicateSerialNumberException, DeviceNotFoundException {
		Pen example = new Pen();
		example.setDisplayId(displayId);
		
		logger.debug("Looking up pen with displayId = " + displayId);
		
		List<Pen>pens = penDao.findByExample(example);
		
		if (pens.size() > 1) {
			String msg = "More than one device found in database with displayId = '" + displayId + "'.";
			throw new DuplicateSerialNumberException(msg);
			
		} else if (pens.size() == 0) {
			String msg = "No device found in database with displayId = '" + displayId + "'.";
			throw new DeviceNotFoundException(msg);
		}
		
		Pen pen = pens.get(0);
		return pen;
	}
}

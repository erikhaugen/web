/**
 * Created:  Oct 13, 2010 1:44:36 PM
 */
package com.livescribe.community.service;

import org.apache.log4j.Logger;

import com.livescribe.community.cache.CacheClient;
import com.livescribe.community.cache.Cacheable;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public abstract class BaseService {

	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * <p></p>
	 * 
	 */
	public BaseService() {
	}
}

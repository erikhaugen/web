/**
 * Created:  Apr 11, 2014 10:50:02 AM
 */
package com.livescribe.framework.version.dao;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.livescribe.framework.orm.versions.VersionHistoryDao;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
//@Repository("versionHistoryDao")
public class CustomVersionHistoryDao extends VersionHistoryDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public CustomVersionHistoryDao() {
	}

}

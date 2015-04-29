/**
 * Created:  Apr 10, 2014 5:24:48 PM
 */
package com.livescribe.framework.orm.versions;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class AbstractDao {

	protected Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	protected SessionFactory sessionFactoryVersions;

	/**
	 * <p></p>
	 * 
	 */
	public AbstractDao() {
	}

}

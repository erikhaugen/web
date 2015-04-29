/**
 * 
 */
package com.livescribe.framework.orm.evernote;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public abstract class AbstractDao {

	protected Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
	protected SessionFactory sessionFactoryEvernote;
	
	/**
	 * <p></p>
	 *
	 */
	public AbstractDao() {
		
	}

}

/**
 * Created:  Oct 31, 2013 2:49:22 PM
 */
package com.livescribe.framework.orm.vectordb;

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
	protected SessionFactory sessionFactoryVectorDB;
	
	/**
	 * <p></p>
	 * 
	 */
	public AbstractDao() {
	}

}

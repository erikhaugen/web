/**
 * 
 */
package com.livescribe.framework.orm.lspaperclouddb;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 * 
 * @author <a href="mailto:gkalra@livescribe.com">Gurmeet S. Kalra</a>
 * @version 1.0
 */
public abstract class AbstractDao {

	protected Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
	protected SessionFactory sessionFactoryPaperCloud;
	
	/**
	 * <p></p>
	 *
	 */
	public AbstractDao() {
		
	}

}

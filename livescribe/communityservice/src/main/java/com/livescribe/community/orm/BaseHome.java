/**
 * 
 */
package com.livescribe.community.orm;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

/**
 * <p>Base class for all <code>Home</code> classes.</p>
 * 
 * Provides a Log4j logger and auto-wired {@link org.hibernate.SessionFactory}.
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public abstract class BaseHome {
	
	protected Logger log = Logger.getLogger(getClass());

	protected SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * <p>Default class constructor.</p> 
	 */
	public BaseHome() {}
	
}

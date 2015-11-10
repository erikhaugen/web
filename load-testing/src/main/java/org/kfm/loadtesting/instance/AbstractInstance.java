/**
 * Created: Nov 9, 2015 3:16:33 PM
 *      by: kmurdoff
 */
package org.kfm.loadtesting.instance;

import org.apache.log4j.Logger;
import org.kfm.loadtesting.cloud.CloudProvider;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
public class AbstractInstance {

	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	protected String id;
    protected String instanceType;
    protected int supportedUsers;
    
	protected CloudProvider cloudProvider;

	/**
	 * <p></p>
	 * 
	 */
	public AbstractInstance() {
		
	}

	/**
	 * <p></p>
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * <p></p>
	 * 
	 * @return the cloudProvider
	 */
	public CloudProvider getCloudProvider() {
		return cloudProvider;
	}

	/**
	 * <p></p>
	 * 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

}

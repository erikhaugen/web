/**
 * Created: Nov 9, 2015 5:21:36 PM
 *      by: kmurdoff
 */
package org.kfm.loadtesting.factory;

import org.kfm.loadtesting.agent.Agent;
import org.kfm.loadtesting.instance.AgentInstance;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
public class AgentFactory {

	/**
	 * <p></p>
	 * 
	 */
	public AgentFactory() {
		// TODO Auto-generated constructor stub
	}

	public static Agent create() {
		
		Agent agent = new AgentInstance();
		
		return agent;
	}
}

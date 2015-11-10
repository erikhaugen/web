/**
 * Created: Nov 9, 2015 2:54:42 PM
 *      by: kmurdoff
 */
package org.kfm.loadtesting.service;

import org.kfm.loadtesting.agent.Agent;
import org.kfm.loadtesting.instance.AgentInstance;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
public interface AgentService {
	
	/**
	 * <p>Launches a new {@code Agent} instance.</p>
	 *
	 */
	public AgentInstance launch(Agent agent);
	
	/**
	 * <p>Stop an {@code Agent} instance.</p>
	 *
	 */
	public void pause(Agent agent);

	/**
	 * <p>Starts a previously stopped {@code Agent} instance.</p>
	 *
	 */
	public void resume(Agent agent);
	
	/**
	 * <p>Permanently terminates a given {@code Agent} instance.</p>
	 *
	 */
	public void terminate(Agent agent);	
}

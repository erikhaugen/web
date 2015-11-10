/**
 * Created: Nov 9, 2015 3:14:45 PM
 *      by: kmurdoff
 */
package org.kfm.loadtesting.service.impl;

import org.apache.log4j.Logger;
import org.kfm.loadtesting.agent.Agent;
import org.kfm.loadtesting.instance.AgentInstance;
import org.kfm.loadtesting.service.AgentService;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
public class AgentServiceImpl implements AgentService {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * <p></p>
	 * 
	 */
	public AgentServiceImpl() {
		
	}

	/* (non-Javadoc)
	 * @see org.kfm.loadtesting.service.AgentService#launch()
	 */
	@Override
	public AgentInstance launch(Agent agent) {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.kfm.loadtesting.service.AgentService#pause()
	 */
	@Override
	public void pause(Agent agent) {
		

	}

	/* (non-Javadoc)
	 * @see org.kfm.loadtesting.service.AgentService#resume()
	 */
	@Override
	public void resume(Agent agent) {
		

	}

	/* (non-Javadoc)
	 * @see org.kfm.loadtesting.service.AgentService#terminate()
	 */
	@Override
	public void terminate(Agent agent) {
		

	}

}

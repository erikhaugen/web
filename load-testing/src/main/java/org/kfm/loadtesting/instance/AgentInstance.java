/**
 * Created: Nov 9, 2015 3:16:21 PM
 *      by: kmurdoff
 */
package org.kfm.loadtesting.instance;

import org.kfm.loadtesting.agent.Agent;
import org.kfm.loadtesting.agent.AgentStatus;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
public class AgentInstance extends AbstractInstance implements Agent {
	
	private AgentStatus status;
	
    /**
	 * <p></p>
	 * 
	 */
	public AgentInstance() {
		super();
		status = AgentStatus.LOADED;
	}

	/* (non-Javadoc)
	 * @see org.kfm.loadtesting.agent.Agent#start()
	 */
	@Override
	public void start() {
		
		logger.debug("start(); - ");
	}

	/* (non-Javadoc)
	 * @see org.kfm.loadtesting.agent.Agent#stop()
	 */
	@Override
	public void stop() {

		logger.debug("stop(); - ");
	}

	/* (non-Javadoc)
	 * @see org.kfm.loadtesting.agent.Agent#pause()
	 */
	@Override
	public void pause() {
		
		logger.debug("pause(); - ");
	}

	/* (non-Javadoc)
	 * @see org.kfm.loadtesting.agent.Agent#resume()
	 */
	@Override
	public void resume() {

		logger.debug("resume(); - ");
	}

	/* (non-Javadoc)
	 * @see org.kfm.loadtesting.agent.Agent#status()
	 */
	@Override
	public void status() {
		
		logger.debug("status(); - ");
	}

	/* (non-Javadoc)
	 * @see org.kfm.loadtesting.agent.Agent#execute()
	 */
	@Override
	public void execute() {
		
		logger.debug("execute(); - ");
	}
}

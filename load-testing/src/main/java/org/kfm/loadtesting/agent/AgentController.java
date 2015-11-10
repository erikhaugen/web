/**
 * Created: Nov 9, 2015 5:30:06 PM
 *      by: kmurdoff
 */
package org.kfm.loadtesting.agent;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
@RestController
public class AgentController {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * <p></p>
	 * 
	 */
	public AgentController() {
		// TODO Auto-generated constructor stub
	}

	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public AgentStatusResponse start() {
		logger.debug("start() - ");
		AgentStatusResponse resp = new AgentStatusResponse();
		resp.setAgentStatus(AgentStatus.RUNNING);
		return resp;
	}

	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	public AgentStatusResponse stop() {
		logger.debug("stop() - ");
		AgentStatusResponse resp = new AgentStatusResponse();
		resp.setAgentStatus(AgentStatus.STOPPED);
		return resp;
	}
	
	@RequestMapping(value = "/terminate", method = RequestMethod.POST)
	public AgentStatusResponse terminate() {
		logger.debug("terminate() - ");
		AgentStatusResponse resp = new AgentStatusResponse();
		resp.setAgentStatus(AgentStatus.TERMINATED);
		return resp;
	}
}

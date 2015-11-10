/**
 * Created: Nov 10, 2015 11:14:00 AM
 *      by: kmurdoff
 */
package org.kfm.loadtesting.agent;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
public class AgentStatusResponse {

	private AgentStatus agentStatus;
	
	/**
	 * <p></p>
	 * 
	 */
	public AgentStatusResponse() {}

	/**
	 * <p></p>
	 * 
	 * @return the agentStatus
	 */
	public AgentStatus getAgentStatus() {
		return agentStatus;
	}

	/**
	 * <p></p>
	 * 
	 * @param agentStatus the agentStatus to set
	 */
	public void setAgentStatus(AgentStatus agentStatus) {
		this.agentStatus = agentStatus;
	}

}

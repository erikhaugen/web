/**
 * Created: Nov 9, 2015 2:58:21 PM
 *      by: kmurdoff
 */
package org.kfm.loadtesting.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kfm.BaseTest;
import org.kfm.loadtesting.agent.Agent;
import org.kfm.loadtesting.instance.AgentInstance;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
public class AgentServiceTest extends BaseTest {

	@Autowired
	private AgentService agentService;
	
	/**
	 * <p></p>
	 * 
	 */
	public AgentServiceTest() {
		super();
	}

	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testLaunchAgent() {
		
		Assert.assertNotNull("The autowired AgentService instance was 'null'.", agentService);
		
		Agent agent = null;
		AgentInstance agentInstance = agentService.launch(agent);
		Assert.assertNotNull("The returned AgentInstance was 'null'.", agentInstance);
	}
}

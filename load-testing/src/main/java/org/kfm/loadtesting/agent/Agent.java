/**
 * Created: Nov 9, 2015 2:53:31 PM
 *      by: kmurdoff
 */
package org.kfm.loadtesting.agent;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
public interface Agent {

	public void start();
	public void stop();
	public void pause();
	public void resume();
	public void status();
	
	/**
	 * <p>Begins execution of the test script.</p>
	 *
	 */
	public void execute();
}

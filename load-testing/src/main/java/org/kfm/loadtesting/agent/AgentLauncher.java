/**
 * Created: Nov 9, 2015 5:27:20 PM
 *      by: kmurdoff
 */
package org.kfm.loadtesting.agent;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
@SpringBootApplication
public class AgentLauncher {

	/**
	 * <p></p>
	 * 
	 */
	public AgentLauncher() {
		
	}

	/**
	 * <p></p>
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(AgentLauncher.class, args);
	}
}

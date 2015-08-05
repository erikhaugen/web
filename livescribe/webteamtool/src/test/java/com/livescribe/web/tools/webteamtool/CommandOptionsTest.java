/**
 * Created:  Jun 17, 2013 12:08:30 PM
 */
package com.livescribe.web.tools.webteamtool;

import org.junit.Assert;
import org.junit.Test;

import com.beust.jcommander.JCommander;
import com.livescribe.web.tools.webteamtool.cli.CommandOptions;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CommandOptionsTest {

	/**
	 * <p></p>
	 * 
	 */
	public CommandOptionsTest() {
		
	}

	@Test
	public void testDisplayId() {
		
		String[] args = {"-d", "AYE-APS-DHH-HS"};
		CommandOptions opts = new CommandOptions();
		JCommander jc = new JCommander(opts, args);
		Assert.assertEquals(opts.getDisplayId(), "AYE-APS-DHH-HS");
	}
	
	@Test
	public void testSerialNumber() {
		
		String[] args = {"-s", "2594170374701"};
		CommandOptions opts = new CommandOptions();
		JCommander jc = new JCommander(opts, args);
		Long sn = new Long("2594170374701");
		Assert.assertEquals(opts.getSerialNumber(), sn);
	}
}

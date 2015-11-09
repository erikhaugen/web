/**
 * Created:  Mar 28, 2014 4:36:27 PM
 */
package com.livescribe.scratch.velocity.mock;

import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class MockVelocityContext extends VelocityContext {

	/**
	 * <p></p>
	 * 
	 */
	public MockVelocityContext() {
		put("player_launch_url_href", "http://localhost/int/player/lsnotesdesktop.htm");
		put("evernote_media_hash", "asdjf098adsgu98htpq24tu9q3tghaq-89tu-q34rf");
		put("evernote_media_type", "image/png");
		put("livescribe_home_url_ref", "http://www.livescribe.com");
		put("evernote_media_logo_inactive_frost_resource_hash", "a9dsfnasdf890ansd8anv908n90r709aern90a7t");
		put("evernote_media_logo_inactive_frost_resource_type", "image/gif");
	}

	/**
	 * <p></p>
	 * 
	 * @param context
	 */
	public MockVelocityContext(Map context) {
		super(context);
	}

	/**
	 * <p></p>
	 * 
	 * @param innerContext
	 */
	public MockVelocityContext(Context innerContext) {
		super(innerContext);
	}

	/**
	 * <p></p>
	 * 
	 * @param context
	 * @param innerContext
	 */
	public MockVelocityContext(Map context, Context innerContext) {
		super(context, innerContext);
	}

}

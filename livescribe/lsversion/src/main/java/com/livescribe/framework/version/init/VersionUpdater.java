/**
 * Created:  May 13, 2013 5:08:41 PM
 */
package com.livescribe.framework.version.init;

import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.framework.lsconfiguration.AppProperties;

/**
 * <p>Updates the <code>versions</code> database upon startup.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class VersionUpdater {

	@Autowired
	private AppProperties appProperties;
	
	/**
	 * <p></p>
	 * 
	 */
	public VersionUpdater() {
		
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public String getVersionFromDatabase() {
		
		return null;
	}
	
	public void addVersionToDatabase() {
		
	}
}

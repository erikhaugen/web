/**
 * Created:  Apr 11, 2014 5:22:38 PM
 */
package com.livescribe.framework.version.dto;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.livescribe.framework.orm.versions.Version;
import com.livescribe.framework.version.BaseTest;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class VersionComparatorTest extends BaseTest {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private VersionComparator comparator;
	
	/**
	 * <p></p>
	 * 
	 */
	public VersionComparatorTest() {
		comparator = new VersionComparator();
	}

	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testCompare_SmallVsLargeBuildNumber() {
		
		Version v1 = new Version();
		v1.setAppVersion("1.2.0-SNAPSHOT");
		v1.setBuildNumber("36");
		Version v2 = new Version();
		v2.setAppVersion("1.2.0-SNAPSHOT");
		v2.setBuildNumber("77");
		
		comparator.compare(v1, v2);
	}
}

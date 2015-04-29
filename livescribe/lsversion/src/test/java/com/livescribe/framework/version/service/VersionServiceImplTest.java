/**
 * Created:  May 13, 2013 5:00:23 PM
 */
package com.livescribe.framework.version.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.framework.version.BaseTest;
import com.livescribe.framework.version.dao.CustomVersionDao;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class VersionServiceImplTest extends BaseTest {

	@Autowired
	private VersionService versionService;
	
	@Autowired
	private CustomVersionDao versionDao;
	
	/**
	 * <p></p>
	 * 
	 */
	public VersionServiceImplTest() {
		super();
	}

	@Before
	public void setUp() {
		
	}
	
	@Test
	@Transactional("txVersions")
	public void testPostVersion() {
		
//		versionService.postVersion();
//		versionService.getVersion();
//		versionDao.findByAppAndEnvironment("testServletContext", Env.LOCAL);
	}
}

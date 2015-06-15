/**
 * 
 */
package com.livescribe.community.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.community.BaseTest;
import com.livescribe.community.cache.CacheAccessControl;
import com.livescribe.community.cache.CacheClient;
import com.livescribe.community.cache.Cacheable;
import com.livescribe.community.cache.CacheablePencast;
import com.livescribe.community.cache.CacheablePencastList;
import com.livescribe.community.cache.CacheablePencastList.ListType;
import com.livescribe.community.config.CommunityProperties;
import com.livescribe.community.view.vo.PencastVo;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PencastServiceTest extends BaseTest {
	
	private CacheClient<? extends Cacheable> cacheClient;
	
	@Autowired
	private PencastService pencastService;
	
	@Autowired
	private CommunityProperties communityProperties;

	/**
	 * <p></p>
	 */
	public PencastServiceTest() {
		cacheClient = CacheAccessControl.getInstance().getCacheClient(CacheablePencast.class);
	}
	
	/**
	 * Test method for {@link com.livescribe.community.service.PencastService#getAll()}.
	 */
//	@Test
	public void testGetAll() {
		
//		List<PencastVo> list = pencastService.getAllPencasts();
//		assertNotNull("The returned List was 'null'.", list);
//		assertTrue("The returned List was empty.", list.size() > 0);
	}

	/**
	 * Test method for {@link com.livescribe.community.service.PencastService#findByShortId(java.lang.String)}.
	 */
	@Test
	@Transactional
	public void testFindByShortId() {

		if (!skipTests) {
			String methodName = new Exception().getStackTrace()[0].getMethodName();
			String propertyKey = this.getClass().getSimpleName() + "." + methodName + ".test.shortId";
			String TEST_PENCAST_SHORT_ID = communityProperties.getProperty(propertyKey);
			
			PencastVo pencast = pencastService.findByShortId(TEST_PENCAST_SHORT_ID);
			
			assertNotNull("The returned pencast object was 'null'.", pencast);
		}
	}

	/**
	 * Test method for {@link com.livescribe.community.service.PencastService#getPencastsByCategoryName(java.lang.String)}.
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
//	@Test
//	Commented out because memcached is not running on app3-test.  Therefore,
//	this test cannot complete successfully.
	@Transactional
	public void testGetPencastsByCategoryName() throws IOException, ClassNotFoundException {
		
		if (!skipTests) {
			String methodName = new Exception().getStackTrace()[0].getMethodName();
			String catPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.categoryName";
			String TEST_CATEGORY_NAME = communityProperties.getProperty(catPropKey);
	
			//	Get the 'start' number for testing.
			String startPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.start";
			String startStr = communityProperties.getProperty(startPropKey);
			int TEST_START = Integer.parseInt(startStr);
			
			//	Get the number to return for testing.  (fetchSize)
			String numPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.fetchSize";
			String numStr = communityProperties.getProperty(numPropKey);
			int TEST_FETCH_SIZE = Integer.parseInt(numStr);
			
			List<PencastVo> list = pencastService.getPencastsByCategoryName(TEST_CATEGORY_NAME, TEST_START, TEST_FETCH_SIZE);
			
			assertNotNull("The returned List was 'null'.", list);
			//assertTrue("The returned List was empty.", list.size() > 0);
			
			logger.debug("The first list size is:  " + list.size());
			
			ListType type = CacheablePencastList.ListType.BY_CATEGORY_NAME;
			
			int size1 = list.size();
			
			if (size1 > 0) {
				
				//	Determine that request was added to the cache.
				Cacheable object = null;
				
				object = cacheClient.getCachedObject(CacheablePencastList.getCacheKey(type, TEST_CATEGORY_NAME + "-" + TEST_START + "-" + TEST_FETCH_SIZE));
				
				List<PencastVo> cachedList = null;
				if ( object != null && object instanceof CacheablePencastList ) {
					cachedList = ((CacheablePencastList)object).getPencastVoList();
				}
				assertNotNull("The returned List of cached pencasts was 'null'.", cachedList);
				
				//	Remove the first pencast from the List ...
				PencastVo pv = list.remove(0);
				
				//	... and replace the list back in the cache.
				Date now = new Date();
				cacheClient.cacheObject(new CacheablePencastList(list, type, TEST_CATEGORY_NAME + "-" + TEST_START + "-" + TEST_FETCH_SIZE, 5, now));
				
				//	Now re-fetch the list of pencasts and determine if the list 
				//	was obtained from the cache.
				List<PencastVo> list2 = pencastService.getPencastsByCategoryName(TEST_CATEGORY_NAME, TEST_START, TEST_FETCH_SIZE);
				int size2 = list2.size();
				
				//	The second list should be a size 1 less than the first.
				assertTrue("The size of the initial List was NOT larger than the second List.", size1 > size2);
				assertEquals("The first List was NOT larger than the second List by 1.", (size1 - 1), size2);
			}
			else {
				assertTrue("The initial list of pencasts was empty.  Unable to continue with test.", false);
			}
	//		assertTrue("The returned List did not have " + EXPECTED_PENCAST_FETCH_SIZE + " items.", list.size() == EXPECTED_PENCAST_FETCH_SIZE);

		}
	}
	
	/**
	 * Test method for {@link com.livescribe.community.service.PencastService#getRecentPencasts()}.
	 */
	@Test
	@Transactional
	public void testGetRecentPencasts() {
		
		if (!skipTests) {
			String methodName = new Exception().getStackTrace()[0].getMethodName();
			
			//	Get the 'start' number for testing.
			String startPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.start";
			String startStr = communityProperties.getProperty(startPropKey);
			int TEST_START = Integer.parseInt(startStr);
			
			//	Get the number to return for testing.
			String numPropKey = this.getClass().getSimpleName() + "." + methodName + ".test.fetchSize";
			String numStr = communityProperties.getProperty(numPropKey);
			int TEST_FETCH_SIZE = Integer.parseInt(numStr);
			
			List<PencastVo> list = pencastService.getRecentPencasts(TEST_START, TEST_FETCH_SIZE);
			
			//	Get the expected number of returned pencasts.
			String propertyKey = this.getClass().getSimpleName() + "." + methodName + ".expected.fetchSize";
			String fetchSize = communityProperties.getProperty(propertyKey);
			int EXPECTED_PENCAST_FETCH_SIZE = Integer.parseInt(fetchSize);
	
			assertNotNull("The returned List was 'null'.", list);
	//		assertEquals("The returned List did not have " + EXPECTED_PENCAST_FETCH_SIZE + " items.", EXPECTED_PENCAST_FETCH_SIZE, list.size());
	//		assertTrue("The returned List did not have " + EXPECTED_PENCAST_FETCH_SIZE + " items.", list.size() == EXPECTED_PENCAST_FETCH_SIZE);
		}
	}
	
	/**
	 * Test method for {@link com.livescribe.community.service.PencastService#getUserPencasts(java.lang.String)}.
	 */
//	@Test
	@Transactional
	public void testGetUserPencasts() {
		
		if (!skipTests) {
			String methodName = new Exception().getStackTrace()[0].getMethodName();
			String propertyKey = this.getClass().getSimpleName() + "." + methodName + ".test.token";
			String TEST_USER_TOKEN = communityProperties.getProperty(propertyKey);
	
			List<PencastVo> list = pencastService.getUserPencasts(TEST_USER_TOKEN);
			
			assertNotNull("The returned List was 'null'.", list);
	//		assertTrue("The returned List was empty.", list.size() > 0);
			logger.debug("Found " + list.size() + " pencasts.");
			
		}
	}
}

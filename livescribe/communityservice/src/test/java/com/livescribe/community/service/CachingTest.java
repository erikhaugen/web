/**
 * 
 */
package com.livescribe.community.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import com.livescribe.base.utils.WOAppMigrationUtils;
import com.livescribe.community.BaseTest;
import com.livescribe.community.cache.CacheAccessControl;
import com.livescribe.community.cache.CacheClient;
import com.livescribe.community.cache.Cacheable;
import com.livescribe.community.cache.CacheablePencast;
import com.livescribe.community.cache.CacheablePencastAudio;
import com.livescribe.community.cache.CacheablePencastList;
import com.livescribe.community.cache.CacheablePencastPage;
import com.livescribe.community.cache.CacheablePencastList.ListType;
import com.livescribe.community.view.vo.PencastVo;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CachingTest extends BaseTest {

	private CacheClient<? extends Cacheable> cacheClient;
	
	private static String CACHE_KEY = "";
	
	/**
	 * 
	 */
	public CachingTest() {
		cacheClient = CacheAccessControl.getInstance().getCacheClient(CacheablePencast.class);
	}

	/**
	 * 
	 */
	@Test
	public void testWriteToCache() throws Exception {
		
		if (!skipTests) {
			CacheablePencastAudio pa = new CacheablePencastAudio();
			pa.setBeginTime(new Long(334));
			pa.setDuration(new Long(665577));
			pa.setFilePath("/This/is/a/test/file/path");
			pa.setFileSize(new Long(342));
			pa.setFileUrl("http://www-kfm.ls.com/test/url");
			pa.setType("flv");
			
			CacheablePencastPage pp = new CacheablePencastPage();
			pp.setBackground("#FFFFFF");
			pp.setExists(true);
			pp.setFilePath("/Another/test/file/path");
			pp.setFileSize(new Long(64));
			pp.setFileUrl("http://www-kfm.ls.com/test/url");
			pp.setFormat("PCC1.4");
			pp.setPageId(new Integer(0));
			pp.setLabel("1");
			pp.setHeight(new Integer(24));
			pp.setWidth(new Integer(60));
			
			PencastVo pencast = new PencastVo();
			pencast.addPage(pp);
			
			pencast.addAudio(pa);
			pencast.setAudioDuration(new Long(665577));
			pencast.setAuthorEmail("tester1@ls.com");
			pencast.setAuthorFirstName("Bob");
			pencast.setAuthorLastName("Weir");
			pencast.setAuthorScreenName("Rat Dog");
			pencast.setAverageRating(new Double(4.67));
			pencast.setCategoryName("Fun Stuff");
			pencast.setShortId("a;lsdfkja;sdf");
			pencast.setFileDate(new Date());
			pencast.setFilePath("/Pencast/file/path");
			pencast.setFileSize(new Integer(56));
			pencast.setFlashFileFound(true);
			String pk = "0000C0A80115000009C63200000001191FE431D618CD8310";
			byte[] TEST_PENCAST_PK = WOAppMigrationUtils.convertStringToPrimaryKey(pk);
			pencast.setPrimaryKey(TEST_PENCAST_PK);
			
			ArrayList<PencastVo> list = new ArrayList<PencastVo>();
			ListType type = CacheablePencastList.ListType.USER;
			list.add(pencast);
			
			Date now = new Date();
			boolean success = cacheClient.cacheObject(new CacheablePencastList(list, type, "0-20", 5, now));
			
			assertTrue("Writing to the cache was not successful.", success);
		}
	}
	
	public void testReadFromCache() throws Exception {
		
		if (!skipTests) {
			ListType type = CacheablePencastList.ListType.USER;
			
			Cacheable object = cacheClient.getCachedObject(CacheablePencastList.getCacheKey(type, "0-20"));
			
			assertNotNull("The returned object from the cache was 'null'.", object);
		}
	}
}

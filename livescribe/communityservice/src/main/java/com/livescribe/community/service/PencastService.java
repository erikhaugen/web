/**
 * 
 */
package com.livescribe.community.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.community.cache.CacheAccessControl;
import com.livescribe.community.cache.CacheClient;
import com.livescribe.community.cache.Cacheable;
import com.livescribe.community.cache.CacheablePencast;
import com.livescribe.community.cache.CacheablePencastList;
import com.livescribe.community.cache.CacheableList;
import com.livescribe.community.cache.CacheablePencastList.ListType;
import com.livescribe.community.config.ConfigClient;
import com.livescribe.community.dao.CategoryDao;
import com.livescribe.community.dao.PencastDao;
import com.livescribe.community.dao.UserDao;
import com.livescribe.community.orm.ActiveUser;
import com.livescribe.community.orm.PencastAudio;
import com.livescribe.community.orm.PencastPage;
import com.livescribe.community.orm.UGCategory;
import com.livescribe.community.orm.UserProfile;
import com.livescribe.community.util.FlashXmlParser;
import com.livescribe.community.util.Utils;
import com.livescribe.community.view.vo.PencastVo;

/**
 * <p>Provides service APIs regarding <code>Pencast</code>s.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PencastService {

	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static String BASE_LDAPP_PREVIEW_URL = "/cgi-bin/WebObjects/LDApp.woa/wa/pencastPreview";

	private static String CACHE_KEY_SHORT_ID				= "PencastVo-shortId-";
	
	private static String CACHE_TIMEOUT_KEY_SHORT_ID		= "pencast.cache.shortid.interval";
	private static String CACHE_TIMEOUT_KEY_CATEGORY_LIST	= "pencast.cache.categorylist.interval";
	private static String CACHE_TIMEOUT_KEY_MOST_VIEWED		= "pencast.cache.mostviewed.interval";
	private static String CACHE_TIMEOUT_KEY_TOP				= "pencast.cache.top.interval";
	private static String CACHE_TIMEOUT_KEY_RECENT			= "pencast.cache.recent.interval";
	private static String CACHE_TIMEOUT_KEY_CATEGORY		= "pencast.cache.category.interval";
	private static String CACHE_TIMEOUT_KEY_USER			= "pencast.cache.user.interval";

	private CacheClient<? extends Cacheable> cacheClient;

	@Autowired
	private PencastDao pencastDao;

	@Autowired
	private UserService userService;
	
	//	@Autowired
	//	private S3Dao s3Dao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CategoryDao categoryDao;

	//	private TreeSet<String> uploadedFileKeys = new TreeSet<String>();

	/**
	 * <p>Default class constructor.</p>
	 */
	public PencastService() {
		cacheClient = CacheAccessControl.getInstance().getCacheClient(CacheablePencast.class);
	}

	private String constructThumbnailPath(String afdPath) {

		String basePath = Utils.constructDerivativePath(afdPath);
		StringBuilder builder = new StringBuilder();
		builder.append(basePath);
		builder.append("/content.gif");

		return builder.toString();
	}

	/**
	 * <p></p>
	 * 
	 * @param id
	 * 
	 * @return
	 */
	@Transactional
	public PencastVo findByPrimaryKey(byte[] primaryKey) {

		String method = "findByPrimaryKey():  ";
		
		Cacheable object = null;

		try {
			object = cacheClient.getCachedObject(CacheablePencast.getPKCacheKey(primaryKey));
		} catch ( Exception ex ) {
			logger.error(method + "Unable to read from the Memcache Server " + ex.getMessage());
			logger.debug(method + "", ex);
		}

		PencastVo pencast = null;

		if ( object != null ) {
			if (object instanceof PencastVo ) {
				pencast = (PencastVo)object;
			}
		} else {
			pencast = pencastDao.findByPrimaryKey(primaryKey);
			try {
				cacheClient.cacheObject(new CacheablePencast(pencast));
			} catch ( Exception ex ) {
				logger.error(method + "Unable to write object to the Memcache Server " + ex.getMessage());
				logger.debug(method + "", ex);
			}
		}
		return pencast;
	}

	/**
	 * <p>Returns a pencast identified by the given short ID.</p>
	 * 
	 * @param id The &apos;shortId&apos; of the pencast.
	 * 
	 * @return a pencast identified by the given short ID.
	 */
	@Transactional
	public PencastVo findByShortId(String shortId) {
		
		String method = "findByShortId():  ";
		
		Cacheable object = null;
		int timeout = ConfigClient.getTimeout(CACHE_TIMEOUT_KEY_SHORT_ID);
		String key = CACHE_KEY_SHORT_ID + shortId;
		
		try {
			logger.debug(method + "Looking up pencast in cache.");
			object = cacheClient.getCachedObject(key);
		} 
		catch ( Exception ex ) {
			logger.error(method + "Unable to read from the Memcache Server " + ex.getMessage());
			logger.debug(method + "", ex);
		}

		PencastVo pencast = null;

		if ( object != null) {
			if ( object instanceof PencastVo ) {
				logger.debug(method + "Found cached pencast with shortId = '" + shortId + "'.");
				pencast = (PencastVo)object;
			}
			else {
				logger.error(method + "Cached object was not an instance of PencastVo.");
			}
		} 
		else {
			logger.debug(method + "*** Going to the database to fetch pencast with shortId = '" + shortId + "'.");
			pencast = pencastDao.findByPencastShortId(shortId);	
			try {
				Date now = new Date();
				cacheClient.cacheObject(new CacheablePencast(pencast, key, timeout, now));
				logger.debug(method + "Cached pencast with shortId = '" + shortId + "'.");
			} 
			catch ( Exception ex ) {
				logger.error(method + "Unable to write object to the Memcache Server " + ex.getMessage());
				logger.debug(method + "", ex);
			}		
		}
		if (pencast != null) {
			boolean found = findFlashXmlFile(pencast);
			if (found) {
				addFlashXmlInfo(pencast);
			}
		}
		return pencast;
	}

	/**
	 * <p>Locates the <code>flash.xml</code> file on the file system and sets
	 * the path to the file on the given </code>PencastVo</code>.</p>
	 * 
	 * @param pencast The pencast for which to locate the <code>flash.xml</code> file.
	 * 
	 * @return <code>true</code> if the file was found; <code>false</code> if not.
	 */
	private boolean findFlashXmlFile(PencastVo pencast) {

		String filePath = pencast.getFilePath();
		String basePath = Utils.constructDerivativePath(filePath);
		String xmlPath = basePath + "/flash.xml";
		File file = new File(xmlPath);
		pencast.setFlashXmlPath(xmlPath);
		if (file.exists()) {
			pencast.setFlashFileFound(true);
			return true;
		}
		pencast.setFlashFileFound(false);
		return false;
	}

	/**
	 * <p>Returns a list of all pencasts.</p>
	 * 
	 * @return  a <code>List</code> of pencasts.
	 */
	@Transactional
	public List<PencastVo> getAllPencasts(int start, int fetchSize) {

		String method = "getAllPencasts():  ";
		
		ListType type = CacheablePencastList.ListType.ALL_IDX;

		Cacheable object = null;

		try {
			logger.debug(method + "Looking up pencast in cache.");
			object = cacheClient.getCachedObject(CacheablePencastList.getCacheKey(type, start + "-" + fetchSize));
		} 
		catch ( Exception ex ) {
			logger.error(method + "Unable to read from the Memcache Server " + ex.getMessage());
			logger.debug(method + "", ex);
		}
		List<PencastVo> list = null;

		if ( object != null && object instanceof CacheablePencastList ) {
			logger.debug(method + "Found list of cached pencasts.");
			list = ((CacheablePencastList)object).getPencastVoList();
		} 
		else {
			logger.debug(method + "*** Going to the database to fetch pencasts.");
			list = pencastDao.getAllPencasts();
			try {
				Date now = new Date();
				cacheClient.cacheObject(new CacheablePencastList(list, type, start + "-" + fetchSize, now));
				logger.debug(method + "Cached list of pencasts.");
			} 
			catch ( Exception ex ) {
				logger.error(method + "Unable to write object to the Memcache Server " + ex.getMessage());
				logger.debug(method + "", ex);
			}		
		}
		Iterator<PencastVo> iter = list.iterator();
		while (iter.hasNext()) {
			PencastVo pencast = iter.next();
			boolean found = findFlashXmlFile(pencast);
			if (found) {
				//				Document flashXmlDom = getFlashXmlFile(pencast);
				//	TODO:  Decide whether to add this Document to the PencastVo.
			}
			//	Construct the direct path on the file system to the thumbnail image.
//			String thumbnailPath = constructThumbnailPath(pencast.getFilePath());

			//	Construct the URL to LDApp, requesting the thumbnail image.
			pencast.setThumbnailUrl(BASE_LDAPP_PREVIEW_URL + "?sid=" + pencast.getShortId());

//			String defaultImgPath = "/images/icons/default_thumb_small.gif";
			//			String directPath = "derivative/content.gif";

			//			"/cgi-bin/WebObjects/LDApp.woa/wa/pencastPreview?sid=rJBjF9Kl7B52";
		}
		return list;
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<String> getCategoryList() {

		String method = "getCategoryList():  ";
		
		Cacheable object = null;
		int timeout = ConfigClient.getTimeout(CACHE_TIMEOUT_KEY_CATEGORY_LIST);

		try {
			logger.debug(method + "Looking up categories in cache.");
			object = cacheClient.getCachedObject(CacheableList.getCacheKey("CategoryNames"));
		} 
		catch ( Exception ex ) {
			logger.error(method + "Unable to read from the Memcache Server " + ex.getMessage());
			logger.debug("", ex);
		}
		List<String> categoryNames = null;


		if ( object != null ) {
			if ( object instanceof CacheableList ) {
				logger.debug(method + "Found list of cached categories.");
				categoryNames = ((CacheableList<String>)object).getCachedList();
			}
			else {
				logger.error(method + "Cached object was not an instance of CacheableList<String>.");
			}
		} 
		else {
			logger.debug(method + "*** Going to the database to fetch pencasts.");
			List<UGCategory> list = categoryDao.getCategoryList();
			categoryNames = new ArrayList<String>();
			Iterator<UGCategory> catIter = list.iterator();
			while (catIter.hasNext()) {
				UGCategory cat = catIter.next();
				categoryNames.add(cat.getDisplayName());
			}		
			try {
				Date now = new Date();
				cacheClient.cacheObject(new CacheableList<String>(categoryNames, String.class,  "CategoryNames", timeout, now));
				logger.debug(method + "Cached list of pencasts.");
			} 
			catch ( Exception ex ) {
				logger.error(method + "Unable to write object to the Memcache Server " + ex.getMessage());
				logger.debug(method + "", ex);
			}		
		}

		return categoryNames;
	}

	/**
	 * <p></p>
	 * 
	 * @param pencast
	 * 
	 * @return
	 */
	private Document getFlashXmlFile(PencastVo pencast) {

		if (pencast.isFlashFileFound()) {
			File flashXmlFile = new File(pencast.getFlashXmlPath());
			Document flashXmlDom = FlashXmlParser.parse(flashXmlFile);
			return flashXmlDom;
		}
		return null;
	}

	/**
	 * <p></p>
	 */
	@Transactional
	public List<PencastVo> getMostViewedPencasts(int start, int fetchSize) {

		String method = "getMostViewedPencasts():  ";
		
		ListType type = CacheablePencastList.ListType.MOSTVIEWED;

		Cacheable object = null;
		int timeout = ConfigClient.getTimeout(CACHE_TIMEOUT_KEY_MOST_VIEWED);

		try {
			logger.debug(method + "Looking up pencasts in cache.");
			object = cacheClient.getCachedObject(CacheablePencastList.getCacheKey(type, start+"-"+fetchSize));
		} 
		catch ( ClassNotFoundException cnfe ) {
			logger.error(method + "Unable to read from the Memcache Server " + cnfe.getMessage());
			logger.debug(method + "", cnfe);
		}
		catch ( IOException ioe ) {
			logger.error(method + "Unable to read from the Memcache Server " + ioe.getMessage());
			logger.debug(method + "", ioe);
		}
		List<PencastVo> list = null;
		List<PencastVo> filteredList = null;
		
		if ( object != null ) {
			if ( object instanceof CacheablePencastList ) {
				logger.debug(method + "Found list of cached pencasts.");
				list = ((CacheablePencastList)object).getPencastVoList();
			}
			else {
				logger.error(method + "Cached object was not an instance of CacheablePencastList.");
			}
		}
		else {
			logger.debug(method + "*** Going to the database to fetch pencasts.");
			list = pencastDao.getMostViewedPencasts(start, fetchSize);
			filteredList = filterList(list);
			
			try {
				Date now = new Date();
				cacheClient.cacheObject(new CacheablePencastList(filteredList, type, start+"-"+fetchSize, timeout, now));
				logger.debug(method + "Cached list of pencasts.");
			} 
			catch ( IOException ioe ) {
				logger.error(method + "Unable to write object to the Memcache Server " + ioe.getMessage());
				logger.debug(method + "", ioe);
			}
			return filteredList;
		}
		return list;
	}

	/**
	 * <p>Returns a list of pencasts tagged with the given category name, 
	 * <code>null</code> if none are found.</p>
	 * 
	 * @param categoryName The name of the category to search on.
	 * 
	 * @return a list of pencasts tagged with the given category name, 
	 * <code>null</code> if none are found.</p>
	 */
	@Transactional
	public List<PencastVo> getPencastsByCategoryName(String categoryName, int start, int fetchSize) {

		String method = "getPencastsByCategoryName():  ";
		
		//		List<PencastVo> list = pencastDao.getPencastsByCategoryName(categoryName);
		//		List<PencastVo> list = pencastDao.getPencastsByCategoryName(categoryName, start, fetchSize);
		ListType type = CacheablePencastList.ListType.BY_CATEGORY_NAME;

		Cacheable object = null;
		int timeout = ConfigClient.getTimeout(CACHE_TIMEOUT_KEY_CATEGORY);
		
		try {
			logger.debug(method + "Looking up pencasts in cache.");
			object = cacheClient.getCachedObject(CacheablePencastList.getCacheKey(type, categoryName + "-" + start + "-" + fetchSize));
		} 
		catch ( ClassNotFoundException cnfe ) {
			logger.error(method + "Unable to read from the Memcache Server " + cnfe.getMessage());
			cnfe.printStackTrace();
		}
		catch (IOException ioe) {
			logger.error(method + "Unable to read from the Memcache Server " + ioe.getMessage());
			ioe.printStackTrace();
		}
		
		List<PencastVo> list = null;
		List<PencastVo> filteredList = null;
		
		if ( object != null ) {
			if ( object instanceof CacheablePencastList ) {
				logger.debug(method + "Found list of cached pencasts.");
				list = ((CacheablePencastList)object).getPencastVoList();
			}
			else {
				logger.error(method + "Cached object was not an instance of CacheablePencastList.");
			}
		} 
		else {
			logger.debug(method + "*** Going to the database to fetch pencasts.");
			list = pencastDao.getPencastsByCategory(categoryName, start, fetchSize);
			filteredList = filterList(list);
			
			try {
				Date now = new Date();
				cacheClient.cacheObject(new CacheablePencastList(filteredList, type, categoryName + "-" + start + "-" + fetchSize, timeout, now));
				logger.debug(method + "Cached list of pencasts.");
			} 
			catch ( IOException ioe ) {
				logger.error(method + "Unable to write object to the Memcache Server " + ioe.getMessage());
				logger.debug(method + "Exception thrown.", ioe);
			}
			return filteredList;
		}
		return list;
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	@Transactional
	public List<PencastVo> getRecentPencasts(int start, int fetchSize) {

		String method = "getRecentPencasts():  ";
		
		ListType type = CacheablePencastList.ListType.RECENT;

		Cacheable object = null;
		int timeout = ConfigClient.getTimeout(CACHE_TIMEOUT_KEY_RECENT);
		
		try {
			logger.debug(method + "Looking up pencasts in cache.");
			object = cacheClient.getCachedObject(CacheablePencastList.getCacheKey(type, start+"-"+fetchSize));
		} 
		catch ( Exception ex ) {
			logger.error(method + "Unable to read from the Memcache Server " + ex.getMessage());
			logger.debug(method + "", ex);
		}
		List<PencastVo> list = null;
		List<PencastVo> filteredList = null;
		
		if ( object != null ) {
			if ( object instanceof CacheablePencastList ) {
				logger.debug(method + "Found list of cached pencasts.");
				list = ((CacheablePencastList)object).getPencastVoList();
 			}
			else {
				logger.error(method + "Cached object was not an instance of CacheablePencastList.");
			}
		} 
		else {
			logger.debug(method + "*** Going to the database to fetch pencasts.");
			list = pencastDao.getRecentPencasts(start, fetchSize);
			filteredList = filterList(list);
			
			try {
				Date now = new Date();
				cacheClient.cacheObject(new CacheablePencastList(filteredList, type, start + "-" + fetchSize, timeout, now));
				logger.debug(method + "Cached list of pencasts.");
			} 
			catch ( Exception ex ) {
				logger.error(method + "Unable to write object to the Memcache Server " + ex.getMessage());
				logger.debug(method + "", ex);
			}
			return filteredList;
		}
		return list;
	}

	/**
	 * <p>Returns a list of top <code>num</code> pencasts.</p>
	 * 
	 * @param num The number of pencasts to return.
	 * 
	 * @return a list of the top <code>num</code> pencasts.
	 */
	@Transactional
	public List<PencastVo> getTopPencasts(int start, int fetchSize) {

		String method = "getTopPencasts():  ";
		
		ListType type = CacheablePencastList.ListType.TOP;

		Cacheable object = null;
		int timeout = ConfigClient.getTimeout(CACHE_TIMEOUT_KEY_TOP);

		try {
			logger.debug(method + "Looking up pencasts in cache.");
			object = cacheClient.getCachedObject(CacheablePencastList.getCacheKey(type, start + "-" + fetchSize));
		} 
		catch ( Exception ex ) {
			logger.error(method + "Unable to read from the Memcache Server " + ex.getMessage());
			logger.debug("", ex);
		}
		List<PencastVo> list = null;
		List<PencastVo> filteredList = null;
		
		if ( object != null ) {
			if ( object instanceof CacheablePencastList ) {
				logger.debug(method + "Found list of cached pencasts.");
				list = ((CacheablePencastList)object).getPencastVoList();
			}
			else {
				logger.error(method + "Cached object was not an instance of CacheablePencastList.");
			}
		} 
		else {
			logger.debug(method + "*** Going to the database to fetch pencasts.");
			list = pencastDao.getTopPencasts(start, fetchSize);
			filteredList = filterList(list);

			try {
				Date now = new Date();
				cacheClient.cacheObject(new CacheablePencastList(filteredList, type, start + "-" + fetchSize, timeout, now));
				logger.debug(method + "Cached list of pencasts.");
			} 
			catch ( Exception ex ) {
				logger.error(method + "Unable to write object to the Memcache Server " + ex.getMessage());
				logger.debug(method + "Exception thrown while attempting to cache list of Top pencasts.  " + ex.getMessage());
			}
			return filteredList;
		}
		return list;
	}

	/**
	 * <p></p>
	 * 
	 * @param list The original List to filter out pencasts with existing files. 
	 * 
	 * @return
	 */
	private List<PencastVo> filterList(List<PencastVo> list) {
		
		StringBuilder logBuilder = new StringBuilder();
		logBuilder.append("\n\n");
		
		//	Create a List of pencasts known to have an existing 'flash.xml' file.
		List<PencastVo> filteredList = new ArrayList<PencastVo>();
		Iterator<PencastVo> iter = list.iterator();
		while (iter.hasNext()) {
			PencastVo pencast = iter.next();
			logBuilder.append(pencast.getShortId());
			
			boolean found = findFlashXmlFile(pencast);
			if (found) {
				logBuilder.append(" <-- found");
				
				addFlashXmlInfo(pencast);
				
				filteredList.add(pencast);				
			}
			logBuilder.append("\n");
		}
		logger.debug(logBuilder.toString());
		return filteredList;
	}

	/**
	 * @param pencast
	 */
	private void addFlashXmlInfo(PencastVo pencast) {
		Document flashXmlDom = getFlashXmlFile(pencast);
		
		//	Add List of audio files to the pencast.
		List<PencastAudio> audios = FlashXmlParser.getAudioClips(flashXmlDom, pencast.getDerivativePath());
		if (audios != null) {
			pencast.setAudioClips(audios);
		}
		
		//	Add List of page files to the pencast.
		List<PencastPage> pages = FlashXmlParser.getPages(flashXmlDom, pencast.getDerivativePath());
		if (pages != null) {
			pencast.setPages(pages);
		}
		
		String durStr = flashXmlDom.valueOf("/pencast/duration");
		logger.debug("filterList():  durStr:  " + durStr);
		if ((durStr != null) && (!"".equals(durStr))) {
			Long duration = new Long(durStr);
			pencast.setAudioDuration(duration);
		}
		else {
			pencast.setAudioDuration(new Long(0));
		}
	}

	/**
	 * <p>Returns a list of pencasts a user owns, <code>null</code> if
	 * none are found.</p>
	 * 
	 * @param token The unique <code>token</code> of the logged-in user.
	 * 
	 * @return a list of pencasts.  Returns <code>null</code> if no
	 * pencasts were found.
	 */
	@Transactional
	public List<PencastVo> getUserPencasts(String token) {

		String method = "getUserPencasts():  ";
		
		if (token != null) {
			
			//	TODO:  Decide if login check happens here or in CommunityController.getPencastsByUser().
//			if (userService.isUserLoggedInByToken(token)) {
				
				ActiveUser aUser = userDao.findActiveUserByToken(token);
				UserProfile uProfile = userDao.findUserProfileByActiveUser(aUser);
				
				if (uProfile != null) {
					byte[] primaryKey = uProfile.getPrimaryKey();
			
					String primaryKeyStr = new String(primaryKey);
			
					ListType type = CacheablePencastList.ListType.USER;
			
					Cacheable object = null;
					int timeout = ConfigClient.getTimeout(CACHE_TIMEOUT_KEY_USER);
		
					try {
						logger.debug(method + "Looking up pencasts in cache.");
						object = cacheClient.getCachedObject(CacheablePencastList.getCacheKey(type, primaryKeyStr));
					} 
					catch ( Exception ex ) {
						logger.error(method + "Unable to read from the Memcache Server " + ex.getMessage());
						logger.debug("", ex);
					}
					List<PencastVo> list = null;
					List<PencastVo> filteredList = null;
					
					if ( object != null ) {
						if ( object instanceof CacheablePencastList ) {
							logger.debug(method + "Found list of cached pencasts.");
							list = ((CacheablePencastList)object).getPencastVoList();
							
							Date now = new Date();
							Date fromDate = DateUtils.addMinutes(now, (-1 * timeout));
							
							if (pencastDao.recentUserPencastsExist(primaryKey, fromDate)) {
								List recents = pencastDao.getRecentUserPencasts(primaryKey, fromDate);
								list.addAll(recents);
							}
						}
						else {
							logger.error(method + "Cached object was not an instance of CacheablePencastList.");
						}
					} 
					else {
						logger.debug(method + "*** Going to the database to fetch pencasts.");
						list = pencastDao.getUserPencasts(primaryKey);
						filteredList = filterList(list);
						
						try {
							Date now = new Date();
							cacheClient.cacheObject(new CacheablePencastList(filteredList, type, primaryKeyStr, timeout, now));
							logger.debug(method + "Cached list of pencasts.");
						} 
						catch ( Exception ex ) {
							logger.error(method + "Unable to write object to the Memcache Server " + ex.getMessage());
							logger.debug(method + "", ex);
						}
						return filteredList;
					}
				//		uploadPencastFilesToS3(list);
		
					return list;
				}
//			}
//			else {
//				//	TODO:  Log to a database as part of data metrics.
//				logger.debug(method + "User with token '" + token + "' was not logged in.");
//			}
		}
		else {
			//	TODO:  Log to a database as part of data metrics.
			logger.debug(method + "No token provided.");
		}
		return null;
	}

}

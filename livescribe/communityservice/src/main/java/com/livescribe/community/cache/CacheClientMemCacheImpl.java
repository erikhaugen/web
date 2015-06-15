package com.livescribe.community.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.livescribe.base.DateUtils;

public class CacheClientMemCacheImpl<T extends Cacheable> implements CacheClient<T> {

	private static final Logger logger = Logger.getLogger(CacheClientMemCacheImpl.class);
	private String _selfKey;
	private SockIOPool _pool;
	private MemCachedClient _memcacheClient;

	private boolean available;

	public CacheClientMemCacheImpl(String key, String[] hosts) {
		this._selfKey = key;
		this._pool = SockIOPool.getInstance(key);
		this._pool.setServers(hosts);
		this._pool.initialize();

		this._memcacheClient = new MemCachedClient(key);
		this._memcacheClient.setPrimitiveAsString(true);
		this._memcacheClient.setCompressEnable(true);
		this._memcacheClient.setCompressThreshold(64*1024);	
		
		// Very crude for now. If memcache is down when app is started, it will not reconnect.
		// Uncomment the code block below to achieve that, but that requires a class in a different
		// project. That will cause the deletion of all builds in Hudson from the workspace. Once
		// the apps is stableized or we figure out where to store the builds, we can add the code block
		// below
		// Change is needed in
		// 		-	src/main/webapp/WEB-INF/web.xml
		//		-	pom.xml
		//		-	parent/pom.xml
		// All changes have been made but are commented out
		// For developer, you need to checkout lswebutils parallel to other projects, and run 
		//     mvn eclipse:clean eclipse:eclipse 
		// from communityserver/parent folder and refresh your eclipse work space, and import the
		// project lswebutils
		this.available = this._memcacheClient.set("test", "test-str", DateUtils.addMinutes(new Date(), 1));
		
		if ( !this.available ) {
			logger.error("Memcache client was not detected. Running without the cache... Please restart after memcache is running to enable and leverage caching ..");
		} else {
			logger.info("Set up Memcached Client");
		}
//		final com.livescribe.concurrent.utils.SchedulerUtils scheduler = new com.livescribe.concurrent.utils.SchedulerUtils(1);
//		scheduler.submitForMinutelyExecution(15, new Runnable() {
//			@Override
//			public void run() {
//				available = _memcacheClient.set("test", "test-str", DateUtils.addMinutes(new Date(), 1));	
//			}
//		}, true);
//		com.livescribe.web.utils.listener.LSServletContextListener.addShutdownListener(new com.livescribe.web.utils.listener.ShutdownListener() {
//			@Override
//			public void shutdown(javax.servlet.ServletContextEvent event) {
//				scheduler.cleanup(true);
//			}
//			
//		});
	}

	public boolean isAvailable() {
		return available;
	}

	public String getKey() {
		return _selfKey;
	}

	public boolean cacheObject (Cacheable cacheable) throws IOException {
		boolean returnValue = false;
		if ( available ) {
			List<String> cacheKeys = cacheable.getCacheKeys();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(cacheable);
			String encodedString = Base64.encodeBase64String(baos.toByteArray());
			//System.out.println("Writing to MMS \n" +encodedString);
			for ( String cacheKey : cacheKeys ) {
				logger.debug("cacheObject():  Caching object using cache key = '" + cacheKey + "'");
				returnValue = _memcacheClient.set(cacheKey, encodedString, cacheable.getExpiresAt());
			}
		}
		return returnValue;
	}

	public Cacheable getCachedObject(String key) throws IOException, ClassNotFoundException {
		
		String method = "getCachedObject():  ";
		
		if ( available ) {
			
			logger.debug(method + "Locating cached object with key = '" + key + "'");
			
			Object obj = _memcacheClient.get(key);
			if ( obj != null  ) {
				if ( obj instanceof String) {
					String encodedString = (String) obj;
					//System.out.println("Reading from MMS \n" +encodedString);
					byte[] byteArray = Base64.decodeBase64(encodedString);
					ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
					ObjectInputStream in = new ObjectInputStream(bais);
					Object readObject = in.readObject();
					if ( readObject instanceof Cacheable ) {
						logger.debug(method + "Found Cacheable object.");
						return (Cacheable) readObject;
					}
					logger.debug(method + "Cacheable object not found.");
				}
			} else {
				return null;
			}
			throw new ClassCastException("Expected an Object of type " + 
					Cacheable.class + ", found " + obj.getClass());
		}
		return null;
	}

	public static void main(String[] argv) throws Exception {
		System.out.println("creating CacheClientMemCacheImpl ");
		CacheClientMemCacheImpl<CacheAccessControl.CacheableDummyClass> client = 
			new CacheClientMemCacheImpl<CacheAccessControl.CacheableDummyClass>("test", new String[] {"localhost:11211"});
		System.out.println("created CacheClientMemCacheImpl " +  client.getKey());

		//		boolean stored = client._memcacheClient.set("test", "This is a test String");
		//
		//		if ( stored ) {
		//			System.out.println("Value was stored");
		//			String result = (String) client._memcacheClient.get("test");
		//			System.out.println("Extracted Value - " + result);
		//		} else {
		//			System.out.println("Value was not stored");
		//			System.exit(0);
		//		}


		System.out.println("creating CacheableDummyClass ");

		CacheAccessControl.CacheableDummyClass object1 = new CacheAccessControl.CacheableDummyClass("test1");

		System.out.println("Created CacheableDummyClass " + object1.getValue() + "expires at" + DateUtils.getAllFieldsDateFormat().format(object1.getExpiresAt()));

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(object1);

		byte[] bArr = baos.toByteArray();

		String str = Base64.encodeBase64String(bArr);

		byte[] bArrNew = Base64.decodeBase64(str);
		ByteArrayInputStream bais = new ByteArrayInputStream(bArrNew);
		ObjectInputStream in = new ObjectInputStream(bais);
		Object reconObj = in.readObject();

		System.out.println("Reconstructed Obj " + reconObj.getClass().getName());

		System.out.println("caching CacheableDummyClass " + object1.getValue());
		client.cacheObject(object1);
		System.out.println("cached CacheableDummyClass " + object1.getValue());

		System.out.println("fetching CacheableDummyClass " + object1.getValue());
		Cacheable object2 = client.getCachedObject(object1.getValue());
		if ( object2 != null ) {
			if ( object2 instanceof CacheAccessControl.CacheableDummyClass ) {
				CacheAccessControl.CacheableDummyClass obj = (CacheAccessControl.CacheableDummyClass)object2;
				System.out.println("fecthed CacheableDummyClass " + obj.getValue());
			} else {
				System.out.println("fecthed CacheableDummyClass but return class is invalid " + object2.getClass() );
			}
		} else {
			System.out.println("Object is null, this was not as expected" );
			System.exit(0);
		}

		System.out.println("Sleeping for object to become stale");

		Thread.sleep(1 * 60 * 1000 + 5);
		System.out.println("woke up, the  object should be stale now");

		object2 = client.getCachedObject(object1.getValue());
		if ( object2 != null ) {
			System.out.println("Got Object instead of null return class is  " + object2.getClass() );
			if ( object2 instanceof CacheAccessControl.CacheableDummyClass ) {
				CacheAccessControl.CacheableDummyClass obj = (CacheAccessControl.CacheableDummyClass)object2;
				System.out.println("fecthed CacheableDummyClass " + obj.getValue());
			} else {
				System.out.println("fecthed CacheableDummyClass but return class is invalid " + object2.getClass() );
			}
		} else {
			System.out.println("Object is null as expected" );
		}		
	}
}

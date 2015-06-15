package com.livescribe.community.cache;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.livescribe.base.DateUtils;
import com.livescribe.base.StringUtils;
import com.livescribe.community.config.ConfigClient;

public class CacheAccessControl {

	private static final Logger logger = Logger.getLogger(CacheAccessControl.class);
	public static final String DEFAULT_CACHE_KEY = "default";

	private static final String[] DEF_MEM_CACHE_HOSTS = ConfigClient.getMemcacheServerList(DEFAULT_CACHE_KEY).toArray(new String[0]);

	private final Map<String, CacheClient<? extends Cacheable>> CLIENT_MAP = 
		new HashMap<String, CacheClient<? extends Cacheable>>();

	private static CacheAccessControl _self = new CacheAccessControl();

	private CacheAccessControl() {
		try {
			logger.debug("DEFAULT_CACHE_KEY is " + DEFAULT_CACHE_KEY);
			CacheClient<Cacheable> client = new CacheClientMemCacheImpl<Cacheable>(DEFAULT_CACHE_KEY, DEF_MEM_CACHE_HOSTS);
			CLIENT_MAP.put(DEFAULT_CACHE_KEY, client);
		} catch ( Exception ex) {
			logger.error("Unable to create CacheAccessControl " + ex.getMessage());
			logger.debug("DEFAULT_CACHE_KEY is " + DEFAULT_CACHE_KEY);
			logger.debug("MEM_CACHE_HOSTS size " + DEF_MEM_CACHE_HOSTS.length);
		}
	}

	public static CacheAccessControl getInstance() {
		return _self;
	}

	public <T extends Cacheable> CacheClient<T> getCacheClient(Class<T> clazz) {
		return getCacheClient(DEFAULT_CACHE_KEY, clazz);
	}

	@SuppressWarnings("unchecked")
	public <T extends Cacheable> CacheClient<T> getCacheClient(String key, Class<T> clazz) {
		key = ((StringUtils.isBlank(key)) ? DEFAULT_CACHE_KEY : key);
		CacheClient<T> client = (CacheClient<T>) CLIENT_MAP.get(key);
		if ( client == null ) {
			synchronized (CacheAccessControl.class) {
				client = (CacheClient<T>) CLIENT_MAP.get(key);
				if ( client == null ) {
					String[] memcacheHosts = ConfigClient.getMemcacheServerList(key).toArray(new String[0]);
					if ( memcacheHosts != null && memcacheHosts.length > 0 ) {
						client = new CacheClientMemCacheImpl<T>(key, memcacheHosts);
						CLIENT_MAP.put(key, client);
					}
				}
			}
		}
		return client;
	}


	public static void main(String[] argv) throws Exception {

		System.out.println("Memcache list "  + StringUtils.print(ConfigClient.getMemcacheServerList(DEFAULT_CACHE_KEY), ", "));
		com.mchange.v2.c3p0.ComboPooledDataSource ds = ConfigClient.getBean("consumerDatasource", com.mchange.v2.c3p0.ComboPooledDataSource.class);
		System.out.println("Got the consumerDatasource");
		
		System.out.println("URL = " + ds.getJdbcUrl());
		
		System.out.println("getDefaultFetchSize" + ConfigClient.getDefaultFetchSize() );
		System.out.println("getMaxFetchSize" + ConfigClient.getMaxFetchSize() );
		
		Connection conn = ds.getConnection();
		
		if ( conn != null ) {
			System.out.println("Got the consumerDatasource Connection");
		}

		CacheAccessControl cac = CacheAccessControl.getInstance();
		CacheClient<CacheableDummyClass> client = (CacheClient<CacheableDummyClass>) cac.getCacheClient(CacheableDummyClass.class);

		System.out.println("creating CacheableDummyClass ");
		CacheableDummyClass object1 = new CacheableDummyClass("test1");
		System.out.println("Created CacheableDummyClass " + object1.getValue() + "expires at" + DateUtils.getAllFieldsDateFormat().format(object1.getExpiresAt()));

		System.out.println("caching CacheableDummyClass " + object1.getValue());
		boolean result = client.cacheObject(object1);
		System.out.println("cached CacheableDummyClass " + object1.getValue() + "-" + result);

		if ( result ) {
			System.out.println("fetching CacheableDummyClass " + object1.getValue());
			Cacheable object2 = client.getCachedObject(object1.getValue());
			if ( object2 != null ) {
				if ( object2 instanceof CacheableDummyClass ) {
					CacheableDummyClass obj = (CacheableDummyClass)object2;
					System.out.println("fecthed CacheableDummyClass " + obj.getValue());
				} else {
					System.out.println("fecthed CacheableDummyClass but return class is invalid " + object2.getClass() );
				}
				System.out.println("Sleeping for object to become stale");

				Thread.sleep(1 * 60 * 1000 + 5);
				System.out.println("woke up, the  object should be stale now");

				object2 = client.getCachedObject(object1.getValue());
				if ( object2 != null ) {
					System.out.println("Got Object instead of null return class is  " + object2.getClass() );
					if ( object2 instanceof CacheableDummyClass ) {
						CacheableDummyClass obj = (CacheableDummyClass)object2;
						System.out.println("fecthed CacheableDummyClass " + obj.getValue());
					} else {
						System.out.println("fecthed CacheableDummyClass but return class is invalid " + object2.getClass() );
					}
				} else {
					System.out.println("Object is null as expected" );
				}
			} else {
				System.out.println("Object is null, this was not as expected" );
			}			
		}
	}

	static class CacheableDummyClass implements Cacheable {

		private List<String> cacheKeys = new ArrayList<String>();

		private Date expiresAt;

		private String value;

		public CacheableDummyClass() {

		}

		public CacheableDummyClass(String value) {
			cacheKeys.add(value);
			this.value = value;
			this.expiresAt = DateUtils.addMinutes(new Date(), 1);
		}

		@Override
		public List<String> getCacheKeys() {
			return cacheKeys;
		}

		@Override
		public Date getExpiresAt() {
			return expiresAt;
		}

		public String getValue() {
			return value;
		}

		@Override
		public void readExternal(ObjectInput in) throws IOException,
		ClassNotFoundException {
			String str = in.readUTF();
			if ( "CacheableDummyClass".equals(str) ) {
				value = in.readUTF();
				cacheKeys.add(value);
				String dateStr = in.readUTF();
				try {
					expiresAt = DateUtils.getAllFieldsDateFormat().parse(dateStr);
				} catch ( ParseException pe ) {
					throw new ClassNotFoundException("Expected Date field not found - " + dateStr);
				}
			} else {
				throw new ClassNotFoundException("Expected Prefix String - CacheableDummyClass not found, instead found " + str );
			}

		}

		@Override
		public void writeExternal(ObjectOutput out) throws IOException {
			out.writeUTF("CacheableDummyClass");
			out.writeUTF(value);			
			out.writeUTF(DateUtils.getAllFieldsDateFormat().format(expiresAt));
		}
	}
}

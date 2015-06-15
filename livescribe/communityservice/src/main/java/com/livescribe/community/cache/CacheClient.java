package com.livescribe.community.cache;

import java.io.IOException;

public interface CacheClient<T extends Cacheable> {

	public String getKey();
	
	public boolean isAvailable() throws IOException;
	public boolean cacheObject (Cacheable object) throws IOException;
	public Cacheable getCachedObject(String key) throws IOException, ClassNotFoundException;
}

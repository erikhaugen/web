package com.livescribe.community.cache;

import java.util.Date;
import java.util.List;

public interface Cacheable extends java.io.Externalizable {
	public List<String> getCacheKeys();
	public Date getExpiresAt();
}

/**
 * 
 */
package com.livescribe.community.cache;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.List;

import com.livescribe.base.DateUtils;
import com.livescribe.base.StringUtils;
import com.livescribe.community.orm.PencastAudio;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CacheablePencastAudio extends PencastAudio implements Cacheable {

	private Date cachedDate;
	
	/**
	 * 
	 */
	public CacheablePencastAudio() {
		this.expiresAt = DateUtils.addHours(new Date(), 15);
	}
	
	public CacheablePencastAudio(PencastAudio source) {
		this(source, 15);
	}
	
	public CacheablePencastAudio(PencastAudio source, int cacheInterval) {
		super(source);
		this.expiresAt = DateUtils.addHours(new Date(), cacheInterval);
	}

	/* (non-Javadoc)
	 * @see com.livescribe.community.cache.Cacheable#getCachedDate()
	 */
	public Date getCachedDate() {
		return this.cachedDate;
	}

	/* (non-Javadoc)
	 * @see com.livescribe.community.cache.Cacheable#getCacheKeys()
	 */
	@Override
	public List<String> getCacheKeys() {

		return null;
	}

	@Override
	public Date getExpiresAt() {
		return expiresAt;
	}
	
	Date expiresAt;
	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}
	
	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		
		String str = in.readUTF();
		if ( "CacheablePencastAudio".equals(str) ) {
			setFilePath(in.readUTF());
			setType(in.readUTF());
			setBeginTime(in.readLong());
			setDuration(in.readLong());
			setFileSize(in.readLong());
			setFileUrl(in.readUTF());
		}		
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

		out.writeUTF("CacheablePencastAudio");
		
		if (StringUtils.isBlank(getFilePath())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getFilePath());
		}

		if (StringUtils.isBlank(getType())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getType());
		}
		
		Long bt = getBeginTime();
		if (bt != null) {
			out.writeLong(bt);
		}
		else {
			out.writeLong(0);
		}

		Long dur = getDuration();
		if (dur != null) {
			out.writeLong(dur);
		}
		else {
			out.writeLong(0);
		}

		Long fs = getFileSize();
		if (fs != null) {
			out.writeLong(fs);
		}
		else {
			out.writeLong(0);
		}
		
		if (StringUtils.isBlank(getFileUrl())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getFileUrl());
		}
	}

}

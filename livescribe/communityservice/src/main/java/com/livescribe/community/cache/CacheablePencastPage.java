/**
 * 
 */
package com.livescribe.community.cache;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.livescribe.base.DateUtils;
import com.livescribe.community.orm.PencastPage;

/**
 * @author kmurdoff
 *
 */
public class CacheablePencastPage extends PencastPage implements Cacheable {

	/**
	 * 
	 */
	public CacheablePencastPage() {
		this.expiresAt = DateUtils.addHours(new Date(), 15);		
	}
	
	public CacheablePencastPage(PencastPage source) {
		this(source, 15);
	}
	
	public CacheablePencastPage(PencastPage source, int cacheInterval) {
		super(source);
		this.expiresAt = DateUtils.addHours(new Date(), cacheInterval);
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
	 * @see com.livescribe.community.cache.Cacheable#getCacheKeys()
	 */
	@Override
	public List<String> getCacheKeys() {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		
		String str = in.readUTF();
		if ( "CacheablePencastPage".equals(str) ) {
			setPageId(in.readInt());
			setLabel(in.readUTF());
			setWidth(in.readInt());
			setHeight(in.readInt());
			setBackground(in.readUTF());
			setFormat(in.readUTF());
			setFileSize(in.readLong());
			setFilePath(in.readUTF());
			setFileUrl(in.readUTF());
			setExists(in.readBoolean());
		}		
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		
		out.writeUTF("CacheablePencastPage");
		
		Integer pid = getPageId();
		if (pid != null) {
			out.writeInt(pid);
		}
		else {
			out.writeInt(-1);
		}
		
		if (StringUtils.isBlank(getLabel())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getLabel());
		}
		
		Integer w = getWidth();
		if (w != null) {
			out.writeInt(w);
		}
		else {
			out.writeInt(0);
		}

		Integer h = getHeight();
		if (h != null) {
			out.writeInt(h);
		}
		else {
			out.writeInt(0);
		}
		
		if (StringUtils.isBlank(getBackground())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getBackground());
		}

		if (StringUtils.isBlank(getFormat())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getFormat());
		}

		Long fs = getFileSize();
		if (fs != null) {
			out.writeLong(fs);
		}
		else {
			out.writeLong(0);
		}
		
		if (StringUtils.isBlank(getFilePath())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getFilePath());
		}

		if (StringUtils.isBlank(getFileUrl())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getFileUrl());
		}

		Boolean ex = exists();
		if (ex != null) {
			out.writeBoolean(ex);
		}
		else {
			out.writeBoolean(false);
		}
	}
}

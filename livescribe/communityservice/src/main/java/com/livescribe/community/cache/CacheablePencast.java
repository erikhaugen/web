package com.livescribe.community.cache;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.livescribe.base.DateUtils;
import com.livescribe.base.StringUtils;
import com.livescribe.community.orm.PencastAudio;
import com.livescribe.community.orm.PencastPage;
import com.livescribe.community.view.vo.PencastVo;

public class CacheablePencast extends PencastVo implements Cacheable {
	
	private static final long serialVersionUID = 1L;
	public static final String CACHEKEY_PRIMARYKEY = "PencastVo-PK-";
	public static final String CACHEKEY_SHORTID = "PencastVo-shortId-";
	
	private List<String> cacheKeys = new ArrayList<String>();
	private Date cachedDate;
	private Date expiresAt;
	private String cacheKeyInfo;
	
	public CacheablePencast () {
		this.expiresAt = DateUtils.addHours(new Date(), 15);
	}
	
	public CacheablePencast(PencastVo source) {
		this(source, 15);
	}
	
	public CacheablePencast(PencastVo source, int cacheInterval) {
		super(source);
		this.expiresAt = DateUtils.addHours(new Date(), cacheInterval);
	}
	
	public CacheablePencast(PencastVo source, String cacheKeyInfo, int cacheIntervalMinutes, Date cachedDate) {
		
		super(source);
				
		this.cacheKeyInfo = cacheKeyInfo;
		this.cachedDate = cachedDate;
		this.expiresAt = DateUtils.addMinutes(cachedDate, cacheIntervalMinutes);
	}
	
	public void addCacheKey(String cacheKey) {
		if (StringUtils.isNotBlank(cacheKey))  {
			this.cacheKeys.add(cacheKey);
		} 
	}

	/**
	 * @return the cachedDate
	 */
	public Date getCachedDate() {
		return cachedDate;
	}

	/**
	 * @return the cacheKeyInfo
	 */
	public String getCacheKeyInfo() {
		return cacheKeyInfo;
	}

	@Override
	public List<String> getCacheKeys() {
		if (cacheKeys.size() == 0 ) {
			cacheKeys.add(getPKCacheKey(getPrimaryKey()));
			cacheKeys.add(getShortIdCacheKey(getShortId()));
		}
		return cacheKeys;
	}
	
	@Override
	public Date getExpiresAt() {
		return expiresAt;
	}
	
	public static final String getPKCacheKey(byte[] primaryKey) {
		return CACHEKEY_PRIMARYKEY + new String(primaryKey);
	}
	
	public static final String getShortIdCacheKey(String shortId) {
		return CACHEKEY_SHORTID + shortId;
	}
	
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		
		String str = in.readUTF();
		if ( "CacheablePencast".equals(str) ) {
			String dateStr = in.readUTF();
			try {
				Date fileDate = DateUtils.getAllFieldsDateFormat().parse(dateStr);
				setFileDate(fileDate);
			} catch ( ParseException pe ) {
				throw new ClassNotFoundException("Expected Date field not found");
			}
			setAuthorEmail(in.readUTF());
			setAuthorFirstName(in.readUTF());
			setAuthorLastName(in.readUTF());
			setAuthorPrimaryKey(Base64.decodeBase64(in.readUTF()));
			setAuthorScreenName(in.readUTF());
			setCategoryName(in.readUTF());
			setDerivativePath(in.readUTF());
			setDescription(in.readUTF());
			setDisplayName(in.readUTF());
			setFilePath(in.readUTF());
			setFileSize(in.readInt());
			setGlobalShare(in.readInt());
			setNumberOfViews(in.readInt());
			
			setPrimaryKey(Base64.decodeBase64(in.readUTF()));
			
			setRating(in.readDouble());
			setThumbnailUrl(in.readUTF());
			setShortId(in.readUTF());
			setAverageRating(in.readDouble());
			
			setAudioDuration(in.readLong());
			
			Integer clips = in.readInt();
			if (clips > 0) {
				ArrayList<PencastAudio> audioList = new ArrayList<PencastAudio>();
				for (int i = 0; i < clips; i++) {
					CacheablePencastAudio pa = (CacheablePencastAudio)in.readObject();
					audioList.add(pa);
				}
				setAudioClips(audioList);
			}
			
			Integer pps = in.readInt();
			if (pps > 0) {
				ArrayList<PencastPage> pgList = new ArrayList<PencastPage>();
				for (int i = 0; i < pps; i++) {
					CacheablePencastPage pp = (CacheablePencastPage)in.readObject();
					pgList.add(pp);
				}
				setPages(pgList);
			}
		} 
		else {
			throw new ClassNotFoundException("Expected Prefix String - CacheablePencast not found, instead found " + str );
		}
	}
	
	/**
	 * @param cachedDate the cachedDate to set
	 */
	public void setCachedDate(Date cachedDate) {
		this.cachedDate = cachedDate;
	}

	/**
	 * @param cacheKeyInfo the cacheKeyInfo to set
	 */
	public void setCacheKeyInfo(String cacheKeyInfo) {
		this.cacheKeyInfo = cacheKeyInfo;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		
		out.writeUTF("CacheablePencast");
		out.writeUTF(DateUtils.getAllFieldsDateFormat().format(getFileDate()));
		
		if (StringUtils.isBlank(getAuthorEmail())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getAuthorEmail());
		}
		
		if (StringUtils.isBlank(getAuthorFirstName())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getAuthorFirstName());
		}
		
		if (StringUtils.isBlank(getAuthorLastName())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getAuthorLastName());
		}
		
		if (getAuthorPrimaryKey() == null) {
			out.writeUTF("");
		}
		else {
			String userPkStr = Base64.encodeBase64String(getAuthorPrimaryKey());
			out.writeUTF(userPkStr);
		}
		
		if (StringUtils.isBlank(getAuthorScreenName())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getAuthorScreenName());
		}
		
		if (StringUtils.isBlank(getCategoryName())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getCategoryName());
		}
		
		if (StringUtils.isBlank(getDerivativePath())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getDerivativePath());
		}
		
		if (StringUtils.isBlank(getDescription())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getDescription());
		}
		
		if (StringUtils.isBlank(getDisplayName())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getDisplayName());
		}
		
		if (StringUtils.isBlank(getFilePath())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getFilePath());
		}
		
		Integer fs = getFileSize();
		if (fs != null) {
			out.writeInt(getFileSize());
		}
		else {
			out.writeInt(0);
		}
		
		Integer gs = getGlobalShare();
		if (gs != null) {
			out.writeInt(getGlobalShare());
		}
		else {
			out.writeInt(0);
		}
		
		Integer nov = getNumberOfViews();
		if (nov != null) {
			out.writeInt(getNumberOfViews());
		}
		else {
			out.writeInt(0);
		}
		
		String pkStr = Base64.encodeBase64String(getPrimaryKey());
		out.writeUTF(pkStr);
		
		Double rating = getRating();
		if (rating != null) {
			out.writeDouble(getRating());
		}
		else {
			out.writeDouble(0);
		}
		
		if (StringUtils.isBlank(getThumbnailUrl())) {
			out.writeUTF("");
		}
		else {
			out.writeUTF(getThumbnailUrl());
		}
		
		out.writeUTF(getShortId());
		
		Double avg = getAverageRating();
		if (avg != null) {
			out.writeDouble(getAverageRating());
		}
		else {
			out.writeDouble(0);
		}
		
		Long dur = getAudioDuration();
		if (dur != null) {
			out.writeLong(dur);
		}
		else {
			out.writeLong(new Long(0));
		}
		
		List<PencastAudio> acList = getAudioClips();
		if (acList != null) {
			int clips = acList.size();
			out.writeInt(clips);
			for (int i = 0; i < clips; i++) {
				CacheablePencastAudio pa = new CacheablePencastAudio(acList.get(i));
				out.writeObject(pa);
			}
		}
		else {
			out.writeInt(0);
		}
		
		List<PencastPage> ppList = getPages();
		if (ppList != null) {
			int pps = ppList.size();
			out.writeInt(pps);
			for (int i = 0; i < pps; i++) {
				CacheablePencastPage pp = new CacheablePencastPage(ppList.get(i));
				out.writeObject(pp);
			}
		}
		else {
			out.writeInt(0);
		}
	}
}

package com.livescribe.community.cache;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.livescribe.base.DateUtils;
import com.livescribe.community.view.vo.PencastVo;

public class CacheablePencastList implements Cacheable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum ListType {
		ALL_IDX,
		MOSTVIEWED,
		BY_CATEGORY_NAME,
		RECENT,
		TOP,
		USER
	}

	private List<PencastVo> pencastVoList;
	private ListType type;
	private String cacheKeyInfo;

	private List<String> cacheKeys = new ArrayList<String>();
	private Date expiresAt;
	private Date cachedDate;
	
	public CacheablePencastList() {}
	
	public CacheablePencastList(List<PencastVo> pencastVoList, ListType type, String cacheKeyInfo, Date cachedDate) {
		this(pencastVoList, type, cacheKeyInfo, 15, cachedDate);
	}
	
	public CacheablePencastList(List<PencastVo> pencastVoList, ListType type, String cacheKeyInfo, int cacheIntervalMinutes, Date cachedDate) {
		this.pencastVoList = pencastVoList;
		this.type = type;
		this.cacheKeyInfo = cacheKeyInfo;
		this.cacheKeys.add(getCacheKey(type, cacheKeyInfo));
		this.expiresAt = DateUtils.addMinutes(new Date(), cacheIntervalMinutes);
		this.cachedDate = cachedDate;
	}

	/**
	 * <p>Returns the date when this list was cached."</p>
	 * 
	 * @return the date this list was cached.
	 */
	public Date getCachedDate() {
		return cachedDate;
	}

	public static String getCacheKey(ListType type, String cacheKeyInfo) {
		return "ListPencastVO-"+type.toString()+"-"+cacheKeyInfo;
	}

	@Override
	public List<String> getCacheKeys() {
		return cacheKeys;
	}

	@Override
	public Date getExpiresAt() {
		return expiresAt;
	}

	public List<PencastVo> getPencastVoList() {
		return pencastVoList;
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		String str = in.readUTF();
		if ( "CacheablePencastList".equals(str) ) {
			String cachedDateStr = in.readUTF();
			try {
				cachedDate = DateUtils.getAllFieldsDateFormat().parse(cachedDateStr);
			} 
			catch ( ParseException pe ) {
				throw new ClassNotFoundException("Expected 'cachedDate' field not found - " + cachedDateStr);
			}			
			
			String dateStr = in.readUTF();
			try {
				expiresAt = DateUtils.getAllFieldsDateFormat().parse(dateStr);
			} 
			catch ( ParseException pe ) {
				throw new ClassNotFoundException("Expected Date field not found - " + dateStr);
			}
			type = ListType.valueOf(in.readUTF());
			cacheKeyInfo = in.readUTF();
			int size = in.readInt();
			List myList = new ArrayList();
			
			for ( int i = 0; i < size; i++ ) {
				CacheablePencast cPencast = (CacheablePencast)in.readObject();
				myList.add(cPencast);
			}

			pencastVoList = myList;
			cacheKeys.add(getCacheKey(type, cacheKeyInfo));
		} else {
			throw new ClassNotFoundException("Expected Prefix String - CacheablePencastList not found, instead found " + str );
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF("CacheablePencastList");
		out.writeUTF(DateUtils.getAllFieldsDateFormat().format(cachedDate));
		out.writeUTF(DateUtils.getAllFieldsDateFormat().format(expiresAt));
		out.writeUTF(type.name());
		out.writeUTF(cacheKeyInfo);
		out.writeInt(pencastVoList.size());

		for ( PencastVo pencast : pencastVoList ) {
			out.writeObject(new CacheablePencast(pencast));
		}

	}

}

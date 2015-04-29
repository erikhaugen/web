package com.livescribe.base.dao;

import java.util.Date;
import java.util.Map;
import com.livescribe.base.anno.Mappable;

/**
 * Abstract base class for all data objects. Easier to manage the standard fields
 * that we want in each Data Object,
 *   id
 *   version
 *   dateCreated
 *   dateUpdated
 *   
 * @author smukker
 *
 */
@Mappable
public abstract class LSBaseDaoObject {
	
	private long id;
	private long version;
	
	private Date dateCreated;
	private Date dateUpdated;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public void addIdToMap(Map<String, Object> map) {
		addIdToMap(map, "id");
	}
	
	public void addIdToMap(Map<String, Object> map, String key) {
		map.put(key, id);
	}
	
	public void setIdFromMap(Map<String, Object> map) {
		setIdFromMap(map, "id");
	}
	
	public void setIdFromMap(Map<String, Object> map, String key) {
		Object obj = map.get(key);
		
		if ( obj instanceof Number ) {
			id = (Long) obj;
		}
	}
}

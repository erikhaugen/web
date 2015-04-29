package com.livescribe.base.utils.cache;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.livescribe.base.DateUtils;

public class CacheableList<T> implements Cacheable {
	private static final long serialVersionUID = 1L;
	private List<T> objectList;

	private List<String> cacheKeys = new ArrayList<String>();
	private Date expiresAt;
	private Date cachedDate;
	
	private String listIdentification;

	private Class<T> clazz;

	public CacheableList() {

	}

	public CacheableList(List<T> objectList, Class<T> clazz, String listIdentification, Date cachedDate) {
		this(objectList, clazz, listIdentification, 15, cachedDate);
	}

	public CacheableList(List<T> objectList, Class<T> clazz, String listIdentification, int cacheIntervalMinutes, Date cachedDate) {
		if ( canCache(clazz) ) {
			this.clazz = clazz;
			this.objectList = objectList;
			cacheKeys.add(getCacheKey(listIdentification));
			this.expiresAt = DateUtils.addMinutes(new Date(), cacheIntervalMinutes);
			this.cachedDate = cachedDate;
			this.listIdentification = listIdentification;
		}
	}

	/**
	 * @return the cachedDate
	 */
	public Date getCachedDate() {
		return cachedDate;
	}

	public List<T> getCachedList() {
		return this.objectList;
	}

	@Override
	public List<String> getCacheKeys() {
		return cacheKeys;
	}

	@Override
	public Date getExpiresAt() {
		return expiresAt;
	}

	public static String getCacheKey(String listIdentification) {
		return "CacheableList-"+listIdentification;
	}

	protected boolean canCache(Class<T> clazz) {
		if ( !clazz.isPrimitive() && !Externalizable.class.isAssignableFrom(clazz) && !String.class.isAssignableFrom(clazz) ) {
			throw new ClassCastException("CacheableList can only be used for Primitive, java.lang.String and classes implementing Externalizable Interface");
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		String str = in.readUTF();
		if ( "CacheableList".equals(str) ) {
			String clazzName = in.readUTF();
			Class myclazz = Class.forName(clazzName);
			if ( canCache(myclazz) ) {
				clazz = myclazz;
				String dateStr = in.readUTF();
				try {
					expiresAt = DateUtils.getAllFieldsDateFormat().parse(dateStr);
				} catch ( ParseException pe ) {
					throw new ClassNotFoundException("Expected Date field not found - " + dateStr);
				}
				listIdentification = in.readUTF();
				int size = in.readInt();
				List myList = new ArrayList();
				if ( clazz.isPrimitive() ) {
					if ( Long.class.isAssignableFrom(clazz) ) {
						for ( int i = 0; i < size; i++ ) {
							myList.add(in.readLong());
						}
					} else if ( Integer.class.isAssignableFrom(clazz) ) {
						for ( int i = 0; i < size; i++ ) {
							myList.add(in.readInt());
						}			
					} else if ( String.class.isAssignableFrom(clazz) ) {
						for ( int i = 0; i < size; i++ ) {
							myList.add(in.readUTF());
						}
					} else if ( Float.class.isAssignableFrom(clazz) ) {
						for ( int i = 0; i < size; i++ ) {
							myList.add(in.readFloat());
						}
					} else if ( Double.class.isAssignableFrom(clazz) ) {
						for ( int i = 0; i < size; i++ ) {
							myList.add(in.readDouble());
						}
					} else if ( Byte.class.isAssignableFrom(clazz) ) {
						for ( int i = 0; i < size; i++ ) {
							myList.add(in.readByte());
						}
					} 
				} else {
					for ( int i = 0; i < size; i++ ) {
						myList.add(in.readObject());
					}
				}
				objectList = myList;
				cacheKeys.add(getCacheKey(listIdentification));
			}
		} else {
			throw new ClassNotFoundException("Expected Prefix String - CacheableList not found, instead found " + str );
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF("CacheableList");
		out.writeUTF(clazz.getName());
		out.writeUTF(DateUtils.getAllFieldsDateFormat().format(expiresAt));
		out.writeUTF(listIdentification);
		out.writeInt(objectList.size());
		if ( clazz.isPrimitive() ) {
			if ( Long.class.isAssignableFrom(clazz) ) {
				for ( T obj : objectList ) {
					out.writeLong((Long)obj);
				}
			} else if ( Integer.class.isAssignableFrom(clazz) ) {
				for ( T obj : objectList ) {
					out.writeInt((Integer)obj);
				}			
			} else if ( String.class.isAssignableFrom(clazz) ) {
				for ( T obj : objectList ) {
					out.writeUTF((String)obj);
				}
			} else if ( Float.class.isAssignableFrom(clazz) ) {
				for ( T obj : objectList ) {
					out.writeFloat((Float)obj);
				}
			} else if ( Double.class.isAssignableFrom(clazz) ) {
				for ( T obj : objectList ) {
					out.writeDouble((Double)obj);
				}
			} else if ( Byte.class.isAssignableFrom(clazz) ) {
				for ( T obj : objectList ) {
					out.writeByte((Byte)obj);
				}
			} 
		} else {
			for ( T obj : objectList ) {
				out.writeObject(obj);
			}
		}
	}
}

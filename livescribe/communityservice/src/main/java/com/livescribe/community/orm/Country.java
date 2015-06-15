/**
 * 
 */
package com.livescribe.community.orm;

import java.sql.Clob;

import org.apache.log4j.Logger;

/**
 * @author kmurdoff
 *
 */
public class Country  implements java.io.Serializable {

	Logger logger = Logger.getLogger(UGFile.class);

	private byte[] primaryKey;
	private Integer isActive;
	private String IsoCode2;
	private String IsoCode3;
	private Integer IsoNumericCode;
	private Clob metaInfo;
	private String name;
	
	public Country() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the primaryKey
	 */
	public byte[] getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @return the isActive
	 */
	public Integer getIsActive() {
		return isActive;
	}

	/**
	 * @return the isoCode2
	 */
	public String getIsoCode2() {
		return IsoCode2;
	}

	/**
	 * @return the isoCode3
	 */
	public String getIsoCode3() {
		return IsoCode3;
	}

	/**
	 * @return the isoNumericCode
	 */
	public Integer getIsoNumericCode() {
		return IsoNumericCode;
	}

	/**
	 * @return the metaInfo
	 */
	public Clob getMetaInfo() {
		return metaInfo;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(byte[] primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	/**
	 * @param isoCode2 the isoCode2 to set
	 */
	public void setIsoCode2(String isoCode2) {
		IsoCode2 = isoCode2;
	}

	/**
	 * @param isoCode3 the isoCode3 to set
	 */
	public void setIsoCode3(String isoCode3) {
		IsoCode3 = isoCode3;
	}

	/**
	 * @param isoNumericCode the isoNumericCode to set
	 */
	public void setIsoNumericCode(Integer isoNumericCode) {
		IsoNumericCode = isoNumericCode;
	}

	/**
	 * @param metaInfo the metaInfo to set
	 */
	public void setMetaInfo(Clob metaInfo) {
		this.metaInfo = metaInfo;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}

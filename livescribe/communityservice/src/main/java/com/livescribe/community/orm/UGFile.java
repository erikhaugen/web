package com.livescribe.community.orm;
// Generated Jun 17, 2010 10:50:10 AM by Hibernate Tools 3.3.0.GA


import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.livescribe.base.IOUtils;

/**
 * UGFile generated by hbm2java
 */
public class UGFile  implements java.io.Serializable {

	Logger logger = Logger.getLogger(UGFile.class);

	private String shortId;
	private byte[] categoryKey;
	//     private Clob contentDescription;
	private String description;
	private String displayName;
	private Date fileDate;
	private String fileName;
	private String filePath;
	private Integer globalShare;
	private Clob metaInfo;
	private byte[] primaryKey;
	private Double rating;
	private byte[] typeKey;
	private byte[] userProfileKey;
	private Integer views;
	private Integer fileSize;
	private String ipAddress;
	private String isSafeFile;
	private Integer inappropriateCounter;

	public UGFile() {
	}


	public UGFile(String shortId) {
		this.shortId = shortId;
	}
	public UGFile(String shortId, byte[] categoryKey, Clob contentDescription, String displayName, Date fileDate, String fileName, String filePath, Integer globalShare, Clob metaInfo, byte[] primaryKey, Double rating, byte[] typeKey, byte[] userProfileKey, Integer views, Integer fileSize, String ipAddress, String isSafeFile, Integer inappropriateCounter) {
		this.shortId = shortId;
		this.categoryKey = categoryKey;
		setContentDescription(contentDescription);
		this.displayName = displayName;
		this.fileDate = fileDate;
		this.fileName = fileName;
		this.filePath = filePath;
		this.globalShare = globalShare;
		this.metaInfo = metaInfo;
		this.primaryKey = primaryKey;
		this.rating = rating;
		this.typeKey = typeKey;
		this.userProfileKey = userProfileKey;
		this.views = views;
		this.fileSize = fileSize;
		this.ipAddress = ipAddress;
		this.isSafeFile = isSafeFile;
		this.inappropriateCounter = inappropriateCounter;
	}

	public String getShortId() {
		return this.shortId;
	}

	public void setShortId(String shortId) {
		this.shortId = shortId;
	}
	public byte[] getCategoryKey() {
		return this.categoryKey;
	}

	public void setCategoryKey(byte[] categoryKey) {
		this.categoryKey = categoryKey;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public Clob getContentDescription() {
		return null;
	}

	public void setContentDescription(Clob contentDescription) {
		try {
			this.description = IOUtils.getStringFromClob(contentDescription);
		} catch (SQLException e) {
			logger.error("SQLException while reading Description from Clob" + e.getMessage());
			logger.debug("", e);
		} catch (IOException e) {
			logger.error("IOException while reading Description from Clob" + e.getMessage());
			logger.debug("", e);
		}
	}
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public Date getFileDate() {
		return this.fileDate;
	}

	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}
	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Integer getGlobalShare() {
		return this.globalShare;
	}

	public void setGlobalShare(Integer globalShare) {
		this.globalShare = globalShare;
	}
	public Clob getMetaInfo() {
		return this.metaInfo;
	}

	public void setMetaInfo(Clob metaInfo) {
		this.metaInfo = metaInfo;
	}
	public Double getRating() {
		return this.rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}
	public byte[] getTypeKey() {
		return this.typeKey;
	}

	public void setTypeKey(byte[] typeKey) {
		this.typeKey = typeKey;
	}
	public byte[] getUserProfileKey() {
		return this.userProfileKey;
	}

	public void setUserProfileKey(byte[] userProfileKey) {
		this.userProfileKey = userProfileKey;
	}
	public Integer getViews() {
		return this.views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}
	public Integer getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}
	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getIsSafeFile() {
		return this.isSafeFile;
	}

	public void setIsSafeFile(String isSafeFile) {
		this.isSafeFile = isSafeFile;
	}
	public Integer getInappropriateCounter() {
		return this.inappropriateCounter;
	}

	public void setInappropriateCounter(Integer inappropriateCounter) {
		this.inappropriateCounter = inappropriateCounter;
	}

	public byte[] getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(byte[] primaryKey) {
		this.primaryKey = primaryKey;
	}

}


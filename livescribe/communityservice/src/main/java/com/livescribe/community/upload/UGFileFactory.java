/**
 * 
 */
package com.livescribe.community.upload;

import org.springframework.web.multipart.MultipartFile;

import com.livescribe.community.orm.UGFile;

/**
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UGFileFactory {
	
	/**
	 * @param userPK
	 * @param mpFile
	 * @return
	 */
	public static UGFile create(byte[] userPK, MultipartFile mpFile) {
		
		UGFile ugFile = new UGFile();
//		ugFile.setCategoryKey(categoryKey);
//		ugFile.setContentDescription(contentDescription);
//		ugFile.setDisplayName(displayName);
//		ugFile.setFileDate(fileDate);
//		ugFile.setFileName(fileName);
//		ugFile.setFilePath(filePath);
//		ugFile.setFileSize(fileSize);
//		ugFile.setGlobalShare(globalShare);
//		ugFile.setInappropriateCounter(inappropriateCounter);
//		ugFile.setIpAddress(ipAddress);
//		ugFile.setIsSafeFile(isSafeFile);
//		ugFile.setMetaInfo(metaInfo);
//		ugFile.setPrimaryKey(primaryKey);
//		ugFile.setRating(rating);
//		ugFile.setShortId(shortId);
//		ugFile.setTypeKey(typeKey);
//		ugFile.setUserProfileKey(userProfileKey);
//		ugFile.setViews(views);
		
		return ugFile;
	}
}

/**
 * 
 */
package com.livescribe.community.view.vo;

import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.livescribe.base.IOUtils;
import com.livescribe.community.orm.PencastAudio;
import com.livescribe.community.orm.PencastPage;
import com.livescribe.base.utils.WOAppMigrationUtils;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PencastVo {

	private static final Logger logger = Logger.getLogger(PencastVo.class);

	private String	authorEmail;
	private String	authorFirstName;
	private String	authorLastName;
	private String	authorScreenName;
	private byte[]	authorPrimaryKey;
	private String	categoryName;
	//	private Clob	contentDescription;
	private String	derivativePath;
	private String	description;
	private String	displayName;
	private Date	fileDate;
	private String	filePath;
	private Integer fileSize;
	private Integer globalShare;
	private boolean flashFileFound = false;
	private Document flashXmlDom;
	private String	flashXmlPath;
	private Integer	numberOfViews;
	private byte[]	primaryKey;
	//	private String publishDate;
	private Double	rating;
	private String	shortId;
	private String	thumbnailUrl;
	private Double	averageRating;
	private Long	audioDuration;
	
	private List<PencastAudio> audioClips = new ArrayList<PencastAudio>();
	private List<PencastPage> pages = new ArrayList<PencastPage>();
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public PencastVo() {
	}

	public PencastVo(PencastVo source) {
		authorEmail = source.authorEmail;
		authorFirstName = source.authorFirstName;
		authorLastName = source.authorLastName;
		authorPrimaryKey = source.authorPrimaryKey;
		authorScreenName = source.authorScreenName;
		categoryName = source.categoryName;
		description = source.description;
		derivativePath = source.derivativePath;
		description = source.description;
		displayName = source.displayName;
		fileDate = source.fileDate;
		filePath = source.filePath;
		fileSize = source.fileSize;
		globalShare = source.globalShare;
		flashFileFound = source.flashFileFound;
		flashXmlDom = source.flashXmlDom;
		flashXmlPath = source.flashXmlPath;
		numberOfViews = source.numberOfViews;
		primaryKey = source.primaryKey;
		rating = source.rating;
		shortId = source.shortId;
		thumbnailUrl = source.thumbnailUrl;
		averageRating = source.averageRating;
		audioDuration = source.audioDuration;
		audioClips = source.audioClips;
		pages = source.pages;
	}
	
	public void addAudio(PencastAudio audio) {
		audioClips.add(audio);
	}
	
	public void addPage(PencastPage page) {
		pages.add(page);
	}
	
	/**
	 * @return the audioClips
	 */
	public List<PencastAudio> getAudioClips() {
		return audioClips;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public String getAuthorFirstName() {
		return authorFirstName;
	}

	public String getAuthorLastName() {
		return authorLastName;
	}

	/**
	 * @return the authorPrimaryKey
	 */
	public byte[] getAuthorPrimaryKey() {
		return authorPrimaryKey;
	}

	public String getAuthorScreenName() {
		return authorScreenName;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public String getCategoryName() {
		return categoryName;
	}
	public Clob getContentDescription() {
		return null;
	}
	public String getDerivativePath() {
		return derivativePath;
	}

	public String getDescription() {
		return description;
	}

	public String getDisplayName() {
		return displayName;
	}
	public Date getFileDate() {
		return fileDate;
	}
	public String getFilePath() {
		return filePath;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public Document getFlashXmlDom() {
		return flashXmlDom;
	}
	public String getFlashXmlPath() {
		return flashXmlPath;
	}

	/**
	 * @return the globalShare
	 */
	public Integer getGlobalShare() {
		return globalShare;
	}

	public Integer getNumberOfViews() {
		return numberOfViews;
	}
	/**
	 * @return the pages
	 */
	public List<PencastPage> getPages() {
		return pages;
	}

	public byte[] getPrimaryKey() {
		return primaryKey;
	}
	public Double getRating() {
		return rating;
	}
	public String getShortId() {
		return shortId;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public boolean isFlashFileFound() {
		return flashFileFound;
	}

	/**
	 * @param audioClips the audioClips to set
	 */
	public void setAudioClips(List<PencastAudio> audioClips) {
		this.audioClips = audioClips;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	public void setAuthorFirstName(String authorFirstName) {
		this.authorFirstName = authorFirstName;
	}

	public void setAuthorLastName(String authorLastName) {
		this.authorLastName = authorLastName;
	}

	/**
	 * @param authorPrimaryKey the authorPrimaryKey to set
	 */
	public void setAuthorPrimaryKey(byte[] authorPrimaryKey) {
		this.authorPrimaryKey = authorPrimaryKey;
	}

	public void setAuthorScreenName(String authorScreenName) {
		this.authorScreenName = authorScreenName;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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
	public void setDerivativePath(String derivativePath) {
		this.derivativePath = derivativePath;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}
	public void setFlashFileFound(boolean flashFileFound) {
		this.flashFileFound = flashFileFound;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public void setFlashXmlDom(Document flashXmlDom) {
		this.flashXmlDom = flashXmlDom;
	}

	public void setFlashXmlPath(String flashXmlPath) {
		this.flashXmlPath = flashXmlPath;
	}

	/**
	 * @param globalShare the globalShare to set
	 */
	public void setGlobalShare(Integer globalShare) {
		this.globalShare = globalShare;
	}

	public void setNumberOfViews(Integer numberOfViews) {
		this.numberOfViews = numberOfViews;
	}
	/**
	 * @param pages the pages to set
	 */
	public void setPages(List<PencastPage> pages) {
		this.pages = pages;
	}

	public void setPrimaryKey(byte[] primaryKey) {
		this.primaryKey = primaryKey;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public void setShortId(String shortId) {
		this.shortId = shortId;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public static void main(String args[]) {

//		String queryFormat = "SELECT TOP(%d,%d) primaryKey, contentDescription from UGFile where contentDescription is not null and fileSize is not null and fileSize <> 0 order by shortId asc";
		String queryFormat = "SELECT TOP(%d,%d) primaryKey, commentDescription from UGComment where commentDescription is not null order by ugfileKey asc";

		String rowFormat = "Printing row-%d [%s] [%s]";
		
//		System.out.println(String.format(queryFormat, 1, 10));
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("com.frontbase.jdbc.FBJDriver");
			conn = DriverManager.getConnection("jdbc:frontbase://localhost/local.consumer/user=_system/isolation=read_committed/locking=deferred/readonly=false/");
			
			int start = 1;
			int fetchSize = 1000;
			
			int count = 0;
			do {
				stmt = conn.createStatement();
				
				String query = String.format(queryFormat, start, fetchSize);
				System.out.println("Executing " + query);
				ResultSet resultSet = stmt.executeQuery(query);

				count = 0;
				while ( resultSet.next() ) {
					count++;
					String value;
					try {
						Clob clob = resultSet.getClob("commentDescription");
						if ( clob != null && clob.length() > 0 ) {
							value = IOUtils.getStringFromClob(clob);
							System.out.println(String.format(rowFormat, (start+count-1), value, WOAppMigrationUtils.convertPrimaryKeyToString(resultSet.getBytes("primaryKey"))));
						} else {
							//System.out.println("Row " + (start+count-1) + " clob != null is "  + (clob != null) + " and clob.length() = " + (clob==null ? 0 : clob.length() ));
						}
					} catch (IOException e) {
						System.out.println("Exception while reading row # " + (start+count-1) + " primaryKey " + WOAppMigrationUtils.convertPrimaryKeyToString(resultSet.getBytes("primaryKey")));
						e.printStackTrace();
					}
				}
				start += fetchSize;
			} while (count > 0);
			//} while ( start < 10);
			
			//			stmt.clearParameters()

			//	    	
			//	    	String query = "SELECT TOP("
			//	    	ResultSet rset = stmt.executeQuery("select displayName from UGCategory where primaryKey = x'00000A0101C90000F1D502000000011760AD35E3203CE0DE'");
			//	    	
			//	    	
			//	    	for (int i = 1; i <= rset..getColumnCount(); i++) {
			//	    		int colType = rsmd.getColumnType(i);
			//	    		String colTypeName = rsmd.getColumnTypeName(i);
			//	    		String dispName = rsmd.getColumnName(i);
			//	    		System.out.println(dispName + " " + colTypeName + " " + colType);
			//	    	}
			////	    	ResultSet rset = stmt.executeQuery("select * from UGCategory where displayName = 'Random'");
			////    	"00000A0101C90000F1D502000000011760AD35E3203CE0DE"
			////    	"Random"
			//	    	stmt.close();
			//	    	conn.close();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
		}
	}

	/**
	 * @return the audioDuration
	 */
	public Long getAudioDuration() {
		
		if (audioDuration == null) {
			return new Long(0);
		}
		return audioDuration;
	}

	/**
	 * @param audioDuration the audioDuration to set
	 */
	public void setAudioDuration(Long audioDuration) {
		this.audioDuration = audioDuration;
	}
}

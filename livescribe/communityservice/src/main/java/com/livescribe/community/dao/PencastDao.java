/**
 * 
 */
package com.livescribe.community.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import com.livescribe.base.utils.WOAppMigrationUtils;
import com.livescribe.community.view.vo.PencastVo;

/**
 * <p>Handles all data access for pencast information.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PencastDao extends BaseDao {
	
	private static String PLACEHOLDER_CATEGORY_NAME		= "categoryName";
	private static String PLACEHOLDER_INAPPROP_COUNT	= "inappropCount";
	private static String PLACEHOLDER_NUM				= "num";
	private static String PLACEHOLDER_SHARE				= "share";
	private static String PLACEHOLDER_SHORT_ID			= "shortId";
	private static String PLACEHOLDER_USER_PK			= "userPK";
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public PencastDao() {
		super();
	}
	
	/**
	 * <p>Returns the pencast identified by the given short ID.</p>
	 * 
	 * @param id The unique <code>shortId</code> of the pencast.
	 * 
	 * @return the pencast identified by the given short ID.
	 */
	public PencastVo findByPencastShortId(String id) {
		
		Query query = sessionFactory.getCurrentSession().getNamedQuery("findPencastByShortId");
		query.setString(PLACEHOLDER_SHORT_ID, id);
		
		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));
		
		List<PencastVo> list = query.list();
		if ((list == null) || (list.isEmpty())) {
			logger.error("No pencast was found in the database with ID = '" + id + "'.");
			return null;
		}
		else if (list.size() > 1) {
			logger.error("More than one pencast was found in the database with ID = '" + id + "'!");
			return null;
		}
			
		return list.get(0);
	}

	/**
	 * <p>Returns the pencast identified by the given primary key.</p>
	 * 
	 * @param primaryKey The primary key to search on.
	 * 
	 * @return the pencast identified by the given primary key.
	 */
	public PencastVo findByPrimaryKey(byte[] primaryKey) {
		
		Query query = sessionFactory.getCurrentSession().getNamedQuery("findPencastByShortId");
		query.setBinary(PLACEHOLDER_USER_PK, primaryKey);
		
		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));
		
		List<PencastVo> list = query.list();
		if ((list == null) || (list.isEmpty())) {
			logger.error("No pencast was found in the database with ID = '" + WOAppMigrationUtils.convertPrimaryKeyToString(primaryKey) + "'.");
			return null;
		}
		else if (list.size() > 1) {
			logger.error("More than one pencast was found in the database with ID = '" + WOAppMigrationUtils.convertPrimaryKeyToString(primaryKey) + "'!");
			return null;
		}
			
		return list.get(0);
	}
	
	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public List getRecentUserPencasts(byte[] userPk, Date fromDate) {
		
		String pkStr = WOAppMigrationUtils.convertPrimaryKeyToString(userPk);
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("SELECT \"f\".\"primaryKey\" as \"primaryKey\",");
		builder.append("\"f\".\"shortId\" as \"shortId\",");
		builder.append("\"c\".\"displayName\" as \"categoryName\",");
		builder.append("\"f\".\"contentDescription\" as \"contentDescription\",");
		builder.append("\"f\".\"displayName\" as \"displayName\",");
		builder.append("\"f\".\"fileDate\" as \"fileDate\",");
		builder.append("\"f\".\"filePath\" as \"filePath\",");
		builder.append("\"f\".\"fileSize\" as \"fileSize\",");
		builder.append("\"f\".\"globalShare\" as \"globalShare\",");
		builder.append("\"f\".\"views\" as \"numberOfViews\",");
		builder.append("\"f\".\"rating\" as \"rating\",");
		builder.append("\"p\".\"firstName\" as \"authorFirstName\",");
		builder.append("\"p\".\"lastName\" as \"authorLastName\",");
		builder.append("\"p\".\"email\" as \"authorEmail\",");
		builder.append("\"p\".\"primaryKey\" as \"authorPrimaryKey\", ");
		builder.append("\"p\".\"screenName\" as \"authorScreenName\" ");
		
		builder.append(" FROM \"UGCategory\" \"c\",");
		builder.append(" \"UGFile\" \"f\",");
		builder.append(" \"UserProfile\" \"p\"");
 		
		builder.append(" WHERE \"f\".\"categoryKey\" = \"c\".\"primaryKey\"");
		builder.append(" AND \"f\".\"userProfileKey\" = \"p\".\"primaryKey\"");
		builder.append(" AND \"f\".\"filePath\" is not null ");
		builder.append(" AND \"f\".\"fileSize\" > 0 ");
		builder.append(" AND \"f\".\"fileDate\" > :fromDate ");
		builder.append(" AND \"p\".\"primaryKey\" = x'" + pkStr + "'");

		builder.append(" ORDER BY \"f\".\"fileDate\" ASC");
		
		Query query = sessionFactory.getCurrentSession().createQuery(builder.toString());
		query.setParameter("userpk", userPk);
		query.setParameter("fromDate", fromDate);
		
		String upkStr = WOAppMigrationUtils.convertPrimaryKeyToString(userPk);
		String dStr = WOAppMigrationUtils.convertDateToGMTDateString(fromDate);
		
		logger.debug(query.getQueryString());
		logger.debug("  User PK: " + upkStr);
		logger.debug("From Date: " + dStr);
		
		List list = query.list();

		
		if (list != null) {
			if (!list.isEmpty()) {
				Long longCount = (Long)list.get(0);
				int count = longCount.intValue();
				if (count > 0) {
					logger.debug("Count > 0: " + count);
				}
				else {
					logger.debug("Count == 0");
				}
			}
			else {
				logger.debug("List was empty.  No count found.");
			}
		}
		else {
			logger.debug("List was 'null'.  No count found.");
		}
		return list;
	}
	
	/**
	 * <p>Returns a list of ALL pencasts in the database that are shareable and have
	 * an inappropriate count less than 3.</p>
	 * 
	 * @return a list of all pencasts.
	 */
	public List<PencastVo> getAllPencasts() {
		
		String method = "getAllPencasts():  ";
			
		Query query = sessionFactory.getCurrentSession().getNamedQuery("getAllPencasts");
		query.setInteger(PLACEHOLDER_SHARE, 1);
		query.setInteger(PLACEHOLDER_INAPPROP_COUNT, 3);

		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));
		
		List<PencastVo> list = query.list();
		
		logger.debug(method + "Returning " + list.size() + " pencasts.");
		
		return list;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param start
	 * @param fetchSize
	 * 
	 * @return
	 */
	public List<PencastVo> getMostViewedPencasts(int start, int fetchSize) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("SELECT TOP (" + start + ", " + fetchSize + ") \"f\".\"primaryKey\" as \"primaryKey\",");
		builder.append("\"f\".\"shortId\" as \"shortId\", ");
		builder.append("\"c\".\"displayName\" as \"categoryName\", ");
		builder.append("\"f\".\"contentDescription\" as \"contentDescription\", ");
		builder.append("\"f\".\"displayName\" as \"displayName\", ");
		builder.append("\"f\".\"fileDate\" as \"fileDate\", ");
		builder.append("\"f\".\"filePath\" as \"filePath\", ");
		builder.append("\"f\".\"fileSize\" as \"fileSize\", ");
		builder.append("\"f\".\"globalShare\" as \"globalShare\", ");
		builder.append("\"f\".\"views\" as \"numberOfViews\", ");
		builder.append("\"f\".\"rating\" as \"rating\", ");
		builder.append("\"p\".\"firstName\" as \"authorFirstName\", ");
		builder.append("\"p\".\"lastName\" as \"authorLastName\", ");
		builder.append("\"p\".\"email\" as \"authorEmail\", ");
		builder.append("\"p\".\"primaryKey\" as \"authorPrimaryKey\", ");
		builder.append("\"p\".\"screenName\" as \"authorScreenName\" ");
		
		builder.append(" FROM \"UGFile\" \"f\",");
		builder.append(" \"UGCategory\" \"c\",");
		builder.append(" \"UserProfile\" \"p\"");
		
		builder.append(" WHERE \"f\".\"categoryKey\" = \"c\".\"primaryKey\" ");
		builder.append(" AND \"f\".\"userProfileKey\" = \"p\".\"primaryKey\"");
		builder.append(" AND \"f\".\"filePath\" is not null");
		builder.append(" AND \"f\".\"fileSize\" > 0");
		builder.append(" AND \"f\".\"globalShare\" = 1");
		builder.append(" AND \"f\".\"inappropriateCounter\" < 3");
			
		builder.append(" ORDER BY \"f\".\"views\" DESC");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(builder.toString());
		
		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));
		
		List<PencastVo> list = query.list();
		
		return list;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param categoryName
	 * @param start
	 * @param fetchSize
	 * 
	 * @return
	 */
	public List<PencastVo> getPencastsByCategory(String categoryName, int start, int fetchSize) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("SELECT	TOP (" + start + ", " + fetchSize + ") \"f\".\"primaryKey\" as \"primaryKey\", ");
		builder.append("\"f\".\"shortId\" as \"shortId\", ");
		builder.append("\"c\".\"displayName\" as \"categoryName\", ");
		builder.append("\"f\".\"contentDescription\" as \"contentDescription\", ");
		builder.append("\"f\".\"displayName\" as \"displayName\", ");
		builder.append("\"f\".\"fileDate\" as \"fileDate\", ");
		builder.append("\"f\".\"filePath\" as \"filePath\", ");
		builder.append("\"f\".\"fileSize\" as \"fileSize\", ");
		builder.append("\"f\".\"globalShare\" as \"globalShare\", ");
		builder.append("\"f\".\"views\" as \"numberOfViews\", ");
		builder.append("\"f\".\"rating\" as \"rating\", ");
		builder.append("\"p\".\"firstName\" as \"authorFirstName\", ");
		builder.append("\"p\".\"lastName\" as \"authorLastName\", ");
		builder.append("\"p\".\"email\" as \"authorEmail\", ");
		builder.append("\"p\".\"primaryKey\" as \"authorPrimaryKey\", ");
		builder.append("\"p\".\"screenName\" as \"authorScreenName\" ");
		
		builder.append("FROM \"UGFile\" \"f\", ");
		builder.append("\"UGCategory\" \"c\", ");
		builder.append("\"UserProfile\" \"p\" ");
		
		builder.append(" WHERE \"f\".\"categoryKey\" = \"c\".\"primaryKey\" ");
		builder.append(" AND \"f\".\"userProfileKey\" = \"p\".\"primaryKey\" ");
		builder.append(" AND \"f\".\"filePath\" is not null");
		builder.append(" AND \"f\".\"fileSize\" > 0");
		builder.append(" AND \"f\".\"globalShare\" = 1 ");
		builder.append(" AND \"f\".\"inappropriateCounter\" < 3 ");
		builder.append(" AND \"c\".\"displayName\" = '" + categoryName + "' ");
			
		builder.append(" ORDER BY \"f\".\"fileDate\", \"f\".\"views\" DESC ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(builder.toString());

		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));
		
		List<PencastVo> list = query.list();
		
		return list;
	}
	
	/**
	 * <p>Returns a list of pencasts tagged with the given category name.</p>
	 * 
	 * @param categoryName The name of the category to search on.
	 * 
	 * @return a list of pencasts tagged with the given category name.
	 */
	public List<PencastVo> getPencastsByCategoryName(String categoryName) {
		
		Query query = sessionFactory.getCurrentSession().getNamedQuery("getPencastsByCategoryName");
		query.setString(PLACEHOLDER_CATEGORY_NAME, categoryName);
		query.setInteger(PLACEHOLDER_SHARE, 1);
		query.setInteger(PLACEHOLDER_INAPPROP_COUNT, 3);

		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));
		
		List<PencastVo> list = query.list();
		
		return list;
	}
	
	/**
	 * <p>Returns a list of pencasts tagged with the given category name.</p>
	 * 
	 * @param categoryName The name of the category to search on.
	 * 
	 * @return a list of pencasts tagged with the given category name.
	 */
	public List<PencastVo> getPencastsByCategoryName(String categoryName, int start, int fetchSize) {
		
		logger.debug("categoryName = " + categoryName + ", start = " + start + ", fetchSize = " + fetchSize);
		
		Query query = sessionFactory.getCurrentSession().getNamedQuery("getPencastsByCategoryName");
//		query.setInteger("start", start);
//		query.setInteger("num", num);
		query.setString(PLACEHOLDER_CATEGORY_NAME, categoryName);
		query.setInteger(PLACEHOLDER_SHARE, 1);
		query.setInteger(PLACEHOLDER_INAPPROP_COUNT, 3);

		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));
		
		List<PencastVo> list = query.list();
		
		return list;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param start
	 * @param fetchSize
	 * 
	 * @return
	 */
	public List<PencastVo> getRecentPencasts(int start, int fetchSize) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("SELECT TOP (" + start + ", " + fetchSize + ") ");
		builder.append("\"f1\".\"primaryKey\" as \"primaryKey\", ");
		builder.append("\"f1\".\"shortId\" as \"shortId\", ");
		builder.append("\"c\".\"displayName\" as \"categoryName\", ");
		builder.append("\"f1\".\"contentDescription\" as \"contentDescription\", ");
		builder.append("\"f1\".\"fileDate\" as \"fileDate\", ");
		builder.append("\"f1\".\"displayName\" as \"displayName\", ");
		builder.append("\"f1\".\"filePath\" as \"filePath\", ");
		builder.append("\"f1\".\"fileSize\" as \"fileSize\", ");
		builder.append("\"f1\".\"views\" as \"numberOfViews\", ");
		builder.append("\"f1\".\"rating\" as \"rating\", ");
		builder.append("\"p\".\"firstName\" as \"authorFirstName\", ");
		builder.append("\"p\".\"lastName\" as \"authorLastName\", ");
		builder.append("\"p\".\"email\" as \"authorEmail\", ");
		builder.append("\"p\".\"primaryKey\" as \"authorPrimaryKey\", ");
		builder.append("\"p\".\"screenName\" as \"authorScreenName\" ");
		
		builder.append("FROM \"UGFile\" \"f1\", ");
		builder.append("\"UserProfile\" \"p\", ");
		builder.append("\"UGCategory\" \"c\" ");
		
		builder.append("WHERE \"f1\".\"globalShare\" = 1 ");
 		builder.append("AND \"f1\".\"filePath\" is not null ");
		builder.append("AND \"f1\".\"fileSize\" > 0 ");
		builder.append("AND \"f1\".\"fileDate\" = (");
		
		builder.append("SELECT MAX (\"f2\".\"fileDate\") ");
			
		builder.append("FROM \"UGFile\" \"f2\" ");
			  
		builder.append("WHERE \"f1\".\"userProfileKey\" = \"f2\".\"userProfileKey\" ");
		builder.append("AND \"f2\".\"fileDate\" > TIMESTAMP'2009-01-01 00:00:00' ");
		builder.append("AND \"f2\".\"globalShare\" = 1 ");
		builder.append("AND \"f2\".\"views\" > 1 ");
		builder.append("AND \"f2\".\"inappropriateCounter\" < 1) ");
		builder.append("AND \"f1\".\"userProfileKey\" = \"p\".\"primaryKey\" ");
		builder.append("AND \"f1\".\"categoryKey\" = \"c\".\"primaryKey\" ");
		
		builder.append("ORDER BY \"f1\".\"fileDate\" DESC");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(builder.toString());
		
		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));

		List<PencastVo> list = query.list();
		
		return list;
	}
	
	/**
	 * <p>Returns a list of <code>num</code> pencasts ordered by average rating.</p>
	 * 
	 * @param num The number of pencasts to return.
	 * 
	 * @return a list of <code>num</code> pencasts ordered by average rating.
	 */
	public List<PencastVo> getTopPencasts(int fetchSize) {
		
		Query query = sessionFactory.getCurrentSession().getNamedQuery("getTopPencasts");
//		query.setInteger(PLACEHOLDER_NUM, fetchSize);		//	<-- Doesn't work with placeholder in SELECT clause.
		query.setInteger(PLACEHOLDER_SHARE, 1);
		query.setInteger(PLACEHOLDER_INAPPROP_COUNT, 3);

		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));
		
		List<PencastVo> list = query.list();
		
		return list;
	}
	
	/**
	 * <p>Returns a list of <code>num</code> pencasts ordered by average rating.</p>
	 * 
	 * @param start The index in the sorted list to start the result set from.
	 * @param fetchSize The number of pencasts to return.
	 * 
	 * @return a list of <code>num</code> pencasts ordered by average rating.
	 */
	public List<PencastVo> getTopPencasts(int start, int fetchSize) {

		StringBuilder builder = new StringBuilder();
		
		builder.append("SELECT TOP (" + start + ", " + fetchSize + ") \"cmnt\".\"ugFileKey\" as \"primaryKey\", ");
		builder.append("MAX (\"f\".\"shortId\") as \"shortId\", ");
		builder.append("\"c\".\"displayName\" as \"categoryName\", ");
		builder.append("MAX (\"f\".\"contentDescription\") as \"contentDescription\", ");
		builder.append("MAX (\"f\".\"displayName\") as \"displayName\", ");
		builder.append("MAX (\"f\".\"fileDate\") as \"fileDate\", ");
		builder.append("MAX (\"f\".\"filePath\") as \"filePath\", ");
		builder.append("MAX (\"f\".\"fileSize\") as \"fileSize\", ");
		builder.append("MAX (\"f\".\"globalShare\") as \"globalShare\", ");
		builder.append("MAX (\"f\".\"views\") as \"numberOfViews\", ");
		builder.append("MAX (\"f\".\"rating\") as \"rating\", ");
		builder.append("\"p\".\"firstName\" as \"authorFirstName\", ");
		builder.append("\"p\".\"lastName\" as \"authorLastName\", ");
		builder.append("\"p\".\"email\" as \"authorEmail\", ");
		builder.append("\"p\".\"primaryKey\" as \"authorPrimaryKey\", ");
		builder.append("\"p\".\"screenName\" as \"authorScreenName\" ");
//		builder.append("(sum(\"cmnt\".\"rating\")/count(*)) AS \"averageRating\" ");
	
		builder.append("FROM \"UGFile\" \"f\", ");
		builder.append("\"UGCategory\" \"c\", ");
		builder.append("\"UGComment\" \"cmnt\", ");
		builder.append("\"UserProfile\" \"p\" ");
	
		builder.append("WHERE \"f\".\"categoryKey\" = \"c\".\"primaryKey\" ");
		builder.append("AND \"cmnt\".\"ugFileKey\" = \"f\".\"primaryKey\" ");
		builder.append("AND \"f\".\"userProfileKey\" = \"p\".\"primaryKey\" ");
		builder.append("AND \"f\".\"filePath\" is not null ");
		builder.append("AND \"f\".\"fileSize\" > 0 ");
		builder.append("AND \"f\".\"globalShare\" = 1 ");
		builder.append("AND \"f\".\"inappropriateCounter\" < 3 ");
		
		builder.append("GROUP BY \"primaryKey\", ");
//		builder.append("\"shortId\",  ");
		builder.append("\"categoryName\",  ");
//		builder.append("\"contentDescription\",  ");
//		builder.append("\"displayName\",  ");
//		builder.append("\"fileDate\", ");
//		builder.append("\"filePath\", ");
//		builder.append("\"fileSize\", ");
//		builder.append("\"globalShare\", ");
//		builder.append("\"numberOfViews\", ");
//		builder.append("\"rating\", ");
		builder.append("\"authorFirstName\", ");
		builder.append("\"authorLastName\", ");
		builder.append("\"authorEmail\", ");
		builder.append("\"authorPrimaryKey\", ");
		builder.append("\"authorScreenName\" ");

		builder.append("HAVING COUNT(*) >  2 ");

		builder.append("ORDER BY \"rating\" DESC ");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(builder.toString());
		
		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));
		
		List<PencastVo> list = query.list();
		
		return list;
	}

	public List<PencastVo> getTopPencasts2(int start, int fetchSize) {
		
		StringBuilder bldr1 = new StringBuilder();
		
		bldr1.append("SELECT	TOP(" + start + ", " + fetchSize + ") ugFileKey as primaryKey, (SUM(rating)/COUNT(*)) AS averageRating ");
		bldr1.append("FROM	UGComment ");
		bldr1.append("WHERE	ugFileKey IN (");
		bldr1.append("SELECT primaryKey FROM UGFile WHERE globalShare = 1 ");
		bldr1.append("AND inappropriateCounter < 1) ");
		bldr1.append("GROUP BY primaryKey ");
		bldr1.append("HAVING	COUNT(*) >  3 ");
		bldr1.append("ORDER BY averageRating DESC");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(bldr1.toString());
		
		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));
		
		List<PencastVo> list = query.list();
		
		return list;
	}
	
	/**
	 * <p>Returns a list of pencasts a user owns, or <code>null</code> if none are found.</p>
	 * 
	 * @param primaryKey the unique <code>primaryKey</code> of the user.
	 * 
	 * @return a list of pencasts.  Returns <code>null</code> if no
	 * pencasts were found.
	 */
	public List<PencastVo> getUserPencasts(byte[] primaryKey) {
		
		Query query = sessionFactory.getCurrentSession().getNamedQuery("getUserPencasts");
		query.setBinary(PLACEHOLDER_USER_PK, primaryKey);
		
		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));

		List<PencastVo> list = query.list();
		
		return list;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param primaryKey
	 * @param start
	 * @param num
	 * 
	 * @return
	 */
	public List<PencastVo> getUserPencasts(byte[] primaryKey, int start, int fetchSize) {
		
		String pkStr = WOAppMigrationUtils.convertPrimaryKeyToString(primaryKey);
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("SELECT TOP (" + start + ", " + fetchSize + ") \"f\".\"primaryKey\" as \"primaryKey\",");
		builder.append("\"f\".\"shortId\" as \"shortId\",");
		builder.append("\"c\".\"displayName\" as \"categoryName\",");
		builder.append("\"f\".\"contentDescription\" as \"contentDescription\",");
		builder.append("\"f\".\"displayName\" as \"displayName\",");
		builder.append("\"f\".\"fileDate\" as \"fileDate\",");
		builder.append("\"f\".\"filePath\" as \"filePath\",");
		builder.append("\"f\".\"fileSize\" as \"fileSize\",");
		builder.append("\"f\".\"globalShare\" as \"globalShare\",");
		builder.append("\"f\".\"views\" as \"numberOfViews\",");
		builder.append("\"f\".\"rating\" as \"rating\",");
		builder.append("\"p\".\"firstName\" as \"authorFirstName\",");
		builder.append("\"p\".\"lastName\" as \"authorLastName\",");
		builder.append("\"p\".\"email\" as \"authorEmail\",");
		builder.append("\"p\".\"primaryKey\" as \"authorPrimaryKey\", ");
		builder.append("\"p\".\"screenName\" as \"authorScreenName\" ");
		
		builder.append(" FROM \"UGCategory\" \"c\",");
		builder.append(" \"UGFile\" \"f\",");
		builder.append(" \"UserProfile\" \"p\"");
 		
		builder.append(" WHERE \"f\".\"categoryKey\" = \"c\".\"primaryKey\"");
		builder.append(" AND \"f\".\"userProfileKey\" = \"p\".\"primaryKey\"");
		builder.append(" AND \"f\".\"filePath\" is not null ");
		builder.append(" AND \"f\".\"fileSize\" > 0 ");
		builder.append(" AND \"p\".\"primaryKey\" = x'" + pkStr + "'");

		builder.append(" ORDER BY \"f\".\"fileDate\" ASC");
		
		logger.debug(builder.toString());
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(builder.toString());
		
		//	Transform the results of the query into instances of the PencastVo class.
		query.setResultTransformer(Transformers.aliasToBean(PencastVo.class));

		List<PencastVo> list = query.list();
		
		return list;
	}
	
	public boolean recentUserPencastsExist(byte[] userPk, Date fromDate) {
		
		Query query = sessionFactory.getCurrentSession().createQuery("select count(*) from UGFile where userProfileKey = :userpk and fileDate > :fromDate");
		query.setParameter("userpk", userPk);
		query.setParameter("fromDate", fromDate);
		
		String upkStr = WOAppMigrationUtils.convertPrimaryKeyToString(userPk);
		String dStr = WOAppMigrationUtils.convertDateToGMTDateString(fromDate);
		
		logger.debug(query.getQueryString());
		logger.debug("  User PK: " + upkStr);
		logger.debug("From Date: " + dStr);
		
		List list = query.list();
		
		if (list != null) {
			if (!list.isEmpty()) {
				Long longCount = (Long)list.get(0);
				int count = longCount.intValue();
				if (count > 0) {
					logger.debug("Count > 0: " + count);
					return true;
				}
				else {
					logger.debug("Count == 0");
					return false;
				}
			}
			else {
				logger.debug("List was empty.  No count found.");
				return false;
			}
		}
		else {
			logger.debug("List was 'null'.  No count found.");
			return false;
		}
	}
}

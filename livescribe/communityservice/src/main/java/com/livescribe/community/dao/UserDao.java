/**
 * 
 */
package com.livescribe.community.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import com.livescribe.community.orm.ActiveUser;
import com.livescribe.community.orm.UserProfile;
import com.livescribe.base.utils.WOAppMigrationUtils;

/**
 * <p>Handles all data access for user information.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class UserDao extends BaseDao {

	/**
	 * <p>Second <code>SessionFactory</code> to obtain the <code>ActiveUser</code>.
	 */
	private SessionFactory loginSessionFactory;

	public void setLoginSessionFactory(SessionFactory loginSessionFactory) {
		this.loginSessionFactory = loginSessionFactory;
	}
	
	private static String PLACEHOLDER_KEY	= "key";
	private static String PLACEHOLDER_TOKEN	= "token";
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public UserDao() {
		super();
	}
	
	/**
	 * <p>Looks up an &apos;active&apos; user with the given token.</p>
	 * 
	 * This method requires a separate (second) <code>SessionFactory</code>
	 * be configured to access active user records from a separate database/schema.
	 * 
	 * @param token The unique token to use in looking up the active user.
	 * 
	 * @return the active user identified by the given token; <code>null</code> if 
	 * no user is found.
	 */
	public ActiveUser findActiveUserByToken(String token) {
		
		Query query = loginSessionFactory.getCurrentSession().getNamedQuery("findActiveUser");
		query.setString(PLACEHOLDER_TOKEN, token);
		
		//	Transform the results of the query into instances of the ActiveUser class.
		query.setResultTransformer(Transformers.aliasToBean(ActiveUser.class));
		
		List<ActiveUser> list = query.list();
		
		if ((list == null) || (list.isEmpty())) {
			return null;
		}
		else if (list.size() > 1) {
			logger.warn("More than one ActiveUser found with token = '" + token + "'.");
			return null;
		}
		else {
			return list.get(0);
		}
	}
	
	/**
	 * <p>Returns the user profile for user identified by the given <code>ActiveUser</code>.</p>
	 * 
	 * @param aUser The active user to use in looking up the profile information.
	 * 
	 * @return the user profile for the given active user.
	 */
	public UserProfile findUserProfileByActiveUser(ActiveUser aUser) {
		
		if (aUser != null) {
			byte[] pKey = aUser.getUserId();
			
			Query query = sessionFactory.getCurrentSession().getNamedQuery("findUserProfile");
			query.setBinary(PLACEHOLDER_KEY, pKey);
			
			//	Transform the results of the query into instances of the ActiveUser class.
			query.setResultTransformer(Transformers.aliasToBean(UserProfile.class));
			
			List<UserProfile> list = query.list();
			
			if ((list == null) || (list.isEmpty())) {
				return null;
			}
			else if (list.size() > 1) {
				String keyStr = WOAppMigrationUtils.convertPrimaryKeyToString(pKey);
				logger.warn("More than one UserProfile found with primary key = '" + keyStr + "'.");
				return null;
			}
			else {
				return list.get(0);
			}
		}
		return null;
	}
}

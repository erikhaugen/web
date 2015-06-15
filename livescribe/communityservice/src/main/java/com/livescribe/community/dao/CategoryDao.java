/**
 * 
 */
package com.livescribe.community.dao;

import java.util.List;

import org.hibernate.Query;

import com.livescribe.community.orm.UGCategory;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CategoryDao extends BaseDao {
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public CategoryDao() {
		super();
	}
	
	/**
	 * <p>Returns a <code>List</code> of all categories of pencasts from the database.</p>
	 * 
	 * @return a <code>List</code> of all categories of pencasts from the database.
	 */
	public List<UGCategory> getCategoryList() {
		
		Query query = sessionFactory.getCurrentSession().createQuery("from UGCategory");
		
		List<UGCategory> list = query.list();
		
		return list;
	}
}

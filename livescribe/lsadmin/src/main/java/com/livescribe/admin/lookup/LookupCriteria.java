package com.livescribe.admin.lookup;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

public class LookupCriteria {
	private QueryField queryField;
	private Comparator comparator;
	private String value;

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Default constructor
	 */
	public LookupCriteria() {
		this.queryField = null;
		this.comparator = null;
		this.value = null;
	}
	
	/**
	 * Constructor
	 * 
	 * @param queryField
	 * @param comparator
	 * @param value
	 */
	public LookupCriteria(QueryField queryField, Comparator comparator, String value) {
		this.queryField = queryField;
		this.comparator = comparator;
		this.value = value;
	}
	
	/**
	 * Build Hibernate Query object
	 * 
	 * @param sessionFactory
	 * @return
	 */
	public Query getSQLQuery(SessionFactory sessionFactory) {
		String conditionalSQL = getConditionalSQLStatement();
		
		Query query = sessionFactory.getCurrentSession().createQuery(conditionalSQL);
		
		if (DataType.LONG == queryField.getDataType()) {
			query.setLong(queryField.getField(), Long.parseLong(value));
			
		} else if (DataType.STRING == queryField.getDataType()) {
			query.setString(queryField.getField(), value);
		}
		
		return query;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getConditionalSQLStatement() {
		StringBuffer sb = new StringBuffer();
		sb.append("FROM ");
		sb.append(queryField.getEntityName());
		sb.append(" WHERE ");
		sb.append(queryField.getField());
		sb.append(comparator.getSqlOperator());
		sb.append(":" + queryField.getField());
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSQLWhereCondition() {
		StringBuffer sb = new StringBuffer();
		sb.append(queryField.getEntityAlias());
		sb.append(".");
		sb.append(queryField.getField());
		sb.append(comparator.getSqlOperator());
		sb.append(":" + queryField.getField());
		
		return sb.toString();
	}
	
	public QueryField getQueryField() {
		return queryField;
	}

	public void setQueryField(QueryField queryField) {
		this.queryField = queryField;
	}

	public Comparator getComparator() {
		return comparator;
	}

	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LookupCriteria[");
		sb.append(queryField.toString());
		sb.append(" comparator=").append(comparator.getName());
		sb.append(" value=").append(value);
		sb.append("]");
		
		return sb.toString();
	}
}

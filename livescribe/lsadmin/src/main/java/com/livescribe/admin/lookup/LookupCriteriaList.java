package com.livescribe.admin.lookup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

import com.livescribe.afp.AFPException;
import com.livescribe.afp.PenID;

public class LookupCriteriaList {
    final int LIMIT_RESULT = 50;
	private List<LookupCriteria> criteriaList;
	
	private Set<String> entities;
	
	private static final String AND_JOIN = " AND ";
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Default constructor
	 */
	public LookupCriteriaList() {
		criteriaList = new ArrayList<LookupCriteria>();
		entities = new HashSet<String>();
	}
	
	/**
	 * 
	 * @param criteria
	 */
	public void addCriteria(LookupCriteria criteria) {
		if (criteria == null) {
			return;
		}
		
		criteriaList.add(criteria);
		entities.add(criteria.getQueryField().getEntityName() + " " + criteria.getQueryField().getEntityAlias());
	}
	
	/**
	 * 
	 * @return
	 */
	public List<LookupCriteria> getCriteriaList() {
		return this.criteriaList;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getConditionalJoinSQL() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT u FROM User u, "); // add User to FROM clause since we always search on User entity
		
		// Get all entities to add to FROM clause
		for (String entity : entities) {			
			// Skip User entity since we already added
			if (entity.equals("User u")) {
				continue;
			}
			sb.append(entity);
			sb.append(", ");
		}
		
		sb = new StringBuffer(sb.substring(0, sb.length() - 2)); // trim the last ", "
		sb.append(" WHERE ");
		
		// Preparing WHERE clause
		for (LookupCriteria criteria : criteriaList) {
			sb.append(criteria.getSQLWhereCondition());
			sb.append(AND_JOIN);
		}
		
		// Add join condition to WHERE clause if we are looking up from more than 1 Entities
		sb.append(getEntitiesJoinConditionalSQL());
		
		return sb.substring(0, sb.length() - AND_JOIN.length()); // trim the last AND_JOIN
	}

	private String getEntitiesJoinConditionalSQL() {
		StringBuffer sb = new StringBuffer();
		
		for (LookupCriteria criteria : criteriaList) {
			String entityName = criteria.getQueryField().getEntityName();
			String entityAlias = criteria.getQueryField().getEntityAlias();
			
			// Skip User entity
			if (entityName.equals(QueryField.PRIMARY_EMAIL.getEntityName())) {
				continue;
			}
			sb.append(entityAlias);
			sb.append(".user = u");
			sb.append(AND_JOIN);
		}
		
		return sb.toString();
	}
	
	/**
	 * <p>Creates the Hibernate <code>Query</code> to be used for the search.</p>
	 * 
	 * @param sessionFactory The Hibernate <code>SessionFactory</code> to use
	 * to create the <code>Query</code> object.
	 * 
	 * @return a Hibernate <code>Query</code> object.
	 */
	public Query getSQLQuery(SessionFactory sessionFactory) {
		
		String method = "getSQLQuery()";
		
		String queryString = getConditionalJoinSQL();
		
		logger.debug(queryString);
		
		Query query = sessionFactory.getCurrentSession().createQuery(queryString);
		query.setMaxResults(LIMIT_RESULT);
		for (LookupCriteria criteria : criteriaList) {
			QueryField queryField = criteria.getQueryField();
			Comparator comparator = criteria.getComparator();
			
			String value;
			// If the queryField is PEN_ID, we'll detect if its value is a displayId or serialNumber
			if (QueryField.PEN_ID.equals(queryField)) {
				value = convertPenIDtoSerialNumber(criteria.getValue());
				logger.debug("Convert deviceSerialNumber to " + value);
			} else {
				value = criteria.getValue();
			}
			
			
			// If the comparator is "contains", append % to the value
			if (Comparator.CONTAINS.equals(comparator) && DataType.STRING == queryField.getDataType()) {
				if (!value.contains("%")) {
					value = "%" + value + "%";
				}
			}
			
			if (DataType.LONG == queryField.getDataType()) {
				query.setLong(queryField.getField(), Long.parseLong(value));
				
			} else if (DataType.STRING == queryField.getDataType()) {
				query.setString(queryField.getField(), value);
			}
		}
		
		logger.debug(method + " - toString():  " + query.toString());
		String qString = query.getQueryString();
		logger.debug(method + " - getQueryString():  " + qString);
		
		return query;
	}
	
	private String convertPenIDtoSerialNumber(String id) {
		
		logger.debug("convertPenIDtoSerialNumber called with id " + id);
		
		try {
			// if id is in a format of dispayID
			if (id.contains("-")) {
				PenID penID = new PenID(id);
				return String.valueOf(penID.getId());
				
			} else {
				// If id is serialnumber_hex
				if (id.indexOf("0x") == 0) {
					// try to convert from hex to dec
					return id.substring(2);
				}
			}
		} catch (AFPException afpe) {
			logger.error("Error parsing penID:" + id);
			
		} catch (NumberFormatException nfe) {
			logger.error("Error parsing serialnumber_hex:" + id);
		}
		
		return id;
	}
}

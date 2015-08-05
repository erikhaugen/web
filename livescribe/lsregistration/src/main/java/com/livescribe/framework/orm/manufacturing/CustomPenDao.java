/**
 * Created:  Sep 18, 2013 1:25:33 PM
 */
package com.livescribe.framework.orm.manufacturing;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import com.livescribe.framework.exception.MultipleRecordsFoundException;

/**
 * <p>Adds queries to the base <code>PenDao</code> class.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CustomPenDao extends PenDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p>Default class constructor.</p>
	 * 
	 */
	public CustomPenDao() {
		super();
	}

	/**
	 * <p>Returns the pen record matching the given display ID.</p>
	 * 
	 * <p>Returns <code>null</code> if no record is found.</p>
	 * 
	 * @param displayId The 14-character pen display ID.
	 * 
	 * @return the pen record matching the given display ID.
	 * 
	 * @throws MultipleRecordsFoundException if more than 1 record is found.
	 */
	public Pen findByDisplayId(String displayId) throws MultipleRecordsFoundException {
		
		Criteria crit = sessionFactoryManufacturing.getCurrentSession().createCriteria(Pen.class);
		crit.add(Restrictions.eq("displayId", displayId));
		List<Pen> list = crit.list();
		if ((list == null) || (list.isEmpty())) {
			logger.warn("findByDisplayId() - " + displayId + " - No record found.");
			return null;
		}
		if (list.size() > 1) {
			logger.warn("findByDisplayId() - " + displayId + " - Multiple records found.");
			throw new MultipleRecordsFoundException();
		}
		Pen pen = (Pen)list.get(0);
		return pen;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param serialNumber The numeric serial number of the pen.
	 * 
	 * @return a <code>Pen</code> object.
	 * 
	 * @throws MultipleRecordsFoundException
	 */
	@SuppressWarnings("unchecked")
	public Pen findBySerialNumber(String serialNumber) throws MultipleRecordsFoundException {
		
		Criteria crit = sessionFactoryManufacturing.getCurrentSession().createCriteria(Pen.class);
		crit.add(Restrictions.eq("serialnumber", serialNumber));
		
		List<Pen> list = null;
		try {
			list = crit.list();
		} catch (HibernateException e) {
			String msg = "HibernateException thrown";
			logger.debug(msg, e);
		}
		if ((list == null) || (list.isEmpty())) {
			logger.warn("findBySerialNumber() - " + serialNumber + " - No record found.");
			return null;
		}
		if (list.size() > 1) {
			String msg = "Multiple records found for pen with serial number '" + serialNumber + "'.";
			logger.warn("findBySerialNumber() - " + serialNumber + " - " + msg);
			throw new MultipleRecordsFoundException(msg);
		}
		Pen pen = (Pen)list.get(0);
		return pen;
	}
}

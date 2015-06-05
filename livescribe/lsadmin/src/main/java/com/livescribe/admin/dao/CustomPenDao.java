package com.livescribe.admin.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.livescribe.framework.orm.manufacturing.Pen;
import com.livescribe.framework.orm.manufacturing.PenDao;

/**
 * Custom DAO class for accessing 'Pen' records from the Corp_Manufacturing database.
 * @author Mohammad M. Naqvi
 *
 */
public class CustomPenDao extends PenDao {
    public SessionFactory getSessionFactory() {
        return sessionFactoryManufacturing;
    }
	
	/**
	 * <p>Finds the pen by given serial number.</p>
	 * 
     * @param serialNumber
     * @return Pen having the given serial number.
     */
	@SuppressWarnings("unchecked")
	public Pen findPenBySerailNumber(String serialNumber) {
		Criteria crit = sessionFactoryManufacturing.getCurrentSession().createCriteria(Pen.class)
				.add(Restrictions.eq("serialnumber", serialNumber))
				.addOrder(Order.desc("created"));
		
		List<Pen> pens = crit.list();
		if (null == pens || pens.isEmpty()) {
			return null;
		}
		return pens.get(0); // There should not be more than one Pen with a given serial number..
	}
	
	/**
	 * <p>Finds the pen by given displayId.</p>
	 * 
     * @param displayId
     * @return Pen having the given display Id.
     */
	@SuppressWarnings("unchecked")
	public Pen findPenByDisplayId(String displayId) {
		Criteria crit = sessionFactoryManufacturing.getCurrentSession().createCriteria(Pen.class)
				.add(Restrictions.eq("displayId", displayId))
				.addOrder(Order.desc("created"));
		
		List<Pen> pens = crit.list();
		if (null == pens || pens.isEmpty()) {
			return null;
		}
		return pens.get(0); // There should not be more than one Pen with a given serial number..
	}
	
	/**
	 * <p>Finds the list of pens by partial serial number.</p>
	 * 
     * @param partialSerialNumber
     * @return list of matching Pen records
     */
	@SuppressWarnings("unchecked")
	public List<Pen> findPensByPartialSerailNumber(String partialSerialNumber) {
		Criteria crit = sessionFactoryManufacturing.getCurrentSession().createCriteria(Pen.class)
				.add(Restrictions.like("serialnumber", partialSerialNumber, MatchMode.ANYWHERE))
				.addOrder(Order.desc("created"));
		crit.setMaxResults(200);
		return (List<Pen>)(crit.list());
	}
	
	/**
	 * <p>Finds the list of pens by partial displayId.</p>
	 * 
     * @param partialDisplayId
     * @return list of matching Pen records
     */
	@SuppressWarnings("unchecked")
	public List<Pen> findPensByPartialDisplayId(String partialDisplayId) {
		Criteria crit = sessionFactoryManufacturing.getCurrentSession().createCriteria(Pen.class)
				.add(Restrictions.like("displayId", partialDisplayId, MatchMode.ANYWHERE))
				.addOrder(Order.desc("created"));
		crit.setMaxResults(200);
		return (List<Pen>)(crit.list());
	}
}

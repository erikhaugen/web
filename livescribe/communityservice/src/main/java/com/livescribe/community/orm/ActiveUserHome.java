package com.livescribe.community.orm;
// Generated Jul 12, 2010 8:44:50 PM by Hibernate Tools 3.3.0.GA


import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class ActiveUser.
 * @see com.livescribe.community.orm.ActiveUser
 * @author Hibernate Tools
 */
public class ActiveUserHome extends BaseHome {

	/**
	 * <p>Default class constructor.</p>
	 */
	public ActiveUserHome() {
		super();
	}
	
    public void persist(ActiveUser transientInstance) {
        log.debug("persisting ActiveUser instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            log.debug("persist successful");
        }
        catch (RuntimeException re) {
            log.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(ActiveUser instance) {
        log.debug("attaching dirty ActiveUser instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            log.debug("attach successful");
        }
        catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(ActiveUser instance) {
        log.debug("attaching clean ActiveUser instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        }
        catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(ActiveUser persistentInstance) {
        log.debug("deleting ActiveUser instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            log.debug("delete successful");
        }
        catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public ActiveUser merge(ActiveUser detachedInstance) {
        log.debug("merging ActiveUser instance");
        try {
            ActiveUser result = (ActiveUser) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }
    
    public ActiveUser findById( byte[] id) {
        log.debug("getting ActiveUser instance with id: " + id);
        try {
            ActiveUser instance = (ActiveUser) sessionFactory.getCurrentSession()
                    .get("com.livescribe.community.orm.ActiveUser", id);
            if (instance==null) {
                log.debug("get successful, no instance found");
            }
            else {
                log.debug("get successful, instance found");
            }
            return instance;
        }
        catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    public List findByExample(ActiveUser instance) {
        log.debug("finding ActiveUser instance by example");
        try {
            List results = sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.community.orm.ActiveUser")
                    .add(Example.create(instance))
            .list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        }
        catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    } 
}


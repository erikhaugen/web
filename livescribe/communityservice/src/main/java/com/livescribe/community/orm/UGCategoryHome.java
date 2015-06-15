package com.livescribe.community.orm;
// Generated Jun 17, 2010 10:50:10 AM by Hibernate Tools 3.3.0.GA


import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Home object for domain model class UGCategory.
 * @see com.livescribe.community.orm.UGCategory
 * @author Hibernate Tools
 */
public class UGCategoryHome extends BaseHome {

//    private final SessionFactory sessionFactory = getSessionFactory();
//    
//    protected SessionFactory getSessionFactory() {
//        try {
//            return (SessionFactory) new InitialContext().lookup("SessionFactory");
//        }
//        catch (Exception e) {
//            log.error("Could not locate SessionFactory in JNDI", e);
//            throw new IllegalStateException("Could not locate SessionFactory in JNDI");
//        }
//    }
    
    public void persist(UGCategory transientInstance) {
        log.debug("persisting UGCategory instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            log.debug("persist successful");
        }
        catch (RuntimeException re) {
            log.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(UGCategory instance) {
        log.debug("attaching dirty UGCategory instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            log.debug("attach successful");
        }
        catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(UGCategory instance) {
        log.debug("attaching clean UGCategory instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        }
        catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(UGCategory persistentInstance) {
        log.debug("deleting UGCategory instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            log.debug("delete successful");
        }
        catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public UGCategory merge(UGCategory detachedInstance) {
        log.debug("merging UGCategory instance");
        try {
            UGCategory result = (UGCategory) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }
    
    public UGCategory findById( byte[] id) {
        log.debug("getting UGCategory instance with id: " + id);
        try {
            UGCategory instance = (UGCategory) sessionFactory.getCurrentSession()
                    .get("com.livescribe.community.orm.UGCategory", id);
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
    
    public List findByExample(UGCategory instance) {
        log.debug("finding UGCategory instance by example");
        try {
            List results = sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.community.orm.UGCategory")
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


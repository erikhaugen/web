package com.livescribe.community.orm;
// Generated Jul 1, 2010 11:08:11 AM by Hibernate Tools 3.3.0.GA


import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class UGComment.
 * @see com.livescribe.community.orm.UGComment
 * @author Hibernate Tools
 */
public class UGCommentHome extends BaseHome {

    public void persist(UGComment transientInstance) {
        log.debug("persisting UGComment instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            log.debug("persist successful");
        }
        catch (RuntimeException re) {
            log.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(UGComment instance) {
        log.debug("attaching dirty UGComment instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            log.debug("attach successful");
        }
        catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(UGComment instance) {
        log.debug("attaching clean UGComment instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        }
        catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(UGComment persistentInstance) {
        log.debug("deleting UGComment instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            log.debug("delete successful");
        }
        catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public UGComment merge(UGComment detachedInstance) {
        log.debug("merging UGComment instance");
        try {
            UGComment result = (UGComment) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }
    
    public UGComment findById( byte[] id) {
        log.debug("getting UGComment instance with id: " + id);
        try {
            UGComment instance = (UGComment) sessionFactory.getCurrentSession()
                    .get("com.livescribe.community.orm.UGComment", id);
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
    
    public List findByExample(UGComment instance) {
        log.debug("finding UGComment instance by example");
        try {
            List results = sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.community.orm.UGComment")
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


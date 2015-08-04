package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Feb 28, 2012 2:27:21 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.CaptivePortal;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class CaptivePortal.
 * @see com.livescribe.aws.heartbeat.orm.CaptivePortal
 * @author Hibernate Tools
 */
public class CaptivePortalDaoImpl extends AbstractDao implements GenericDao<CaptivePortal> {

    
    @Transactional
    public void persist(CaptivePortal transientInstance) {
        logger.debug("persisting CaptivePortal instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(CaptivePortal instance) {
        logger.debug("attaching dirty CaptivePortal instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(CaptivePortal instance) {
        logger.debug("attaching clean CaptivePortal instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(CaptivePortal persistentInstance) {
        logger.debug("deleting CaptivePortal instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public CaptivePortal merge(CaptivePortal detachedInstance) {
//        logger.debug("merging CaptivePortal instance");
        try {
            CaptivePortal result = (CaptivePortal) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public CaptivePortal findById( java.lang.Long id) {
        logger.debug("getting CaptivePortal instance with id: " + id);
        try {
            CaptivePortal instance = (CaptivePortal) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.CaptivePortal", id);
            if (instance==null) {
                logger.debug("get successful, no instance found");
            }
            else {
                logger.debug("get successful, instance found");
            }
            return instance;
        }
        catch (RuntimeException re) {
            logger.error("get failed", re);
            throw re;
        }
    }
    
    public List<CaptivePortal> findByExample(CaptivePortal instance) {
        logger.debug("finding CaptivePortal instance by example");
        try {
            List<CaptivePortal> results = (List<CaptivePortal>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.CaptivePortal")
                    .add( create(instance) )
            .list();
            logger.debug("find by example successful, result size: " + results.size());
            return results;
        }
        catch (RuntimeException re) {
            logger.error("find by example failed", re);
            throw re;
        }
    }
    
}


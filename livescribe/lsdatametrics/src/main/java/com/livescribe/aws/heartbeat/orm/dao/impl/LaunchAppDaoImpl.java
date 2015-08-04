package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.LaunchApp;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class LaunchApp.
 * @see com.livescribe.aws.heartbeat.orm.LaunchApp
 * @author Hibernate Tools
 */
public class LaunchAppDaoImpl extends AbstractDao implements GenericDao<LaunchApp> {

    
    @Transactional
    public void persist(LaunchApp transientInstance) {
        logger.debug("persisting LaunchApp instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(LaunchApp instance) {
        logger.debug("attaching dirty LaunchApp instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(LaunchApp instance) {
        logger.debug("attaching clean LaunchApp instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(LaunchApp persistentInstance) {
        logger.debug("deleting LaunchApp instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public LaunchApp merge(LaunchApp detachedInstance) {
//        logger.debug("merging LaunchApp instance");
        try {
            LaunchApp result = (LaunchApp) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public LaunchApp findById( java.lang.Long id) {
        logger.debug("getting LaunchApp instance with id: " + id);
        try {
            LaunchApp instance = (LaunchApp) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.LaunchApp", id);
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
    
    public List<LaunchApp> findByExample(LaunchApp instance) {
        logger.debug("finding LaunchApp instance by example");
        try {
            List<LaunchApp> results = (List<LaunchApp>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.LaunchApp")
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


package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.LaunchLine;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class LaunchLine.
 * @see com.livescribe.aws.heartbeat.orm.LaunchLine
 * @author Hibernate Tools
 */
public class LaunchLineDaoImpl extends AbstractDao implements GenericDao<LaunchLine> {

    
    @Transactional
    public void persist(LaunchLine transientInstance) {
        logger.debug("persisting LaunchLine instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(LaunchLine instance) {
        logger.debug("attaching dirty LaunchLine instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(LaunchLine instance) {
        logger.debug("attaching clean LaunchLine instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(LaunchLine persistentInstance) {
        logger.debug("deleting LaunchLine instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public LaunchLine merge(LaunchLine detachedInstance) {
//        logger.debug("merging LaunchLine instance");
        try {
            LaunchLine result = (LaunchLine) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public LaunchLine findById( java.lang.Long id) {
        logger.debug("getting LaunchLine instance with id: " + id);
        try {
            LaunchLine instance = (LaunchLine) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.LaunchLine", id);
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
    
    public List<LaunchLine> findByExample(LaunchLine instance) {
        logger.debug("finding LaunchLine instance by example");
        try {
            List<LaunchLine> results = (List<LaunchLine>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.LaunchLine")
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


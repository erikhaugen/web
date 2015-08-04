package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.Crash;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class Crash.
 * @see com.livescribe.aws.heartbeat.orm.Crash
 * @author Hibernate Tools
 */
public class CrashDaoImpl extends AbstractDao implements GenericDao<Crash> {

    
    @Transactional
    public void persist(Crash transientInstance) {
        logger.debug("persisting Crash instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(Crash instance) {
        logger.debug("attaching dirty Crash instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(Crash instance) {
        logger.debug("attaching clean Crash instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(Crash persistentInstance) {
        logger.debug("deleting Crash instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public Crash merge(Crash detachedInstance) {
//        logger.debug("merging Crash instance");
        try {
            Crash result = (Crash) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public Crash findById( java.lang.Long id) {
        logger.debug("getting Crash instance with id: " + id);
        try {
            Crash instance = (Crash) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.Crash", id);
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
    
    public List<Crash> findByExample(Crash instance) {
        logger.debug("finding Crash instance by example");
        try {
            List<Crash> results = (List<Crash>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.Crash")
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


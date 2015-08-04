package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 11, 2011 1:32:27 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.Quickrecord;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class Quickrecord.
 * @see com.livescribe.aws.heartbeat.orm.Quickrecord
 * @author Hibernate Tools
 */
public class QuickrecordDaoImpl extends AbstractDao implements GenericDao<Quickrecord> {

    
    @Transactional
    public void persist(Quickrecord transientInstance) {
        logger.debug("persisting Quickrecord instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(Quickrecord instance) {
        logger.debug("attaching dirty Quickrecord instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(Quickrecord instance) {
        logger.debug("attaching clean Quickrecord instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(Quickrecord persistentInstance) {
        logger.debug("deleting Quickrecord instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public Quickrecord merge(Quickrecord detachedInstance) {
//        logger.debug("merging Quickrecord instance");
        try {
            Quickrecord result = (Quickrecord) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public Quickrecord findById( java.lang.Long id) {
        logger.debug("getting Quickrecord instance with id: " + id);
        try {
            Quickrecord instance = (Quickrecord) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.Quickrecord", id);
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
    
    public List<Quickrecord> findByExample(Quickrecord instance) {
        logger.debug("finding Quickrecord instance by example");
        try {
            List<Quickrecord> results = (List<Quickrecord>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.Quickrecord")
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


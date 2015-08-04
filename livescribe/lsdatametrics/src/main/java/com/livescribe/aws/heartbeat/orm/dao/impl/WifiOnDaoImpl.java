package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.WifiOn;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class WifiOn.
 * @see com.livescribe.aws.heartbeat.orm.WifiOn
 * @author Hibernate Tools
 */
public class WifiOnDaoImpl extends AbstractDao implements GenericDao<WifiOn> {

    
    @Transactional
    public void persist(WifiOn transientInstance) {
        logger.debug("persisting WifiOn instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(WifiOn instance) {
        logger.debug("attaching dirty WifiOn instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(WifiOn instance) {
        logger.debug("attaching clean WifiOn instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(WifiOn persistentInstance) {
        logger.debug("deleting WifiOn instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public WifiOn merge(WifiOn detachedInstance) {
//        logger.debug("merging WifiOn instance");
        try {
            WifiOn result = (WifiOn) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public WifiOn findById( java.lang.Long id) {
        logger.debug("getting WifiOn instance with id: " + id);
        try {
            WifiOn instance = (WifiOn) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.WifiOn", id);
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
    
    public List<WifiOn> findByExample(WifiOn instance) {
        logger.debug("finding WifiOn instance by example");
        try {
            List<WifiOn> results = (List<WifiOn>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.WifiOn")
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


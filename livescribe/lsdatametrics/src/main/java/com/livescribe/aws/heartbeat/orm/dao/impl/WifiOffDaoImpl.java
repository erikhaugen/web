package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.WifiOff;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class WifiOff.
 * @see com.livescribe.aws.heartbeat.orm.WifiOff
 * @author Hibernate Tools
 */
public class WifiOffDaoImpl extends AbstractDao implements GenericDao<WifiOff> {

    
    @Transactional
    public void persist(WifiOff transientInstance) {
        logger.debug("persisting WifiOff instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(WifiOff instance) {
        logger.debug("attaching dirty WifiOff instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(WifiOff instance) {
        logger.debug("attaching clean WifiOff instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(WifiOff persistentInstance) {
        logger.debug("deleting WifiOff instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public WifiOff merge(WifiOff detachedInstance) {
//        logger.debug("merging WifiOff instance");
        try {
            WifiOff result = (WifiOff) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public WifiOff findById( java.lang.Long id) {
        logger.debug("getting WifiOff instance with id: " + id);
        try {
            WifiOff instance = (WifiOff) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.WifiOff", id);
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
    
    public List<WifiOff> findByExample(WifiOff instance) {
        logger.debug("finding WifiOff instance by example");
        try {
            List<WifiOff> results = (List<WifiOff>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.WifiOff")
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


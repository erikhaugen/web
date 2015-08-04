package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Feb 14, 2012 4:16:39 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.NewHostSpot;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class NewHostSpot.
 * @see com.livescribe.aws.heartbeat.orm.NewHostSpot
 * @author Hibernate Tools
 */
public class NewHostSpotDaoImpl extends AbstractDao implements GenericDao<NewHostSpot> {

    
    @Transactional
    public void persist(NewHostSpot transientInstance) {
        logger.debug("persisting NewHostSpot instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(NewHostSpot instance) {
        logger.debug("attaching dirty NewHostSpot instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(NewHostSpot instance) {
        logger.debug("attaching clean NewHostSpot instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(NewHostSpot persistentInstance) {
        logger.debug("deleting NewHostSpot instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public NewHostSpot merge(NewHostSpot detachedInstance) {
//        logger.debug("merging NewHostSpot instance");
        try {
            NewHostSpot result = (NewHostSpot) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public NewHostSpot findById( java.lang.Long id) {
        logger.debug("getting NewHostSpot instance with id: " + id);
        try {
            NewHostSpot instance = (NewHostSpot) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.NewHostSpot", id);
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
    
    public List<NewHostSpot> findByExample(NewHostSpot instance) {
        logger.debug("finding NewHostSpot instance by example");
        try {
            List<NewHostSpot> results = (List<NewHostSpot>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.NewHostSpot")
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


package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.DynnavplusCreated;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class DynnavplusCreated.
 * @see com.livescribe.aws.heartbeat.orm.DynnavplusCreated
 * @author Hibernate Tools
 */
public class DynnavplusCreatedDaoImpl extends AbstractDao implements GenericDao<DynnavplusCreated> {

    
    @Transactional
    public void persist(DynnavplusCreated transientInstance) {
        logger.debug("persisting DynnavplusCreated instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(DynnavplusCreated instance) {
        logger.debug("attaching dirty DynnavplusCreated instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(DynnavplusCreated instance) {
        logger.debug("attaching clean DynnavplusCreated instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(DynnavplusCreated persistentInstance) {
        logger.debug("deleting DynnavplusCreated instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public DynnavplusCreated merge(DynnavplusCreated detachedInstance) {
//        logger.debug("merging DynnavplusCreated instance");
        try {
            DynnavplusCreated result = (DynnavplusCreated) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public DynnavplusCreated findById( java.lang.Long id) {
        logger.debug("getting DynnavplusCreated instance with id: " + id);
        try {
            DynnavplusCreated instance = (DynnavplusCreated) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.DynnavplusCreated", id);
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
    
    public List<DynnavplusCreated> findByExample(DynnavplusCreated instance) {
        logger.debug("finding DynnavplusCreated instance by example");
        try {
            List<DynnavplusCreated> results = (List<DynnavplusCreated>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.DynnavplusCreated")
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


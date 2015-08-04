package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.PenUndocked;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class PenUndocked.
 * @see com.livescribe.aws.heartbeat.orm.PenUndocked
 * @author Hibernate Tools
 */
public class PenUndockedDaoImpl extends AbstractDao implements GenericDao<PenUndocked> {

    
    @Transactional
    public void persist(PenUndocked transientInstance) {
        logger.debug("persisting PenUndocked instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(PenUndocked instance) {
        logger.debug("attaching dirty PenUndocked instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(PenUndocked instance) {
        logger.debug("attaching clean PenUndocked instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(PenUndocked persistentInstance) {
        logger.debug("deleting PenUndocked instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public PenUndocked merge(PenUndocked detachedInstance) {
//        logger.debug("merging PenUndocked instance");
        try {
            PenUndocked result = (PenUndocked) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public PenUndocked findById( java.lang.Long id) {
        logger.debug("getting PenUndocked instance with id: " + id);
        try {
            PenUndocked instance = (PenUndocked) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.PenUndocked", id);
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
    
    public List<PenUndocked> findByExample(PenUndocked instance) {
        logger.debug("finding PenUndocked instance by example");
        try {
            List<PenUndocked> results = (List<PenUndocked>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.PenUndocked")
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


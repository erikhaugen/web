package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.PenOff;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class PenOff.
 * @see com.livescribe.aws.heartbeat.orm.PenOff
 * @author Hibernate Tools
 */
public class PenOffDaoImpl extends AbstractDao implements GenericDao<PenOff> {

    
    @Transactional
    public void persist(PenOff transientInstance) {
        logger.debug("persisting PenOff instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(PenOff instance) {
        logger.debug("attaching dirty PenOff instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(PenOff instance) {
        logger.debug("attaching clean PenOff instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(PenOff persistentInstance) {
        logger.debug("deleting PenOff instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public PenOff merge(PenOff detachedInstance) {
//        logger.debug("merging PenOff instance");
        try {
            PenOff result = (PenOff) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public PenOff findById( java.lang.Long id) {
        logger.debug("getting PenOff instance with id: " + id);
        try {
            PenOff instance = (PenOff) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.PenOff", id);
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
    
    public List<PenOff> findByExample(PenOff instance) {
        logger.debug("finding PenOff instance by example");
        try {
            List<PenOff> results = (List<PenOff>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.PenOff")
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


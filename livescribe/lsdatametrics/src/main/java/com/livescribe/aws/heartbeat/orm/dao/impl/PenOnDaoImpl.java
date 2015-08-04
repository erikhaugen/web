package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.PenOn;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class PenOn.
 * @see com.livescribe.aws.heartbeat.orm.PenOn
 * @author Hibernate Tools
 */
public class PenOnDaoImpl extends AbstractDao implements GenericDao<PenOn> {

    
    @Transactional
    public void persist(PenOn transientInstance) {
        logger.debug("persisting PenOn instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(PenOn instance) {
        logger.debug("attaching dirty PenOn instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(PenOn instance) {
        logger.debug("attaching clean PenOn instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(PenOn persistentInstance) {
        logger.debug("deleting PenOn instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public PenOn merge(PenOn detachedInstance) {
//        logger.debug("merging PenOn instance");
        try {
            PenOn result = (PenOn) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public PenOn findById( java.lang.Long id) {
        logger.debug("getting PenOn instance with id: " + id);
        try {
            PenOn instance = (PenOn) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.PenOn", id);
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
    
    public List<PenOn> findByExample(PenOn instance) {
        logger.debug("finding PenOn instance by example");
        try {
            List<PenOn> results = (List<PenOn>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.PenOn")
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


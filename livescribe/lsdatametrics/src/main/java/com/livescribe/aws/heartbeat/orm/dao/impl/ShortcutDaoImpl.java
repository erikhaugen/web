package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.Shortcut;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class Shortcut.
 * @see com.livescribe.aws.heartbeat.orm.Shortcut
 * @author Hibernate Tools
 */
public class ShortcutDaoImpl extends AbstractDao implements GenericDao<Shortcut> {

    
    @Transactional
    public void persist(Shortcut transientInstance) {
        logger.debug("persisting Shortcut instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(Shortcut instance) {
        logger.debug("attaching dirty Shortcut instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(Shortcut instance) {
        logger.debug("attaching clean Shortcut instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(Shortcut persistentInstance) {
        logger.debug("deleting Shortcut instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public Shortcut merge(Shortcut detachedInstance) {
//        logger.debug("merging Shortcut instance");
        try {
            Shortcut result = (Shortcut) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public Shortcut findById( java.lang.Long id) {
        logger.debug("getting Shortcut instance with id: " + id);
        try {
            Shortcut instance = (Shortcut) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.Shortcut", id);
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
    
    public List<Shortcut> findByExample(Shortcut instance) {
        logger.debug("finding Shortcut instance by example");
        try {
            List<Shortcut> results = (List<Shortcut>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.Shortcut")
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


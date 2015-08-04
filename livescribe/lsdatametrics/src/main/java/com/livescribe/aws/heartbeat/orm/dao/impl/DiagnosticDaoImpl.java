package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Feb 14, 2012 4:16:39 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.Diagnostic;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class Diagnostic.
 * @see com.livescribe.aws.heartbeat.orm.Diagnostic
 * @author Hibernate Tools
 */
public class DiagnosticDaoImpl extends AbstractDao implements GenericDao<Diagnostic> {

    
    @Transactional
    public void persist(Diagnostic transientInstance) {
        logger.debug("persisting Diagnostic instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(Diagnostic instance) {
        logger.debug("attaching dirty Diagnostic instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(Diagnostic instance) {
        logger.debug("attaching clean Diagnostic instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(Diagnostic persistentInstance) {
        logger.debug("deleting Diagnostic instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public Diagnostic merge(Diagnostic detachedInstance) {
//        logger.debug("merging Diagnostic instance");
        try {
            Diagnostic result = (Diagnostic) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public Diagnostic findById( java.lang.Long id) {
        logger.debug("getting Diagnostic instance with id: " + id);
        try {
            Diagnostic instance = (Diagnostic) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.Diagnostic", id);
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
    
    public List<Diagnostic> findByExample(Diagnostic instance) {
        logger.debug("finding Diagnostic instance by example");
        try {
            List<Diagnostic> results = (List<Diagnostic>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.Diagnostic")
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


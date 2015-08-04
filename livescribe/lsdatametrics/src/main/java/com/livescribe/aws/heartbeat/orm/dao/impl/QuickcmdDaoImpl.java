package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.Quickcmd;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class Quickcmd.
 * @see com.livescribe.aws.heartbeat.orm.Quickcmd
 * @author Hibernate Tools
 */
public class QuickcmdDaoImpl extends AbstractDao implements GenericDao<Quickcmd> {

    
    @Transactional
    public void persist(Quickcmd transientInstance) {
        logger.debug("persisting Quickcmd instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(Quickcmd instance) {
        logger.debug("attaching dirty Quickcmd instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(Quickcmd instance) {
        logger.debug("attaching clean Quickcmd instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(Quickcmd persistentInstance) {
        logger.debug("deleting Quickcmd instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public Quickcmd merge(Quickcmd detachedInstance) {
//        logger.debug("merging Quickcmd instance");
        try {
            Quickcmd result = (Quickcmd) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public Quickcmd findById( java.lang.Long id) {
        logger.debug("getting Quickcmd instance with id: " + id);
        try {
            Quickcmd instance = (Quickcmd) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.Quickcmd", id);
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
    
    public List<Quickcmd> findByExample(Quickcmd instance) {
        logger.debug("finding Quickcmd instance by example");
        try {
            List<Quickcmd> results = (List<Quickcmd>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.Quickcmd")
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


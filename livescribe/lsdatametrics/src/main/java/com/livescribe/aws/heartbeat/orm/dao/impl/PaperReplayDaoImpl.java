package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.PaperReplay;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class PaperReplay.
 * @see com.livescribe.aws.heartbeat.orm.PaperReplay
 * @author Hibernate Tools
 */
public class PaperReplayDaoImpl extends AbstractDao implements GenericDao<PaperReplay> {

    
    @Transactional
    public void persist(PaperReplay transientInstance) {
        logger.debug("persisting PaperReplay instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(PaperReplay instance) {
        logger.debug("attaching dirty PaperReplay instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(PaperReplay instance) {
        logger.debug("attaching clean PaperReplay instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(PaperReplay persistentInstance) {
        logger.debug("deleting PaperReplay instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public PaperReplay merge(PaperReplay detachedInstance) {
//        logger.debug("merging PaperReplay instance");
        try {
            PaperReplay result = (PaperReplay) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public PaperReplay findById( java.lang.Long id) {
        logger.debug("getting PaperReplay instance with id: " + id);
        try {
            PaperReplay instance = (PaperReplay) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.PaperReplay", id);
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
    
    public List<PaperReplay> findByExample(PaperReplay instance) {
        logger.debug("finding PaperReplay instance by example");
        try {
            List<PaperReplay> results = (List<PaperReplay>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.PaperReplay")
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


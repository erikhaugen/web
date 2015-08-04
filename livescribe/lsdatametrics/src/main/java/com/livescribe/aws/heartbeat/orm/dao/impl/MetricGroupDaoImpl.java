package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.MetricGroup;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class MetricGroup.
 * @see com.livescribe.aws.heartbeat.orm.MetricGroup
 * @author Hibernate Tools
 */
public class MetricGroupDaoImpl extends AbstractDao implements GenericDao<MetricGroup> {

    
    @Transactional
    public void persist(MetricGroup transientInstance) {
        logger.debug("persisting MetricGroup instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    @Transactional
    public void attachDirty(MetricGroup instance) {
        logger.debug("attaching dirty MetricGroup instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    @Transactional
    public void attachClean(MetricGroup instance) {
        logger.debug("attaching clean MetricGroup instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    @Transactional
    public void delete(MetricGroup persistentInstance) {
        logger.debug("deleting MetricGroup instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    @Transactional
    public MetricGroup merge(MetricGroup detachedInstance) {
//        logger.debug("merging MetricGroup instance");
        try {
            MetricGroup result = (MetricGroup) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    @Transactional
    public MetricGroup findById( java.lang.Long id) {
        logger.debug("getting MetricGroup instance with id: " + id);
        try {
            MetricGroup instance = (MetricGroup) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.MetricGroup", id);
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
    
    @Transactional
    public List<MetricGroup> findByExample(MetricGroup instance) {
        logger.debug("finding MetricGroup instance by example");
        try {
            List<MetricGroup> results = (List<MetricGroup>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.MetricGroup")
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


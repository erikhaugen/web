package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.PageSwitch;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class PageSwitch.
 * @see com.livescribe.aws.heartbeat.orm.PageSwitch
 * @author Hibernate Tools
 */
public class PageSwitchDaoImpl extends AbstractDao implements GenericDao<PageSwitch> {

    
    @Transactional
    public void persist(PageSwitch transientInstance) {
        logger.debug("persisting PageSwitch instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(PageSwitch instance) {
        logger.debug("attaching dirty PageSwitch instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(PageSwitch instance) {
        logger.debug("attaching clean PageSwitch instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(PageSwitch persistentInstance) {
        logger.debug("deleting PageSwitch instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public PageSwitch merge(PageSwitch detachedInstance) {
//        logger.debug("merging PageSwitch instance");
        try {
            PageSwitch result = (PageSwitch) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public PageSwitch findById( java.lang.Long id) {
        logger.debug("getting PageSwitch instance with id: " + id);
        try {
            PageSwitch instance = (PageSwitch) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.PageSwitch", id);
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
    
    public List<PageSwitch> findByExample(PageSwitch instance) {
        logger.debug("finding PageSwitch instance by example");
        try {
            List<PageSwitch> results = (List<PageSwitch>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.PageSwitch")
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


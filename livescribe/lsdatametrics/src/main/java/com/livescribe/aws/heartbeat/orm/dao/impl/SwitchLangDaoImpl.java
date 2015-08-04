package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.SwitchLang;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class SwitchLang.
 * @see com.livescribe.aws.heartbeat.orm.SwitchLang
 * @author Hibernate Tools
 */
public class SwitchLangDaoImpl extends AbstractDao implements GenericDao<SwitchLang> {

    
    @Transactional
    public void persist(SwitchLang transientInstance) {
        logger.debug("persisting SwitchLang instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(SwitchLang instance) {
        logger.debug("attaching dirty SwitchLang instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(SwitchLang instance) {
        logger.debug("attaching clean SwitchLang instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(SwitchLang persistentInstance) {
        logger.debug("deleting SwitchLang instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public SwitchLang merge(SwitchLang detachedInstance) {
//        logger.debug("merging SwitchLang instance");
        try {
            SwitchLang result = (SwitchLang) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public SwitchLang findById( java.lang.Long id) {
        logger.debug("getting SwitchLang instance with id: " + id);
        try {
            SwitchLang instance = (SwitchLang) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.SwitchLang", id);
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
    
    public List<SwitchLang> findByExample(SwitchLang instance) {
        logger.debug("finding SwitchLang instance by example");
        try {
            List<SwitchLang> results = (List<SwitchLang>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.SwitchLang")
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


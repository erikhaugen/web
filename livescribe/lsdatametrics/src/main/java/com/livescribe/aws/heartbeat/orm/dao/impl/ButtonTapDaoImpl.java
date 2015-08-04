package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.ButtonTap;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class ButtonTap.
 * @see com.livescribe.aws.heartbeat.orm.ButtonTap
 * @author Hibernate Tools
 */
public class ButtonTapDaoImpl extends AbstractDao implements GenericDao<ButtonTap> {

    
    @Transactional
    public void persist(ButtonTap transientInstance) {
        logger.debug("persisting ButtonTap instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(ButtonTap instance) {
        logger.debug("attaching dirty ButtonTap instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(ButtonTap instance) {
        logger.debug("attaching clean ButtonTap instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(ButtonTap persistentInstance) {
        logger.debug("deleting ButtonTap instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public ButtonTap merge(ButtonTap detachedInstance) {
//        logger.debug("merging ButtonTap instance");
        try {
            ButtonTap result = (ButtonTap) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public ButtonTap findById( java.lang.Long id) {
        logger.debug("getting ButtonTap instance with id: " + id);
        try {
            ButtonTap instance = (ButtonTap) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.ButtonTap", id);
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
    
    public List<ButtonTap> findByExample(ButtonTap instance) {
        logger.debug("finding ButtonTap instance by example");
        try {
            List<ButtonTap> results = (List<ButtonTap>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.ButtonTap")
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


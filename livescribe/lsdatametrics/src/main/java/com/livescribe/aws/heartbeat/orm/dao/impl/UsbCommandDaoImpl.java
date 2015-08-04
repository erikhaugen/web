package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.UsbCommand;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class UsbCommand.
 * @see com.livescribe.aws.heartbeat.orm.UsbCommand
 * @author Hibernate Tools
 */
public class UsbCommandDaoImpl extends AbstractDao implements GenericDao<UsbCommand> {

    
    @Transactional
    public void persist(UsbCommand transientInstance) {
        logger.debug("persisting UsbCommand instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(UsbCommand instance) {
        logger.debug("attaching dirty UsbCommand instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(UsbCommand instance) {
        logger.debug("attaching clean UsbCommand instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(UsbCommand persistentInstance) {
        logger.debug("deleting UsbCommand instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public UsbCommand merge(UsbCommand detachedInstance) {
//        logger.debug("merging UsbCommand instance");
        try {
            UsbCommand result = (UsbCommand) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public UsbCommand findById( java.lang.Long id) {
        logger.debug("getting UsbCommand instance with id: " + id);
        try {
            UsbCommand instance = (UsbCommand) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.UsbCommand", id);
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
    
    public List<UsbCommand> findByExample(UsbCommand instance) {
        logger.debug("finding UsbCommand instance by example");
        try {
            List<UsbCommand> results = (List<UsbCommand>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.UsbCommand")
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


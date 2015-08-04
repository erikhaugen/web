package com.livescribe.aws.heartbeat.orm.dao.impl;
// Generated Nov 9, 2011 5:40:48 PM by Hibernate Tools 3.2.2.GA


import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.aws.heartbeat.orm.FirmwareUpdate;
import com.livescribe.aws.heartbeat.orm.dao.AbstractDao;
import com.livescribe.aws.heartbeat.orm.dao.GenericDao;

/**
 * Home object for domain model class FirmwareUpdate.
 * @see com.livescribe.aws.heartbeat.orm.FirmwareUpdate
 * @author Hibernate Tools
 */
public class FirmwareUpdateDaoImpl extends AbstractDao implements GenericDao<FirmwareUpdate> {

    
    @Transactional
    public void persist(FirmwareUpdate transientInstance) {
        logger.debug("persisting FirmwareUpdate instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(FirmwareUpdate instance) {
        logger.debug("attaching dirty FirmwareUpdate instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(FirmwareUpdate instance) {
        logger.debug("attaching clean FirmwareUpdate instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(FirmwareUpdate persistentInstance) {
        logger.debug("deleting FirmwareUpdate instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public FirmwareUpdate merge(FirmwareUpdate detachedInstance) {
//        logger.debug("merging FirmwareUpdate instance");
        try {
            FirmwareUpdate result = (FirmwareUpdate) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
    public FirmwareUpdate findById( java.lang.Long id) {
        logger.debug("getting FirmwareUpdate instance with id: " + id);
        try {
            FirmwareUpdate instance = (FirmwareUpdate) sessionFactory.getCurrentSession()
                    .get("com.livescribe.aws.heartbeat.orm.FirmwareUpdate", id);
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
    
    public List<FirmwareUpdate> findByExample(FirmwareUpdate instance) {
        logger.debug("finding FirmwareUpdate instance by example");
        try {
            List<FirmwareUpdate> results = (List<FirmwareUpdate>) sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.aws.heartbeat.orm.FirmwareUpdate")
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


package com.livescribe.community.orm;
// Generated Jun 17, 2010 10:50:10 AM by Hibernate Tools 3.3.0.GA


import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class UserProfile.
 * @see com.livescribe.community.orm.UserProfile
 * @author Hibernate Tools
 */
public class UserProfileHome extends BaseHome {

    public void attachDirty(UserProfile instance) {
        log.debug("attaching dirty UserProfile instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            log.debug("attach successful");
        }
        catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(UserProfile instance) {
        log.debug("attaching clean UserProfile instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        }
        catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(UserProfile persistentInstance) {
        log.debug("deleting UserProfile instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            log.debug("delete successful");
        }
        catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public UserProfile findById(byte[] id) {
        log.debug("getting UserProfile instance with id: " + id);
        try {
            UserProfile instance = (UserProfile) sessionFactory.getCurrentSession()
                    .get("com.livescribe.community.orm.UserProfile", id);
            if (instance==null) {
                log.debug("get successful, no instance found");
            }
            else {
                log.debug("get successful, instance found");
            }
            return instance;
        }
        catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    /**
	 * <p>Returns a user&apos;s profile identified by the given short ID.</p>
	 * 
	 * @param id The unique short ID of the user.
	 * 
	 * @return a user&apos;s profile identified by the given short ID.
	 */
	public UserProfile findById(String id) {
		
		String method = "findById():  ";
		UserProfile user = null;
		
		try {
			Query q = sessionFactory.getCurrentSession().createQuery("from UserProfile up where up.shortId = " + id);
			List<UserProfile> list = q.list();
			if (list.size() > 1) {
				log.warn(method + "More than one user was found with shortId = '" + id + "'.");
			}
			else if (list.size() == 0) {
				log.info(method + "No user found with shortId = '" + id + "'.");
			}
			else {
				user = list.get(0);
			}
		}
		catch (RuntimeException re) {
			log.error(method + "Failed!  " + re);
		}
		return user;
	}

	public List findByExample(UserProfile instance) {
        log.debug("finding UserProfile instance by example");
        try {
            List results = sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.community.orm.UserProfile")
                    .add(Example.create(instance))
            .list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        }
        catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }

	public UserProfile merge(UserProfile detachedInstance) {
	    log.debug("merging UserProfile instance");
	    try {
	        UserProfile result = (UserProfile) sessionFactory.getCurrentSession()
	                .merge(detachedInstance);
	        log.debug("merge successful");
	        return result;
	    }
	    catch (RuntimeException re) {
	        log.error("merge failed", re);
	        throw re;
	    }
	}

	public void persist(UserProfile transientInstance) {
        log.debug("persisting UserProfile instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            log.debug("persist successful");
        }
        catch (RuntimeException re) {
            log.error("persist failed", re);
            throw re;
        }
    } 
}


package com.livescribe.community.orm;
// Generated Jun 17, 2010 10:50:10 AM by Hibernate Tools 3.3.0.GA


import java.util.ArrayList;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.community.dto.Pencast;

/**
 * Home object for domain model class UGFile.
 * @see com.livescribe.community.orm.UGFile
 * @author Hibernate Tools
 */
public class UGFileHome extends BaseHome {

//	@Autowired
//	private SessionFactory sessionFactory;
//	
////    private static final Log log = LogFactory.getLog(UGFileHome.class);
//    private static final Logger log = Logger.getLogger(UGFileHome.class);
//
//    private final SessionFactory sessionFactory = getSessionFactory();
//    
//    protected SessionFactory getSessionFactory() {
//        try {
//            return (SessionFactory) new InitialContext().lookup("SessionFactory");
//        }
//        catch (Exception e) {
//            log.error("Could not locate SessionFactory in JNDI", e);
//            throw new IllegalStateException("Could not locate SessionFactory in JNDI");
//        }
//    }
    
    /**
     * 
     */
    public UGFileHome() {
    	
    	log.debug("Initialized ...");
    }
    
    public void persist(UGFile transientInstance) {
        log.debug("persisting UGFile instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            log.debug("persist successful");
        }
        catch (RuntimeException re) {
            log.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(UGFile instance) {
        log.debug("attaching dirty UGFile instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            log.debug("attach successful");
        }
        catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(UGFile instance) {
        log.debug("attaching clean UGFile instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        }
        catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(UGFile persistentInstance) {
        log.debug("deleting UGFile instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            log.debug("delete successful");
        }
        catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public UGFile merge(UGFile detachedInstance) {
        log.debug("merging UGFile instance");
        try {
            UGFile result = (UGFile) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }
    
    public UGFile findById( byte[] id) {
        log.debug("getting UGFile instance with id: " + id);
        try {
            UGFile instance = (UGFile) sessionFactory.getCurrentSession()
                    .get("com.livescribe.community.orm.UGFile", id);
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
    
    public List findAll() {
    	
    	try {
    		Query q = sessionFactory.getCurrentSession().createQuery("from UGFile");
    		List list = q.list();
    		return list;
    	}
    	catch (RuntimeException re) {
            log.error("findAll():  Failed.", re);
            throw re;
    	}
    }
    
    /**
     * <p></p>
     * 
     * @param id
     * 
     * @return a <code>UGFile</code> identified by the given &apos;shortId&apos;, <code>null</code>
     * if none, or more than one are found.
     */
    public UGFile findByShortId(String id) {
    	
    	try {
    		Query q = sessionFactory.getCurrentSession().createQuery("from UGFile f where f.shortId = '" + id + "'");
    		List<UGFile> list = q.list();
    		if (list.size() == 1) {
    			return list.get(0);
    		}
    		else {
    			log.error("More than one UGFile found with 'shortId' = '" + id + "'");
    			return null;
    		}
    	}
    	catch (RuntimeException re) {
            log.error("findByShortId():  Failed.", re);
            throw re;
    	}
    }
    
    public List findByExample(UGFile instance) {
        log.debug("finding UGFile instance by example");
        try {
            List results = sessionFactory.getCurrentSession()
                    .createCriteria("com.livescribe.community.orm.UGFile")
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
    
    /**
     * 
     * 
     * @param user
     * 
     * @return
     */
    public List<Pencast> findByUser(UserProfile user) {
    	
    	String method = "findByUser():  ";
    	
    	List<Pencast> list = null;
    	byte[] id = user.getPrimaryKey();
    	
    	try {
    		Query q = sessionFactory.getCurrentSession().createQuery("from UGFile f where f.userProfileKey = " + id);
    		list = q.list();
    	}
    	catch (RuntimeException re) {
            log.error(method + "Failed.", re);
    	}    	
    	return list;
    }
}


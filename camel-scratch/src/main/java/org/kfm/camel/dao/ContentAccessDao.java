/**
 * Created:  Dec 23, 2014 11:58:20 AM
 */
package org.kfm.camel.dao;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.kfm.jpa.ContentAccess;
import org.kfm.jpa.Document;
import org.kfm.jpa.Page;
import org.kfm.jpa.Session;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ContentAccessDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	/**
	 * <p></p>
	 * 
	 */
	public ContentAccessDao() {
	}

	public void delete(ContentAccess contentAccess) {
		
		EntityManager em = entityManagerFactory.createEntityManager();
		em.remove(contentAccess);
		em.flush();
	}
	
	public ContentAccess find(String uid, Document document, Page page, long enUserId) {
		
		EntityManager em = entityManagerFactory.createEntityManager();
		Query q = em.createQuery("select x from ContentAccess x where x.uid = :uid and x.document = :document and x.page = :page and x.enUserId = :enUserId");
		q.setParameter("uid", uid);
		q.setParameter("document", document);
		q.setParameter("page", page);
		q.setParameter("enUserId", enUserId);
		
		ContentAccess result = (ContentAccess)q.getSingleResult();
		
		return result;
	}
	
	public ContentAccess find(String uid, Document document, Session session, long enUserId) {
		
		EntityManager em = entityManagerFactory.createEntityManager();
		Query q = em.createQuery("select x from ContentAccess x where x.uid = :uid and x.document = :document and x.session = :session and x.enUserId = :enUserId");
		q.setParameter("uid", uid);
		q.setParameter("document", document);
		q.setParameter("session", session);
		q.setParameter("enUserId", enUserId);
		
		ContentAccess result = (ContentAccess)q.getSingleResult();
		
		return result;
	}

	public void save(ContentAccess contentAccess) throws EntityExistsException {
		
		EntityManager em = entityManagerFactory.createEntityManager();
		em.persist(contentAccess);
		
		return;
	}
}

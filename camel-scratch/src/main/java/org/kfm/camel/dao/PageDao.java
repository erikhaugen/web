/**
 * Created:  Dec 5, 2014 4:38:31 PM
 */
package org.kfm.camel.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.kfm.jpa.Document;
import org.kfm.jpa.Page;
import org.kfm.jpa.Session;
import org.kfm.jpa.TimeMap;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PageDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	/**
	 * <p></p>
	 * 
	 */
	public PageDao() {
	}

	public Page find(Long documentId, Integer pageIndex) {
		
		EntityManager em = entityManagerFactory.createEntityManager();
		Query q = em.createQuery("select x from Page x where x.documentId = :documentId and x.pageIndex = :pageIndex");
		q.setParameter("documentId", documentId);
		q.setParameter("pageIndex", pageIndex);
		
		Page page = (Page)q.getResultList();
		
		if (page != null) {
			logger.debug("find(docId, pageIndex) - Returning Page with EN Note GUID: " + page.getEnNoteGuid());
			List<TimeMap> timeMaps = page.getTimeMaps();
			
			if (timeMaps != null) {
				logger.debug("find(docId, pageIndex) - Page has " + timeMaps.size() + " TimeMap records.");
			}
		} else {
			logger.debug("find(docId, pageIndex) - Page not found with Document ID '" + documentId + "' and page index '" + pageIndex + "'.");
		}

		return page;
	}
}

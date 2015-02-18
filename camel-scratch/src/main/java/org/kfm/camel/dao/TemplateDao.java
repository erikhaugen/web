/**
 * Created:  Dec 5, 2014 4:38:49 PM
 */
package org.kfm.camel.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.kfm.jpa.Document;
import org.kfm.jpa.Template;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class TemplateDao extends AbstractEvernoteDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	/**
	 * <p></p>
	 * 
	 */
	public TemplateDao() {
	}

	public List<Template> findByDocument(Document document) {
		
		String documentId = document.getDocumentId();
		
		EntityManager em = entityManagerFactory.createEntityManager();
		Query q = em.createQuery("select x from Template x where x.documentId = :documentId");
		q.setParameter("documentId", documentId);
		List<Template> templates = (List<Template>)q.getResultList();
		
		return templates;
	}
	
	public Template findByDocumentAndTemplateIndex(Document document, int templateIndex) {
		
		String documentId = document.getDocumentId();
		
		EntityManager em = entityManagerFactory.createEntityManager();
		Query q = em.createQuery("select x from Template x where x.documentId = :documentId and x.templateIndex := templateIndex");
		q.setParameter("documentId", documentId);
		q.setParameter("templateIndex", templateIndex);
		Template template = (Template)q.getSingleResult();
		
		return template;
	}
}

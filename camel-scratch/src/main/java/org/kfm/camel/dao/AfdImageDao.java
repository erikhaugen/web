/**
 * Created:  Dec 19, 2014 11:18:58 AM
 */
package org.kfm.camel.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.kfm.jpa.AfdImage;
import org.kfm.jpa.Document;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class AfdImageDao extends AbstractEvernoteDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	/**
	 * <p></p>
	 * 
	 */
	public AfdImageDao() {
	}

	public AfdImage findByGuidVersionTemplate(String guid, String version, int index) {
		
		EntityManager em = entityManagerFactory.createEntityManager();
		Query q = em.createQuery("select x from AfdImage x where x.afdGuid = :guid and x.afdVersion := version and x.templateIndex := index");
		q.setParameter("guid", guid);
		q.setParameter("version", version);
		q.setParameter("index", index);
		AfdImage afdImage = (AfdImage)q.getSingleResult();
		return afdImage;
	}
}

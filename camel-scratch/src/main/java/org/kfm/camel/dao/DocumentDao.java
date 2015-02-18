/**
 * Created:  Nov 20, 2014 8:26:02 PM
 */
package org.kfm.camel.dao;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.kfm.camel.response.AuthorizationResponse;
import org.kfm.jpa.Document;
import org.kfm.jpa.Session;
import org.springframework.beans.factory.annotation.Autowired;

import com.livescribe.afp.Afd;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class DocumentDao {

	private Logger logger = Logger.getLogger(this.getClass().getName());

//	@Autowired
//	private CamelContext context;
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
//	private EntityManager entityManager;
	
	/**
	 * <p></p>
	 * 
	 */
	public DocumentDao() {
	}

	public List<Document> findByAuthorizationResponse(AuthorizationResponse response) {
		
		String uid = response.getUid();
		
		logger.debug("findByAuthorizationResponse() - uid = " + uid);
//		JpaTemplate jpaTemplate = context.getEndpoint("jpa:" + Document.class.getName(), JpaEndpoint.class).getTemplate();
//		List<Document> documents = jpaTemplate.find("select x from com.domain.MyEntity e where e.processDate < ?1", new Date());

		EntityManager em = entityManagerFactory.createEntityManager();
		Query q = em.createQuery("select x from Document x where x.uid = :uid");
		q.setParameter("uid", uid);
		List<Document> list = (List<Document>)q.getResultList();
		if (list != null) {
			logger.debug("findByAuthorizationResponse() - Returning list with " + list.size() + " items.");
			for (Document doc : list) {
				List<Session> sessions = doc.getSessions();
				if ((sessions != null) && (!sessions.isEmpty())) {
					logger.debug("findByAuthorizationResponse() - Document has " + sessions.size() + " sessions.");
				} else {
					logger.debug("findByAuthorizationResponse() - Document has no sessions.");
				}
			}
		} else {
			logger.debug("findByAuthorizationResponse() - Returning 'null' list");
		}
		return list;
	}
	
	public List<Document> findByAfd(Exchange exchange, Afd afd) {

		logger.debug("findByAfd() - Called.");
		
		if (afd == null) {
			logger.warn("findByAfd() - The provided Afd object was 'null'.  Returning 'null'.");
			return null;
		}
		String uid = exchange.getIn().getHeader("uid", String.class);
		Long enUserId = exchange.getIn().getHeader("enUserId", Long.class);
		String penDisplayId = exchange.getIn().getHeader("displayId", String.class);
		
		String guid = afd.getGuid();
		int copy = afd.getCopy();
		
		logger.debug("findByAfd() - Parameters: [" + uid + " | " + enUserId + " |" + penDisplayId + " | " + guid + " | " + copy + "]    [ uid | enUserId | penDisplayId | guid | copy ]");
		
		EntityManager em = entityManagerFactory.createEntityManager();
		Query q = em.createQuery("select x from Document x where x.uid = :uid and x.enUserId = :enUserId and x.penDisplayId = :penDisplayId and x.guid = :guid and x.copy = :copy and x.archive = 0");
		q.setParameter("uid", uid);
		q.setParameter("enUserId", enUserId);
		q.setParameter("penDisplayId", penDisplayId);
		q.setParameter("guid", guid);
		q.setParameter("copy", copy);
		
		List<Document> list = (List<Document>)q.getResultList();
		
		if (list != null) {
			logger.debug("findByAfd() - Found " + list.size() + " Document records.");

			if (list.size() == 0) {
				exchange.getIn().setHeader("isNewDocument", "true");
				logger.debug("findByAfd() - Returning empty list");
			} else {
				for (Document doc : list) {
					List<Session> sessions = doc.getSessions();
					if ((sessions != null) && (!sessions.isEmpty())) {
						logger.debug("findByAfd() - Document has " + sessions.size() + " sessions.");
					} else {
						logger.debug("findByAfd() - Document has no sessions.");
					}
				}
				exchange.getIn().setHeader("isNewDocument", "false");
			}
		} else {
			exchange.getIn().setHeader("isNewDocument", "true");
			logger.debug("findByAfd() - Returning 'null' list");
		}
		
		return list;
	}
	
	public List<Document> findByUser(Exchange exchange, Afd afd) {
		
		String method = "findByUser()";
		
		if (afd == null) {
			logger.warn(method + " - The provided Afd object was 'null'.  Returning 'null'.");
			return null;
		}

		String uid = exchange.getIn().getHeader("uid", String.class);
		Long enUserId = exchange.getIn().getHeader("enUserId", Long.class);
		String penDisplayId = exchange.getIn().getHeader("displayId", String.class);

		String guid = afd.getGuid();
		int copy = afd.getCopy();

		EntityManager em = entityManagerFactory.createEntityManager();
		Query q = em.createQuery("select x from Document x where x.uid = :uid and x.enUserId = :enUserId and x.penDisplayId = :penDisplayId and x.archive = 0");
		q.setParameter("uid", uid);
		q.setParameter("enUserId", enUserId);
		q.setParameter("penDisplayId", penDisplayId);
		
		List<Document> list = (List<Document>)q.getResultList();
		
		if (list != null) {
			logger.debug(method + " - Found " + list.size() + " documents.");
			
			//	Determine if any match the given Afd.  If none match, set the 
			//	isNewDocument header to 'true'.
			boolean found = false;
			for (Document doc : list) {
				String docGuid = doc.getGuid();
				int docCopy = doc.getCopy().intValue();
				if ((docGuid.equals(guid)) && (docCopy == copy)) {
					found = true;
					break;
				}
			}
			
			if (found) {
				exchange.getIn().setHeader("isNewDocument", true);
			} else {
				exchange.getIn().setHeader("isNewDocument", false);
			}
		} else {
			exchange.getIn().setHeader("isNewDocument", true);
		}
		return list;
	}
}

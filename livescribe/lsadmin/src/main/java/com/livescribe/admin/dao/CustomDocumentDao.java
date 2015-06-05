/**
 * 
 */
package com.livescribe.admin.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.livescribe.framework.orm.lsevernotedb.Document;
import com.livescribe.framework.orm.lsevernotedb.DocumentDao;

/**
 * <p>Provides additional methods to locate and return <code>Document</code>s.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CustomDocumentDao extends DocumentDao {

	/**
	 * <p>Default class constructor.</p>
	 *
	 */
	public CustomDocumentDao() {
		super();
	}

	/**
	 * <p>Returns a list of documents sync&apos;d by the pen with given display ID.</p>
	 * 
	 * @param displayId A pen&apos;s display ID  (e.g.  AYE-XXX-XXX-XX)
	 * 
	 * @return a <code>List</code> of <code>Document</code>s.
	 */
	public List<Document> findByDisplayId(String displayId) {
		
//		Query q = this.sessionFactoryEvernote.getCurrentSession().createQuery("from Document d where d.penSerial = :displayId order by d.lastModified desc");
		Criteria crit = this.sessionFactoryEvernote.getCurrentSession().createCriteria(Document.class).add(Restrictions.eq("penSerial", displayId)).addOrder(Order.desc("lastModified"));
		List<Document> list = crit.list();
//		q.setString("displayId", displayId);
		
//		List<Document> list = q.list();
		
		return list;
	}
	
	/**
     * <p>Returns a list of documents sync&apos;d by the pen with given display ID.</p>
     * 
     * @param displayId A pen&apos;s display ID  (e.g.  AYE-XXX-XXX-XX)
     * 
     * @return a <code>List</code> of <code>Document</code>s.
     */
    public List<Document> findByUIDAndDisplayId(String uid, String displayId) {
        
//      Query q = this.sessionFactoryEvernote.getCurrentSession().createQuery("from Document d where d.penSerial = :displayId order by d.lastModified desc");
        Criteria crit = this.sessionFactoryEvernote.getCurrentSession()
                .createCriteria(Document.class)
                .add(Restrictions.eq("penSerial", displayId))
                .add(Restrictions.eq("user", uid))
                .addOrder(Order.desc("lastModified"));
        List<Document> list = crit.list();
//      q.setString("displayId", displayId);
        
//      List<Document> list = q.list();
        
        return list;
    }
	
	/**
     * <p>Returns a list of documents sync&apos;d by the pen with given display ID, by the given user and using the given EN account.</p>
     * 
     * @param displayId A pen&apos;s display ID  (e.g.  AYE-XXX-XXX-XX)
     * @param uid uid if the user
     * @param enUserId Evernote user id of the user
     * @return a <code>List</code> of <code>Document</code>s.
     */
    public List<Document> findByDisplayIdUidAndEnUserId(String displayId, String uid, Long enUserId) {
        Criteria crit = this.sessionFactoryEvernote.getCurrentSession()
                .createCriteria(Document.class)
                .add(Restrictions.eq("penSerial", displayId))
                .add(Restrictions.eq("user", uid))
                .add(Restrictions.eq("enUserId", enUserId))
                .addOrder(Order.desc("lastModified"));
        @SuppressWarnings("unchecked")
		List<Document> list = crit.list();
        return list;
    }
	
	/**
     * <p>Returns a list of documents sync&apos;d by the given user and using the given EN account.</p>
     * 
     * @param uid uid if the user
     * @param enUserId Evernote user id of the user
     * @return a <code>List</code> of <code>Document</code>s.
     */
    public List<Document> findByUidAndEnUserId(String uid, Long enUserId) {
        Criteria crit = this.sessionFactoryEvernote.getCurrentSession()
                .createCriteria(Document.class)
                .add(Restrictions.eq("user", uid))
                .add(Restrictions.eq("enUserId", enUserId))
                .addOrder(Order.desc("lastModified"));
        @SuppressWarnings("unchecked")
		List<Document> list = crit.list();
        return list;
    }
	
	/**
	 * <p>Returns a list of documents sync&apos;d by the user with given UID.</p>
	 * 
	 * @param uid A user&apos;s unique ID.  (e.g.  78fd4ff9-eda5-4364-a395-ebf35188d664)
	 * 
	 * @return a <code>List</code> of <code>Document</code>s.
	 */
	public List<Document> findByUid(String uid) {
		
		Query q = this.sessionFactoryEvernote.getCurrentSession().createQuery("from Document d where d.user = :uid");
		q.setString("uid", uid);
		
		List<Document> list = q.list();
		
		return list;
	}
}

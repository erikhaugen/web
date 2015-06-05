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
import com.livescribe.framework.orm.lsevernotedb.Page;
import com.livescribe.framework.orm.lsevernotedb.PageDao;
import com.livescribe.framework.orm.lsevernotedb.Session;

/**
 * <p>
 * Provides additional methods to locate and return <code>Document</code>s.
 * </p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CustomSessionDao extends PageDao {

    /**
     * <p>
     * Default class constructor.
     * </p>
     * 
     */
    public CustomSessionDao() {
        super();
    }

    /**
     * <p>
     * Returns a list of sessions of a document sync&apos;d by the pen with given documentID.
     * </p>
     * 
     * @param documentID
     * 
     * @return a <code>List</code> of <code>Session</code>s.
     */
    public List<Session> findSessionsOfSyncDocumentByPen(long documentId) {

        Criteria crit = this.sessionFactoryEvernote.getCurrentSession()
                .createCriteria(Session.class)
                .add(Restrictions.eq("documentId", documentId))
                .addOrder(Order.desc("lastModified"));
        List<Session> list = crit.list();

        return list;
    }
}

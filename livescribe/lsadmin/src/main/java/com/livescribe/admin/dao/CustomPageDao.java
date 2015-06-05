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
public class CustomPageDao extends PageDao {

    /**
     * <p>
     * Default class constructor.
     * </p>
     * 
     */
    public CustomPageDao() {
        super();
    }

    /**
     * <p>
     * Returns a list of pages of a document sync&apos;d by the pen with given documentID.
     * </p>
     * 
     * @param documentID
     * 
     * @return a <code>List</code> of <code>Page</code>s.
     */
    public List<Page> findPagesOfSyncDocumentByPen(long documentId) {

        Criteria crit = this.sessionFactoryEvernote.getCurrentSession()
                .createCriteria(Page.class)
                .add(Restrictions.eq("documentId", documentId))
                .addOrder(Order.desc("lastModified"));
        List<Page> list = crit.list();

        return list;
    }

}

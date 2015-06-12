package org.kfm.jpa;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-12-18T17:45:11.211-0800")
@StaticMetamodel(Document.class)
public class Document_ {
	public static volatile SingularAttribute<Document, String> documentId;
	public static volatile SingularAttribute<Document, BigInteger> archive;
	public static volatile SingularAttribute<Document, BigInteger> copy;
	public static volatile SingularAttribute<Document, Date> created;
	public static volatile SingularAttribute<Document, String> docName;
	public static volatile SingularAttribute<Document, String> enNotebookGuid;
	public static volatile SingularAttribute<Document, BigInteger> enUserId;
	public static volatile SingularAttribute<Document, String> guid;
	public static volatile SingularAttribute<Document, Date> lastModified;
	public static volatile SingularAttribute<Document, String> lastModifiedBy;
	public static volatile SingularAttribute<Document, String> penDisplayId;
	public static volatile SingularAttribute<Document, String> uid;
	public static volatile ListAttribute<Document, Archive> archives;
	public static volatile ListAttribute<Document, AudioError> audioErrors;
	public static volatile ListAttribute<Document, ContentAccess> contentAccesses;
	public static volatile SingularAttribute<Document, SyncConfig> syncConfig;
	public static volatile ListAttribute<Document, Page> pages;
	public static volatile ListAttribute<Document, Session> sessions;
	public static volatile ListAttribute<Document, SyncTimesInfo> syncTimesInfos;
	public static volatile ListAttribute<Document, Template> templates;
}

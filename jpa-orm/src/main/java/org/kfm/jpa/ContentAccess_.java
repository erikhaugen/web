package org.kfm.jpa;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-17T18:44:49.028-0800")
@StaticMetamodel(ContentAccess.class)
public class ContentAccess_ {
	public static volatile SingularAttribute<ContentAccess, String> cakId;
	public static volatile SingularAttribute<ContentAccess, Date> created;
	public static volatile SingularAttribute<ContentAccess, BigInteger> enUserId;
	public static volatile SingularAttribute<ContentAccess, Date> lastModified;
	public static volatile SingularAttribute<ContentAccess, String> lastModifiedBy;
	public static volatile SingularAttribute<ContentAccess, String> otherData;
	public static volatile SingularAttribute<ContentAccess, String> uid;
	public static volatile SingularAttribute<ContentAccess, Document> document;
	public static volatile SingularAttribute<ContentAccess, Page> page;
	public static volatile SingularAttribute<ContentAccess, Session> session;
	public static volatile SingularAttribute<ContentAccess, Template> template;
}

package org.kfm.jpa;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-17T18:44:46.826-0800")
@StaticMetamodel(Archive.class)
public class Archive_ {
	public static volatile SingularAttribute<Archive, String> archiveId;
	public static volatile SingularAttribute<Archive, String> archivedNotebookName;
	public static volatile SingularAttribute<Archive, BigInteger> authorizationId;
	public static volatile SingularAttribute<Archive, String> comment;
	public static volatile SingularAttribute<Archive, Date> created;
	public static volatile SingularAttribute<Archive, Date> dateDeletedByUser;
	public static volatile SingularAttribute<Archive, BigInteger> endTime;
	public static volatile SingularAttribute<Archive, Date> lastModified;
	public static volatile SingularAttribute<Archive, String> lastModifiedBy;
	public static volatile SingularAttribute<Archive, BigInteger> startTime;
	public static volatile SingularAttribute<Archive, Document> document;
	public static volatile SingularAttribute<Archive, SyncTimesInfo> syncTimesInfo;
}

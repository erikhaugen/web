package org.kfm.jpa;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-17T18:44:46.927-0800")
@StaticMetamodel(SyncTimesInfo.class)
public class SyncTimesInfo_ {
	public static volatile SingularAttribute<SyncTimesInfo, String> syncTimesInfoId;
	public static volatile SingularAttribute<SyncTimesInfo, Date> created;
	public static volatile SingularAttribute<SyncTimesInfo, String> docName;
	public static volatile SingularAttribute<SyncTimesInfo, BigInteger> endTime;
	public static volatile SingularAttribute<SyncTimesInfo, String> guid;
	public static volatile SingularAttribute<SyncTimesInfo, BigInteger> inProgressTime;
	public static volatile SingularAttribute<SyncTimesInfo, Date> lastModified;
	public static volatile SingularAttribute<SyncTimesInfo, String> lastModifiedBy;
	public static volatile SingularAttribute<SyncTimesInfo, String> penDisplayId;
	public static volatile SingularAttribute<SyncTimesInfo, BigInteger> startTime;
	public static volatile SingularAttribute<SyncTimesInfo, String> user;
	public static volatile ListAttribute<SyncTimesInfo, Archive> archives;
	public static volatile SingularAttribute<SyncTimesInfo, Document> document;
}

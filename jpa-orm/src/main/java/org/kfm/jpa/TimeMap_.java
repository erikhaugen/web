package org.kfm.jpa;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-17T18:44:46.931-0800")
@StaticMetamodel(TimeMap.class)
public class TimeMap_ {
	public static volatile SingularAttribute<TimeMap, String> timeMapId;
	public static volatile SingularAttribute<TimeMap, Date> created;
	public static volatile SingularAttribute<TimeMap, BigInteger> endTime;
	public static volatile SingularAttribute<TimeMap, Date> lastModified;
	public static volatile SingularAttribute<TimeMap, String> lastModifiedBy;
	public static volatile SingularAttribute<TimeMap, BigInteger> mapTime;
	public static volatile SingularAttribute<TimeMap, BigInteger> startTime;
	public static volatile SingularAttribute<TimeMap, Page> page;
	public static volatile SingularAttribute<TimeMap, Session> session;
}

package org.kfm.jpa;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-17T18:44:46.911-0800")
@StaticMetamodel(Session.class)
public class Session_ {
	public static volatile SingularAttribute<Session, String> sessionId;
	public static volatile SingularAttribute<Session, Date> created;
	public static volatile SingularAttribute<Session, String> enHashLsLogoResource;
	public static volatile SingularAttribute<Session, String> enHashLsUiSetResource;
	public static volatile SingularAttribute<Session, BigInteger> endTime;
	public static volatile SingularAttribute<Session, String> evernoteGuidNote;
	public static volatile SingularAttribute<Session, Date> lastModified;
	public static volatile SingularAttribute<Session, String> lastModifiedBy;
	public static volatile SingularAttribute<Session, BigInteger> startTime;
	public static volatile ListAttribute<Session, Audio> audios;
	public static volatile ListAttribute<Session, AudioError> audioErrors;
	public static volatile ListAttribute<Session, ContentAccess> contentAccesses;
	public static volatile SingularAttribute<Session, Document> document;
	public static volatile ListAttribute<Session, TimeMap> timeMaps;
}

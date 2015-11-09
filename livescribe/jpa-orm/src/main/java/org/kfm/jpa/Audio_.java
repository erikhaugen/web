package org.kfm.jpa;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-17T18:44:46.828-0800")
@StaticMetamodel(Audio.class)
public class Audio_ {
	public static volatile SingularAttribute<Audio, String> audioId;
	public static volatile SingularAttribute<Audio, Date> created;
	public static volatile SingularAttribute<Audio, String> enAudioResourceGuid;
	public static volatile SingularAttribute<Audio, String> enAudioResourceHash;
	public static volatile SingularAttribute<Audio, BigInteger> endTime;
	public static volatile SingularAttribute<Audio, Date> lastModified;
	public static volatile SingularAttribute<Audio, String> lastModifiedBy;
	public static volatile SingularAttribute<Audio, BigInteger> startTime;
	public static volatile SingularAttribute<Audio, Session> session;
}

package org.kfm.jpa;

import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-17T18:44:46.831-0800")
@StaticMetamodel(AudioError.class)
public class AudioError_ {
	public static volatile SingularAttribute<AudioError, String> audioErrorId;
	public static volatile SingularAttribute<AudioError, Integer> aacSize;
	public static volatile SingularAttribute<AudioError, Timestamp> created;
	public static volatile SingularAttribute<AudioError, Timestamp> errorDate;
	public static volatile SingularAttribute<AudioError, String> fileSystemPath;
	public static volatile SingularAttribute<AudioError, Timestamp> lastModified;
	public static volatile SingularAttribute<AudioError, String> lastModifiedBy;
	public static volatile SingularAttribute<AudioError, String> penDisplayId;
	public static volatile SingularAttribute<AudioError, String> uid;
	public static volatile SingularAttribute<AudioError, Document> document;
	public static volatile SingularAttribute<AudioError, Session> session;
}

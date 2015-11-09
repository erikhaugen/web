package org.kfm.jpa;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-17T18:44:46.925-0800")
@StaticMetamodel(SyncConfig.class)
public class SyncConfig_ {
	public static volatile SingularAttribute<SyncConfig, String> syncConfigId;
	public static volatile SingularAttribute<SyncConfig, Date> created;
	public static volatile SingularAttribute<SyncConfig, Date> lastModified;
	public static volatile SingularAttribute<SyncConfig, String> lastModifiedBy;
	public static volatile SingularAttribute<SyncConfig, String> lspName;
	public static volatile SingularAttribute<SyncConfig, String> lspParams;
	public static volatile SingularAttribute<SyncConfig, String> lspUrl;
	public static volatile ListAttribute<SyncConfig, Document> documents;
}

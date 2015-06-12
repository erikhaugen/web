package org.kfm.jpa;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-12-18T17:35:17.367-0800")
@StaticMetamodel(Page.class)
public class Page_ {
	public static volatile SingularAttribute<Page, String> pageId;
	public static volatile SingularAttribute<Page, Date> created;
	public static volatile SingularAttribute<Page, String> enImageResourceGuid;
	public static volatile SingularAttribute<Page, String> enImageResourceHash;
	public static volatile SingularAttribute<Page, String> enLsLogoResourceHash;
	public static volatile SingularAttribute<Page, String> enLsUiSetResourceHash;
	public static volatile SingularAttribute<Page, String> enNoteGuid;
	public static volatile SingularAttribute<Page, String> enStrokeResourceGuid;
	public static volatile SingularAttribute<Page, String> enStrokeResourceHash;
	public static volatile SingularAttribute<Page, BigInteger> endTime;
	public static volatile SingularAttribute<Page, String> label;
	public static volatile SingularAttribute<Page, Date> lastModified;
	public static volatile SingularAttribute<Page, String> lastModifiedBy;
	public static volatile SingularAttribute<Page, Integer> pageIndex;
	public static volatile SingularAttribute<Page, BigInteger> startTime;
	public static volatile SingularAttribute<Page, BigInteger> updateCounter;
	public static volatile ListAttribute<Page, ContentAccess> contentAccesses;
	public static volatile SingularAttribute<Page, AfdImage> afdImage;
	public static volatile SingularAttribute<Page, Document> document;
	public static volatile SingularAttribute<Page, Template> template;
	public static volatile ListAttribute<Page, TimeMap> timeMaps;
}

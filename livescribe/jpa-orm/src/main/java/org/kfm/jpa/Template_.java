package org.kfm.jpa;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-17T18:44:46.929-0800")
@StaticMetamodel(Template.class)
public class Template_ {
	public static volatile SingularAttribute<Template, String> templateId;
	public static volatile SingularAttribute<Template, Date> created;
	public static volatile SingularAttribute<Template, Double> height;
	public static volatile SingularAttribute<Template, Date> lastModified;
	public static volatile SingularAttribute<Template, String> lastModifiedBy;
	public static volatile SingularAttribute<Template, Integer> templateIndex;
	public static volatile SingularAttribute<Template, Double> width;
	public static volatile SingularAttribute<Template, Double> x;
	public static volatile SingularAttribute<Template, Double> y;
	public static volatile ListAttribute<Template, ContentAccess> contentAccesses;
	public static volatile ListAttribute<Template, Image> images;
	public static volatile ListAttribute<Template, Page> pages;
	public static volatile SingularAttribute<Template, Document> document;
}

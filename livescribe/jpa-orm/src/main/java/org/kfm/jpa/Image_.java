package org.kfm.jpa;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-17T18:44:46.850-0800")
@StaticMetamodel(Image.class)
public class Image_ {
	public static volatile SingularAttribute<Image, String> imageId;
	public static volatile SingularAttribute<Image, Date> created;
	public static volatile SingularAttribute<Image, byte[]> data;
	public static volatile SingularAttribute<Image, Double> height;
	public static volatile SingularAttribute<Image, Date> lastModified;
	public static volatile SingularAttribute<Image, String> lastModifiedBy;
	public static volatile SingularAttribute<Image, String> mimeType;
	public static volatile SingularAttribute<Image, Double> width;
	public static volatile SingularAttribute<Image, Double> x;
	public static volatile SingularAttribute<Image, Double> y;
	public static volatile SingularAttribute<Image, Double> z;
	public static volatile SingularAttribute<Image, Template> template;
}

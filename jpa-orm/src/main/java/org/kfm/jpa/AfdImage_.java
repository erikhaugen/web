package org.kfm.jpa;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-17T18:44:46.822-0800")
@StaticMetamodel(AfdImage.class)
public class AfdImage_ {
	public static volatile SingularAttribute<AfdImage, String> imageId;
	public static volatile SingularAttribute<AfdImage, String> afdGuid;
	public static volatile SingularAttribute<AfdImage, String> afdVersion;
	public static volatile SingularAttribute<AfdImage, Date> created;
	public static volatile SingularAttribute<AfdImage, byte[]> data;
	public static volatile SingularAttribute<AfdImage, Double> height;
	public static volatile SingularAttribute<AfdImage, Date> lastModified;
	public static volatile SingularAttribute<AfdImage, String> lastModifiedBy;
	public static volatile SingularAttribute<AfdImage, String> mimeType;
	public static volatile SingularAttribute<AfdImage, BigInteger> templateIndex;
	public static volatile SingularAttribute<AfdImage, Double> width;
	public static volatile ListAttribute<AfdImage, Page> pages;
}

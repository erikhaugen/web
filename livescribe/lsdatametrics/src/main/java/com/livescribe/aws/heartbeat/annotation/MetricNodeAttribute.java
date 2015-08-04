package com.livescribe.aws.heartbeat.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MetricNodeAttribute {
	/* attribute name of <metric> node */
	String name();
	
	/* java data type when mapping to object */
	String dataType();
}

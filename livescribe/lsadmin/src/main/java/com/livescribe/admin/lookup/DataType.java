package com.livescribe.admin.lookup;

public enum DataType {
	LONG		("Long", 	java.lang.Long.class),
	STRING		("String",	java.lang.String.class);
	
	private String name;
	private Class dataType;
	
	DataType(String name, Class dataType) {
		this.name = name;
		this.dataType = dataType;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Class getDataType() {
		return this.dataType;
	}
}

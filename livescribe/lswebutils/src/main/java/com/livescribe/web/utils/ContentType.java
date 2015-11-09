package com.livescribe.web.utils;

public enum ContentType {
	IMAGE("image/"),
	PENCAST("application/octet-stream");
	
	private String typeStr;
	private ContentType(String typeStr) {
		this.typeStr = typeStr;
	}
	
	public String getTypeString() {
		return this.typeStr;
	}
}

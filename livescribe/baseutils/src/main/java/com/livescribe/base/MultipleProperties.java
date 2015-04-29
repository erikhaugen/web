package com.livescribe.base;

import java.util.List;
import java.util.Properties;

public class MultipleProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MultipleProperties() {
	}
	
	public void setPropertiesList(List<Properties> propertiesList) {
		for ( Properties properties : propertiesList ) {
			this.putAll(properties);
		}
	}
	
	public void setProperties(Properties properties) {
		this.putAll(properties);
	}
}

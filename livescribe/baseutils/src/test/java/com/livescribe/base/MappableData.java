package com.livescribe.base;

import com.livescribe.base.anno.Mappable;
import com.livescribe.base.anno.MappedElement;

@Mappable
public class MappableData {
	@MappedElement(key = "int")
	private int intValue;
	@MappedElement(key = "str")
	private String strValue;

	public MappableData() {

	}

	public MappableData( int intValue, String strValue) {
		this.intValue = intValue;
		this.strValue = strValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public String getStrValue() {
		return strValue;
	}

}

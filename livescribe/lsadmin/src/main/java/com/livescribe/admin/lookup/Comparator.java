package com.livescribe.admin.lookup;

public enum Comparator {
	
	IS_EQUAL_TO						("isEqualTo", 				" = "	),
	IS_NOT_EQUAL_TO					("isNotEqualTo",			" <> "	),
	IS_GREATER_THAN					("isGreaterThan", 			" > "	),
	IS_GREATER_THAN_OR_EQUAL_TO		("isGreaterThanOrEqualTo",	" >= "	),
	IS_LESS_THAN					("isLessThan",				" < "	),
	IS_LESS_THAN_OR_EQUAL_TO		("isLessThanOrEqualTo",		" <= "	),
	CONTAINS						("contains",				" LIKE ");
	
	private String name;
	private String sqlOperator;
	
	Comparator(String name, String sqlOperator) {
		this.name = name;
		this.sqlOperator = sqlOperator;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getSqlOperator() {
		return this.sqlOperator;
	}
}

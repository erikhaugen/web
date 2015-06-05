/**
 * 
 */
package com.livescribe.admin.lookup;


/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public enum QueryField {

	PRIMARY_EMAIL		("primaryEmail",		DataType.STRING, 	"User",					"u"),
	UID					("uid",					DataType.STRING,	"User",					"u"),
	EN_USER_NAME		("enUsername",			DataType.STRING,	"Authorization",		"a"),
	EN_USER_ID			("enUserId",			DataType.STRING,	"Authorization",		"a"),
    PEN_ID  			("penId",				DataType.STRING,    "RegisteredDevice",     "rd"), // This could either be serialNumber or displayId..
    DISPLAY_ID  		("displayId",			DataType.STRING,    "RegisteredDevice",     "rd"),
    PEN_SERIAL_NUMBER	("penSerialNumber",		DataType.STRING,    "RegisteredDevice",     "rd"),
    PEN_DISPLAY_ID		("penDisplayId",  		DataType.STRING,    "RegisteredDevice",     "rd");;
	
	private final String field;
	
	private DataType dataType;
	
	private String entityName;		// Hibernate ORM entity name
	
	private String entityAlias;
	
	QueryField(String field, DataType dataType, String entityName, String entityAlias) {
		this.field = field;
		this.dataType = dataType;
		this.entityName = entityName;
		this.entityAlias = entityAlias;
	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}
	
	
	public DataType getDataType() {
		return dataType;
	}

	public String getEntityName() {
		return entityName;
	}

	public String getEntityAlias() {
		return entityAlias;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("QueryField[");
		sb.append("field=").append(field);
		sb.append(" dataType=").append(dataType.getName());
		sb.append(" entityName=").append(entityName);
		sb.append(" entityAlias=").append(entityAlias);
		sb.append("]");
		return sb.toString();
	}
}

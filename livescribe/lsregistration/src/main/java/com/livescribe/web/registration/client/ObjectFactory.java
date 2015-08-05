package com.livescribe.web.registration.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.dom4j.Node;

public class ObjectFactory {

	private static Logger logger = Logger.getLogger(ObjectFactory.class.getName());
	
	private static final String DATE_FORMAT		= "yyyy-MM-dd HH:mm:ss";
	
	public static final	String	NODE_RESPONSE_CODE					= "//response/responseCode";
	public static final	String	NODE_ERROR_CODE						= "//response/errorCode";
	public static final	String	NODE_ERROR_MESSAGE					= "//response/message";
	
	public static final	String	NODE_REGISTRATION_APP_ID			= "appId";
	public static final	String	NODE_REGISTRATION_DEVICE_ID			= "deviceId";
	public static final	String	NODE_REGISTRATION_EDITION			= "edition";
	public static final	String	NODE_REGISTRATION_PEN_SERIAL		= "penSerial";
	public static final	String	NODE_REGISTRATION_DISPLAY_ID		= "displayId";
	public static final	String	NODE_REGISTRATION_PEN_NAME			= "penName";
	public static final	String	NODE_REGISTRATION_FIRST_NAME		= "firstName";
	public static final	String	NODE_REGISTRATION_LAST_NAME			= "lastName";
	public static final	String	NODE_REGISTRATION_EMAIL				= "email";
	public static final	String	NODE_REGISTRATION_LOCALE			= "locale";
	public static final	String	NODE_REGISTRATION_COUNTRY			= "country";
	public static final	String	NODE_REGISTRATION_LOC_LAT			= "locLat";
	public static final	String	NODE_REGISTRATION_LOC_LONG			= "locLong";
	public static final	String	NODE_REGISTRATION_OPT_IN			= "optIn";
	public static final	String	NODE_REGISTRATION_CREATED_DATE		= "created";
	public static final	String	NODE_REGISTRATION_DATE				= "registrationDate";


	/**
	 * 
	 * @param root
	 * @param xPath
	 * @return
	 */
	public static String selectSingleNodeText(Node root, String xPath) {
		Node node = root.selectSingleNode(xPath);
		if (node == null) {
			return "";
		}
		
		return node.getText();
	}
	
	/**
	 * 
	 * @param root
	 * @param xPath
	 * @return
	 */
	public static int selectSingleNodeInt(Node root, String xPath) {
		Node node = root.selectSingleNode(xPath);
		if (node == null) {
			return 0;
		}
		
		return Integer.parseInt(node.getText());
	}
	
	/**
	 * 
	 * @param root
	 * @param xPath
	 * @return
	 */
	public static double selectSingleNodeDouble(Node root, String xPath) {
		Node node = root.selectSingleNode(xPath);
		
		if (node == null) {
			return 0.00;
		}
		
		return Double.parseDouble(node.getText());
	}

	
	/**
	 * 
	 * @param root
	 * @param xPath
	 * @return
	 */
	public static boolean selectSingleNodeBoolean(Node root, String xPath) {
		Node node = root.selectSingleNode(xPath);
		
		if (node == null) {
			return false;
		}
		
		return Boolean.parseBoolean(node.getText());
	}
	
	/**
	 * 
	 * @param root
	 * @param xPath
	 * @return
	 */
	public static Date selectSingleNodeDate(Node root, String xPath) {
		Node node = root.selectSingleNode(xPath);
		
		if (node == null) {
			return null;
		}
		
		try {
			return new SimpleDateFormat(DATE_FORMAT).parse(node.getText());
			
		} catch (ParseException e) {
			return null;
		}
	}
}

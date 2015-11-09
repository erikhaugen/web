package com.livescribe.importsecretkeytool;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.livescribe.importsecretkeytool.exception.FailToParseLineException;

public class SecretKeyItem {
	private static Logger log = Logger.getLogger(SecretKeyItem.class);
	private static Pattern secretKeyPattern = Pattern.compile("[0-9A-F]*"); 
	private String penHexId;
	private String penDisplayId;
	private String secretKey;
	
	
	public static SecretKeyItem parse(String str) throws FailToParseLineException{
		String penHexId = null;
		String penDisplayId = null;
		String secretKey = null;
		try {
			String[] strs = str.split(",");
			penHexId = strs[0].trim();
			penDisplayId = strs[1].trim();
			secretKey = strs[2].trim();
			if (secretKey.length() != 256 || !secretKeyPattern.matcher(secretKey).matches())
				throw new IllegalArgumentException("Invalid secret key");
			return new SecretKeyItem(penHexId, penDisplayId, secretKey);
		} catch (RuntimeException e) {
			throw new FailToParseLineException("cannot parse SecretKeyItem: " + e.toString(), e);
		}
	}
	
	public SecretKeyItem(String penHexId, String penDisplayId, String secretKey) throws FailToParseLineException {
		String error = null;
		if (penHexId == null || penHexId.length() == 0)
			error = "penHexId is null/empty";
		else if (penDisplayId == null || penDisplayId.length() == 0)
			error = "penDisplayId is null/empty";
		else if (secretKey == null || secretKey.length() == 0)
			error = "secretKey is null/empty";
		if (error != null)
			throw new FailToParseLineException("cannot parse SecretKeyItem: " + error);
		
		this.penHexId = penHexId;
		this.penDisplayId = penDisplayId;
		this.secretKey = secretKey;
	}
	
	public String getPenHexId() {
		return penHexId;
	}
	public String getPenDisplayId() {
		return penDisplayId;
	}
	public String getSecretKey() {
		return secretKey;
	}
}

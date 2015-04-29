package com.livescribe.base.utils;

import java.io.File;

public class FileUtils {
	
	public static String getLastPathComponent(String filename) {
		String returnValue = filename;
		int index = filename.lastIndexOf(File.separator);
		if (index >=0 ) {
			returnValue = filename.substring(index);
		}
		return returnValue;
	}
}

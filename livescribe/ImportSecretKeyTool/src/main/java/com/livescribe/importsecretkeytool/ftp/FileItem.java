package com.livescribe.importsecretkeytool.ftp;

public class FileItem {
	private String filename;
	private boolean isDirectory;

	public FileItem(String filename, boolean isDirectory) {
		super();
		this.filename = filename;
		this.isDirectory = isDirectory;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public String getFilename() {
		return filename;
	}
}

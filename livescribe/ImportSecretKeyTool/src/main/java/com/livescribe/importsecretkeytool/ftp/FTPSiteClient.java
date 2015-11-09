package com.livescribe.importsecretkeytool.ftp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;

public abstract class FTPSiteClient {
	private boolean logined = false;
	protected String hostname;
	protected int port;
	protected String username;
	protected String password;
	protected String workingDirectory = "/";

	protected abstract void loginImpl() throws SocketException, IOException;

	/**
	 * login ftp site server
	 * 
	 * @param hostname
	 * @param port
	 * @param username
	 * @param password
	 * @throws SocketException
	 * @throws IOException
	 */
	public void login(String hostname, int port, String username,
			String password) throws SocketException, IOException {
		if (logined == true)
			throw new IllegalStateException("Client is in login mode already");

		if (hostname == null)
			throw new IllegalArgumentException("hostname cannot be null");
		if (username == null)
			throw new IllegalArgumentException("username cannot be null");
		if (password == null)
			throw new IllegalArgumentException("password cannot be null");
		if (port <= 0)
			throw new IllegalArgumentException("invalid port=" + port);
		
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
		loginImpl();
		logined = true;
	}

	protected abstract List<FileItem> listFilesImpl(String path)
			throws IOException;

	/**
	 * List all files in <i>path</i>
	 * @param path
	 * @return
	 * @throws FTPClientNotLoginException
	 * @throws IOException
	 */
	public List<FileItem> listFiles(String path)
			throws FTPClientNotLoginException, IOException {
		if (!logined)
			throw new FTPClientNotLoginException();

		return listFilesImpl(path);
	}

	/**
	 * List all files in current working directory
	 * @param path
	 * @return
	 * @throws FTPClientNotLoginException
	 * @throws IOException
	 */
	public List<FileItem> listFiles() throws FTPClientNotLoginException,
			IOException {
		return this.listFiles(".");
	}

	protected abstract void setWorkingDirectoryImpl(String path)
			throws IOException;

	public void setWorkingDirectory(String path)
			throws FTPClientNotLoginException, IOException {
		if (!logined)
			throw new FTPClientNotLoginException();

		if (path == null)
			throw new IllegalArgumentException("path cannot be null");

		setWorkingDirectoryImpl(path);

		this.workingDirectory = path;
	}

	protected abstract void retrieveFileImpl(String fileName, OutputStream out)
			throws IOException;

	public void retrieveFile(String fileName, OutputStream out)
			throws FTPClientNotLoginException, IOException {
		if (!logined)
			throw new FTPClientNotLoginException();

		retrieveFileImpl(fileName, out);
	}

	protected abstract void deleteFileImpl(String fileName) throws IOException;

	public void deleteFile(String fileName) throws FTPClientNotLoginException,
			IOException {
		if (!logined)
			throw new FTPClientNotLoginException();

		deleteFileImpl(fileName);
	}

	protected abstract void logoutImpl() throws IOException;

	public void logout() throws FTPClientNotLoginException, IOException {
		if (!logined)
			throw new FTPClientNotLoginException();

		logoutImpl();
		logined = false;
	}
}

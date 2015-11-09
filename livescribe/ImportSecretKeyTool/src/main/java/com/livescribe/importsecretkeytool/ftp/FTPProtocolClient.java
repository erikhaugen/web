package com.livescribe.importsecretkeytool.ftp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPProtocolClient extends FTPSiteClient {
	private FTPClient client;

	@Override
	protected void loginImpl() throws SocketException, IOException {
		client = new FTPClient();
		client.connect(hostname, port);
		boolean loginOk = client.login(username, password);
		if (!loginOk)
			throw new IOException("Cannot authenticate with server");
	}

	@Override
	protected void logoutImpl() throws IOException {
		boolean logoutOk = client.logout();
		client.disconnect();
		client = null;
		if (!logoutOk)
			throw new IOException("Cannot logout");
	}

	@Override
	protected void setWorkingDirectoryImpl(String path) throws IOException {
		if (!client.changeWorkingDirectory(path))
			throw new IOException("Cannot change directory to " + path
					+ " from current directory " + workingDirectory);
	}

	@Override
	protected void retrieveFileImpl(String fileName, OutputStream out)
			throws IOException {
		client.retrieveFile(fileName, out);
	}

	@Override
	protected void deleteFileImpl(String fileName) throws IOException {
		client.deleteFile(fileName);
	}

	@Override
	protected List<FileItem> listFilesImpl(String path) throws IOException {
		List<FileItem> result = new ArrayList<FileItem>();
		FTPFile[] files = client.listFiles(path);
		for (FTPFile ftpFile : files) {
			if (ftpFile == null)
				continue;
			result.add(new FileItem(ftpFile.getName(), ftpFile.isDirectory()));
		}
		return result;
	}

}

package com.livescribe.importsecretkeytool.ftp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPProtocolClient extends FTPSiteClient {
	private static final JSch jsch = new JSch();
	private Session session = null;
	private ChannelSftp sftpChannel = null;

	@Override
	protected void loginImpl() throws SocketException, IOException {
		try {
			session = jsch.getSession(username, hostname, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect();

			sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();
		} catch (JSchException e) {
			try {
				sftpChannel.exit();
			} catch (Exception e1) {
			}
			try {
				session.disconnect();
			} catch (Exception e1) {
			}
			throw new IOException(e);
		}
	}

	@Override
	protected void logoutImpl() throws IOException {
		sftpChannel.exit();
		sftpChannel = null;
		session.disconnect();
		session = null;
	}

	@Override
	protected void setWorkingDirectoryImpl(String path) throws IOException {
		if (path.endsWith("/"))
			path += "/";
		
		String workingDirectoryTmp;
		if (path.startsWith("/"))
			workingDirectoryTmp = path;
		else
			workingDirectoryTmp = workingDirectory + path;
		
		try {
			sftpChannel.cd(workingDirectoryTmp);
			workingDirectory = workingDirectoryTmp;
		} catch (SftpException e) {
			throw new IOException("Cannot change directory to " + path
					+ " from current directory " + workingDirectory, e);
		}
	}

	@Override
	protected void retrieveFileImpl(String fileName, OutputStream out)
			throws IOException {
		try {
			sftpChannel.get(fileName, out);
		} catch (SftpException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected void deleteFileImpl(String fileName) throws IOException {
		try {
			sftpChannel.rm(fileName);
		} catch (SftpException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected List<FileItem> listFilesImpl(String path) throws IOException {
		List<FileItem> result = new ArrayList<FileItem>();
		try {
			Vector<ChannelSftp.LsEntry> files = sftpChannel.ls(path);
			for (ChannelSftp.LsEntry lsEntry : files) {
				if (lsEntry == null)
					continue;
				result.add(new FileItem(lsEntry.getFilename(), lsEntry.getAttrs().isDir()));
			}
		} catch (SftpException e) {
			throw new IOException(e);
		}
		return result;
	}

}

package com.livescribe.importsecretkeytool.ftp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;

public class FTPSProtocolClient extends FTPProtocolClient {
	private FTPClient client;

	@Override
	protected void loginImpl() throws SocketException, IOException {
		client = new FTPSClient();
		client.connect(hostname, port);
		client.login(username, password);
	}

}

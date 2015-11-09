package com.livescribe.importsecretkeytool.cli;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.livescribe.importsecretkeytool.cli.Configuration.FTPProtocol;

public class Parameters {

	@Parameter(names = "-configFile", description = "Configuration file in java properties file format")
	private String configFile = "conf.properties";

	public Configuration getConfiguration() throws Exception {
		
		Configuration configuration = null;

		// load configuration file if configFile is declared
		if (configFile.length() != 0)
			configuration = Configuration.loadConfiguration(configFile);
		else
			throw new IllegalStateException("Failed to load configuration.");

		
		//checking FPT options
		if (configuration.isFTPEnabled()) {
			if (configuration.getFTPFolderPath() == null ||
					configuration.getFTPFolderPath().length() == 0)
				throw new Exception("Error: " + ProgramParameter.FTP_FOLDER_PATH.getName() + " cannot be empty.");
			
			if (configuration.getFTPHostname() == null ||
					configuration.getFTPHostname().length() == 0)
				throw new Exception("Error: " + ProgramParameter.FTP_HOSTNAME.getName() + " cannot be empty.");
			
			//checking ftp port
			try {
				configuration.getFTPPort();
			} catch (Exception e) {
				throw new Exception("Cannot parse FTP server's port");
			}
			
			//checking ftp protocol
			try {
				FTPProtocol protocol = configuration.getFTPProtocol();
//				if (protocol == FTPProtocol.SFTP)
//					throw new RuntimeException(FTPProtocol.SFTP + " is currently not supported.");
			} catch (RuntimeException e){
				throw new Exception(e.getMessage());
			} catch (Exception e) {
				throw new Exception("Cannot parse FTP protocol");
			}
		}
		
		//checking folders' existence
		File checkFile = configuration.getMonitoredPath();
		if (!checkFile.exists())
			throw new Exception("Error: " + checkFile.getAbsolutePath()
					+ " doesn't exist.");
		else if (!checkFile.isDirectory())
			throw new Exception("Error: " + checkFile.getAbsolutePath()
					+ " must be a directory.");
		
		checkFile = configuration.getErrorStorePath();
		if (!checkFile.exists())
			throw new Exception("Error: " + checkFile.getAbsolutePath()
					+ " doesn't exist.");
		else if (!checkFile.isDirectory())
			throw new Exception("Error: " + checkFile.getAbsolutePath()
					+ " must be a directory.");
		
		checkFile = configuration.getProcessedStorePath();
		if (!checkFile.exists())
			throw new Exception("Error: " + checkFile.getAbsolutePath()
					+ " doesn't exist.");
		else if (!checkFile.isDirectory())
			throw new Exception("Error: " + checkFile.getAbsolutePath()
					+ " must be a directory.");

		return configuration;
	}
}

package com.livescribe.importsecretkeytool;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.livescribe.importsecretkeytool.cli.Configuration;
import com.livescribe.importsecretkeytool.cli.Parameters;
import com.livescribe.importsecretkeytool.exception.ProcessingException;
import com.livescribe.importsecretkeytool.ftp.FTPClientNotLoginException;
import com.livescribe.importsecretkeytool.ftp.FTPProtocolClient;
import com.livescribe.importsecretkeytool.ftp.FTPSProtocolClient;
import com.livescribe.importsecretkeytool.ftp.FTPSiteClient;
import com.livescribe.importsecretkeytool.ftp.FileItem;
import com.livescribe.importsecretkeytool.ftp.SFTPProtocolClient;

public class Client {
	private static Logger log = Logger.getLogger(Client.class);
	static int QUERY_LIMIT = 100;
	private static Configuration conf;

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			IOException, Exception {

		String OSName = System.getProperty("os.name");
		if (OSName == null)
			log.warn("Cannot determine OS type. Assume it is unix-base. This tool only works properly on unix system.");
		OSName = OSName.toLowerCase();
		if (!(OSName.contains("mac") || OSName.contains("nix")|| OSName.contains("nux"))) {
			log.error("This tool only works properly on unix system.");
			return;
		}
		
		URLClassLoader cl = (URLClassLoader) new AppLauncher().getClass().getClassLoader().getClass().getClassLoader();
		try {
			URL url = cl.findResource("META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(url.openStream());
			Attributes entries = manifest.getMainAttributes();
			log.info("======================================================================");
			log.info("Import Secret Key Tool's Jenkin build #: " + entries.getValue("Hudson-Build-Number"));
			log.info("Timestamp: " + new Date());
		} catch (Exception E) {
		  	log.warn("Warning: cannot determine build information");
		}
		

		// process parameters, make configuration for signing task
		Parameters parameters = new Parameters();
		JCommander paramParser = null;
		conf = null;
		try {
			paramParser = new JCommander(parameters);
			paramParser.parse(args);
			conf = parameters.getConfiguration();
		} catch (Exception e) {
			log.error("Invalid parameters: " + e.toString());
			log.error("");
			paramParser.usage();
			System.exit(-2);
		}

		// doing part
		try {
			if (conf != null) {
				//print conf file name
				log.info("Using config file: " + conf.getConfigName());
				
				//check SMTP configuration
				if (conf.isMailEnabled() && conf.isCheckMailConfigurationOnStartUp()) {
					log.info("Sending start email to check SMTP configuration.");
					boolean OK = Utils.sendEmail(conf.getMailSender(), conf.getEmails(),
							"Start import secret keys at " + new Date(),
							"SMTP server works fine.");
					if (!OK)
						throw new ProcessingException("SMTP configuration might be incorrect.");
				}
				
				//initiate file's filter
				FileFilter filter = new FileFilter() {
					public boolean accept(File pathname) {
						Matcher matcher = conf.getMonitoredFilePattern().matcher(pathname.getName());
						return matcher.matches();
					}
				};
				
				//get files from FTP Sites
				if (conf.isFTPEnabled()) {
					log.info("");log.info("");
					log.info("------------Searching on FTP site------------");
					log.info("");
					//checking emptyness of monitored Folder
					File[] monitoredFiles = conf.getMonitoredPath().listFiles(filter);
					if (monitoredFiles.length != 0)
						throw new ProcessingException("There is unprocessed files in " + conf.getMonitoredPath().getAbsolutePath()
								+ ". Please process these files before running with ftp.enabled=true.");
					
					//move file from FTP to local
					FTPSiteClient client = null;
					switch (conf.getFTPProtocol()) {
					case FTP : client = new FTPProtocolClient();break;
					case SFTP: client = new SFTPProtocolClient();break;
					case FTPS: client = new FTPSProtocolClient();break;
					}
					try {
						log.info("Login into ftp site "
								+ conf.getFTPProtocol().toString()
										.toLowerCase() + "://"
								+ conf.getFTPHostname());
						client.login(conf.getFTPHostname(), conf.getFTPPort(),
								conf.getFTPUsername(), conf.getFTPPassword());
						log.info("Login into ftp site "
								+ conf.getFTPProtocol().toString()
										.toLowerCase() + "://"
								+ conf.getFTPHostname() + "...OK");
						String errorMsg = getFileFromFTP(client);
						
						//send email error
						if (errorMsg != null && conf.isMailEnabled()) {
							EmailTask emailTask = new EmailTask(conf.getEmails(), 
									"Error when moving file from ftp to local", errorMsg);
							EmailTask.exec(emailTask);
						}
					} catch (Exception e) {
						// error will be handled by outer try-catch block
						throw e;
					} finally {
						try {
							log.info("Logout ftp site "
									+ conf.getFTPProtocol().toString()
											.toLowerCase() + "://"
									+ conf.getFTPHostname());
							client.logout();
							log.info("Logout ftp site "
									+ conf.getFTPProtocol().toString()
											.toLowerCase() + "://"
									+ conf.getFTPHostname() + "...OK");
						} catch (Exception e) {
							log.warn("Logout ftp site "
									+ conf.getFTPProtocol().toString()
											.toLowerCase() + "://"
									+ conf.getFTPHostname() + "...OK");
						}
					}
				}
				
				//start processing all files
				log.info("");log.info("");
				log.info("------------Processing files in " + conf.getMonitoredPath().getAbsolutePath() + "------------");
				log.info("");
				File[] monitoredFiles = conf.getMonitoredPath().listFiles(filter);
				ExecutorService execSvc = Executors.newFixedThreadPool(10);
				for (File file : monitoredFiles) {
					ProcessFileTask task = new ProcessFileTask(conf, file);
					execSvc.execute(task);
				}
				execSvc.shutdown();
				
				//wait until all processFileTasks finish
				while (!execSvc.awaitTermination(30, TimeUnit.SECONDS));
				log.info(monitoredFiles.length + " files are processed.");
				//shut down email executor pool
				EmailTask.shutdown();
			}
		} catch (Exception e) {
			log.error("Error: " + e.getMessage(), e);
			System.exit(-1);
		}

	}

	/**
	 * 
	 * @param client: ftp client
	 * @return null or error message if any
	 * @throws SocketException
	 * @throws IOException
	 * @throws FTPConnectionException
	 * @throws FTPClientNotLoginException 
	 */
	static private String getFileFromFTP(FTPSiteClient client)
			throws SocketException, IOException,
			FTPClientNotLoginException {
    		final String folderTemp;
    		if (!conf.getFTPFolderPath().endsWith("/"))
    			folderTemp = conf.getFTPFolderPath() + "/";
    		else
    			folderTemp = conf.getFTPFolderPath();
    		
    		final String folder;
    		if (!folderTemp.startsWith("/"))
    			folder = "/" + folderTemp;
    		else
    			folder = folderTemp;
    		
    		client.setWorkingDirectory(folder);
    		
    		String errorMsg = ""; 
    		{
    			log.info("Change current working ftp folder to " + folder + "...OK");
    			List<FileItem> files = client.listFiles();
    			if (files == null || files.size() == 0)
    				log.warn("No file found in " + folder + ".");
    			else {
    				log.info("Found " + files.size() + " files in " + folder + ".");
    				log.info("Preparing moving files on ftp site to " + conf.getMonitoredPath().getAbsolutePath());
    				int downloadedFileCount = 0;
    				for (FileItem ftpFile : files) {
    					if (ftpFile == null || ftpFile.isDirectory()) continue;
    					log.info("Processing file " + ftpFile.getFilename() + "...");
    					
    					//matching file name pattern
    					Matcher matcher = conf.getMonitoredFilePattern().matcher(ftpFile.getFilename());
    					if (!matcher.matches()) {
    						log.info("File " + ftpFile.getFilename() + " does not match file's pattern.");
    						continue;
    					}
    					
    					//download file from ftp server
    					boolean ok = false;
    					String file = folder + ftpFile.getFilename();
    					try {
    						OutputStream out = new FileOutputStream(
    								new File(conf.getMonitoredPath(), ftpFile.getFilename()));
						client.retrieveFile(file , out);
						ok = true;
    					} catch (IOException e) {
    						// doesn't matter
    					}
    					
    					//delete file on ftp server
					if (ok) {
						log.info("Copying file " + ftpFile.getFilename() + "...OK");
						downloadedFileCount++;
						ok = false;
						try {
							client.deleteFile(file);
							ok = true;
						} catch (Exception e) {
							//doesn't matter
						}
						if (ok)
							log.info("Deleting file " + ftpFile.getFilename() + "...OK");
						else {
							String err = "Deleting file " + ftpFile.getFilename() + "...FAILED";
							errorMsg += err + "\n";
							log.error(err);
						}
					} else {
						String err = "Copying " + ftpFile.getFilename() + "...FAILED";
						errorMsg += err + "\n";
						log.error(err);
					}
				}
    				log.info("Moved " + downloadedFileCount + "/" + files.size()
    						+ " files from ftp://" + conf.getFTPHostname() + folder
    						+ " to " + conf.getMonitoredPath().getAbsolutePath() + ".");
    			}
    		}
    		
        return errorMsg.length() == 0 ? null : errorMsg;
	}
}

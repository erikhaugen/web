package com.livescribe.importsecretkeytool.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.activity.InvalidActivityException;

import org.apache.log4j.Logger;

public class Configuration {
	public enum FTPProtocol {FTP, SFTP, FTPS};
	private static Logger log = Logger.getLogger(Configuration.class);
	private static Configuration configuration;
	Properties properties;
	private String configFilename = "";
	private Pattern monitoredFilePattern;

	static {
		// Load the JDBC driver for Frontbase
		String fb_DriverName = "com.frontbase.jdbc.FBJDriver";
		try {
			Class.forName(fb_DriverName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("cannot load class " + fb_DriverName);
		}

		// Load the JDBC driver for Frontbase
		String mysql_DriverName = "com.mysql.jdbc.Driver";
		try {
			Class.forName(mysql_DriverName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("cannot load class " + mysql_DriverName);
		}
	}
	
	static Configuration loadConfiguration(String filename) throws FileNotFoundException, IOException {
		configuration = new Configuration(filename);
		configuration.configFilename = filename;
		return configuration;
	}
	
	static Configuration loadConfiguration(File file) throws FileNotFoundException, IOException {
		configuration = new Configuration(file);
		configuration.configFilename = file.getName();
		return configuration;
	}
	
	static public Configuration getConfiguration() {
		if (configuration == null)
			throw new IllegalStateException("Configuration is not initiated");
		return configuration;
	}

	private Configuration() {
		properties = new Properties();
	}

	private Configuration(String fileName) throws FileNotFoundException,
			IOException {
		this(new File(fileName));
	}

	private Configuration(File confFile) throws FileNotFoundException,
			IOException {
		this();
		properties.load(new FileInputStream(confFile));
	}

	public String getConfigName() {
		return configFilename;
	}
	
	public String getProperty(String prop) {
		return properties.getProperty(prop);
	}
	
	public String getProperty(ProgramParameter prop) {
		return properties.getProperty(prop.getName());
	}
	
	public String getPropertyWithDefault(ProgramParameter prop) {
		return properties.getProperty(prop.getName(), prop.getDefaultValue());
	}

	// =====================DATABASE CONFIGURATION=======================//

	public String getFrontbaseDBURL() {
		return getProperty(ProgramParameter.FB_DATABASE_URL.getName());
	}

	public String getFrontbaseDBUser() {
		return getProperty(ProgramParameter.FB_DATABASE_USER.getName());
	}

	public String getFrontbaseDBPassword() {
		return getProperty(ProgramParameter.FB_DATABASE_PASSWORD.getName());
	}

	public Connection getFrontbaseConnection()
			throws ClassNotFoundException, SQLException {
		// Create a connection to the Frontbase database
		String fb_url = this.getFrontbaseDBURL();
		String fb_username = this.getFrontbaseDBUser();
		String fb_password = this.getFrontbaseDBPassword();
		log.debug("Connecting to frontbase database server " + fb_url + "...");
		Connection fb_connection = DriverManager.getConnection(fb_url,
				fb_username, fb_password);
		if (fb_connection == null)
			throw new IllegalStateException(
					"Cannot connect to frontbase database " + fb_url);
		return fb_connection;
	}

	public String getMySQLDBURL() {
		return getProperty(ProgramParameter.MYSQL_DATABASE_URL.getName());
	}

	public String getMySQLDBUser() {
		return getProperty(ProgramParameter.MYSQL_DATABASE_USER.getName());
	}

	public String getMySQLDBPassword() {
		return getProperty(ProgramParameter.MYSQL_DATABASE_PASSWORD.getName());
	}

	public Connection getMySQLConnection() throws ClassNotFoundException,
			SQLException {
		// Create a connection to the Frontbase database
		String mysql_url = this.getMySQLDBURL();
		String mysql_username = this.getMySQLDBUser();
		String mysql_password = this.getMySQLDBPassword();
		log.debug("Connecting to mysql database server " + mysql_url + "...");
		Connection mysql_connection = DriverManager.getConnection(mysql_url,
				mysql_username, mysql_password);
		if (mysql_connection == null)
			throw new IllegalStateException("Cannot connect to mysql database "
					+ mysql_url);
		return mysql_connection;
	}

	public File getMonitoredPath() {
		return new File(
				getProperty(ProgramParameter.MONITORED_PATH.getName()));
	}

	public File getErrorStorePath() {
		return new File(
				getProperty(ProgramParameter.ERROR_STORAGE_PATH.getName()));
	}

	public File getProcessedStorePath() {
		return new File(
				getProperty(ProgramParameter.PROCESSED_STORAGE_PATH.getName()));
	}

	public Pattern getMonitoredFilePattern() {
		if (this.monitoredFilePattern == null)
			this.monitoredFilePattern = Pattern.compile(
					getProperty(ProgramParameter.MONITORED_FILE_PATTERN
							.getName()), Pattern.CASE_INSENSITIVE);
		return this.monitoredFilePattern;
	}
	
	//=============MAIL CONFIGURATION=============//
	public Properties getMailProperties() {
		Properties result = new Properties();
		List<ProgramParameter> mailParameters = new ArrayList<ProgramParameter>();
		mailParameters.add(ProgramParameter.MAIL_SMTP_HOST);
		mailParameters.add(ProgramParameter.MAIL_SMTP_PORT);
		mailParameters.add(ProgramParameter.MAIL_SMTP_TLS);
		mailParameters.add(ProgramParameter.MAIL_SMTP_AUTH);
		
		for (ProgramParameter programParameter : mailParameters) {
			result.put(programParameter.getName(), this.getPropertyWithDefault(programParameter));
		}
		
		return result;
	}
	
	public boolean isCheckMailConfigurationOnStartUp() {
		return Boolean.parseBoolean(this.getPropertyWithDefault(ProgramParameter.MAIL_CHECK_ON_STARTUP));
	}
	
	public boolean isSMTPAuth() {
		return Boolean.parseBoolean(this.getPropertyWithDefault(ProgramParameter.MAIL_SMTP_AUTH));
	}
	
	public String getSMTPUser() {
		return this.getProperty(ProgramParameter.MAIL_SMTP_USER);
	}
	
	public String getSMTPPassword() {
		return this.getProperty(ProgramParameter.MAIL_SMTP_PASSWORD);
	}

	public List<String> getEmails() {
		List<String> result = new ArrayList<String>();
		String str = this.getProperty(ProgramParameter.MAIL_RECEIVERS);
		String[] receivers = str.split(",");
		for (String string : receivers) {
			result.add(string.trim());
		}
		return result;
	}
	
	public boolean isMailEnabled() {
		return Boolean.parseBoolean(this.getPropertyWithDefault(ProgramParameter.MAIL_ENABLED));
	}
	
	public String getMailSender() {
		return this.getPropertyWithDefault(ProgramParameter.MAIL_SENDER);
	}
	
	
	//=============FTP CONFIGURATION=============//
	public String getFTPHostname() {
		return this.getProperty(ProgramParameter.FTP_HOSTNAME);
	}

	public int getFTPPort() {
		return Integer.parseInt(this.getPropertyWithDefault(ProgramParameter.FTP_PORT));
	}
	
	public String getFTPFolderPath() {
		return this.getProperty(ProgramParameter.FTP_FOLDER_PATH);
	}
	
	public String getFTPUsername() {
		return this.getProperty(ProgramParameter.FTP_USER);
	}
	
	public String getFTPPassword() {
		return this.getProperty(ProgramParameter.FTP_PASSWORD);
	}
	
	public FTPProtocol getFTPProtocol() {
		String protocolStr = this.getProperty(ProgramParameter.FTP_PROTOCOL);
		return FTPProtocol.valueOf(protocolStr.toUpperCase());
	}
	
	public boolean isFTPEnabled() {
		return Boolean.parseBoolean(this.getPropertyWithDefault(ProgramParameter.FTP_ENABLED));
	}
}

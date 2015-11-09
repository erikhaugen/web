package com.livescribe.importsecretkeytool.cli;

public enum ProgramParameter {
	MONITORED_PATH("monitoredPath", "", "path that is monitored by the tool"),
	MONITORED_FILE_PATTERN("monitoredFilePattern", "", "file name in monitored path that is monitored by the tool"),
	ERROR_STORAGE_PATH("errorStoragePath", "", "folder that stored file that has problem when being processed"),
	PROCESSED_STORAGE_PATH("processedStoragePath", "", "folder that stored file that was processed successful"),
	
	FB_DATABASE_URL("fbDBUrl", "db1-test.pensoft.local", "database connection url"),
	FB_DATABASE_USER("fbDBUser", "mfgsecretkeyuser", "database username"),
	FB_DATABASE_PASSWORD("fbDBPassword", "lk3pg2KdPjmd", "user's password"),
	
	MYSQL_DATABASE_URL("mysqlDBUrl", "db1-test.pensoft.local", "database connection url"),
	MYSQL_DATABASE_USER("mysqlDBUser", "mfgsecretkeyuser", "database username"),
	MYSQL_DATABASE_PASSWORD("mysqlDBPassword", "lk3pg2KdPjmd", "user's password"),
	
	MAIL_ENABLED("mail.enabled", "true", "enable/disable sending email feature"),
	MAIL_RECEIVERS("mail.receivers", "", "list of user receive email if error happens"),
	MAIL_SENDER("mail.sender", "importsecretkeytool@livescribe.com", "email address in FROM attribute"),
	MAIL_CHECK_ON_STARTUP("mail.smtp.checkOnStartup", "true", "check smtp configuration on startup"),
	MAIL_SMTP_HOST("mail.smtp.host", "localhost", "hostname/ip, port, tls of smtp server"),
	MAIL_SMTP_PORT("mail.smtp.port", "25", "smtp server's port"),
	MAIL_SMTP_TLS("mail.smtp.starttls.enable", "false", "smtp connect with tls"),
	MAIL_SMTP_AUTH("mail.smtp.auth", "false", "smtp server needs authentication?"),
	MAIL_SMTP_USER("mail.smtp.auth.user", "", "username to authenticate with smtp server"),
	MAIL_SMTP_PASSWORD("mail.smtp.auth.password", "", "password of smtp server's user"),
	
	FTP_ENABLED("ftp.enabled", "true", "Turn on to move files from FTP site to local for processing"),
	FTP_PROTOCOL("ftp.protocol", "sftp", "select protocol: ftp,sftp or ftps"),
	FTP_HOSTNAME("ftp.hostname", "", "FTP server's hostname"),
	FTP_PORT("ftp.port", "21", "FTP server's port"),
	FTP_FOLDER_PATH("ftp.folderPath", "", "folder path on ftp site"),
	FTP_USER("ftp.auth.user", "", "authorized username"),
	FTP_PASSWORD("ftp.auth.password", "", "authorized username's password");
	
	private String defaultValue;
	private String paramName;
	private String description;

	private ProgramParameter(String paramName, String defaultValue, String description) {
		this.defaultValue = defaultValue;
		this.paramName = paramName;
		this.description = description;
	}
	
	public String getName(){
		return paramName;
	}
	
	public String getDefaultValue(){
		return defaultValue;
	}
	
	public String getDescription(){
		return description;
	}
	
	static public String getDefaultValueOf(String parameter) {
		String result = null;
		for(ProgramParameter param : ProgramParameter.values()) {
			if (param.getName().equals(parameter))
				return param.getDefaultValue();
		}
		return result;
	}
}

package com.livescribe.importsecretkeytool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.livescribe.importsecretkeytool.cli.Configuration;
import com.livescribe.importsecretkeytool.exception.FailToParseLineException;
import com.livescribe.importsecretkeytool.exception.ProcessingException;

public class ProcessFileTask implements Runnable {
	private static Logger log = Logger.getLogger(ProcessFileTask.class);
	private Configuration conf;
	private File file;

	public ProcessFileTask(Configuration conf, File file)
			throws FileNotFoundException {
		if (file == null || !file.exists() || file.isDirectory())
			throw new IllegalArgumentException("File " + file.getAbsolutePath()
					+ " doesn't exist or is not a file.");

		this.setConf(conf);
		this.setFile(file);
	}

	protected String doit() throws ProcessingException {
		String error = null;
		// get connections
		Connection mysql_connection = null;
		Connection fb_connection = null;
		try {
			// get connections
			mysql_connection = conf.getMySQLConnection();
			fb_connection = conf.getFrontbaseConnection();
			mysql_connection.setAutoCommit(false);
			fb_connection.setAutoCommit(false);
			
			FileReader fr = new FileReader(getFile());
			BufferedReader br = new BufferedReader(fr);
			String line = null;

			while ((line = br.readLine()) != null) {
				SecretKeyItem item = null;
				try {
					line = line.trim();
					if (line.length() == 0)
						continue;
					item = SecretKeyItem.parse(line);
				} catch (FailToParseLineException e) {
					String err = line + ",ERROR: " + e.getMessage();
					log.error("Fail to parse line: " + err);
					error = (error == null) ? err : error + "\n" + err;
					continue;
				}
				String err = importSecretKeyItem(item, mysql_connection, fb_connection);
				if (err != null) {
					log.error("Error on line: " + line + "; ERROR is: " + err);
					err = line + ",ERROR: " + err;
					error = (error == null) ? err : error + "\n" + err;
				} else {
					log.info("Successfully import secretkey of penid=" + item.getPenHexId());
				}
			}
			
			br.close();
			log.info("Complete processing file " + getFile().getName());
		} catch (Exception e) {
			throw new ProcessingException(e.getMessage(), e);
		} finally {
			try {
				if (mysql_connection != null && !mysql_connection.isClosed())
					mysql_connection.close();
			} catch (SQLException e) {
				log.info("fail to close mysql connection");
				log.error("fail to close mysql connection", e);
			}
			try {
				if (fb_connection != null && !fb_connection.isClosed())
					fb_connection.close();
			} catch (SQLException e) {
				log.error("fail to close frontbase connection");
				log.info("fail to close frontbase connection", e);
			}
		}
		
		return error;
	}

	protected String importSecretKeyItem(SecretKeyItem item, Connection mysql_connection, Connection fb_connection)
			{
		String error = "";
		int result = 0;
		PreparedStatement mysql_preparedStmt = null;
		boolean mysqlUpdated = false;
		try {
			mysql_preparedStmt = mysql_connection
					.prepareStatement("UPDATE pen SET key_transport=?, last_modified=?, last_modified_by=? " +
							"WHERE serialnumber_hex=?");
			mysql_preparedStmt.setString(1, item.getSecretKey());
			mysql_preparedStmt.setTimestamp(2, new Timestamp(new Date().getTime()));
			mysql_preparedStmt.setString(3, "ImportSecretKeyTool");
			mysql_preparedStmt.setString(4, item.getPenHexId());
			result = mysql_preparedStmt.executeUpdate();
			if (result == 0) {
				throw new SQLException("Looks like hexId=" + item.getPenHexId() + 
						" doesn't exist on mysql");
			}
			mysql_connection.commit();
			mysqlUpdated = true;
		} catch (SQLException e) {
			error += "Fail to update hexId=" + item.getPenHexId() + " on mysql and frontbase: " + e.getMessage() + ". ";
			try {
				mysql_connection.rollback();
			} catch (SQLException e1) {
				error += "Fail to rollback mysql after error of updating hexId=" + item.getPenHexId() + ". ";
			}
		}
		
		if (mysqlUpdated)
			try {
				PreparedStatement fb_preparedStmt = fb_connection
						.prepareStatement("UPDATE pen SET key_transport=? WHERE penID=?");
				fb_preparedStmt.setString(1, item.getSecretKey());
				fb_preparedStmt.setString(2, item.getPenHexId());
				result = fb_preparedStmt.executeUpdate();
				if (result == 0) {
					throw new SQLException("Looks like hexId=" + item.getPenHexId() + 
							" doesn't exist on frontbase");
				}
				fb_connection.commit();
			} catch (SQLException e) {
				error += "Fail to update hexId=" + item.getPenHexId() + " on frontbase: " + e.getMessage() + ". ";
				try {
					fb_connection.rollback();
				} catch (SQLException e2) {
					error += "Fail to rollback frontbase after error of updating hexId=" + item.getPenHexId() + ". ";
				}
				try {
					log.warn("Trying to revert update secret key of "
							+ item.getPenHexId() + " on mysql");
					mysql_preparedStmt = mysql_connection
							.prepareStatement("UPDATE pen SET key_transport=? WHERE serialnumber_hex=?");
					mysql_preparedStmt.setString(1, null);
					mysql_preparedStmt.setString(2, item.getPenHexId());
					result = mysql_preparedStmt.executeUpdate();
					if (result == 0) {
						throw new SQLException("Fail to remove hexId=" + item.getPenHexId() + 
								" on mysql");
					}
					mysql_connection.commit();
					error += "Note: secret key was rollbacked on mysql. ";
					log.warn("Trying to revert update secret key of "
							+ item.getPenHexId() + " on mysql...OK");
				} catch (SQLException e1) {
					log.warn("Trying to revert update secret key of "
							+ item.getPenHexId() + " on mysql...FAILED");
					error += "Note: secret key was likely updated on mysql only. ";
					
					try {
						mysql_connection.rollback();
					} catch (SQLException e2) {
						error += "Fail to rollback mysql after error of removing hexId=" + item.getPenHexId() + ". ";
					}
				}
			}
		
			
		return error.length() == 0 ? null : error;
	}

	public void run() {
		String threadName = Thread.currentThread().getName();
		Thread.currentThread().setName(threadName + "-" + getFile().getName());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_Z");
		String date = dateFormat.format(new Date());
		try {

			String error = doit();
			
			if (error != null) {
				throw new ProcessingException(error);
			}
		} catch (ProcessingException e) {
			log.error("failed to process file " + getFile().getName() + ": ", e);

			
			if (e.getCause() == null) {
				// save error item to somewhere for reprocessing
				File storeErrorFile = new File(getConf().getErrorStorePath(),
						getFile().getName() + "." + date + ".error");
				try {
					FileOutputStream fis = new FileOutputStream(storeErrorFile);
					PrintWriter pw = new PrintWriter(fis);
					pw.println(e.getMessage());
					pw.close();
				} catch (IOException e1) {
					log.error("failed to copy error processed file: "
							+ getFile().getName(), e1);
				}
			} else {
				//save the file to error place
				try {
					File storeErrorFile = new File(getConf()
							.getErrorStorePath(), getFile().getName() + "." + date);
					Utils.copyfile(getFile(), storeErrorFile);
					getFile().delete();
				} catch (IOException e1) {
					log.error("failed to copy error processed file: "
							+ getFile().getName(), e1);
				}
			}
			
			//send email
			if (conf.isMailEnabled()) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				PrintStream pw = new PrintStream(os);
				e.printStackTrace(pw);
				String stackTrace = os.toString();
				EmailTask emailTask = new EmailTask(conf.getEmails(), "Error", "Failed to process file " + getFile().getName()
						+ "\n\n====Error message====\n" + e.getMessage()
						+ "\n\n====Stack trace====\n" + stackTrace);
				EmailTask.exec(emailTask);
			}
		}
		
		//store file after processing
		try {
			File storeProcessedFile = new File(getConf()
					.getProcessedStorePath(), getFile().getName() + "." + date);
			Utils.copyfile(getFile(), storeProcessedFile);
			getFile().delete();
		} catch (IOException e1) {
			log.error("failed to copy processed file: "
					+ getFile().getName(), e1);
		}
	}

	public Configuration getConf() {
		return conf;
	}

	private void setConf(Configuration conf) {
		this.conf = conf;
	}

	public File getFile() {
		return file;
	}

	private void setFile(File file) {
		this.file = file;
	}
}

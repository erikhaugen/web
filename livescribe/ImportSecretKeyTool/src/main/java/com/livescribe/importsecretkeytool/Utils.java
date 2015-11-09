package com.livescribe.importsecretkeytool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

import javax.activity.InvalidActivityException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.livescribe.importsecretkeytool.cli.Configuration;
import com.livescribe.importsecretkeytool.exception.ProcessingException;

public class Utils {
	static private Logger log = Logger.getLogger(Utils.class);
	
	public static void copyfile(File srFile, File dtFile)
			throws IOException {
		InputStream in = new FileInputStream(srFile);

		File desDir = dtFile.getParentFile();
		if (!desDir.exists()){
			boolean ret = desDir.mkdirs();
			if (!ret)
				throw new IOException("Cannot create directory " + desDir.getName());
		}

		// For Overwrite the file.
		OutputStream out = new FileOutputStream(dtFile);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
		
	}

	public static boolean isSymlink(File file) throws IOException {
		if (file == null)
			throw new NullPointerException("File must not be null");
		File canon;
		if (file.getParent() == null) {
			canon = file;
		} else {
			File canonDir = file.getParentFile().getCanonicalFile();
			canon = new File(canonDir, file.getName());
		}
		return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
	}

	public static boolean sendEmail(String from, List<String> emails, String subject, String content) {
		final Configuration conf = Configuration.getConfiguration();

		// Get mail properties
		Properties properties = conf.getMailProperties();

		// Get the default Session object.
		Session session = null;
		if (conf.isSMTPAuth())
			session = Session.getInstance(properties,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(conf
									.getSMTPUser(), conf.getSMTPPassword());
						}
					});
		else
			Session.getDefaultInstance(properties);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
			message.setHeader("Content-Type", "text/plain; charset=\"UTF-8\"");

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			for(String to : emails)
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject(subject);

			// Now set the actual message
			message.setText(content);

			// Send message
			Transport.send(message);
			
			return true;
		} catch (Exception e) {
			log.error("Cannot send email", e);
		}
		return false;
	}

}
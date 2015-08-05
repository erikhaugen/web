/**
 * Created:  Jun 14, 2013 12:53:47 PM
 */
package com.livescribe.web.tools.webteamtool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

import com.beust.jcommander.JCommander;
import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.notestore.SyncState;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.TException;
import com.evernote.thrift.transport.TTransportException;
import com.livescribe.afp.AFPException;
import com.livescribe.afp.PenID;
import com.livescribe.framework.exception.AuthorizationNotFoundException;
import com.livescribe.framework.exception.MultipleRecordsFoundException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.web.tools.webteamtool.cli.CommandOptions;
import com.livescribe.web.tools.webteamtool.sax.Generator;
import com.livescribe.web.tools.webteamtool.service.EvernoteService;
import com.livescribe.web.tools.webteamtool.service.UserService;
import com.livescribe.web.tools.webteamtool.util.EncryptionUtils;
import com.livescribe.web.tools.webteamtool.util.Utils;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class Main {

	private static final ApplicationContext ac = new ClassPathXmlApplicationContext("application-context.xml");
	private static Logger logger = Logger.getLogger(Main.class);
	
	/**
	 * <p></p>
	 * 
	 */
	public Main() {
		
	}

	/**
	 * <p></p>
	 * 
	 * @param args
	 * @throws AFPException 
	 * @throws EDAMNotFoundException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws AFPException, EDAMNotFoundException, InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException, SAXException, IOException {
		
		DOMConfigurator.configureAndWatch("log4j.xml");
		
		CommandOptions opts = new CommandOptions();
		new JCommander(opts, args);
		
		//	Display ID --> Serial Number
		if (opts.getDisplayId() != null) {
			Long sn = convertDisplayId(opts.getDisplayId());
			System.out.println(sn);
		}
		
		//	Serial Number --> Display ID
		else if (opts.getSerialNumber() != null) {
			String displayId = convertSerialNumber(opts.getSerialNumber());
			System.out.println(displayId);
		}
		
		//	Validate OAuth access token.
		else if (opts.getAccessToken() != null) {
			boolean valid = validateToken(opts.getAccessToken());
			if (valid) {
				System.out.println("The given OAuth access token is valid.");
			}
			else {
				System.out.println("The given OAuth access token is NOT valid.");
			}
		}
		
		//	Validate OAuth access token for given email address.
		else if (opts.getEmailAddress() != null) {
			System.out.println("Not implemented.");
			//	TODO:  Lookup primary authorization and validate that access token.
		}
		
		//	Find Evernote upload quota for given email address.
		else if (opts.getQuotaEmailAddress() != null) {
			try {
				long quota = getUserQuota(opts.getQuotaEmailAddress());
				System.out.println("\nUser's remaining quota:  " + quota + " bytes.\n");
			} catch (MultipleRecordsFoundException mrfe) {
				String msg = "Multiple records found found for User with email address '" + opts.getQuotaEmailAddress() + "'.";
				System.out.println(msg);
				logger.error(msg, mrfe);
			} catch (EDAMUserException eue) {
				String msg = "EDAMUserException thrown";
				EDAMErrorCode code = eue.getErrorCode();
				if (EDAMErrorCode.AUTH_EXPIRED.equals(code)) {
					msg = "\nEvernote Authorization expired for Livescribe user with email address: '" + opts.getQuotaEmailAddress() + "'.\n";
				}
				else {
					msg = code.toString();
				}
				System.out.println(msg);
				logger.error(msg, eue);
			} catch (EDAMSystemException ese) {
				String msg = "EDAMSystemException thrown";
				System.out.println(msg);
				logger.error(msg, ese);
			} catch (TException te) {
				String msg = "TException thrown";
				System.out.println(msg);
				logger.error(msg, te);
			} catch (UserNotFoundException unfe) {
				String msg = "UserNotFoundException thrown";
				System.out.println(msg);
				logger.error(msg, unfe);
			} catch (AuthorizationNotFoundException anfe) {
				String msg = "AuthorizationNotFoundException thrown.  " + anfe.getMessage();
				System.out.println(msg);
				logger.error(msg, anfe);
			}
		}
		
		//	Find OAuth access token for given email address.
		else if (opts.getAccessTokenEmailAddress() != null) {
			try {
				String token = findOAuthAccessTokenByEmail(opts.getAccessTokenEmailAddress());
				System.out.println("\nUser's OAuth access token:  " + token + "\n");
			} catch (MultipleRecordsFoundException e) {
				String msg = "MultipleRecordsFoundException thrown";
			}
		}
		
		//	List system properties.
		else if (opts.getSystemProperties() != null) {
			Properties props = System.getProperties();
			props.list(System.out);
		}
		
		//	Hash a password (MySQL).
		else if (opts.getPasswordString() != null) {
			String original = opts.getPasswordString();
			EncryptionUtils encUtil = new EncryptionUtils();
			String hashed = encUtil.generateBase64EncodedSHAHash(original);
			System.out.println("\nThe string '" + original + "' hashes to '" + hashed + "' using Sky/Vector algorithm.\n");
		}
		else if (opts.getFrontBasePasswordString() != null) {
			String original = opts.getFrontBasePasswordString();
			EncryptionUtils encUtil = new EncryptionUtils();
			String hashed = encUtil.generateFrontBaseHash(original);
			System.out.println("\nThe string '" + original + "' hashes to '" + hashed + "' using WebObjects/FrontBase algorithm.\n");
		} else if (opts.getFrontBasePrimaryKey() != null) {
			String original = opts.getFrontBasePrimaryKey();
			EncryptionUtils encUtil = new EncryptionUtils();
			StringBuilder hashed = encUtil.hashValueFor(original);
			System.out.println("\nHash of primary key " + original + " is '" + hashed.toString() + "'.");
		}
		
//		else if (opts.getXmlSchema() != null) {
//			String schema = opts.getXmlSchema();
//			String pkg = opts.getTargetPackage();
//			Generator generator = new Generator();
//			generator.generate(schema, pkg);
//		}
	}

	/**
	 * <p></p>
	 * 
	 * @param quotaEmailAddress
	 * @return
	 * @throws MultipleRecordsFoundException 
	 * @throws TException 
	 * @throws EDAMSystemException 
	 * @throws EDAMUserException 
	 * @throws AuthorizationNotFoundException 
	 * @throws UserNotFoundException 
	 */
	private static long getUserQuota(String quotaEmailAddress) throws MultipleRecordsFoundException, EDAMUserException, EDAMSystemException, TException, UserNotFoundException, AuthorizationNotFoundException {
		
		String method = "getUserQuota()";
		System.out.println("BEGIN - " + method);
		long start = System.currentTimeMillis();
		
		EvernoteService evernoteService = (EvernoteService)ac.getBean("evernoteService");
		long remainingQuota = evernoteService.findRemainingQuota(quotaEmailAddress);
		
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("AFTER - " + method + " - " + duration + " milliseconds.");

		return remainingQuota;
	}

	private static Long convertDisplayId(String displayId) throws AFPException {
		
		PenID id = new PenID(displayId);
		Long sn = id.getId();
		return sn;
	}
	
	private static String convertSerialNumber(Long serialNumber) {
		
		PenID id = new PenID(serialNumber);
		String displayId = id.getSerial();
		return displayId;
	}
	
	public static String findOAuthAccessTokenByEmail(String email) throws MultipleRecordsFoundException {
		
		UserService userService = (UserService)ac.getBean("userService", UserService.class);
		Authorization auth = userService.findPrimaryAuthorization(email);
		String oauthAccessToken = auth.getOauthAccessToken();
		
		return oauthAccessToken;
	}
	
	/**
	 * <p>Returns the number of bytes the user can upload to their Evernote 
	 * account.</p>
	 * 
	 * @param oauthAccessToken The user&apos;s OAuth access token.
	 * 
	 * @return the number of remaining bytes left in the user&apos;s quota.
	 * 
	 * @throws EDAMUserException
	 * @throws EDAMSystemException
	 * @throws TException
	 */
	public static long findRemainingQuota(String oauthAccessToken) throws EDAMUserException, EDAMSystemException, TException {
		
		String method = "findRemainingQuota()";
		
		System.out.println("BEGIN - " + method);
		long start = System.currentTimeMillis();

		NoteStore.Client noteStore = Utils.getEvernoteNoteStoreClient(oauthAccessToken);
		
		System.out.println("    Got NoteStore ...");
		
		SyncState syncState = noteStore.getSyncState(oauthAccessToken);
		long bytesUploaded = syncState.getUploaded();
		
		UserStore.Client userStore = Utils.getEvernoteUserStoreClient();
		
		System.out.println("    Got UserStore ...");
		
		com.evernote.edam.type.User user = userStore.getUser(oauthAccessToken);
		long byteUploadLimit = user.getAccounting().getUploadLimit();
		
		long remainingQuota = byteUploadLimit - bytesUploaded;
		
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("AFTER - " + method + " - " + duration + " milliseconds.");

		return remainingQuota;
	}

	private static boolean validateToken(String token) throws EDAMNotFoundException {
		
		OAuthTokenValidator validator = (OAuthTokenValidator)ac.getBean("oauthValidator");
		boolean valid = false;
		try {
			valid = validator.validate(token);
		} catch (TTransportException tte) {
			String msg = "TTransportException thrown";
			System.out.println(msg + " - " + tte.getMessage());
		} catch (EDAMUserException eue) {
			String code = eue.getErrorCode().toString();
			String msg = "EDAMUserException thrown - " + code;
			System.out.println(msg + " - " + eue.getMessage());
		} catch (EDAMSystemException ese) {
			String msg = "EDAMSystemException thrown";
			System.out.println(msg + " - " + ese.getMessage());
		} catch (TException te) {
			String msg = "TException thrown";
			System.out.println(msg + " - " + te.getMessage());
		}
		return valid;
	}
}

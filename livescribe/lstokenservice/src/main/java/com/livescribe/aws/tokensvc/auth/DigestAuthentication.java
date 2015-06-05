/**
 * Created:  August 30, 2012
 *      By:  Gurmeet Kalra (gkalra@livescribe.com)
 */
package com.livescribe.aws.tokensvc.auth;

import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * DigestAuthentication class provide support for the support of Http Digest.
 * It provides the nonce to the clent apis that needs one and also
 * validates the authentication data that was send by the pen/device to
 * the server along with the user data, user analytics, user settings, etc.
 * 
 * @author Gurmeet Kalra
 */
public class DigestAuthentication {

	// class members
	public static final String HEADER_AUTHENTICATE = "WWW-Authenticate";
	public static final String HEADER_AUTHORIZATION = "Authorization";
	private static final String HEADER_AUTH_DIGEST = "Digest";

	private static final String DATA_REALM = "services.livescribe.com";
	private static final String DATA_QOP = "auth";

	private static final String HEADER_AUTH_REALM = "realm";
	private static final String HEADER_AUTH_QOP = "qop";
	private static final String HEADER_AUTH_NONCE = "nonce";
	private static final String HEADER_AUTH_OPAQUE = "opaque";
	
	//
	// Define some class static variables as we need to to maintain
	// nonce information across this object instance and the thread
	// that keeps changing the nonce every specific period of time
	//
	private static String currentNonce = null;
	private static String previousNonce = null;
	private static ScheduledExecutorService nonceRegenExecutorService = null;

	// instance members
	private static Logger logger = null;
	
	/**
	 * Constructor of the authenticate object which starts a thread on first usage of this object
	 * 
	 */
	public DigestAuthentication() {
		//String METHOD = getClass().getEnclosingMethod().getName(); DON'T USE, AS IT CAUSES An EXCEPTION

		if (null == logger)
			logger = Logger.getLogger(this.getClass().getName());

		start();
	}

	/**
	 * This public api will return the current nonce.
	 * 
	 * @return nounce in the string with a length of 32
	 */
	private String getNonce() {
		return currentNonce;
	}
	
	private boolean start()
	{
		final String METHOD = "start(): ";

		boolean initialized = false;
		
		if (null == currentNonce) {
			currentNonce = computeNonce();
		}

		// start a thread which will run every two minutes and generate a new nonce 
		if (null == nonceRegenExecutorService) {
			
			try {
				// create a thread
				nonceRegenExecutorService = Executors.newScheduledThreadPool(1);
				nonceRegenExecutorService.scheduleAtFixedRate(new Runnable() {
					
					@Override
					public void run() {
						
						// save the last one and update the current one
						previousNonce = currentNonce;
						currentNonce = computeNonce();
						
					}
				}, 2, 2, TimeUnit.MINUTES);
				initialized = true;
			}
			catch (Exception ex) {
				logger.fatal(METHOD + "Nonce thread creation ERROR:" + ex.getMessage());
				ex.getStackTrace();
			}
			
		}
		
		
		return initialized;
	}
	
	public static void stop()
	{
		final String METHOD = "stop(): ";
		
		// shutdown the separate thread/service that regenerates nonce
		try {
			if (null != nonceRegenExecutorService) {
				nonceRegenExecutorService.shutdown();
				nonceRegenExecutorService = null;
				logger.info(METHOD + "Nonce Generator Shutdown");
			}
		}
		catch (SecurityException ex) {
			logger.fatal(METHOD + "Nonce thread shutdown ERROR:" + ex.getMessage());
			ex.getStackTrace();
		}
	
		// we should clear the nonce for now as the last generated one is not needed
		// the sever may be getting started, etc...
		// This should be called only when the server/tokenservice is going down
		// and we NOT provide authentication support to the PEN devices any more
		currentNonce = null;
		
	}
	
	/**
	 *  This api computes the nonce that will be used the device to authenticate with LS servers
	 *  The computation is based on the current time written out in string format and combined
	 *  with a random number generated with a seed of a billion. 
	 */
	private String computeNonce()
	{
		final String METHOD = "computeNonce(): ";

		// get current date string
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
		String cdFormat = sdf.format(now);
		
		// get 32-bit random number string 
		Random rand = new Random(1000000000);
		Integer intRand = rand.nextInt();

		// combine and generate a hash in hex format of 32 chars
		String result = DigestUtils.md5Hex(cdFormat + intRand.toString());
		logger.info(METHOD + "Latest nonce=" + result + ", len=" + result.length() + ", prev=" + previousNonce);

		return result;
	}
	
	/**
	 *  This api computes the opaque that is also passed back in the response along with the nonce.
	 *  It is computed from the realm and nonce strings.
	 */
	private String getOpaque()
	{
		//final String METHOD = "getOpaque(): ";

		// combine and generate a hash in hex format of 32 chars
		String result = DigestUtils.md5Hex(DATA_REALM + getNonce());
		//logger.error(METHOD + "Opaque=" + result);

		return result;
	}
	
	/**
	 *  This api builds the header text string whihc contains all the essential data params for authentication
	 *  including the nonce needed by the client
	 * 
	 * @return Header in string format
	 */
	public String getAuthDataHeader()
	{
		final String METHOD = "getAuthDataHeader(): ";

		// combine and generate a hash in hex format of 32 chars
		String header = HEADER_AUTH_DIGEST + " "
						+ HEADER_AUTH_REALM + "=\"" + DATA_REALM + "\","
						+ HEADER_AUTH_QOP + "=" + DATA_QOP + ","
						+ HEADER_AUTH_NONCE + "=\"" + getNonce() + "\","
						+ HEADER_AUTH_OPAQUE + "=\"" + getOpaque() + "\"";
		logger.info(METHOD + "Auth header is - " + header);

		return header;
	}

	
	private static final String HEADER_AUTH_USERNAME = "username";
	private static final String HEADER_AUTH_URI = "uri";
	private static final String HEADER_AUTH_RESPONSE = "response";
	private static final String HEADER_AUTH_NC = "nc";
	private static final String HEADER_AUTH_CLIENTNONCE = "cnonce";

	/**
	 * This function basically authenticates the authorization data that was send by the client/pen.
	 * 
	 * @param httpRequest The original request object of the message that was sent
	 * @param httpResponse The response object to the message
	 * @param encryptedPassword The encrypted password that was obtained from the database
	 * @return When the authorization is successful it returns true else false
	 */
	public boolean authenticate(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String encryptedPassword)
	{
		final String METHOD_NAME = "authenticate(): ";

		boolean accept = false;
		
		if (null != httpRequest && null != httpResponse) {

			logger.debug(METHOD_NAME + "NOT IMPLEMENTED YET. Request input is - " + httpRequest.getHeader(DigestAuthentication.HEADER_AUTHORIZATION));
			try {

				String logText = "";
		        String clientResponse = "";
		        httpResponse.setContentType("text/html;charset=UTF-8");
		        PrintWriter out = null;

		        try {
		            String authHeader = httpRequest.getHeader(DigestAuthentication.HEADER_AUTHORIZATION);
		            if (StringUtils.isBlank(authHeader)) {
		            	logger.info(METHOD_NAME + "Authorization NOT in header - Sending Nonce... ");
		                httpResponse.addHeader(DigestAuthentication.HEADER_AUTHENTICATE, getAuthDataHeader());
		                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		            } else {
		                if (authHeader.startsWith(HEADER_AUTH_DIGEST)) {
		                	//logger.info(METHOD_NAME + "DIGEST Authorization PRESENT in header ");
		                    // parse the values of the Authentication header into a hashmap
		                    HashMap<String, String> authData = parseAuthHeader(authHeader);

		                    String method = httpRequest.getMethod();
		                    String username = authData.get(HEADER_AUTH_USERNAME);
		                    String password = getPassword(encryptedPassword);

		                    // here we will get the user name aka serial number and the secret code
		                    // from the database
		                    String ha1 = DigestUtils.md5Hex(username + ":" + authData.get(HEADER_AUTH_REALM) + ":" + password);

		                    String qop = authData.get(HEADER_AUTH_QOP);
		                    String nonce = authData.get(HEADER_AUTH_NONCE);

		                    String ha2;

		                    String reqURI = authData.get(HEADER_AUTH_URI);

		                    // will be always here for NOW
	                        ha2 = DigestUtils.md5Hex(method + ":" + reqURI);

		                    String serverResponse;

		                    if (StringUtils.isBlank(qop)) {
		                        serverResponse = DigestUtils.md5Hex(ha1 + ":" + nonce + ":" + ha2);

		                    } else {
		                    	// TODO Use the DOMAIN field too
		                        //String domain = headerValues.get("realm");

		                        String nonceCount = authData.get(HEADER_AUTH_NC);
		                        String clientNonce = authData.get(HEADER_AUTH_CLIENTNONCE);

		                        serverResponse = DigestUtils.md5Hex(ha1 + ":" + nonce + ":"
		                                + nonceCount + ":" + clientNonce + ":" + qop + ":" + ha2);

		                        logText = "nc=" + nonceCount + ", nounce=" + nonce + ", qop=" + qop;
		                        //logger.info(METHOD_NAME + "Creating SERVER Response, " + logText);
		                    }
		                    
		                    //TODO Make it local
	                        logger.info(METHOD_NAME + "Created SERVER Response, " + serverResponse);
		                    clientResponse = authData.get("response");
	                        //logger.info(METHOD_NAME + "Got CLIENT Response, " + clientResponse);
		                    //TODO - GSK TEMPORARY AS ALL PENS DON"T HAVE SECRET KEY IN DB
		                    serverResponse = clientResponse; // TO LET EVERYTHING PASS

		                    if (!serverResponse.equals(clientResponse)) {
		                    	logger.info(METHOD_NAME + "Digest Failed - Sending Nonce.... AGAIN");
				                httpResponse.addHeader(DigestAuthentication.HEADER_AUTHENTICATE, getAuthDataHeader());
				                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		                    } else {
		                    	// validated successfully
								accept = true;
		                    	//logger.info(METHOD_NAME + "SUCCESS with Digest - Livescribe supports Digest Authorization");
		                    }
		                } else {
		                	logger.info(METHOD_NAME + "Response had NO Digest at all - Livescribe only supports Digest Authorization");
			                httpResponse.addHeader(DigestAuthentication.HEADER_AUTHENTICATE, getAuthDataHeader());
		                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Livescribe only supports Digest Authorization");
		                }
					}

//			        out = httpResponse.getWriter();
					//out.println("<html>");
//					out.println("<head>");
//					out.println("<title>Servlet HttpDigest</title>");
//					out.println("</head>");
//					out.println("<body>");
//					out.println("<h1>Servlet HttpDigest at " + httpRequest.getContextPath () + "</h1>");
//					if (accept)
//						out.println("<h2>Authentication SUCCEEDED" + "</h2>");
//					else
//						out.println("<h2>Authentication FAILED" + "</h2>");
//					out.println("<h3>" + logText + "</h3>");
//					out.println("<h3>" + clientResponse + "</h3>");
//					out.println("</body>");
//					out.println("</html>");

		        } catch (Exception ex) {
	            	logger.error(METHOD_NAME + "Exception thrown ex=" + ex.getMessage());
	            } finally {
	        		if (null != out) out.close(); // cleanup
				}
				
			} catch (Exception ex) {
            	logger.error(METHOD_NAME + "Exception thrown ex=" + ex.getMessage());
			}
		}
		
		logger.debug(METHOD_NAME + "Success=" + accept);
		return accept;
	}
    
    // The aim of this function is extract the various pieces of name value pairs
    // send by the client for authorization of the pen messages
	private HashMap<String, String> parseAuthHeader(String authHeader)
	{
        final String METHOD_NAME = "parseAuthHeader";
        
        HashMap<String, String> authData = new HashMap<String, String>();

        String headerStringWithoutScheme = authHeader.substring(authHeader.indexOf(" ") + 1).trim();
        String keyValueArray[] = headerStringWithoutScheme.split(",");
        for (String keyval : keyValueArray) {
            if (keyval.contains("=")) {
                String key = keyval.substring(0, keyval.indexOf("="));
                String value = keyval.substring(keyval.indexOf("=") + 1);
                authData.put(key.trim(), value.replaceAll("\"", "").trim());
            }
        }

		//logger.info(METHOD_NAME + ": AUTH data received raw=" + authHeader);
		//logger.info(METHOD_NAME + ": AUTH " + HEADER_AUTH_USERNAME + "=" + authData.get(HEADER_AUTH_USERNAME) + ","
		//			+ HEADER_AUTH_REALM + "=" + authData.get(HEADER_AUTH_REALM) + ","
		//			+ HEADER_AUTH_NONCE + "=" + authData.get(HEADER_AUTH_NONCE) + ",");
		//logger.info(METHOD_NAME + ": AUTH " + HEADER_AUTH_URI + "=" + authData.get(HEADER_AUTH_URI) + ","
		//			+ HEADER_AUTH_RESPONSE + "=" + authData.get(HEADER_AUTH_RESPONSE) + "\","
		//			+ HEADER_AUTH_OPAQUE + "=" + authData.get(HEADER_AUTH_OPAQUE) + ",");
		//logger.info(METHOD_NAME + ": AUTH " + HEADER_AUTH_QOP + "=" + authData.get(HEADER_AUTH_QOP) + ","
		//			+ HEADER_AUTH_NC + "=" + authData.get(HEADER_AUTH_NC) + ","
		//			+ HEADER_AUTH_CLIENTNONCE + "=" + authData.get(HEADER_AUTH_CLIENTNONCE));

		return authData;
	}
	
	private String getPassword(String encrytedPassword)
	{
        final String METHOD_NAME = "getPassword";
		//TODO FOr now it returns the original password string passed in
		
		String password = encrytedPassword;

		// verify input
		if (null != encrytedPassword && encrytedPassword.length() == 256) {
			
			try{
				logger.error(METHOD_NAME + ": THIS IS JUST RETURNING THE ORIGINAL KEY ep=" + password); //TODO REMOVE THIS
				//Security.addProvider(new BouncyCastleProvider());

				//PEMReader reader= new PEMReader(new InputStreamReader(new FileInputStream("pen.key")));
				//PrivateKey pk = ((KeyPair)reader.readObject()).getPrivate();
				//System.out.println("PrivateKey\n" +pk);

				//byte[] decodedTransport = decodeTransport(encrytedPassword, pk);

				//System.out.println("\nPen encryption_key:");
				//System.out.println(toHexString(decodedTransport));
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			
		}
		return password;
	}
	
	public/*private*/ static byte[] decodeTransport(byte[] receivedTransport, PrivateKey pk)
	{
		//byte [] hash_init = {0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF, (byte) 0xFE, (byte) 0xDC, (byte) 0xBA, (byte) 0x98, 0x76, 0x54, 0x32, 0x10};
		byte [] result = new byte[16];
		
		try {
			byte[] decryptedTransport = decrypt(receivedTransport, pk);
			Cipher cipher  = Cipher.getInstance("AES/ECB/NoPadding", "BC");
	
			int index=0;
			while(index<receivedTransport.length) {
				SecretKeySpec skeySpec = new SecretKeySpec(decryptedTransport,index,16, "AES");
				cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
				byte [] enc = cipher.update(result);
	
				for(int i=0;i<16;i++) {
					result[i] ^= enc[i];
				}
				index += 16;
			}
		} catch (Exception e) {
			//logger.info(METHOD_NAME + ": AUTH data received raw=" + authHeader);
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * This api basically decrypts the encryption bytes based on the key.
	 * 
	 * @param encryptionBytes The bytes to be decrypted
	 * @param key
	 * @return The decrypted bytes.
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	private static byte[] decrypt(byte[] encryptionBytes, Key key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cipher  = Cipher.getInstance("RSA", "BC");
		cipher.init(Cipher.DECRYPT_MODE, key);     
		return cipher.doFinal(encryptionBytes);
	}

}
